alter table ProcessInstanceLog add correlationKey varchar2(255 char);
alter table TaskEvent add message varchar2(255 char);

alter table AuditTaskImpl add workItemId number(19,0);
update AuditTaskImpl a set workItemId = (select workItemId from Task where id = a.taskId);
        
create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);