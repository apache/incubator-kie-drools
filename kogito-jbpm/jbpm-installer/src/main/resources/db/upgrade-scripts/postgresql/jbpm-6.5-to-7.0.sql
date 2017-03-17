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