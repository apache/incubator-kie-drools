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