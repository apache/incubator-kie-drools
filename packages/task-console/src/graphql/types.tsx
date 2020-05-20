/* tslint:disable */
import gql from 'graphql-tag';
import * as ApolloReactCommon from '@apollo/react-common';
import * as ApolloReactHooks from '@apollo/react-hooks';
export type Maybe<T> = T | null;

/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string,
  String: string,
  Boolean: boolean,
  Int: number,
  Float: number,
  DateTime: any,
};

export type Address = {
   __typename?: 'Address',
  city?: Maybe<Scalars['String']>,
  country?: Maybe<Scalars['String']>,
  street?: Maybe<Scalars['String']>,
  zipCode?: Maybe<Scalars['String']>,
};

export type AddressArgument = {
  city?: Maybe<StringArgument>,
  country?: Maybe<StringArgument>,
  street?: Maybe<StringArgument>,
  zipCode?: Maybe<StringArgument>,
};

export type AddressOrderBy = {
  city?: Maybe<OrderBy>,
  country?: Maybe<OrderBy>,
  street?: Maybe<OrderBy>,
  zipCode?: Maybe<OrderBy>,
};

export type BooleanArgument = {
  isNull?: Maybe<Scalars['Boolean']>,
  equal?: Maybe<Scalars['Boolean']>,
};

export type DateArgument = {
  isNull?: Maybe<Scalars['Boolean']>,
  equal?: Maybe<Scalars['DateTime']>,
  greaterThan?: Maybe<Scalars['DateTime']>,
  greaterThanEqual?: Maybe<Scalars['DateTime']>,
  lessThan?: Maybe<Scalars['DateTime']>,
  lessThanEqual?: Maybe<Scalars['DateTime']>,
  between?: Maybe<DateRange>,
};

export type DateRange = {
  from: Scalars['DateTime'],
  to: Scalars['DateTime'],
};


export type Deals = {
   __typename?: 'Deals',
  id?: Maybe<Scalars['String']>,
  name?: Maybe<Scalars['String']>,
  review?: Maybe<Scalars['String']>,
  traveller?: Maybe<Traveller>,
  metadata?: Maybe<KogitoMetadata>,
};

export type DealsArgument = {
  and?: Maybe<Array<DealsArgument>>,
  or?: Maybe<Array<DealsArgument>>,
  id?: Maybe<IdArgument>,
  name?: Maybe<StringArgument>,
  review?: Maybe<StringArgument>,
  traveller?: Maybe<TravellerArgument>,
  metadata?: Maybe<KogitoMetadataArgument>,
};

export type DealsOrderBy = {
  name?: Maybe<OrderBy>,
  review?: Maybe<OrderBy>,
  traveller?: Maybe<TravellerOrderBy>,
  metadata?: Maybe<KogitoMetadataOrderBy>,
};

export type Flight = {
   __typename?: 'Flight',
  arrival?: Maybe<Scalars['String']>,
  departure?: Maybe<Scalars['String']>,
  flightNumber?: Maybe<Scalars['String']>,
  gate?: Maybe<Scalars['String']>,
  seat?: Maybe<Scalars['String']>,
};

export type FlightArgument = {
  arrival?: Maybe<StringArgument>,
  departure?: Maybe<StringArgument>,
  flightNumber?: Maybe<StringArgument>,
  gate?: Maybe<StringArgument>,
  seat?: Maybe<StringArgument>,
};

export type FlightOrderBy = {
  arrival?: Maybe<OrderBy>,
  departure?: Maybe<OrderBy>,
  flightNumber?: Maybe<OrderBy>,
  gate?: Maybe<OrderBy>,
  seat?: Maybe<OrderBy>,
};

export type Hotel = {
   __typename?: 'Hotel',
  address?: Maybe<Address>,
  bookingNumber?: Maybe<Scalars['String']>,
  name?: Maybe<Scalars['String']>,
  phone?: Maybe<Scalars['String']>,
  room?: Maybe<Scalars['String']>,
};

