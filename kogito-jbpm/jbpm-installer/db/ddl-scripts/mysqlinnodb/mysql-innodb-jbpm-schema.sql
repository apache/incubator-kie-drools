    create table Attachment (
        id bigint not null auto_increment,
        accessType integer,
        attachedAt datetime,
        attachmentContentId bigint not null,
        contentType varchar(255),
        name varchar(255),
        attachment_size integer,
        attachedBy_id varchar(255),
        TaskData_Attachments_Id bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table AuditTaskImpl (
        id bigint not null auto_increment,
        activationTime datetime,
        actualOwner varchar(255),
        createdBy varchar(255),
        createdOn datetime,
        deploymentId varchar(255),
        description varchar(255),
        dueDate datetime,
        name varchar(255),
        parentId bigint not null,
        priority integer not null,
        processId varchar(255),
        processInstanceId bigint not null,
        processSessionId bigint not null,
        status varchar(255),
        taskId bigint,
        workItemId bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table BAMTaskSummary (
        pk bigint not null auto_increment,
        createdDate datetime,
        duration bigint,
        endDate datetime,
        processInstanceId bigint not null,
        startDate datetime,
        status varchar(255),
        taskId bigint not null,
        taskName varchar(255),
        userId varchar(255),
        OPTLOCK integer,
        primary key (pk)
    ) ENGINE=InnoDB;

    create table BooleanExpression (
        id bigint not null auto_increment,
        expression longtext,
        type varchar(255),
        Escalation_Constraints_Id bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table Content (
        id bigint not null auto_increment,
        content longblob,
        primary key (id)
    ) ENGINE=InnoDB;

    create table ContextMappingInfo (
        mappingId bigint not null auto_increment,
        CONTEXT_ID varchar(255) not null,
        KSESSION_ID bigint not null,
        OWNER_ID varchar(255),
        OPTLOCK integer,
        primary key (mappingId)
    ) ENGINE=InnoDB;

    create table CorrelationKeyInfo (
        keyId bigint not null auto_increment,
        name varchar(255),
        processInstanceId bigint not null,
        OPTLOCK integer,
        primary key (keyId)
    ) ENGINE=InnoDB;

    create table CorrelationPropertyInfo (
        propertyId bigint not null auto_increment,
        name varchar(255),
        value varchar(255),
        OPTLOCK integer,
        correlationKey_keyId bigint,
        primary key (propertyId)
    ) ENGINE=InnoDB;

    create table Deadline (
        id bigint not null auto_increment,
        deadline_date datetime,
        escalated smallint,
        Deadlines_StartDeadLine_Id bigint,
        Deadlines_EndDeadLine_Id bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table Delegation_delegates (
        task_id bigint not null,
        entity_id varchar(255) not null
    ) ENGINE=InnoDB;

    create table DeploymentStore (
        id bigint not null auto_increment,
        attributes varchar(255),
        DEPLOYMENT_ID varchar(255),
        deploymentUnit longtext,
        state integer,
        updateDate datetime,
        primary key (id)
    ) ENGINE=InnoDB;

    create table ErrorInfo (
        id bigint not null auto_increment,
        message varchar(255),
        stacktrace varchar(5000),
        timestamp datetime,
        REQUEST_ID bigint not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table Escalation (
        id bigint not null auto_increment,
        name varchar(255),
        Deadline_Escalation_Id bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table EventTypes (
        InstanceId bigint not null,
        element varchar(255)
    ) ENGINE=InnoDB;

    create table I18NText (
        id bigint not null auto_increment,
        language varchar(255),
        shortText varchar(255),
        text longtext,
        Task_Subjects_Id bigint,
        Task_Names_Id bigint,
        Task_Descriptions_Id bigint,
        Reassignment_Documentation_Id bigint,
        Notification_Subjects_Id bigint,
        Notification_Names_Id bigint,
        Notification_Documentation_Id bigint,
        Notification_Descriptions_Id bigint,
        Deadline_Documentation_Id bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table NodeInstanceLog (
        id bigint not null auto_increment,
        connection varchar(255),
        log_date datetime,
        externalId varchar(255),
        nodeId varchar(255),
        nodeInstanceId varchar(255),
        nodeName varchar(255),
        nodeType varchar(255),
        processId varchar(255),
        processInstanceId bigint not null,
        type integer not null,
        workItemId bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table Notification (
        DTYPE varchar(31) not null,
        id bigint not null auto_increment,
        priority integer not null,
        Escalation_Notifications_Id bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table Notification_BAs (
        task_id bigint not null,
        entity_id varchar(255) not null
    ) ENGINE=InnoDB;

    create table Notification_Recipients (
        task_id bigint not null,
        entity_id varchar(255) not null
    ) ENGINE=InnoDB;

    create table Notification_email_header (
        Notification_id bigint not null,
        emailHeaders_id bigint not null,
        mapkey varchar(255) not null,
        primary key (Notification_id, mapkey)
    ) ENGINE=InnoDB;

    create table OrganizationalEntity (
        DTYPE varchar(31) not null,
        id varchar(255) not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table PeopleAssignments_BAs (
        task_id bigint not null,
        entity_id varchar(255) not null
    ) ENGINE=InnoDB;

    create table PeopleAssignments_ExclOwners (
        task_id bigint not null,
        entity_id varchar(255) not null
    ) ENGINE=InnoDB;

    create table PeopleAssignments_PotOwners (
        task_id bigint not null,
        entity_id varchar(255) not null
    ) ENGINE=InnoDB;

    create table PeopleAssignments_Recipients (
        task_id bigint not null,
        entity_id varchar(255) not null
    ) ENGINE=InnoDB;

    create table PeopleAssignments_Stakeholders (
        task_id bigint not null,
        entity_id varchar(255) not null
    ) ENGINE=InnoDB;

    create table ProcessInstanceInfo (
        InstanceId bigint not null auto_increment,
        lastModificationDate datetime,
        lastReadDate datetime,
        processId varchar(255),
        processInstanceByteArray longblob,
        startDate datetime,
        state integer not null,
        OPTLOCK integer,
        primary key (InstanceId)
    ) ENGINE=InnoDB;

    create table ProcessInstanceLog (
        id bigint not null auto_increment,
        duration bigint,
        end_date datetime,
        externalId varchar(255),
        user_identity varchar(255),
        outcome varchar(255),
        parentProcessInstanceId bigint,
        processId varchar(255),
        processInstanceDescription varchar(255),
        correlationKey varchar(255),
        processInstanceId bigint not null,
        processName varchar(255),
        processVersion varchar(255),
        start_date datetime,
        status integer,
        primary key (id)
    ) ENGINE=InnoDB;

    create table Reassignment (
        id bigint not null auto_increment,
        Escalation_Reassignments_Id bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table Reassignment_potentialOwners (
        task_id bigint not null,
        entity_id varchar(255) not null
    ) ENGINE=InnoDB;

    create table RequestInfo (
        id bigint not null auto_increment,
        commandName varchar(255),
        deploymentId varchar(255),
        executions integer not null,
        businessKey varchar(255),
        message varchar(255),
        owner varchar(255),
        requestData longblob,
        responseData longblob,
        retries integer not null,
        status varchar(255),
        timestamp datetime,
        primary key (id)
    ) ENGINE=InnoDB;

    create table SessionInfo (
        id bigint not null auto_increment,
        lastModificationDate datetime,
        rulesByteArray longblob,
        startDate datetime,
        OPTLOCK integer,
        primary key (id)
    ) ENGINE=InnoDB;

    create table Task (
        id bigint not null auto_increment,
        archived smallint,
        allowedToDelegate varchar(255),
        description varchar(255),
        formName varchar(255),
        name varchar(255),
        priority integer not null,
        subTaskStrategy varchar(255),
        subject varchar(255),
        activationTime datetime,
        createdOn datetime,
        deploymentId varchar(255),
        documentAccessType integer,
        documentContentId bigint not null,
        documentType varchar(255),
        expirationTime datetime,
        faultAccessType integer,
        faultContentId bigint not null,
        faultName varchar(255),
        faultType varchar(255),
        outputAccessType integer,
        outputContentId bigint not null,
        outputType varchar(255),
        parentId bigint not null,
        previousStatus integer,
        processId varchar(255),
        processInstanceId bigint not null,
        processSessionId bigint not null,
        skipable boolean not null,
        status varchar(255),
        workItemId bigint not null,
        taskType varchar(255),
        OPTLOCK integer,
        taskInitiator_id varchar(255),
        actualOwner_id varchar(255),
        createdBy_id varchar(255),
        primary key (id)
    ) ENGINE=InnoDB;

    create table TaskDef (
        id bigint not null auto_increment,
        name varchar(255),
        priority integer not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table TaskEvent (
        id bigint not null auto_increment,
        logTime datetime,
        processInstanceId bigint,
        taskId bigint,
        type varchar(255),
        userId varchar(255),
        message varchar(255),
        OPTLOCK integer,
        workItemId bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table TaskVariableImpl (
        id bigint not null auto_increment,
        modificationDate datetime,
        name varchar(255),
        processId varchar(255),
        processInstanceId bigint,
        taskId bigint,
        type integer,
        value varchar(255),
        primary key (id)
    ) ENGINE=InnoDB;

    create table VariableInstanceLog (
        id bigint not null auto_increment,
        log_date datetime,
        externalId varchar(255),
        oldValue varchar(255),
        processId varchar(255),
        processInstanceId bigint not null,
        value varchar(255),
        variableId varchar(255),
        variableInstanceId varchar(255),
        primary key (id)
    ) ENGINE=InnoDB;

    create table WorkItemInfo (
        workItemId bigint not null auto_increment,
        creationDate datetime,
        name varchar(255),
        processInstanceId bigint not null,
        state bigint not null,
        OPTLOCK integer,
        workItemByteArray longblob,
        primary key (workItemId)
    ) ENGINE=InnoDB;

    create table email_header (
        id bigint not null auto_increment,
        body longtext,
        fromAddress varchar(255),
        language varchar(255),
        replyToAddress varchar(255),
        subject varchar(255),
        primary key (id)
    ) ENGINE=InnoDB;

    create table task_comment (
        id bigint not null auto_increment,
        addedAt datetime,
        text longtext,
        addedBy_id varchar(255),
        TaskData_Comments_Id bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    alter table Attachment 
        add index FK1C93543D937BFB5 (attachedBy_id), 
        add constraint FK1C93543D937BFB5 
        foreign key (attachedBy_id) 
        references OrganizationalEntity (id);

    alter table Attachment 
        add index FK1C9354333CA892A (TaskData_Attachments_Id), 
        add constraint FK1C9354333CA892A 
        foreign key (TaskData_Attachments_Id) 
        references Task (id);

    alter table BooleanExpression 
        add index FKE3D208C06C97C90E (Escalation_Constraints_Id), 
        add constraint FKE3D208C06C97C90E 
        foreign key (Escalation_Constraints_Id) 
        references Escalation (id);

    alter table CorrelationPropertyInfo 
        add index FK761452A5D87156ED (correlationKey_keyId), 
        add constraint FK761452A5D87156ED 
        foreign key (correlationKey_keyId) 
        references CorrelationKeyInfo (keyId);

    alter table Deadline 
        add index FK21DF3E78A9FE0EF4 (Deadlines_StartDeadLine_Id), 
        add constraint FK21DF3E78A9FE0EF4 
        foreign key (Deadlines_StartDeadLine_Id) 
        references Task (id);

    alter table Deadline 
        add index FK21DF3E78695E4DDB (Deadlines_EndDeadLine_Id), 
        add constraint FK21DF3E78695E4DDB 
        foreign key (Deadlines_EndDeadLine_Id) 
        references Task (id);

    alter table Delegation_delegates 
        add index FK47485D5772B3A123 (entity_id), 
        add constraint FK47485D5772B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table Delegation_delegates 
        add index FK47485D57786553A5 (task_id), 
        add constraint FK47485D57786553A5 
        foreign key (task_id) 
        references Task (id);

    alter table DeploymentStore 
        add constraint UK_DeploymentStore_1 unique (DEPLOYMENT_ID);

    alter table ErrorInfo 
        add index FK8B1186B6724A467 (REQUEST_ID), 
        add constraint FK8B1186B6724A467 
        foreign key (REQUEST_ID) 
        references RequestInfo (id);

    alter table Escalation 
        add index FK67B2C6B5D1E5CC1 (Deadline_Escalation_Id), 
        add constraint FK67B2C6B5D1E5CC1 
        foreign key (Deadline_Escalation_Id) 
        references Deadline (id);

    alter table EventTypes 
        add index FKB0E5621F7665489A (InstanceId), 
        add constraint FKB0E5621F7665489A 
        foreign key (InstanceId) 
        references ProcessInstanceInfo (InstanceId);

    alter table I18NText 
        add index FK2349686BF4ACCD69 (Task_Subjects_Id), 
        add constraint FK2349686BF4ACCD69 
        foreign key (Task_Subjects_Id) 
        references Task (id);

    alter table I18NText 
        add index FK2349686B424B187C (Task_Names_Id), 
        add constraint FK2349686B424B187C 
        foreign key (Task_Names_Id) 
        references Task (id);

    alter table I18NText 
        add index FK2349686BAB648139 (Task_Descriptions_Id), 
        add constraint FK2349686BAB648139 
        foreign key (Task_Descriptions_Id) 
        references Task (id);

    alter table I18NText 
        add index FK2349686BB340A2AA (Reassignment_Documentation_Id), 
        add constraint FK2349686BB340A2AA 
        foreign key (Reassignment_Documentation_Id) 
        references Reassignment (id);

    alter table I18NText 
        add index FK2349686BF0CDED35 (Notification_Subjects_Id), 
        add constraint FK2349686BF0CDED35 
        foreign key (Notification_Subjects_Id) 
        references Notification (id);

    alter table I18NText 
        add index FK2349686BCC03ED3C (Notification_Names_Id), 
        add constraint FK2349686BCC03ED3C 
        foreign key (Notification_Names_Id) 
        references Notification (id);

    alter table I18NText 
        add index FK2349686B77C1C08A (Notification_Documentation_Id), 
        add constraint FK2349686B77C1C08A 
        foreign key (Notification_Documentation_Id) 
        references Notification (id);

    alter table I18NText 
        add index FK2349686B18DDFE05 (Notification_Descriptions_Id), 
        add constraint FK2349686B18DDFE05 
        foreign key (Notification_Descriptions_Id) 
        references Notification (id);

    alter table I18NText 
        add index FK2349686B78AF072A (Deadline_Documentation_Id), 
        add constraint FK2349686B78AF072A 
        foreign key (Deadline_Documentation_Id) 
        references Deadline (id);

    alter table Notification 
        add index FK2D45DD0BC0C0F29C (Escalation_Notifications_Id), 
        add constraint FK2D45DD0BC0C0F29C 
        foreign key (Escalation_Notifications_Id) 
        references Escalation (id);

    alter table Notification_BAs 
        add index FK2DD68EE072B3A123 (entity_id), 
        add constraint FK2DD68EE072B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table Notification_BAs 
        add index FK2DD68EE093F2090B (task_id), 
        add constraint FK2DD68EE093F2090B 
        foreign key (task_id) 
        references Notification (id);

    alter table Notification_Recipients 
        add index FK98FD214E72B3A123 (entity_id), 
        add constraint FK98FD214E72B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table Notification_Recipients 
        add index FK98FD214E93F2090B (task_id), 
        add constraint FK98FD214E93F2090B 
        foreign key (task_id) 
        references Notification (id);

    alter table Notification_email_header 
        add constraint UK_F30FE3446CEA0510 unique (emailHeaders_id);

    alter table Notification_email_header 
        add index FKF30FE3448BED1339 (emailHeaders_id), 
        add constraint FKF30FE3448BED1339 
        foreign key (emailHeaders_id) 
        references email_header (id);

    alter table Notification_email_header 
        add index FKF30FE3443E3E97EB (Notification_id), 
        add constraint FKF30FE3443E3E97EB 
        foreign key (Notification_id) 
        references Notification (id);

    alter table PeopleAssignments_BAs 
        add index FK9D8CF4EC72B3A123 (entity_id), 
        add constraint FK9D8CF4EC72B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table PeopleAssignments_BAs 
        add index FK9D8CF4EC786553A5 (task_id), 
        add constraint FK9D8CF4EC786553A5 
        foreign key (task_id) 
        references Task (id);

    alter table PeopleAssignments_ExclOwners 
        add index FKC77B97E472B3A123 (entity_id), 
        add constraint FKC77B97E472B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table PeopleAssignments_ExclOwners 
        add index FKC77B97E4786553A5 (task_id), 
        add constraint FKC77B97E4786553A5 
        foreign key (task_id) 
        references Task (id);

    alter table PeopleAssignments_PotOwners 
        add index FK1EE418D72B3A123 (entity_id), 
        add constraint FK1EE418D72B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table PeopleAssignments_PotOwners 
        add index FK1EE418D786553A5 (task_id), 
        add constraint FK1EE418D786553A5 
        foreign key (task_id) 
        references Task (id);

    alter table PeopleAssignments_Recipients 
        add index FKC6F615C272B3A123 (entity_id), 
        add constraint FKC6F615C272B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table PeopleAssignments_Recipients 
        add index FKC6F615C2786553A5 (task_id), 
        add constraint FKC6F615C2786553A5 
        foreign key (task_id) 
        references Task (id);

    alter table PeopleAssignments_Stakeholders 
        add index FK482F79D572B3A123 (entity_id), 
        add constraint FK482F79D572B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table PeopleAssignments_Stakeholders 
        add index FK482F79D5786553A5 (task_id), 
        add constraint FK482F79D5786553A5 
        foreign key (task_id) 
        references Task (id);

    alter table Reassignment 
        add index FK724D056062A1E871 (Escalation_Reassignments_Id), 
        add constraint FK724D056062A1E871 
        foreign key (Escalation_Reassignments_Id) 
        references Escalation (id);

    alter table Reassignment_potentialOwners 
        add index FK90B59CFF72B3A123 (entity_id), 
        add constraint FK90B59CFF72B3A123 
        foreign key (entity_id) 
        references OrganizationalEntity (id);

    alter table Reassignment_potentialOwners 
        add index FK90B59CFF35D2FEE0 (task_id), 
        add constraint FK90B59CFF35D2FEE0 
        foreign key (task_id) 
        references Reassignment (id);

    alter table Task 
        add index FK27A9A53C55C806 (taskInitiator_id), 
        add constraint FK27A9A53C55C806 
        foreign key (taskInitiator_id) 
        references OrganizationalEntity (id);

    alter table Task 
        add index FK27A9A5B723BE8B (actualOwner_id), 
        add constraint FK27A9A5B723BE8B 
        foreign key (actualOwner_id) 
        references OrganizationalEntity (id);

    alter table Task 
        add index FK27A9A55427E8F1 (createdBy_id), 
        add constraint FK27A9A55427E8F1 
        foreign key (createdBy_id) 
        references OrganizationalEntity (id);

    alter table task_comment 
        add index FK61F475A57A3215D9 (addedBy_id), 
        add constraint FK61F475A57A3215D9 
        foreign key (addedBy_id) 
        references OrganizationalEntity (id);

    alter table task_comment 
        add index FK61F475A5F510CB46 (TaskData_Comments_Id), 
        add constraint FK61F475A5F510CB46 
        foreign key (TaskData_Comments_Id) 
        references Task (id);

        
    create index IDX_Attachment_Id ON Attachment(attachedBy_id);
    create index IDX_Attachment_DataId ON Attachment(TaskData_Attachments_Id);
    create index IDX_BoolExpr_Id ON BooleanExpression(Escalation_Constraints_Id);
    create index IDX_CorrPropInfo_Id ON CorrelationPropertyInfo(correlationKey_keyId);
    create index IDX_Deadline_StartId ON Deadline(Deadlines_StartDeadLine_Id);
    create index IDX_Deadline_EndId ON Deadline(Deadlines_EndDeadLine_Id);
    create index IDX_Delegation_EntityId ON Delegation_delegates(entity_id);
    create index IDX_Delegation_TaskId ON Delegation_delegates(task_id);
    create index IDX_ErrorInfo_Id ON ErrorInfo(REQUEST_ID);
    create index IDX_Escalation_Id ON Escalation(Deadline_Escalation_Id);
    create index IDX_EventTypes_Id ON EventTypes(InstanceId);
    create index IDX_I18NText_SubjId ON I18NText(Task_Subjects_Id);
    create index IDX_I18NText_NameId ON I18NText(Task_Names_Id);
    create index IDX_I18NText_DescrId ON I18NText(Task_Descriptions_Id);
    create index IDX_I18NText_ReassignId ON I18NText(Reassignment_Documentation_Id);
    create index IDX_I18NText_NotSubjId ON I18NText(Notification_Subjects_Id);
    create index IDX_I18NText_NotDocId ON I18NText(Notification_Documentation_Id);
    create index IDX_I18NText_NotDescrId ON I18NText(Notification_Descriptions_Id);
    create index IDX_I18NText_DeadDocId ON I18NText(Deadline_Documentation_Id);
    create index IDX_Not_EscId ON Notification(Escalation_Notifications_Id);
    create index IDX_NotBAs_Entity ON Notification_BAs(entity_id);
    create index IDX_NotBAs_Task ON Notification_BAs(task_id);
    create index IDX_NotRec_Entity ON Notification_Recipients(entity_id);
    create index IDX_NotRec_Task ON Notification_Recipients(task_id);
    create index IDX_NotEmail_Header ON Notification_email_header(emailHeaders_id);
    create index IDX_NotEmail_Not ON Notification_email_header(Notification_id);
    create index IDX_PAsBAs_Entity ON PeopleAssignments_BAs(entity_id);
    create index IDX_PAsBAs_Task ON PeopleAssignments_BAs(task_id);
    create index IDX_PAsExcl_Entity ON PeopleAssignments_ExclOwners(entity_id);
    create index IDX_PAsExcl_Task ON PeopleAssignments_ExclOwners(task_id);
    create index IDX_PAsPot_Entity ON PeopleAssignments_PotOwners(entity_id);
    create index IDX_PAsPot_Task ON PeopleAssignments_PotOwners(task_id);
    create index IDX_PAsRecip_Entity ON PeopleAssignments_Recipients(entity_id);
    create index IDX_PAsRecip_Task ON PeopleAssignments_Recipients(task_id);
    create index IDX_PAsStake_Entity ON PeopleAssignments_Stakeholders(entity_id);
    create index IDX_PAsStake_Task ON PeopleAssignments_Stakeholders(task_id);
    create index IDX_Reassign_Esc ON Reassignment(Escalation_Reassignments_Id);
    create index IDX_ReassignPO_Entity ON Reassignment_potentialOwners(entity_id);
    create index IDX_ReassignPO_Task ON Reassignment_potentialOwners(task_id);
    create index IDX_Task_Initiator ON Task(taskInitiator_id);
    create index IDX_Task_ActualOwner ON Task(actualOwner_id);
    create index IDX_Task_CreatedBy ON Task(createdBy_id);
    create index IDX_TaskComments_CreatedBy ON task_comment(addedBy_id);
    create index IDX_TaskComments_Id ON task_comment(TaskData_Comments_Id);
        
    create index IDX_Task_processInstanceId on Task(processInstanceId);
    create index IDX_Task_processId on Task(processId);
    create index IDX_Task_status on Task(status);
    create index IDX_Task_archived on Task(archived);
    create index IDX_Task_workItemId on Task(workItemId);
    
    create index IDX_EventTypes_element ON EventTypes(element);

    create index IDX_CMI_Context ON ContextMappingInfo(CONTEXT_ID);    
    create index IDX_CMI_KSession ON ContextMappingInfo(KSESSION_ID);    
    create index IDX_CMI_Owner ON ContextMappingInfo(OWNER_ID);
    
    create index IDX_RequestInfo_status ON RequestInfo(status);
    create index IDX_RequestInfo_timestamp ON RequestInfo(timestamp);
    create index IDX_RequestInfo_owner ON RequestInfo(owner);
    
    create index IDX_BAMTaskSumm_createdDate on BAMTaskSummary(createdDate);
    create index IDX_BAMTaskSumm_duration on BAMTaskSummary(duration);
    create index IDX_BAMTaskSumm_endDate on BAMTaskSummary(endDate);
    create index IDX_BAMTaskSumm_pInstId on BAMTaskSummary(processInstanceId);
    create index IDX_BAMTaskSumm_startDate on BAMTaskSummary(startDate);
    create index IDX_BAMTaskSumm_status on BAMTaskSummary(status);
    create index IDX_BAMTaskSumm_taskId on BAMTaskSummary(taskId);
    create index IDX_BAMTaskSumm_taskName on BAMTaskSummary(taskName);
    create index IDX_BAMTaskSumm_userId on BAMTaskSummary(userId);
    
    create index IDX_PInstLog_duration on ProcessInstanceLog(duration);
    create index IDX_PInstLog_end_date on ProcessInstanceLog(end_date);
    create index IDX_PInstLog_extId on ProcessInstanceLog(externalId);
    create index IDX_PInstLog_user_identity on ProcessInstanceLog(user_identity);
    create index IDX_PInstLog_outcome on ProcessInstanceLog(outcome);
    create index IDX_PInstLog_parentPInstId on ProcessInstanceLog(parentProcessInstanceId);
    create index IDX_PInstLog_pId on ProcessInstanceLog(processId);
    create index IDX_PInstLog_pInsteDescr on ProcessInstanceLog(processInstanceDescription);
    create index IDX_PInstLog_pInstId on ProcessInstanceLog(processInstanceId);
    create index IDX_PInstLog_pName on ProcessInstanceLog(processName);
    create index IDX_PInstLog_pVersion on ProcessInstanceLog(processVersion);
    create index IDX_PInstLog_start_date on ProcessInstanceLog(start_date);
    create index IDX_PInstLog_status on ProcessInstanceLog(status);
    create index IDX_PInstLog_correlation on ProcessInstanceLog(correlationKey);