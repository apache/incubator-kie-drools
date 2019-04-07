-- update context mapping info table with owner id (deployment id) for per process instance strategies
alter table ContextMappingInfo add OWNER_ID varchar(255);
update ContextMappingInfo set OWNER_ID = (select externalId from ProcessInstanceLog where processInstanceId = cast(CONTEXT_ID as numeric(19,0)));

create table AuditTaskImpl (
        id numeric(19,0) identity not null,
        activationTime datetime,
        actualOwner varchar(255),
        createdBy varchar(255),
        createdOn datetime,
        deploymentId varchar(255),
        description varchar(255),
        dueDate datetime,
        name varchar(255),
        parentId numeric(19,0) not null,
        priority int not null,
        processId varchar(255),
        processInstanceId numeric(19,0) not null,
        processSessionId int not null,
        status varchar(255),
        taskId numeric(19,0),
        primary key (id));