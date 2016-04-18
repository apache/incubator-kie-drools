alter table RequestInfo add column priority integer;
update RequestInfo set priority = 5;