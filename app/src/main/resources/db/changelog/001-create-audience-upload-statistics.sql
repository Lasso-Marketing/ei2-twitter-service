--liquibase formatted sql

--changeset romanr:EI2-278
CREATE TABLE audience_upload_statistics (
    trace_id VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    version INTEGER,
    matched_records INTEGER,
    total_records INTEGER,
    PRIMARY KEY (trace_id)
);