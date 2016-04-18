alter table RequestInfo add column priority int;
update RequestInfo set priority = 5;