ALTER TABLE ProcessInstanceLog ADD correlationKey varchar(255);
ALTER TABLE TaskEvent ADD message varchar(255); 

ALTER TABLE AuditTaskImpl ADD workItemId bigint;
UPDATE AuditTaskImpl a SET workItemId = (SELECT workItemId FROM Task WHERE id = a.taskId);

create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);