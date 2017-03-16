package db.DatabaseServices;

/**
 * Created by pacman29 on 16.03.17.
 */
public class ThreadTableController {
    public static class ThreadModel{
        private String author;
        private String created;
        private String forum;
        private Integer id;
        private String message;
        private String slug;
        private String title;
        private Integer votes;

        public ThreadModel() {
        }

        public ThreadModel(String author,
                           String created,
                           String forum,
                           Integer id,
                           String message,
                           String slug,
                           String title,
                           Integer votes) {
            this.author = author;
            this.created = created;
            this.forum = forum;
            this.id = id;
            this.message = message;
            this.slug = slug;
            this.title = title;
            this.votes = votes;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getForum() {
            return forum;
        }

        public void setForum(String forum) {
            this.forum = forum;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getVotes() {
            return votes;
        }

        public void setVotes(Integer votes) {
            this.votes = votes;
        }
    }
}