export type HotelArgument = {
  address?: Maybe<AddressArgument>,
  bookingNumber?: Maybe<StringArgument>,
  name?: Maybe<StringArgument>,
  phone?: Maybe<StringArgument>,
  room?: Maybe<StringArgument>,
};

export type HotelOrderBy = {
  address?: Maybe<AddressOrderBy>,
  bookingNumber?: Maybe<OrderBy>,
  name?: Maybe<OrderBy>,
  phone?: Maybe<OrderBy>,
  room?: Maybe<OrderBy>,
};

export type IdArgument = {
  in?: Maybe<Array<Scalars['String']>>,
  equal?: Maybe<Scalars['String']>,
  isNull?: Maybe<Scalars['Boolean']>,
};

export type Job = {
   __typename?: 'Job',
  id: Scalars['String'],
  processId?: Maybe<Scalars['String']>,
  processInstanceId?: Maybe<Scalars['String']>,
  rootProcessInstanceId?: Maybe<Scalars['String']>,
  rootProcessId?: Maybe<Scalars['String']>,
  status: JobStatus,
  expirationTime?: Maybe<Scalars['DateTime']>,
  priority?: Maybe<Scalars['Int']>,
  callbackEndpoint?: Maybe<Scalars['String']>,
  repeatInterval?: Maybe<Scalars['Int']>,
  repeatLimit?: Maybe<Scalars['Int']>,
  scheduledId?: Maybe<Scalars['String']>,
  retries?: Maybe<Scalars['Int']>,
  lastUpdate?: Maybe<Scalars['DateTime']>,
  executionCounter?: Maybe<Scalars['Int']>,
};

export type JobArgument = {
  and?: Maybe<Array<JobArgument>>,
  or?: Maybe<Array<JobArgument>>,
  id?: Maybe<IdArgument>,
  processId?: Maybe<StringArgument>,
  processInstanceId?: Maybe<IdArgument>,
  rootProcessInstanceId?: Maybe<IdArgument>,
  rootProcessId?: Maybe<StringArgument>,
  status?: Maybe<JobStatusArgument>,
  expirationTime?: Maybe<DateArgument>,
  priority?: Maybe<NumericArgument>,
  scheduledId?: Maybe<IdArgument>,
  lastUpdate?: Maybe<DateArgument>,
};

export type JobOrderBy = {
  processId?: Maybe<OrderBy>,
  rootProcessId?: Maybe<OrderBy>,
  status?: Maybe<OrderBy>,
  expirationTime?: Maybe<OrderBy>,
  priority?: Maybe<OrderBy>,
  retries?: Maybe<OrderBy>,
  lastUpdate?: Maybe<OrderBy>,
  executionCounter?: Maybe<OrderBy>,
};

export enum JobStatus {
  Error = 'ERROR',
  Executed = 'EXECUTED',
  Scheduled = 'SCHEDULED',
  Retry = 'RETRY',
  Canceled = 'CANCELED'
}

export type JobStatusArgument = {
  equal?: Maybe<JobStatus>,
  in?: Maybe<Array<Maybe<JobStatus>>>,
};

export type KogitoMetadata = {
   __typename?: 'KogitoMetadata',
  lastUpdate: Scalars['DateTime'],
  processInstances?: Maybe<Array<Maybe<ProcessInstanceMeta>>>,
  userTasks?: Maybe<Array<Maybe<UserTaskInstanceMeta>>>,
};

export type KogitoMetadataArgument = {
  lastUpdate?: Maybe<DateArgument>,
  processInstances?: Maybe<ProcessInstanceMetaArgument>,
  userTasks?: Maybe<UserTaskInstanceMetaArgument>,
};

