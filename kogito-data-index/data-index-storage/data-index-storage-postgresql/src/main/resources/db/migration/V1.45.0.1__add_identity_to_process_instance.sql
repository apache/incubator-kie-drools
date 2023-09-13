ALTER TABLE processes
    ADD COLUMN created_by character varying,
    ADD COLUMN updated_by character varying;