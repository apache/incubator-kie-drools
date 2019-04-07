-- update context mapping info table with owner id (deployment id) for per process instance strategies
alter table ContextMappingInfo add OWNER_ID varchar(255)
go
update ContextMappingInfo set OWNER_ID = (select externalId from ProcessInstanceLog where processInstanceId = cast(CONTEXT_ID as bigint))
go

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
        primary key (id))
go

ALTER TABLE SessionInfo MODIFY id NUMERIC(19,0)
go
ALTER TABLE AuditTaskImpl MODIFY processSessionId NUMERIC(19,0)
go
ALTER TABLE AuditTaskImpl MODIFY activationTime TIMESTAMP
go
ALTER TABLE AuditTaskImpl MODIFY createdOn TIMESTAMP
go
ALTER TABLE AuditTaskImpl MODIFY dueDate TIMESTAMP
go
ALTER TABLE ContextMappingInfo MODIFY KSESSION_ID NUMERIC(19,0)
go
ALTER TABLE Task MODIFY processSessionId NUMERIC(19,0)
go

CREATE TABLE DeploymentStore (
    id BIGINT NOT NULL,
    attributes VARCHAR(255),
    DEPLOYMENT_ID VARCHAR(255),
    deploymentUnit TEXT,
    state INTEGER,
    updateDate TIMESTAMP,
    PRIMARY KEY(id)
)
go

CREATE UNIQUE INDEX UK_DeploymentStore_1 on DeploymentStore(DEPLOYMENT_ID)
go

ALTER TABLE ProcessInstanceLog ADD processInstanceDescription VARCHAR(255)
go
ALTER TABLE RequestInfo ADD owner VARCHAR(255)
go
ALTER TABLE Task ADD description VARCHAR(255)
go
ALTER TABLE Task ADD name VARCHAR(255)
go
ALTER TABLE Task ADD subject VARCHAR(255)
go

-- update all tasks with its name, subject and description
UPDATE Task SET name = (SELECT shortText FROM I18NText WHERE Task_Names_Id = task.id)
go
UPDATE Task SET subject = (SELECT shortText FROM I18NText WHERE Task_Subjects_Id = task.id)
go
UPDATE Task SET description = (SELECT shortText FROM I18NText WHERE Task_Descriptions_Id = task.id)
go

INSERT INTO AuditTaskImpl (activationTime, actualOwner, createdBy, createdOn, deploymentId, description, dueDate, name, parentId, priority, processId, processInstanceId, processSessionId, status, taskId)
SELECT activationTime, actualOwner_id, createdBy_id, createdOn, deploymentId, description, expirationTime, name, parentId, priority,processId, processInstanceId, processSessionId, status, id
FROM Task
go

ALTER TABLE TaskEvent ADD workItemId NUMERIC(19,0)
go
ALTER TABLE TaskEvent ADD processInstanceId NUMERIC(19,0)
go
UPDATE TaskEvent t SET workItemId = (SELECT workItemId FROM Task WHERE id = t.taskId)
go
UPDATE TaskEvent t SET processInstanceId = (SELECT processInstanceId FROM Task WHERE id = t.taskId)
go