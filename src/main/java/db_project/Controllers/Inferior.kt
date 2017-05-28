package db_project.Controllers

import db_project.DAO.JdbcDAO.JdbcForumDAO
import db_project.DAO.JdbcDAO.JdbcPostDAO
import db_project.DAO.JdbcDAO.JdbcThreadDAO
import db_project.DAO.JdbcDAO.JdbcUserDAO
import db_project.Views.StatusView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by pacman29 on 9.05.17.
 */
@RestController
@RequestMapping("api/service")
open class Inferior {
    @Autowired
    protected var jdbcUserDAO: JdbcUserDAO? = null
    @Autowired
    protected var jdbcForumDAO: JdbcForumDAO? = null
    @Autowired
    protected var jdbcThreadDAO: JdbcThreadDAO? = null
    @Autowired
    protected var jdbcPostDAO: JdbcPostDAO? = null

    @RequestMapping("/status")
    fun serverStatus(): ResponseEntity<Any> {
        val forumsCount = jdbcForumDAO!!.count()
        val postsCount = jdbcPostDAO!!.count()
        val threadsCount = jdbcThreadDAO!!.count()
        val usersCount = jdbcUserDAO!!.count()
        return ResponseEntity.status(HttpStatus.OK).body<Any>(StatusView(forumsCount, postsCount, threadsCount, usersCount))
    }

    @RequestMapping("/clear")
    fun clearService(): ResponseEntity<Any> {
        jdbcPostDAO!!.clear()
        jdbcThreadDAO!!.clear()
        jdbcForumDAO!!.clear()
        jdbcUserDAO!!.clear()
        return ResponseEntity.status(HttpStatus.OK).body<Any>(null)
    }
}
