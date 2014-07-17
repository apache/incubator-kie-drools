    create table Attachment (
        id numeric(19,0) identity not null,
        accessType int,
        attachedAt datetime,
        attachmentContentId numeric(19,0) not null,
        contentType varchar(255),
        name varchar(255),
        attachment_size int,
        attachedBy_id varchar(255),
        TaskData_Attachments_Id numeric(19,0),
        primary key (id)
    );

    create table AuditTaskImpl (
        id numeric(19,0) identity not null,
        activationTime datetime,
        actualOwner varchar(255),
        createdBy varchar(255),
        createdOn datetime,
        deploymentId varchar(255),
        description varchar(255),
        dueDate datetime,
        name varchar(255),
        parentId numeric(19,0) not null,
        priority int not null,
        processId varchar(255),
        processInstanceId numeric(19,0) not null,
        processSessionId int not null,
        status varchar(255),
        taskId numeric(19,0),
        primary key (id)
    );

    create table BAMTaskSummary (
        pk numeric(19,0) identity not null,
        createdDate datetime,
        duration numeric(19,0),
        endDate datetime,
        processInstanceId numeric(19,0) not null,
        startDate datetime,
        status varchar(255),
        taskId numeric(19,0) not null,
        taskName varchar(255),
        userId varchar(255),
        OPTLOCK int,
        primary key (pk)
    );

    create table BooleanExpression (
        id numeric(19,0) identity not null,
        expression text,
        type varchar(255),
        Escalation_Constraints_Id numeric(19,0),
        primary key (id)
    );

    create table Content (
        id numeric(19,0) identity not null,
        content image,
        primary key (id)
    );

    create table ContextMappingInfo (
        mappingId numeric(19,0) identity not null,
        CONTEXT_ID varchar(255) not null,
        KSESSION_ID int not null,
        OWNER_ID varchar(255),
        OPTLOCK int,
        primary key (mappingId)
    );

    create table CorrelationKeyInfo (
        keyId numeric(19,0) identity not null,
        name varchar(255),
        processInstanceId numeric(19,0) not null,
        OPTLOCK int,
        primary key (keyId)
    );

    create table CorrelationPropertyInfo (
        propertyId numeric(19,0) identity not null,
        name varchar(255),
        value varchar(255),
        OPTLOCK int,
        correlationKey_keyId numeric(19,0),
        primary key (propertyId)
    );

    create table Deadline (
        id numeric(19,0) identity not null,
        deadline_date datetime,
        escalated smallint,
        Deadlines_StartDeadLine_Id numeric(19,0),
        Deadlines_EndDeadLine_Id numeric(19,0),
        primary key (id)
    );

    create table Delegation_delegates (
        task_id numeric(19,0) not null,
        entity_id varchar(255) not null
    );

    create table DeploymentStore (
        id numeric(19,0) identity not null,
        attributes varchar(255),
        DEPLOYMENT_ID varchar(255),
        deploymentUnit text,
        state int,
        updateDate datetime,
        primary key (id)
    );

    create table ErrorInfo (
        id numeric(19,0) identity not null,
        message varchar(255),
        stacktrace varchar(5000),
        timestamp datetime,
        REQUEST_ID numeric(19,0) not null,
        primary key (id)
    );

    create table Escalation (
        id numeric(19,0) identity not null,
        name varchar(255),
        Deadline_Escalation_Id numeric(19,0),
        primary key (id)
    );

    create table EventTypes (
        InstanceId numeric(19,0) not null,
        element varchar(255)
    );

    create table I18NText (
        id numeric(19,0) identity not null,
        language varchar(255),
        shortText varchar(255),
        text text,
        Task_Subjects_Id numeric(19,0),
        Task_Names_Id numeric(19,0),
        Task_Descriptions_Id numeric(19,0),
        Reassignment_Documentation_Id numeric(19,0),
        Notification_Subjects_Id numeric(19,0),
        Notification_Names_Id numeric(19,0),
        Notification_Documentation_Id numeric(19,0),
        Notification_Descriptions_Id numeric(19,0),
        Deadline_Documentation_Id numeric(19,0),
        primary key (id)
    );

    create table NodeInstanceLog (
        id numeric(19,0) identity not null,
        connection varchar(255),
        log_date datetime,
        externalId varchar(255),
        nodeId varchar(255),
        nodeInstanceId varchar(255),
        nodeName varchar(255),
        nodeType varchar(255),
        processId varchar(255),
        processInstanceId numeric(19,0) not null,
        type int not null,
        workItemId numeric(19,0),
        primary key (id)
    );

    create table Notification (
        DTYPE varchar(31) not null,
        id numeric(19,0) identity not null,
        priority int not null,
        Escalation_Notifications_Id numeric(19,0),
        primary key (id)
    );

    create table Notification_BAs (
        task_id numeric(19,0) not null,
        entity_id varchar(255) not null
    );

    create table Notification_Recipients (
        task_id numeric(19,0) not null,
        entity_id varchar(255) not null
    );

    create table Notification_email_header (
        Notification_id numeric(19,0) not null,
        emailHeaders_id numeric(19,0) not null,
        mapkey varchar(255) not null,
        primary key (Notification_id, mapkey)
    );

    create table OrganizationalEntity (
        DTYPE varchar(31) not null,
        id varchar(255) not null,
        primary key (id)
    );

    create table PeopleAssignments_BAs (
        task_id numeric(19,0) not null,
        entity_id varchar(255) not null
    );

    create table PeopleAssignments_ExclOwners (
        task_id numeric(19,0) not null,
        entity_id varchar(255) not null
    );

    create table PeopleAssignments_PotOwners (
        task_id numeric(19,0) not null,
        entity_id varchar(255) not null
    );

    create table PeopleAssignments_Recipients (
        task_id numeric(19,0) not null,
        entity_id varchar(255) not null
    );

    create table PeopleAssignments_Stakeholders (
        task_id numeric(19,0) not null,
        entity_id varchar(255) not null
    );

    create table ProcessInstanceInfo (
        InstanceId numeric(19,0) identity not null,
        lastModificationDate datetime,
        lastReadDate datetime,
        processId varchar(255),
        processInstanceByteArray image,
        startDate datetime,
        state int not null,
        OPTLOCK int,
        primary key (InstanceId)
    );

    create table ProcessInstanceLog (
        id numeric(19,0) identity not null,
        duration numeric(19,0),
        end_date datetime,
        externalId varchar(255),
        user_identity varchar(255),
        outcome varchar(255),
        parentProcessInstanceId numeric(19,0),
        processId varchar(255),
        processInstanceId numeric(19,0) not null,
        processName varchar(255),
        processVersion varchar(255),
        start_date datetime,
        status int,
        primary key (id)
    );

    create table Reassignment (
        id numeric(19,0) identity not null,
        Escalation_Reassignments_Id numeric(19,0),
        primary key (id)
    );

    create table Reassignment_potentialOwners (
        task_id numeric(19,0) not null,
        entity_id varchar(255) not null
    );

    create table RequestInfo (
        id numeric(19,0) identity not null,
        commandName varchar(255),
        deploymentId varchar(255),
        executions int not null,
        businessKey varchar(255),
        message varchar(255),
        requestData image,
        responseData image,
        retries int not null,
        status varchar(255),
        timestamp datetime,
        primary key (id)
    );

    create table SessionInfo (
        id int identity not null,
        lastModificationDate datetime,
        rulesByteArray image,
        startDate datetime,
        OPTLOCK int,
        primary key (id)
    );

    create table Task (
        id numeric(19,0) identity not null,
        archived smallint,
        allowedToDelegate varchar(255),
        description varchar(255),
        formName varchar(255),
        name varchar(255),
        priority int not null,
        subTaskStrategy varchar(255),
        subject varchar(255),
        activationTime datetime,
        createdOn datetime,
        deploymentId varchar(255),
        documentAccessType int,
        documentContentId numeric(19,0) not null,
        documentType varchar(255),
        expirationTime datetime,
        faultAccessType int,
        faultContentId numeric(19,0) not null,
        faultName varchar(255),
        faultType varchar(255),
        outputAccessType int,
        outputContentId numeric(19,0) not null,
        outputType varchar(255),
        parentId numeric(19,0) not null,
        previousStatus int,
        processId varchar(255),
        processInstanceId numeric(19,0) not null,
        processSessionId int not null,
        skipable bit not null,
        status varchar(255),
        workItemId numeric(19,0) not null,
        taskType varchar(255),
        OPTLOCK int,
        taskInitiator_id varchar(255),
        actualOwner_id varchar(255),
        createdBy_id varchar(255),
        primary key (id)
    );

    create table TaskDef (
        id numeric(19,0) identity not null,
        name varchar(255),
        priority int not null,
        primary key (id)
    );

    create table TaskEvent (
        id numeric(19,0) identity not null,
        logTime datetime,
        processInstanceId numeric(19,0),
        taskId numeric(19,0),
        type varchar(255),
        userId varchar(255),
        OPTLOCK int,
        workItemId numeric(19,0),
        primary key (id)
    );

    create table VariableInstanceLog (
        id numeric(19,0) identity not null,
        log_date datetime,
        externalId varchar(255),
        oldValue varchar(255),
        processId varchar(255),
        processInstanceId numeric(19,0) not null,
        value varchar(255),
        variableId varchar(255),
        variableInstanceId varchar(255),
        primary key (id)
    );

    create table WorkItemInfo (
        workItemId numeric(19,0) identity not null,
        creationDate datetime,
        name varchar(255),
        processInstanceId numeric(19,0) not null,
        state numeric(19,0) not null,
        OPTLOCK int,
        workItemByteArray image,
        primary key (workItemId)
    );

    create table email_header (
        id numeric(19,0) identity not null,
        body text,
        fromAddress varchar(255),
        language varchar(255),
        replyToAddress varchar(255),
        subject varchar(255),
        primary key (id)
    );

    create table task_comment (
        id numeric(19,0) identity not null,
        addedAt datetime,
        text text,
        addedBy_id varchar(255),
        TaskData_Comments_Id numeric(19,0),
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

    alter table DeploymentStore 
        add constraint UK_DeploymentStore_1 unique (DEPLOYMENT_ID);

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
