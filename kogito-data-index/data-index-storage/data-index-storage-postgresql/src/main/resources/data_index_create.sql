
    create table JobEntity (
       id varchar(255) not null,
        callbackEndpoint varchar(255),
        endpoint varchar(255),
        executionCounter int4,
        expirationTime timestamp,
        lastUpdate timestamp,
        nodeInstanceId varchar(255),
        priority int4,
        processId varchar(255),
        processInstanceId varchar(255),
        repeatInterval int8,
        repeatLimit int4,
        retries int4,
        rootProcessId varchar(255),
        rootProcessInstanceId varchar(255),
        scheduledId varchar(255),
        status varchar(255),
        primary key (id)
    );

    create table MilestoneEntity (
       id varchar(255) not null,
        name varchar(255),
        status varchar(255),
        primary key (id)
    );

    create table NodeInstanceEntity (
       id varchar(255) not null,
        definitionId varchar(255),
        enter timestamp,
        exit timestamp,
        name varchar(255),
        nodeId varchar(255),
        type varchar(255),
        primary key (id)
    );

    create table ProcessInstanceEntity (
       id varchar(255) not null,
        businessKey varchar(255),
        end_time timestamp,
        endpoint varchar(255),
        message varchar(255),
        nodeDefinitionId varchar(255),
        last_update_time timestamp,
        parentProcessInstanceId varchar(255),
        processId varchar(255),
        processName varchar(255),
        rootProcessId varchar(255),
        rootProcessInstanceId varchar(255),
        start_time timestamp,
        state int4,
        variables jsonb,
        primary key (id)
    );

    create table ProcessInstanceEntity_addons (
       ProcessInstanceEntity_id varchar(255) not null,
        addons varchar(255)
    );

    create table ProcessInstanceEntity_MilestoneEntity (
       ProcessInstanceEntity_id varchar(255) not null,
        milestones_id varchar(255) not null
    );

    create table ProcessInstanceEntity_NodeInstanceEntity (
       ProcessInstanceEntity_id varchar(255) not null,
        nodes_id varchar(255) not null
    );

    create table ProcessInstanceEntity_roles (
       ProcessInstanceEntity_id varchar(255) not null,
        roles varchar(255)
    );

    create table UserTaskInstanceEntity (
       id varchar(255) not null,
        actualOwner varchar(255),
        completed timestamp,
        description varchar(255),
        endpoint varchar(255),
        inputs jsonb,
        lastUpdate timestamp,
        name varchar(255),
        outputs jsonb,
        priority varchar(255),
        processId varchar(255),
        processInstanceId varchar(255),
        referenceName varchar(255),
        rootProcessId varchar(255),
        rootProcessInstanceId varchar(255),
        started timestamp,
        state varchar(255),
        primary key (id)
    );

    create table UserTaskInstanceEntity_adminGroups (
       UserTaskInstanceEntity_id varchar(255) not null,
        adminGroups varchar(255)
    );

    create table UserTaskInstanceEntity_adminUsers (
       UserTaskInstanceEntity_id varchar(255) not null,
        adminUsers varchar(255)
    );

    create table UserTaskInstanceEntity_excludedUsers (
       UserTaskInstanceEntity_id varchar(255) not null,
        excludedUsers varchar(255)
    );

    create table UserTaskInstanceEntity_potentialGroups (
       UserTaskInstanceEntity_id varchar(255) not null,
        potentialGroups varchar(255)
    );

    create table UserTaskInstanceEntity_potentialUsers (
       UserTaskInstanceEntity_id varchar(255) not null,
        potentialUsers varchar(255)
    );

    alter table if exists ProcessInstanceEntity_MilestoneEntity 
       add constraint UK_iw2hpwwogyfuwe1oss9oqar93 unique (milestones_id);

    alter table if exists ProcessInstanceEntity_NodeInstanceEntity 
       add constraint UK_sdve1m52p29bajp0ui95er5qj unique (nodes_id);

    alter table if exists ProcessInstanceEntity_addons 
       add constraint FKsdc13xvts9tdmimek9pfei5up 
       foreign key (ProcessInstanceEntity_id) 
       references ProcessInstanceEntity 
       on delete cascade;

    alter table if exists ProcessInstanceEntity_MilestoneEntity 
       add constraint FKkk3pdt6yntyad4257jbcul7xk 
       foreign key (milestones_id) 
       references MilestoneEntity;

    alter table if exists ProcessInstanceEntity_MilestoneEntity 
       add constraint FK8frxihvalnxacv1vcdbrt4rwa 
       foreign key (ProcessInstanceEntity_id) 
       references ProcessInstanceEntity;

    alter table if exists ProcessInstanceEntity_NodeInstanceEntity 
       add constraint FKrsiysusml360wxgiqkfjbuftg 
       foreign key (nodes_id) 
       references NodeInstanceEntity;

    alter table if exists ProcessInstanceEntity_NodeInstanceEntity 
       add constraint FK2w1s1fhpoaeighh1og96q24dn 
       foreign key (ProcessInstanceEntity_id) 
       references ProcessInstanceEntity;

    alter table if exists ProcessInstanceEntity_roles 
       add constraint FK2cwq2idof87vgrg6wy5ng15h2 
       foreign key (ProcessInstanceEntity_id) 
       references ProcessInstanceEntity 
       on delete cascade;

    alter table if exists UserTaskInstanceEntity_adminGroups 
       add constraint FKae51g1f8eyy6695mdecdi3bjd 
       foreign key (UserTaskInstanceEntity_id) 
       references UserTaskInstanceEntity 
       on delete cascade;

    alter table if exists UserTaskInstanceEntity_adminUsers 
       add constraint FK142t43lxnp57eq9wiwtlayat4 
       foreign key (UserTaskInstanceEntity_id) 
       references UserTaskInstanceEntity 
       on delete cascade;

    alter table if exists UserTaskInstanceEntity_excludedUsers 
       add constraint FKl3j5dxg2be39bc8rqtxp5stvj 
       foreign key (UserTaskInstanceEntity_id) 
       references UserTaskInstanceEntity 
       on delete cascade;

    alter table if exists UserTaskInstanceEntity_potentialGroups 
       add constraint FKr1s3ekcbpoe38m13jb680qoo8 
       foreign key (UserTaskInstanceEntity_id) 
       references UserTaskInstanceEntity 
       on delete cascade;

    alter table if exists UserTaskInstanceEntity_potentialUsers 
       add constraint FK472u4nopslxglrwxldys5axje 
       foreign key (UserTaskInstanceEntity_id) 
       references UserTaskInstanceEntity 
       on delete cascade;
