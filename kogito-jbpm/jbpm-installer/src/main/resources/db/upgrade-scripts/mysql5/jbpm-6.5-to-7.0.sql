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
