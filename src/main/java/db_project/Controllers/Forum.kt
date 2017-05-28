package db_project.Controllers

import db_project.Views.ForumView
import db_project.Views.ThreadView
import db_project.Views.UserView
import org.springframework.dao.DataAccessException
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Created by pacman29 on 27.02.17.
 */
@RestController
@RequestMapping(value = "api/forum")
class Forum : Inferior() {
    @RequestMapping(value = "/create", method = arrayOf(RequestMethod.POST), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun createForum(@RequestBody forum: ForumView): ResponseEntity<ForumView> {
        try {
            jdbcForumDAO?.create(forum.user, forum.slug, forum.title)
        } catch (ex: DuplicateKeyException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(jdbcForumDAO?.get_slag(forum.slug))
        } catch (ex: DataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body<ForumView>(null)
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(jdbcForumDAO?.get_slag(forum.slug))
    }

    @RequestMapping(value = "/{slug}/create", method = arrayOf(RequestMethod.POST), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun createSlug(@RequestBody body: ThreadView,
                   @PathVariable(value = "slug") slug: String): ResponseEntity<ThreadView> {
        val thread: ThreadView?
        val threadSlug = body.slug
        try {
            thread = jdbcThreadDAO?.create(body.author, body.created, slug,
                    body.message, body.slug, body.title)
        } catch (ex: DuplicateKeyException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(jdbcThreadDAO!!.get_id_slug(threadSlug))
        } catch (ex: DataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body<ThreadView>(null)
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(thread)
    }

    @RequestMapping(value = "/{slug}/details", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun viewForum(@PathVariable("slug") slug: String): ResponseEntity<ForumView> {
        val forum: ForumView?
        try {
            forum = jdbcForumDAO?.get_slag(slug)
        } catch (ex: DataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body<ForumView>(null)
        }
        return ResponseEntity.status(HttpStatus.OK).body(forum)
    }

    @RequestMapping(value = "/{slug}/threads", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun viewThreads(
            @RequestParam(value = "limit", required = false, defaultValue = "100") limit: Int?,
            @RequestParam(value = "since", required = false) since: String?,
            @RequestParam(value = "desc", required = false, defaultValue = "false") desc: Boolean?,
            @PathVariable("slug") slug: String): ResponseEntity<List<ThreadView>> {
        try {
            jdbcForumDAO?.get_slag(slug)
        } catch (ex: DataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body<List<ThreadView>>(null)
        }
        return ResponseEntity.status(HttpStatus.OK).body(jdbcForumDAO?.get_treads(slug, limit, since, desc))
    }

    @RequestMapping(value = "/{slug}/users", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun viewUsers(
            @RequestParam(value = "limit", required = false, defaultValue = "100") limit: Int?,
            @RequestParam(value = "since", required = false) since: String?,
            @RequestParam(value = "desc", required = false, defaultValue = "false") desc: Boolean?,
            @PathVariable("slug") slug: String): ResponseEntity<List<UserView>> {
        try {
            jdbcForumDAO?.get_slag(slug)
        } catch (ex: DataAccessException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body<List<UserView>>(null)
        }
        return ResponseEntity.status(HttpStatus.OK).body(jdbcForumDAO?.get_users(slug, limit, since, desc))
    }
}
