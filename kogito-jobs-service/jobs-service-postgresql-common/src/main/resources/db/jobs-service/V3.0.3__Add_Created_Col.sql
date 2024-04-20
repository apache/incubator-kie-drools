ALTER TABLE job_details
    ADD COLUMN created TIMESTAMPTZ;

UPDATE job_details
SET created = last_update
WHERE created is null;

CREATE INDEX job_details_created_idx
    ON job_details (created);
