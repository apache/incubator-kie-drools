/* tslint:disable */
import gql from 'graphql-tag';
import * as ApolloReactCommon from '@apollo/react-common';
import * as ApolloReactHooks from '@apollo/react-hooks';
export type Maybe<T> = T | null;

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

export type BooleanArgument = {
  isNull?: Maybe<Scalars['Boolean']>;
  equal?: Maybe<Scalars['Boolean']>;
};

export type DateArgument = {
  isNull?: Maybe<Scalars['Boolean']>;
  equal?: Maybe<Scalars['DateTime']>;
  greaterThan?: Maybe<Scalars['DateTime']>;
  greaterThanEqual?: Maybe<Scalars['DateTime']>;
  lessThan?: Maybe<Scalars['DateTime']>;
  lessThanEqual?: Maybe<Scalars['DateTime']>;
  between?: Maybe<DateRange>;
};

export type DateRange = {
  from: Scalars['DateTime'];
  to: Scalars['DateTime'];
};

export type IdArgument = {
  in?: Maybe<Array<Scalars['String']>>;
  equal?: Maybe<Scalars['String']>;
  isNull?: Maybe<Scalars['Boolean']>;
};

export type KogitoMetadata = {
  __typename?: 'KogitoMetadata';
  lastUpdate: Scalars['DateTime'];
  processInstances?: Maybe<Array<Maybe<ProcessInstanceMeta>>>;
  userTasks?: Maybe<Array<Maybe<UserTaskInstanceMeta>>>;
};

export type KogitoMetadataArgument = {
  lastUpdate?: Maybe<DateArgument>;
  processInstances?: Maybe<ProcessInstanceMetaArgument>;
  userTasks?: Maybe<UserTaskInstanceMetaArgument>;
};

export type KogitoMetadataOrderBy = {
  lastUpdate?: Maybe<OrderBy>;
};

export type NodeInstance = {
  __typename?: 'NodeInstance';
  id: Scalars['String'];
  name: Scalars['String'];
  type: Scalars['String'];
  enter: Scalars['DateTime'];
  exit?: Maybe<Scalars['DateTime']>;
  definitionId: Scalars['String'];
  nodeId: Scalars['String'];
};

export type NodeInstanceArgument = {
  id?: Maybe<IdArgument>;
  name?: Maybe<StringArgument>;
  definitionId?: Maybe<StringArgument>;
  nodeId?: Maybe<StringArgument>;
  type?: Maybe<StringArgument>;
  enter?: Maybe<DateArgument>;
  exit?: Maybe<DateArgument>;
};

export type NumericArgument = {
  in?: Maybe<Array<Scalars['Int']>>;
  isNull?: Maybe<Scalars['Boolean']>;
  equal?: Maybe<Scalars['Int']>;
  greaterThan?: Maybe<Scalars['Int']>;
  greaterThanEqual?: Maybe<Scalars['Int']>;
  lessThan?: Maybe<Scalars['Int']>;
  lessThanEqual?: Maybe<Scalars['Int']>;
  between?: Maybe<NumericRange>;
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
  limit?: Maybe<Scalars['Int']>;
  offset?: Maybe<Scalars['Int']>;
};

export type ProcessInstance = {
  __typename?: 'ProcessInstance';
  id: Scalars['String'];
  processId: Scalars['String'];
  processName?: Maybe<Scalars['String']>;
  parentProcessInstanceId?: Maybe<Scalars['String']>;
  rootProcessInstanceId?: Maybe<Scalars['String']>;
  rootProcessId?: Maybe<Scalars['String']>;
  roles?: Maybe<Array<Scalars['String']>>;
  state: ProcessInstanceState;
  endpoint: Scalars['String'];
  nodes: Array<NodeInstance>;
  variables?: Maybe<Scalars['String']>;
  start: Scalars['DateTime'];
  end?: Maybe<Scalars['DateTime']>;
  childProcessInstanceId?: Maybe<Array<Scalars['String']>>;
  error?: Maybe<ProcessInstanceError>;
  addons?: Maybe<Array<Scalars['String']>>;
  lastUpdate: Scalars['DateTime'];
};

export type ProcessInstanceArgument = {
  and?: Maybe<Array<ProcessInstanceArgument>>;
  or?: Maybe<Array<ProcessInstanceArgument>>;
  id?: Maybe<IdArgument>;
  processId?: Maybe<StringArgument>;
  processName?: Maybe<StringArgument>;
  parentProcessInstanceId?: Maybe<IdArgument>;
  rootProcessInstanceId?: Maybe<IdArgument>;
  rootProcessId?: Maybe<StringArgument>;
  state?: Maybe<ProcessInstanceStateArgument>;
  error?: Maybe<ProcessInstanceErrorArgument>;
  nodes?: Maybe<NodeInstanceArgument>;
  endpoint?: Maybe<StringArgument>;
  roles?: Maybe<StringArrayArgument>;
  start?: Maybe<DateArgument>;
  end?: Maybe<DateArgument>;
  addons?: Maybe<StringArrayArgument>;
  lastUpdate?: Maybe<DateArgument>;
};

