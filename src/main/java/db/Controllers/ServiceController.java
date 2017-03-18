package db.Controllers;

import db.DatabaseServices.ServiceTableService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by pacman29 on 18.03.17.
 */
@RestController
@RequestMapping("/api/service")
class ServiceController {
    public ServiceController(ServiceTableService service) {
        this.service = service;
    }

    /**
     * @brief Class used for communication with database.
     */

    private ServiceTableService service;

    @RequestMapping("/status")
    public final ResponseEntity<Object> serverStatus() {
        return new ResponseEntity<>(service.status(), HttpStatus.OK);
    }

    @RequestMapping("/clear")
    public final ResponseEntity<Object> clearService() {
        service.delete();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}