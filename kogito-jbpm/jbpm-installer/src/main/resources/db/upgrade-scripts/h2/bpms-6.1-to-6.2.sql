ALTER TABLE ProcessInstanceLog ADD correlationKey varchar(255);
ALTER TABLE TaskEvent ADD message varchar(255);

ALTER TABLE AuditTaskImpl ADD workItemId bigint;
UPDATE AuditTaskImpl a SET workItemId = (SELECT workItemId FROM Task WHERE id = a.taskId);

CREATE index IDX_PInstLog_correlation ON ProcessInstanceLog(correlationKey);