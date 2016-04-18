alter table RequestInfo add column priority number(10,0);
update RequestInfo set priority = 5;