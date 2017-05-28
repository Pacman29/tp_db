package db_project.Controllers

import db_project.Views.UserView
import org.springframework.dao.DataAccessException
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Created by pacman29 on 23.02.17.
 */

@RestController
@RequestMapping(value = "/api/user/{nickname}")
class User : Inferior() {
    @RequestMapping(value = "/create", method = arrayOf(RequestMethod.POST), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun createUser(@RequestBody user: UserView,
                   @PathVariable(value = "nickname") nickname: String): ResponseEntity<Any> {
        try {
            jdbcUserDAO!!.create(user.about, user.email, user.fullname, nickname)
        } catch (ex: DuplicateKeyException) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body<Any>(jdbcUserDAO!!.get_users_nick_email(nickname, user.email))
        } catch (ex: DataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(null)
        }

        user.nickname = nickname
        return ResponseEntity.status(HttpStatus.CREATED).body<Any>(user)
    }

    @RequestMapping(value = "/profile", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun viewProfile(@PathVariable(value = "nickname") nickname: String): ResponseEntity<UserView> {
        val user: UserView
        try {
            user = jdbcUserDAO!!.get_one_email_nick(nickname, null)
        } catch (ex: DataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body<UserView>(null)
        }

        return ResponseEntity.status(HttpStatus.OK).body(user)
    }

    @RequestMapping(value = "/profile", method = arrayOf(RequestMethod.POST), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun modifyProfile(@RequestBody body: UserView,
                      @PathVariable(value = "nickname") nickname: String): ResponseEntity<UserView> {
        var user = body
        try {
            jdbcUserDAO!!.update(user.about, user.email, user.fullname, nickname)
            user = jdbcUserDAO!!.get_one_email_nick(nickname, user.email)
        } catch (ex: DuplicateKeyException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body<UserView>(null)
        } catch (ex: DataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body<UserView>(null)
        }

        return ResponseEntity.status(HttpStatus.OK).body(user)
    }
}
