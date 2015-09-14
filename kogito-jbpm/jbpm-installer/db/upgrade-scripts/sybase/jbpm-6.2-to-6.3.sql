ALTER TABLE ProcessInstanceLog ADD correlationKey VARCHAR(255);
ALTER TABLE TaskEvent ADD message VARCHAR(255);

ALTER TABLE AuditTaskImpl ADD workItemId NUMERIC(19,0);
UPDATE AuditTaskImpl SET workItemId = (SELECT workItemId FROM Task WHERE id = audittaskimpl.taskId);

CREATE INDEX IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);