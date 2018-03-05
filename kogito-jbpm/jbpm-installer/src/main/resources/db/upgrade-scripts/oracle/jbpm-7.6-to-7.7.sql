alter table ProcessInstanceLog add sla_due_date timestamp;
alter table ProcessInstanceLog add slaCompliance number(10,0);
ALTER TABLE NodeInstanceLog ADD sla_due_date timestamp;
ALTER TABLE NodeInstanceLog ADD slaCompliance number(10,0);
