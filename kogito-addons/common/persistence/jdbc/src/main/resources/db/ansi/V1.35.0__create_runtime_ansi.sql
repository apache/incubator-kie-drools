CREATE TABLE process_instances
(
    id              character(36)      NOT NULL,
    payload         varbinary(1000000) NOT NULL,
    process_id      character varying(4000) NOT NULL,
    version         bigint,
    process_version character varying(4000),
    CONSTRAINT process_instances_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_process_instances_process_id ON process_instances (process_id, id, process_version);
