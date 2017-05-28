package db_project.DAO.Queries

/**
 * Created by pacman29 on 29.04.17.
 */
object UserQueries {
    val create: String
        get() = "INSERT INTO users (about, email, fullname, nickname) VALUES(?, ?, ?, ?)"

    val find: String
        get() = "SELECT * FROM users WHERE nickname = ? OR email = ?"

    val find_by_id: String
        get() = "SELECT id FROM users WHERE nickname = ?"

    val count: String
        get() = "SELECT COUNT(*) FROM users"

    val clear: String
        get() =  "DELETE FROM users"
}
