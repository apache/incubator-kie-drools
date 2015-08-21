-- oracle

alter table ProcessInstanceLog add correlationKey varchar2(255 char);
alter table TaskEvent add message varchar2(255 char); 
        
create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);