package db_project.DAO

import db_project.Views.UserView

/**
 * Created by pacman29 on 9.05.17.
 */
interface UserDAO {
    fun create(about: String?, email: String?, fullname: String?, nickname: String?)
    fun update(about: String?, email: String?, fullname: String?, nickname: String?)
    fun get_one_email_nick(nickname: String?, email: String?): UserView
    fun get_users_nick_email(nickname: String?, email: String?): List<UserView>
    fun count(): Int?
    fun clear()
}
