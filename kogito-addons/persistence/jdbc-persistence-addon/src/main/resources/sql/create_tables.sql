CREATE TABLE process_instances(id uuid NOT NULL,
                                      payload bytea NOT NULL,
                                      process_id character varying NOT NULL,
                                      version bigint,
                                      CONSTRAINT process_instances_pkey PRIMARY KEY (id)
                                      );
CREATE INDEX idx_process_instances_process_id ON process_instances
    (
     process_id
    );