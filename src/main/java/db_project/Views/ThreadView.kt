package db_project.Views

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by pacman29 on 9.05.17.
 */
class ThreadView(@param:JsonProperty("author") var author: String?,
                 @param:JsonProperty("created") var created: String?,
                 @param:JsonProperty("forum") var forum: String?,
                 @param:JsonProperty("id") var id: Int?,
                 @param:JsonProperty("message") var message: String?,
                 @param:JsonProperty("slug") var slug: String?,
                 @param:JsonProperty("title") var title: String?,
                 @param:JsonProperty("votes") var votes: Int?)