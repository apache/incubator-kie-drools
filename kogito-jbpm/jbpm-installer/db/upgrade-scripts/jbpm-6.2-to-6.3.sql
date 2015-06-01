--db2

alter table ProcessInstanceLog add correlationKey varchar(255); 
alter table TaskEvent add column message varchar(255); 


--derby
alter table ProcessInstanceLog add column correlationKey varchar(255); 
alter table TaskEvent add column message varchar(255); 

-- h2

ALTER TABLE ProcessInstanceLog ADD correlationKey varchar(255);
ALTER TABLE TaskEvent ADD message varchar(255); 


-- hsqldb
      
ALTER TABLE ProcessInstanceLog ADD COLUMN correlationKey VARCHAR(255);
ALTER TABLE TaskEvent ADD COLUMN message VARCHAR(255); 


-- mysql

ALTER TABLE ProcessInstanceLog ADD COLUMN correlationKey VARCHAR(255);
ALTER TABLE TaskEvent ADD COLUMN message varchar(255); 


-- oracle

alter table ProcessInstanceLog add correlationKey varchar2(255 char);
alter table TaskEvent add message varchar2(255 char); 
        
-- postgres

ALTER TABLE ProcessInstanceLog ADD COLUMN correlationKey varchar(255);
ALTER TABLE TaskEvent ADD COLUMN message varchar(255); 

--sql server

ALTER TABLE ProcessInstanceLog ADD correlationKey varchar(255);
ALTER TABLE TaskEvent ADD message varchar(255); 

