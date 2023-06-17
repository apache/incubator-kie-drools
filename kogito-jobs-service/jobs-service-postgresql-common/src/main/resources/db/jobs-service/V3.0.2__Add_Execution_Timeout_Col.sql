ALTER TABLE job_details
    ADD COLUMN execution_timeout BIGINT;

ALTER TABLE job_details
    ADD COLUMN execution_timeout_unit VARCHAR(40);
