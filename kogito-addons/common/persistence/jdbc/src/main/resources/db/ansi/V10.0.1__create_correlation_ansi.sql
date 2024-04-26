CREATE TABLE correlation_instances
(
    id                     character(36)         NOT NULL,
    encoded_correlation_id character varying(36) NOT NULL UNIQUE,
    correlated_id          character varying(36) NOT NULL,
    correlation            character varying(8000)     NOT NULL,
    version                bigint,
    CONSTRAINT correlation_instances_pkey PRIMARY KEY (id)
);