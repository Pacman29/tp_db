package db_project.DAO.JdbcDAO

import db_project.Views.ForumView
import db_project.Views.PostView
import db_project.Views.ThreadView
import db_project.Views.UserView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.support.JdbcDaoSupport
import java.sql.ResultSet

import java.text.SimpleDateFormat
import java.util.TimeZone

/**
 * Created by pacman29 on 9.05.17.
 */
open class JdbcInferiorDAO @Autowired
constructor(jdbcTemplate: JdbcTemplate) : JdbcDaoSupport() {
    init {
        setJdbcTemplate(jdbcTemplate)
    }

    protected var readUser = { rs: ResultSet, rowNum: Int ->
        UserView(rs.getString("about"), rs.getString("email"),
                rs.getString("fullname"), rs.getString("nickname"))
    }

    protected var readForum = { rs: ResultSet, rowNum: Int ->
        ForumView(rs.getInt("posts"), rs.getString("slug"),
                rs.getInt("threads"), rs.getString("title"), rs.getString("nickname"))
    }

    protected var readThread = { rs: ResultSet, rowNum: Int ->
        val timestamp = rs.getTimestamp("created")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        ThreadView(rs.getString("nickname"), dateFormat.format(timestamp.getTime()),
                rs.getString("f_slug"), rs.getInt("id"), rs.getString("message"),
                rs.getString("t_slug"), rs.getString("title"), rs.getInt("votes"))
    }

    protected var readPost = { rs: ResultSet, rowNum: Int ->
        val timestamp = rs.getTimestamp("created")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        PostView(rs.getString("nickname"), dateFormat.format(timestamp),
                rs.getString("slug"), rs.getInt("id"), rs.getBoolean("is_edited"),
                rs.getString("message"), rs.getInt("parent"), rs.getInt("thread_id"))
    }
}
