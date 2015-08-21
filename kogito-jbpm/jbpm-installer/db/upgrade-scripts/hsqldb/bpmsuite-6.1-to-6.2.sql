-- hsqldb
      
ALTER TABLE ProcessInstanceLog ADD COLUMN correlationKey VARCHAR(255);
ALTER TABLE TaskEvent ADD COLUMN message VARCHAR(255); 

create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);