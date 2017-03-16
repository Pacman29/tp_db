package db.DatabaseServices;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by pacman29 on 15.03.17.
 */
@Service
public final class ForumsTableService {

    private final JdbcTemplate jdbc;

    public ForumsTableService(final JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public static class ForumModel{
        private Integer posts;
        private String slug;
        private Integer threads;
        private String title;
        private String user;

        public ForumModel(Integer posts, String slug, Integer threads, String title, String user) {
            this.posts = posts;
            this.slug = slug;
            this.threads = threads;
            this.title = title;
            this.user = user;
        }

        public ForumModel() {
            this.slug = this.title = this.user = "";
        }

        public Integer getPosts() {
            return posts;
        }

        public void setPosts(Integer posts) {
            this.posts = posts;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public Integer getThreads() {
            return threads;
        }

        public void setThreads(Integer threads) {
            this.threads = threads;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUser() { return user;        }

        public void setUser(String user) {
            this.user = user;
        }
    }

    public static ForumModel read(ResultSet rs, int rowNum) throws SQLException {
        return new ForumModel(
                rs.getInt("posts"),
                rs.getString("slug"),
                rs.getInt("threads"),
                rs.getString("title"),
                rs.getString("user")
        );
    }

    public final void insert(final ForumModel forum) {
        jdbc.update("INSERT INTO forums (slug, title, \"user\") VALUES(?, ?, " +
                        "(SELECT nickname FROM users WHERE LOWER(nickname) = LOWER(?)))",
                forum.getSlug(), forum.getTitle(), forum.getUser());
    }

    public final List<ForumModel> get(final ForumModel forum) {
        return jdbc.query(
                "SELECT * FROM forums WHERE LOWER(slug) = LOWER(?)",
                new Object[]{forum.getSlug()},
                ForumsTableService::read);
    }

}
