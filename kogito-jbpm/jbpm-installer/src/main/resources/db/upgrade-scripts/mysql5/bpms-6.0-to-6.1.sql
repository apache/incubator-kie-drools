-- update context mapping info table with owner id (deployment id) for per process instance strategies
alter table ContextMappingInfo add column OWNER_ID varchar(255);
update ContextMappingInfo set OWNER_ID = (select externalId from ProcessInstanceLog where processInstanceId = cast(CONTEXT_ID as DECIMAL(19,0)));

create table AuditTaskImpl (
        id bigint not null auto_increment,
        activationTime date,
        actualOwner varchar(255),
        createdBy varchar(255),
        createdOn date,
        deploymentId varchar(255),
        description varchar(255),
        dueDate date,
        name varchar(255),
        parentId bigint not null,
        priority integer not null,
        processId varchar(255),
        processInstanceId bigint not null,
        processSessionId integer not null,
        status varchar(255),
        taskId bigint,
        primary key (id));

ALTER TABLE SessionInfo MODIFY id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE AuditTaskImpl MODIFY processSessionId BIGINT;
ALTER TABLE AuditTaskImpl MODIFY activationTime DATETIME;
ALTER TABLE AuditTaskImpl MODIFY createdOn DATETIME;
ALTER TABLE AuditTaskImpl MODIFY dueDate DATETIME;
ALTER TABLE ContextMappingInfo MODIFY KSESSION_ID BIGINT;
ALTER TABLE Task MODIFY processSessionId BIGINT;

CREATE TABLE DeploymentStore (
    id bigint NOT NULL AUTO_INCREMENT,
    attributes VARCHAR(255),
    DEPLOYMENT_ID VARCHAR(255),
    deploymentUnit LONGTEXT,
    state INTEGER,
    updateDate DATETIME,
    PRIMARY KEY (id)
);

ALTER TABLE DeploymentStore ADD CONSTRAINT UK_DeploymentStore_1 UNIQUE (DEPLOYMENT_ID);

ALTER TABLE ProcessInstanceLog ADD COLUMN processInstanceDescription VARCHAR(255);
ALTER TABLE RequestInfo ADD COLUMN owner VARCHAR(255);
ALTER TABLE Task ADD COLUMN description VARCHAR(255);
ALTER TABLE Task ADD COLUMN name VARCHAR(255);
ALTER TABLE Task ADD COLUMN subject VARCHAR(255);

-- update all tasks with its name, subject and description
UPDATE Task t SET name = (SELECT shortText FROM I18NText WHERE Task_Names_Id = t.id);
UPDATE Task t SET subject = (SELECT shortText FROM I18NText WHERE Task_Subjects_Id = t.id);
UPDATE Task t SET description = (SELECT shortText FROM I18NText WHERE Task_Descriptions_Id = t.id);
        
INSERT INTO AuditTaskImpl (activationTime, actualOwner, createdBy, createdOn, deploymentId, description, dueDate, name, parentId, priority, processId, processInstanceId, processSessionId, status, taskId)
SELECT activationTime, actualOwner_id, createdBy_id, createdOn, deploymentId, description, expirationTime, name, parentId, priority,processId, processInstanceId, processSessionId, status, id 
FROM Task;

ALTER TABLE TaskEvent ADD COLUMN workItemId bigint;
ALTER TABLE TaskEvent ADD COLUMN processInstanceId bigint;
UPDATE TaskEvent t SET workItemId = (SELECT workItemId FROM Task WHERE id = t.taskId);
UPDATE TaskEvent t SET processInstanceId = (SELECT processInstanceId FROM Task WHERE id = t.taskId);