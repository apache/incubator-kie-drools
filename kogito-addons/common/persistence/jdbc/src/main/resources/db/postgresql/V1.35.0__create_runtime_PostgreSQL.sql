-- To be used with kie-addons-quarkus-persistence-jdbc for Quarkus or kie-addons-springboot-persistence-jdbc for SpringBoot
CREATE TABLE process_instances
(
    id              character(36)     NOT NULL,
    payload         bytea             NOT NULL,
    process_id      character varying NOT NULL,
    version         bigint,
    process_version character varying,
    CONSTRAINT process_instances_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_process_instances_process_id ON process_instances (process_id, id, process_version);

CREATE TABLE correlation_instances
(
    id                     character(36)         NOT NULL,
    encoded_correlation_id character varying(36) NOT NULL UNIQUE,
    correlated_id          character varying(36) NOT NULL,
    correlation            json                  NOT NULL,
    version                bigint,
    CONSTRAINT correlation_instances_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_correlation_instances_encoded_id ON correlation_instances (encoded_correlation_id);
CREATE INDEX idx_correlation_instances_correlated_id ON correlation_instances (correlated_id);
