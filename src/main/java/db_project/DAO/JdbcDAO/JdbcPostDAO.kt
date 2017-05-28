package db_project.DAO.JdbcDAO

import db_project.DAO.PostDAO
import db_project.DAO.Queries.ForumQueries
import db_project.DAO.Queries.PostQueries
import db_project.DAO.Queries.ThreadQueries
import db_project.DAO.Queries.UserQueries
import db_project.Views.*
import org.springframework.dao.DataRetrievalFailureException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

import java.sql.*
import java.text.SimpleDateFormat
import java.util.TimeZone

/**
 * Created by pacman29 on 9.05.17.
 */
@Service
class JdbcPostDAO(jdbcTemplate: JdbcTemplate) : JdbcInferiorDAO(jdbcTemplate), PostDAO {
    @Suppress("UNCHECKED_CAST")
    override fun create(posts: List<PostView>?, slug_or_id: String?) {
        val threadId = if ((slug_or_id as String).matches("\\d+".toRegex()))
            Integer.valueOf(slug_or_id)
        else
            jdbcTemplate.queryForObject(ThreadQueries.get_thread_id, Int::class.java, slug_or_id)
        val forumId = jdbcTemplate.queryForObject(ThreadQueries.get_forum_id, Int::class.java, threadId)
        val created = Timestamp(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        try {
            jdbcTemplate.dataSource.connection.use { connection ->
                connection.autoCommit = false
                try {
                    connection.prepareStatement(PostQueries.create, Statement.NO_GENERATED_KEYS).use { postsPrepared ->
                        connection.prepareStatement(PostQueries.insert_into_forum_users, Statement.NO_GENERATED_KEYS).use { userForumsPrepared ->
                            for (post in posts as List<PostView>) {
                                val userId = jdbcTemplate.queryForObject(UserQueries.find_by_id, Int::class.java, post.author)
                                val postId = jdbcTemplate.queryForObject("SELECT nextval('posts_id_seq')", Int::class.java)
                                postsPrepared.setInt(1, userId!!)
                                postsPrepared.setTimestamp(2, created)
                                postsPrepared.setInt(3, forumId!!)
                                postsPrepared.setInt(4, postId!!)
                                postsPrepared.setString(5, post.message)
                                postsPrepared.setInt(6, post.parent!!)
                                postsPrepared.setInt(7, threadId!!)
                                postsPrepared.setInt(8, post.parent!!)
                                postsPrepared.setInt(9, postId)
                                postsPrepared.addBatch()
                                userForumsPrepared.setInt(1, userId)
                                userForumsPrepared.setInt(2, forumId)
                                userForumsPrepared.addBatch()
                                post.created = dateFormat.format(created)
                                post.id = postId
                            }
                            postsPrepared.executeBatch()
                            userForumsPrepared.executeBatch()
                            connection.commit()
                        }
                    }
                } catch (ex: SQLException) {
                    connection.rollback()
                    throw DataRetrievalFailureException(null)
                } finally {
                    connection.autoCommit = true
                }
            }
        } catch (ex: SQLException) {
            throw DataRetrievalFailureException(null)
        }
        jdbcTemplate.update(ThreadQueries.update_ForumsPosts_count, posts?.size, forumId)
    }

    override fun update(message: String?, id: Int?): PostView {
        val post = get_id(id)
        val sql = StringBuilder("UPDATE posts SET message = ?")
        if (message != post.message) {
            sql.append(", is_edited = TRUE")
            post.isEdited = true
            post.message = message
        }
        sql.append(" WHERE id = ?")
        jdbcTemplate.update(sql.toString(), message, id)
        return post
    }

    override fun get_id(id: Int?): PostView {
        return jdbcTemplate.queryForObject(PostQueries.get, arrayOf(id), readPost)
    }

    override fun detailed(id: Int?, related: Array<String>?): PostDetailedView {
        val post = get_id(id)
        var user: UserView? = null
        var forum: ForumView? = null
        var thread: ThreadView? = null
        if (related != null) {
            for (relation in related) {
                when (relation) {
                    "user" -> user = jdbcTemplate.queryForObject(UserQueries.find,
                            arrayOf(post.author, null), readUser)
                    "forum" -> forum = jdbcTemplate.queryForObject(ForumQueries.get,
                            arrayOf(post.forum), readForum)
                    "thread" -> thread = jdbcTemplate.queryForObject(ThreadQueries.get_Thread(post.thread.toString()),
                            arrayOf(post.thread), readThread)
                }
            }
        }
        return PostDetailedView(user, forum, post, thread)
    }

    override fun sort(limit: Int?, offset: Int?, sort: String?,
                      desc: Boolean?, slug_or_id: String?): List<PostView> {
        when (sort) {
            "flat" -> return jdbcTemplate.query(PostQueries.flat_sort(slug_or_id as String, desc as Boolean),
                    arrayOf(slug_or_id, limit, offset), readPost)
            "tree" -> return jdbcTemplate.query(PostQueries.tree_sort(slug_or_id as String, desc as Boolean),
                    arrayOf(slug_or_id, limit, offset), readPost)
            "parent_tree" -> return jdbcTemplate.query(PostQueries.parent_tree_sort(slug_or_id as String, desc as Boolean),
                    arrayOf(slug_or_id, limit, offset), readPost)
            else -> throw NullPointerException()
        }
    }

    override fun count(): Int? {
        return jdbcTemplate.queryForObject(PostQueries.count, Int::class.java)
    }

    override fun clear() {
        jdbcTemplate.execute(PostQueries.clear)
    }
}
