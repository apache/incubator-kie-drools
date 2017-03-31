alter table RequestInfo add priority int;
ALTER TABLE ProcessInstanceLog ADD processType int;

update ProcessInstanceLog set processType = 1;
update RequestInfo set priority = 5;

create table CaseIdInfo (
    id bigint identity not null,
    caseIdPrefix varchar(255),
    currentValue bigint,
    primary key (id)
);

create table CaseRoleAssignmentLog (
    id bigint identity not null,
    caseId varchar(255),
    entityId varchar(255),
    processInstanceId bigint not null,
    roleName varchar(255),
    type int not null,
    primary key (id)
);

alter table CaseIdInfo 
    add constraint UK_CaseIdInfo_1 unique (caseIdPrefix);
    
ALTER TABLE NodeInstanceLog ADD COLUMN referenceId bigint;
ALTER TABLE NodeInstanceLog ADD COLUMN nodeContainerId varchar(255);     

ALTER TABLE RequestInfo ADD COLUMN processInstanceId bigint;

ALTER TABLE AuditTaskImpl ADD COLUMN lastModificationDate datetime2;
update AuditTaskImpl ati set lastModificationDate = (
    select max(logTime) from TaskEvent where taskId=ati.taskId group by taskId
);