package db.Controllers;

import db.DatabaseServices.ForumsTableService;
import db.DatabaseServices.ForumsTableService.ForumModel;
import db.DatabaseServices.ThreadsTableService;
import db.DatabaseServices.ThreadsTableService.ThreadModel;
import db.DatabaseServices.UsersTableService;
import db.DatabaseServices.UsersTableService.UserModel;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Created by pacman29 on 15.03.17.
 */
@RestController
@RequestMapping(value = "api/forum")
public final class ForumsController {

    private final ForumsTableService forumService;
    private final ThreadsTableService threadService;
    private final UsersTableService userService;

    public ForumsController(ForumsTableService forumService, ThreadsTableService threadService, UsersTableService userService) {
        this.forumService = forumService;
        this.threadService = threadService;
        this.userService = userService;
    }



    @RequestMapping(value = "/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<Object> createForum(
            @RequestBody final ForumModel forum) {
        try {
            forumService.insert(forum);

        } catch (DuplicateKeyException ex) {
            return new ResponseEntity<>(forumService.get(forum.getSlug()).get(0),HttpStatus.CONFLICT);

        } catch (DataAccessException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(forumService.get(forum.getSlug()).get(0), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{slug}/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<Object> createSlug(
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
            threads = threadService.insert(thread);

            if (threads.isEmpty()) {
                throw new EmptyResultDataAccessException(0);
            }

        } catch (DuplicateKeyException ex) {
            return new ResponseEntity<>(threadService.get(thread.getSlug()).get(0), HttpStatus.CONFLICT);

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

        try {
            forums = forumService.get(slug);

            if (forums.isEmpty()) {
                throw new EmptyResultDataAccessException(0);
            }

        } catch (DataAccessException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(forums.get(0), HttpStatus.OK);
    }

    @RequestMapping(value = "/{slug}/threads", produces = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<Object> viewThreads(
            @RequestParam(value = "limit", required = false, defaultValue = "100") final Integer limit,
            @RequestParam(value = "since", required = false) final String since,
            @RequestParam(value = "desc", required = false) final Boolean desc,
            @PathVariable("slug") final String slug) {

        ThreadModel thread = new ThreadModel();
        thread.setSlug(slug);

        try {
            final List<ForumModel> forums = forumService.get(slug);

            if (forums.isEmpty()) {
                throw new EmptyResultDataAccessException(0);
            }

        } catch (DataAccessException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(threadService.getInfo(thread, limit, since, desc), HttpStatus.OK);
    }

    @RequestMapping(value = "/{slug}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<Object> viewUsers(
            @RequestParam(value = "limit", required = false, defaultValue = "100") final Integer limit,
            @RequestParam(value = "since", required = false) final String since,
            @RequestParam(value = "desc", required = false) final Boolean desc,
            @PathVariable("slug") final String slug
    ) {
        List<UserModel> users;

        try {
            users = userService.getInfo(slug, limit, since, desc);
            final List<ForumModel> forums = forumService.get(slug);

            if (forums.isEmpty()) {
                throw new EmptyResultDataAccessException(0);
            }

        } catch (DataAccessException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
