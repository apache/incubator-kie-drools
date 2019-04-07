-- If exist drop the procedure 'alter_table_session_info'
IF EXISTS (SELECT * FROM sys.objects WHERE type ='P' AND name = 'alter_table_session_info')
DROP PROCEDURE alter_table_session_info
-- Creating the procedure to delete the constraint from SessionInfo table
GO
CREATE PROCEDURE alter_table_session_info
AS
	DECLARE @const_name VARCHAR(255)
	DECLARE @sqlDroppingConstraint VARCHAR(255)
	DECLARE @sqlAlterTableSessionInfo VARCHAR(255)
	DECLARE @sqlRecriateConstraint VARCHAR(255)

	SELECT @const_name = (SELECT CONSTRAINT_NAME from INFORMATION_SCHEMA.TABLE_CONSTRAINTS where TABLE_NAME='SessionInfo')
	BEGIN
		SELECT @sqlDroppingConstraint = 'ALTER TABLE SessionInfo DROP CONSTRAINT ' + @const_name
		EXEC (@sqlDroppingConstraint)

		SELECT @sqlAlterTableSessionInfo = 'ALTER TABLE SessionInfo ALTER COLUMN id bigint'
		EXEC (@sqlAlterTableSessionInfo)

		SELECT @sqlRecriateConstraint = 'ALTER TABLE SessionInfo ADD CONSTRAINT ' + @const_name + ' PRIMARY KEY CLUSTERED ([id] ASC)'
		EXEC (@sqlRecriateConstraint)
	END
GO
-- Executing the procedure
EXECUTE alter_table_session_info;
-- Deleting the procedure to clean it from database
DROP PROCEDURE alter_table_session_info;
-- Recriating the Index
ALTER INDEX ALL ON SessionInfo REORGANIZE;

ALTER TABLE AuditTaskImpl ALTER COLUMN processSessionId bigint;
ALTER TABLE AuditTaskImpl ALTER COLUMN activationTime datetime;
ALTER TABLE AuditTaskImpl ALTER COLUMN createdOn datetime;
ALTER TABLE AuditTaskImpl ALTER COLUMN dueDate datetime;
ALTER TABLE ContextMappingInfo ALTER COLUMN KSESSION_ID bigint;
ALTER TABLE Task ALTER COLUMN processSessionId bigint;

CREATE TABLE DeploymentStore (
    id bigint identity not null,
    attributes varchar(255),
    DEPLOYMENT_ID varchar(255),
    deploymentUnit varchar(MAX),
    state int,
    updateDate datetime2,
    PRIMARY KEY (id)
);

ALTER TABLE DeploymentStore ADD CONSTRAINT UK_DeploymentStore_1 UNIQUE (DEPLOYMENT_ID);

ALTER TABLE ProcessInstanceLog ADD processInstanceDescription varchar(255);
ALTER TABLE RequestInfo ADD owner varchar(255);
ALTER TABLE Task ADD description varchar(255);
ALTER TABLE Task ADD name varchar(255);
ALTER TABLE Task ADD subject varchar(255);

-- update all tasks with its name, subject and description
UPDATE Task SET name = (SELECT shortText FROM I18NText WHERE I18NText.Task_Names_Id = Task.id);
UPDATE Task SET subject = (SELECT shortText FROM I18NText WHERE I18NText.Task_Subjects_Id = Task.id);
UPDATE Task SET description = (SELECT shortText FROM I18NText WHERE I18NText.Task_Descriptions_Id = Task.id);

INSERT INTO AuditTaskImpl (activationTime, actualOwner, createdBy, createdOn, deploymentId, description, dueDate, name, parentId, priority, processId, processInstanceId, processSessionId, status, taskId)
SELECT activationTime, actualOwner_id, createdBy_id, createdOn, deploymentId, description, expirationTime, name, parentId, priority,processId, processInstanceId, processSessionId, status, id
FROM Task;

ALTER TABLE TaskEvent ADD workItemId bigint;
ALTER TABLE TaskEvent ADD processInstanceId bigint;
UPDATE TaskEvent SET workItemId = (SELECT workItemId FROM Task WHERE Task.id = TaskEvent.taskId);
UPDATE TaskEvent SET processInstanceId = (SELECT processInstanceId FROM Task WHERE Task.id = TaskEvent.taskId);
