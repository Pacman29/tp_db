package db_project.Views

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by pacman29 on 9.05.17.
 */
class PostDetailedView(@param:JsonProperty("author") var author: UserView?,
                       @param:JsonProperty("forum") var forum: ForumView?,
                       @param:JsonProperty("post") var post: PostView?,
                       @param:JsonProperty("thread") var thread: ThreadView?)
