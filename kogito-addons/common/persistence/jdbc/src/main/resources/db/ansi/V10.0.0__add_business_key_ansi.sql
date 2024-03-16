CREATE TABLE business_key_mapping (
     business_key VARCHAR (255) NOT NULL,
     process_instance_id VARCHAR (36) NOT NULL,
     CONSTRAINT business_key_primary_key PRIMARY KEY (business_key),
     CONSTRAINT fk_process_instances 
     FOREIGN KEY (process_instance_id)
     REFERENCES process_instances(id)
     ON DELETE CASCADE
);

CREATE INDEX idx_business_key_process_instance_id ON business_key_mapping (process_instance_id);
