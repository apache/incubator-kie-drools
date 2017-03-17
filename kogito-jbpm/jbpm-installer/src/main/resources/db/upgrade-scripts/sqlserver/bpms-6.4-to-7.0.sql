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
    
ALTER TABLE NodeInstanceLog ADD COLUMN referenceId numeric(19,0);
ALTER TABLE NodeInstanceLog ADD COLUMN nodeContainerId varchar(255);   

ALTER TABLE RequestInfo ADD COLUMN processInstanceId numeric(19,0); 