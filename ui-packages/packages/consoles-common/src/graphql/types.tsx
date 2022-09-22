/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* eslint-disable */
import gql from 'graphql-tag';
import * as ApolloReactCommon from '@apollo/react-common';
import * as ApolloReactHooks from '@apollo/react-hooks';
export namespace GraphQL {
  export type Maybe<T> = T | null;
  export type InputMaybe<T> = Maybe<T>;
  export type Exact<T extends { [key: string]: unknown }> = {
    [K in keyof T]: T[K];
  };
  export type MakeOptional<T, K extends keyof T> = Omit<T, K> &
    { [SubKey in K]?: Maybe<T[SubKey]> };
  export type MakeMaybe<T, K extends keyof T> = Omit<T, K> &
    { [SubKey in K]: Maybe<T[SubKey]> };
  const defaultOptions = {};
  /** All built-in and custom scalars, mapped to their actual values */
  export type Scalars = {
    ID: string;
    String: string;
    Boolean: boolean;
    Int: number;
    Float: number;
    /** An ISO-8601 compliant DateTime Scalar */
    DateTime: any;
  };

  export type Attachment = {
    __typename?: 'Attachment';
    content: Scalars['String'];
    id: Scalars['String'];
    name: Scalars['String'];
    updatedAt: Scalars['DateTime'];
    updatedBy: Scalars['String'];
  };

  export type AttachmentArgument = {
    id?: InputMaybe<IdArgument>;
    name?: InputMaybe<StringArgument>;
  };

  export type BooleanArgument = {
    equal?: InputMaybe<Scalars['Boolean']>;
    isNull?: InputMaybe<Scalars['Boolean']>;
  };

  export type Comment = {
    __typename?: 'Comment';
    content: Scalars['String'];
    id: Scalars['String'];
    updatedAt: Scalars['DateTime'];
    updatedBy: Scalars['String'];
  };

  export type CommentArgument = {
    id?: InputMaybe<IdArgument>;
    name?: InputMaybe<StringArgument>;
  };

  export type DateArgument = {
    between?: InputMaybe<DateRange>;
    equal?: InputMaybe<Scalars['DateTime']>;
    greaterThan?: InputMaybe<Scalars['DateTime']>;
    greaterThanEqual?: InputMaybe<Scalars['DateTime']>;
    isNull?: InputMaybe<Scalars['Boolean']>;
    lessThan?: InputMaybe<Scalars['DateTime']>;
    lessThanEqual?: InputMaybe<Scalars['DateTime']>;
  };

  export type DateRange = {
    from: Scalars['DateTime'];
    to: Scalars['DateTime'];
  };

  export type IdArgument = {
    equal?: InputMaybe<Scalars['String']>;
    in?: InputMaybe<Array<Scalars['String']>>;
    isNull?: InputMaybe<Scalars['Boolean']>;
  };

  export type Job = {
    __typename?: 'Job';
    callbackEndpoint?: Maybe<Scalars['String']>;
    endpoint?: Maybe<Scalars['String']>;
    executionCounter?: Maybe<Scalars['Int']>;
    expirationTime?: Maybe<Scalars['DateTime']>;
    id: Scalars['String'];
    lastUpdate?: Maybe<Scalars['DateTime']>;
    nodeInstanceId?: Maybe<Scalars['String']>;
    priority?: Maybe<Scalars['Int']>;
    processId?: Maybe<Scalars['String']>;
    processInstanceId?: Maybe<Scalars['String']>;
    repeatInterval?: Maybe<Scalars['Int']>;
    repeatLimit?: Maybe<Scalars['Int']>;
    retries?: Maybe<Scalars['Int']>;
    rootProcessId?: Maybe<Scalars['String']>;
    rootProcessInstanceId?: Maybe<Scalars['String']>;
    scheduledId?: Maybe<Scalars['String']>;
    status: JobStatus;
  };

  export type JobArgument = {
    and?: InputMaybe<Array<JobArgument>>;
    expirationTime?: InputMaybe<DateArgument>;
    id?: InputMaybe<IdArgument>;
    lastUpdate?: InputMaybe<DateArgument>;
    nodeInstanceId?: InputMaybe<IdArgument>;
    not?: InputMaybe<JobArgument>;
    or?: InputMaybe<Array<JobArgument>>;
    priority?: InputMaybe<NumericArgument>;
    processId?: InputMaybe<StringArgument>;
    processInstanceId?: InputMaybe<IdArgument>;
    rootProcessId?: InputMaybe<StringArgument>;
    rootProcessInstanceId?: InputMaybe<IdArgument>;
    scheduledId?: InputMaybe<IdArgument>;
    status?: InputMaybe<JobStatusArgument>;
  };

  export type JobOrderBy = {
    executionCounter?: InputMaybe<OrderBy>;
    expirationTime?: InputMaybe<OrderBy>;
    lastUpdate?: InputMaybe<OrderBy>;
    priority?: InputMaybe<OrderBy>;
    processId?: InputMaybe<OrderBy>;
    retries?: InputMaybe<OrderBy>;
    rootProcessId?: InputMaybe<OrderBy>;
    status?: InputMaybe<OrderBy>;
  };

  export enum JobStatus {
    Canceled = 'CANCELED',
    Error = 'ERROR',
    Executed = 'EXECUTED',
    Retry = 'RETRY',
    Scheduled = 'SCHEDULED'
  }

  export type JobStatusArgument = {
    equal?: InputMaybe<JobStatus>;
    in?: InputMaybe<Array<InputMaybe<JobStatus>>>;
  };

  export type KogitoMetadata = {
    __typename?: 'KogitoMetadata';
    lastUpdate: Scalars['DateTime'];
    processInstances?: Maybe<Array<Maybe<ProcessInstanceMeta>>>;
    userTasks?: Maybe<Array<Maybe<UserTaskInstanceMeta>>>;
  };

  export type KogitoMetadataArgument = {
    lastUpdate?: InputMaybe<DateArgument>;
    processInstances?: InputMaybe<ProcessInstanceMetaArgument>;
    userTasks?: InputMaybe<UserTaskInstanceMetaArgument>;
  };

  export type KogitoMetadataOrderBy = {
    lastUpdate?: InputMaybe<OrderBy>;
  };

  export type Milestone = {
    __typename?: 'Milestone';
    id: Scalars['String'];
    name: Scalars['String'];
    status: MilestoneStatus;
  };

  export type MilestoneArgument = {
    id?: InputMaybe<IdArgument>;
    name?: InputMaybe<StringArgument>;
    status?: InputMaybe<MilestoneStatusArgument>;
  };

  export enum MilestoneStatus {
    Active = 'ACTIVE',
    Available = 'AVAILABLE',
    Completed = 'COMPLETED'
  }

  export type MilestoneStatusArgument = {
    equal?: InputMaybe<MilestoneStatus>;
    in?: InputMaybe<Array<InputMaybe<MilestoneStatus>>>;
  };

  export type Mutation = {
    __typename?: 'Mutation';
    JobCancel?: Maybe<Scalars['String']>;
    JobReschedule?: Maybe<Scalars['String']>;
    NodeInstanceCancel?: Maybe<Scalars['String']>;
    NodeInstanceRetrigger?: Maybe<Scalars['String']>;
    NodeInstanceTrigger?: Maybe<Scalars['String']>;
    ProcessInstanceAbort?: Maybe<Scalars['String']>;
    ProcessInstanceRetry?: Maybe<Scalars['String']>;
    ProcessInstanceSkip?: Maybe<Scalars['String']>;
    ProcessInstanceUpdateVariables?: Maybe<Scalars['String']>;
    UserTaskInstanceAttachmentCreate?: Maybe<Scalars['String']>;
    UserTaskInstanceAttachmentDelete?: Maybe<Scalars['String']>;
    UserTaskInstanceAttachmentUpdate?: Maybe<Scalars['String']>;
    UserTaskInstanceCommentCreate?: Maybe<Scalars['String']>;
    UserTaskInstanceCommentDelete?: Maybe<Scalars['String']>;
    UserTaskInstanceCommentUpdate?: Maybe<Scalars['String']>;
    UserTaskInstanceUpdate?: Maybe<Scalars['String']>;
  };

  export type MutationJobCancelArgs = {
    id?: InputMaybe<Scalars['String']>;
  };

  export type MutationJobRescheduleArgs = {
    data?: InputMaybe<Scalars['String']>;
    id?: InputMaybe<Scalars['String']>;
  };

  export type MutationNodeInstanceCancelArgs = {
    id?: InputMaybe<Scalars['String']>;
    nodeInstanceId?: InputMaybe<Scalars['String']>;
  };

  export type MutationNodeInstanceRetriggerArgs = {
    id?: InputMaybe<Scalars['String']>;
    nodeInstanceId?: InputMaybe<Scalars['String']>;
  };

  export type MutationNodeInstanceTriggerArgs = {
    id?: InputMaybe<Scalars['String']>;
    nodeId?: InputMaybe<Scalars['String']>;
  };

  export type MutationProcessInstanceAbortArgs = {
    id?: InputMaybe<Scalars['String']>;
  };

  export type MutationProcessInstanceRetryArgs = {
    id?: InputMaybe<Scalars['String']>;
  };

  export type MutationProcessInstanceSkipArgs = {
    id?: InputMaybe<Scalars['String']>;
  };

  export type MutationProcessInstanceUpdateVariablesArgs = {
    id?: InputMaybe<Scalars['String']>;
    variables?: InputMaybe<Scalars['String']>;
  };

  export type MutationUserTaskInstanceAttachmentCreateArgs = {
    groups?: InputMaybe<Array<InputMaybe<Scalars['String']>>>;
    name?: InputMaybe<Scalars['String']>;
    taskId?: InputMaybe<Scalars['String']>;
    uri?: InputMaybe<Scalars['String']>;
    user?: InputMaybe<Scalars['String']>;
  };

  export type MutationUserTaskInstanceAttachmentDeleteArgs = {
    attachmentId?: InputMaybe<Scalars['String']>;
    groups?: InputMaybe<Array<InputMaybe<Scalars['String']>>>;
    user?: InputMaybe<Scalars['String']>;
  };

  export type MutationUserTaskInstanceAttachmentUpdateArgs = {
    attachmentId?: InputMaybe<Scalars['String']>;
    groups?: InputMaybe<Array<InputMaybe<Scalars['String']>>>;
    name?: InputMaybe<Scalars['String']>;
    uri?: InputMaybe<Scalars['String']>;
    user?: InputMaybe<Scalars['String']>;
  };

  export type MutationUserTaskInstanceCommentCreateArgs = {
    comment?: InputMaybe<Scalars['String']>;
    groups?: InputMaybe<Array<InputMaybe<Scalars['String']>>>;
    taskId?: InputMaybe<Scalars['String']>;
    user?: InputMaybe<Scalars['String']>;
  };

  export type MutationUserTaskInstanceCommentDeleteArgs = {
    commentId?: InputMaybe<Scalars['String']>;
    groups?: InputMaybe<Array<InputMaybe<Scalars['String']>>>;
    user?: InputMaybe<Scalars['String']>;
  };

  export type MutationUserTaskInstanceCommentUpdateArgs = {
    comment?: InputMaybe<Scalars['String']>;
    commentId?: InputMaybe<Scalars['String']>;
    groups?: InputMaybe<Array<InputMaybe<Scalars['String']>>>;
    user?: InputMaybe<Scalars['String']>;
  };

  export type MutationUserTaskInstanceUpdateArgs = {
    actualOwner?: InputMaybe<Scalars['String']>;
    adminGroups?: InputMaybe<Array<Scalars['String']>>;
    adminUsers?: InputMaybe<Array<Scalars['String']>>;
    description?: InputMaybe<Scalars['String']>;
    excludedUsers?: InputMaybe<Array<Scalars['String']>>;
    groups?: InputMaybe<Array<InputMaybe<Scalars['String']>>>;
    inputParams?: InputMaybe<Scalars['String']>;
    potentialGroups?: InputMaybe<Array<Scalars['String']>>;
    potentialUsers?: InputMaybe<Array<Scalars['String']>>;
    priority?: InputMaybe<Scalars['String']>;
    taskId?: InputMaybe<Scalars['String']>;
    user?: InputMaybe<Scalars['String']>;
  };

  export type Node = {
    __typename?: 'Node';
    id: Scalars['String'];
    name: Scalars['String'];
    nodeDefinitionId: Scalars['String'];
    type: Scalars['String'];
    uniqueId: Scalars['String'];
  };

  export type NodeInstance = {
    __typename?: 'NodeInstance';
    definitionId: Scalars['String'];
    enter: Scalars['DateTime'];
    exit?: Maybe<Scalars['DateTime']>;
    id: Scalars['String'];
    name: Scalars['String'];
    nodeId: Scalars['String'];
    type: Scalars['String'];
  };

