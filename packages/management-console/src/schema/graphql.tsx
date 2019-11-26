import gql from 'graphql-tag';
export type Maybe<T> = T | null;
/** All built-in and custom scalars, mapped to their actual values */
/* tslint:disable */
export type Scalars = {
  ID: string,
  String: string,
  Boolean: boolean,
  Int: number,
  Float: number,
  /** An RFC-3339 compliant DateTime Scalar */
  DateTime: any,
};


export type NodeInstance = {
   __typename?: 'NodeInstance',
  id: Scalars['String'],
  name: Scalars['String'],
  type: Scalars['String'],
  enter: Scalars['DateTime'],
  exit?: Maybe<Scalars['DateTime']>,
  definitionId: Scalars['String'],
  nodeId: Scalars['String'],
};

export type ProcessInstance = {
   __typename?: 'ProcessInstance',
  id: Scalars['String'],
  processId: Scalars['String'],
  processName?: Maybe<Scalars['String']>,
  parentProcessInstanceId?: Maybe<Scalars['String']>,
  rootProcessInstanceId?: Maybe<Scalars['String']>,
  rootProcessId?: Maybe<Scalars['String']>,
  roles?: Maybe<Array<Scalars['String']>>,
  state: ProcessInstanceState,
  endpoint: Scalars['String'],
  nodes: NodeInstance[],
  variables?: Maybe<Scalars['String']>,
  start: Scalars['DateTime'],
  end?: Maybe<Scalars['DateTime']>,
  childProcessInstanceId?: Maybe<Array<Scalars['String']>>,
};

export type ProcessInstanceFilter = {
  state?: Maybe<ProcessInstanceState[]>,
  id?: Maybe<Array<Scalars['String']>>,
  parentProcessInstanceId?: Maybe<Array<Maybe<Scalars['String']>>>,
  rootProcessInstanceId?: Maybe<Array<Maybe<Scalars['String']>>>,
  processId?: Maybe<Array<Scalars['String']>>,
  limit?: Maybe<Scalars['Int']>,
  offset?: Maybe<Scalars['Int']>,
};

export type ProcessInstanceMeta = {
   __typename?: 'ProcessInstanceMeta',
  id: Scalars['String'],
  processId: Scalars['String'],
  processName?: Maybe<Scalars['String']>,
  parentProcessInstanceId?: Maybe<Scalars['String']>,
  rootProcessInstanceId?: Maybe<Scalars['String']>,
  rootProcessId?: Maybe<Scalars['String']>,
  roles?: Maybe<Array<Scalars['String']>>,
  state: ProcessInstanceState,
  endpoint: Scalars['String'],
  start: Scalars['DateTime'],
  end?: Maybe<Scalars['DateTime']>,
};

export enum ProcessInstanceState {
  Pending = 'PENDING',
  Active = 'ACTIVE',
  Completed = 'COMPLETED',
  Aborted = 'ABORTED',
  Suspended = 'SUSPENDED',
  Error = 'ERROR'
}

export type Query = {
   __typename?: 'Query',
  ProcessInstances?: Maybe<Array<Maybe<ProcessInstance>>>,
  UserTaskInstances?: Maybe<Array<Maybe<UserTaskInstance>>>,
};


export type QueryProcessInstancesArgs = {
  filter?: Maybe<ProcessInstanceFilter>
};


export type QueryUserTaskInstancesArgs = {
  filter?: Maybe<UserTaskInstanceFilter>
};

export type Subscription = {
   __typename?: 'Subscription',
  ProcessInstanceAdded: ProcessInstance,
  ProcessInstanceUpdated: ProcessInstance,
  UserTaskInstanceAdded: UserTaskInstance,
  UserTaskInstanceUpdated: UserTaskInstance,
};

export type UserTaskInstance = {
   __typename?: 'UserTaskInstance',
  id: Scalars['String'],
  description?: Maybe<Scalars['String']>,
  name?: Maybe<Scalars['String']>,
  priority?: Maybe<Scalars['String']>,
  processInstanceId: Scalars['String'],
  processId: Scalars['String'],
  rootProcessInstanceId?: Maybe<Scalars['String']>,
  rootProcessId?: Maybe<Scalars['String']>,
  state: Scalars['String'],
  actualOwner?: Maybe<Scalars['String']>,
  adminGroups?: Maybe<Array<Scalars['String']>>,
  adminUsers?: Maybe<Array<Scalars['String']>>,
  completed?: Maybe<Scalars['DateTime']>,
  started: Scalars['DateTime'],
  excludedUsers?: Maybe<Array<Scalars['String']>>,
  potentialGroups?: Maybe<Array<Scalars['String']>>,
  potentialUsers?: Maybe<Array<Scalars['String']>>,
  inputs?: Maybe<Scalars['String']>,
  outputs?: Maybe<Scalars['String']>,
  referenceName?: Maybe<Scalars['String']>,
};

export type UserTaskInstanceFilter = {
  state?: Maybe<Array<Scalars['String']>>,
  id?: Maybe<Array<Scalars['String']>>,
  processInstanceId?: Maybe<Array<Scalars['String']>>,
  actualOwner?: Maybe<Array<Scalars['String']>>,
  potentialUsers?: Maybe<Array<Scalars['String']>>,
  potentialGroups?: Maybe<Array<Scalars['String']>>,
  limit?: Maybe<Scalars['Int']>,
  offset?: Maybe<Scalars['Int']>,
};

export type UserTaskInstanceMeta = {
   __typename?: 'UserTaskInstanceMeta',
  id: Scalars['String'],
  description?: Maybe<Scalars['String']>,
  name?: Maybe<Scalars['String']>,
  priority?: Maybe<Scalars['String']>,
  processInstanceId: Scalars['String'],
  state: Scalars['String'],
  actualOwner?: Maybe<Scalars['String']>,
  adminGroups?: Maybe<Array<Scalars['String']>>,
  adminUsers?: Maybe<Array<Scalars['String']>>,
  completed?: Maybe<Scalars['DateTime']>,
  started: Scalars['DateTime'],
  excludedUsers?: Maybe<Array<Scalars['String']>>,
  potentialGroups?: Maybe<Array<Scalars['String']>>,
  potentialUsers?: Maybe<Array<Scalars['String']>>,
  referenceName?: Maybe<Scalars['String']>,
};

