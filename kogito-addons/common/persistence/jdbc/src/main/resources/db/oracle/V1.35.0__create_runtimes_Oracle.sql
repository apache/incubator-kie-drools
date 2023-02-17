CREATE TABLE process_instances
(
    id              char(36) NOT NULL,
    payload         blob     NOT NULL,
    process_id      varchar2(3000) NOT NULL,
    version         number(19),
    process_version varchar2(3000),
    CONSTRAINT process_instances_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_process_instances_proc_id ON process_instances (process_id, id, process_version);
