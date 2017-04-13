alter table RequestInfo add priority number(10,0);
alter table ProcessInstanceLog add processType number(10,0);

update ProcessInstanceLog set processType = 1;
update RequestInfo set priority = 5;

create table CaseIdInfo (
    id number(19,0) not null,
    caseIdPrefix varchar2(255 char),
    currentValue number(19,0),
    primary key (id)
);

create table CaseRoleAssignmentLog (
    id number(19,0) not null,
    caseId varchar2(255 char),
    entityId varchar2(255 char),
    processInstanceId number(19,0) not null,
    roleName varchar2(255 char),
    type number(10,0) not null,
    primary key (id)
);

alter table CaseIdInfo 
    add constraint UK_CaseIdInfo_1 unique (caseIdPrefix);

create sequence CASE_ID_INFO_ID_SEQ;

create sequence CASE_ROLE_ASSIGN_LOG_ID_SEQ;

ALTER TABLE NodeInstanceLog ADD COLUMN referenceId number(19,0);
ALTER TABLE NodeInstanceLog ADD COLUMN nodeContainerId varchar2(255 char);

ALTER TABLE RequestInfo ADD COLUMN processInstanceId number(19,0);

ALTER TABLE AuditTaskImpl ADD COLUMN lastModificationDate timestamp;
update AuditTaskImpl ati set lastModificationDate = (
    select max(logTime) from TaskEvent where taskId=ati.taskId group by taskId
);

create table CaseFileDataLog (
    id number(19,0) not null,
    caseDefId varchar2(255 char),
    caseId varchar2(255 char),
    itemName varchar2(255 char),
    itemType varchar2(255 char),
    itemValue varchar2(255 char),
    lastModified timestamp,
    lastModifiedBy varchar2(255 char),
    primary key (id)
);

create sequence CASE_FILE_DATA_LOG_ID_SEQ;
