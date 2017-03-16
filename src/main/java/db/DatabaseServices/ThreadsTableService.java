package db.DatabaseServices;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pacman29 on 16.03.17.
 */
@Service
public final class ThreadsTableService {

    private final JdbcTemplate jdbc;

    public ThreadsTableService(final JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

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

    public final List<ThreadModel> insert(final ThreadModel thread) {
        if (thread.getCreated() == null) {
            thread.setCreated(LocalDateTime.now().toString());
        }

        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.parse(thread.getCreated(), DateTimeFormatter.ISO_DATE_TIME));

        if (!thread.getCreated().endsWith("Z")) {
            timestamp = Timestamp.from(timestamp.toInstant().plusSeconds(-10800));
        }


        jdbc.update("INSERT INTO threads (author, created, forum, \"message\", " +
                "slug, title) VALUES(?, ?, " +
                "(SELECT slug FROM forums WHERE LOWER(slug) = LOWER(?)), " +
                "?, ?, ?)", thread.getAuthor(), timestamp, thread.getForum(),
                thread.getMessage(), thread.getSlug(), thread.getTitle()
        );

        jdbc.update("UPDATE forums SET threads = threads + 1 WHERE LOWER(slug) = LOWER(?)",
                thread.getForum());

        return get(thread);
    }

    public final List<ThreadModel> get(final ThreadModel thread) {
        return jdbc.query(
                "SELECT * FROM threads WHERE LOWER(slug) = LOWER(?)",
                new Object[]{thread.getSlug()},
                ThreadsTableService::read
        );
    }

    public final List<ThreadModel> getInfo(
            final ThreadModel thread,
            final Integer limit,
            final String since,
            final Boolean desc) {

        String query = "SELECT * FROM threads WHERE LOWER(forum) = LOWER(?)";
        final List<Object> args = new ArrayList<>();
        args.add(thread.getSlug());

        if (since != null) {
            query +=" AND created ";

            if (desc == Boolean.TRUE) {
                query += "<= ?";

            } else {
                query += ">= ?";
            }

            args.add(Timestamp.valueOf(LocalDateTime.parse(since, DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        }

        query+= " ORDER BY created";

        if (desc == Boolean.TRUE) {
            query += " DESC";
        }

        query+= " LIMIT ?";
        args.add(limit);

        return jdbc.query(query,
                args.toArray(new Object[args.size()]),
                ThreadsTableService::read
        );
    }




    public static ThreadModel read(ResultSet rs, int rowNum) throws SQLException {
        final Timestamp timestamp = rs.getTimestamp("created");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+03:00"));

        return new ThreadModel(
                rs.getString("author"),
                dateFormat.format(timestamp),
                rs.getString("forum"),
                rs.getInt("id"),
                rs.getString("message"),
                rs.getString("slug"),
                rs.getString("title"),
                rs.getInt("votes")
        );
    }
}