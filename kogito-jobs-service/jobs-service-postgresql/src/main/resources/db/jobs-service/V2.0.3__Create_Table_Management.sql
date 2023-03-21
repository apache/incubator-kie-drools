CREATE TABLE job_service_management
(
  id VARCHAR(40) PRIMARY KEY,
  last_heartbeat TIMESTAMPTZ,
  token VARCHAR(40) UNIQUE
);