export type KogitoMetadataOrderBy = {
  lastUpdate?: Maybe<OrderBy>,
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

export type NodeInstanceArgument = {
  id?: Maybe<IdArgument>,
  name?: Maybe<StringArgument>,
  definitionId?: Maybe<StringArgument>,
  nodeId?: Maybe<StringArgument>,
  type?: Maybe<StringArgument>,
  enter?: Maybe<DateArgument>,
  exit?: Maybe<DateArgument>,
};

export type NumericArgument = {
  in?: Maybe<Array<Scalars['Int']>>,
  isNull?: Maybe<Scalars['Boolean']>,
  equal?: Maybe<Scalars['Int']>,
  greaterThan?: Maybe<Scalars['Int']>,
  greaterThanEqual?: Maybe<Scalars['Int']>,
  lessThan?: Maybe<Scalars['Int']>,
  lessThanEqual?: Maybe<Scalars['Int']>,
  between?: Maybe<NumericRange>,
};

export type NumericRange = {
  from: Scalars['Int'],
  to: Scalars['Int'],
};

export enum OrderBy {
  Asc = 'ASC',
  Desc = 'DESC'
}

export type Pagination = {
  limit?: Maybe<Scalars['Int']>,
  offset?: Maybe<Scalars['Int']>,
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
  nodes: Array<NodeInstance>,
  variables?: Maybe<Scalars['String']>,
  start: Scalars['DateTime'],
  end?: Maybe<Scalars['DateTime']>,
  parentProcessInstance?: Maybe<ProcessInstance>,
  childProcessInstances?: Maybe<Array<ProcessInstance>>,
  error?: Maybe<ProcessInstanceError>,
  addons?: Maybe<Array<Scalars['String']>>,
  lastUpdate: Scalars['DateTime'],
  businessKey?: Maybe<Scalars['String']>,
};

export type ProcessInstanceArgument = {
  and?: Maybe<Array<ProcessInstanceArgument>>,
  or?: Maybe<Array<ProcessInstanceArgument>>,
  id?: Maybe<IdArgument>,
  processId?: Maybe<StringArgument>,
  processName?: Maybe<StringArgument>,
  parentProcessInstanceId?: Maybe<IdArgument>,
  rootProcessInstanceId?: Maybe<IdArgument>,
  rootProcessId?: Maybe<StringArgument>,
  state?: Maybe<ProcessInstanceStateArgument>,
  error?: Maybe<ProcessInstanceErrorArgument>,
  nodes?: Maybe<NodeInstanceArgument>,
  endpoint?: Maybe<StringArgument>,
  roles?: Maybe<StringArrayArgument>,
  start?: Maybe<DateArgument>,
  end?: Maybe<DateArgument>,
  addons?: Maybe<StringArrayArgument>,
  lastUpdate?: Maybe<DateArgument>,
  businessKey?: Maybe<StringArgument>,
};

export type ProcessInstanceError = {
   __typename?: 'ProcessInstanceError',
  nodeDefinitionId: Scalars['String'],
  message?: Maybe<Scalars['String']>,
};

export type ProcessInstanceErrorArgument = {
  nodeDefinitionId?: Maybe<StringArgument>,
  message?: Maybe<StringArgument>,
};

export type ProcessInstanceErrorOrderBy = {
  nodeDefinitionId?: Maybe<OrderBy>,
  message?: Maybe<OrderBy>,
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
  lastUpdate: Scalars['DateTime'],
  businessKey?: Maybe<Scalars['String']>,
};

export type ProcessInstanceMetaArgument = {
  id?: Maybe<IdArgument>,
  processId?: Maybe<StringArgument>,
  processName?: Maybe<StringArgument>,
  parentProcessInstanceId?: Maybe<IdArgument>,
  rootProcessInstanceId?: Maybe<IdArgument>,
  rootProcessId?: Maybe<StringArgument>,
  state?: Maybe<ProcessInstanceStateArgument>,
  endpoint?: Maybe<StringArgument>,
  roles?: Maybe<StringArrayArgument>,
  start?: Maybe<DateArgument>,
  end?: Maybe<DateArgument>,
  businessKey?: Maybe<StringArgument>,
};

export type ProcessInstanceOrderBy = {
  processId?: Maybe<OrderBy>,
  processName?: Maybe<OrderBy>,
  rootProcessId?: Maybe<OrderBy>,
  state?: Maybe<OrderBy>,
  start?: Maybe<OrderBy>,
  end?: Maybe<OrderBy>,
  error?: Maybe<ProcessInstanceErrorOrderBy>,
  lastUpdate?: Maybe<OrderBy>,
  businessKey?: Maybe<OrderBy>,
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
  equal?: Maybe<ProcessInstanceState>,
  in?: Maybe<Array<Maybe<ProcessInstanceState>>>,
};

export type Query = {
   __typename?: 'Query',
  ProcessInstances?: Maybe<Array<Maybe<ProcessInstance>>>,
  UserTaskInstances?: Maybe<Array<Maybe<UserTaskInstance>>>,
  Jobs?: Maybe<Array<Maybe<Job>>>,
  VisaApplications?: Maybe<Array<Maybe<VisaApplications>>>,
  Deals?: Maybe<Array<Maybe<Deals>>>,
  Travels?: Maybe<Array<Maybe<Travels>>>,
};


export type QueryProcessInstancesArgs = {
  where?: Maybe<ProcessInstanceArgument>,
  orderBy?: Maybe<ProcessInstanceOrderBy>,
  pagination?: Maybe<Pagination>
};


export type QueryUserTaskInstancesArgs = {
  where?: Maybe<UserTaskInstanceArgument>,
  orderBy?: Maybe<UserTaskInstanceOrderBy>,
  pagination?: Maybe<Pagination>
};


export type QueryJobsArgs = {
  where?: Maybe<JobArgument>,
  orderBy?: Maybe<JobOrderBy>,
  pagination?: Maybe<Pagination>
};


export type QueryVisaApplicationsArgs = {
  where?: Maybe<VisaApplicationsArgument>,
  orderBy?: Maybe<VisaApplicationsOrderBy>,
  pagination?: Maybe<Pagination>
};


export type QueryDealsArgs = {
  where?: Maybe<DealsArgument>,
  orderBy?: Maybe<DealsOrderBy>,
  pagination?: Maybe<Pagination>
};


export type QueryTravelsArgs = {
  where?: Maybe<TravelsArgument>,
  orderBy?: Maybe<TravelsOrderBy>,
  pagination?: Maybe<Pagination>
};

export type StringArgument = {
  in?: Maybe<Array<Scalars['String']>>,
  like?: Maybe<Scalars['String']>,
  isNull?: Maybe<Scalars['Boolean']>,
  equal?: Maybe<Scalars['String']>,
};

export type StringArrayArgument = {
  contains?: Maybe<Scalars['String']>,
  containsAll?: Maybe<Array<Scalars['String']>>,
  containsAny?: Maybe<Array<Scalars['String']>>,
  isNull?: Maybe<Scalars['Boolean']>,
};

export type Subscription = {
   __typename?: 'Subscription',
  ProcessInstanceAdded: ProcessInstance,
  ProcessInstanceUpdated: ProcessInstance,
  UserTaskInstanceAdded: UserTaskInstance,
  UserTaskInstanceUpdated: UserTaskInstance,
  JobAdded: Job,
  JobUpdated: Job,
  VisaApplicationsAdded: VisaApplications,
  VisaApplicationsUpdated: VisaApplications,
  DealsAdded: Deals,
  DealsUpdated: Deals,
  TravelsAdded: Travels,
  TravelsUpdated: Travels,
};

export type Traveller = {
   __typename?: 'Traveller',
  address?: Maybe<Address>,
  email?: Maybe<Scalars['String']>,
  firstName?: Maybe<Scalars['String']>,
  lastName?: Maybe<Scalars['String']>,
  nationality?: Maybe<Scalars['String']>,
};

export type TravellerArgument = {
  address?: Maybe<AddressArgument>,
  email?: Maybe<StringArgument>,
  firstName?: Maybe<StringArgument>,
  lastName?: Maybe<StringArgument>,
  nationality?: Maybe<StringArgument>,
};

export type TravellerOrderBy = {
  address?: Maybe<AddressOrderBy>,
  email?: Maybe<OrderBy>,
  firstName?: Maybe<OrderBy>,
  lastName?: Maybe<OrderBy>,
  nationality?: Maybe<OrderBy>,
};

export type Travels = {
   __typename?: 'Travels',
  flight?: Maybe<Flight>,
  hotel?: Maybe<Hotel>,
  id?: Maybe<Scalars['String']>,
  traveller?: Maybe<Traveller>,
  trip?: Maybe<Trip>,
  visaApplication?: Maybe<VisaApplication>,
  metadata?: Maybe<KogitoMetadata>,
};

export type TravelsArgument = {
  and?: Maybe<Array<TravelsArgument>>,
  or?: Maybe<Array<TravelsArgument>>,
  flight?: Maybe<FlightArgument>,
  hotel?: Maybe<HotelArgument>,
  id?: Maybe<IdArgument>,
  traveller?: Maybe<TravellerArgument>,
  trip?: Maybe<TripArgument>,
  visaApplication?: Maybe<VisaApplicationArgument>,
  metadata?: Maybe<KogitoMetadataArgument>,
};

export type TravelsOrderBy = {
  flight?: Maybe<FlightOrderBy>,
  hotel?: Maybe<HotelOrderBy>,
  traveller?: Maybe<TravellerOrderBy>,
  trip?: Maybe<TripOrderBy>,
  visaApplication?: Maybe<VisaApplicationOrderBy>,
  metadata?: Maybe<KogitoMetadataOrderBy>,
};

export type Trip = {
   __typename?: 'Trip',
  begin?: Maybe<Scalars['String']>,
  city?: Maybe<Scalars['String']>,
  country?: Maybe<Scalars['String']>,
  end?: Maybe<Scalars['String']>,
  visaRequired?: Maybe<Scalars['Boolean']>,
};

export type TripArgument = {
  begin?: Maybe<StringArgument>,
  city?: Maybe<StringArgument>,
  country?: Maybe<StringArgument>,
  end?: Maybe<StringArgument>,
  visaRequired?: Maybe<BooleanArgument>,
};

export type TripOrderBy = {
  begin?: Maybe<OrderBy>,
  city?: Maybe<OrderBy>,
  country?: Maybe<OrderBy>,
  end?: Maybe<OrderBy>,
  visaRequired?: Maybe<OrderBy>,
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
  lastUpdate: Scalars['DateTime'],
};

export type UserTaskInstanceArgument = {
  and?: Maybe<Array<UserTaskInstanceArgument>>,
  or?: Maybe<Array<UserTaskInstanceArgument>>,
  state?: Maybe<StringArgument>,
  id?: Maybe<IdArgument>,
  description?: Maybe<StringArgument>,
  name?: Maybe<StringArgument>,
  priority?: Maybe<StringArgument>,
  processInstanceId?: Maybe<IdArgument>,
  actualOwner?: Maybe<StringArgument>,
  potentialUsers?: Maybe<StringArrayArgument>,
  potentialGroups?: Maybe<StringArrayArgument>,
  excludedUsers?: Maybe<StringArrayArgument>,
  adminGroups?: Maybe<StringArrayArgument>,
  adminUsers?: Maybe<StringArrayArgument>,
  completed?: Maybe<DateArgument>,
  started?: Maybe<DateArgument>,
  referenceName?: Maybe<StringArgument>,
  lastUpdate?: Maybe<DateArgument>,
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
  lastUpdate: Scalars['DateTime'],
};

export type UserTaskInstanceMetaArgument = {
  state?: Maybe<StringArgument>,
  id?: Maybe<IdArgument>,
  description?: Maybe<StringArgument>,
  name?: Maybe<StringArgument>,
  priority?: Maybe<StringArgument>,
  processInstanceId?: Maybe<IdArgument>,
  actualOwner?: Maybe<StringArgument>,
  potentialUsers?: Maybe<StringArrayArgument>,
  potentialGroups?: Maybe<StringArrayArgument>,
  excludedUsers?: Maybe<StringArrayArgument>,
  adminGroups?: Maybe<StringArrayArgument>,
  adminUsers?: Maybe<StringArrayArgument>,
  completed?: Maybe<DateArgument>,
  started?: Maybe<DateArgument>,
  referenceName?: Maybe<StringArgument>,
};

export type UserTaskInstanceOrderBy = {
  state?: Maybe<OrderBy>,
  actualOwner?: Maybe<OrderBy>,
  description?: Maybe<OrderBy>,
  name?: Maybe<OrderBy>,
  priority?: Maybe<OrderBy>,
  completed?: Maybe<OrderBy>,
  started?: Maybe<OrderBy>,
  referenceName?: Maybe<OrderBy>,
  lastUpdate?: Maybe<OrderBy>,
};

export type VisaApplication = {
   __typename?: 'VisaApplication',
  approved?: Maybe<Scalars['Boolean']>,
  city?: Maybe<Scalars['String']>,
  country?: Maybe<Scalars['String']>,
  duration?: Maybe<Scalars['Int']>,
  firstName?: Maybe<Scalars['String']>,
  lastName?: Maybe<Scalars['String']>,
  nationality?: Maybe<Scalars['String']>,
  passportNumber?: Maybe<Scalars['String']>,
};

export type VisaApplicationArgument = {
  approved?: Maybe<BooleanArgument>,
  city?: Maybe<StringArgument>,
  country?: Maybe<StringArgument>,
  duration?: Maybe<NumericArgument>,
  firstName?: Maybe<StringArgument>,
  lastName?: Maybe<StringArgument>,
  nationality?: Maybe<StringArgument>,
  passportNumber?: Maybe<StringArgument>,
};

export type VisaApplicationOrderBy = {
  approved?: Maybe<OrderBy>,
  city?: Maybe<OrderBy>,
  country?: Maybe<OrderBy>,
  duration?: Maybe<OrderBy>,
  firstName?: Maybe<OrderBy>,
  lastName?: Maybe<OrderBy>,
  nationality?: Maybe<OrderBy>,
  passportNumber?: Maybe<OrderBy>,
};

export type VisaApplications = {
   __typename?: 'VisaApplications',
  id?: Maybe<Scalars['String']>,
  visaApplication?: Maybe<VisaApplication>,
  metadata?: Maybe<KogitoMetadata>,
};

export type VisaApplicationsArgument = {
  and?: Maybe<Array<VisaApplicationsArgument>>,
  or?: Maybe<Array<VisaApplicationsArgument>>,
  id?: Maybe<IdArgument>,
  visaApplication?: Maybe<VisaApplicationArgument>,
  metadata?: Maybe<KogitoMetadataArgument>,
};

export type VisaApplicationsOrderBy = {
  visaApplication?: Maybe<VisaApplicationOrderBy>,
  metadata?: Maybe<KogitoMetadataOrderBy>,
};

export type GetUserTasksByStateQueryVariables = {
  state?: Maybe<Scalars['String']>
};


export type GetUserTasksByStateQuery = (
  { __typename?: 'Query' }
  & { UserTaskInstances: Maybe<Array<Maybe<(
    { __typename?: 'UserTaskInstance' }
    & Pick<UserTaskInstance, 'id' | 'description' | 'name' | 'priority' | 'processInstanceId' | 'processId' | 'rootProcessInstanceId' | 'rootProcessId' | 'state' | 'actualOwner' | 'adminGroups' | 'adminUsers' | 'completed' | 'started' | 'excludedUsers' | 'potentialGroups' | 'potentialUsers' | 'inputs' | 'outputs' | 'referenceName'>
  )>>> }
);

export type GetUserTasksByStatesQueryVariables = {
  state?: Maybe<Array<Scalars['String']>>
};


export type GetUserTasksByStatesQuery = (
  { __typename?: 'Query' }
  & { UserTaskInstances: Maybe<Array<Maybe<(
    { __typename?: 'UserTaskInstance' }
    & Pick<UserTaskInstance, 'id' | 'description' | 'name' | 'priority' | 'processInstanceId' | 'processId' | 'rootProcessInstanceId' | 'rootProcessId' | 'state' | 'actualOwner' | 'adminGroups' | 'adminUsers' | 'completed' | 'started' | 'excludedUsers' | 'potentialGroups' | 'potentialUsers' | 'inputs' | 'outputs' | 'referenceName'>
  )>>> }
);

export type GetUserTaskByIdQueryVariables = {
  id?: Maybe<Scalars['String']>
};


export type GetUserTaskByIdQuery = (
  { __typename?: 'Query' }
  & { UserTaskInstances: Maybe<Array<Maybe<(
    { __typename?: 'UserTaskInstance' }
    & Pick<UserTaskInstance, 'id' | 'description' | 'name' | 'priority' | 'processInstanceId' | 'processId' | 'rootProcessInstanceId' | 'rootProcessId' | 'state' | 'actualOwner' | 'adminGroups' | 'adminUsers' | 'completed' | 'started' | 'excludedUsers' | 'potentialGroups' | 'potentialUsers' | 'inputs' | 'outputs' | 'referenceName'>
  )>>> }
);

export type GetProcessInstanceByIdQueryVariables = {
  id?: Maybe<Scalars['String']>
};


export type GetProcessInstanceByIdQuery = (
  { __typename?: 'Query' }
  & { ProcessInstances: Maybe<Array<Maybe<(
    { __typename?: 'ProcessInstance' }
    & Pick<ProcessInstance, 'id' | 'processId' | 'processName' | 'endpoint'>
  )>>> }
);


export const GetUserTasksByStateDocument = gql`
    query getUserTasksByState($state: String) {
  UserTaskInstances(where: {state: {equal: $state}}) {
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
  }
}
    `;

/**
 * __useGetUserTasksByStateQuery__
 *
 * To run a query within a React component, call `useGetUserTasksByStateQuery` and pass it any options that fit your needs.
 * When your component renders, `useGetUserTasksByStateQuery` returns an object from Apollo Client that contains loading, error, and data properties 
 * you can use to render your UI.
 *
 * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
 *
 * @example
 * const { data, loading, error } = useGetUserTasksByStateQuery({
 *   variables: {
 *      currentState: // value for 'currentState'
 *   },
 * });
 */
export function useGetUserTasksByStateQuery(baseOptions?: ApolloReactHooks.QueryHookOptions<GetUserTasksByStateQuery, GetUserTasksByStateQueryVariables>) {
        return ApolloReactHooks.useQuery<GetUserTasksByStateQuery, GetUserTasksByStateQueryVariables>(GetUserTasksByStateDocument, baseOptions);
      }
export function useGetUserTasksByStateLazyQuery(baseOptions?: ApolloReactHooks.LazyQueryHookOptions<GetUserTasksByStateQuery, GetUserTasksByStateQueryVariables>) {
          return ApolloReactHooks.useLazyQuery<GetUserTasksByStateQuery, GetUserTasksByStateQueryVariables>(GetUserTasksByStateDocument, baseOptions);
        }
export type GetUserTasksByStateQueryHookResult = ReturnType<typeof useGetUserTasksByStateQuery>;
export type GetUserTasksByStateLazyQueryHookResult = ReturnType<typeof useGetUserTasksByStateLazyQuery>;
export type GetUserTasksByStateQueryResult = ApolloReactCommon.QueryResult<GetUserTasksByStateQuery, GetUserTasksByStateQueryVariables>;
export const GetUserTasksByStatesDocument = gql`
    query getUserTasksByStates($state: [String!]) {
  UserTaskInstances(where: {state: {in: $state}}) {
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
 *      currentState: // value for 'currentState'
 *   },
 * });
 */
export function useGetUserTasksByStatesQuery(baseOptions?: ApolloReactHooks.QueryHookOptions<GetUserTasksByStatesQuery, GetUserTasksByStatesQueryVariables>) {
        return ApolloReactHooks.useQuery<GetUserTasksByStatesQuery, GetUserTasksByStatesQueryVariables>(GetUserTasksByStatesDocument, baseOptions);
      }
export function useGetUserTasksByStatesLazyQuery(baseOptions?: ApolloReactHooks.LazyQueryHookOptions<GetUserTasksByStatesQuery, GetUserTasksByStatesQueryVariables>) {
          return ApolloReactHooks.useLazyQuery<GetUserTasksByStatesQuery, GetUserTasksByStatesQueryVariables>(GetUserTasksByStatesDocument, baseOptions);
        }
export type GetUserTasksByStatesQueryHookResult = ReturnType<typeof useGetUserTasksByStatesQuery>;
export type GetUserTasksByStatesLazyQueryHookResult = ReturnType<typeof useGetUserTasksByStatesLazyQuery>;
export type GetUserTasksByStatesQueryResult = ApolloReactCommon.QueryResult<GetUserTasksByStatesQuery, GetUserTasksByStatesQueryVariables>;
export const GetUserTaskByIdDocument = gql`
    query getUserTaskById($id: String) {
  UserTaskInstances(where: {id: {equal: $id}}) {
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
export function useGetUserTaskByIdQuery(baseOptions?: ApolloReactHooks.QueryHookOptions<GetUserTaskByIdQuery, GetUserTaskByIdQueryVariables>) {
        return ApolloReactHooks.useQuery<GetUserTaskByIdQuery, GetUserTaskByIdQueryVariables>(GetUserTaskByIdDocument, baseOptions);
      }
export function useGetUserTaskByIdLazyQuery(baseOptions?: ApolloReactHooks.LazyQueryHookOptions<GetUserTaskByIdQuery, GetUserTaskByIdQueryVariables>) {
          return ApolloReactHooks.useLazyQuery<GetUserTaskByIdQuery, GetUserTaskByIdQueryVariables>(GetUserTaskByIdDocument, baseOptions);
        }
export type GetUserTaskByIdQueryHookResult = ReturnType<typeof useGetUserTaskByIdQuery>;
export type GetUserTaskByIdLazyQueryHookResult = ReturnType<typeof useGetUserTaskByIdLazyQuery>;
export type GetUserTaskByIdQueryResult = ApolloReactCommon.QueryResult<GetUserTaskByIdQuery, GetUserTaskByIdQueryVariables>;
export const GetProcessInstanceByIdDocument = gql`
    query getProcessInstanceById($id: String) {
  ProcessInstances(where: {id: {equal: $id}}) {
    id
    processId
    processName
    endpoint
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
export function useGetProcessInstanceByIdQuery(baseOptions?: ApolloReactHooks.QueryHookOptions<GetProcessInstanceByIdQuery, GetProcessInstanceByIdQueryVariables>) {
        return ApolloReactHooks.useQuery<GetProcessInstanceByIdQuery, GetProcessInstanceByIdQueryVariables>(GetProcessInstanceByIdDocument, baseOptions);
      }
export function useGetProcessInstanceByIdLazyQuery(baseOptions?: ApolloReactHooks.LazyQueryHookOptions<GetProcessInstanceByIdQuery, GetProcessInstanceByIdQueryVariables>) {
          return ApolloReactHooks.useLazyQuery<GetProcessInstanceByIdQuery, GetProcessInstanceByIdQueryVariables>(GetProcessInstanceByIdDocument, baseOptions);
        }
export type GetProcessInstanceByIdQueryHookResult = ReturnType<typeof useGetProcessInstanceByIdQuery>;
export type GetProcessInstanceByIdLazyQueryHookResult = ReturnType<typeof useGetProcessInstanceByIdLazyQuery>;
export type GetProcessInstanceByIdQueryResult = ApolloReactCommon.QueryResult<GetProcessInstanceByIdQuery, GetProcessInstanceByIdQueryVariables>;