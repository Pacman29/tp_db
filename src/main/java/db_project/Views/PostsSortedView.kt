package db_project.Views

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by pacman29 on 9.05.17.
 */
class PostsSortedView(@param:JsonProperty("marker") var marker: String?,
                      @param:JsonProperty("posts") var posts: List<PostView>?)