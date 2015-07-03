--db2

alter table ProcessInstanceLog add correlationKey varchar(255); 
alter table TaskEvent add column message varchar(255); 

create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);


--derby
alter table ProcessInstanceLog add column correlationKey varchar(255); 
alter table TaskEvent add column message varchar(255); 

create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);
-- h2

ALTER TABLE ProcessInstanceLog ADD correlationKey varchar(255);
ALTER TABLE TaskEvent ADD message varchar(255); 

create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);
-- hsqldb
      
ALTER TABLE ProcessInstanceLog ADD COLUMN correlationKey VARCHAR(255);
ALTER TABLE TaskEvent ADD COLUMN message VARCHAR(255); 

create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);
-- mysql

ALTER TABLE ProcessInstanceLog ADD COLUMN correlationKey VARCHAR(255);
ALTER TABLE TaskEvent ADD COLUMN message varchar(255); 

create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);
-- oracle

alter table ProcessInstanceLog add correlationKey varchar2(255 char);
alter table TaskEvent add message varchar2(255 char); 
        
create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);
-- postgres

ALTER TABLE ProcessInstanceLog ADD COLUMN correlationKey varchar(255);
ALTER TABLE TaskEvent ADD COLUMN message varchar(255); 

create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);
-- sql server

ALTER TABLE ProcessInstanceLog ADD correlationKey varchar(255);
ALTER TABLE TaskEvent ADD message varchar(255); 

create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);

-- sybase
ALTER TABLE ProcessInstanceLog ADD correlationKey VARCHAR(255);
ALTER TABLE TaskEvent ADD message VARCHAR(255);

CREATE INDEX IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);