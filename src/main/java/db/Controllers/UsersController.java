package db.Controllers;

import db.DatabaseServices.UsersTableService;
import db.DatabaseServices.UsersTableService.UserModel;
import com.alibaba.fastjson.JSON;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by pacman29 on 16.03.17.
 */
@RestController
@RequestMapping(value = "/api/user/{nickname}")
public final class UsersController {
    public UsersController(final UsersTableService service) {
        this.service = service;
    }

    private final UsersTableService service;

    @RequestMapping(value = "/create",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<Object> CreateUser(@RequestBody UserModel user,
                                                   @PathVariable(value = "nickname") String nickname){

        user.setNickname(nickname);

        try{
            service.insert(user);
        } catch (DuplicateKeyException ex) {
            return new ResponseEntity<>(service.get(user),HttpStatus.CONFLICT);

        } catch (DataAccessException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/profile",
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<Object> UserProfile(@PathVariable(value = "nickname") String nickname){
        final List<UserModel> users;

        try {
            users = service.get(new UserModel(null, null, null, nickname));

            if (users.isEmpty()) {
                throw new EmptyResultDataAccessException(0);
            }

        } catch (DataAccessException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(users.get(0), HttpStatus.OK);
    }

    @RequestMapping(value = "/profile",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<Object> ChangeUserProfile(
            @RequestBody UserModel user,
            @PathVariable(value = "nickname") String nickname
    ) {
        user.setNickname(nickname);

        try {
            service.update(user);
            final List<UserModel> users = service.get(user);

            if (users.isEmpty()) {
                throw new EmptyResultDataAccessException(0);
            }

            user = users.get(0);

        } catch (DuplicateKeyException ex) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        } catch (DataAccessException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
