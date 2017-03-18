package db.DatabaseServices;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by pacman29 on 18.03.17.
 */
@Service
public class ServiceTableService {

    private JdbcTemplate jdbc;

    public ServiceTableService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public class ServiceModel {
        private Integer forum;
        private Integer post;
        private Integer thread;
        private Integer user;

        public ServiceModel(
               final Integer forum,
               final Integer post,
               final Integer thread,
               final Integer user
        ) {
            this.forum = forum;
            this.post = post;
            this.thread = thread;
            this.user = user;
        }

        public ServiceModel() {
        }

        public final Integer getForum() {
            return this.forum;
        }

        public void setForum(final Integer forum) {
            this.forum = forum;
        }

        public final Integer getPost() {
            return this.post;
        }

        public void setPost(final Integer post) {
            this.post = post;
        }

        public final Integer getThread() {
            return this.thread;
        }

        public void setThread(final Integer thread) {
            this.thread = thread;
        }

        public final Integer getUser() {
            return this.user;
        }

        public void setUser(final Integer user) {
            this.user = user;
        }
    }

    public ServiceModel status(){
        final Integer forumsCount = jdbc.queryForObject("SELECT COUNT(*) FROM forums", Integer.class);
        final Integer postsCount = jdbc.queryForObject("SELECT COUNT(*) FROM posts", Integer.class);
        final Integer threadsCount = jdbc.queryForObject("SELECT COUNT(*) FROM threads", Integer.class);
        final Integer usersCount = jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        return new ServiceModel(forumsCount, postsCount, threadsCount, usersCount);
    }

    public void delete(){
        jdbc.execute("DELETE FROM uservotes");
        jdbc.execute("DELETE FROM posts");
        jdbc.execute("DELETE FROM threads");
        jdbc.execute("DELETE FROM forums");
        jdbc.execute("DELETE FROM users");
    }

}
