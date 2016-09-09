-- update context mapping info table with owner id (deployment id) for per process instance strategies
alter table ContextMappingInfo add OWNER_ID varchar2(255 char);
update ContextMappingInfo set OWNER_ID = (select externalId from ProcessInstanceLog where processInstanceId = cast(CONTEXT_ID as number(19,0)));

create table AuditTaskImpl (
        id number(19,0) not null,
        activationTime date,
        actualOwner varchar2(255 char),
        createdBy varchar2(255 char),
        createdOn date,
        deploymentId varchar2(255 char),
        description varchar2(255 char),
        dueDate date,
        name varchar2(255 char),
        parentId number(19,0) not null,
        priority number(10,0) not null,
        processId varchar2(255 char),
        processInstanceId number(19,0) not null,
        processSessionId number(10,0) not null,
        status varchar2(255 char),
        taskId number(19,0),
        primary key (id));

create sequence AUDIT_ID_SEQ;

alter table SessionInfo modify ( id number(19,0) );
alter table AuditTaskImpl modify ( processSessionId number(19,0) );
alter table AuditTaskImpl modify ( activationTime timestamp );
alter table AuditTaskImpl modify ( createdOn timestamp );
alter table AuditTaskImpl modify ( dueDate timestamp );
alter table ContextMappingInfo modify ( KSESSION_ID number(19,0) );
alter table Task modify ( processSessionId number(19,0) );

create table DeploymentStore (
    id number(19,0) not null,
    attributes varchar2(255 char),
    DEPLOYMENT_ID varchar2(255 char),
    deploymentUnit clob,
    state number(10,0),
    updateDate timestamp,
    primary key (id)
);
alter table DeploymentStore add constraint UK_DeploymentStore_1 unique (DEPLOYMENT_ID);
create sequence DEPLOY_STORE_ID_SEQ;

alter table ProcessInstanceLog add processInstanceDescription varchar2(255 char);
alter table RequestInfo add owner varchar2(255 char);
alter table Task add (
	description varchar2(255 char),
	name varchar2(255 char),
	subject varchar2(255 char)
	);
	
-- update all tasks with its name, subject and description
update Task t set name = (select shortText from I18NText where Task_Names_Id = t.id);
update Task t set subject = (select shortText from I18NText where Task_Subjects_Id = t.id);
update Task t set description = (select shortText from I18NText where Task_Descriptions_Id = t.id);

INSERT INTO AuditTaskImpl (id, activationTime, actualOwner, createdBy, createdOn, deploymentId, description, dueDate, name, parentId, priority, processId, processInstanceId, processSessionId, status, taskId)
SELECT AUDIT_ID_SEQ.nextval, activationTime, actualOwner_id, createdBy_id, createdOn, deploymentId, description, expirationTime, name, parentId, priority,processId, processInstanceId, processSessionId, status, id
FROM Task;

alter table TaskEvent add workItemId number(19,0);
alter table TaskEvent add processInstanceId number(19,0);
update TaskEvent t set workItemId = (select workItemId from Task where id = t.taskId);
update TaskEvent t set processInstanceId = (select processInstanceId from Task where id = t.taskId);