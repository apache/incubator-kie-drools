alter table ProcessInstanceLog add correlationKey varchar(255);
alter table TaskEvent add column message varchar(255);

alter table AuditTaskImpl add column workItemId bigint;
update AuditTaskImpl a set workItemId = (select workItemId from Task where id = a.taskId);

create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);