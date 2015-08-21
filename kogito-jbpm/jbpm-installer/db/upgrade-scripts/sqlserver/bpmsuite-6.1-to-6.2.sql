-- sql server

ALTER TABLE ProcessInstanceLog ADD correlationKey varchar(255);
ALTER TABLE TaskEvent ADD message varchar(255); 

create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);