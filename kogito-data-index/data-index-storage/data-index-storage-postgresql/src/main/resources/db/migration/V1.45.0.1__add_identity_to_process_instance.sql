ALTER TABLE IF exists processes
    ADD COLUMN IF NOT EXISTS created_by character varying,
    ADD COLUMN IF NOT EXISTS updated_by character varying;