  export type NodeInstanceArgument = {
    definitionId?: InputMaybe<StringArgument>;
    enter?: InputMaybe<DateArgument>;
    exit?: InputMaybe<DateArgument>;
    id?: InputMaybe<IdArgument>;
    name?: InputMaybe<StringArgument>;
    nodeId?: InputMaybe<StringArgument>;
    type?: InputMaybe<StringArgument>;
  };

  export type NumericArgument = {
    between?: InputMaybe<NumericRange>;
    equal?: InputMaybe<Scalars['Int']>;
    greaterThan?: InputMaybe<Scalars['Int']>;
    greaterThanEqual?: InputMaybe<Scalars['Int']>;
    in?: InputMaybe<Array<Scalars['Int']>>;
    isNull?: InputMaybe<Scalars['Boolean']>;
    lessThan?: InputMaybe<Scalars['Int']>;
    lessThanEqual?: InputMaybe<Scalars['Int']>;
  };

  export type NumericRange = {
    from: Scalars['Int'];
    to: Scalars['Int'];
  };

  export enum OrderBy {
    Asc = 'ASC',
    Desc = 'DESC'
  }

  export type Pagination = {
    limit?: InputMaybe<Scalars['Int']>;
    offset?: InputMaybe<Scalars['Int']>;
  };

  export type ProcessInstance = {
    __typename?: 'ProcessInstance';
    addons?: Maybe<Array<Scalars['String']>>;
    businessKey?: Maybe<Scalars['String']>;
    childProcessInstances?: Maybe<Array<ProcessInstance>>;
    diagram?: Maybe<Scalars['String']>;
    end?: Maybe<Scalars['DateTime']>;
    endpoint: Scalars['String'];
    error?: Maybe<ProcessInstanceError>;
    id: Scalars['String'];
    lastUpdate: Scalars['DateTime'];
    milestones?: Maybe<Array<Milestone>>;
    nodeDefinitions?: Maybe<Array<Node>>;
    nodes: Array<NodeInstance>;
    parentProcessInstance?: Maybe<ProcessInstance>;
    parentProcessInstanceId?: Maybe<Scalars['String']>;
    processId: Scalars['String'];
    processName?: Maybe<Scalars['String']>;
    roles?: Maybe<Array<Scalars['String']>>;
    rootProcessId?: Maybe<Scalars['String']>;
    rootProcessInstanceId?: Maybe<Scalars['String']>;
    serviceUrl?: Maybe<Scalars['String']>;
    source?: Maybe<Scalars['String']>;
    start: Scalars['DateTime'];
    state: ProcessInstanceState;
    variables?: Maybe<Scalars['String']>;
  };

  export type ProcessInstanceArgument = {
    addons?: InputMaybe<StringArrayArgument>;
    and?: InputMaybe<Array<ProcessInstanceArgument>>;
    businessKey?: InputMaybe<StringArgument>;
    end?: InputMaybe<DateArgument>;
    endpoint?: InputMaybe<StringArgument>;
    error?: InputMaybe<ProcessInstanceErrorArgument>;
    id?: InputMaybe<IdArgument>;
    lastUpdate?: InputMaybe<DateArgument>;
    milestones?: InputMaybe<MilestoneArgument>;
    nodes?: InputMaybe<NodeInstanceArgument>;
    not?: InputMaybe<ProcessInstanceArgument>;
    or?: InputMaybe<Array<ProcessInstanceArgument>>;
    parentProcessInstanceId?: InputMaybe<IdArgument>;
    processId?: InputMaybe<StringArgument>;
    processName?: InputMaybe<StringArgument>;
    roles?: InputMaybe<StringArrayArgument>;
    rootProcessId?: InputMaybe<StringArgument>;
    rootProcessInstanceId?: InputMaybe<IdArgument>;
    start?: InputMaybe<DateArgument>;
    state?: InputMaybe<ProcessInstanceStateArgument>;
  };

  export type ProcessInstanceError = {
    __typename?: 'ProcessInstanceError';
    message?: Maybe<Scalars['String']>;
    nodeDefinitionId: Scalars['String'];
  };

  export type ProcessInstanceErrorArgument = {
    message?: InputMaybe<StringArgument>;
    nodeDefinitionId?: InputMaybe<StringArgument>;
  };

  export type ProcessInstanceErrorOrderBy = {
    message?: InputMaybe<OrderBy>;
    nodeDefinitionId?: InputMaybe<OrderBy>;
  };

  export type ProcessInstanceMeta = {
    __typename?: 'ProcessInstanceMeta';
    businessKey?: Maybe<Scalars['String']>;
    end?: Maybe<Scalars['DateTime']>;
    endpoint: Scalars['String'];
    id: Scalars['String'];
    lastUpdate: Scalars['DateTime'];
    parentProcessInstanceId?: Maybe<Scalars['String']>;
    processId: Scalars['String'];
    processName?: Maybe<Scalars['String']>;
    roles?: Maybe<Array<Scalars['String']>>;
    rootProcessId?: Maybe<Scalars['String']>;
    rootProcessInstanceId?: Maybe<Scalars['String']>;
    serviceUrl?: Maybe<Scalars['String']>;
    start: Scalars['DateTime'];
    state: ProcessInstanceState;
  };

  export type ProcessInstanceMetaArgument = {
    businessKey?: InputMaybe<StringArgument>;
    end?: InputMaybe<DateArgument>;
    endpoint?: InputMaybe<StringArgument>;
    id?: InputMaybe<IdArgument>;
    parentProcessInstanceId?: InputMaybe<IdArgument>;
    processId?: InputMaybe<StringArgument>;
    processName?: InputMaybe<StringArgument>;
    roles?: InputMaybe<StringArrayArgument>;
    rootProcessId?: InputMaybe<StringArgument>;
    rootProcessInstanceId?: InputMaybe<IdArgument>;
    start?: InputMaybe<DateArgument>;
    state?: InputMaybe<ProcessInstanceStateArgument>;
  };

  export type ProcessInstanceOrderBy = {
    businessKey?: InputMaybe<OrderBy>;
    end?: InputMaybe<OrderBy>;
    error?: InputMaybe<ProcessInstanceErrorOrderBy>;
    lastUpdate?: InputMaybe<OrderBy>;
    processId?: InputMaybe<OrderBy>;
    processName?: InputMaybe<OrderBy>;
    rootProcessId?: InputMaybe<OrderBy>;
    start?: InputMaybe<OrderBy>;
    state?: InputMaybe<OrderBy>;
  };

  export enum ProcessInstanceState {
    Aborted = 'ABORTED',
    Active = 'ACTIVE',
    Completed = 'COMPLETED',
    Error = 'ERROR',
    Pending = 'PENDING',
    Suspended = 'SUSPENDED'
  }

  export type ProcessInstanceStateArgument = {
    equal?: InputMaybe<ProcessInstanceState>;
    in?: InputMaybe<Array<InputMaybe<ProcessInstanceState>>>;
  };

  export type Query = {
    __typename?: 'Query';
    Jobs?: Maybe<Array<Maybe<Job>>>;
    ProcessInstances?: Maybe<Array<Maybe<ProcessInstance>>>;
    UserTaskInstances?: Maybe<Array<Maybe<UserTaskInstance>>>;
  };

  export type QueryJobsArgs = {
    orderBy?: InputMaybe<JobOrderBy>;
    pagination?: InputMaybe<Pagination>;
    where?: InputMaybe<JobArgument>;
  };

  export type QueryProcessInstancesArgs = {
    orderBy?: InputMaybe<ProcessInstanceOrderBy>;
    pagination?: InputMaybe<Pagination>;
    where?: InputMaybe<ProcessInstanceArgument>;
  };

  export type QueryUserTaskInstancesArgs = {
    orderBy?: InputMaybe<UserTaskInstanceOrderBy>;
    pagination?: InputMaybe<Pagination>;
    where?: InputMaybe<UserTaskInstanceArgument>;
  };

  export type StringArgument = {
    equal?: InputMaybe<Scalars['String']>;
    in?: InputMaybe<Array<Scalars['String']>>;
    isNull?: InputMaybe<Scalars['Boolean']>;
    like?: InputMaybe<Scalars['String']>;
  };

  export type StringArrayArgument = {
    contains?: InputMaybe<Scalars['String']>;
    containsAll?: InputMaybe<Array<Scalars['String']>>;
    containsAny?: InputMaybe<Array<Scalars['String']>>;
    isNull?: InputMaybe<Scalars['Boolean']>;
  };

  export type Subscription = {
    __typename?: 'Subscription';
    JobAdded: Job;
    JobUpdated: Job;
    ProcessInstanceAdded: ProcessInstance;
    ProcessInstanceUpdated: ProcessInstance;
    UserTaskInstanceAdded: UserTaskInstance;
    UserTaskInstanceUpdated: UserTaskInstance;
  };

  export type UserTaskInstance = {
    __typename?: 'UserTaskInstance';
    actualOwner?: Maybe<Scalars['String']>;
    adminGroups?: Maybe<Array<Scalars['String']>>;
    adminUsers?: Maybe<Array<Scalars['String']>>;
    attachments?: Maybe<Array<Attachment>>;
    comments?: Maybe<Array<Comment>>;
    completed?: Maybe<Scalars['DateTime']>;
    description?: Maybe<Scalars['String']>;
    endpoint?: Maybe<Scalars['String']>;
    excludedUsers?: Maybe<Array<Scalars['String']>>;
    id: Scalars['String'];
    inputs?: Maybe<Scalars['String']>;
    lastUpdate: Scalars['DateTime'];
    name?: Maybe<Scalars['String']>;
    outputs?: Maybe<Scalars['String']>;
    potentialGroups?: Maybe<Array<Scalars['String']>>;
    potentialUsers?: Maybe<Array<Scalars['String']>>;
    priority?: Maybe<Scalars['String']>;
    processId: Scalars['String'];
    processInstanceId: Scalars['String'];
    referenceName?: Maybe<Scalars['String']>;
    rootProcessId?: Maybe<Scalars['String']>;
    rootProcessInstanceId?: Maybe<Scalars['String']>;
    schema?: Maybe<Scalars['String']>;
    started: Scalars['DateTime'];
    state: Scalars['String'];
  };

  export type UserTaskInstanceSchemaArgs = {
    groups?: InputMaybe<Array<InputMaybe<Scalars['String']>>>;
    user?: InputMaybe<Scalars['String']>;
  };

  export type UserTaskInstanceArgument = {
    actualOwner?: InputMaybe<StringArgument>;
    adminGroups?: InputMaybe<StringArrayArgument>;
    adminUsers?: InputMaybe<StringArrayArgument>;
    and?: InputMaybe<Array<UserTaskInstanceArgument>>;
    attachments?: InputMaybe<AttachmentArgument>;
    comments?: InputMaybe<CommentArgument>;
    completed?: InputMaybe<DateArgument>;
    description?: InputMaybe<StringArgument>;
    excludedUsers?: InputMaybe<StringArrayArgument>;
    id?: InputMaybe<IdArgument>;
    lastUpdate?: InputMaybe<DateArgument>;
    name?: InputMaybe<StringArgument>;
    not?: InputMaybe<UserTaskInstanceArgument>;
    or?: InputMaybe<Array<UserTaskInstanceArgument>>;
    potentialGroups?: InputMaybe<StringArrayArgument>;
    potentialUsers?: InputMaybe<StringArrayArgument>;
    priority?: InputMaybe<StringArgument>;
    processId?: InputMaybe<StringArgument>;
    processInstanceId?: InputMaybe<IdArgument>;
    referenceName?: InputMaybe<StringArgument>;
    started?: InputMaybe<DateArgument>;
    state?: InputMaybe<StringArgument>;
  };

  export type UserTaskInstanceMeta = {
    __typename?: 'UserTaskInstanceMeta';
    actualOwner?: Maybe<Scalars['String']>;
    adminGroups?: Maybe<Array<Scalars['String']>>;
    adminUsers?: Maybe<Array<Scalars['String']>>;
    attachments?: Maybe<Array<Attachment>>;
    comments?: Maybe<Array<Comment>>;
    completed?: Maybe<Scalars['DateTime']>;
    description?: Maybe<Scalars['String']>;
    excludedUsers?: Maybe<Array<Scalars['String']>>;
    id: Scalars['String'];
    lastUpdate: Scalars['DateTime'];
    name?: Maybe<Scalars['String']>;
    potentialGroups?: Maybe<Array<Scalars['String']>>;
    potentialUsers?: Maybe<Array<Scalars['String']>>;
    priority?: Maybe<Scalars['String']>;
    processInstanceId: Scalars['String'];
    referenceName?: Maybe<Scalars['String']>;
    started: Scalars['DateTime'];
    state: Scalars['String'];
  };

