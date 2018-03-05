ALTER TABLE ProcessInstanceLog ADD COLUMN slaCompliance integer;
ALTER TABLE ProcessInstanceLog ADD COLUMN sla_due_date timestamp;
ALTER TABLE NodeInstanceLog ADD COLUMN slaCompliance integer;
ALTER TABLE NodeInstanceLog ADD COLUMN sla_due_date timestamp;
