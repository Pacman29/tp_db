package db_project.Views

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by pacman29 on 9.05.17.
 */
class PostView(@param:JsonProperty("author") var author: String?,
               @param:JsonProperty("created") var created: String?,
               @param:JsonProperty("forum") var forum: String?,
               @param:JsonProperty("id") var id: Int?,
               @JsonProperty("isEdited") isEdited: Boolean?,
               @param:JsonProperty("message") var message: String?,
               @JsonProperty("parent") parent: Int?,
               @param:JsonProperty("thread") var thread: Int?) {
    var parent: Int? = null
    @JsonProperty("isEdited") var isEdited: Boolean? = null

    init {
        this.parent = parent ?: 0
        this.isEdited = isEdited ?: false
    }
}