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

import db.DatabaseServices.UservotesTableController.VoteModel;
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

        @Override
        public String toString() {
            return "ThreadModel{" +
                    "author='" + author + '\'' +
                    ", created='" + created + '\'' +
                    ", forum='" + forum + '\'' +
                    ", id=" + id +
                    ", message='" + message + '\'' +
                    ", slug='" + slug + '\'' +
                    ", title='" + title + '\'' +
                    ", votes=" + votes +
                    '}';
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

        System.out.println(thread);
        jdbc.update("INSERT INTO threads (author, created, forum, \"message\", " +
                "slug, title) VALUES(?, ?, " +
                "(SELECT slug FROM forums WHERE LOWER(slug) = LOWER(?)), " +
                "?, ?, ?)", thread.getAuthor(), timestamp, thread.getForum(),
                thread.getMessage(), thread.getSlug(), thread.getTitle()
        );
        System.out.println("insert ThreadsTable test");
        jdbc.update("UPDATE forums SET threads = threads + 1 WHERE LOWER(slug) = LOWER(?)",
                thread.getForum());
        System.out.println("insert ThreadsTable test");
        return get(thread.slug);
    }

    public final List<ThreadModel> get(final String slug) {
        return jdbc.query(
                "SELECT * FROM threads WHERE LOWER(slug) = LOWER(?)",
                new Object[]{slug},
                ThreadsTableService::read
        );
    }

    public final List<ThreadModel> get(final Integer id) {
        return jdbc.query(
                "SELECT * FROM threads WHERE id = ?",
                new Object[]{id},
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

    public List<ThreadModel> updateVotes(String slug,Integer vote){
        System.out.println(slug.toString() +"  "+ vote.toString());
        jdbc.update("UPDATE threads SET votes = votes+ ("+ vote.toString() +") WHERE LOWER(threads.slug) = LOWER(?)",
                slug);
        return jdbc.query("SELECT * FROM threads WHERE LOWER(threads.slug) = LOWER(?)",
                new Object[]{slug}, ThreadsTableService::read);
    }

    public List<ThreadModel> updateVotes(Integer id,Integer vote){
        System.out.println(id.toString() +"  "+ vote.toString());
        jdbc.update("UPDATE threads SET votes = votes+ ("+ vote.toString()+") WHERE id = ?",
                id);
        return jdbc.query("SELECT * FROM threads WHERE id = ?",
                new Object[]{id}, ThreadsTableService::read);
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


    public final void updateThreadInfo(final ThreadModel thread, final String slug) {
        final StringBuilder sql = new StringBuilder("UPDATE threads SET");
        final List<Object> args = new ArrayList<>();
        final List<String> str_args = new ArrayList<>();

        if (thread.getMessage() != null && !thread.getMessage().isEmpty()) {
            str_args.add(" message = ?");
            args.add(thread.getMessage());
        }

        if (thread.getTitle() != null && !thread.getTitle().isEmpty()) {
            str_args.add(" title = ?");
            args.add(thread.getTitle());
        }

        if (args.isEmpty()) {
            return;
        }

        sql.append(String.join(",",str_args));
        final Integer id;

        sql.append(" WHERE LOWER(slug) = LOWER(?)");
        args.add(slug);

        jdbc.update(sql.toString(), args.toArray());
    }

    public final void updateThreadInfo(final ThreadModel thread, final Integer id) {
        final StringBuilder sql = new StringBuilder("UPDATE threads SET");
        final List<Object> args = new ArrayList<>();
        final List<String> str_args = new ArrayList<>();

        if (thread.getMessage() != null && !thread.getMessage().isEmpty()) {
            str_args.add(" message = ?");
            args.add(thread.getMessage());
        }

        if (thread.getTitle() != null && !thread.getTitle().isEmpty()) {
            str_args.add(" title = ?");
            args.add(thread.getTitle());
        }

        if (args.isEmpty()) {
            return;
        }

        sql.append(String.join(",",str_args));
        sql.append(" WHERE id = ?");
        args.add(id);

        jdbc.update(sql.toString(), args.toArray());
    }
}
