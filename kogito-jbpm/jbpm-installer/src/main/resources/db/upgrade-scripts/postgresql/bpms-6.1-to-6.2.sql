ALTER TABLE ProcessInstanceLog ADD COLUMN correlationKey varchar(255);
ALTER TABLE TaskEvent ADD COLUMN message varchar(255);

ALTER TABLE AuditTaskImpl ADD COLUMN workItemId int8;
UPDATE AuditTaskImpl a SET workItemId = (SELECT workItemId FROM Task WHERE id = a.taskId);

create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);