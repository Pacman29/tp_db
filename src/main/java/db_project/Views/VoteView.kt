package db_project.Views

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by pacman29 on 9.05.17.
 */
class VoteView(@param:JsonProperty("nickname") var nickname: String?,
               @param:JsonProperty("voice") var voice: Int?)
