alter table ProcessInstanceLog add column slaCompliance integer;
alter table ProcessInstanceLog add column sla_due_date datetime;
ALTER TABLE NodeInstanceLog ADD COLUMN slaCompliance integer;
ALTER TABLE NodeInstanceLog ADD COLUMN sla_due_date datetime; 
