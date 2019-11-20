const {gql} = require("apollo-server-express");
module.exports = typeDefs = gql`
    scalar DateTime

    schema {
        query: Query
        subscription: Subscription
    }
    type ProcessIntanceError { nodeDefinitionId: String! message: String }
    type ProcessInstance {
        id: String!
        processId: String!
        processName: String
        parentProcessInstanceId: String
        rootProcessInstanceId: String
        rootProcessId: String
        roles: [String!]
        state: ProcessInstanceState!
        managementEnabled: Boolean
        endpoint: String!
        nodes: [NodeInstance!]!
        error: ProcessIntanceError!
        variables: String
        start: DateTime!
        end: DateTime
        childProcessInstanceId: [String!]
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
        managementEnabled: Boolean
        endpoint: String!
        start: DateTime!
        end: DateTime
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

    type Query {
        ProcessInstances(filter: ProcessInstanceFilter): [ProcessInstance]
        UserTaskInstances(filter: UserTaskInstanceFilter): [UserTaskInstance]
        ProcessId(filter: ProcessDetailsFilter): [ProcessInstance]
    }

    input ProcessInstanceFilter {
        state: [ProcessInstanceState!]
        id: [String!]
        parentProcessInstanceId: [String]
        rootProcessInstanceId: [String]
        processId: [String!]
        limit: Int
        offset: Int
        managementEnabled: Boolean
        erro: String
        nodes: [String]
    }

    input ProcessDetailsFilter {
        state: [ProcessInstanceState!]
        id: String
        parentProcessInstanceId: [String!]
        rootProcessInstanceId: [String!]
        processId: [String!]
        limit: Int
        offset: Int
        nodes: [String]
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
    }

    input UserTaskInstanceFilter {
        state: [String!]
        id: [String!]
        processInstanceId: [String!]
        actualOwner: [String!]
        potentialUsers: [String!]
        potentialGroups: [String!]
        limit: Int
        offset: Int
    }

    type Subscription {
        ProcessInstanceAdded: ProcessInstance!
        ProcessInstanceUpdated: ProcessInstance!
        UserTaskInstanceAdded: UserTaskInstance!
        UserTaskInstanceUpdated: UserTaskInstance!
    }
`;
