/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.task.service;


public enum CommandName {   
    OperationRequest,
    OperationResponse,
    
    ClaimRequest,
    ClaimResponse,
    
    ClaimNextAvailableRequest,
    
    StartRequest,
    StartResponse,
    
    StopRequest,
    StopResponse,
    
    ReleaseRequest,
    ReleaseResponse,  
    
    SuspendRequest,
    SuspendResponse, 
    
    ResumeRequest,
    ResumeResponse, 
    
    SkipRequest,
    SkipResponse,  
    
    DelegateRequest,
    DelegateResponse,
    
    ForwardRequest,
    ForwardResponse,
    
    CompleteRequest,
    CompleteResponse,   
    
    FailRequest,
    FailResponse,
    
    GetTaskRequest,
    GetTaskResponse,
    
    AddTaskRequest,
    AddTaskResponse,
    
    AddAttachmentRequest,
    AddAttachmentResponse,    
    DeleteAttachmentRequest,
    DeleteAttachmentResponse,
        
    SetDocumentContentRequest,
    SetDocumentContentResponse,
    GetContentRequest,
    GetContentResponse,
    
    AddCommentRequest,
    AddCommentResponse,    
    DeleteCommentRequest,    
    DeleteCommentResponse,    
    
    QueryTasksOwned,    
    QueryTasksOwnedWithParticularStatus,    
    QueryTasksAssignedAsBusinessAdministrator,
    QueryTasksAssignedAsExcludedOwner,
    QueryTasksAssignedAsPotentialOwner,
    QueryTasksAssignedAsPotentialOwnerWithGroup,
    QueryTasksAssignedAsPotentialOwnerByGroup,
    QueryTasksAssignedAsPotentialOwnerByStatus,
    QueryTasksAssignedAsPotentialOwnerByStatusByGroup,
    QuerySubTasksAssignedAsPotentialOwner,
    QueryGetSubTasksByParentTaskId,
    QueryTasksAssignedAsRecipient,
    QueryTasksAssignedAsTaskInitiator,
    QueryTasksAssignedAsTaskStakeholder,    
    QueryTaskSummaryResponse,
    
    QueryTaskByWorkItemId,
    QueryTaskByWorkItemIdResponse,

    QueryTasksByStatusByProcessId,
    QueryTasksByStatusByProcessIdByTaskName,
        
    RegisterForEventRequest,
    UnRegisterForEventRequest,
    EventTriggerResponse,
    
    RegisterClient,
    
    QueryGenericRequest,
    QueryGenericResponse,
    
    NominateTaskRequest,
    SetOutputRequest,
    SetFaultRequest,
    SetPriorityRequest,
    DeleteOutputRequest,
    DeleteFaultRequest   
    
}
