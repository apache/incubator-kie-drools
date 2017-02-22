alter table RequestInfo add column priority integer;
alter table ProcessInstanceLog add column processType integer;

update ProcessInstanceLog set processType = 1;
update RequestInfo set priority = 5;

create table CaseIdInfo (
    id bigint not null auto_increment,
    caseIdPrefix varchar(255),
    currentValue bigint,
    primary key (id)
) ENGINE=InnoDB;

create table CaseRoleAssignmentLog (
    id bigint not null auto_increment,
    caseId varchar(255),
    entityId varchar(255),
    processInstanceId bigint not null,
    roleName varchar(255),
    type integer not null,
    primary key (id)
) ENGINE=InnoDB;

alter table CaseIdInfo 
        add constraint UK_CaseIdInfo_1 unique (caseIdPrefix);
        
ALTER TABLE NodeInstanceLog ADD COLUMN referenceId bigint;
ALTER TABLE NodeInstanceLog ADD COLUMN nodeContainerId varchar(255);        