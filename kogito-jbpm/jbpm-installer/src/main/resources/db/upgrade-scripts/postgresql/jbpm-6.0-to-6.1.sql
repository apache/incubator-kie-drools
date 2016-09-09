-- update context mapping info table with owner id (deployment id) for per process instance strategies
alter table ContextMappingInfo add column OWNER_ID varchar(255);
update ContextMappingInfo set OWNER_ID = (select externalId from ProcessInstanceLog where processInstanceId = cast(CONTEXT_ID as int8));

create table AuditTaskImpl (
        id int8 not null,
        activationTime date,
        actualOwner varchar(255),
        createdBy varchar(255),
        createdOn date,
        deploymentId varchar(255),
        description varchar(255),
        dueDate date,
        name varchar(255),
        parentId int8 not null,
        priority int4 not null,
        processId varchar(255),
        processInstanceId int8 not null,
        processSessionId int4 not null,
        status varchar(255),
        taskId int8,
        primary key (id));

create sequence AUDIT_ID_SEQ;