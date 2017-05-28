package db_project.DAO.JdbcDAO

import db_project.DAO.UserDAO
import db_project.DAO.Queries.UserQueries
import db_project.Views.UserView
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

import java.util.ArrayList

/**
 * Created by pacman29 on 9.05.17.
 */
@Service
class JdbcUserDAO(jdbcTemplate: JdbcTemplate) : JdbcInferiorDAO(jdbcTemplate), UserDAO {

    override fun create(about: String?, email: String?, fullname: String?, nickname: String?) {
        jdbcTemplate.update(UserQueries.create, about, email, fullname, nickname)
    }

    override fun update(about: String?, email: String?, fullname: String?, nickname: String?) {
        val sql = StringBuilder("UPDATE users SET")
        val args = ArrayList<Any>()
        if (about != null) {
            sql.append(" about = ?,")
            args.add(about)
        }
        if (email != null) {
            sql.append(" email = ?,")
            args.add(email)
        }
        if (fullname != null) {
            sql.append(" fullname = ?,")
            args.add(fullname)
        }
        if (!args.isEmpty()) {
            sql.delete(sql.length - 1, sql.length)
            sql.append(" WHERE nickname = ?")
            args.add(nickname as Any)
            jdbcTemplate.update(sql.toString(), *args.toTypedArray())
        }
    }

    override fun get_one_email_nick(nickname: String?, email: String?): UserView {
        return jdbcTemplate.queryForObject(UserQueries.find, arrayOf(nickname, email), readUser)
    }

    override fun get_users_nick_email(nickname: String?, email: String?): List<UserView> {
        return jdbcTemplate.query(UserQueries.find, arrayOf(nickname, email), readUser)
    }

    override fun count(): Int? {
        return jdbcTemplate.queryForObject(UserQueries.count, Int::class.java)
    }

    override fun clear() {
        jdbcTemplate.execute(UserQueries.clear)
    }
}
