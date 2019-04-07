alter table RequestInfo add column priority integer;
alter table ProcessInstanceLog add column processType integer;

update ProcessInstanceLog set processType = 1;
update RequestInfo set priority = 5;

create table CaseIdInfo (
    id bigint not null auto_increment,
    caseIdPrefix varchar(255),
    currentValue bigint,
    primary key (id)
);

create table CaseRoleAssignmentLog (
    id bigint not null auto_increment,
    caseId varchar(255),
    entityId varchar(255),
    processInstanceId bigint not null,
    roleName varchar(255),
    type integer not null,
    primary key (id)
);

alter table CaseIdInfo 
        add constraint UK_CaseIdInfo_1 unique (caseIdPrefix);
        
ALTER TABLE NodeInstanceLog ADD COLUMN referenceId bigint;
ALTER TABLE NodeInstanceLog ADD COLUMN nodeContainerId varchar(255); 

ALTER TABLE RequestInfo ADD COLUMN processInstanceId bigint;

ALTER TABLE AuditTaskImpl ADD COLUMN lastModificationDate datetime;
update AuditTaskImpl ati set lastModificationDate = (
    select max(logTime) from TaskEvent where taskId=ati.taskId group by taskId
);

create table CaseFileDataLog (
    id bigint not null auto_increment,
    caseDefId varchar(255),
    caseId varchar(255),
    itemName varchar(255),
    itemType varchar(255),
    itemValue varchar(255),
    lastModified datetime,
    lastModifiedBy varchar(255),
    primary key (id)
);

create table ExecutionErrorInfo (
    id bigint not null auto_increment,
    ERROR_ACK smallint,
    ERROR_ACK_AT datetime,
    ERROR_ACK_BY varchar(255),
    ACTIVITY_ID bigint,
    ACTIVITY_NAME varchar(255),
    DEPLOYMENT_ID varchar(255),
    ERROR_INFO longtext,
    ERROR_DATE datetime,
    ERROR_ID varchar(255),
    ERROR_MSG varchar(255),
    INIT_ACTIVITY_ID bigint,
    JOB_ID bigint,
    PROCESS_ID varchar(255),
    PROCESS_INST_ID bigint,
    ERROR_TYPE varchar(255),
    primary key (id)
);

create index IDX_ErrorInfo_pInstId on ExecutionErrorInfo(PROCESS_INST_ID);
create index IDX_ErrorInfo_errorAck on ExecutionErrorInfo(ERROR_ACK);