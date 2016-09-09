alter table RequestInfo add column priority int4;
ALTER TABLE ProcessInstanceLog ADD COLUMN processType int4;

update ProcessInstanceLog set processType = 1;
update RequestInfo set priority = 5;