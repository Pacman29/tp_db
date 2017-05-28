package db_project.Views

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by pacman29 on 9.05.17.
 */
class UserView(@param:JsonProperty("about") var about: String?,
               @param:JsonProperty("email") var email: String?,
               @param:JsonProperty("fullname") var fullname: String?,
               @param:JsonProperty("nickname") var nickname: String?)
