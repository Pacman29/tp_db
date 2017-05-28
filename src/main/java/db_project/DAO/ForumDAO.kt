package db_project.DAO

import db_project.Views.ForumView
import db_project.Views.ThreadView
import db_project.Views.UserView

/**
 * Created by pacman29 on 9.05.17.
 */
interface ForumDAO {
    fun create(username: String?, slug: String?, title: String?)
    fun get_slag(slug: String?): ForumView
    fun get_treads(slug: String?, limit: Int?, since: String?, desc: Boolean?): List<ThreadView>
    fun get_users(slug: String?, limit: Int?, since: String?, desc: Boolean?): List<UserView>
    fun count(): Int?
    fun clear()
}
