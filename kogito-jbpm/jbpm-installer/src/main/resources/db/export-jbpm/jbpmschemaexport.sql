
    drop table CaseIdInfo;

    drop table CaseRoleAssignmentLog;

    create table CaseIdInfo (
        id bigint identity not null,
        caseIdPrefix varchar(255) null,
        currentValue bigint null,
        primary key (id)
    );

    create table CaseRoleAssignmentLog (
        id bigint identity not null,
        caseId varchar(255) null,
        entityId varchar(255) null,
        processInstanceId bigint not null,
        roleName varchar(255) null,
        type int not null,
        primary key (id)
    );

    alter table CaseIdInfo 
        add constraint UK_CaseIdInfo_1 unique (caseIdPrefix);
