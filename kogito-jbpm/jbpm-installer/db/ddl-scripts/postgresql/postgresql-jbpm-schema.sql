    create table Attachment (
        id int8 not null,
        accessType int4,
        attachedAt timestamp,
        attachmentContentId int8 not null,
        contentType varchar(255),
        name varchar(255),
        attachment_size int4,
        attachedBy_id varchar(255),
        TaskData_Attachments_Id int8,
        primary key (id)
    );

    create table BAMTaskSummary (
        pk int8 not null,
        createdDate timestamp,
        duration int8,
        endDate timestamp,
        processInstanceId int8 not null,
        startDate timestamp,
        status varchar(255),
        taskId int8 not null,
        taskName varchar(255),
        userId varchar(255),
        OPTLOCK int4,
        primary key (pk)
    );

    create table BooleanExpression (
        id int8 not null,
        expression text,
        type varchar(255),
        Escalation_Constraints_Id int8,
        primary key (id)
    );

    create table Content (
        id int8 not null,
        content oid,
        primary key (id)
    );

    create table ContextMappingInfo (
        mappingId int8 not null,
        CONTEXT_ID varchar(255) not null,
        KSESSION_ID int4 not null,
        OPTLOCK int4,
        primary key (mappingId)
    );

    create table CorrelationKeyInfo (
        keyId int8 not null,
        name varchar(255),
        processInstanceId int8 not null,
        OPTLOCK int4,
        primary key (keyId)
    );

    create table CorrelationPropertyInfo (
        propertyId int8 not null,
        name varchar(255),
        value varchar(255),
        OPTLOCK int4,
        correlationKey_keyId int8,
        primary key (propertyId)
    );

    create table Deadline (
        id int8 not null,
        deadline_date timestamp,
        escalated int2,
        Deadlines_StartDeadLine_Id int8,
        Deadlines_EndDeadLine_Id int8,
        primary key (id)
    );

    create table Delegation_delegates (
        task_id int8 not null,
        entity_id varchar(255) not null
    );

    create table ErrorInfo (
        id int8 not null,
        message varchar(255),
        stacktrace varchar(5000),
        timestamp timestamp,
        REQUEST_ID int8 not null,
        primary key (id)
    );

    create table Escalation (
        id int8 not null,
        name varchar(255),
        Deadline_Escalation_Id int8,
        primary key (id)
    );

    create table EventTypes (
        InstanceId int8 not null,
        element varchar(255)
    );

    create table I18NText (
        id int8 not null,
        language varchar(255),
        shortText varchar(255),
        text text,
        Task_Subjects_Id int8,
        Task_Names_Id int8,
        Task_Descriptions_Id int8,
        Reassignment_Documentation_Id int8,
        Notification_Subjects_Id int8,
        Notification_Names_Id int8,
        Notification_Documentation_Id int8,
        Notification_Descriptions_Id int8,
        Deadline_Documentation_Id int8,
        primary key (id)
    );

    create table NodeInstanceLog (
        id int8 not null,
        connection varchar(255),
        log_date timestamp,
        externalId varchar(255),
        nodeId varchar(255),
        nodeInstanceId varchar(255),
        nodeName varchar(255),
        nodeType varchar(255),
        processId varchar(255),
        processInstanceId int8 not null,
        type int4 not null,
        workItemId int8,
        primary key (id)
    );

    create table Notification (
        DTYPE varchar(31) not null,
        id int8 not null,
        priority int4 not null,
        Escalation_Notifications_Id int8,
        primary key (id)
    );

    create table Notification_BAs (
        task_id int8 not null,
        entity_id varchar(255) not null
    );

    create table Notification_Recipients (
        task_id int8 not null,
        entity_id varchar(255) not null
    );

    create table Notification_email_header (
        Notification_id int8 not null,
        emailHeaders_id int8 not null,
        mapkey varchar(255) not null,
        primary key (Notification_id, mapkey)
    );

    create table OrganizationalEntity (
        DTYPE varchar(31) not null,
        id varchar(255) not null,
        primary key (id)
    );

    create table PeopleAssignments_BAs (
        task_id int8 not null,
        entity_id varchar(255) not null
    );

    create table PeopleAssignments_ExclOwners (
        task_id int8 not null,
        entity_id varchar(255) not null
    );

    create table PeopleAssignments_PotOwners (
        task_id int8 not null,
        entity_id varchar(255) not null
    );

    create table PeopleAssignments_Recipients (
        task_id int8 not null,
        entity_id varchar(255) not null
    );

    create table PeopleAssignments_Stakeholders (
        task_id int8 not null,
        entity_id varchar(255) not null
    );

    create table ProcessInstanceInfo (
        InstanceId int8 not null,
        lastModificationDate timestamp,
        lastReadDate timestamp,
        processId varchar(255),
        processInstanceByteArray oid,
        startDate timestamp,
        state int4 not null,
        OPTLOCK int4,
        primary key (InstanceId)
    );

    create table ProcessInstanceLog (
        id int8 not null,
        duration int8,
        end_date timestamp,
        externalId varchar(255),
        user_identity varchar(255),
        outcome varchar(255),
        parentProcessInstanceId int8,
        processId varchar(255),
        processInstanceId int8 not null,
        processName varchar(255),
        processVersion varchar(255),
        start_date timestamp,
        status int4,
        primary key (id)
    );

    create table Reassignment (
        id int8 not null,
        Escalation_Reassignments_Id int8,
        primary key (id)
    );

    create table Reassignment_potentialOwners (
        task_id int8 not null,
        entity_id varchar(255) not null
    );

    create table RequestInfo (
        id int8 not null,
        commandName varchar(255),
        deploymentId varchar(255),
        executions int4 not null,
        businessKey varchar(255),
        message varchar(255),
        requestData oid,
        responseData oid,
        retries int4 not null,
        status varchar(255),
        timestamp timestamp,
        primary key (id)
    );

    create table SessionInfo (
        id int4 not null,
        lastModificationDate timestamp,
        rulesByteArray oid,
        startDate timestamp,
        OPTLOCK int4,
        primary key (id)
    );

    create table Task (
        id int8 not null,
        archived int2,
        allowedToDelegate varchar(255),
        formName varchar(255),
        priority int4 not null,
        subTaskStrategy varchar(255),
        activationTime timestamp,
        createdOn timestamp,
        deploymentId varchar(255),
        documentAccessType int4,
        documentContentId int8 not null,
        documentType varchar(255),
        expirationTime timestamp,
        faultAccessType int4,
        faultContentId int8 not null,
        faultName varchar(255),
        faultType varchar(255),
        outputAccessType int4,
        outputContentId int8 not null,
        outputType varchar(255),
        parentId int8 not null,
        previousStatus int4,
        processId varchar(255),
        processInstanceId int8 not null,
        processSessionId int4 not null,
        skipable boolean not null,
        status varchar(255),
        workItemId int8 not null,
        taskType varchar(255),
        OPTLOCK int4,
        taskInitiator_id varchar(255),
        actualOwner_id varchar(255),
        createdBy_id varchar(255),
        primary key (id)
    );

    create table TaskDef (
        id int8 not null,
        name varchar(255),
        priority int4 not null,
        primary key (id)
    );

    create table TaskEvent (
        id int8 not null,
        logTime timestamp,
        taskId int8,
        type varchar(255),
        userId varchar(255),
        OPTLOCK int4,
        primary key (id)
    );

    create table VariableInstanceLog (
        id int8 not null,
        log_date timestamp,
        externalId varchar(255),
        oldValue varchar(255),
        processId varchar(255),
        processInstanceId int8 not null,
        value varchar(255),
        variableId varchar(255),
        variableInstanceId varchar(255),
        primary key (id)
    );

    create table WorkItemInfo (
        workItemId int8 not null,
        creationDate timestamp,
        name varchar(255),
        processInstanceId int8 not null,
        state int8 not null,
        OPTLOCK int4,
        workItemByteArray oid,
        primary key (workItemId)
    );

    create table email_header (
        id int8 not null,
        body text,
        fromAddress varchar(255),
        language varchar(255),
        replyToAddress varchar(255),
        subject varchar(255),
        primary key (id)
    );

    create table task_comment (
        id int8 not null,
        addedAt timestamp,
        text text,
        addedBy_id varchar(255),
        TaskData_Comments_Id int8,
        primary key (id)
    );

    alter table Attachment 
        add constraint FK1C93543D937BFB5 
        foreign key (attachedBy_id) 
        references OrganizationalEntity;

    alter table Attachment 
        add constraint FK1C9354333CA892A 
        foreign key (TaskData_Attachments_Id) 
        references Task;

    alter table BooleanExpression 
        add constraint FKE3D208C06C97C90E 
        foreign key (Escalation_Constraints_Id) 
        references Escalation;

    alter table CorrelationPropertyInfo 
        add constraint FK761452A5D87156ED 
        foreign key (correlationKey_keyId) 
        references CorrelationKeyInfo;

    alter table Deadline 
        add constraint FK21DF3E78A9FE0EF4 
        foreign key (Deadlines_StartDeadLine_Id) 
        references Task;

    alter table Deadline 
        add constraint FK21DF3E78695E4DDB 
        foreign key (Deadlines_EndDeadLine_Id) 
        references Task;

    alter table Delegation_delegates 
        add constraint FK47485D5772B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table Delegation_delegates 
        add constraint FK47485D57786553A5 
        foreign key (task_id) 
        references Task;

    alter table ErrorInfo 
        add constraint FK8B1186B6724A467 
        foreign key (REQUEST_ID) 
        references RequestInfo;

    alter table Escalation 
        add constraint FK67B2C6B5D1E5CC1 
        foreign key (Deadline_Escalation_Id) 
        references Deadline;

    alter table EventTypes 
        add constraint FKB0E5621F7665489A 
        foreign key (InstanceId) 
        references ProcessInstanceInfo;

    alter table I18NText 
        add constraint FK2349686BF4ACCD69 
        foreign key (Task_Subjects_Id) 
        references Task;

    alter table I18NText 
        add constraint FK2349686B424B187C 
        foreign key (Task_Names_Id) 
        references Task;

    alter table I18NText 
        add constraint FK2349686BAB648139 
        foreign key (Task_Descriptions_Id) 
        references Task;

    alter table I18NText 
        add constraint FK2349686BB340A2AA 
        foreign key (Reassignment_Documentation_Id) 
        references Reassignment;

    alter table I18NText 
        add constraint FK2349686BF0CDED35 
        foreign key (Notification_Subjects_Id) 
        references Notification;

    alter table I18NText 
        add constraint FK2349686BCC03ED3C 
        foreign key (Notification_Names_Id) 
        references Notification;

    alter table I18NText 
        add constraint FK2349686B77C1C08A 
        foreign key (Notification_Documentation_Id) 
        references Notification;

    alter table I18NText 
        add constraint FK2349686B18DDFE05 
        foreign key (Notification_Descriptions_Id) 
        references Notification;

    alter table I18NText 
        add constraint FK2349686B78AF072A 
        foreign key (Deadline_Documentation_Id) 
        references Deadline;

    alter table Notification 
        add constraint FK2D45DD0BC0C0F29C 
        foreign key (Escalation_Notifications_Id) 
        references Escalation;

    alter table Notification_BAs 
        add constraint FK2DD68EE072B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table Notification_BAs 
        add constraint FK2DD68EE093F2090B 
        foreign key (task_id) 
        references Notification;

    alter table Notification_Recipients 
        add constraint FK98FD214E72B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table Notification_Recipients 
        add constraint FK98FD214E93F2090B 
        foreign key (task_id) 
        references Notification;

    alter table Notification_email_header 
        add constraint UK_F30FE3446CEA0510 unique (emailHeaders_id);

    alter table Notification_email_header 
        add constraint FKF30FE3448BED1339 
        foreign key (emailHeaders_id) 
        references email_header;

    alter table Notification_email_header 
        add constraint FKF30FE3443E3E97EB 
        foreign key (Notification_id) 
        references Notification;

    alter table PeopleAssignments_BAs 
        add constraint FK9D8CF4EC72B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table PeopleAssignments_BAs 
        add constraint FK9D8CF4EC786553A5 
        foreign key (task_id) 
        references Task;

    alter table PeopleAssignments_ExclOwners 
        add constraint FKC77B97E472B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table PeopleAssignments_ExclOwners 
        add constraint FKC77B97E4786553A5 
        foreign key (task_id) 
        references Task;

    alter table PeopleAssignments_PotOwners 
        add constraint FK1EE418D72B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table PeopleAssignments_PotOwners 
        add constraint FK1EE418D786553A5 
        foreign key (task_id) 
        references Task;

    alter table PeopleAssignments_Recipients 
        add constraint FKC6F615C272B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table PeopleAssignments_Recipients 
        add constraint FKC6F615C2786553A5 
        foreign key (task_id) 
        references Task;

    alter table PeopleAssignments_Stakeholders 
        add constraint FK482F79D572B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table PeopleAssignments_Stakeholders 
        add constraint FK482F79D5786553A5 
        foreign key (task_id) 
        references Task;

    alter table Reassignment 
        add constraint FK724D056062A1E871 
        foreign key (Escalation_Reassignments_Id) 
        references Escalation;

    alter table Reassignment_potentialOwners 
        add constraint FK90B59CFF72B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table Reassignment_potentialOwners 
        add constraint FK90B59CFF35D2FEE0 
        foreign key (task_id) 
        references Reassignment;

    alter table Task 
        add constraint FK27A9A53C55C806 
        foreign key (taskInitiator_id) 
        references OrganizationalEntity;

    alter table Task 
        add constraint FK27A9A5B723BE8B 
        foreign key (actualOwner_id) 
        references OrganizationalEntity;

    alter table Task 
        add constraint FK27A9A55427E8F1 
        foreign key (createdBy_id) 
        references OrganizationalEntity;

    alter table task_comment 
        add constraint FK61F475A57A3215D9 
        foreign key (addedBy_id) 
        references OrganizationalEntity;

    alter table task_comment 
        add constraint FK61F475A5F510CB46 
        foreign key (TaskData_Comments_Id) 
        references Task;

    create sequence ATTACHMENT_ID_SEQ;

    create sequence BAM_TASK_ID_SEQ;

    create sequence BOOLEANEXPR_ID_SEQ;

    create sequence COMMENT_ID_SEQ;

    create sequence CONTENT_ID_SEQ;

    create sequence CONTEXT_MAPPING_INFO_ID_SEQ;

    create sequence CORRELATION_KEY_ID_SEQ;

    create sequence CORRELATION_PROP_ID_SEQ;

    create sequence DEADLINE_ID_SEQ;

    create sequence EMAILNOTIFHEAD_ID_SEQ;

    create sequence ERROR_INFO_ID_SEQ;

    create sequence ESCALATION_ID_SEQ;

    create sequence I18NTEXT_ID_SEQ;

    create sequence NODE_INST_LOG_ID_SEQ;

    create sequence NOTIFICATION_ID_SEQ;

    create sequence PROCESS_INSTANCE_INFO_ID_SEQ;

    create sequence PROC_INST_LOG_ID_SEQ;

    create sequence REASSIGNMENT_ID_SEQ;

    create sequence REQUEST_INFO_ID_SEQ;

    create sequence SESSIONINFO_ID_SEQ;

    create sequence TASK_DEF_ID_SEQ;

    create sequence TASK_EVENT_ID_SEQ;

    create sequence TASK_ID_SEQ;

    create sequence VAR_INST_LOG_ID_SEQ;

    create sequence WORKITEMINFO_ID_SEQ;