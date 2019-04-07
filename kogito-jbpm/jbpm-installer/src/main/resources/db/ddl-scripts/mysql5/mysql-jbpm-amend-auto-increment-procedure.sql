DELIMITER //
CREATE PROCEDURE JbpmAmendAutoIncrement()
BEGIN
      SET @max1 = (SELECT MAX(processInstanceId) + 1 FROM ProcessInstanceLog);
      set @alter_statement1 = concat('ALTER TABLE ProcessInstanceInfo AUTO_INCREMENT = ', @max1);
      PREPARE stmt1 FROM @alter_statement1;
      EXECUTE stmt1;
      DEALLOCATE PREPARE stmt1;

      SET @max2 = (SELECT MAX(workItemId) + 1 FROM NodeInstanceLog);
      set @alter_statement2 = concat('ALTER TABLE WorkItemInfo AUTO_INCREMENT = ', @max2);
      PREPARE stmt2 FROM @alter_statement2;
      EXECUTE stmt2;
      DEALLOCATE PREPARE stmt2;

      SET @max3 = (SELECT MAX(taskId) + 1 FROM AuditTaskImpl);
      set @alter_statement3 = concat('ALTER TABLE Task AUTO_INCREMENT = ', @max3);
      PREPARE stmt3 FROM @alter_statement3;
      EXECUTE stmt3;
      DEALLOCATE PREPARE stmt3;
END
//
DELIMITER ;

