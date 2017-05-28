package db_project.Controllers

import db_project.Views.PostView
import db_project.Views.PostsSortedView
import db_project.Views.ThreadView
import db_project.Views.VoteView
import org.springframework.dao.DataAccessException
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Created by pacman29 on 27.02.17.
 */
@RestController
@RequestMapping(value = "/api/thread/{slug_or_id}")
class Thread : Inferior() {
    @RequestMapping(value = "/create", method = arrayOf(RequestMethod.POST), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun createPosts(@RequestBody posts: List<PostView>,
                    @PathVariable(value = "slug_or_id") slug_or_id: String): ResponseEntity<List<PostView>> {
        try {
            val thread = jdbcThreadDAO!!.get_id_slug(slug_or_id)
            if (posts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body<List<PostView>>(null)
            }
            for (post in posts) {
                if (post.parent as Int != 0) {
                    try {
                        val parent = jdbcPostDAO!!.get_id(post.parent)
                        if (thread.id != parent.thread) {
                            return ResponseEntity.status(HttpStatus.CONFLICT).body<List<PostView>>(null)
                        }
                    } catch (ex: EmptyResultDataAccessException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body<List<PostView>>(null)
                    }
                }
                post.forum = thread.forum
                post.thread = thread.id
            }
            jdbcPostDAO!!.create(posts, slug_or_id)
        } catch (ex: DuplicateKeyException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body<List<PostView>>(null)
        } catch (ex: DataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body<List<PostView>>(null)
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(posts)
    }

    @RequestMapping(value = "/details", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun viewThread(@PathVariable(value = "slug_or_id") slug_or_id: String): ResponseEntity<ThreadView> {
        val thread: ThreadView
        try {
            thread = jdbcThreadDAO!!.get_id_slug(slug_or_id)
        } catch (ex: DataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body<ThreadView>(null)
        }
        return ResponseEntity.status(HttpStatus.OK).body(thread)
    }

    @RequestMapping(value = "/details", method = arrayOf(RequestMethod.POST), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun updateThread(@RequestBody body: ThreadView,
                     @PathVariable(value = "slug_or_id") slug_or_id: String): ResponseEntity<ThreadView> {
        var thread = body
        try {
            jdbcThreadDAO!!.update(thread.message, thread.title, slug_or_id)
            thread = jdbcThreadDAO!!.get_id_slug(slug_or_id)
        } catch (ex: DuplicateKeyException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(jdbcThreadDAO!!.get_id_slug(slug_or_id))
        } catch (ex: DataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body<ThreadView>(null)
        }
        return ResponseEntity.status(HttpStatus.OK).body(thread)
    }

    @RequestMapping(value = "/vote", method = arrayOf(RequestMethod.POST), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun voteForThread(@RequestBody vote: VoteView,
                      @PathVariable("slug_or_id") slug_or_id: String): ResponseEntity<ThreadView> {
        val thread: ThreadView
        try {
            thread = jdbcThreadDAO!!.update_votes(vote, slug_or_id)
        } catch (ex: DuplicateKeyException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(jdbcThreadDAO!!.get_id_slug(slug_or_id))
        } catch (ex: DataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body<ThreadView>(null)
        }
        return ResponseEntity.status(HttpStatus.OK).body(thread)
    }

    @RequestMapping(value = "/posts", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun viewThreads(
            @RequestParam(value = "limit", required = false, defaultValue = "100") limit: Int?,
            @RequestParam(value = "marker", required = false) marker: String?,
            @RequestParam(value = "sort", required = false, defaultValue = "flat") sort: String?,
            @RequestParam(value = "desc", required = false, defaultValue = "false") desc: Boolean?,
            @PathVariable("slug_or_id") slug_or_id: String): ResponseEntity<PostsSortedView> {
        var temp = marker
        if (marker == null) {
            temp = "0"
        }
        val posts = jdbcPostDAO!!.sort(limit, Integer.parseInt(temp), sort, desc, slug_or_id)
        if (posts.isEmpty() && temp == "0") {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body<PostsSortedView>(null)
        }
        return ResponseEntity.status(HttpStatus.OK).body(PostsSortedView(
                if (!posts.isEmpty()) (Integer.parseInt(temp) + limit!!).toString() else temp, posts))
    }
}
