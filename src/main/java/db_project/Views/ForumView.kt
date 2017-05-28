package db_project.Views

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by pacman29 on 9.05.17.
 */
class ForumView(@param:JsonProperty("posts") var posts: Int?,
                @param:JsonProperty("slug") var slug: String?,
                @param:JsonProperty("threads") var threads: Int?,
                @param:JsonProperty("title") var title: String?,
                @param:JsonProperty("user") var user: String?)