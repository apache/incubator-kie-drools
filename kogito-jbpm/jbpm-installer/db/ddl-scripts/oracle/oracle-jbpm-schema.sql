

    create table Attachment (
        AttachmentId number(19,0) not null,
        accessType number(10,0),
        attachedAt timestamp,
        attachmentContentId number(19,0) not null,
        contentType varchar2(255 char),
        name varchar2(255 char),
        attachment_size number(10,0),
        attachedBy_id varchar2(255 char),
        TaskData_Attachments_Id number(19,0),
        primary key (AttachmentId)
    );

    create table BAMTaskSummary (
        BAMTaskId number(19,0) not null,
        createdDate timestamp,
        duration number(19,0),
        endDate timestamp,
        processInstanceId number(19,0) not null,
        startDate timestamp,
        status varchar2(255 char),
        taskId number(19,0) not null,
        taskName varchar2(255 char),
        userId varchar2(255 char),
        primary key (BAMTaskId)
    );

    create table BooleanExpression (
        id number(19,0) not null,
        expression clob,
        type varchar2(255 char),
        Escalation_Constraints_Id number(19,0),
        primary key (id)
    );

    create table Content (
        id number(19,0) not null,
        content blob,
        primary key (id)
    );

    create table ContextMappingInfo (
        mappingId number(19,0) not null,
        CONTEXT_ID varchar2(255 char) not null,
        KSESSION_ID number(10,0) not null,
        OPTLOCK number(10,0),
        primary key (mappingId)
    );

    create table CorrelationKeyInfo (
        keyId number(19,0) not null,
        name varchar2(255 char),
        processInstanceId number(19,0) not null,
        OPTLOCK number(10,0),
        primary key (keyId)
    );

    create table CorrelationPropertyInfo (
        propertyId number(19,0) not null,
        name varchar2(255 char),
        value varchar2(255 char),
        OPTLOCK number(10,0),
        correlationKey_keyId number(19,0),
        primary key (propertyId)
    );

    create table Deadline (
        id number(19,0) not null,
        deadline_date timestamp,
        escalated number(5,0),
        Deadlines_StartDeadLine_Id number(19,0),
        Deadlines_EndDeadLine_Id number(19,0),
        primary key (id)
    );

    create table Delegation_delegates (
        task_id number(19,0) not null,
        entity_id varchar2(255 char) not null
    );

    create table ErrorInfo (
        id number(19,0) not null,
        message varchar2(255 char),
        stacktrace long,
        timestamp timestamp,
        REQUEST_ID number(19,0) not null,
        primary key (id)
    );

    create table Escalation (
        id number(19,0) not null,
        name varchar2(255 char),
        Deadline_Escalation_Id number(19,0),
        primary key (id)
    );

    create table EventTypes (
        InstanceId number(19,0) not null,
        eventTypes varchar2(255 char)
    );

    create table I18NText (
        I18NTextId number(19,0) not null,
        language varchar2(255 char),
        shortText varchar2(255 char),
        text clob,
        Task_Subjects_Id number(19,0),
        Task_Names_Id number(19,0),
        Task_Descriptions_Id number(19,0),
        Reassignment_Documentation_Id number(19,0),
        Notification_Subjects_Id number(19,0),
        Notification_Names_Id number(19,0),
        Notification_Documentation_Id number(19,0),
        Notification_Descriptions_Id number(19,0),
        Deadline_Documentation_Id number(19,0),
        primary key (I18NTextId)
    );

    create table NodeInstanceLog (
        id number(19,0) not null,
        connection varchar2(255 char),
        log_date timestamp,
        externalId varchar2(255 char),
        nodeId varchar2(255 char),
        nodeInstanceId varchar2(255 char),
        nodeName varchar2(255 char),
        nodeType varchar2(255 char),
        processId varchar2(255 char),
        processInstanceId number(19,0) not null,
        type number(10,0) not null,
        workItemId number(19,0),
        primary key (id)
    );

    create table Notification (
        DTYPE varchar2(31 char) not null,
        NotificationId number(19,0) not null,
        priority number(10,0) not null,
        Escalation_Notifications_Id number(19,0),
        primary key (NotificationId)
    );

    create table Notification_BAs (
        task_id number(19,0) not null,
        entity_id varchar2(255 char) not null
    );

    create table Notification_Recipients (
        task_id number(19,0) not null,
        entity_id varchar2(255 char) not null
    );

    create table Notification_email_header (
        Notification_NotificationId number(19,0) not null,
        emailHeaders_id number(19,0) not null,
        mapkey varchar2(255 char) not null,
        primary key (Notification_NotificationId, mapkey)
    );

    create table OrganizationalEntity (
        DTYPE varchar2(31 char) not null,
        id varchar2(255 char) not null,
        primary key (id)
    );

    create table PeopleAssignments_BAs (
        task_id number(19,0) not null,
        entity_id varchar2(255 char) not null
    );

    create table PeopleAssignments_ExclOwners (
        task_id number(19,0) not null,
        entity_id varchar2(255 char) not null
    );

    create table PeopleAssignments_PotOwners (
        task_id number(19,0) not null,
        entity_id varchar2(255 char) not null
    );

    create table PeopleAssignments_Recipients (
        task_id number(19,0) not null,
        entity_id varchar2(255 char) not null
    );

    create table PeopleAssignments_Stakeholders (
        task_id number(19,0) not null,
        entity_id varchar2(255 char) not null
    );

    create table ProcessInstanceInfo (
        InstanceId number(19,0) not null,
        lastModificationDate timestamp,
        lastReadDate timestamp,
        processId varchar2(255 char),
        processInstanceByteArray blob,
        startDate timestamp,
        state number(10,0) not null,
        OPTLOCK number(10,0),
        primary key (InstanceId)
    );

    create table ProcessInstanceLog (
        id number(19,0) not null,
        duration number(19,0),
        end_date timestamp,
        externalId varchar2(255 char),
        user_identity varchar2(255 char),
        outcome varchar2(255 char),
        parentProcessInstanceId number(19,0),
        processId varchar2(255 char),
        processInstanceId number(19,0) not null,
        processName varchar2(255 char),
        processVersion varchar2(255 char),
        start_date timestamp,
        status number(10,0),
        primary key (id)
    );

    create table Reassignment (
        id number(19,0) not null,
        Escalation_Reassignments_Id number(19,0),
        primary key (id)
    );

    create table Reassignment_potentialOwners (
        task_id number(19,0) not null,
        entity_id varchar2(255 char) not null
    );

    create table RequestInfo (
        id number(19,0) not null,
        commandName varchar2(255 char),
        deploymentId varchar2(255 char),
        executions number(10,0) not null,
        businessKey varchar2(255 char),
        message varchar2(255 char),
        requestData blob,
        responseData blob,
        retries number(10,0) not null,
        status varchar2(255 char),
        timestamp timestamp,
        primary key (id)
    );

    create table SessionInfo (
        id number(10,0) not null,
        lastModificationDate timestamp,
        rulesByteArray blob,
        startDate timestamp,
        OPTLOCK number(10,0),
        primary key (id)
    );

    create table Task (
        TaskId number(19,0) not null,
        archived number(5,0),
        allowedToDelegate varchar2(255 char),
        formName varchar2(255 char),
        priority number(10,0) not null,
        subTaskStrategy varchar2(255 char),
        activationTime timestamp,
        createdOn timestamp,
        deploymentId varchar2(255 char),
        documentAccessType number(10,0),
        documentContentId number(19,0) not null,
        documentType varchar2(255 char),
        expirationTime timestamp,
        faultAccessType number(10,0),
        faultContentId number(19,0) not null,
        faultName varchar2(255 char),
        faultType varchar2(255 char),
        outputAccessType number(10,0),
        outputContentId number(19,0) not null,
        outputType varchar2(255 char),
        parentId number(19,0) not null,
        previousStatus number(10,0),
        processId varchar2(255 char),
        processInstanceId number(19,0) not null,
        processSessionId number(10,0) not null,
        skipable number(1,0) not null,
        status varchar2(255 char),
        workItemId number(19,0) not null,
        taskType varchar2(255 char),
        OPTLOCK number(10,0),
        taskInitiator_id varchar2(255 char),
        actualOwner_id varchar2(255 char),
        createdBy_id varchar2(255 char),
        primary key (TaskId)
    );

    create table TaskDef (
        TaskDefId number(19,0) not null,
        name varchar2(255 char),
        priority number(10,0) not null,
        primary key (TaskDefId)
    );

    create table TaskEvent (
        id number(19,0) not null,
        logTime timestamp,
        taskId number(19,0),
        type varchar2(255 char),
        userId varchar2(255 char),
        primary key (id)
    );

    create table VariableInstanceLog (
        id number(19,0) not null,
        log_date timestamp,
        externalId varchar2(255 char),
        oldValue varchar2(255 char),
        processId varchar2(255 char),
        processInstanceId number(19,0) not null,
        value varchar2(255 char),
        variableId varchar2(255 char),
        variableInstanceId varchar2(255 char),
        primary key (id)
    );

    create table WorkItemInfo (
        workItemId number(19,0) not null,
        creationDate timestamp,
        name varchar2(255 char),
        processInstanceId number(19,0) not null,
        state number(19,0) not null,
        OPTLOCK number(10,0),
        workItemByteArray blob,
        primary key (workItemId)
    );

    create table email_header (
        id number(19,0) not null,
        body clob,
        fromAddress varchar2(255 char),
        language varchar2(255 char),
        replyToAddress varchar2(255 char),
        subject varchar2(255 char),
        primary key (id)
    );

    create table task_comment (
        id number(19,0) not null,
        addedAt timestamp,
        text clob,
        addedBy_id varchar2(255 char),
        TaskData_Comments_Id number(19,0),
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
        add constraint FKF30FE344DD2D7416 
        foreign key (Notification_NotificationId) 
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
