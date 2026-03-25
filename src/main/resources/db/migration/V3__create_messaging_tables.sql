CREATE TABLE IF NOT EXISTS conversations (
    id           BIGSERIAL PRIMARY KEY,
    student1_id  BIGINT      NOT NULL REFERENCES students(id),
    student2_id  BIGINT      NOT NULL REFERENCES students(id),
    is_accepted  BOOLEAN     NOT NULL DEFAULT FALSE,
    accepted_at  TIMESTAMP,
    expires_at   TIMESTAMP,
    created_at   TIMESTAMP   NOT NULL,

    CONSTRAINT uk_conversation UNIQUE (student1_id, student2_id)
    );

CREATE TABLE IF NOT EXISTS messages (
    id               BIGSERIAL PRIMARY KEY,
    conversation_id  BIGINT       NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_id        BIGINT       NOT NULL REFERENCES students(id),
    content          VARCHAR(1000) NOT NULL,
    is_read          BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP    NOT NULL
    );

CREATE INDEX idx_conversation_student1 ON conversations(student1_id);
CREATE INDEX idx_conversation_student2 ON conversations(student2_id);
CREATE INDEX idx_message_conversation  ON messages(conversation_id);
CREATE INDEX idx_message_sender        ON messages(sender_id);