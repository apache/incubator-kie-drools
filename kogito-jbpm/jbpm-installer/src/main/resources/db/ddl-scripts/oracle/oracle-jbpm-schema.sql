    create table Attachment (
        id number(19,0) not null,
        accessType number(10,0),
        attachedAt timestamp,
        attachmentContentId number(19,0) not null,
        contentType varchar2(255 char),
        name varchar2(255 char),
        attachment_size number(10,0),
        attachedBy_id varchar2(255 char),
        TaskData_Attachments_Id number(19,0),
        primary key (id)
    );

    create table AuditTaskImpl (
        id number(19,0) not null,
        activationTime timestamp,
        actualOwner varchar2(255 char),
        createdBy varchar2(255 char),
        createdOn timestamp,
        deploymentId varchar2(255 char),
        description varchar2(255 char),
        dueDate timestamp,
        name varchar2(255 char),
        parentId number(19,0) not null,
        priority number(10,0) not null,
        processId varchar2(255 char),
        processInstanceId number(19,0) not null,
        processSessionId number(19,0) not null,
        status varchar2(255 char),
        taskId number(19,0),
        workItemId number(19,0),
        lastModificationDate timestamp,
        primary key (id)
    );

    create table BAMTaskSummary (
        pk number(19,0) not null,
        createdDate timestamp,
        duration number(19,0),
        endDate timestamp,
        processInstanceId number(19,0) not null,
        startDate timestamp,
        status varchar2(255 char),
        taskId number(19,0) not null,
        taskName varchar2(255 char),
        userId varchar2(255 char),
        OPTLOCK number(10,0),
        primary key (pk)
    );

    create table BooleanExpression (
        id number(19,0) not null,
        expression clob,
        type varchar2(255 char),
        Escalation_Constraints_Id number(19,0),
        primary key (id)
    );
    
    create table CaseIdInfo (
        id number(19,0) not null,
        caseIdPrefix varchar2(255 char),
        currentValue number(19,0),
        primary key (id)
    );
    
    create table CaseFileDataLog (
        id number(19,0) not null,
        caseDefId varchar2(255 char),
        caseId varchar2(255 char),
        itemName varchar2(255 char),
        itemType varchar2(255 char),
        itemValue varchar2(255 char),
        lastModified timestamp,
        lastModifiedBy varchar2(255 char),
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

    create table Content (
        id number(19,0) not null,
        content blob,
        primary key (id)
    );

    create table ContextMappingInfo (
        mappingId number(19,0) not null,
        CONTEXT_ID varchar2(255 char) not null,
        KSESSION_ID number(19,0) not null,
        OWNER_ID varchar2(255 char),
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

    create table DeploymentStore (
        id number(19,0) not null,
        attributes varchar2(255 char),
        DEPLOYMENT_ID varchar2(255 char),
        deploymentUnit clob,
        state number(10,0),
        updateDate timestamp,
        primary key (id)
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
        element varchar2(255 char)
    );
    
    create table ExecutionErrorInfo (
        id number(19,0) not null,
        ERROR_ACK number(5,0),
        ERROR_ACK_AT timestamp,
        ERROR_ACK_BY varchar2(255 char),
        ACTIVITY_ID number(19,0),
        ACTIVITY_NAME varchar2(255 char),
        DEPLOYMENT_ID varchar2(255 char),
        ERROR_INFO clob,
        ERROR_DATE timestamp,
        ERROR_ID varchar2(255 char),
        ERROR_MSG varchar2(255 char),
        INIT_ACTIVITY_ID number(19,0),
        JOB_ID number(19,0),
        PROCESS_ID varchar2(255 char),
        PROCESS_INST_ID number(19,0),
        ERROR_TYPE varchar2(255 char),
        primary key (id)
    );

    create table I18NText (
        id number(19,0) not null,
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
        primary key (id)
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
        sla_due_date timestamp,
        slaCompliance number(10,0),
        type number(10,0) not null,
        workItemId number(19,0),
        nodeContainerId varchar2(255 char),
        referenceId number(19,0),
        primary key (id)
    );

    create table Notification (
        DTYPE varchar2(31 char) not null,
        id number(19,0) not null,
        priority number(10,0) not null,
        Escalation_Notifications_Id number(19,0),
        primary key (id)
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
        Notification_id number(19,0) not null,
        emailHeaders_id number(19,0) not null,
        mapkey varchar2(255 char) not null,
        primary key (Notification_id, mapkey)
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
        correlationKey varchar2(255 char),
        duration number(19,0),
        end_date timestamp,
        externalId varchar2(255 char),
        user_identity varchar2(255 char),
        outcome varchar2(255 char),
        parentProcessInstanceId number(19,0),
        processId varchar2(255 char),
        processInstanceDescription varchar2(255 char),
        processInstanceId number(19,0) not null,
        processName varchar2(255 char),
        processType number(10,0),
        processVersion varchar2(255 char),
        sla_due_date timestamp,
        slaCompliance number(10,0),
        start_date timestamp,
        status number(10,0),
        primary key (id)
    );

    create table QueryDefinitionStore (
        id number(19,0) not null,
        qExpression clob,
        qName varchar2(255 char),
        qSource varchar2(255 char),
        qTarget varchar2(255 char),
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
        owner varchar2(255 char),
        priority number(10,0) not null,
        processInstanceId number(19,0),
        requestData blob,
        responseData blob,
        retries number(10,0) not null,
        status varchar2(255 char),
        timestamp timestamp,
        primary key (id)
    );

    create table SessionInfo (
        id number(19,0) not null,
        lastModificationDate timestamp,
        rulesByteArray blob,
        startDate timestamp,
        OPTLOCK number(10,0),
        primary key (id)
    );

    create table Task (
        id number(19,0) not null,
        archived number(5,0),
        allowedToDelegate varchar2(255 char),
        description varchar2(255 char),
        formName varchar2(255 char),
        name varchar2(255 char),
        priority number(10,0) not null,
        subTaskStrategy varchar2(255 char),
        subject varchar2(255 char),
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
        processSessionId number(19,0) not null,
        skipable number(1,0) not null,
        status varchar2(255 char),
        workItemId number(19,0) not null,
        taskType varchar2(255 char),
        OPTLOCK number(10,0),
        taskInitiator_id varchar2(255 char),
        actualOwner_id varchar2(255 char),
        createdBy_id varchar2(255 char),
        primary key (id)
    );

    create table TaskDef (
        id number(19,0) not null,
        name varchar2(255 char),
        priority number(10,0) not null,
        primary key (id)
    );

    create table TaskEvent (
        id number(19,0) not null,
        logTime timestamp,
        message varchar2(255 char),
        processInstanceId number(19,0),
        taskId number(19,0),
        type varchar2(255 char),
        userId varchar2(255 char),
        OPTLOCK number(10,0),
        workItemId number(19,0),
        primary key (id)
    );

    create table TaskVariableImpl (
        id number(19,0) not null,
        modificationDate timestamp,
        name varchar2(255 char),
        processId varchar2(255 char),
        processInstanceId number(19,0),
        taskId number(19,0),
        type number(10,0),
        value varchar2(4000 char),
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

    create sequence ATTACHMENT_ID_SEQ;

    create sequence AUDIT_ID_SEQ;

    create sequence BAM_TASK_ID_SEQ;

    create sequence BOOLEANEXPR_ID_SEQ;
    
    create sequence CASE_ID_INFO_ID_SEQ;
    
    create sequence CASE_FILE_DATA_LOG_ID_SEQ;

    create sequence CASE_ROLE_ASSIGN_LOG_ID_SEQ;    

    create sequence COMMENT_ID_SEQ;

    create sequence CONTENT_ID_SEQ;

    create sequence CONTEXT_MAPPING_INFO_ID_SEQ;

    create sequence CORRELATION_KEY_ID_SEQ;

    create sequence CORRELATION_PROP_ID_SEQ;

    create sequence DEADLINE_ID_SEQ;

    create sequence DEPLOY_STORE_ID_SEQ;

    create sequence EMAILNOTIFHEAD_ID_SEQ;

    create sequence ERROR_INFO_ID_SEQ;

    create sequence ESCALATION_ID_SEQ;
    
    create sequence EXEC_ERROR_INFO_ID_SEQ;

    create sequence I18NTEXT_ID_SEQ;

    create sequence NODE_INST_LOG_ID_SEQ;

    create sequence NOTIFICATION_ID_SEQ;

    create sequence PROCESS_INSTANCE_INFO_ID_SEQ;

    create sequence PROC_INST_LOG_ID_SEQ;

    create sequence QUERY_DEF_ID_SEQ;

    create sequence REASSIGNMENT_ID_SEQ;

    create sequence REQUEST_INFO_ID_SEQ;

    create sequence SESSIONINFO_ID_SEQ;

    create sequence TASK_DEF_ID_SEQ;

    create sequence TASK_EVENT_ID_SEQ;

    create sequence TASK_ID_SEQ;

    create sequence TASK_VAR_ID_SEQ;

    create sequence VAR_INST_LOG_ID_SEQ;

    create sequence WORKITEMINFO_ID_SEQ;

       
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