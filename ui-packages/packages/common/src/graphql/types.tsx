/* eslint-disable */
import gql from 'graphql-tag';
import * as ApolloReactCommon from '@apollo/react-common';
import * as ApolloReactHooks from '@apollo/react-hooks';

export namespace GraphQL {
  export type Maybe<T> = T | null;
  export type Exact<T extends { [key: string]: any }> = {
    [K in keyof T]: T[K];
  };

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

  export type Job = {
    __typename?: 'Job';
    id: Scalars['String'];
    processId?: Maybe<Scalars['String']>;
    processInstanceId?: Maybe<Scalars['String']>;
    nodeInstanceId?: Maybe<Scalars['String']>;
    rootProcessInstanceId?: Maybe<Scalars['String']>;
    rootProcessId?: Maybe<Scalars['String']>;
    status: JobStatus;
    expirationTime?: Maybe<Scalars['DateTime']>;
    priority?: Maybe<Scalars['Int']>;
    callbackEndpoint?: Maybe<Scalars['String']>;
    repeatInterval?: Maybe<Scalars['Int']>;
    repeatLimit?: Maybe<Scalars['Int']>;
    scheduledId?: Maybe<Scalars['String']>;
    retries?: Maybe<Scalars['Int']>;
    lastUpdate?: Maybe<Scalars['DateTime']>;
    executionCounter?: Maybe<Scalars['Int']>;
    endpoint?: Maybe<Scalars['String']>;
  };

  export type JobArgument = {
    and?: Maybe<Array<JobArgument>>;
    or?: Maybe<Array<JobArgument>>;
    not?: Maybe<JobArgument>;
    id?: Maybe<IdArgument>;
    processId?: Maybe<StringArgument>;
    processInstanceId?: Maybe<IdArgument>;
    nodeInstanceId?: Maybe<IdArgument>;
    rootProcessInstanceId?: Maybe<IdArgument>;
    rootProcessId?: Maybe<StringArgument>;
    status?: Maybe<JobStatusArgument>;
    expirationTime?: Maybe<DateArgument>;
    priority?: Maybe<NumericArgument>;
    scheduledId?: Maybe<IdArgument>;
    lastUpdate?: Maybe<DateArgument>;
  };

  export type JobOrderBy = {
    processId?: Maybe<OrderBy>;
    rootProcessId?: Maybe<OrderBy>;
    status?: Maybe<OrderBy>;
    expirationTime?: Maybe<OrderBy>;
    priority?: Maybe<OrderBy>;
    retries?: Maybe<OrderBy>;
    lastUpdate?: Maybe<OrderBy>;
    executionCounter?: Maybe<OrderBy>;
  };

  export enum JobStatus {
    Error = 'ERROR',
    Executed = 'EXECUTED',
    Scheduled = 'SCHEDULED',
    Retry = 'RETRY',
    Canceled = 'CANCELED'
  }

