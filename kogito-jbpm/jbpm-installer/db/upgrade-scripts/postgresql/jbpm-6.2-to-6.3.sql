-- postgres

ALTER TABLE ProcessInstanceLog ADD COLUMN correlationKey varchar(255);
ALTER TABLE TaskEvent ADD COLUMN message varchar(255); 

create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);