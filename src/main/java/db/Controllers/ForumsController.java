package db.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by pacman29 on 15.03.17.
 */
@RestController
@RequestMapping(value = "api/forums")
public final class ForumsController {
    private final JdbcTemplate jdbc;

    public ForumsController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @RequestMapping(value = "/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<String> CreateForum(@RequestBody final String msg){
        return new ResponseEntity<String>("", HttpStatus.CREATED);
    };
}
