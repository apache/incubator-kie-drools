alter table RequestInfo add priority int null;
ALTER TABLE ProcessInstanceLog ADD processType int null;

update ProcessInstanceLog set processType = 1;
update RequestInfo set priority = 5;

create table CaseIdInfo (
    id numeric(19,0) identity not null,
    caseIdPrefix varchar(255),
    currentValue numeric(19,0),
    primary key (id)
);

create table CaseRoleAssignmentLog (
    id numeric(19,0) identity not null,
    caseId varchar(255),
    entityId varchar(255),
    processInstanceId numeric(19,0) not null,
    roleName varchar(255),
    type int not null,
    primary key (id)
);

alter table CaseIdInfo 
    add constraint UK_CaseIdInfo_1 unique (caseIdPrefix);
    
ALTER TABLE NodeInstanceLog ADD referenceId numeric(19,0);
ALTER TABLE NodeInstanceLog ADD nodeContainerId varchar(255);   

ALTER TABLE RequestInfo ADD processInstanceId numeric(19,0);

ALTER TABLE AuditTaskImpl ADD lastModificationDate datetime;
update AuditTaskImpl ati set lastModificationDate = (
    select max(logTime) from TaskEvent where taskId=ati.taskId group by taskId
);

create table CaseFileDataLog (
    id numeric(19,0) identity not null,
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
    id numeric(19,0) identity not null,
    ERROR_ACK smallint,
    ERROR_ACK_AT datetime,
    ERROR_ACK_BY varchar(255),
    ACTIVITY_ID numeric(19,0),
    ACTIVITY_NAME varchar(255),
    DEPLOYMENT_ID varchar(255),
    ERROR_INFO text,
    ERROR_DATE datetime,
    ERROR_ID varchar(255),
    ERROR_MSG varchar(255),
    INIT_ACTIVITY_ID numeric(19,0),
    JOB_ID numeric(19,0),
    PROCESS_ID varchar(255),
    PROCESS_INST_ID numeric(19,0),
    ERROR_TYPE varchar(255),
    primary key (id)
);

ALTER TABLE ProcessInstanceLog ADD sla_due_date datetime;
ALTER TABLE ProcessInstanceLog ADD slaCompliance int;
ALTER TABLE NodeInstanceLog ADD sla_due_date datetime;
ALTER TABLE NodeInstanceLog ADD slaCompliance int;  