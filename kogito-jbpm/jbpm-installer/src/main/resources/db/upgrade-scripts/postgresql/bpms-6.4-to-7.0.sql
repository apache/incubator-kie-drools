alter table RequestInfo add column priority int4;
ALTER TABLE ProcessInstanceLog ADD COLUMN processType int4;

update ProcessInstanceLog set processType = 1;
update RequestInfo set priority = 5;

create table CaseIdInfo (
    id int8 not null,
    caseIdPrefix varchar(255),
    currentValue int8,
    primary key (id)
);

create table CaseRoleAssignmentLog (
    id int8 not null,
    caseId varchar(255),
    entityId varchar(255),
    processInstanceId int8 not null,
    roleName varchar(255),
    type int4 not null,
    primary key (id)
);

alter table CaseIdInfo 
    add constraint UK_CaseIdInfo_1 unique (caseIdPrefix);

create sequence CASE_ID_INFO_ID_SEQ;

create sequence CASE_ROLE_ASSIGN_LOG_ID_SEQ;

ALTER TABLE NodeInstanceLog ADD COLUMN referenceId int8;
ALTER TABLE NodeInstanceLog ADD COLUMN nodeContainerId varchar(255);

ALTER TABLE RequestInfo ADD COLUMN processInstanceId int8;

ALTER TABLE AuditTaskImpl ADD COLUMN lastModificationDate timestamp;
update AuditTaskImpl ati set lastModificationDate = (
    select max(logTime) from TaskEvent where taskId=ati.taskId group by taskId
);

create table CaseFileDataLog (
    id int8 not null,
    caseDefId varchar(255),
    caseId varchar(255),
    itemName varchar(255),
    itemType varchar(255),
    itemValue varchar(255),
    lastModified timestamp,
    lastModifiedBy varchar(255),
    primary key (id)
);

create table ExecutionErrorInfo (
    id int8 not null,
    ERROR_ACK int2,
    ERROR_ACK_AT timestamp,
    ERROR_ACK_BY varchar(255),
    ACTIVITY_ID int8,
    ACTIVITY_NAME varchar(255),
    DEPLOYMENT_ID varchar(255),
    ERROR_INFO text,
    ERROR_DATE timestamp,
    ERROR_ID varchar(255),
    ERROR_MSG varchar(255),
    INIT_ACTIVITY_ID int8,
    JOB_ID int8,
    PROCESS_ID varchar(255),
    PROCESS_INST_ID int8,
    ERROR_TYPE varchar(255),
    primary key (id)
);

create sequence CASE_FILE_DATA_LOG_ID_SEQ;

create sequence EXEC_ERROR_INFO_ID_SEQ;

ALTER TABLE ProcessInstanceLog ADD COLUMN sla_due_date timestamp;
ALTER TABLE ProcessInstanceLog ADD COLUMN slaCompliance int4;
ALTER TABLE NodeInstanceLog ADD COLUMN sla_due_date timestamp;
ALTER TABLE NodeInstanceLog ADD COLUMN slaCompliance int4;