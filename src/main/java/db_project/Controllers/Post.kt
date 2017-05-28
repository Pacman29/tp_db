package db_project.Controllers

import db_project.Views.PostDetailedView
import db_project.Views.PostView
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Created by pacman29 on 4.03.17.
 */
@RestController
@RequestMapping("/api/post/{id}")
class Post : Inferior() {
    @RequestMapping(value = "/details", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun viewForum(
            @RequestParam(value = "related", required = false) related: String?, @PathVariable("id") id: Int?): ResponseEntity<PostDetailedView> {
        val post: PostDetailedView
        try {
            post = jdbcPostDAO!!.detailed(id, related?.split(",")?.toTypedArray())
        } catch (ex: DataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body<PostDetailedView>(null)
        }
        return ResponseEntity.status(HttpStatus.OK).body(post)
    }

    @RequestMapping(value = "/details", method = arrayOf(RequestMethod.POST), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun viewForum(@RequestBody body: PostView, @PathVariable("id") id: Int?): ResponseEntity<PostView> {
        var post = body
        try {
            post = if (post.message != null) jdbcPostDAO!!.update(post.message, id) else jdbcPostDAO!!.get_id(id)
        } catch (ex: DataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body<PostView>(null)
        }
        return ResponseEntity.status(HttpStatus.OK).body(post)
    }
}
