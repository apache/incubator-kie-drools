package org.kie.internal.task.api.model;


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
