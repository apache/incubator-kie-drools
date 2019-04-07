ALTER TABLE SessionInfo ALTER COLUMN id TYPE bigint;
ALTER TABLE AuditTaskImpl ALTER COLUMN processSessionId TYPE bigint;
ALTER TABLE AuditTaskImpl ALTER COLUMN activationTime TYPE timestamp;
ALTER TABLE AuditTaskImpl ALTER COLUMN createdOn TYPE timestamp;
ALTER TABLE AuditTaskImpl ALTER COLUMN dueDate TYPE timestamp;
ALTER TABLE ContextMappingInfo ALTER COLUMN KSESSION_ID TYPE bigint;
ALTER TABLE Task ALTER COLUMN processSessionId TYPE bigint;

CREATE TABLE DeploymentStore (
    id int8 not null,
    attributes varchar(255),
    DEPLOYMENT_ID varchar(255),
    deploymentUnit text,
    state int4,
    updateDate timestamp,
    PRIMARY KEY (id)
);

ALTER TABLE DeploymentStore ADD CONSTRAINT UK_DeploymentStore_1 UNIQUE (DEPLOYMENT_ID);
CREATE sequence DEPLOY_STORE_ID_SEQ;

ALTER TABLE ProcessInstanceLog ADD COLUMN processInstanceDescription varchar(255);
ALTER TABLE RequestInfo ADD COLUMN owner varchar(255);
ALTER TABLE Task ADD COLUMN description varchar(255);
ALTER TABLE Task ADD COLUMN name varchar(255);
ALTER TABLE Task ADD COLUMN subject varchar(255);

-- update all tasks with its name, subject and description
UPDATE Task t SET name = (SELECT shortText FROM I18NText WHERE Task_Names_Id = t.id);
UPDATE Task t SET subject = (SELECT shortText FROM I18NText WHERE Task_Subjects_Id = t.id);
UPDATE Task t SET description = (SELECT shortText FROM I18NText WHERE Task_Descriptions_Id = t.id);

INSERT INTO AuditTaskImpl (id, activationTime, actualOwner, createdBy, createdOn, deploymentId, description, dueDate, name, parentId, priority, processId, processInstanceId, processSessionId, status, taskId)
SELECT nextval('AUDIT_ID_SEQ'), activationTime, actualOwner_id, createdBy_id, createdOn, deploymentId, description, expirationTime, name, parentId, priority,processId, processInstanceId, processSessionId, status, id
FROM Task;

ALTER TABLE TaskEvent ADD COLUMN workItemId bigint;
ALTER TABLE TaskEvent ADD COLUMN processInstanceId bigint;
UPDATE TaskEvent t SET workItemId = (SELECT workItemId FROM Task WHERE id = t.taskId);
UPDATE TaskEvent t SET processInstanceId = (SELECT processInstanceId FROM Task WHERE id = t.taskId);