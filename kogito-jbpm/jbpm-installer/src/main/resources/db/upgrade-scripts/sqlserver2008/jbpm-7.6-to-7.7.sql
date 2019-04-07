ALTER TABLE ProcessInstanceLog ADD sla_due_date datetime2;
ALTER TABLE ProcessInstanceLog ADD slaCompliance int; 
ALTER TABLE NodeInstanceLog ADD sla_due_date datetime2;
ALTER TABLE NodeInstanceLog ADD slaCompliance int;     
