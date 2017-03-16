package db.Controllers;

import db.DatabaseServices.PostsTableService;
import db.DatabaseServices.PostsTableService.PostModel;
import db.DatabaseServices.ThreadsTableService;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pacman29 on 16.03.17.
 */
@RestController
@RequestMapping(value = "api/thread/{slug}")
public final class ThreadController {
    private final ThreadsTableService threadservice;
    private final PostsTableService postservices;

    public ThreadController(final ThreadsTableService threadservice,
                            final PostsTableService postservices) {
        this.threadservice = threadservice;
        this.postservices = postservices;
    }

    @RequestMapping(value = "/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<Object> createPosts(
            @RequestBody final List<PostModel> posts,
            @PathVariable(value = "slug") final String slug) {

        final List<PostModel> dbPosts;

        try {

            if (posts.isEmpty()) {
                throw new EmptyResultDataAccessException(0);
            }
            dbPosts = postservices.insertPosts(posts, slug);

        } catch (DuplicateKeyException ex) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        } catch (DataAccessException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(dbPosts, HttpStatus.CREATED);
    }

    private static Integer markerValue = 0;

    @RequestMapping(value = "/posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<PostsMarkerModel> viewThreads(
            @RequestParam(value = "limit", required = false, defaultValue = "100") final Integer limit,
            @RequestParam(value = "marker", required = false) final String marker,
            @RequestParam(value = "sort", required = false, defaultValue = "flat") final String sort,
            @RequestParam(value = "desc", required = false) final Boolean desc,
            @PathVariable("slug") final String slug
    ) {
        final List<PostModel> posts = service.getPostsSorted(sort, desc, slug);

        if (posts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        markerValue += marker != null && !Objects.equals(sort, "parent_tree") ? limit : 0;

        if (Objects.equals(sort, "parent_tree")) {

            if (markerValue >= posts.size() && marker != null) {
                markerValue = 0;

                return new ResponseEntity<>(new PostsMarkerModel(marker, new ArrayList<>()), HttpStatus.OK);

            } else if (markerValue == posts.size()) {
                markerValue = 0;
            }

            Integer zeroCount = 0, counter = 0;

            for (PostModel post : posts.subList(markerValue, posts.size())) {

                if (zeroCount.equals(limit) && desc == Boolean.TRUE) {
                    break;

                } else if (zeroCount.equals(limit + 1) && (desc == Boolean.FALSE || desc == null)) {
                    --counter;
                    break;
                }

                zeroCount += post.getParent().equals(0) ? 1 : 0;
                ++counter;
            }

            return new ResponseEntity<>(new PostsMarkerModel(marker,
                    posts.subList(markerValue, markerValue += counter)), HttpStatus.OK);
        }

        if (markerValue > posts.size()) {
            markerValue = 0;

            return new ResponseEntity<>(new PostsMarkerModel(marker, new ArrayList<>()), HttpStatus.OK);
        }

        return new ResponseEntity<>(new PostsMarkerModel(marker, posts.subList(markerValue,
                limit + markerValue > posts.size() ? posts.size() : limit + markerValue)), HttpStatus.OK);
    }

}
