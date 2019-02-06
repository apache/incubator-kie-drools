ALTER TABLE ProcessInstanceLog ADD sla_due_date datetime null
go
ALTER TABLE ProcessInstanceLog ADD slaCompliance int null
go    
ALTER TABLE NodeInstanceLog ADD sla_due_date datetime null
go
ALTER TABLE NodeInstanceLog ADD slaCompliance int null
go     
