package db_project.DAO.Queries

/**
 * Created by pacman29 on 29.04.17.
 */
object ForumQueries {
    val create: String
        get() = "INSERT INTO forums (user_id, slug, title) VALUES((SELECT id FROM users WHERE nickname = ?), ?, ?)"

    val get: String
        get() = "SELECT f.posts, f.slug, f.threads, f.title, u.nickname " +
                "FROM forums f " +
                "  JOIN users u ON (f.user_id = u.id)" +
                "  WHERE f.slug = ?"

    val get_id: String
        get() = "SELECT id FROM forums WHERE slug = ?"

    val get_threads_by_forum: String
        get() = "SELECT u.nickname, t.created, f.slug as f_slug, t.id, t.message, t.slug as t_slug, t.title, t.votes " +
                "FROM threads t " +
                "  JOIN users u ON (t.user_id = u.id)" +
                "  JOIN forums f ON (t.forum_id = f.id) " +
                "  WHERE f.slug = ?"

    val get_users_by_forum: String
        get() = "SELECT u.about, u.email, u.fullname, u.nickname " +
                "FROM users u " +
                "WHERE u.id IN (" +
                "  SELECT user_id " +
                "  FROM forum_users " +
                "  WHERE forum_id = ?" +
                ")"

    val count: String
        get() = "SELECT COUNT(*) FROM forums"

    val clear: String
        get() = "DELETE FROM forums"
}
