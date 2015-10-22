-- update context mapping info table with owner id (deployment id) for per process instance strategies
alter table ContextMappingInfo add OWNER_ID varchar(255);
update ContextMappingInfo set OWNER_ID = (select externalId from ProcessInstanceLog where processInstanceId = cast(CONTEXT_ID as numeric(19,0)));

create table AuditTaskImpl (
        id bigint identity not null,
        activationTime date,
        actualOwner varchar(255),
        createdBy varchar(255),
        createdOn date,
        deploymentId varchar(255),
        description varchar(255),
        dueDate date,
        name varchar(255),
        parentId bigint not null,
        priority int not null,
        processId varchar(255),
        processInstanceId bigint not null,
        processSessionId int not null,
        status varchar(255),
        taskId bigint,
        primary key (id));

ALTER TABLE SessionInfo ALTER COLUMN id numeric(19,0);
ALTER TABLE AuditTaskImpl ALTER COLUMN processSessionId numeric(19,0);
ALTER TABLE AuditTaskImpl ALTER COLUMN activationTime datetime;
ALTER TABLE AuditTaskImpl ALTER COLUMN createdOn datetime;
ALTER TABLE AuditTaskImpl ALTER COLUMN dueDate datetime;
ALTER TABLE ContextMappingInfo ALTER COLUMN KSESSION_ID numeric(19,0);
ALTER TABLE Task ALTER COLUMN processSessionId numeric(19,0);

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
UPDATE Task t SET name = (SELECT shortText FROM I18NText WHERE Task_Names_Id = t.id);
UPDATE Task t SET subject = (SELECT shortText FROM I18NText WHERE Task_Subjects_Id = t.id);
UPDATE Task t SET description = (SELECT shortText FROM I18NText WHERE Task_Descriptions_Id = t.id);

INSERT INTO AuditTaskImpl (activationTime, actualOwner, createdBy, createdOn, deploymentId, description, dueDate, name, parentId, priority, processId, processInstanceId, processSessionId, status, taskId)
SELECT activationTime, actualOwner_id, createdBy_id, createdOn, deploymentId, description, expirationTime, name, parentId, priority,processId, processInstanceId, processSessionId, status, id
FROM Task;

ALTER TABLE TaskEvent ADD workItemId numeric(19,0);
ALTER TABLE TaskEvent ADD processInstanceId numeric(19,0);
UPDATE TaskEvent t SET workItemId = (SELECT workItemId FROM Task WHERE id = t.taskId);
UPDATE TaskEvent t SET processInstanceId = (SELECT processInstanceId FROM Task WHERE id = t.taskId);