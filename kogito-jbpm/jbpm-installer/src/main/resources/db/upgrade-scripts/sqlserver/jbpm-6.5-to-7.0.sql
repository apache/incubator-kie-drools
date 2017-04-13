alter table RequestInfo add priority int not null;
ALTER TABLE ProcessInstanceLog ADD processType int not null;

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
    
ALTER TABLE NodeInstanceLog ADD COLUMN referenceId numeric(19,0);
ALTER TABLE NodeInstanceLog ADD COLUMN nodeContainerId varchar(255);   

ALTER TABLE RequestInfo ADD COLUMN processInstanceId numeric(19,0);    

ALTER TABLE AuditTaskImpl ADD COLUMN lastModificationDate datetime;
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
