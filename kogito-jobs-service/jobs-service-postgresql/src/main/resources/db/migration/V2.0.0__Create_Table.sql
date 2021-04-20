CREATE TYPE JOB_STATUS AS ENUM
(
  'ERROR',
  'EXECUTED',
  'SCHEDULED',
  'RETRY',
  'CANCELED'
);

CREATE TYPE JOB_TYPE AS ENUM
(
  'HTTP'
);

CREATE TABLE job_details
(
  id VARCHAR(40) PRIMARY KEY,
  correlation_id VARCHAR(40),
  status JOB_STATUS,
  last_update TIMESTAMPTZ,
  retries INT4,
  execution_counter INT4,
  scheduled_id VARCHAR(40),
  payload JSONB,
  type JOB_TYPE,
  priority INT4,
  recipient JSONB,
  trigger JSONB
);

CREATE INDEX status_date ON job_details
(
  status,
  ((trigger->>'nextFireTime')::INT8)
);