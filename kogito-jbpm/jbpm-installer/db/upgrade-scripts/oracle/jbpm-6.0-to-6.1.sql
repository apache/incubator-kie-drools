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