export type ProcessInstanceError = {
  __typename?: 'ProcessInstanceError';
  nodeDefinitionId: Scalars['String'];
  message?: Maybe<Scalars['String']>;
};

export type ProcessInstanceErrorArgument = {
  nodeDefinitionId?: Maybe<StringArgument>;
  message?: Maybe<StringArgument>;
};

export type ProcessInstanceErrorOrderBy = {
  nodeDefinitionId?: Maybe<OrderBy>;
  message?: Maybe<OrderBy>;
};

export type ProcessInstanceMeta = {
  __typename?: 'ProcessInstanceMeta';
  id: Scalars['String'];
  processId: Scalars['String'];
  processName?: Maybe<Scalars['String']>;
  parentProcessInstanceId?: Maybe<Scalars['String']>;
  rootProcessInstanceId?: Maybe<Scalars['String']>;
  rootProcessId?: Maybe<Scalars['String']>;
  roles?: Maybe<Array<Scalars['String']>>;
  state: ProcessInstanceState;
  endpoint: Scalars['String'];
  start: Scalars['DateTime'];
  end?: Maybe<Scalars['DateTime']>;
  lastUpdate: Scalars['DateTime'];
};

export type ProcessInstanceMetaArgument = {
  id?: Maybe<IdArgument>;
  processId?: Maybe<StringArgument>;
  processName?: Maybe<StringArgument>;
  parentProcessInstanceId?: Maybe<IdArgument>;
  rootProcessInstanceId?: Maybe<IdArgument>;
  rootProcessId?: Maybe<StringArgument>;
  state?: Maybe<ProcessInstanceStateArgument>;
  endpoint?: Maybe<StringArgument>;
  roles?: Maybe<StringArrayArgument>;
  start?: Maybe<DateArgument>;
  end?: Maybe<DateArgument>;
};

export type ProcessInstanceOrderBy = {
  processId?: Maybe<OrderBy>;
  processName?: Maybe<OrderBy>;
  rootProcessId?: Maybe<OrderBy>;
  state?: Maybe<OrderBy>;
  start?: Maybe<OrderBy>;
  end?: Maybe<OrderBy>;
  error?: Maybe<ProcessInstanceErrorOrderBy>;
  lastUpdate?: Maybe<OrderBy>;
};

export enum ProcessInstanceState {
  Pending = 'PENDING',
  Active = 'ACTIVE',
  Completed = 'COMPLETED',
  Aborted = 'ABORTED',
  Suspended = 'SUSPENDED',
  Error = 'ERROR'
}

export type ProcessInstanceStateArgument = {
  equal?: Maybe<ProcessInstanceState>;
  in?: Maybe<Array<Maybe<ProcessInstanceState>>>;
};

export type Query = {
  __typename?: 'Query';
  ProcessInstances?: Maybe<Array<Maybe<ProcessInstance>>>;
  UserTaskInstances?: Maybe<Array<Maybe<UserTaskInstance>>>;
};

export type QueryProcessInstancesArgs = {
  where?: Maybe<ProcessInstanceArgument>;
  orderBy?: Maybe<ProcessInstanceOrderBy>;
  pagination?: Maybe<Pagination>;
};

export type QueryUserTaskInstancesArgs = {
  where?: Maybe<UserTaskInstanceArgument>;
  orderBy?: Maybe<UserTaskInstanceOrderBy>;
  pagination?: Maybe<Pagination>;
};

export type StringArgument = {
  in?: Maybe<Array<Scalars['String']>>;
  like?: Maybe<Scalars['String']>;
  isNull?: Maybe<Scalars['Boolean']>;
  equal?: Maybe<Scalars['String']>;
};

export type StringArrayArgument = {
  contains?: Maybe<Scalars['String']>;
  containsAll?: Maybe<Array<Scalars['String']>>;
  containsAny?: Maybe<Array<Scalars['String']>>;
  isNull?: Maybe<Scalars['Boolean']>;
};

export type Subscription = {
  __typename?: 'Subscription';
  ProcessInstanceAdded: ProcessInstance;
  ProcessInstanceUpdated: ProcessInstance;
  UserTaskInstanceAdded: UserTaskInstance;
  UserTaskInstanceUpdated: UserTaskInstance;
};

