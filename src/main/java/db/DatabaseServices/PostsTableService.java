package db.DatabaseServices;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by pacman29 on 16.03.17.
 */
@Service
public class PostsTableService {
    private final JdbcTemplate jdbc;

    public PostsTableService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public static class PostModel{
        private String author;
        private String created;
        private String forum;
        private Integer id;
        private Boolean isEdited;
        private String message;
        private Integer parent;
        private Integer thread;

        public PostModel(String author,
                         String created,
                         String forum,
                         Integer id,
                         Boolean isEdited,
                         String message,
                         Integer parent,
                         Integer thread) {
            this.author = author;
            this.created = created;
            this.forum = forum;
            this.id = id;
            this.isEdited = isEdited;
            this.message = message;
            this.parent = parent;
            this.thread = thread;
        }

        public PostModel() {
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

        public Boolean getEdited() {
            return isEdited;
        }

        public void setEdited(Boolean edited) {
            isEdited = edited;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Integer getParent() {
            return parent;
        }

        public void setParent(Integer parent) {
            this.parent = parent;
        }

        public Integer getThread() {
            return thread;
        }

        public void setThread(Integer thread) {
            this.thread = thread;
        }
    }

    public static PostModel read(ResultSet rs, int rowNum) throws SQLException {
        final Timestamp timestamp = rs.getTimestamp("created");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+03:00"));

        return new PostModel(
                rs.getString("author"),
                dateFormat.format(timestamp),
                rs.getString("forum"),
                rs.getInt("id"),
                rs.getBoolean("isEdited"),
                rs.getString("message"),
                rs.getInt("parent"),
                rs.getInt("thread")
        );
    }

    public final List<PostModel> insertPosts(final List<PostModel> posts, final String slug) {
        final Map<String,String> requests = makeRequests(slug);

        for (PostModel post : posts) {

            if (post.getParent() != 0) {
                final List<Integer> dBPosts = jdbc.queryForList(requests.get("check"), Integer.class, slug);

                if (!dBPosts.contains(post.getParent())) {
                    throw new DuplicateKeyException(null);
                }
            }

            if (post.getCreated() == null) {
                post.setCreated(LocalDateTime.now().toString());
            }

            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.parse(post.getCreated(), DateTimeFormatter.ISO_DATE_TIME));

            if (!post.getCreated().endsWith("Z")) {
                timestamp = Timestamp.from(timestamp.toInstant().plusSeconds(-10800));
            }

            jdbc.update(requests.get("insert"), post.getAuthor(), timestamp,slug,
                    post.getMessage(),slug, post.getParent());
        }

        jdbc.update(requests.get("update"), posts.size(), slug);

        final List<PostModel> dbPosts = jdbc.query(requests.get("get"),
                new Object[]{slug}, PostsTableService::read);
        final Integer beginIndex = dbPosts.size() - posts.size();
        final Integer endIndex = dbPosts.size();

        return dbPosts.subList(beginIndex, endIndex);
    }


    private final Map<String,String> makeRequests(final String slug) {

        Map<String,String> requests = new HashMap<>();

        requests.put("insert","INSERT INTO posts (author, created, forum, \"message\", thread, parent) ");
        requests.put("get","SELECT * FROM posts WHERE thread =");
        requests.put("update","UPDATE forums SET posts = posts + ? " +
                "WHERE forums.slug = (SELECT threads.forum FROM threads WHERE");
        requests.put("check","SELECT posts.id FROM posts WHERE posts.thread = ");

        if(slug.matches("^-?\\d+$"))
        {
            requests.merge("insert",
                           "VALUES(?, ?, (SELECT forum FROM threads WHERE id = ?), ?, ?, ?)",
                          String::concat);
            requests.merge("get",
                    " ?",
                    String::concat);
            requests.merge("update",
                    " threads.id = ?)",
                    String::concat);
            requests.merge("check",
                    " ?",
                    String::concat);
        } else {
            requests.merge("insert",
                    "VALUES(?, ?, (SELECT forum FROM threads WHERE LOWER(slug) = LOWER(?)), ?," +
                            "(SELECT id FROM threads WHERE LOWER(slug) = LOWER(?)), ?)",
                    String::concat);
            requests.merge("get",
                    " (SELECT id FROM threads WHERE LOWER(slug) = LOWER(?))",
                    String::concat);
            requests.merge("update",
                    " threads.slug = ?)",
                    String::concat);
            requests.merge("check",
                    " (SELECT threads.id FROM threads WHERE LOWER(threads.slug) = LOWER(?))",
                    String::concat);
        }

        requests.merge("get"," ORDER BY posts.id",String::concat);

        return requests;
    }
}
