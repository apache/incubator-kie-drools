-- sybase
ALTER TABLE ProcessInstanceLog ADD correlationKey VARCHAR(255);
ALTER TABLE TaskEvent ADD message VARCHAR(255);

CREATE INDEX IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);