package db_project.DAO

import db_project.Views.ThreadView
import db_project.Views.VoteView

/**
 * Created by pacman29 on 9.05.17.
 */
interface ThreadDAO {
    fun create(author: String?, created: String?, forum: String?, message: String?, slug: String?, title: String?): ThreadView
    fun update(message: String?, title: String?, slug_or_id: String?)
    fun get_id_slug(slug_or_id: String?): ThreadView
    fun update_votes(view: VoteView?, slug_or_id: String?): ThreadView
    fun count(): Int?
    fun clear()
}
