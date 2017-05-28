package db_project.DAO.JdbcDAO

import db_project.DAO.ForumDAO
import db_project.DAO.Queries.ForumQueries
import db_project.Views.ForumView
import db_project.Views.ThreadView
import db_project.Views.UserView
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

import java.util.ArrayList

/**
 * Created by pacman29 on 9.05.17.
 */
@Service
class JdbcForumDAO(jdbcTemplate: JdbcTemplate) : JdbcInferiorDAO(jdbcTemplate), ForumDAO {

    override fun create(username: String?, slug: String?, title: String?) {
        jdbcTemplate.update(ForumQueries.create, username, slug, title)
    }

    override fun get_slag(slug: String?): ForumView {
        return jdbcTemplate.queryForObject(ForumQueries.get, arrayOf(slug as Any), readForum)
    }

    override fun get_treads(slug: String?, limit: Int?, since: String?, desc: Boolean?): List<ThreadView> {
        val sql = StringBuilder(ForumQueries.get_threads_by_forum)
        val args = ArrayList<Any>()
        args.add(slug as Any)
        if (since != null) {
            sql.append(" AND t.created ")
            sql.append(if (desc as Boolean) "<= ?" else ">= ?")
            args.add(since)
        }
        sql.append(" ORDER BY t.created")
        sql.append(if (desc as Boolean) " DESC" else "")
        sql.append(" LIMIT ?")
        args.add(limit as Any)
        return jdbcTemplate.query(sql.toString(), args.toTypedArray(), readThread)
    }

    override fun get_users(slug: String?, limit: Int?, since: String?, desc: Boolean?): List<UserView> {
        val forumId = jdbcTemplate.queryForObject("SELECT id FROM forums WHERE slug = ?", Int::class.java, slug)
        val sql = StringBuilder(ForumQueries.get_users_by_forum)
        val args = ArrayList<Any>()
        args.add(forumId)
        if (since != null) {
            sql.append(" AND u.nickname ")
            sql.append(if (desc as Boolean) "< ?" else "> ?")
            args.add(since)
        }
        sql.append(" ORDER BY u.nickname COLLATE ucs_basic")
        sql.append(if (desc as Boolean) " DESC" else "")
        sql.append(" LIMIT ?")
        args.add(limit as Any)
        return jdbcTemplate.query(sql.toString(), args.toTypedArray(), readUser)
    }

    override fun count(): Int? {
        return jdbcTemplate.queryForObject(ForumQueries.count, Int::class.java)
    }

    override fun clear() {
        jdbcTemplate.execute(ForumQueries.clear)
    }
}
