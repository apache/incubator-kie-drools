ALTER TABLE ProcessInstanceLog ADD correlationKey VARCHAR(255)
go
ALTER TABLE TaskEvent ADD message VARCHAR(255)
go

ALTER TABLE AuditTaskImpl ADD workItemId NUMERIC(19,0)
go
UPDATE AuditTaskImpl SET workItemId = (SELECT workItemId FROM Task WHERE id = audittaskimpl.taskId)
go

CREATE INDEX IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey)
go