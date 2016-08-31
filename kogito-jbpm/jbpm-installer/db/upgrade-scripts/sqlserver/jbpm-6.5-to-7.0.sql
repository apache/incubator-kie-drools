alter table RequestInfo add priority int not null;
ALTER TABLE ProcessInstanceLog ADD processType int not null;

update ProcessInstanceLog set processType = 1;
update RequestInfo set priority = 5;