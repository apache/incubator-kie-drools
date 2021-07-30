const { gql } = require('apollo-server-express');
module.exports = typeDefs = gql`
  scalar DateTime

  schema {
    query: Query
    subscription: Subscription
  }

  type Query {
    ProcessInstances(
      where: ProcessInstanceArgument
      orderBy: ProcessInstanceOrderBy
      pagination: Pagination
    ): [ProcessInstance]
    UserTaskInstances(
      where: UserTaskInstanceArgument
      orderBy: UserTaskInstanceOrderBy
      pagination: Pagination
    ): [UserTaskInstance]
    Travels(
      where: TravelsArgument
      orderBy: TravelsOrderBy
      pagination: Pagination
    ): [Travels]
    VisaApplications(
      where: VisaApplicationsArgument
      orderBy: VisaApplicationsOrderBy
      pagination: Pagination
    ): [VisaApplications]
    Jobs(
      where: JobArgument
      orderBy: JobOrderBy
      pagination: Pagination
      ): [Job]
  }

  type ProcessInstance {
    id: String!
    processId: String!
    processName: String
    parentProcessInstanceId: String
    parentProcessInstance: ProcessInstance
    rootProcessInstanceId: String
    rootProcessId: String
    roles: [String!]
    state: ProcessInstanceState!
    serviceUrl: String
    endpoint: String!
    nodes: [NodeInstance!]!
    milestones: [Milestones!]
    variables: String
    start: DateTime!
    end: DateTime
    businessKey: String
    childProcessInstances: [ProcessInstance!]
    error: ProcessInstanceError
    addons: [String!]
    lastUpdate: DateTime!
  }

  type KogitoMetadata {
    lastUpdate: DateTime!
    processInstances: [ProcessInstanceMeta]
    userTasks: [UserTaskInstanceMeta]
  }

  input KogitoMetadataOrderBy {
    lastUpdate: OrderBy
  }

  input KogitoMetadataArgument {
    lastUpdate: DateArgument
    processInstances: ProcessInstanceMetaArgument
    userTasks: UserTaskInstanceMetaArgument
  }

  type ProcessInstanceMeta {
    id: String!
    processId: String!
    processName: String
    parentProcessInstanceId: String
    rootProcessInstanceId: String
    rootProcessId: String
    roles: [String!]
    state: ProcessInstanceState!
    endpoint: String!
    start: DateTime!
    end: DateTime
    lastUpdate: DateTime!
    businessKey: String
    serviceUrl: String
  }

  type ProcessInstanceError {
    nodeDefinitionId: String!
    message: String
  }

  enum ProcessInstanceState {
    PENDING
    ACTIVE
    COMPLETED
    ABORTED
    SUSPENDED
    ERROR
  }

  type NodeInstance {
    id: String!
    name: String!
    type: String!
    enter: DateTime!
    exit: DateTime
    definitionId: String!
    nodeId: String!
  }

  type Milestones {
    id: String!
    name: String!
    status: MilestoneStatus!
  }
  enum MilestoneStatus {
    ACTIVE
    AVAILABLE
    COMPLETED
  }
  
  input ProcessInstanceOrderBy {
    processId: OrderBy
    processName: OrderBy
    rootProcessId: OrderBy
    state: OrderBy
    start: OrderBy
    end: OrderBy
    error: ProcessInstanceErrorOrderBy
    lastUpdate: OrderBy
  }

  input ProcessInstanceErrorOrderBy {
    nodeDefinitionId: OrderBy
    message: OrderBy
  }

  input ProcessInstanceArgument {
    and: [ProcessInstanceArgument!]
    or: [ProcessInstanceArgument!]
    id: IdArgument
    processId: StringArgument
    processName: StringArgument
    parentProcessInstanceId: IdArgument
    rootProcessInstanceId: IdArgument
    rootProcessId: StringArgument
    state: ProcessInstanceStateArgument
    error: ProcessInstanceErrorArgument
    nodes: NodeInstanceArgument
    endpoint: StringArgument
    roles: StringArrayArgument
    start: DateArgument
    end: DateArgument
    addons: StringArrayArgument
    lastUpdate: DateArgument
    businessKey: StringArgument
  }

  input ProcessInstanceErrorArgument {
    nodeDefinitionId: StringArgument
    message: StringArgument
  }

  input ProcessInstanceMetaArgument {
    id: IdArgument
    processId: StringArgument
    processName: StringArgument
    parentProcessInstanceId: IdArgument
    rootProcessInstanceId: IdArgument
    rootProcessId: StringArgument
    state: ProcessInstanceStateArgument
    endpoint: StringArgument
    roles: StringArrayArgument
    start: DateArgument
    end: DateArgument
  }

  input NodeInstanceArgument {
    id: IdArgument
    name: StringArgument
    definitionId: StringArgument
    nodeId: StringArgument
    type: StringArgument
    enter: DateArgument
    exit: DateArgument
  }

  input StringArrayArgument {
    contains: String
    containsAll: [String!]
    containsAny: [String!]
    isNull: Boolean
  }

  input IdArgument {
    in: [String!]
    equal: String
    isNull: Boolean
  }

  input StringArgument {
    in: [String!]
    like: String
    isNull: Boolean
    equal: String
  }

  input BooleanArgument {
    isNull: Boolean
    equal: Boolean
  }

  input NumericArgument {
    in: [Int!]
    isNull: Boolean
    equal: Int
    greaterThan: Int
    greaterThanEqual: Int
    lessThan: Int
    lessThanEqual: Int
    between: NumericRange
  }

  input NumericRange {
    from: Int!
    to: Int!
  }

  input DateArgument {
    isNull: Boolean
    equal: DateTime
    greaterThan: DateTime
    greaterThanEqual: DateTime
    lessThan: DateTime
    lessThanEqual: DateTime
    between: DateRange
  }

  input DateRange {
    from: DateTime!
    to: DateTime!
  }

  input ProcessInstanceStateArgument {
    equal: ProcessInstanceState
    in: [ProcessInstanceState]
  }

  type UserTaskInstance {
    id: String!
    description: String
    name: String
    priority: String
    processInstanceId: String!
    processId: String!
    rootProcessInstanceId: String
    rootProcessId: String
    state: String!
    actualOwner: String
    adminGroups: [String!]
    adminUsers: [String!]
    completed: DateTime
    started: DateTime!
    excludedUsers: [String!]
    potentialGroups: [String!]
    potentialUsers: [String!]
    inputs: String
    outputs: String
    referenceName: String
    lastUpdate: DateTime!
    endpoint: String
  }

  type UserTaskInstanceMeta {
    id: String!
    description: String
    name: String
    priority: String
    processInstanceId: String!
    state: String!
    actualOwner: String
    adminGroups: [String!]
    adminUsers: [String!]
    completed: DateTime
    started: DateTime!
    excludedUsers: [String!]
    potentialGroups: [String!]
    potentialUsers: [String!]
    referenceName: String
    lastUpdate: DateTime!
  }

  input UserTaskInstanceArgument {
    and: [UserTaskInstanceArgument!]
    or: [UserTaskInstanceArgument!]
    not: UserTaskInstanceArgument
    state: StringArgument
    id: IdArgument
    description: StringArgument
    name: StringArgument
    priority: StringArgument
    processInstanceId: IdArgument
    actualOwner: StringArgument
    potentialUsers: StringArrayArgument
    potentialGroups: StringArrayArgument
    excludedUsers: StringArrayArgument
    adminGroups: StringArrayArgument
    adminUsers: StringArrayArgument
    completed: DateArgument
    started: DateArgument
    referenceName: StringArgument
    lastUpdate: DateArgument
  }

  input UserTaskInstanceMetaArgument {
    state: StringArgument
    id: IdArgument
    description: StringArgument
    name: StringArgument
    priority: StringArgument
    processInstanceId: IdArgument
    actualOwner: StringArgument
    potentialUsers: StringArrayArgument
    potentialGroups: StringArrayArgument
    excludedUsers: StringArrayArgument
    adminGroups: StringArrayArgument
    adminUsers: StringArrayArgument
    completed: DateArgument
    started: DateArgument
    referenceName: StringArgument
  }

  input UserTaskInstanceOrderBy {
    state: OrderBy
    actualOwner: OrderBy
    description: OrderBy
    name: OrderBy
    priority: OrderBy
    completed: OrderBy
    started: OrderBy
    referenceName: OrderBy
    lastUpdate: OrderBy
  }

  type Subscription {
    ProcessInstanceAdded: ProcessInstance!
    ProcessInstanceUpdated: ProcessInstance!
    UserTaskInstanceAdded: UserTaskInstance!
    UserTaskInstanceUpdated: UserTaskInstance!
  }

  enum OrderBy {
    ASC
    DESC
  }

  input Pagination {
    limit: Int
    offset: Int
  }

  type Travels {
    flight: Flight
    hotel: Hotel
    id: String
    traveller: Traveller
    trip: Trip
    visaApplication: VisaApplication
    metadata: KogitoMetadata
  }

  type Flight {
    arrival: String
    departure: String
    flightNumber: String
    gate: String
    seat: String
  }

  type Hotel {
    address: Address
    bookingNumber: String
    name: String
    phone: String
    room: String
  }

  type Address {
    city: String
    country: String
    street: String
    zipCode: String
  }

  type Traveller {
    address: Address
    email: String
    firstName: String
    lastName: String
    nationality: String
  }

  type Trip {
    begin: String
    city: String
    country: String
    end: String
    visaRequired: Boolean
  }

  type VisaApplication {
    approved: Boolean
    city: String
    country: String
    duration: Int
    firstName: String
    lastName: String
    nationality: String
    passportNumber: String
  }

  input TravelsArgument {
    and: [TravelsArgument!]
    or: [TravelsArgument!]
    flight: FlightArgument
    hotel: HotelArgument
    id: IdArgument
    traveller: TravellerArgument
    trip: TripArgument
    visaApplication: VisaApplicationArgument
    metadata: KogitoMetadataArgument
  }

  input FlightArgument {
    arrival: StringArgument
    departure: StringArgument
    flightNumber: StringArgument
    gate: StringArgument
    seat: StringArgument
  }

  input HotelArgument {
    address: AddressArgument
    bookingNumber: StringArgument
    name: StringArgument
    phone: StringArgument
    room: StringArgument
  }

  input AddressArgument {
    city: StringArgument
    country: StringArgument
    street: StringArgument
    zipCode: StringArgument
  }

  input TravellerArgument {
    address: AddressArgument
    email: StringArgument
    firstName: StringArgument
    lastName: StringArgument
    nationality: StringArgument
  }

  input TripArgument {
    begin: StringArgument
    city: StringArgument
    country: StringArgument
    end: StringArgument
    visaRequired: BooleanArgument
  }

  input VisaApplicationArgument {
    approved: BooleanArgument
    city: StringArgument
    country: StringArgument
    duration: NumericArgument
    firstName: StringArgument
    lastName: StringArgument
    nationality: StringArgument
    passportNumber: StringArgument
  }

  input TravelsOrderBy {
    flight: FlightOrderBy
    hotel: HotelOrderBy
    traveller: TravellerOrderBy
    trip: TripOrderBy
    visaApplication: VisaApplicationOrderBy
    metadata: KogitoMetadataOrderBy
  }

  input FlightOrderBy {
    arrival: OrderBy
    departure: OrderBy
    flightNumber: OrderBy
    gate: OrderBy
    seat: OrderBy
  }

  input HotelOrderBy {
    address: AddressOrderBy
    bookingNumber: OrderBy
    name: OrderBy
    phone: OrderBy
    room: OrderBy
  }

  input AddressOrderBy {
    city: OrderBy
    country: OrderBy
    street: OrderBy
    zipCode: OrderBy
  }

  input TravellerOrderBy {
    address: AddressOrderBy
    email: OrderBy
    firstName: OrderBy
    lastName: OrderBy
    nationality: OrderBy
  }

  input TripOrderBy {
    begin: OrderBy
    city: OrderBy
    country: OrderBy
    end: OrderBy
    visaRequired: OrderBy
  }

  input VisaApplicationOrderBy {
    approved: OrderBy
    city: OrderBy
    country: OrderBy
    duration: OrderBy
    firstName: OrderBy
    lastName: OrderBy
    nationality: OrderBy
    passportNumber: OrderBy
  }

  type VisaApplications {
    id: String
    visaApplication: VisaApplication
    metadata: KogitoMetadata
  }

  input VisaApplicationsArgument {
    and: [VisaApplicationsArgument!]
    or: [VisaApplicationsArgument!]
    id: IdArgument
    visaApplication: VisaApplicationArgument
    metadata: KogitoMetadataArgument
  }

  input VisaApplicationsOrderBy {
    visaApplication: VisaApplicationOrderBy
    metadata: KogitoMetadataOrderBy
  }

  input JobArgument {
    and: [JobArgument!]
    or: [JobArgument!]
    id: IdArgument
    processId: StringArgument
    processInstanceId: IdArgument
    rootProcessInstanceId: IdArgument
    rootProcessId: StringArgument
    status: JobStatusArgument
    expirationTime: DateArgument
    priority: NumericArgument
    scheduledId: IdArgument
    lastUpdate: DateArgument
    endpoint: StringArgument
    nodeInstanceId: StringArgument
}

input JobOrderBy {
    processId: OrderBy
    rootProcessId: OrderBy
    status: OrderBy
    expirationTime: OrderBy
    priority: OrderBy
    retries: OrderBy
    lastUpdate: OrderBy
    executionCounter: OrderBy
}

input JobStatusArgument {
  equal: JobStatus
  in: [JobStatus]
}

type Job {
  id: String!
  processId: String
  processInstanceId: String
  rootProcessInstanceId: String
  rootProcessId: String
  status: JobStatus!
  expirationTime: DateTime
  priority: Int
  callbackEndpoint: String
  repeatInterval: Int
  repeatLimit: Int
  scheduledId: String
  retries: Int
  lastUpdate: DateTime
  executionCounter: Int
  endpoint: String
  nodeInstanceId: String
}

enum JobStatus {
  ERROR
  EXECUTED
  SCHEDULED
  RETRY
  CANCELED
}
`;
