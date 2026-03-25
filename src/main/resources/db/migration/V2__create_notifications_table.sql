CREATE TABLE notifications (

    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES students(id),
    sender_student_id BIGINT NOT NULL REFERENCES students(id),
    type VARCHAR(50) NOT NULL,
    post_id BIGINT,
    comment_id BIGINT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_student_id ON notifications(student_id);
CREATE INDEX idx_notifications_is_read ON notifications(student_id, is_read);