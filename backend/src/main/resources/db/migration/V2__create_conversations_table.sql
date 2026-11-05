CREATE TABLE conversations (
                               id BIGSERIAL PRIMARY KEY,
                               student_name VARCHAR(150) NOT NULL,
                               student_email VARCHAR(180),
                               channel VARCHAR(30) NOT NULL,
                               status VARCHAR(30) NOT NULL,
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                               updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

                               CONSTRAINT chk_conversation_channel
                                   CHECK (channel IN ('WEBSITE', 'WHATSAPP', 'INSTAGRAM')),

                               CONSTRAINT chk_conversation_status
                                   CHECK (status IN ('OPEN', 'IN_PROGRESS', 'CLOSED'))
);

CREATE INDEX idx_conversations_status
    ON conversations(status);

CREATE INDEX idx_conversations_created_at
    ON conversations(created_at);