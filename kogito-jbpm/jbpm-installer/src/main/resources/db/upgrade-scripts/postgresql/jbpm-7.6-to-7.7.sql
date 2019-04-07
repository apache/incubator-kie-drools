ALTER TABLE ProcessInstanceLog ADD COLUMN sla_due_date timestamp;
ALTER TABLE ProcessInstanceLog ADD COLUMN slaCompliance int4;
ALTER TABLE NodeInstanceLog ADD COLUMN sla_due_date timestamp;
ALTER TABLE NodeInstanceLog ADD COLUMN slaCompliance int4;
