CREATE TABLE classification_records (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    complaint_id    UUID NOT NULL REFERENCES complaints(id),
    category        VARCHAR(20) NOT NULL,
    urgency         VARCHAR(20) NOT NULL,
    suggested_team  VARCHAR(100) NOT NULL,
    reasoning       TEXT,
    prompt_version  VARCHAR(20) NOT NULL,
    classified_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_classification_complaint
    ON classification_records(complaint_id);

CREATE INDEX idx_classification_prompt_version
    ON classification_records(prompt_version);