export type UserTaskInstance = {
  __typename?: 'UserTaskInstance';
  id: Scalars['String'];
  description?: Maybe<Scalars['String']>;
  name?: Maybe<Scalars['String']>;
  priority?: Maybe<Scalars['String']>;
  processInstanceId: Scalars['String'];
  processId: Scalars['String'];
  rootProcessInstanceId?: Maybe<Scalars['String']>;
  rootProcessId?: Maybe<Scalars['String']>;
  state: Scalars['String'];
  actualOwner?: Maybe<Scalars['String']>;
  adminGroups?: Maybe<Array<Scalars['String']>>;
  adminUsers?: Maybe<Array<Scalars['String']>>;
  completed?: Maybe<Scalars['DateTime']>;
  started: Scalars['DateTime'];
  excludedUsers?: Maybe<Array<Scalars['String']>>;
  potentialGroups?: Maybe<Array<Scalars['String']>>;
  potentialUsers?: Maybe<Array<Scalars['String']>>;
  inputs?: Maybe<Scalars['String']>;
  outputs?: Maybe<Scalars['String']>;
  referenceName?: Maybe<Scalars['String']>;
  lastUpdate: Scalars['DateTime'];
};

export type UserTaskInstanceArgument = {
  and?: Maybe<Array<UserTaskInstanceArgument>>;
  or?: Maybe<Array<UserTaskInstanceArgument>>;
  state?: Maybe<StringArgument>;
  id?: Maybe<IdArgument>;
  description?: Maybe<StringArgument>;
  name?: Maybe<StringArgument>;
  priority?: Maybe<StringArgument>;
  processInstanceId?: Maybe<IdArgument>;
  actualOwner?: Maybe<StringArgument>;
  potentialUsers?: Maybe<StringArrayArgument>;
  potentialGroups?: Maybe<StringArrayArgument>;
  excludedUsers?: Maybe<StringArrayArgument>;
  adminGroups?: Maybe<StringArrayArgument>;
  adminUsers?: Maybe<StringArrayArgument>;
  completed?: Maybe<DateArgument>;
  started?: Maybe<DateArgument>;
  referenceName?: Maybe<StringArgument>;
  lastUpdate?: Maybe<DateArgument>;
};

export type UserTaskInstanceMeta = {
  __typename?: 'UserTaskInstanceMeta';
  id: Scalars['String'];
  description?: Maybe<Scalars['String']>;
  name?: Maybe<Scalars['String']>;
  priority?: Maybe<Scalars['String']>;
  processInstanceId: Scalars['String'];
  state: Scalars['String'];
  actualOwner?: Maybe<Scalars['String']>;
  adminGroups?: Maybe<Array<Scalars['String']>>;
  adminUsers?: Maybe<Array<Scalars['String']>>;
  completed?: Maybe<Scalars['DateTime']>;
  started: Scalars['DateTime'];
  excludedUsers?: Maybe<Array<Scalars['String']>>;
  potentialGroups?: Maybe<Array<Scalars['String']>>;
  potentialUsers?: Maybe<Array<Scalars['String']>>;
  referenceName?: Maybe<Scalars['String']>;
  lastUpdate: Scalars['DateTime'];
};

export type UserTaskInstanceMetaArgument = {
  state?: Maybe<StringArgument>;
  id?: Maybe<IdArgument>;
  description?: Maybe<StringArgument>;
  name?: Maybe<StringArgument>;
  priority?: Maybe<StringArgument>;
  processInstanceId?: Maybe<IdArgument>;
  actualOwner?: Maybe<StringArgument>;
  potentialUsers?: Maybe<StringArrayArgument>;
  potentialGroups?: Maybe<StringArrayArgument>;
  excludedUsers?: Maybe<StringArrayArgument>;
  adminGroups?: Maybe<StringArrayArgument>;
  adminUsers?: Maybe<StringArrayArgument>;
  completed?: Maybe<DateArgument>;
  started?: Maybe<DateArgument>;
  referenceName?: Maybe<StringArgument>;
};

export type UserTaskInstanceOrderBy = {
  state?: Maybe<OrderBy>;
  actualOwner?: Maybe<OrderBy>;
  description?: Maybe<OrderBy>;
  name?: Maybe<OrderBy>;
  priority?: Maybe<OrderBy>;
  completed?: Maybe<OrderBy>;
  started?: Maybe<OrderBy>;
  referenceName?: Maybe<OrderBy>;
  lastUpdate?: Maybe<OrderBy>;
};

export type GetProcessInstancesQueryVariables = {
  state?: Maybe<Array<ProcessInstanceState>>;
};

export type GetProcessInstancesQuery = { __typename?: 'Query' } & {
  ProcessInstances: Maybe<
    Array<
      Maybe<
        { __typename?: 'ProcessInstance' } & Pick<
          ProcessInstance,
          | 'id'
          | 'processId'
          | 'processName'
          | 'parentProcessInstanceId'
          | 'roles'
          | 'state'
          | 'start'
          | 'addons'
          | 'endpoint'
        > & {
            error: Maybe<
              { __typename?: 'ProcessInstanceError' } & Pick<
                ProcessInstanceError,
                'nodeDefinitionId' | 'message'
              >
            >;
          }
      >
    >
  >;
};

