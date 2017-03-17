package db.Controllers;

import db.DatabaseServices.PostsTableService;
import db.DatabaseServices.PostsTableService.PostModel;
import db.DatabaseServices.ThreadsTableService;
import db.DatabaseServices.ThreadsTableService.ThreadModel;
import db.DatabaseServices.UservotesTableController;
import db.DatabaseServices.UservotesTableController.VoteModel;
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
    private final UservotesTableController voteservices;

    public ThreadController(final ThreadsTableService threadservice,
                            final PostsTableService postservices,
                            final UservotesTableController voteservices) {
        this.threadservice = threadservice;
        this.postservices = postservices;
        this.voteservices = voteservices;
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

    @RequestMapping(value = "/vote",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<Object> voteForThread(
            @RequestBody final VoteModel vote,
            @PathVariable("slug") final String slug) {

        List<ThreadModel> threads = new ArrayList<>();
        try {
            Integer vote_change = (vote.getVoice() == -1) ? (-2) : (2);
            if(slug.matches("^-?\\d+$"))
            {
                Integer id = Integer.valueOf(slug);
                threads = threadservice.get(id);
                vote.setThread_slug(threads.get(0).getSlug());
                switch (voteservices.update(vote)){
                    case NOTHING: break;
                    case CHANGE:{
                        threads = threadservice.updateVotes(id,vote_change);
                        break;
                    }
                    case SETVOICE:{
                        threads = threadservice.updateVotes(id,vote.getVoice());
                    }
                }
            } else {
                vote.setThread_slug(slug);
                switch (voteservices.update(vote)){
                    case NOTHING: {
                        threads = threadservice.get(slug);
                        break;
                    }
                    case CHANGE: {
                        threads = threadservice.updateVotes(slug,vote_change);
                        break;
                    }
                    case SETVOICE:{
                        threads = threadservice.updateVotes(slug,vote.getVoice());
                    }
                }
            }

        } catch (DuplicateKeyException ex) {
            return new ResponseEntity<>(threadservice.get(slug).get(0), HttpStatus.CONFLICT);

        } catch (DataAccessException ex) {
            return new ResponseEntity<>(ex.toString(),HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(threads.get(0), HttpStatus.OK);
    }

}
