CREATE TABLE IF NOT EXISTS students (
    id                          BIGSERIAL PRIMARY KEY,
    student_firstname           VARCHAR(30)  NOT NULL,
    student_lastname            VARCHAR(30)  NOT NULL,
    username                    VARCHAR(20)  NOT NULL UNIQUE,
    student_email               VARCHAR(255) NOT NULL UNIQUE,
    password                    VARCHAR(255) NOT NULL,
    bio                         VARCHAR(160),
    profile_photo_url           VARCHAR(255),
    department                  VARCHAR(50)  NOT NULL,
    role                        VARCHAR(20)  NOT NULL DEFAULT 'STUDENT',
    is_email_verified           BOOLEAN      NOT NULL DEFAULT FALSE,
    email_verification_token    VARCHAR(255),
    is_deleted                  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at                  TIMESTAMP    NOT NULL,
    updated_at                  TIMESTAMP    NOT NULL,

    CONSTRAINT uk_student_email    UNIQUE (student_email),
    CONSTRAINT uk_student_username UNIQUE (username)
    );

CREATE TABLE IF NOT EXISTS posts (
    id             BIGSERIAL PRIMARY KEY,
    content        VARCHAR(500) NOT NULL,
    tag            VARCHAR(20)  NOT NULL DEFAULT 'SOSYAL',
    like_count     INT          NOT NULL DEFAULT 0,
    comment_count  INT          NOT NULL DEFAULT 0,
    student_id     BIGINT       NOT NULL REFERENCES students(id),
    is_deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL
    );

CREATE TABLE IF NOT EXISTS post_images (
    id           BIGSERIAL PRIMARY KEY,
    image_url    VARCHAR(255) NOT NULL,
    image_order  INT          NOT NULL,
    post_id      BIGINT       NOT NULL REFERENCES posts(id),
    is_deleted   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL
    );

CREATE TABLE IF NOT EXISTS comments (
    id                BIGSERIAL PRIMARY KEY,
    content           VARCHAR(300) NOT NULL,
    like_count        INT          NOT NULL DEFAULT 0,
    reply_count       INT          NOT NULL DEFAULT 0,
    post_id           BIGINT       NOT NULL REFERENCES posts(id),
    student_id        BIGINT       NOT NULL REFERENCES students(id),
    parent_comment_id BIGINT       REFERENCES comments(id),
    is_deleted        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP    NOT NULL,
    updated_at        TIMESTAMP    NOT NULL
    );

CREATE TABLE IF NOT EXISTS likes (
    id          BIGSERIAL PRIMARY KEY,
    student_id  BIGINT NOT NULL REFERENCES students(id),
    post_id     BIGINT REFERENCES posts(id),
    comment_id  BIGINT REFERENCES comments(id),
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL,

    CONSTRAINT uk_like_student_post    UNIQUE (student_id, post_id),
    CONSTRAINT uk_like_student_comment UNIQUE (student_id, comment_id)
    );

CREATE INDEX idx_student_email    ON students(student_email);
CREATE INDEX idx_student_username ON students(username);
CREATE INDEX idx_post_student_id  ON posts(student_id);
CREATE INDEX idx_post_tag         ON posts(tag);
CREATE INDEX idx_comment_post_id  ON comments(post_id);
CREATE INDEX idx_comment_student_id ON comments(student_id);
CREATE INDEX idx_like_student_id  ON likes(student_id);
CREATE INDEX idx_like_post_id     ON likes(post_id);
CREATE INDEX idx_like_comment_id  ON likes(comment_id);