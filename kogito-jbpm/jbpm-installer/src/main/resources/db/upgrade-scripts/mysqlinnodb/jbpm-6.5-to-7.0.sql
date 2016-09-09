alter table RequestInfo add column priority integer;
alter table ProcessInstanceLog add column processType integer;

update ProcessInstanceLog set processType = 1;
update RequestInfo set priority = 5;