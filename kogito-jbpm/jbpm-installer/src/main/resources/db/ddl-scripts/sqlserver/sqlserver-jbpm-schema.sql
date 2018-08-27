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
        processSessionId numeric(19,0) not null,
        status varchar(255),
        taskId numeric(19,0),
        workItemId numeric(19,0),
        lastModificationDate datetime,
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
    
    create table CaseIdInfo (
        id numeric(19,0) identity not null,
        caseIdPrefix varchar(255),
        currentValue numeric(19,0),
        primary key (id)
    );
    
    create table CaseFileDataLog (
        id numeric(19,0) identity not null,
        caseDefId varchar(255),
        caseId varchar(255),
        itemName varchar(255),
        itemType varchar(255),
        itemValue varchar(255),
        lastModified datetime,
        lastModifiedBy varchar(255),
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

    create table Content (
        id numeric(19,0) identity not null,
        content image,
        primary key (id)
    );

    create table ContextMappingInfo (
        mappingId numeric(19,0) identity not null,
        CONTEXT_ID varchar(255) not null,
        KSESSION_ID numeric(19,0) not null,
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
    
    create table ExecutionErrorInfo (
        id numeric(19,0) identity not null,
        ERROR_ACK smallint,
        ERROR_ACK_AT datetime,
        ERROR_ACK_BY varchar(255),
        ACTIVITY_ID numeric(19,0),
        ACTIVITY_NAME varchar(255),
        DEPLOYMENT_ID varchar(255),
        ERROR_INFO text,
        ERROR_DATE datetime,
        ERROR_ID varchar(255),
        ERROR_MSG varchar(255),
        INIT_ACTIVITY_ID numeric(19,0),
        JOB_ID numeric(19,0),
        PROCESS_ID varchar(255),
        PROCESS_INST_ID numeric(19,0),
        ERROR_TYPE varchar(255),
        primary key (id)
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
        sla_due_date datetime,
        slaCompliance int,
        type int not null,
        workItemId numeric(19,0),
        nodeContainerId varchar(255),
        referenceId numeric(19,0),
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
        correlationKey varchar(255),
        duration numeric(19,0),
        end_date datetime,
        externalId varchar(255),
        user_identity varchar(255),
        outcome varchar(255),
        parentProcessInstanceId numeric(19,0),
        processId varchar(255),
        processInstanceDescription varchar(255),
        processInstanceId numeric(19,0) not null,
        processName varchar(255),
        processType int,
        processVersion varchar(255),
        sla_due_date datetime,
        slaCompliance int,
        start_date datetime,
        status int,
        primary key (id)
    );

    create table QueryDefinitionStore (
        id numeric(19,0) identity not null,
        qExpression text,
        qName varchar(255),
        qSource varchar(255),
        qTarget varchar(255),
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
        owner varchar(255),
        priority int not null,
        processInstanceId numeric(19,0),
        requestData image,
        responseData image,
        retries int not null,
        status varchar(255),
        timestamp datetime,
        primary key (id)
    );

    create table SessionInfo (
        id numeric(19,0) identity not null,
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
        processSessionId numeric(19,0) not null,
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
        message varchar(255),
        processInstanceId numeric(19,0),
        taskId numeric(19,0),
        type varchar(255),
        userId varchar(255),
        OPTLOCK int,
        workItemId numeric(19,0),
        primary key (id)
    );

    create table TaskVariableImpl (
        id numeric(19,0) identity not null,
        modificationDate datetime,
        name varchar(255),
        processId varchar(255),
        processInstanceId numeric(19,0),
        taskId numeric(19,0),
        type int,
        value varchar(4000),
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

    alter table DeploymentStore 
        add constraint UK_85rgskt09thd8mkkfl3tb0y81 unique (DEPLOYMENT_ID);

    alter table Notification_email_header 
        add constraint UK_ptaka5kost68h7l3wflv7w6y8 unique (emailHeaders_id);

    alter table QueryDefinitionStore 
        add constraint UK_4ry5gt77jvq0orfttsoghta2j unique (qName);

    alter table Attachment 
        add constraint FK_7ndpfa311i50bq7hy18q05va3 
        foreign key (attachedBy_id) 
        references OrganizationalEntity;

    alter table Attachment 
        add constraint FK_hqupx569krp0f0sgu9kh87513 
        foreign key (TaskData_Attachments_Id) 
        references Task;

    alter table BooleanExpression 
        add constraint FK_394nf2qoc0k9ok6omgd6jtpso 
        foreign key (Escalation_Constraints_Id) 
        references Escalation;
        
    alter table CaseIdInfo 
        add constraint UK_CaseIdInfo_1 unique (caseIdPrefix);        

    alter table CorrelationPropertyInfo 
        add constraint FK_hrmx1m882cejwj9c04ixh50i4 
        foreign key (correlationKey_keyId) 
        references CorrelationKeyInfo;

    alter table Deadline 
        add constraint FK_68w742sge00vco2cq3jhbvmgx 
        foreign key (Deadlines_StartDeadLine_Id) 
        references Task;

    alter table Deadline 
        add constraint FK_euoohoelbqvv94d8a8rcg8s5n 
        foreign key (Deadlines_EndDeadLine_Id) 
        references Task;

    alter table Delegation_delegates 
        add constraint FK_gn7ula51sk55wj1o1m57guqxb 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table Delegation_delegates 
        add constraint FK_fajq6kossbsqwr3opkrctxei3 
        foreign key (task_id) 
        references Task;

    alter table ErrorInfo 
        add constraint FK_cms0met37ggfw5p5gci3otaq0 
        foreign key (REQUEST_ID) 
        references RequestInfo;

    alter table Escalation 
        add constraint FK_ay2gd4fvl9yaapviyxudwuvfg 
        foreign key (Deadline_Escalation_Id) 
        references Deadline;

    alter table EventTypes 
        add constraint FK_nrecj4617iwxlc65ij6m7lsl1 
        foreign key (InstanceId) 
        references ProcessInstanceInfo;

    alter table I18NText 
        add constraint FK_k16jpgrh67ti9uedf6konsu1p 
        foreign key (Task_Subjects_Id) 
        references Task;

    alter table I18NText 
        add constraint FK_fd9uk6hemv2dx1ojovo7ms3vp 
        foreign key (Task_Names_Id) 
        references Task;

    alter table I18NText 
        add constraint FK_4eyfp69ucrron2hr7qx4np2fp 
        foreign key (Task_Descriptions_Id) 
        references Task;

    alter table I18NText 
        add constraint FK_pqarjvvnwfjpeyb87yd7m0bfi 
        foreign key (Reassignment_Documentation_Id) 
        references Reassignment;

    alter table I18NText 
        add constraint FK_o84rkh69r47ti8uv4eyj7bmo2 
        foreign key (Notification_Subjects_Id) 
        references Notification;

    alter table I18NText 
        add constraint FK_g1trxri8w64enudw2t1qahhk5 
        foreign key (Notification_Names_Id) 
        references Notification;

    alter table I18NText 
        add constraint FK_qoce92c70adem3ccb3i7lec8x 
        foreign key (Notification_Documentation_Id) 
        references Notification;

    alter table I18NText 
        add constraint FK_bw8vmpekejxt1ei2ge26gdsry 
        foreign key (Notification_Descriptions_Id) 
        references Notification;

    alter table I18NText 
        add constraint FK_21qvifarxsvuxeaw5sxwh473w 
        foreign key (Deadline_Documentation_Id) 
        references Deadline;

    alter table Notification 
        add constraint FK_bdbeml3768go5im41cgfpyso9 
        foreign key (Escalation_Notifications_Id) 
        references Escalation;

    alter table Notification_BAs 
        add constraint FK_mfbsnbrhth4rjhqc2ud338s4i 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table Notification_BAs 
        add constraint FK_fc0uuy76t2bvxaxqysoo8xts7 
        foreign key (task_id) 
        references Notification;

    alter table Notification_Recipients 
        add constraint FK_blf9jsrumtrthdaqnpwxt25eu 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table Notification_Recipients 
        add constraint FK_3l244pj8sh78vtn9imaymrg47 
        foreign key (task_id) 
        references Notification;

    alter table Notification_email_header 
        add constraint FK_ptaka5kost68h7l3wflv7w6y8 
        foreign key (emailHeaders_id) 
        references email_header;

    alter table Notification_email_header 
        add constraint FK_eth4nvxn21fk1vnju85vkjrai 
        foreign key (Notification_id) 
        references Notification;

    alter table PeopleAssignments_BAs 
        add constraint FK_t38xbkrq6cppifnxequhvjsl2 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table PeopleAssignments_BAs 
        add constraint FK_omjg5qh7uv8e9bolbaq7hv6oh 
        foreign key (task_id) 
        references Task;

    alter table PeopleAssignments_ExclOwners 
        add constraint FK_pth28a73rj6bxtlfc69kmqo0a 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table PeopleAssignments_ExclOwners 
        add constraint FK_b8owuxfrdng050ugpk0pdowa7 
        foreign key (task_id) 
        references Task;

    alter table PeopleAssignments_PotOwners 
        add constraint FK_tee3ftir7xs6eo3fdvi3xw026 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table PeopleAssignments_PotOwners 
        add constraint FK_4dv2oji7pr35ru0w45trix02x 
        foreign key (task_id) 
        references Task;

    alter table PeopleAssignments_Recipients 
        add constraint FK_4g7y3wx6gnokf6vycgpxs83d6 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table PeopleAssignments_Recipients 
        add constraint FK_enhk831fghf6akjilfn58okl4 
        foreign key (task_id) 
        references Task;

    alter table PeopleAssignments_Stakeholders 
        add constraint FK_met63inaep6cq4ofb3nnxi4tm 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table PeopleAssignments_Stakeholders 
        add constraint FK_4bh3ay74x6ql9usunubttfdf1 
        foreign key (task_id) 
        references Task;

    alter table Reassignment 
        add constraint FK_pnpeue9hs6kx2ep0sp16b6kfd 
        foreign key (Escalation_Reassignments_Id) 
        references Escalation;

    alter table Reassignment_potentialOwners 
        add constraint FK_8frl6la7tgparlnukhp8xmody 
        foreign key (entity_id) 
        references OrganizationalEntity;

    alter table Reassignment_potentialOwners 
        add constraint FK_qbega5ncu6b9yigwlw55aeijn 
        foreign key (task_id) 
        references Reassignment;

    alter table Task 
        add constraint FK_dpk0f9ucm14c78bsxthh7h8yh 
        foreign key (taskInitiator_id) 
        references OrganizationalEntity;

    alter table Task 
        add constraint FK_nh9nnt47f3l61qjlyedqt05rf 
        foreign key (actualOwner_id) 
        references OrganizationalEntity;

    alter table Task 
        add constraint FK_k02og0u71obf1uxgcdjx9rcjc 
        foreign key (createdBy_id) 
        references OrganizationalEntity;

    alter table task_comment 
        add constraint FK_aax378yjnsmw9kb9vsu994jjv 
        foreign key (addedBy_id) 
        references OrganizationalEntity;

    alter table task_comment 
        add constraint FK_1ws9jdmhtey6mxu7jb0r0ufvs 
        foreign key (TaskData_Comments_Id) 
        references Task;

        
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
    create index IDX_I18NText_NotNamId ON I18NText(Notification_Names_Id);
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

    create index IDX_VInstLog_pInstId on VariableInstanceLog(processInstanceId);
    create index IDX_VInstLog_varId on VariableInstanceLog(variableId);
    create index IDX_VInstLog_pId on VariableInstanceLog(processId);

    create index IDX_NInstLog_pInstId on NodeInstanceLog(processInstanceId);
    create index IDX_NInstLog_nodeType on NodeInstanceLog(nodeType);
    create index IDX_NInstLog_pId on NodeInstanceLog(processId);

    create index IDX_ErrorInfo_pInstId on ExecutionErrorInfo(PROCESS_INST_ID);
    create index IDX_ErrorInfo_errorAck on ExecutionErrorInfo(ERROR_ACK);

    create index IDX_AuditTaskImpl_taskId on AuditTaskImpl(taskId);
    create index IDX_AuditTaskImpl_pInstId on AuditTaskImpl(processInstanceId);
    create index IDX_AuditTaskImpl_workItemId on AuditTaskImpl(workItemId);
    create index IDX_AuditTaskImpl_name on AuditTaskImpl(name);
    create index IDX_AuditTaskImpl_processId on AuditTaskImpl(processId);
    create index IDX_AuditTaskImpl_status on AuditTaskImpl(status);