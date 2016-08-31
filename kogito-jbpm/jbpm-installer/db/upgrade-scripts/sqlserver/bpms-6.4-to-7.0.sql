alter table RequestInfo add priority int null;
ALTER TABLE ProcessInstanceLog ADD processType int null;

update ProcessInstanceLog set processType = 1;
update RequestInfo set priority = 5;