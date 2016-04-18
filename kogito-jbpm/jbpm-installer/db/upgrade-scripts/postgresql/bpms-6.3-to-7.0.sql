alter table RequestInfo add column priority int4;
update RequestInfo set priority = 5;