  export type JobStatusArgument = {
    equal?: Maybe<JobStatus>;
    in?: Maybe<Array<Maybe<JobStatus>>>;
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

  export type Milestone = {
    __typename?: 'Milestone';
    id: Scalars['String'];
    name: Scalars['String'];
    status: MilestoneStatus;
  };

  export type MilestoneArgument = {
    id?: Maybe<IdArgument>;
    name?: Maybe<StringArgument>;
    status?: Maybe<MilestoneStatusArgument>;
  };

  export enum MilestoneStatus {
    Available = 'AVAILABLE',
    Active = 'ACTIVE',
    Completed = 'COMPLETED'
  }

  export type MilestoneStatusArgument = {
    equal?: Maybe<MilestoneStatus>;
    in?: Maybe<Array<Maybe<MilestoneStatus>>>;
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
    serviceUrl?: Maybe<Scalars['String']>;
    nodes: Array<NodeInstance>;
    milestones?: Maybe<Array<Milestone>>;
    variables?: Maybe<Scalars['String']>;
    start: Scalars['DateTime'];
    end?: Maybe<Scalars['DateTime']>;
    parentProcessInstance?: Maybe<ProcessInstance>;
    childProcessInstances?: Maybe<Array<ProcessInstance>>;
    error?: Maybe<ProcessInstanceError>;
    addons?: Maybe<Array<Scalars['String']>>;
    lastUpdate: Scalars['DateTime'];
    businessKey?: Maybe<Scalars['String']>;
  };

  export type ProcessInstanceArgument = {
    and?: Maybe<Array<ProcessInstanceArgument>>;
    or?: Maybe<Array<ProcessInstanceArgument>>;
    not?: Maybe<ProcessInstanceArgument>;
    id?: Maybe<IdArgument>;
    processId?: Maybe<StringArgument>;
    processName?: Maybe<StringArgument>;
    parentProcessInstanceId?: Maybe<IdArgument>;
    rootProcessInstanceId?: Maybe<IdArgument>;
    rootProcessId?: Maybe<StringArgument>;
    state?: Maybe<ProcessInstanceStateArgument>;
    error?: Maybe<ProcessInstanceErrorArgument>;
    nodes?: Maybe<NodeInstanceArgument>;
    milestones?: Maybe<MilestoneArgument>;
    endpoint?: Maybe<StringArgument>;
    roles?: Maybe<StringArrayArgument>;
    start?: Maybe<DateArgument>;
    end?: Maybe<DateArgument>;
    addons?: Maybe<StringArrayArgument>;
    lastUpdate?: Maybe<DateArgument>;
    businessKey?: Maybe<StringArgument>;
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
    serviceUrl?: Maybe<Scalars['String']>;
    start: Scalars['DateTime'];
    end?: Maybe<Scalars['DateTime']>;
    lastUpdate: Scalars['DateTime'];
    businessKey?: Maybe<Scalars['String']>;
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
    businessKey?: Maybe<StringArgument>;
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
    businessKey?: Maybe<OrderBy>;
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
    Jobs?: Maybe<Array<Maybe<Job>>>;
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

  export type QueryJobsArgs = {
    where?: Maybe<JobArgument>;
    orderBy?: Maybe<JobOrderBy>;
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
    JobAdded: Job;
    JobUpdated: Job;
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
    endpoint?: Maybe<Scalars['String']>;
  };

  export type UserTaskInstanceArgument = {
    and?: Maybe<Array<UserTaskInstanceArgument>>;
    or?: Maybe<Array<UserTaskInstanceArgument>>;
    not?: Maybe<UserTaskInstanceArgument>;
    state?: Maybe<StringArgument>;
    id?: Maybe<IdArgument>;
    description?: Maybe<StringArgument>;
    name?: Maybe<StringArgument>;
    priority?: Maybe<StringArgument>;
    processId?: Maybe<StringArgument>;
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
    processId?: Maybe<OrderBy>;
    completed?: Maybe<OrderBy>;
    started?: Maybe<OrderBy>;
    referenceName?: Maybe<OrderBy>;
    lastUpdate?: Maybe<OrderBy>;
  };

  /**
   * A Directive provides a way to describe alternate runtime execution and type validation behavior in a GraphQL document.
   *
   * In some cases, you need to provide options to alter GraphQL's execution behavior
   * in ways field arguments will not suffice, such as conditionally including or
   * skipping a field. Directives provide this by describing additional information
   * to the executor.
   */
  export type __Directive = {
    __typename?: '__Directive';
    name: Scalars['String'];
    description?: Maybe<Scalars['String']>;
    locations: Array<__DirectiveLocation>;
    args: Array<__InputValue>;
  };

  /**
   * A Directive can be adjacent to many parts of the GraphQL language, a
   * __DirectiveLocation describes one such possible adjacencies.
   */
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

  /**
   * One possible value for a given Enum. Enum values are unique values, not a
   * placeholder for a string or numeric value. However an Enum value is returned in
   * a JSON response as a string.
   */
  export type __EnumValue = {
    __typename?: '__EnumValue';
    name: Scalars['String'];
    description?: Maybe<Scalars['String']>;
    isDeprecated: Scalars['Boolean'];
    deprecationReason?: Maybe<Scalars['String']>;
  };

  /**
   * Object and Interface types are described by a list of Fields, each of which has
   * a name, potentially a list of arguments, and a return type.
   */
  export type __Field = {
    __typename?: '__Field';
    name: Scalars['String'];
    description?: Maybe<Scalars['String']>;
    args: Array<__InputValue>;
    type: __Type;
    isDeprecated: Scalars['Boolean'];
    deprecationReason?: Maybe<Scalars['String']>;
  };

  /**
   * Arguments provided to Fields or Directives and the input fields of an
   * InputObject are represented as Input Values which describe their type and
   * optionally a default value.
   */
  export type __InputValue = {
    __typename?: '__InputValue';
    name: Scalars['String'];
    description?: Maybe<Scalars['String']>;
    type: __Type;
    /** A GraphQL-formatted string representing the default value for this input value. */
    defaultValue?: Maybe<Scalars['String']>;
  };

  /**
   * A GraphQL Schema defines the capabilities of a GraphQL server. It exposes all
   * available types and directives on the server, as well as the entry points for
   * query, mutation, and subscription operations.
   */
  export type __Schema = {
    __typename?: '__Schema';
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
   * The fundamental unit of any GraphQL Schema is the type. There are many kinds of
   * types in GraphQL as represented by the `__TypeKind` enum.
   *
   * Depending on the kind of a type, certain fields describe information about that
   * type. Scalar types provide no information beyond a name and description, while
   * Enum types provide their values. Object and Interface types provide the fields
   * they describe. Abstract types, Union and Interface, provide the Object types
   * possible at runtime. List and NonNull types compose other types.
   */
  export type __Type = {
    __typename?: '__Type';
    kind: __TypeKind;
    name?: Maybe<Scalars['String']>;
    description?: Maybe<Scalars['String']>;
    fields?: Maybe<Array<__Field>>;
    interfaces?: Maybe<Array<__Type>>;
    possibleTypes?: Maybe<Array<__Type>>;
    enumValues?: Maybe<Array<__EnumValue>>;
    inputFields?: Maybe<Array<__InputValue>>;
    ofType?: Maybe<__Type>;
  };

  /**
   * The fundamental unit of any GraphQL Schema is the type. There are many kinds of
   * types in GraphQL as represented by the `__TypeKind` enum.
   *
   * Depending on the kind of a type, certain fields describe information about that
   * type. Scalar types provide no information beyond a name and description, while
   * Enum types provide their values. Object and Interface types provide the fields
   * they describe. Abstract types, Union and Interface, provide the Object types
   * possible at runtime. List and NonNull types compose other types.
   */
  export type __TypeFieldsArgs = {
    includeDeprecated?: Maybe<Scalars['Boolean']>;
  };

  /**
   * The fundamental unit of any GraphQL Schema is the type. There are many kinds of
   * types in GraphQL as represented by the `__TypeKind` enum.
   *
   * Depending on the kind of a type, certain fields describe information about that
   * type. Scalar types provide no information beyond a name and description, while
   * Enum types provide their values. Object and Interface types provide the fields
   * they describe. Abstract types, Union and Interface, provide the Object types
   * possible at runtime. List and NonNull types compose other types.
   */
  export type __TypeEnumValuesArgs = {
    includeDeprecated?: Maybe<Scalars['Boolean']>;
  };

  /** An enum describing what kind of type a given `__Type` is. */
  export enum __TypeKind {
    /** Indicates this type is a scalar. */
    Scalar = 'SCALAR',
    /** Indicates this type is an object. `fields` and `interfaces` are valid fields. */
    Object = 'OBJECT',
    /** Indicates this type is an interface. `fields` and `possibleTypes` are valid fields. */
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
    where?: Maybe<ProcessInstanceArgument>;
    orderBy?: Maybe<ProcessInstanceOrderBy>;
    offset?: Maybe<Scalars['Int']>;
    limit?: Maybe<Scalars['Int']>;
  }>;

  export type GetProcessInstancesQuery = { __typename?: 'Query' } & {
    ProcessInstances?: Maybe<
      Array<
        Maybe<
          { __typename?: 'ProcessInstance' } & Pick<
            ProcessInstance,
            | 'id'
            | 'processId'
            | 'processName'
            | 'parentProcessInstanceId'
            | 'rootProcessInstanceId'
            | 'roles'
            | 'state'
            | 'start'
            | 'lastUpdate'
            | 'addons'
            | 'businessKey'
            | 'serviceUrl'
          > & {
              error?: Maybe<
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

  export type GetChildInstancesQueryVariables = Exact<{
    rootProcessInstanceId?: Maybe<Scalars['String']>;
  }>;

  export type GetChildInstancesQuery = { __typename?: 'Query' } & {
    ProcessInstances?: Maybe<
      Array<
        Maybe<
          { __typename?: 'ProcessInstance' } & Pick<
            ProcessInstance,
            | 'id'
            | 'processId'
            | 'processName'
            | 'parentProcessInstanceId'
            | 'rootProcessInstanceId'
            | 'roles'
            | 'state'
            | 'start'
            | 'lastUpdate'
            | 'serviceUrl'
            | 'addons'
            | 'businessKey'
          > & {
              error?: Maybe<
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

  export type GetProcessInstanceByIdQueryVariables = Exact<{
    id?: Maybe<Scalars['String']>;
  }>;

  export type GetProcessInstanceByIdQuery = { __typename?: 'Query' } & {
    ProcessInstances?: Maybe<
      Array<
        Maybe<
          { __typename?: 'ProcessInstance' } & Pick<
            ProcessInstance,
            | 'id'
            | 'processId'
            | 'processName'
            | 'businessKey'
            | 'parentProcessInstanceId'
            | 'roles'
            | 'variables'
            | 'state'
            | 'start'
            | 'lastUpdate'
            | 'end'
            | 'addons'
            | 'endpoint'
            | 'serviceUrl'
          > & {
              parentProcessInstance?: Maybe<
                { __typename?: 'ProcessInstance' } & Pick<
                  ProcessInstance,
                  'id' | 'processName' | 'businessKey'
                >
              >;
              error?: Maybe<
                { __typename?: 'ProcessInstanceError' } & Pick<
                  ProcessInstanceError,
                  'nodeDefinitionId' | 'message'
                >
              >;
              childProcessInstances?: Maybe<
                Array<
                  { __typename?: 'ProcessInstance' } & Pick<
                    ProcessInstance,
                    'id' | 'processName' | 'businessKey'
                  >
                >
              >;
              nodes: Array<
                { __typename?: 'NodeInstance' } & Pick<
                  NodeInstance,
                  | 'id'
                  | 'nodeId'
                  | 'name'
                  | 'enter'
                  | 'exit'
                  | 'type'
                  | 'definitionId'
                >
              >;
              milestones?: Maybe<
                Array<
                  { __typename?: 'Milestone' } & Pick<
                    Milestone,
                    'id' | 'name' | 'status'
                  >
                >
              >;
            }
        >
      >
    >;
  };

  export type GetColumnPickerAttributesQueryVariables = Exact<{
    columnPickerType: Scalars['String'];
  }>;

  export type GetColumnPickerAttributesQuery = { __typename?: 'Query' } & {
    __type?: Maybe<
      { __typename?: '__Type' } & Pick<__Type, 'name'> & {
          fields?: Maybe<
            Array<
              { __typename?: '__Field' } & Pick<__Field, 'name'> & {
                  type: { __typename?: '__Type' } & Pick<
                    __Type,
                    'name' | 'kind'
                  > & {
                      fields?: Maybe<
                        Array<
                          { __typename?: '__Field' } & Pick<__Field, 'name'> & {
                              type: { __typename?: '__Type' } & Pick<
                                __Type,
                                'name' | 'kind'
                              >;
                            }
                        >
                      >;
                    };
                }
            >
          >;
        }
    >;
  };

  export type GetQueryTypesQueryVariables = Exact<{ [key: string]: never }>;

  export type GetQueryTypesQuery = { __typename?: 'Query' } & {
    __schema: { __typename?: '__Schema' } & {
      queryType: Array<
        { __typename?: '__Type' } & Pick<__Type, 'name' | 'kind'> & {
            fields?: Maybe<
              Array<
                { __typename?: '__Field' } & Pick<__Field, 'name'> & {
                    type: { __typename?: '__Type' } & Pick<
                      __Type,
                      'name' | 'kind'
                    >;
                  }
              >
            >;
            inputFields?: Maybe<
              Array<
                { __typename?: '__InputValue' } & Pick<__InputValue, 'name'> & {
                    type: { __typename?: '__Type' } & Pick<
                      __Type,
                      'name' | 'kind'
                    >;
                  }
              >
            >;
          }
      >;
    };
  };

  export type GetQueryFieldsQueryVariables = Exact<{ [key: string]: never }>;

  export type GetQueryFieldsQuery = { __typename?: 'Query' } & {
    __type?: Maybe<
      { __typename?: '__Type' } & Pick<__Type, 'name'> & {
          fields?: Maybe<
            Array<
              { __typename?: '__Field' } & Pick<__Field, 'name'> & {
                  args: Array<
                    { __typename?: '__InputValue' } & Pick<
                      __InputValue,
                      'name'
                    > & {
                        type: { __typename?: '__Type' } & Pick<
                          __Type,
                          'kind' | 'name'
                        >;
                      }
                  >;
                  type: { __typename?: '__Type' } & {
                    ofType?: Maybe<
                      { __typename?: '__Type' } & Pick<__Type, 'name'>
                    >;
                  };
                }
            >
          >;
        }
    >;
  };

  export type GetInputFieldsFromQueryQueryVariables = Exact<{
    currentQuery: Scalars['String'];
  }>;

  export type GetInputFieldsFromQueryQuery = { __typename?: 'Query' } & {
    __type?: Maybe<
      { __typename?: '__Type' } & Pick<__Type, 'name'> & {
          inputFields?: Maybe<
            Array<
              { __typename?: '__InputValue' } & Pick<__InputValue, 'name'> & {
                  type: { __typename?: '__Type' } & Pick<
                    __Type,
                    'name' | 'kind'
                  > & {
                      inputFields?: Maybe<
                        Array<
                          { __typename?: '__InputValue' } & Pick<
                            __InputValue,
                            'name'
                          > & {
                              type: { __typename?: '__Type' } & Pick<
                                __Type,
                                'name'
                              >;
                            }
                        >
                      >;
                    };
                }
            >
          >;
        }
    >;
  };

  export type GetInputFieldsFromTypeQueryVariables = Exact<{
    type: Scalars['String'];
  }>;

  export type GetInputFieldsFromTypeQuery = { __typename?: 'Query' } & {
    __type?: Maybe<
      { __typename?: '__Type' } & Pick<__Type, 'name'> & {
          inputFields?: Maybe<
            Array<
              { __typename?: '__InputValue' } & Pick<__InputValue, 'name'> & {
                  type: { __typename?: '__Type' } & Pick<
                    __Type,
                    'name' | 'kind'
                  > & {
                      enumValues?: Maybe<
                        Array<
                          { __typename?: '__EnumValue' } & Pick<
                            __EnumValue,
                            'name'
                          >
                        >
                      >;
                      ofType?: Maybe<
                        { __typename?: '__Type' } & Pick<
                          __Type,
                          'kind' | 'name'
                        > & {
                            enumValues?: Maybe<
                              Array<
                                { __typename?: '__EnumValue' } & Pick<
                                  __EnumValue,
                                  'name'
                                >
                              >
                            >;
                          }
                      >;
                    };
                }
            >
          >;
        }
    >;
  };

  export type GetUserTasksByStatesQueryVariables = Exact<{
    state?: Maybe<Array<Scalars['String']>>;
    orderBy?: Maybe<UserTaskInstanceOrderBy>;
  }>;

  export type GetUserTasksByStatesQuery = { __typename?: 'Query' } & {
    UserTaskInstances?: Maybe<
      Array<
        Maybe<
          { __typename?: 'UserTaskInstance' } & Pick<
            UserTaskInstance,
            | 'id'
            | 'name'
            | 'referenceName'
            | 'description'
            | 'priority'
            | 'processInstanceId'
            | 'processId'
            | 'rootProcessInstanceId'
            | 'rootProcessId'
            | 'state'
            | 'actualOwner'
            | 'adminGroups'
            | 'adminUsers'
            | 'completed'
            | 'started'
            | 'excludedUsers'
            | 'potentialGroups'
            | 'potentialUsers'
            | 'inputs'
            | 'outputs'
            | 'endpoint'
          >
        >
      >
    >;
  };

  export type GetUserTaskByIdQueryVariables = Exact<{
    id?: Maybe<Scalars['String']>;
  }>;

  export type GetUserTaskByIdQuery = { __typename?: 'Query' } & {
    UserTaskInstances?: Maybe<
      Array<
        Maybe<
          { __typename?: 'UserTaskInstance' } & Pick<
            UserTaskInstance,
            | 'id'
            | 'description'
            | 'name'
            | 'priority'
            | 'processInstanceId'
            | 'processId'
            | 'rootProcessInstanceId'
            | 'rootProcessId'
            | 'state'
            | 'actualOwner'
            | 'adminGroups'
            | 'adminUsers'
            | 'completed'
            | 'started'
            | 'excludedUsers'
            | 'potentialGroups'
            | 'potentialUsers'
            | 'inputs'
            | 'outputs'
            | 'referenceName'
            | 'endpoint'
            | 'lastUpdate'
          >
        >
      >
    >;
  };

  export type GetTasksForUserQueryVariables = Exact<{
    whereArgument?: Maybe<UserTaskInstanceArgument>;
    offset?: Maybe<Scalars['Int']>;
    limit?: Maybe<Scalars['Int']>;
    orderBy?: Maybe<UserTaskInstanceOrderBy>;
  }>;

  export type GetTasksForUserQuery = { __typename?: 'Query' } & {
    UserTaskInstances?: Maybe<
      Array<
        Maybe<
          { __typename?: 'UserTaskInstance' } & Pick<
            UserTaskInstance,
            | 'id'
            | 'name'
            | 'referenceName'
            | 'description'
            | 'priority'
            | 'processInstanceId'
            | 'processId'
            | 'rootProcessInstanceId'
            | 'rootProcessId'
            | 'state'
            | 'actualOwner'
            | 'adminGroups'
            | 'adminUsers'
            | 'completed'
            | 'started'
            | 'excludedUsers'
            | 'potentialGroups'
            | 'potentialUsers'
            | 'inputs'
            | 'outputs'
            | 'lastUpdate'
            | 'endpoint'
          >
        >
      >
    >;
  };

  export type GetJobsByProcessInstanceIdQueryVariables = Exact<{
    processInstanceId?: Maybe<Scalars['String']>;
  }>;

  export type GetJobsByProcessInstanceIdQuery = { __typename?: 'Query' } & {
    Jobs?: Maybe<
      Array<
        Maybe<
          { __typename?: 'Job' } & Pick<
            Job,
            | 'id'
            | 'processId'
            | 'processInstanceId'
            | 'rootProcessId'
            | 'status'
            | 'expirationTime'
            | 'priority'
            | 'callbackEndpoint'
            | 'repeatInterval'
            | 'repeatLimit'
            | 'scheduledId'
            | 'retries'
            | 'lastUpdate'
            | 'endpoint'
            | 'nodeInstanceId'
            | 'executionCounter'
          >
        >
      >
    >;
  };

  export type GetJobsWithFiltersQueryVariables = Exact<{
    values?: Maybe<Array<Maybe<JobStatus>>>;
    orderBy?: Maybe<JobOrderBy>;
    offset?: Maybe<Scalars['Int']>;
    limit?: Maybe<Scalars['Int']>;
  }>;

  export type GetJobsWithFiltersQuery = { __typename?: 'Query' } & {
    Jobs?: Maybe<
      Array<
        Maybe<
          { __typename?: 'Job' } & Pick<
            Job,
            | 'id'
            | 'processId'
            | 'processInstanceId'
            | 'rootProcessId'
            | 'status'
            | 'expirationTime'
            | 'priority'
            | 'callbackEndpoint'
            | 'repeatInterval'
            | 'repeatLimit'
            | 'scheduledId'
            | 'retries'
            | 'lastUpdate'
            | 'endpoint'
            | 'executionCounter'
          >
        >
      >
    >;
  };

  export const GetProcessInstancesDocument = gql`
    query getProcessInstances(
      $where: ProcessInstanceArgument
      $orderBy: ProcessInstanceOrderBy
      $offset: Int
      $limit: Int
    ) {
      ProcessInstances(
        where: $where
        orderBy: $orderBy
        pagination: { offset: $offset, limit: $limit }
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
   *      orderBy: // value for 'orderBy'
   *      offset: // value for 'offset'
   *      limit: // value for 'limit'
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
    baseOptions?: ApolloReactHooks.QueryHookOptions<
      GetColumnPickerAttributesQuery,
      GetColumnPickerAttributesQueryVariables
    >
  ) {
    return ApolloReactHooks.useQuery<
      GetColumnPickerAttributesQuery,
      GetColumnPickerAttributesQueryVariables
    >(GetColumnPickerAttributesDocument, baseOptions);
  }
  export function useGetColumnPickerAttributesLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetColumnPickerAttributesQuery,
      GetColumnPickerAttributesQueryVariables
    >
  ) {
    return ApolloReactHooks.useLazyQuery<
      GetColumnPickerAttributesQuery,
      GetColumnPickerAttributesQueryVariables
    >(GetColumnPickerAttributesDocument, baseOptions);
  }
  export type GetColumnPickerAttributesQueryHookResult = ReturnType<
    typeof useGetColumnPickerAttributesQuery
  >;
  export type GetColumnPickerAttributesLazyQueryHookResult = ReturnType<
    typeof useGetColumnPickerAttributesLazyQuery
  >;
  export type GetColumnPickerAttributesQueryResult =
    ApolloReactCommon.QueryResult<
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
    return ApolloReactHooks.useQuery<
      GetQueryTypesQuery,
      GetQueryTypesQueryVariables
    >(GetQueryTypesDocument, baseOptions);
  }
  export function useGetQueryTypesLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetQueryTypesQuery,
      GetQueryTypesQueryVariables
    >
  ) {
    return ApolloReactHooks.useLazyQuery<
      GetQueryTypesQuery,
      GetQueryTypesQueryVariables
    >(GetQueryTypesDocument, baseOptions);
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
    return ApolloReactHooks.useQuery<
      GetQueryFieldsQuery,
      GetQueryFieldsQueryVariables
    >(GetQueryFieldsDocument, baseOptions);
  }
  export function useGetQueryFieldsLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetQueryFieldsQuery,
      GetQueryFieldsQueryVariables
    >
  ) {
    return ApolloReactHooks.useLazyQuery<
      GetQueryFieldsQuery,
      GetQueryFieldsQueryVariables
    >(GetQueryFieldsDocument, baseOptions);
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
    baseOptions?: ApolloReactHooks.QueryHookOptions<
      GetInputFieldsFromQueryQuery,
      GetInputFieldsFromQueryQueryVariables
    >
  ) {
    return ApolloReactHooks.useQuery<
      GetInputFieldsFromQueryQuery,
      GetInputFieldsFromQueryQueryVariables
    >(GetInputFieldsFromQueryDocument, baseOptions);
  }
  export function useGetInputFieldsFromQueryLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetInputFieldsFromQueryQuery,
      GetInputFieldsFromQueryQueryVariables
    >
  ) {
    return ApolloReactHooks.useLazyQuery<
      GetInputFieldsFromQueryQuery,
      GetInputFieldsFromQueryQueryVariables
    >(GetInputFieldsFromQueryDocument, baseOptions);
  }
  export type GetInputFieldsFromQueryQueryHookResult = ReturnType<
    typeof useGetInputFieldsFromQueryQuery
  >;
  export type GetInputFieldsFromQueryLazyQueryHookResult = ReturnType<
    typeof useGetInputFieldsFromQueryLazyQuery
  >;
  export type GetInputFieldsFromQueryQueryResult =
    ApolloReactCommon.QueryResult<
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
    baseOptions?: ApolloReactHooks.QueryHookOptions<
      GetInputFieldsFromTypeQuery,
      GetInputFieldsFromTypeQueryVariables
    >
  ) {
    return ApolloReactHooks.useQuery<
      GetInputFieldsFromTypeQuery,
      GetInputFieldsFromTypeQueryVariables
    >(GetInputFieldsFromTypeDocument, baseOptions);
  }
  export function useGetInputFieldsFromTypeLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetInputFieldsFromTypeQuery,
      GetInputFieldsFromTypeQueryVariables
    >
  ) {
    return ApolloReactHooks.useLazyQuery<
      GetInputFieldsFromTypeQuery,
      GetInputFieldsFromTypeQueryVariables
    >(GetInputFieldsFromTypeDocument, baseOptions);
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
    return ApolloReactHooks.useQuery<
      GetUserTasksByStatesQuery,
      GetUserTasksByStatesQueryVariables
    >(GetUserTasksByStatesDocument, baseOptions);
  }
  export function useGetUserTasksByStatesLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetUserTasksByStatesQuery,
      GetUserTasksByStatesQueryVariables
    >
  ) {
    return ApolloReactHooks.useLazyQuery<
      GetUserTasksByStatesQuery,
      GetUserTasksByStatesQueryVariables
    >(GetUserTasksByStatesDocument, baseOptions);
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
    return ApolloReactHooks.useQuery<
      GetUserTaskByIdQuery,
      GetUserTaskByIdQueryVariables
    >(GetUserTaskByIdDocument, baseOptions);
  }
  export function useGetUserTaskByIdLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetUserTaskByIdQuery,
      GetUserTaskByIdQueryVariables
    >
  ) {
    return ApolloReactHooks.useLazyQuery<
      GetUserTaskByIdQuery,
      GetUserTaskByIdQueryVariables
    >(GetUserTaskByIdDocument, baseOptions);
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
    return ApolloReactHooks.useQuery<
      GetTasksForUserQuery,
      GetTasksForUserQueryVariables
    >(GetTasksForUserDocument, baseOptions);
  }
  export function useGetTasksForUserLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetTasksForUserQuery,
      GetTasksForUserQueryVariables
    >
  ) {
    return ApolloReactHooks.useLazyQuery<
      GetTasksForUserQuery,
      GetTasksForUserQueryVariables
    >(GetTasksForUserDocument, baseOptions);
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
    return ApolloReactHooks.useQuery<
      GetJobsByProcessInstanceIdQuery,
      GetJobsByProcessInstanceIdQueryVariables
    >(GetJobsByProcessInstanceIdDocument, baseOptions);
  }
  export function useGetJobsByProcessInstanceIdLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetJobsByProcessInstanceIdQuery,
      GetJobsByProcessInstanceIdQueryVariables
    >
  ) {
    return ApolloReactHooks.useLazyQuery<
      GetJobsByProcessInstanceIdQuery,
      GetJobsByProcessInstanceIdQueryVariables
    >(GetJobsByProcessInstanceIdDocument, baseOptions);
  }
  export type GetJobsByProcessInstanceIdQueryHookResult = ReturnType<
    typeof useGetJobsByProcessInstanceIdQuery
  >;
  export type GetJobsByProcessInstanceIdLazyQueryHookResult = ReturnType<
    typeof useGetJobsByProcessInstanceIdLazyQuery
  >;
  export type GetJobsByProcessInstanceIdQueryResult =
    ApolloReactCommon.QueryResult<
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
    return ApolloReactHooks.useQuery<
      GetJobsWithFiltersQuery,
      GetJobsWithFiltersQueryVariables
    >(GetJobsWithFiltersDocument, baseOptions);
  }
  export function useGetJobsWithFiltersLazyQuery(
    baseOptions?: ApolloReactHooks.LazyQueryHookOptions<
      GetJobsWithFiltersQuery,
      GetJobsWithFiltersQueryVariables
    >
  ) {
    return ApolloReactHooks.useLazyQuery<
      GetJobsWithFiltersQuery,
      GetJobsWithFiltersQueryVariables
    >(GetJobsWithFiltersDocument, baseOptions);
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
}
