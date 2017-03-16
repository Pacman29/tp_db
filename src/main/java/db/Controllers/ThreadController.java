package db.Controllers;

import db.DatabaseServices.ThreadsTableService;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by pacman29 on 16.03.17.
 */
@RestController
@RequestMapping(value = "api/thread/{slug}")
public class ThreadController {
    private final ThreadsTableService service;

    public ThreadController(ThreadsTableService service,) {
        this.service = service;
    }

    @RequestMapping(value = "/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<Object> createPosts(
            @RequestBody final List<PostModel> posts,
            @PathVariable(value = "slug") final String slug
    ) {
        final List<PostModel> dbPosts;

        try {

            if (posts.isEmpty()) {
                throw new EmptyResultDataAccessException(0);
            }

            dbPosts = service.insertPostsIntoDb(posts, slug);

        } catch (DuplicateKeyException ex) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        } catch (DataAccessException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(dbPosts, HttpStatus.CREATED);
    }
}
