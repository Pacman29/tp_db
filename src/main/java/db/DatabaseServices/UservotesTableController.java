package db.DatabaseServices;


import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by pacman29 on 17.03.17.
 */
@Service
public final class UservotesTableController {
    public static class VoteModel {
        private String nickname;
        private Integer voice;

        public String getThread_slug() {
            return thread_slug;
        }

        public void setThread_slug(String thread_slug) {
            this.thread_slug = thread_slug;
        }

        private String thread_slug;

        public VoteModel( final String nickname,  final Integer voice, String thread_slug) {
            this.nickname = nickname;
            this.voice = voice;
            this.thread_slug = thread_slug;
        }

        public VoteModel(){
            this.voice = 0;
        }

        public final String getNickname() {
            return this.nickname;
        }

        public void setNickname(final String nickname) {
            this.nickname = nickname;
        }

        public final Integer getVoice() {
            return this.voice;
        }

        public void setVoice(final Integer voice) {
            this.voice = voice;
        }

        @Override
        public String toString() {
            return "VoteModel{" +
                    "nickname='" + nickname + '\'' +
                    ", voice=" + voice +
                    ", thread_slug='" + thread_slug + '\'' +
                    '}';
        }
    }

    public UservotesTableController(final JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private JdbcTemplate jdbc;

    public void insert(VoteModel vote){
        jdbc.update("INSERT INTO uservotes (nickname, voice, thread_slug) VALUES (?,?,?)",
                vote.getNickname(),vote.getVoice(),vote.getThread_slug());
        System.out.println(vote);
    }

    public enum status {NOTHING, SETVOICE, CHANGE};

    public status update(VoteModel vote){
        List<VoteModel> votes = jdbc.query("SELECT * FROM  uservotes " +
                            "WHERE LOWER(nickname) = LOWER(?) AND LOWER(thread_slug) = LOWER(?)",
                    new Object[]{vote.getNickname(), vote.getThread_slug()},
                    (resultSet, i) ->
                            new VoteModel(resultSet.getString("nickname"),
                                    resultSet.getInt("voice"),
                                    resultSet.getString("thread_slug"))
        );

        System.out.println(votes.toString());

        if(votes.isEmpty()){
            this.insert(vote);
            return status.SETVOICE;
        } else {
            Integer tmp_voice = votes.get(0).getVoice();
            Integer vote_voice = vote.getVoice();

            if(tmp_voice == vote_voice){
                return status.NOTHING;
            } else {
                jdbc.update("UPDATE uservotes SET voice = ?" +
                        " WHERE LOWER(nickname) = LOWER(?) AND LOWER(thread_slug) = LOWER(?)",
                        new Object[]{vote.getVoice(),vote.getNickname(),vote.getThread_slug()});
                return status.CHANGE;
            }

        }
    }
}