export type GetChildInstancesQueryVariables = {
  instanceId?: Maybe<Scalars['String']>;
};

export type GetChildInstancesQuery = { __typename?: 'Query' } & {
  ProcessInstances: Maybe<
    Array<
      Maybe<
        { __typename?: 'ProcessInstance' } & Pick<
          ProcessInstance,
          | 'id'
          | 'processId'
          | 'processName'
          | 'parentProcessInstanceId'
          | 'roles'
          | 'state'
          | 'start'
          | 'endpoint'
          | 'addons'
        > & {
            error: Maybe<
              { __typename?: 'ProcessInstanceError' } & Pick<
                ProcessInstanceError,
                'nodeDefinitionId' | 'message'
              >
            >;
          }
      >
    >
  >;
};

export type GetProcessInstanceByIdQueryVariables = {
  id?: Maybe<Scalars['String']>;
};

export type GetProcessInstanceByIdQuery = { __typename?: 'Query' } & {
  ProcessInstances: Maybe<
    Array<
      Maybe<
        { __typename?: 'ProcessInstance' } & Pick<
          ProcessInstance,
          | 'id'
          | 'processId'
          | 'processName'
          | 'parentProcessInstanceId'
          | 'roles'
          | 'variables'
          | 'state'
          | 'start'
          | 'end'
          | 'endpoint'
          | 'childProcessInstanceId'
        > & {
            nodes: Array<
              { __typename?: 'NodeInstance' } & Pick<
                NodeInstance,
                'id' | 'name' | 'type' | 'enter' | 'exit'
              >
            >;
          }
      >
    >
  >;
};

export const GetProcessInstancesDocument = gql`
  query getProcessInstances($state: [ProcessInstanceState!]) {
    ProcessInstances(
      where: {
        parentProcessInstanceId: { isNull: true }
        state: { in: $state }
      }
    ) {
      id
      processId
      processName
      parentProcessInstanceId
      roles
      state
      start
      addons
      endpoint
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
 *      state: // value for 'state'
 *   },
 * });
 */
export function useGetProcessInstancesQuery(
  baseOptions?: ApolloReactHooks.QueryHookOptions<
    GetProcessInstancesQuery,
    GetProcessInstancesQueryVariables
  >
) {
  return ApolloReactHooks.useQuery<
    GetProcessInstancesQuery,
    GetProcessInstancesQueryVariables
  >(GetProcessInstancesDocument, baseOptions);
}
export function useGetProcessInstancesLazyQuery(
  baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
    GetProcessInstancesQuery,
    GetProcessInstancesQueryVariables
  >
) {
  return ApolloReactHooks.useLazyQuery<
    GetProcessInstancesQuery,
    GetProcessInstancesQueryVariables
  >(GetProcessInstancesDocument, baseOptions);
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
  query getChildInstances($instanceId: String) {
    ProcessInstances(
      where: { parentProcessInstanceId: { equal: $instanceId } }
    ) {
      id
      processId
      processName
      parentProcessInstanceId
      roles
      state
      start
      endpoint
      addons
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
 *      instanceId: // value for 'instanceId'
 *   },
 * });
 */
export function useGetChildInstancesQuery(
  baseOptions?: ApolloReactHooks.QueryHookOptions<
    GetChildInstancesQuery,
    GetChildInstancesQueryVariables
  >
) {
  return ApolloReactHooks.useQuery<
    GetChildInstancesQuery,
    GetChildInstancesQueryVariables
  >(GetChildInstancesDocument, baseOptions);
}
export function useGetChildInstancesLazyQuery(
  baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
    GetChildInstancesQuery,
    GetChildInstancesQueryVariables
  >
) {
  return ApolloReactHooks.useLazyQuery<
    GetChildInstancesQuery,
    GetChildInstancesQueryVariables
  >(GetChildInstancesDocument, baseOptions);
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
      parentProcessInstanceId
      roles
      variables
      state
      start
      end
      endpoint
      childProcessInstanceId
      nodes {
        id
        name
        type
        enter
        exit
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
  return ApolloReactHooks.useQuery<
    GetProcessInstanceByIdQuery,
    GetProcessInstanceByIdQueryVariables
  >(GetProcessInstanceByIdDocument, baseOptions);
}
export function useGetProcessInstanceByIdLazyQuery(
  baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
    GetProcessInstanceByIdQuery,
    GetProcessInstanceByIdQueryVariables
  >
) {
  return ApolloReactHooks.useLazyQuery<
    GetProcessInstanceByIdQuery,
    GetProcessInstanceByIdQueryVariables
  >(GetProcessInstanceByIdDocument, baseOptions);
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
