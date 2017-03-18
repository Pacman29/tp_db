package db.Controllers;

import db.DatabaseServices.PostsTableService;
import db.DatabaseServices.PostsTableService.PostModel;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by pacman29 on 18.03.17.
 */

@RestController
@RequestMapping("/api/post/{id}")
public class PostController {
    private final PostsTableService postservice;

    public PostController(PostsTableService postservice) {
        this.postservice = postservice;
    }

    @RequestMapping(value = "/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<Object> viewForum(
            @RequestParam(value = "related", required = false) String[] related,
            @PathVariable("id") final Integer id
    ) {
        List<PostModel> posts;

        try {
            posts = postservice.get(id);

            if (posts.isEmpty()) {
                throw new EmptyResultDataAccessException(0);
            }

        } catch (DataAccessException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(postservice.getDetailedPost(posts.get(0), related), HttpStatus.OK);
    }

    @RequestMapping(value = "/details",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<Object> viewForum(
            @RequestBody final PostModel post,
            @PathVariable("id") final Integer id
    ) {
        List<PostModel> posts;

        try {
            if (post.getMessage() != null && !post.getMessage().isEmpty()) {
                posts = postservice.update(post, id);

            } else {
                posts = postservice.get(id);
            }

            if (posts.isEmpty()) {
                throw new EmptyResultDataAccessException(0);
            }

        } catch (DataAccessException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(posts.get(0), HttpStatus.OK);
    }
}
