package db.DatabaseServices;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by pacman29 on 16.03.17.
 */
@Service
public final class UsersTableService {
    public static class UserModel{
        private String about;
        private String email;
        private String fullname;
        private String nickname;

        public UserModel(String about, String email, String fullname, String nickname) {
            this.about = about;
            this.email = email;
            this.fullname = fullname;
            this.nickname = nickname;
        }

        public UserModel() {
            this.about = this.email = this.fullname = this.nickname = "";
        }

        public String getAbout() {
            return about;
        }

        public void setAbout(String about) {
            this.about = about;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        @Override
        public String toString() {
            return "UserModel{" +
                    "about='" + about + '\'' +
                    ", email='" + email + '\'' +
                    ", fullname='" + fullname + '\'' +
                    ", nickname='" + nickname + '\'' +
                    '}';
        }
    }

    public static UserModel read(ResultSet rs, int rowNum) throws SQLException {
        return new UserModel(rs.getString("about"),
                             rs.getString("email"),
                             rs.getString("fullname"),
                             rs.getString("nickname"));
    }

    public UsersTableService(final JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final JdbcTemplate jdbc;



    public final void insert(final UserModel user){
        System.out.println("test insert user");
        System.out.println(user);
        jdbc.update("INSERT INTO users (about, email, fullname, nickname) VALUES (?,?,?,?)",
                user.getAbout(),user.getEmail(),user.getFullname(),user.getNickname());
        System.out.println("test insert user");
    }

    public final List<UserModel> get(final UserModel user){
        return jdbc.query("SELECT * FROM users WHERE LOWER(nickname) = LOWER(?) OR LOWER(email) = LOWER(?)",
                new Object[]{user.getNickname(),user.getEmail()},UsersTableService::read);
    }

    public final void update(final UserModel user){
        String sql = new String("UPDATE users SET");
        final List<CharSequence> fields = new ArrayList<>();
        final List<Object> args = new ArrayList<>();

        if (!user.getAbout().isEmpty()) {
            fields.add(" about = ?");
            args.add(user.getAbout());
        }

        if (!user.getEmail().isEmpty()) {
            fields.add(" email = ?");
            args.add(user.getEmail());
        }

        if (!user.getFullname().isEmpty()) {
            fields.add(" fullname = ?");
            args.add(user.getFullname());
        }

        /*
        for (Field item : user.getClass().getDeclaredFields()) {
            item.setAccessible(true);
            String val = null;
            try {
                val = item.get(user).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if(!val.isEmpty()){
                fields.add(item.getName()+"= ?");
                args.add(val);
            }

        }
        */


        if (args.isEmpty()) {
            return;
        }

        sql += String.join(",",fields) + " WHERE LOWER(nickname) = LOWER(?)";
        args.add(user.getNickname());
        jdbc.update(sql.toString(), args.toArray());
    }


    public final List<UserModel> getInfo(
            final String slug,
            final Integer limit,
            final String since,
            final Boolean desc
    ) {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE LOWER(users.nickname) IN " +
                "(SELECT LOWER(posts.author) FROM posts WHERE LOWER(posts.forum) = LOWER(?) " +
                "UNION " +
                "SELECT LOWER(threads.author) FROM threads WHERE LOWER(threads.forum) = LOWER(?))");
        final List<Object> args = new ArrayList<>();
        args.add(slug);
        args.add(slug);

        if (since != null) {
            sql.append(" AND LOWER(users.nickname) ");

            if (desc == Boolean.TRUE) {
                sql.append("< LOWER(?)");

            } else {
                sql.append("> LOWER(?)");
            }

            args.add(since);
        }

        sql.append(" ORDER BY LOWER(users.nickname) COLLATE ucs_basic");

        if (desc == Boolean.TRUE) {
            sql.append(" DESC");
        }

        sql.append(" LIMIT ?");
        args.add(limit);

        return jdbc.query(
                sql.toString(),
                args.toArray(new Object[args.size()]),
                UsersTableService::read
        );
    }
}