  export type UserTaskInstanceMetaArgument = {
    actualOwner?: InputMaybe<StringArgument>;
    adminGroups?: InputMaybe<StringArrayArgument>;
    adminUsers?: InputMaybe<StringArrayArgument>;
    completed?: InputMaybe<DateArgument>;
    description?: InputMaybe<StringArgument>;
    excludedUsers?: InputMaybe<StringArrayArgument>;
    id?: InputMaybe<IdArgument>;
    name?: InputMaybe<StringArgument>;
    potentialGroups?: InputMaybe<StringArrayArgument>;
    potentialUsers?: InputMaybe<StringArrayArgument>;
    priority?: InputMaybe<StringArgument>;
    processInstanceId?: InputMaybe<IdArgument>;
    referenceName?: InputMaybe<StringArgument>;
    started?: InputMaybe<DateArgument>;
    state?: InputMaybe<StringArgument>;
  };

  export type UserTaskInstanceOrderBy = {
    actualOwner?: InputMaybe<OrderBy>;
    completed?: InputMaybe<OrderBy>;
    description?: InputMaybe<OrderBy>;
    lastUpdate?: InputMaybe<OrderBy>;
    name?: InputMaybe<OrderBy>;
    priority?: InputMaybe<OrderBy>;
    processId?: InputMaybe<OrderBy>;
    referenceName?: InputMaybe<OrderBy>;
    started?: InputMaybe<OrderBy>;
    state?: InputMaybe<OrderBy>;
  };

  /**
   * A Directive provides a way to describe alternate runtime execution and type validation behavior in a GraphQL document.
   *
   * In some cases, you need to provide options to alter GraphQL's execution behavior in ways field arguments will not suffice, such as conditionally including or skipping a field. Directives provide this by describing additional information to the executor.
   */
  export type __Directive = {
    __typename?: '__Directive';
    name: Scalars['String'];
    description?: Maybe<Scalars['String']>;
    isRepeatable: Scalars['Boolean'];
    locations: Array<__DirectiveLocation>;
    args: Array<__InputValue>;
  };

  /**
   * A Directive provides a way to describe alternate runtime execution and type validation behavior in a GraphQL document.
   *
   * In some cases, you need to provide options to alter GraphQL's execution behavior in ways field arguments will not suffice, such as conditionally including or skipping a field. Directives provide this by describing additional information to the executor.
   */
  export type __DirectiveArgsArgs = {
    includeDeprecated?: InputMaybe<Scalars['Boolean']>;
  };

  /** A Directive can be adjacent to many parts of the GraphQL language, a __DirectiveLocation describes one such possible adjacencies. */
  export enum __DirectiveLocation {
    /** Location adjacent to a query operation. */
    Query = 'QUERY',
    /** Location adjacent to a mutation operation. */
    Mutation = 'MUTATION',
    /** Location adjacent to a subscription operation. */
    Subscription = 'SUBSCRIPTION',
    /** Location adjacent to a field. */
    Field = 'FIELD',
    /** Location adjacent to a fragment definition. */
    FragmentDefinition = 'FRAGMENT_DEFINITION',
    /** Location adjacent to a fragment spread. */
    FragmentSpread = 'FRAGMENT_SPREAD',
    /** Location adjacent to an inline fragment. */
    InlineFragment = 'INLINE_FRAGMENT',
    /** Location adjacent to a variable definition. */
    VariableDefinition = 'VARIABLE_DEFINITION',
    /** Location adjacent to a schema definition. */
    Schema = 'SCHEMA',
    /** Location adjacent to a scalar definition. */
    Scalar = 'SCALAR',
    /** Location adjacent to an object type definition. */
    Object = 'OBJECT',
    /** Location adjacent to a field definition. */
    FieldDefinition = 'FIELD_DEFINITION',
    /** Location adjacent to an argument definition. */
    ArgumentDefinition = 'ARGUMENT_DEFINITION',
    /** Location adjacent to an interface definition. */
    Interface = 'INTERFACE',
    /** Location adjacent to a union definition. */
    Union = 'UNION',
    /** Location adjacent to an enum definition. */
    Enum = 'ENUM',
    /** Location adjacent to an enum value definition. */
    EnumValue = 'ENUM_VALUE',
    /** Location adjacent to an input object type definition. */
    InputObject = 'INPUT_OBJECT',
    /** Location adjacent to an input object field definition. */
    InputFieldDefinition = 'INPUT_FIELD_DEFINITION'
  }

  /** One possible value for a given Enum. Enum values are unique values, not a placeholder for a string or numeric value. However an Enum value is returned in a JSON response as a string. */
  export type __EnumValue = {
    __typename?: '__EnumValue';
    name: Scalars['String'];
    description?: Maybe<Scalars['String']>;
    isDeprecated: Scalars['Boolean'];
    deprecationReason?: Maybe<Scalars['String']>;
  };

  /** Object and Interface types are described by a list of Fields, each of which has a name, potentially a list of arguments, and a return type. */
  export type __Field = {
    __typename?: '__Field';
    name: Scalars['String'];
    description?: Maybe<Scalars['String']>;
    args: Array<__InputValue>;
    type: __Type;
    isDeprecated: Scalars['Boolean'];
    deprecationReason?: Maybe<Scalars['String']>;
  };

  /** Object and Interface types are described by a list of Fields, each of which has a name, potentially a list of arguments, and a return type. */
  export type __FieldArgsArgs = {
    includeDeprecated?: InputMaybe<Scalars['Boolean']>;
  };

  /** Arguments provided to Fields or Directives and the input fields of an InputObject are represented as Input Values which describe their type and optionally a default value. */
  export type __InputValue = {
    __typename?: '__InputValue';
    name: Scalars['String'];
    description?: Maybe<Scalars['String']>;
    type: __Type;
    /** A GraphQL-formatted string representing the default value for this input value. */
    defaultValue?: Maybe<Scalars['String']>;
    isDeprecated: Scalars['Boolean'];
    deprecationReason?: Maybe<Scalars['String']>;
  };

  /** A GraphQL Schema defines the capabilities of a GraphQL server. It exposes all available types and directives on the server, as well as the entry points for query, mutation, and subscription operations. */
  export type __Schema = {
    __typename?: '__Schema';
    description?: Maybe<Scalars['String']>;
    /** A list of all types supported by this server. */
    types: Array<__Type>;
    /** The type that query operations will be rooted at. */
    queryType: __Type;
    /** If this server supports mutation, the type that mutation operations will be rooted at. */
    mutationType?: Maybe<__Type>;
    /** If this server support subscription, the type that subscription operations will be rooted at. */
    subscriptionType?: Maybe<__Type>;
    /** A list of all directives supported by this server. */
    directives: Array<__Directive>;
  };

  /**
   * The fundamental unit of any GraphQL Schema is the type. There are many kinds of types in GraphQL as represented by the `__TypeKind` enum.
   *
   * Depending on the kind of a type, certain fields describe information about that type. Scalar types provide no information beyond a name, description and optional `specifiedByUrl`, while Enum types provide their values. Object and Interface types provide the fields they describe. Abstract types, Union and Interface, provide the Object types possible at runtime. List and NonNull types compose other types.
   */
  export type __Type = {
    __typename?: '__Type';
    kind: __TypeKind;
    name?: Maybe<Scalars['String']>;
    description?: Maybe<Scalars['String']>;
    specifiedByUrl?: Maybe<Scalars['String']>;
    fields?: Maybe<Array<__Field>>;
    interfaces?: Maybe<Array<__Type>>;
    possibleTypes?: Maybe<Array<__Type>>;
    enumValues?: Maybe<Array<__EnumValue>>;
    inputFields?: Maybe<Array<__InputValue>>;
    ofType?: Maybe<__Type>;
  };

  /**
   * The fundamental unit of any GraphQL Schema is the type. There are many kinds of types in GraphQL as represented by the `__TypeKind` enum.
   *
   * Depending on the kind of a type, certain fields describe information about that type. Scalar types provide no information beyond a name, description and optional `specifiedByUrl`, while Enum types provide their values. Object and Interface types provide the fields they describe. Abstract types, Union and Interface, provide the Object types possible at runtime. List and NonNull types compose other types.
   */
  export type __TypeFieldsArgs = {
    includeDeprecated?: InputMaybe<Scalars['Boolean']>;
  };

  /**
   * The fundamental unit of any GraphQL Schema is the type. There are many kinds of types in GraphQL as represented by the `__TypeKind` enum.
   *
   * Depending on the kind of a type, certain fields describe information about that type. Scalar types provide no information beyond a name, description and optional `specifiedByUrl`, while Enum types provide their values. Object and Interface types provide the fields they describe. Abstract types, Union and Interface, provide the Object types possible at runtime. List and NonNull types compose other types.
   */
  export type __TypeEnumValuesArgs = {
    includeDeprecated?: InputMaybe<Scalars['Boolean']>;
  };

  /**
   * The fundamental unit of any GraphQL Schema is the type. There are many kinds of types in GraphQL as represented by the `__TypeKind` enum.
   *
   * Depending on the kind of a type, certain fields describe information about that type. Scalar types provide no information beyond a name, description and optional `specifiedByUrl`, while Enum types provide their values. Object and Interface types provide the fields they describe. Abstract types, Union and Interface, provide the Object types possible at runtime. List and NonNull types compose other types.
   */
  export type __TypeInputFieldsArgs = {
    includeDeprecated?: InputMaybe<Scalars['Boolean']>;
  };

  /** An enum describing what kind of type a given `__Type` is. */
  export enum __TypeKind {
    /** Indicates this type is a scalar. */
    Scalar = 'SCALAR',
    /** Indicates this type is an object. `fields` and `interfaces` are valid fields. */
    Object = 'OBJECT',
    /** Indicates this type is an interface. `fields`, `interfaces`, and `possibleTypes` are valid fields. */
    Interface = 'INTERFACE',
    /** Indicates this type is a union. `possibleTypes` is a valid field. */
    Union = 'UNION',
    /** Indicates this type is an enum. `enumValues` is a valid field. */
    Enum = 'ENUM',
    /** Indicates this type is an input object. `inputFields` is a valid field. */
    InputObject = 'INPUT_OBJECT',
    /** Indicates this type is a list. `ofType` is a valid field. */
    List = 'LIST',
    /** Indicates this type is a non-null. `ofType` is a valid field. */
    NonNull = 'NON_NULL'
  }

  export type GetProcessInstancesQueryVariables = Exact<{
    where?: InputMaybe<ProcessInstanceArgument>;
    offset?: InputMaybe<Scalars['Int']>;
    limit?: InputMaybe<Scalars['Int']>;
    orderBy?: InputMaybe<ProcessInstanceOrderBy>;
  }>;

  export type GetProcessInstancesQuery = {
    __typename?: 'Query';
    ProcessInstances?:
      | Array<
          | {
              __typename?: 'ProcessInstance';
              id: string;
              processId: string;
              processName?: string | null | undefined;
              parentProcessInstanceId?: string | null | undefined;
              rootProcessInstanceId?: string | null | undefined;
              roles?: Array<string> | null | undefined;
              state: ProcessInstanceState;
              start: any;
              lastUpdate: any;
              addons?: Array<string> | null | undefined;
              businessKey?: string | null | undefined;
              serviceUrl?: string | null | undefined;
              error?:
                | {
                    __typename?: 'ProcessInstanceError';
                    nodeDefinitionId: string;
                    message?: string | null | undefined;
                  }
                | null
                | undefined;
            }
          | null
          | undefined
        >
      | null
      | undefined;
  };

  export type GetChildInstancesQueryVariables = Exact<{
    rootProcessInstanceId?: InputMaybe<Scalars['String']>;
  }>;

  export type GetChildInstancesQuery = {
    __typename?: 'Query';
    ProcessInstances?:
      | Array<
          | {
              __typename?: 'ProcessInstance';
              id: string;
              processId: string;
              processName?: string | null | undefined;
              parentProcessInstanceId?: string | null | undefined;
              rootProcessInstanceId?: string | null | undefined;
              roles?: Array<string> | null | undefined;
              state: ProcessInstanceState;
              start: any;
              lastUpdate: any;
              serviceUrl?: string | null | undefined;
              addons?: Array<string> | null | undefined;
              businessKey?: string | null | undefined;
              error?:
                | {
                    __typename?: 'ProcessInstanceError';
                    nodeDefinitionId: string;
                    message?: string | null | undefined;
                  }
                | null
                | undefined;
            }
          | null
          | undefined
        >
      | null
      | undefined;
  };

  export type GetProcessInstanceByIdQueryVariables = Exact<{
    id?: InputMaybe<Scalars['String']>;
  }>;

