alter table RequestInfo add column priority number(10,0);
alter table ProcessInstanceLog add processType number(10,0);

update ProcessInstanceLog set processType = 1;
update RequestInfo set priority = 5;