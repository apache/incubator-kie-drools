ALTER TABLE ProcessInstanceLog ADD sla_due_date datetime null;
ALTER TABLE ProcessInstanceLog ADD slaCompliance int null;    
ALTER TABLE NodeInstanceLog ADD sla_due_date datetime null;
ALTER TABLE NodeInstanceLog ADD slaCompliance int null;      