  export type GetProcessInstanceByIdQuery = {
    __typename?: 'Query';
    ProcessInstances?:
      | Array<
          | {
              __typename?: 'ProcessInstance';
              id: string;
              processId: string;
              processName?: string | null | undefined;
              businessKey?: string | null | undefined;
              parentProcessInstanceId?: string | null | undefined;
              roles?: Array<string> | null | undefined;
              variables?: string | null | undefined;
              state: ProcessInstanceState;
              start: any;
              lastUpdate: any;
              end?: any | null | undefined;
              addons?: Array<string> | null | undefined;
              endpoint: string;
              serviceUrl?: string | null | undefined;
              parentProcessInstance?:
                | {
                    __typename?: 'ProcessInstance';
                    id: string;
                    processName?: string | null | undefined;
                    businessKey?: string | null | undefined;
                  }
                | null
                | undefined;
              error?:
                | {
                    __typename?: 'ProcessInstanceError';
                    nodeDefinitionId: string;
                    message?: string | null | undefined;
                  }
                | null
                | undefined;
              childProcessInstances?:
                | Array<{
                    __typename?: 'ProcessInstance';
                    id: string;
                    processName?: string | null | undefined;
                    businessKey?: string | null | undefined;
                  }>
                | null
                | undefined;
              nodes: Array<{
                __typename?: 'NodeInstance';
                id: string;
                nodeId: string;
                name: string;
                enter: any;
                exit?: any | null | undefined;
                type: string;
                definitionId: string;
              }>;
              milestones?:
                | Array<{
                    __typename?: 'Milestone';
                    id: string;
                    name: string;
                    status: MilestoneStatus;
                  }>
                | null
                | undefined;
            }
          | null
          | undefined
        >
      | null
      | undefined;
  };

  export type GetColumnPickerAttributesQueryVariables = Exact<{
    columnPickerType: Scalars['String'];
  }>;

  export type GetColumnPickerAttributesQuery = {
    __typename?: 'Query';
    __type?:
      | {
          __typename?: '__Type';
          name?: string | null | undefined;
          fields?:
            | Array<{
                __typename?: '__Field';
                name: string;
                type: {
                  __typename?: '__Type';
                  name?: string | null | undefined;
                  kind: __TypeKind;
                  fields?:
                    | Array<{
                        __typename?: '__Field';
                        name: string;
                        type: {
                          __typename?: '__Type';
                          name?: string | null | undefined;
                          kind: __TypeKind;
                        };
                      }>
                    | null
                    | undefined;
                };
              }>
            | null
            | undefined;
        }
      | null
      | undefined;
  };

  export type GetQueryTypesQueryVariables = Exact<{ [key: string]: never }>;

  export type GetQueryTypesQuery = {
    __typename?: 'Query';
    __schema: {
      __typename?: '__Schema';
      queryType: Array<{
        __typename?: '__Type';
        name?: string | null | undefined;
        kind: __TypeKind;
        fields?:
          | Array<{
              __typename?: '__Field';
              name: string;
              type: {
                __typename?: '__Type';
                name?: string | null | undefined;
                kind: __TypeKind;
              };
            }>
          | null
          | undefined;
        inputFields?:
          | Array<{
              __typename?: '__InputValue';
              name: string;
              type: {
                __typename?: '__Type';
                name?: string | null | undefined;
                kind: __TypeKind;
              };
            }>
          | null
          | undefined;
      }>;
    };
  };

  export type GetQueryFieldsQueryVariables = Exact<{ [key: string]: never }>;

  export type GetQueryFieldsQuery = {
    __typename?: 'Query';
    __type?:
      | {
          __typename?: '__Type';
          name?: string | null | undefined;
          fields?:
            | Array<{
                __typename?: '__Field';
                name: string;
                args: Array<{
                  __typename?: '__InputValue';
                  name: string;
                  type: {
                    __typename?: '__Type';
                    kind: __TypeKind;
                    name?: string | null | undefined;
                  };
                }>;
                type: {
                  __typename?: '__Type';
                  ofType?:
                    | {
                        __typename?: '__Type';
                        name?: string | null | undefined;
                      }
                    | null
                    | undefined;
                };
              }>
            | null
            | undefined;
        }
      | null
      | undefined;
  };

  export type GetInputFieldsFromQueryQueryVariables = Exact<{
    currentQuery: Scalars['String'];
  }>;

  export type GetInputFieldsFromQueryQuery = {
    __typename?: 'Query';
    __type?:
      | {
          __typename?: '__Type';
          name?: string | null | undefined;
          inputFields?:
            | Array<{
                __typename?: '__InputValue';
                name: string;
                type: {
                  __typename?: '__Type';
                  name?: string | null | undefined;
                  kind: __TypeKind;
                  inputFields?:
                    | Array<{
                        __typename?: '__InputValue';
                        name: string;
                        type: {
                          __typename?: '__Type';
                          name?: string | null | undefined;
                        };
                      }>
                    | null
                    | undefined;
                };
              }>
            | null
            | undefined;
        }
      | null
      | undefined;
  };

  export type GetInputFieldsFromTypeQueryVariables = Exact<{
    type: Scalars['String'];
  }>;

  export type GetInputFieldsFromTypeQuery = {
    __typename?: 'Query';
    __type?:
      | {
          __typename?: '__Type';
          name?: string | null | undefined;
          inputFields?:
            | Array<{
                __typename?: '__InputValue';
                name: string;
                type: {
                  __typename?: '__Type';
                  name?: string | null | undefined;
                  kind: __TypeKind;
                  enumValues?:
                    | Array<{ __typename?: '__EnumValue'; name: string }>
                    | null
                    | undefined;
                  ofType?:
                    | {
                        __typename?: '__Type';
                        kind: __TypeKind;
                        name?: string | null | undefined;
                        enumValues?:
                          | Array<{ __typename?: '__EnumValue'; name: string }>
                          | null
                          | undefined;
                      }
                    | null
                    | undefined;
                };
              }>
            | null
            | undefined;
        }
      | null
      | undefined;
  };

  export type GetUserTasksByStatesQueryVariables = Exact<{
    state?: InputMaybe<Array<Scalars['String']> | Scalars['String']>;
    orderBy?: InputMaybe<UserTaskInstanceOrderBy>;
  }>;

  export type GetUserTasksByStatesQuery = {
    __typename?: 'Query';
    UserTaskInstances?:
      | Array<
          | {
              __typename?: 'UserTaskInstance';
              id: string;
              name?: string | null | undefined;
              referenceName?: string | null | undefined;
              description?: string | null | undefined;
              priority?: string | null | undefined;
              processInstanceId: string;
              processId: string;
              rootProcessInstanceId?: string | null | undefined;
              rootProcessId?: string | null | undefined;
              state: string;
              actualOwner?: string | null | undefined;
              adminGroups?: Array<string> | null | undefined;
              adminUsers?: Array<string> | null | undefined;
              completed?: any | null | undefined;
              started: any;
              excludedUsers?: Array<string> | null | undefined;
              potentialGroups?: Array<string> | null | undefined;
              potentialUsers?: Array<string> | null | undefined;
              inputs?: string | null | undefined;
              outputs?: string | null | undefined;
              endpoint?: string | null | undefined;
            }
          | null
          | undefined
        >
      | null
      | undefined;
  };

  export type GetUserTaskByIdQueryVariables = Exact<{
    id?: InputMaybe<Scalars['String']>;
  }>;

  export type GetUserTaskByIdQuery = {
    __typename?: 'Query';
    UserTaskInstances?:
      | Array<
          | {
              __typename?: 'UserTaskInstance';
              id: string;
              description?: string | null | undefined;
              name?: string | null | undefined;
              priority?: string | null | undefined;
              processInstanceId: string;
              processId: string;
              rootProcessInstanceId?: string | null | undefined;
              rootProcessId?: string | null | undefined;
              state: string;
              actualOwner?: string | null | undefined;
              adminGroups?: Array<string> | null | undefined;
              adminUsers?: Array<string> | null | undefined;
              completed?: any | null | undefined;
              started: any;
              excludedUsers?: Array<string> | null | undefined;
              potentialGroups?: Array<string> | null | undefined;
              potentialUsers?: Array<string> | null | undefined;
              inputs?: string | null | undefined;
              outputs?: string | null | undefined;
              referenceName?: string | null | undefined;
              endpoint?: string | null | undefined;
              lastUpdate: any;
            }
          | null
          | undefined
        >
      | null
      | undefined;
  };

  export type GetTasksForUserQueryVariables = Exact<{
    whereArgument?: InputMaybe<UserTaskInstanceArgument>;
    offset?: InputMaybe<Scalars['Int']>;
    limit?: InputMaybe<Scalars['Int']>;
    orderBy?: InputMaybe<UserTaskInstanceOrderBy>;
  }>;

  export type GetTasksForUserQuery = {
    __typename?: 'Query';
    UserTaskInstances?:
      | Array<
          | {
              __typename?: 'UserTaskInstance';
              id: string;
              name?: string | null | undefined;
              referenceName?: string | null | undefined;
              description?: string | null | undefined;
              priority?: string | null | undefined;
              processInstanceId: string;
              processId: string;
              rootProcessInstanceId?: string | null | undefined;
              rootProcessId?: string | null | undefined;
              state: string;
              actualOwner?: string | null | undefined;
              adminGroups?: Array<string> | null | undefined;
              adminUsers?: Array<string> | null | undefined;
              completed?: any | null | undefined;
              started: any;
              excludedUsers?: Array<string> | null | undefined;
              potentialGroups?: Array<string> | null | undefined;
              potentialUsers?: Array<string> | null | undefined;
              inputs?: string | null | undefined;
              outputs?: string | null | undefined;
              lastUpdate: any;
              endpoint?: string | null | undefined;
            }
          | null
          | undefined
        >
      | null
      | undefined;
  };

  export type GetJobsByProcessInstanceIdQueryVariables = Exact<{
    processInstanceId?: InputMaybe<Scalars['String']>;
  }>;

  export type GetJobsByProcessInstanceIdQuery = {
    __typename?: 'Query';
    Jobs?:
      | Array<
          | {
              __typename?: 'Job';
              id: string;
              processId?: string | null | undefined;
              processInstanceId?: string | null | undefined;
              rootProcessId?: string | null | undefined;
              status: JobStatus;
              expirationTime?: any | null | undefined;
              priority?: number | null | undefined;
              callbackEndpoint?: string | null | undefined;
              repeatInterval?: number | null | undefined;
              repeatLimit?: number | null | undefined;
              scheduledId?: string | null | undefined;
              retries?: number | null | undefined;
              lastUpdate?: any | null | undefined;
              endpoint?: string | null | undefined;
              nodeInstanceId?: string | null | undefined;
              executionCounter?: number | null | undefined;
            }
          | null
          | undefined
        >
      | null
      | undefined;
  };

  export type GetJobsWithFiltersQueryVariables = Exact<{
    values?: InputMaybe<Array<InputMaybe<JobStatus>> | InputMaybe<JobStatus>>;
    orderBy?: InputMaybe<JobOrderBy>;
    offset?: InputMaybe<Scalars['Int']>;
    limit?: InputMaybe<Scalars['Int']>;
  }>;

  export type GetJobsWithFiltersQuery = {
    __typename?: 'Query';
    Jobs?:
      | Array<
          | {
              __typename?: 'Job';
              id: string;
              processId?: string | null | undefined;
              processInstanceId?: string | null | undefined;
              rootProcessId?: string | null | undefined;
              status: JobStatus;
              expirationTime?: any | null | undefined;
              priority?: number | null | undefined;
              callbackEndpoint?: string | null | undefined;
              repeatInterval?: number | null | undefined;
              repeatLimit?: number | null | undefined;
              scheduledId?: string | null | undefined;
              retries?: number | null | undefined;
              lastUpdate?: any | null | undefined;
              endpoint?: string | null | undefined;
              executionCounter?: number | null | undefined;
            }
          | null
          | undefined
        >
      | null
      | undefined;
  };

  export type AbortProcessInstanceMutationVariables = Exact<{
    processId?: InputMaybe<Scalars['String']>;
  }>;

  export type AbortProcessInstanceMutation = {
    __typename?: 'Mutation';
    ProcessInstanceAbort?: string | null | undefined;
  };

  export type SkipProcessInstanceMutationVariables = Exact<{
    processId?: InputMaybe<Scalars['String']>;
  }>;

  export type SkipProcessInstanceMutation = {
    __typename?: 'Mutation';
    ProcessInstanceSkip?: string | null | undefined;
  };

  export type RetryProcessInstanceMutationVariables = Exact<{
    processId?: InputMaybe<Scalars['String']>;
  }>;

  export type RetryProcessInstanceMutation = {
    __typename?: 'Mutation';
    ProcessInstanceRetry?: string | null | undefined;
  };

  export type GetProcessInstanceSvgQueryVariables = Exact<{
    processId?: InputMaybe<Scalars['String']>;
  }>;

  export type GetProcessInstanceSvgQuery = {
    __typename?: 'Query';
    ProcessInstances?:
      | Array<
          | {
              __typename?: 'ProcessInstance';
              diagram?: string | null | undefined;
            }
          | null
          | undefined
        >
      | null
      | undefined;
  };

