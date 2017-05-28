package db_project.DAO.Queries

/**
 * Created by pacman29 on 1.05.17.
 */
object PostQueries {
    val create: String
        get() = "INSERT INTO posts (user_id, created, forum_id, id, message, parent, thread_id, path) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, array_append((SELECT path FROM posts WHERE id = ?), ?))"

    val insert_into_forum_users: String
        get() = "INSERT INTO forum_users (user_id, forum_id) VALUES (?, ?)"

    val get: String
        get() = "SELECT u.nickname, p.created, f.slug, p.id, p.is_edited, p.message, p.parent, p.thread_id " +
                "FROM posts p" +
                "  JOIN users u ON (u.id = p.user_id)" +
                "  JOIN forums f ON (f.id = p.forum_id) " +
                "WHERE p.id = ?"

    fun flat_sort(slug_or_id: String, desc: Boolean): String {
        return "SELECT u.nickname, p.created, f.slug, p.id, p.is_edited, p.message, p.parent, p.thread_id " +
                "FROM posts p" +
                "  JOIN users u ON (u.id = p.user_id)" +
                "  JOIN forums f ON (f.id = p.forum_id) " +
                "WHERE p.thread_id = " +
                (if (slug_or_id.matches("\\d+".toRegex()))
                    "?"
                else
                    "(SELECT threads.id FROM threads WHERE threads.slug = ?)") +
                " ORDER BY p.created " + (if (desc) "DESC" else "ASC") + ", p.id " + (if (desc) "DESC" else "ASC") +
                " LIMIT ? OFFSET ?"
    }

    fun tree_sort(slug_or_id: String, desc: Boolean): String {
        return "SELECT u.nickname, p.created, f.slug, p.id, p.is_edited, p.message, p.parent, p.thread_id " +
                "FROM posts p" +
                "  JOIN users u ON (u.id = p.user_id)" +
                "  JOIN forums f ON (f.id = p.forum_id) " +
                "WHERE p.thread_id = " +
                (if (slug_or_id.matches("\\d+".toRegex()))
                    "?"
                else
                    "(SELECT threads.id FROM threads WHERE threads.slug = ?)") +
                " ORDER BY path " + (if (desc) "DESC" else "ASC") + " LIMIT ? OFFSET ?"
    }

    fun parent_tree_sort(slug_or_id: String, desc: Boolean): String {
        return "WITH RECURSIVE some_posts AS (" +
                "    SELECT u.nickname, p.created, f.slug, p.id, p.is_edited, p.message, p.parent, p.thread_id, p.path" +
                "    FROM posts p" +
                "      JOIN users u ON (u.id = p.user_id)" +
                "      JOIN forums f ON (f.id = p.forum_id) " +
                "    WHERE p.thread_id = " +
                (if (slug_or_id.matches("\\d+".toRegex()))
                    "?"
                else
                    "(SELECT threads.id FROM threads WHERE threads.slug = ?)") +
                "), tree AS (" +
                "    (" +
                "      SELECT *" +
                "      FROM some_posts" +
                "        WHERE parent = 0" +
                "      ORDER BY id " + (if (desc) "DESC" else "ASC") +
                "      LIMIT ? OFFSET ?" +
                "    )" +
                "    UNION ALL" +
                "    (" +
                "      SELECT" +
                "        some_posts.*" +
                "      FROM tree" +
                "        JOIN some_posts ON some_posts.parent = tree.id" +
                "      WHERE some_posts.path && tree.path" +
                "    )" +
                ")" +
                "SELECT * FROM tree ORDER BY path " + if (desc) "DESC" else "ASC"
    }

    val count: String
        get() = "SELECT COUNT(*) FROM posts"

    val clear: String
        get() = "DELETE FROM posts"
}
