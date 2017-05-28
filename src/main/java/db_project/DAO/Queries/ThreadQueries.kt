package db_project.DAO.Queries

/**
 * Created by pacman29 on 28.04.17.
 */
object ThreadQueries {
    val get_forum_id: String
        get() = "SELECT forums.id FROM forums " +
                "JOIN threads ON (threads.forum_id = forums.id) " +
                "WHERE threads.id = ?"

    val get_thread_id: String
        get() = "SELECT id FROM threads WHERE slug = ?"

    val insert_with_time: String
        get() = "INSERT INTO threads (user_id, created, forum_id, message, slug, title) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id"

    val insert_without_time: String
        get() = "INSERT INTO threads (user_id, forum_id, message, slug, title) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id"

    val update_ForumsPosts_count: String
        get() = "UPDATE forums SET posts = posts + ? WHERE forums.id = ?"

    fun get_Thread(slug_or_id: String): String {
        return "SELECT u.nickname, t.created, f.slug AS f_slug, t.id, t.message, t.slug AS t_slug, t.title, t.votes " +
                "FROM threads t " +
                "  JOIN users u ON (t.user_id = u.id)" +
                "  JOIN forums f ON (t.forum_id = f.id) " +
                "  WHERE " + if (slug_or_id.matches("\\d+".toRegex())) "t.id = ?" else "t.slug = ?"
    }

    val count: String
        get() = "SELECT COUNT(*) FROM threads"

    val clear: String
        get() = "DELETE FROM threads"
}
