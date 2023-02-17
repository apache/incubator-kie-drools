CREATE TABLE process_instances
(
    id              CHAR(36)      NOT NULL,
    payload         BLOB          NOT NULL,
    process_id      VARCHAR(4000) NOT NULL,
    version         BIGINT(19),
    process_version VARCHAR(4000),
    CONSTRAINT process_instances_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_process_instances_process_id ON process_instances (process_id, id, process_version);
