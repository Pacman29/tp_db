DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS threads CASCADE;
DROP TABLE IF EXISTS forums CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS forum_users CASCADE;
DROP TABLE IF EXISTS votes CASCADE;

DROP FUNCTION IF EXISTS thread_insert( CITEXT, TIMESTAMPTZ, CITEXT, TEXT, CITEXT, TEXT );
DROP FUNCTION IF EXISTS update_or_insert_votes( INTEGER, INTEGER, INTEGER );

CREATE EXTENSION IF NOT EXISTS CITEXT;

SET SYNCHRONOUS_COMMIT = 'off';

CREATE TABLE IF NOT EXISTS users (
  id       SERIAL PRIMARY KEY,
  about    TEXT DEFAULT NULL,
  email    CITEXT UNIQUE,
  fullname TEXT DEFAULT NULL,
  nickname CITEXT COLLATE ucs_basic UNIQUE
);

CREATE TABLE IF NOT EXISTS forums (
  id      SERIAL PRIMARY KEY,
  user_id INTEGER REFERENCES users (id) ON DELETE CASCADE NOT NULL,
  posts   INTEGER DEFAULT 0,
  threads INTEGER DEFAULT 0,
  slug    CITEXT UNIQUE                                   NOT NULL,
  title   TEXT                                            NOT NULL
);

CREATE TABLE IF NOT EXISTS threads (
  user_id  INTEGER REFERENCES users (id) ON DELETE CASCADE  NOT NULL,
  created  TIMESTAMPTZ DEFAULT NOW(),
  forum_id INTEGER REFERENCES forums (id) ON DELETE CASCADE NOT NULL,
  id       SERIAL PRIMARY KEY,
  message  TEXT        DEFAULT NULL,
  slug     CITEXT UNIQUE,
  title    TEXT                                             NOT NULL,
  votes    INTEGER     DEFAULT 0
);

CREATE TABLE IF NOT EXISTS posts (
  user_id   INTEGER REFERENCES users (id) ON DELETE CASCADE   NOT NULL,
  created   TIMESTAMPTZ DEFAULT NOW(),
  forum_id  INTEGER REFERENCES forums (id) ON DELETE CASCADE  NOT NULL,
  id        SERIAL PRIMARY KEY,
  is_edited BOOLEAN     DEFAULT FALSE,
  message   TEXT        DEFAULT NULL,
  parent    INTEGER     DEFAULT 0,
  thread_id INTEGER REFERENCES threads (id) ON DELETE CASCADE NOT NULL,
  path      INTEGER []                                        NOT NULL
);

CREATE INDEX IF NOT EXISTS posts_user_id_idx
  ON posts (user_id);
CREATE INDEX IF NOT EXISTS posts_forum_id_idx
  ON posts (forum_id);
CREATE INDEX IF NOT EXISTS posts_flat_idx
  ON posts (thread_id, created, id);
CREATE INDEX IF NOT EXISTS posts_path_thread_id_idx
  ON posts (thread_id, path);

CREATE TABLE IF NOT EXISTS forum_users (
  user_id  INTEGER REFERENCES users (id) ON DELETE CASCADE,
  forum_id INTEGER REFERENCES forums (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS votes (
  user_id   INTEGER REFERENCES users (id) ON DELETE CASCADE,
  thread_id INTEGER REFERENCES threads (id) ON DELETE CASCADE,
  voice     INTEGER DEFAULT 0,
  CONSTRAINT user_thread_unique_pair UNIQUE (user_id, thread_id)
);

CREATE OR REPLACE FUNCTION on_thread_insert()
  RETURNS TRIGGER AS '
BEGIN
  UPDATE forums
  SET threads = threads + 1
  WHERE id = NEW.forum_id;
  --
  INSERT INTO forum_users (user_id, forum_id) VALUES (NEW.user_id, NEW.forum_id);
  --
  RETURN NEW;
END;
' LANGUAGE plpgsql;

CREATE TRIGGER thread_insert_trigger
AFTER INSERT ON threads
FOR EACH ROW EXECUTE PROCEDURE on_thread_insert();

CREATE OR REPLACE FUNCTION update_or_insert_votes(vote_user_id INTEGER, vote_thread_it INTEGER, vote_value INTEGER)
  RETURNS VOID AS '
BEGIN
  INSERT INTO votes (user_id, thread_id, voice) VALUES (vote_user_id, vote_thread_it, vote_value)
  ON CONFLICT (user_id, thread_id)
    DO UPDATE SET voice = vote_value;
  UPDATE threads
  SET votes = (SELECT SUM(voice)
               FROM votes
               WHERE thread_id = vote_thread_it)
  WHERE id = vote_thread_it;
END;
' LANGUAGE plpgsql