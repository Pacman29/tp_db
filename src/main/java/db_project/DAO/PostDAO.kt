package db_project.DAO

import db_project.Views.PostDetailedView
import db_project.Views.PostView

/**
 * Created by pacman29 on 9.05.17.
 */
interface PostDAO {
    fun create(posts: List<PostView>?, slug_or_id: String?)
    fun update(message: String?, id: Int?): PostView
    fun get_id(id: Int?): PostView
    fun detailed(id: Int?, related: Array<String>?): PostDetailedView
    fun sort(limit: Int?, offset: Int?, sort: String?, desc: Boolean?, slug_or_id: String?): List<PostView>
    fun count(): Int?
    fun clear()
}
