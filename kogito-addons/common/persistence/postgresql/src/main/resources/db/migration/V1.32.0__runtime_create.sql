-- To be used with kogito-addons-quarkus-persistence-postgresql for Quarkus or kogito-addons-springboot-persistence-postgresql for SpringBoot
CREATE TABLE process_instances
(
    id              uuid              NOT NULL,
    payload         bytea             NOT NULL,
    process_id      character varying NOT NULL,
    version         bigint,
    process_version character varying,
    CONSTRAINT process_instances_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_process_instances_process_id ON process_instances (process_id, id, process_version);