  export type GetProcessInstanceNodeDefinitionsQueryVariables = Exact<{
    processId?: InputMaybe<Scalars['String']>;
  }>;

  export type GetProcessInstanceNodeDefinitionsQuery = {
    __typename?: 'Query';
    ProcessInstances?:
      | Array<
          | {
              __typename?: 'ProcessInstance';
              nodeDefinitions?:
                | Array<{
                    __typename?: 'Node';
                    id: string;
                    name: string;
                    type: string;
                    uniqueId: string;
                    nodeDefinitionId: string;
                  }>
                | null
                | undefined;
            }
          | null
          | undefined
        >
      | null
      | undefined;
  };

  export type HandleNodeTriggerMutationVariables = Exact<{
    processId?: InputMaybe<Scalars['String']>;
    nodeId?: InputMaybe<Scalars['String']>;
  }>;

  export type HandleNodeTriggerMutation = {
    __typename?: 'Mutation';
    NodeInstanceTrigger?: string | null | undefined;
  };

  export type HandleNodeInstanceCancelMutationVariables = Exact<{
    processId?: InputMaybe<Scalars['String']>;
    nodeInstanceId?: InputMaybe<Scalars['String']>;
  }>;

  export type HandleNodeInstanceCancelMutation = {
    __typename?: 'Mutation';
    NodeInstanceCancel?: string | null | undefined;
  };

  export type HandleNodeInstanceRetriggerMutationVariables = Exact<{
    processId?: InputMaybe<Scalars['String']>;
    nodeInstanceId?: InputMaybe<Scalars['String']>;
  }>;

  export type HandleNodeInstanceRetriggerMutation = {
    __typename?: 'Mutation';
    NodeInstanceRetrigger?: string | null | undefined;
  };

  export type HandleProcessVariableUpdateMutationVariables = Exact<{
    processId?: InputMaybe<Scalars['String']>;
    processInstanceVariables?: InputMaybe<Scalars['String']>;
  }>;

  export type HandleProcessVariableUpdateMutation = {
    __typename?: 'Mutation';
    ProcessInstanceUpdateVariables?: string | null | undefined;
  };

  export type JobCancelMutationVariables = Exact<{
    jobId?: InputMaybe<Scalars['String']>;
  }>;

  export type JobCancelMutation = {
    __typename?: 'Mutation';
    JobCancel?: string | null | undefined;
  };

  export type HandleJobRescheduleMutationVariables = Exact<{
    jobId?: InputMaybe<Scalars['String']>;
    data?: InputMaybe<Scalars['String']>;
  }>;

  export type HandleJobRescheduleMutation = {
    __typename?: 'Mutation';
    JobReschedule?: string | null | undefined;
  };

  export const GetProcessInstancesDocument = gql`
    query getProcessInstances(
      $where: ProcessInstanceArgument
      $offset: Int
      $limit: Int
      $orderBy: ProcessInstanceOrderBy
    ) {
      ProcessInstances(
        where: $where
        pagination: { offset: $offset, limit: $limit }
        orderBy: $orderBy
      ) {
        id
        processId
        processName
        parentProcessInstanceId
        rootProcessInstanceId
        roles
        state
        start
        lastUpdate
        addons
        businessKey
        serviceUrl
        error {
          nodeDefinitionId
          message
        }
      }
    }
  `;

