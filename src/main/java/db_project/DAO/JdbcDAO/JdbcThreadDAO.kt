package db_project.DAO.JdbcDAO

import db_project.DAO.ThreadDAO
import db_project.DAO.Queries.ForumQueries
import db_project.DAO.Queries.ThreadQueries
import db_project.DAO.Queries.UserQueries
import db_project.Views.ThreadView
import db_project.Views.VoteView
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

import java.util.ArrayList

/**
 * Created by pacman29 on 9.05.17.
 */
@Service
class JdbcThreadDAO(jdbcTemplate: JdbcTemplate) : JdbcInferiorDAO(jdbcTemplate), ThreadDAO {

    override fun create(author: String?, created: String?, forum: String?,
                        message: String?, slug: String?, title: String?): ThreadView {
        val userId = jdbcTemplate.queryForObject(UserQueries.find_by_id, arrayOf(author), Int::class.java)
        val forumId = jdbcTemplate.queryForObject(ForumQueries.get_id, arrayOf(forum), Int::class.java)
        val threadId: Int
        if (created == null) {
            threadId = jdbcTemplate.queryForObject(ThreadQueries.insert_without_time,
                    arrayOf(userId, forumId, message, slug, title), Int::class.java)
        } else {
            threadId = jdbcTemplate.queryForObject(ThreadQueries.insert_with_time,
                    arrayOf(userId, created, forumId, message, slug, title), Int::class.java)
        }
        return jdbcTemplate.queryForObject(ThreadQueries.get_Thread(threadId.toString()),
                arrayOf(threadId), readThread)
    }

    override fun update(message: String?, title: String?, slug_or_id: String?) {
        val sql = StringBuilder("UPDATE threads SET")
        val args = ArrayList<Any>()
        if (message != null) {
            sql.append(" message = ?,")
            args.add(message)
        }
        if (title != null) {
            sql.append(" title = ?,")
            args.add(title)
        }
        if (!args.isEmpty()) {
            sql.delete(sql.length - 1, sql.length)
            sql.append(if (slug_or_id!!.matches("\\d+".toRegex())) " WHERE id = ?" else " WHERE slug = ?")
            args.add(slug_or_id as Any)
            jdbcTemplate.update(sql.toString(), *args.toTypedArray())
        }
    }

    override fun get_id_slug(slug_or_id: String?): ThreadView {
        return jdbcTemplate.queryForObject(ThreadQueries.get_Thread(slug_or_id as String), arrayOf(slug_or_id), readThread)
    }

    override fun update_votes(view: VoteView?, slug_or_id: String?): ThreadView {
        val userId = jdbcTemplate.queryForObject(UserQueries.find_by_id, Int::class.java, view!!.nickname)
        val threadId = if (slug_or_id!!.matches("\\d+".toRegex()))
            Integer.valueOf(slug_or_id)
        else
            jdbcTemplate.queryForObject(ThreadQueries.get_thread_id, Int::class.java, slug_or_id)
        val query = StringBuilder("SELECT update_or_insert_votes(")
        query.append(userId!!.toString()).append(", ").append(threadId!!.toString())
                .append(", ").append(view.voice).append(")")
        jdbcTemplate.execute(query.toString())
        return jdbcTemplate.queryForObject(ThreadQueries.get_Thread(slug_or_id), arrayOf<Any>(slug_or_id), readThread)
    }

    override fun count(): Int? {
        return jdbcTemplate.queryForObject(ThreadQueries.count, Int::class.java)
    }

    override fun clear() {
        jdbcTemplate.execute(ThreadQueries.clear)
    }
}
