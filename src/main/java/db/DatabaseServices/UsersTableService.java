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
public class UsersTableService {
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
    }

    public static UserModel read(ResultSet rs, int rowNum) throws SQLException {
        return new UserModel(rs.getString("about"),
                             rs.getString("email"),
                             rs.getString("fullname"),
                             rs.getString("nickname"));
    }

    public UsersTableService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final JdbcTemplate jdbc;



    public final void insert(final UserModel user){
        jdbc.update("INSERT INTO users (about, email, fullname, nickname) VALUES (?,?,?,?)",
                user.getAbout(),user.getEmail(),user.getFullname(),user.getNickname());
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
}