  /**
   * __useGetProcessInstancesQuery__
   *
   * To run a query within a React component, call `useGetProcessInstancesQuery` and pass it any options that fit your needs.
   * When your component renders, `useGetProcessInstancesQuery` returns an object from Apollo Client that contains loading, error, and data properties
   * you can use to render your UI.
   *
   * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
   *
   * @example
   * const { data, loading, error } = useGetProcessInstancesQuery({
   *   variables: {
   *      where: // value for 'where'
   *      offset: // value for 'offset'
   *      limit: // value for 'limit'
   *      orderBy: // value for 'orderBy'
   *   },
   * });
   */
  export function useGetProcessInstancesQuery(
    baseOptions?: ApolloReactHooks.QueryHookOptions<
      GetProcessInstancesQuery,
      GetProcessInstancesQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useQuery<
      GetProcessInstancesQuery,
      GetProcessInstancesQueryVariables
    >(GetProcessInstancesDocument, options);
  }
  export function useGetProcessInstancesLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetProcessInstancesQuery,
      GetProcessInstancesQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useLazyQuery<
      GetProcessInstancesQuery,
      GetProcessInstancesQueryVariables
    >(GetProcessInstancesDocument, options);
  }
  export type GetProcessInstancesQueryHookResult = ReturnType<
    typeof useGetProcessInstancesQuery
  >;
  export type GetProcessInstancesLazyQueryHookResult = ReturnType<
    typeof useGetProcessInstancesLazyQuery
  >;
  export type GetProcessInstancesQueryResult = ApolloReactCommon.QueryResult<
    GetProcessInstancesQuery,
    GetProcessInstancesQueryVariables
  >;
  export const GetChildInstancesDocument = gql`
    query getChildInstances($rootProcessInstanceId: String) {
      ProcessInstances(
        where: { rootProcessInstanceId: { equal: $rootProcessInstanceId } }
      ) {
        id
        processId
        processName
        parentProcessInstanceId
        rootProcessInstanceId
        roles
        state
        start
        lastUpdate
        serviceUrl
        addons
        businessKey
        error {
          nodeDefinitionId
          message
        }
      }
    }
  `;

  /**
   * __useGetChildInstancesQuery__
   *
   * To run a query within a React component, call `useGetChildInstancesQuery` and pass it any options that fit your needs.
   * When your component renders, `useGetChildInstancesQuery` returns an object from Apollo Client that contains loading, error, and data properties
   * you can use to render your UI.
   *
   * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
   *
   * @example
   * const { data, loading, error } = useGetChildInstancesQuery({
   *   variables: {
   *      rootProcessInstanceId: // value for 'rootProcessInstanceId'
   *   },
   * });
   */
  export function useGetChildInstancesQuery(
    baseOptions?: ApolloReactHooks.QueryHookOptions<
      GetChildInstancesQuery,
      GetChildInstancesQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useQuery<
      GetChildInstancesQuery,
      GetChildInstancesQueryVariables
    >(GetChildInstancesDocument, options);
  }
  export function useGetChildInstancesLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetChildInstancesQuery,
      GetChildInstancesQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useLazyQuery<
      GetChildInstancesQuery,
      GetChildInstancesQueryVariables
    >(GetChildInstancesDocument, options);
  }
  export type GetChildInstancesQueryHookResult = ReturnType<
    typeof useGetChildInstancesQuery
  >;
  export type GetChildInstancesLazyQueryHookResult = ReturnType<
    typeof useGetChildInstancesLazyQuery
  >;
  export type GetChildInstancesQueryResult = ApolloReactCommon.QueryResult<
    GetChildInstancesQuery,
    GetChildInstancesQueryVariables
  >;
  export const GetProcessInstanceByIdDocument = gql`
    query getProcessInstanceById($id: String) {
      ProcessInstances(where: { id: { equal: $id } }) {
        id
        processId
        processName
        businessKey
        parentProcessInstanceId
        parentProcessInstance {
          id
          processName
          businessKey
        }
        roles
        variables
        state
        start
        lastUpdate
        end
        addons
        endpoint
        addons
        serviceUrl
        error {
          nodeDefinitionId
          message
        }
        childProcessInstances {
          id
          processName
          businessKey
        }
        nodes {
          id
          nodeId
          name
          enter
          exit
          type
          definitionId
        }
        milestones {
          id
          name
          status
        }
      }
    }
  `;

  /**
   * __useGetProcessInstanceByIdQuery__
   *
   * To run a query within a React component, call `useGetProcessInstanceByIdQuery` and pass it any options that fit your needs.
   * When your component renders, `useGetProcessInstanceByIdQuery` returns an object from Apollo Client that contains loading, error, and data properties
   * you can use to render your UI.
   *
   * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
   *
   * @example
   * const { data, loading, error } = useGetProcessInstanceByIdQuery({
   *   variables: {
   *      id: // value for 'id'
   *   },
   * });
   */
  export function useGetProcessInstanceByIdQuery(
    baseOptions?: ApolloReactHooks.QueryHookOptions<
      GetProcessInstanceByIdQuery,
      GetProcessInstanceByIdQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useQuery<
      GetProcessInstanceByIdQuery,
      GetProcessInstanceByIdQueryVariables
    >(GetProcessInstanceByIdDocument, options);
  }
  export function useGetProcessInstanceByIdLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetProcessInstanceByIdQuery,
      GetProcessInstanceByIdQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useLazyQuery<
      GetProcessInstanceByIdQuery,
      GetProcessInstanceByIdQueryVariables
    >(GetProcessInstanceByIdDocument, options);
  }
  export type GetProcessInstanceByIdQueryHookResult = ReturnType<
    typeof useGetProcessInstanceByIdQuery
  >;
  export type GetProcessInstanceByIdLazyQueryHookResult = ReturnType<
    typeof useGetProcessInstanceByIdLazyQuery
  >;
  export type GetProcessInstanceByIdQueryResult = ApolloReactCommon.QueryResult<
    GetProcessInstanceByIdQuery,
    GetProcessInstanceByIdQueryVariables
  >;
  export const GetColumnPickerAttributesDocument = gql`
    query getColumnPickerAttributes($columnPickerType: String!) {
      __type(name: $columnPickerType) {
        name
        fields {
          name
          type {
            name
            kind
            fields {
              name
              type {
                name
                kind
              }
            }
          }
        }
      }
    }
  `;

  /**
   * __useGetColumnPickerAttributesQuery__
   *
   * To run a query within a React component, call `useGetColumnPickerAttributesQuery` and pass it any options that fit your needs.
   * When your component renders, `useGetColumnPickerAttributesQuery` returns an object from Apollo Client that contains loading, error, and data properties
   * you can use to render your UI.
   *
   * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
   *
   * @example
   * const { data, loading, error } = useGetColumnPickerAttributesQuery({
   *   variables: {
   *      columnPickerType: // value for 'columnPickerType'
   *   },
   * });
   */
  export function useGetColumnPickerAttributesQuery(
    baseOptions: ApolloReactHooks.QueryHookOptions<
      GetColumnPickerAttributesQuery,
      GetColumnPickerAttributesQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useQuery<
      GetColumnPickerAttributesQuery,
      GetColumnPickerAttributesQueryVariables
    >(GetColumnPickerAttributesDocument, options);
  }
  export function useGetColumnPickerAttributesLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetColumnPickerAttributesQuery,
      GetColumnPickerAttributesQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useLazyQuery<
      GetColumnPickerAttributesQuery,
      GetColumnPickerAttributesQueryVariables
    >(GetColumnPickerAttributesDocument, options);
  }
  export type GetColumnPickerAttributesQueryHookResult = ReturnType<
    typeof useGetColumnPickerAttributesQuery
  >;
  export type GetColumnPickerAttributesLazyQueryHookResult = ReturnType<
    typeof useGetColumnPickerAttributesLazyQuery
  >;
  export type GetColumnPickerAttributesQueryResult = ApolloReactCommon.QueryResult<
    GetColumnPickerAttributesQuery,
    GetColumnPickerAttributesQueryVariables
  >;
  export const GetQueryTypesDocument = gql`
    query getQueryTypes {
      __schema {
        queryType: types {
          name
          kind
          fields {
            name
            type {
              name
              kind
            }
          }
          inputFields {
            name
            type {
              name
              kind
            }
          }
        }
      }
    }
  `;

  /**
   * __useGetQueryTypesQuery__
   *
   * To run a query within a React component, call `useGetQueryTypesQuery` and pass it any options that fit your needs.
   * When your component renders, `useGetQueryTypesQuery` returns an object from Apollo Client that contains loading, error, and data properties
   * you can use to render your UI.
   *
   * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
   *
   * @example
   * const { data, loading, error } = useGetQueryTypesQuery({
   *   variables: {
   *   },
   * });
   */
  export function useGetQueryTypesQuery(
    baseOptions?: ApolloReactHooks.QueryHookOptions<
      GetQueryTypesQuery,
      GetQueryTypesQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useQuery<
      GetQueryTypesQuery,
      GetQueryTypesQueryVariables
    >(GetQueryTypesDocument, options);
  }
  export function useGetQueryTypesLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetQueryTypesQuery,
      GetQueryTypesQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useLazyQuery<
      GetQueryTypesQuery,
      GetQueryTypesQueryVariables
    >(GetQueryTypesDocument, options);
  }
  export type GetQueryTypesQueryHookResult = ReturnType<
    typeof useGetQueryTypesQuery
  >;
  export type GetQueryTypesLazyQueryHookResult = ReturnType<
    typeof useGetQueryTypesLazyQuery
  >;
  export type GetQueryTypesQueryResult = ApolloReactCommon.QueryResult<
    GetQueryTypesQuery,
    GetQueryTypesQueryVariables
  >;
  export const GetQueryFieldsDocument = gql`
    query getQueryFields {
      __type(name: "Query") {
        name
        fields {
          name
          args {
            name
            type {
              kind
              name
            }
          }
          type {
            ofType {
              name
            }
          }
        }
      }
    }
  `;

  /**
   * __useGetQueryFieldsQuery__
   *
   * To run a query within a React component, call `useGetQueryFieldsQuery` and pass it any options that fit your needs.
   * When your component renders, `useGetQueryFieldsQuery` returns an object from Apollo Client that contains loading, error, and data properties
   * you can use to render your UI.
   *
   * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
   *
   * @example
   * const { data, loading, error } = useGetQueryFieldsQuery({
   *   variables: {
   *   },
   * });
   */
  export function useGetQueryFieldsQuery(
    baseOptions?: ApolloReactHooks.QueryHookOptions<
      GetQueryFieldsQuery,
      GetQueryFieldsQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useQuery<
      GetQueryFieldsQuery,
      GetQueryFieldsQueryVariables
    >(GetQueryFieldsDocument, options);
  }
  export function useGetQueryFieldsLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetQueryFieldsQuery,
      GetQueryFieldsQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useLazyQuery<
      GetQueryFieldsQuery,
      GetQueryFieldsQueryVariables
    >(GetQueryFieldsDocument, options);
  }
  export type GetQueryFieldsQueryHookResult = ReturnType<
    typeof useGetQueryFieldsQuery
  >;
  export type GetQueryFieldsLazyQueryHookResult = ReturnType<
    typeof useGetQueryFieldsLazyQuery
  >;
  export type GetQueryFieldsQueryResult = ApolloReactCommon.QueryResult<
    GetQueryFieldsQuery,
    GetQueryFieldsQueryVariables
  >;
  export const GetInputFieldsFromQueryDocument = gql`
    query getInputFieldsFromQuery($currentQuery: String!) {
      __type(name: $currentQuery) {
        name
        inputFields {
          name
          type {
            name
            kind
            inputFields {
              name
              type {
                name
              }
            }
          }
        }
      }
    }
  `;

  /**
   * __useGetInputFieldsFromQueryQuery__
   *
   * To run a query within a React component, call `useGetInputFieldsFromQueryQuery` and pass it any options that fit your needs.
   * When your component renders, `useGetInputFieldsFromQueryQuery` returns an object from Apollo Client that contains loading, error, and data properties
   * you can use to render your UI.
   *
   * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
   *
   * @example
   * const { data, loading, error } = useGetInputFieldsFromQueryQuery({
   *   variables: {
   *      currentQuery: // value for 'currentQuery'
   *   },
   * });
   */
  export function useGetInputFieldsFromQueryQuery(
    baseOptions: ApolloReactHooks.QueryHookOptions<
      GetInputFieldsFromQueryQuery,
      GetInputFieldsFromQueryQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useQuery<
      GetInputFieldsFromQueryQuery,
      GetInputFieldsFromQueryQueryVariables
    >(GetInputFieldsFromQueryDocument, options);
  }
  export function useGetInputFieldsFromQueryLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetInputFieldsFromQueryQuery,
      GetInputFieldsFromQueryQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useLazyQuery<
      GetInputFieldsFromQueryQuery,
      GetInputFieldsFromQueryQueryVariables
    >(GetInputFieldsFromQueryDocument, options);
  }
  export type GetInputFieldsFromQueryQueryHookResult = ReturnType<
    typeof useGetInputFieldsFromQueryQuery
  >;
  export type GetInputFieldsFromQueryLazyQueryHookResult = ReturnType<
    typeof useGetInputFieldsFromQueryLazyQuery
  >;
  export type GetInputFieldsFromQueryQueryResult = ApolloReactCommon.QueryResult<
    GetInputFieldsFromQueryQuery,
    GetInputFieldsFromQueryQueryVariables
  >;
  export const GetInputFieldsFromTypeDocument = gql`
    query getInputFieldsFromType($type: String!) {
      __type(name: $type) {
        name
        inputFields {
          name
          type {
            name
            kind
            enumValues {
              name
            }
            ofType {
              kind
              name
              enumValues {
                name
              }
            }
          }
        }
      }
    }
  `;

  /**
   * __useGetInputFieldsFromTypeQuery__
   *
   * To run a query within a React component, call `useGetInputFieldsFromTypeQuery` and pass it any options that fit your needs.
   * When your component renders, `useGetInputFieldsFromTypeQuery` returns an object from Apollo Client that contains loading, error, and data properties
   * you can use to render your UI.
   *
   * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
   *
   * @example
   * const { data, loading, error } = useGetInputFieldsFromTypeQuery({
   *   variables: {
   *      type: // value for 'type'
   *   },
   * });
   */
  export function useGetInputFieldsFromTypeQuery(
    baseOptions: ApolloReactHooks.QueryHookOptions<
      GetInputFieldsFromTypeQuery,
      GetInputFieldsFromTypeQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useQuery<
      GetInputFieldsFromTypeQuery,
      GetInputFieldsFromTypeQueryVariables
    >(GetInputFieldsFromTypeDocument, options);
  }
  export function useGetInputFieldsFromTypeLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetInputFieldsFromTypeQuery,
      GetInputFieldsFromTypeQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useLazyQuery<
      GetInputFieldsFromTypeQuery,
      GetInputFieldsFromTypeQueryVariables
    >(GetInputFieldsFromTypeDocument, options);
  }
  export type GetInputFieldsFromTypeQueryHookResult = ReturnType<
    typeof useGetInputFieldsFromTypeQuery
  >;
  export type GetInputFieldsFromTypeLazyQueryHookResult = ReturnType<
    typeof useGetInputFieldsFromTypeLazyQuery
  >;
  export type GetInputFieldsFromTypeQueryResult = ApolloReactCommon.QueryResult<
    GetInputFieldsFromTypeQuery,
    GetInputFieldsFromTypeQueryVariables
  >;
  export const GetUserTasksByStatesDocument = gql`
    query getUserTasksByStates(
      $state: [String!]
      $orderBy: UserTaskInstanceOrderBy
    ) {
      UserTaskInstances(where: { state: { in: $state } }, orderBy: $orderBy) {
        id
        name
        referenceName
        description
        name
        priority
        processInstanceId
        processId
        rootProcessInstanceId
        rootProcessId
        state
        actualOwner
        adminGroups
        adminUsers
        completed
        started
        excludedUsers
        potentialGroups
        potentialUsers
        inputs
        outputs
        referenceName
        endpoint
      }
    }
  `;

  /**
   * __useGetUserTasksByStatesQuery__
   *
   * To run a query within a React component, call `useGetUserTasksByStatesQuery` and pass it any options that fit your needs.
   * When your component renders, `useGetUserTasksByStatesQuery` returns an object from Apollo Client that contains loading, error, and data properties
   * you can use to render your UI.
   *
   * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
   *
   * @example
   * const { data, loading, error } = useGetUserTasksByStatesQuery({
   *   variables: {
   *      state: // value for 'state'
   *      orderBy: // value for 'orderBy'
   *   },
   * });
   */
  export function useGetUserTasksByStatesQuery(
    baseOptions?: ApolloReactHooks.QueryHookOptions<
      GetUserTasksByStatesQuery,
      GetUserTasksByStatesQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useQuery<
      GetUserTasksByStatesQuery,
      GetUserTasksByStatesQueryVariables
    >(GetUserTasksByStatesDocument, options);
  }
  export function useGetUserTasksByStatesLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetUserTasksByStatesQuery,
      GetUserTasksByStatesQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useLazyQuery<
      GetUserTasksByStatesQuery,
      GetUserTasksByStatesQueryVariables
    >(GetUserTasksByStatesDocument, options);
  }
  export type GetUserTasksByStatesQueryHookResult = ReturnType<
    typeof useGetUserTasksByStatesQuery
  >;
  export type GetUserTasksByStatesLazyQueryHookResult = ReturnType<
    typeof useGetUserTasksByStatesLazyQuery
  >;
  export type GetUserTasksByStatesQueryResult = ApolloReactCommon.QueryResult<
    GetUserTasksByStatesQuery,
    GetUserTasksByStatesQueryVariables
  >;
  export const GetUserTaskByIdDocument = gql`
    query getUserTaskById($id: String) {
      UserTaskInstances(where: { id: { equal: $id } }) {
        id
        description
        name
        priority
        processInstanceId
        processId
        rootProcessInstanceId
        rootProcessId
        state
        actualOwner
        adminGroups
        adminUsers
        completed
        started
        excludedUsers
        potentialGroups
        potentialUsers
        inputs
        outputs
        referenceName
        endpoint
        lastUpdate
      }
    }
  `;

  /**
   * __useGetUserTaskByIdQuery__
   *
   * To run a query within a React component, call `useGetUserTaskByIdQuery` and pass it any options that fit your needs.
   * When your component renders, `useGetUserTaskByIdQuery` returns an object from Apollo Client that contains loading, error, and data properties
   * you can use to render your UI.
   *
   * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
   *
   * @example
   * const { data, loading, error } = useGetUserTaskByIdQuery({
   *   variables: {
   *      id: // value for 'id'
   *   },
   * });
   */
  export function useGetUserTaskByIdQuery(
    baseOptions?: ApolloReactHooks.QueryHookOptions<
      GetUserTaskByIdQuery,
      GetUserTaskByIdQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useQuery<
      GetUserTaskByIdQuery,
      GetUserTaskByIdQueryVariables
    >(GetUserTaskByIdDocument, options);
  }
  export function useGetUserTaskByIdLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetUserTaskByIdQuery,
      GetUserTaskByIdQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useLazyQuery<
      GetUserTaskByIdQuery,
      GetUserTaskByIdQueryVariables
    >(GetUserTaskByIdDocument, options);
  }
  export type GetUserTaskByIdQueryHookResult = ReturnType<
    typeof useGetUserTaskByIdQuery
  >;
  export type GetUserTaskByIdLazyQueryHookResult = ReturnType<
    typeof useGetUserTaskByIdLazyQuery
  >;
  export type GetUserTaskByIdQueryResult = ApolloReactCommon.QueryResult<
    GetUserTaskByIdQuery,
    GetUserTaskByIdQueryVariables
  >;
  export const GetTasksForUserDocument = gql`
    query getTasksForUser(
      $whereArgument: UserTaskInstanceArgument
      $offset: Int
      $limit: Int
      $orderBy: UserTaskInstanceOrderBy
    ) {
      UserTaskInstances(
        where: $whereArgument
        pagination: { offset: $offset, limit: $limit }
        orderBy: $orderBy
      ) {
        id
        name
        referenceName
        description
        priority
        processInstanceId
        processId
        rootProcessInstanceId
        rootProcessId
        state
        actualOwner
        adminGroups
        adminUsers
        completed
        started
        excludedUsers
        potentialGroups
        potentialUsers
        inputs
        outputs
        lastUpdate
        endpoint
      }
    }
  `;

  /**
   * __useGetTasksForUserQuery__
   *
   * To run a query within a React component, call `useGetTasksForUserQuery` and pass it any options that fit your needs.
   * When your component renders, `useGetTasksForUserQuery` returns an object from Apollo Client that contains loading, error, and data properties
   * you can use to render your UI.
   *
   * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
   *
   * @example
   * const { data, loading, error } = useGetTasksForUserQuery({
   *   variables: {
   *      whereArgument: // value for 'whereArgument'
   *      offset: // value for 'offset'
   *      limit: // value for 'limit'
   *      orderBy: // value for 'orderBy'
   *   },
   * });
   */
  export function useGetTasksForUserQuery(
    baseOptions?: ApolloReactHooks.QueryHookOptions<
      GetTasksForUserQuery,
      GetTasksForUserQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useQuery<
      GetTasksForUserQuery,
      GetTasksForUserQueryVariables
    >(GetTasksForUserDocument, options);
  }
  export function useGetTasksForUserLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetTasksForUserQuery,
      GetTasksForUserQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useLazyQuery<
      GetTasksForUserQuery,
      GetTasksForUserQueryVariables
    >(GetTasksForUserDocument, options);
  }
  export type GetTasksForUserQueryHookResult = ReturnType<
    typeof useGetTasksForUserQuery
  >;
  export type GetTasksForUserLazyQueryHookResult = ReturnType<
    typeof useGetTasksForUserLazyQuery
  >;
  export type GetTasksForUserQueryResult = ApolloReactCommon.QueryResult<
    GetTasksForUserQuery,
    GetTasksForUserQueryVariables
  >;
  export const GetJobsByProcessInstanceIdDocument = gql`
    query getJobsByProcessInstanceId($processInstanceId: String) {
      Jobs(where: { processInstanceId: { equal: $processInstanceId } }) {
        id
        processId
        processInstanceId
        rootProcessId
        status
        expirationTime
        priority
        callbackEndpoint
        repeatInterval
        repeatLimit
        scheduledId
        retries
        lastUpdate
        endpoint
        nodeInstanceId
        executionCounter
      }
    }
  `;

  /**
   * __useGetJobsByProcessInstanceIdQuery__
   *
   * To run a query within a React component, call `useGetJobsByProcessInstanceIdQuery` and pass it any options that fit your needs.
   * When your component renders, `useGetJobsByProcessInstanceIdQuery` returns an object from Apollo Client that contains loading, error, and data properties
   * you can use to render your UI.
   *
   * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
   *
   * @example
   * const { data, loading, error } = useGetJobsByProcessInstanceIdQuery({
   *   variables: {
   *      processInstanceId: // value for 'processInstanceId'
   *   },
   * });
   */
  export function useGetJobsByProcessInstanceIdQuery(
    baseOptions?: ApolloReactHooks.QueryHookOptions<
      GetJobsByProcessInstanceIdQuery,
      GetJobsByProcessInstanceIdQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useQuery<
      GetJobsByProcessInstanceIdQuery,
      GetJobsByProcessInstanceIdQueryVariables
    >(GetJobsByProcessInstanceIdDocument, options);
  }
  export function useGetJobsByProcessInstanceIdLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetJobsByProcessInstanceIdQuery,
      GetJobsByProcessInstanceIdQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useLazyQuery<
      GetJobsByProcessInstanceIdQuery,
      GetJobsByProcessInstanceIdQueryVariables
    >(GetJobsByProcessInstanceIdDocument, options);
  }
  export type GetJobsByProcessInstanceIdQueryHookResult = ReturnType<
    typeof useGetJobsByProcessInstanceIdQuery
  >;
  export type GetJobsByProcessInstanceIdLazyQueryHookResult = ReturnType<
    typeof useGetJobsByProcessInstanceIdLazyQuery
  >;
  export type GetJobsByProcessInstanceIdQueryResult = ApolloReactCommon.QueryResult<
    GetJobsByProcessInstanceIdQuery,
    GetJobsByProcessInstanceIdQueryVariables
  >;
  export const GetJobsWithFiltersDocument = gql`
    query getJobsWithFilters(
      $values: [JobStatus]
      $orderBy: JobOrderBy
      $offset: Int
      $limit: Int
    ) {
      Jobs(
        where: { status: { in: $values } }
        orderBy: $orderBy
        pagination: { offset: $offset, limit: $limit }
      ) {
        id
        processId
        processInstanceId
        rootProcessId
        status
        expirationTime
        priority
        callbackEndpoint
        repeatInterval
        repeatLimit
        scheduledId
        retries
        lastUpdate
        endpoint
        executionCounter
      }
    }
  `;

  /**
   * __useGetJobsWithFiltersQuery__
   *
   * To run a query within a React component, call `useGetJobsWithFiltersQuery` and pass it any options that fit your needs.
   * When your component renders, `useGetJobsWithFiltersQuery` returns an object from Apollo Client that contains loading, error, and data properties
   * you can use to render your UI.
   *
   * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
   *
   * @example
   * const { data, loading, error } = useGetJobsWithFiltersQuery({
   *   variables: {
   *      values: // value for 'values'
   *      orderBy: // value for 'orderBy'
   *      offset: // value for 'offset'
   *      limit: // value for 'limit'
   *   },
   * });
   */
  export function useGetJobsWithFiltersQuery(
    baseOptions?: ApolloReactHooks.QueryHookOptions<
      GetJobsWithFiltersQuery,
      GetJobsWithFiltersQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useQuery<
      GetJobsWithFiltersQuery,
      GetJobsWithFiltersQueryVariables
    >(GetJobsWithFiltersDocument, options);
  }
  export function useGetJobsWithFiltersLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetJobsWithFiltersQuery,
      GetJobsWithFiltersQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useLazyQuery<
      GetJobsWithFiltersQuery,
      GetJobsWithFiltersQueryVariables
    >(GetJobsWithFiltersDocument, options);
  }
  export type GetJobsWithFiltersQueryHookResult = ReturnType<
    typeof useGetJobsWithFiltersQuery
  >;
  export type GetJobsWithFiltersLazyQueryHookResult = ReturnType<
    typeof useGetJobsWithFiltersLazyQuery
  >;
  export type GetJobsWithFiltersQueryResult = ApolloReactCommon.QueryResult<
    GetJobsWithFiltersQuery,
    GetJobsWithFiltersQueryVariables
  >;
  export const AbortProcessInstanceDocument = gql`
    mutation abortProcessInstance($processId: String) {
      ProcessInstanceAbort(id: $processId)
    }
  `;
  export type AbortProcessInstanceMutationFn = ApolloReactCommon.MutationFunction<
    AbortProcessInstanceMutation,
    AbortProcessInstanceMutationVariables
  >;

  /**
   * __useAbortProcessInstanceMutation__
   *
   * To run a mutation, you first call `useAbortProcessInstanceMutation` within a React component and pass it any options that fit your needs.
   * When your component renders, `useAbortProcessInstanceMutation` returns a tuple that includes:
   * - A mutate function that you can call at any time to execute the mutation
   * - An object with fields that represent the current status of the mutation's execution
   *
   * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
   *
   * @example
   * const [abortProcessInstanceMutation, { data, loading, error }] = useAbortProcessInstanceMutation({
   *   variables: {
   *      processId: // value for 'processId'
   *   },
   * });
   */
  export function useAbortProcessInstanceMutation(
    baseOptions?: ApolloReactHooks.MutationHookOptions<
      AbortProcessInstanceMutation,
      AbortProcessInstanceMutationVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useMutation<
      AbortProcessInstanceMutation,
      AbortProcessInstanceMutationVariables
    >(AbortProcessInstanceDocument, options);
  }
  export type AbortProcessInstanceMutationHookResult = ReturnType<
    typeof useAbortProcessInstanceMutation
  >;
  export type AbortProcessInstanceMutationResult = ApolloReactCommon.MutationResult<
    AbortProcessInstanceMutation
  >;
  export type AbortProcessInstanceMutationOptions = ApolloReactCommon.BaseMutationOptions<
    AbortProcessInstanceMutation,
    AbortProcessInstanceMutationVariables
  >;
  export const SkipProcessInstanceDocument = gql`
    mutation skipProcessInstance($processId: String) {
      ProcessInstanceSkip(id: $processId)
    }
  `;
  export type SkipProcessInstanceMutationFn = ApolloReactCommon.MutationFunction<
    SkipProcessInstanceMutation,
    SkipProcessInstanceMutationVariables
  >;

  /**
   * __useSkipProcessInstanceMutation__
   *
   * To run a mutation, you first call `useSkipProcessInstanceMutation` within a React component and pass it any options that fit your needs.
   * When your component renders, `useSkipProcessInstanceMutation` returns a tuple that includes:
   * - A mutate function that you can call at any time to execute the mutation
   * - An object with fields that represent the current status of the mutation's execution
   *
   * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
   *
   * @example
   * const [skipProcessInstanceMutation, { data, loading, error }] = useSkipProcessInstanceMutation({
   *   variables: {
   *      processId: // value for 'processId'
   *   },
   * });
   */
  export function useSkipProcessInstanceMutation(
    baseOptions?: ApolloReactHooks.MutationHookOptions<
      SkipProcessInstanceMutation,
      SkipProcessInstanceMutationVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useMutation<
      SkipProcessInstanceMutation,
      SkipProcessInstanceMutationVariables
    >(SkipProcessInstanceDocument, options);
  }
  export type SkipProcessInstanceMutationHookResult = ReturnType<
    typeof useSkipProcessInstanceMutation
  >;
  export type SkipProcessInstanceMutationResult = ApolloReactCommon.MutationResult<
    SkipProcessInstanceMutation
  >;
  export type SkipProcessInstanceMutationOptions = ApolloReactCommon.BaseMutationOptions<
    SkipProcessInstanceMutation,
    SkipProcessInstanceMutationVariables
  >;
  export const RetryProcessInstanceDocument = gql`
    mutation retryProcessInstance($processId: String) {
      ProcessInstanceRetry(id: $processId)
    }
  `;
  export type RetryProcessInstanceMutationFn = ApolloReactCommon.MutationFunction<
    RetryProcessInstanceMutation,
    RetryProcessInstanceMutationVariables
  >;

  /**
   * __useRetryProcessInstanceMutation__
   *
   * To run a mutation, you first call `useRetryProcessInstanceMutation` within a React component and pass it any options that fit your needs.
   * When your component renders, `useRetryProcessInstanceMutation` returns a tuple that includes:
   * - A mutate function that you can call at any time to execute the mutation
   * - An object with fields that represent the current status of the mutation's execution
   *
   * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
   *
   * @example
   * const [retryProcessInstanceMutation, { data, loading, error }] = useRetryProcessInstanceMutation({
   *   variables: {
   *      processId: // value for 'processId'
   *   },
   * });
   */
  export function useRetryProcessInstanceMutation(
    baseOptions?: ApolloReactHooks.MutationHookOptions<
      RetryProcessInstanceMutation,
      RetryProcessInstanceMutationVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useMutation<
      RetryProcessInstanceMutation,
      RetryProcessInstanceMutationVariables
    >(RetryProcessInstanceDocument, options);
  }
  export type RetryProcessInstanceMutationHookResult = ReturnType<
    typeof useRetryProcessInstanceMutation
  >;
  export type RetryProcessInstanceMutationResult = ApolloReactCommon.MutationResult<
    RetryProcessInstanceMutation
  >;
  export type RetryProcessInstanceMutationOptions = ApolloReactCommon.BaseMutationOptions<
    RetryProcessInstanceMutation,
    RetryProcessInstanceMutationVariables
  >;
  export const GetProcessInstanceSvgDocument = gql`
    query getProcessInstanceSVG($processId: String) {
      ProcessInstances(where: { id: { equal: $processId } }) {
        diagram
      }
    }
  `;

  /**
   * __useGetProcessInstanceSvgQuery__
   *
   * To run a query within a React component, call `useGetProcessInstanceSvgQuery` and pass it any options that fit your needs.
   * When your component renders, `useGetProcessInstanceSvgQuery` returns an object from Apollo Client that contains loading, error, and data properties
   * you can use to render your UI.
   *
   * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
   *
   * @example
   * const { data, loading, error } = useGetProcessInstanceSvgQuery({
   *   variables: {
   *      processId: // value for 'processId'
   *   },
   * });
   */
  export function useGetProcessInstanceSvgQuery(
    baseOptions?: ApolloReactHooks.QueryHookOptions<
      GetProcessInstanceSvgQuery,
      GetProcessInstanceSvgQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useQuery<
      GetProcessInstanceSvgQuery,
      GetProcessInstanceSvgQueryVariables
    >(GetProcessInstanceSvgDocument, options);
  }
  export function useGetProcessInstanceSvgLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetProcessInstanceSvgQuery,
      GetProcessInstanceSvgQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useLazyQuery<
      GetProcessInstanceSvgQuery,
      GetProcessInstanceSvgQueryVariables
    >(GetProcessInstanceSvgDocument, options);
  }
  export type GetProcessInstanceSvgQueryHookResult = ReturnType<
    typeof useGetProcessInstanceSvgQuery
  >;
  export type GetProcessInstanceSvgLazyQueryHookResult = ReturnType<
    typeof useGetProcessInstanceSvgLazyQuery
  >;
  export type GetProcessInstanceSvgQueryResult = ApolloReactCommon.QueryResult<
    GetProcessInstanceSvgQuery,
    GetProcessInstanceSvgQueryVariables
  >;
  export const GetProcessInstanceNodeDefinitionsDocument = gql`
    query getProcessInstanceNodeDefinitions($processId: String) {
      ProcessInstances(where: { id: { equal: $processId } }) {
        nodeDefinitions {
          id
          name
          type
          uniqueId
          nodeDefinitionId
        }
      }
    }
  `;

  /**
   * __useGetProcessInstanceNodeDefinitionsQuery__
   *
   * To run a query within a React component, call `useGetProcessInstanceNodeDefinitionsQuery` and pass it any options that fit your needs.
   * When your component renders, `useGetProcessInstanceNodeDefinitionsQuery` returns an object from Apollo Client that contains loading, error, and data properties
   * you can use to render your UI.
   *
   * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
   *
   * @example
   * const { data, loading, error } = useGetProcessInstanceNodeDefinitionsQuery({
   *   variables: {
   *      processId: // value for 'processId'
   *   },
   * });
   */
  export function useGetProcessInstanceNodeDefinitionsQuery(
    baseOptions?: ApolloReactHooks.QueryHookOptions<
      GetProcessInstanceNodeDefinitionsQuery,
      GetProcessInstanceNodeDefinitionsQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useQuery<
      GetProcessInstanceNodeDefinitionsQuery,
      GetProcessInstanceNodeDefinitionsQueryVariables
    >(GetProcessInstanceNodeDefinitionsDocument, options);
  }
  export function useGetProcessInstanceNodeDefinitionsLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetProcessInstanceNodeDefinitionsQuery,
      GetProcessInstanceNodeDefinitionsQueryVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useLazyQuery<
      GetProcessInstanceNodeDefinitionsQuery,
      GetProcessInstanceNodeDefinitionsQueryVariables
    >(GetProcessInstanceNodeDefinitionsDocument, options);
  }
  export type GetProcessInstanceNodeDefinitionsQueryHookResult = ReturnType<
    typeof useGetProcessInstanceNodeDefinitionsQuery
  >;
  export type GetProcessInstanceNodeDefinitionsLazyQueryHookResult = ReturnType<
    typeof useGetProcessInstanceNodeDefinitionsLazyQuery
  >;
  export type GetProcessInstanceNodeDefinitionsQueryResult = ApolloReactCommon.QueryResult<
    GetProcessInstanceNodeDefinitionsQuery,
    GetProcessInstanceNodeDefinitionsQueryVariables
  >;
  export const HandleNodeTriggerDocument = gql`
    mutation handleNodeTrigger($processId: String, $nodeId: String) {
      NodeInstanceTrigger(id: $processId, nodeId: $nodeId)
    }
  `;
  export type HandleNodeTriggerMutationFn = ApolloReactCommon.MutationFunction<
    HandleNodeTriggerMutation,
    HandleNodeTriggerMutationVariables
  >;

  /**
   * __useHandleNodeTriggerMutation__
   *
   * To run a mutation, you first call `useHandleNodeTriggerMutation` within a React component and pass it any options that fit your needs.
   * When your component renders, `useHandleNodeTriggerMutation` returns a tuple that includes:
   * - A mutate function that you can call at any time to execute the mutation
   * - An object with fields that represent the current status of the mutation's execution
   *
   * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
   *
   * @example
   * const [handleNodeTriggerMutation, { data, loading, error }] = useHandleNodeTriggerMutation({
   *   variables: {
   *      processId: // value for 'processId'
   *      nodeId: // value for 'nodeId'
   *   },
   * });
   */
  export function useHandleNodeTriggerMutation(
    baseOptions?: ApolloReactHooks.MutationHookOptions<
      HandleNodeTriggerMutation,
      HandleNodeTriggerMutationVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useMutation<
      HandleNodeTriggerMutation,
      HandleNodeTriggerMutationVariables
    >(HandleNodeTriggerDocument, options);
  }
  export type HandleNodeTriggerMutationHookResult = ReturnType<
    typeof useHandleNodeTriggerMutation
  >;
  export type HandleNodeTriggerMutationResult = ApolloReactCommon.MutationResult<
    HandleNodeTriggerMutation
  >;
  export type HandleNodeTriggerMutationOptions = ApolloReactCommon.BaseMutationOptions<
    HandleNodeTriggerMutation,
    HandleNodeTriggerMutationVariables
  >;
  export const HandleNodeInstanceCancelDocument = gql`
    mutation handleNodeInstanceCancel(
      $processId: String
      $nodeInstanceId: String
    ) {
      NodeInstanceCancel(id: $processId, nodeInstanceId: $nodeInstanceId)
    }
  `;
  export type HandleNodeInstanceCancelMutationFn = ApolloReactCommon.MutationFunction<
    HandleNodeInstanceCancelMutation,
    HandleNodeInstanceCancelMutationVariables
  >;

  /**
   * __useHandleNodeInstanceCancelMutation__
   *
   * To run a mutation, you first call `useHandleNodeInstanceCancelMutation` within a React component and pass it any options that fit your needs.
   * When your component renders, `useHandleNodeInstanceCancelMutation` returns a tuple that includes:
   * - A mutate function that you can call at any time to execute the mutation
   * - An object with fields that represent the current status of the mutation's execution
   *
   * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
   *
   * @example
   * const [handleNodeInstanceCancelMutation, { data, loading, error }] = useHandleNodeInstanceCancelMutation({
   *   variables: {
   *      processId: // value for 'processId'
   *      nodeInstanceId: // value for 'nodeInstanceId'
   *   },
   * });
   */
  export function useHandleNodeInstanceCancelMutation(
    baseOptions?: ApolloReactHooks.MutationHookOptions<
      HandleNodeInstanceCancelMutation,
      HandleNodeInstanceCancelMutationVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useMutation<
      HandleNodeInstanceCancelMutation,
      HandleNodeInstanceCancelMutationVariables
    >(HandleNodeInstanceCancelDocument, options);
  }
  export type HandleNodeInstanceCancelMutationHookResult = ReturnType<
    typeof useHandleNodeInstanceCancelMutation
  >;
  export type HandleNodeInstanceCancelMutationResult = ApolloReactCommon.MutationResult<
    HandleNodeInstanceCancelMutation
  >;
  export type HandleNodeInstanceCancelMutationOptions = ApolloReactCommon.BaseMutationOptions<
    HandleNodeInstanceCancelMutation,
    HandleNodeInstanceCancelMutationVariables
  >;
  export const HandleNodeInstanceRetriggerDocument = gql`
    mutation handleNodeInstanceRetrigger(
      $processId: String
      $nodeInstanceId: String
    ) {
      NodeInstanceRetrigger(id: $processId, nodeInstanceId: $nodeInstanceId)
    }
  `;
  export type HandleNodeInstanceRetriggerMutationFn = ApolloReactCommon.MutationFunction<
    HandleNodeInstanceRetriggerMutation,
    HandleNodeInstanceRetriggerMutationVariables
  >;

  /**
   * __useHandleNodeInstanceRetriggerMutation__
   *
   * To run a mutation, you first call `useHandleNodeInstanceRetriggerMutation` within a React component and pass it any options that fit your needs.
   * When your component renders, `useHandleNodeInstanceRetriggerMutation` returns a tuple that includes:
   * - A mutate function that you can call at any time to execute the mutation
   * - An object with fields that represent the current status of the mutation's execution
   *
   * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
   *
   * @example
   * const [handleNodeInstanceRetriggerMutation, { data, loading, error }] = useHandleNodeInstanceRetriggerMutation({
   *   variables: {
   *      processId: // value for 'processId'
   *      nodeInstanceId: // value for 'nodeInstanceId'
   *   },
   * });
   */
  export function useHandleNodeInstanceRetriggerMutation(
    baseOptions?: ApolloReactHooks.MutationHookOptions<
      HandleNodeInstanceRetriggerMutation,
      HandleNodeInstanceRetriggerMutationVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useMutation<
      HandleNodeInstanceRetriggerMutation,
      HandleNodeInstanceRetriggerMutationVariables
    >(HandleNodeInstanceRetriggerDocument, options);
  }
  export type HandleNodeInstanceRetriggerMutationHookResult = ReturnType<
    typeof useHandleNodeInstanceRetriggerMutation
  >;
  export type HandleNodeInstanceRetriggerMutationResult = ApolloReactCommon.MutationResult<
    HandleNodeInstanceRetriggerMutation
  >;
  export type HandleNodeInstanceRetriggerMutationOptions = ApolloReactCommon.BaseMutationOptions<
    HandleNodeInstanceRetriggerMutation,
    HandleNodeInstanceRetriggerMutationVariables
  >;
  export const HandleProcessVariableUpdateDocument = gql`
    mutation handleProcessVariableUpdate(
      $processId: String
      $processInstanceVariables: String
    ) {
      ProcessInstanceUpdateVariables(
        id: $processId
        variables: $processInstanceVariables
      )
    }
  `;
  export type HandleProcessVariableUpdateMutationFn = ApolloReactCommon.MutationFunction<
    HandleProcessVariableUpdateMutation,
    HandleProcessVariableUpdateMutationVariables
  >;

  /**
   * __useHandleProcessVariableUpdateMutation__
   *
   * To run a mutation, you first call `useHandleProcessVariableUpdateMutation` within a React component and pass it any options that fit your needs.
   * When your component renders, `useHandleProcessVariableUpdateMutation` returns a tuple that includes:
   * - A mutate function that you can call at any time to execute the mutation
   * - An object with fields that represent the current status of the mutation's execution
   *
   * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
   *
   * @example
   * const [handleProcessVariableUpdateMutation, { data, loading, error }] = useHandleProcessVariableUpdateMutation({
   *   variables: {
   *      processId: // value for 'processId'
   *      processInstanceVariables: // value for 'processInstanceVariables'
   *   },
   * });
   */
  export function useHandleProcessVariableUpdateMutation(
    baseOptions?: ApolloReactHooks.MutationHookOptions<
      HandleProcessVariableUpdateMutation,
      HandleProcessVariableUpdateMutationVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useMutation<
      HandleProcessVariableUpdateMutation,
      HandleProcessVariableUpdateMutationVariables
    >(HandleProcessVariableUpdateDocument, options);
  }
  export type HandleProcessVariableUpdateMutationHookResult = ReturnType<
    typeof useHandleProcessVariableUpdateMutation
  >;
  export type HandleProcessVariableUpdateMutationResult = ApolloReactCommon.MutationResult<
    HandleProcessVariableUpdateMutation
  >;
  export type HandleProcessVariableUpdateMutationOptions = ApolloReactCommon.BaseMutationOptions<
    HandleProcessVariableUpdateMutation,
    HandleProcessVariableUpdateMutationVariables
  >;
  export const JobCancelDocument = gql`
    mutation jobCancel($jobId: String) {
      JobCancel(id: $jobId)
    }
  `;
  export type JobCancelMutationFn = ApolloReactCommon.MutationFunction<
    JobCancelMutation,
    JobCancelMutationVariables
  >;

  /**
   * __useJobCancelMutation__
   *
   * To run a mutation, you first call `useJobCancelMutation` within a React component and pass it any options that fit your needs.
   * When your component renders, `useJobCancelMutation` returns a tuple that includes:
   * - A mutate function that you can call at any time to execute the mutation
   * - An object with fields that represent the current status of the mutation's execution
   *
   * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
   *
   * @example
   * const [jobCancelMutation, { data, loading, error }] = useJobCancelMutation({
   *   variables: {
   *      jobId: // value for 'jobId'
   *   },
   * });
   */
  export function useJobCancelMutation(
    baseOptions?: ApolloReactHooks.MutationHookOptions<
      JobCancelMutation,
      JobCancelMutationVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useMutation<
      JobCancelMutation,
      JobCancelMutationVariables
    >(JobCancelDocument, options);
  }
  export type JobCancelMutationHookResult = ReturnType<
    typeof useJobCancelMutation
  >;
  export type JobCancelMutationResult = ApolloReactCommon.MutationResult<
    JobCancelMutation
  >;
  export type JobCancelMutationOptions = ApolloReactCommon.BaseMutationOptions<
    JobCancelMutation,
    JobCancelMutationVariables
  >;
  export const HandleJobRescheduleDocument = gql`
    mutation handleJobReschedule($jobId: String, $data: String) {
      JobReschedule(id: $jobId, data: $data)
    }
  `;
  export type HandleJobRescheduleMutationFn = ApolloReactCommon.MutationFunction<
    HandleJobRescheduleMutation,
    HandleJobRescheduleMutationVariables
  >;

  /**
   * __useHandleJobRescheduleMutation__
   *
   * To run a mutation, you first call `useHandleJobRescheduleMutation` within a React component and pass it any options that fit your needs.
   * When your component renders, `useHandleJobRescheduleMutation` returns a tuple that includes:
   * - A mutate function that you can call at any time to execute the mutation
   * - An object with fields that represent the current status of the mutation's execution
   *
   * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
   *
   * @example
   * const [handleJobRescheduleMutation, { data, loading, error }] = useHandleJobRescheduleMutation({
   *   variables: {
   *      jobId: // value for 'jobId'
   *      data: // value for 'data'
   *   },
   * });
   */
  export function useHandleJobRescheduleMutation(
    baseOptions?: ApolloReactHooks.MutationHookOptions<
      HandleJobRescheduleMutation,
      HandleJobRescheduleMutationVariables
    >
  ) {
    const options = { ...defaultOptions, ...baseOptions };
    return ApolloReactHooks.useMutation<
      HandleJobRescheduleMutation,
      HandleJobRescheduleMutationVariables
    >(HandleJobRescheduleDocument, options);
  }
  export type HandleJobRescheduleMutationHookResult = ReturnType<
    typeof useHandleJobRescheduleMutation
  >;
  export type HandleJobRescheduleMutationResult = ApolloReactCommon.MutationResult<
    HandleJobRescheduleMutation
  >;
  export type HandleJobRescheduleMutationOptions = ApolloReactCommon.BaseMutationOptions<
    HandleJobRescheduleMutation,
    HandleJobRescheduleMutationVariables
  >;
}
