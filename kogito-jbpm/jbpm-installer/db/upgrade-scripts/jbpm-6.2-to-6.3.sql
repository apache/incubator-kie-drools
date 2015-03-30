--db2

alter table ProcessInstanceLog add correlationKey varchar(255); 


--derby
alter table ProcessInstanceLog add column correlationKey varchar(255); 


-- h2

ALTER TABLE ProcessInstanceLog ADD correlationKey varchar(255);


-- hsqldb
      
ALTER TABLE ProcessInstanceLog ADD COLUMN correlationKey VARCHAR(255);


-- mysql

ALTER TABLE ProcessInstanceLog ADD COLUMN correlationKey VARCHAR(255);


-- oracle

alter table ProcessInstanceLog add correlationKey varchar2(255 char);

        
-- postgres

ALTER TABLE ProcessInstanceLog ADD COLUMN correlationKey varchar(255);


--sql server

ALTER TABLE ProcessInstanceLog ADD correlationKey varchar(255);

