alter table RequestInfo add column priority integer;
ALTER TABLE ProcessInstanceLog ADD COLUMN processType integer;

update ProcessInstanceLog set processType = 1;
update RequestInfo set priority = 5;