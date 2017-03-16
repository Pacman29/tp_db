package db.Controllers;

import db.DatabaseServices.ForumsTableService;
import db.DatabaseServices.ForumsTableService.ForumModel;
import db.DatabaseServices.ThreadTableController.ThreadModel;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Created by pacman29 on 15.03.17.
 */
@RestController
@RequestMapping(value = "api/forum")
public final class ForumsController {
    public ForumsController(final ForumsTableService service) {
        this.service = service;
    }

    private final ForumsTableService service;

    @RequestMapping(value = "/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<Object> createForum(
            @RequestBody final ForumModel forum) {
        try {
            service.insert(forum);

        } catch (DuplicateKeyException ex) {
            return new ResponseEntity<>(service.get(forum).get(0),HttpStatus.CONFLICT);

        } catch (DataAccessException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(service.get(forum).get(0), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{slug}/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<ThreadModel> createSlug(
            @RequestBody final ThreadModel thread,
            @PathVariable(value = "slug") final String slug) {

        if (thread.getSlug() == null) {
            thread.setSlug(slug);
        }

        if (thread.getForum() == null) {
            thread.setForum(slug);
        }

        final List<ThreadModel> threads;

        try {
            threads = service.insertThreadIntoDb(thread);

            if (threads.isEmpty()) {
                throw new EmptyResultDataAccessException(0);
            }

        } catch (DuplicateKeyException ex) {
            return new ResponseEntity<>(service.getThreadInfo(thread.getSlug()).get(0), HttpStatus.CONFLICT);

        } catch (DataAccessException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (Objects.equals(threads.get(0).getSlug(), threads.get(0).getForum())) {
            threads.get(0).setSlug(null);
        }

        return new ResponseEntity<>(threads.get(0), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{slug}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<Object> viewForum(
            @PathVariable("slug") final String slug) {

        final List<ForumModel> forums;
        final ForumModel forum = new ForumModel();
        forum.setSlug(slug);

        try {
            forums = service.get(forum);

            if (forums.isEmpty()) {
                throw new EmptyResultDataAccessException(0);
            }

        } catch (DataAccessException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(forums.get(0), HttpStatus.OK);
    }
}
