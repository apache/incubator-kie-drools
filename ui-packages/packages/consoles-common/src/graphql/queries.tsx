/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import gql from 'graphql-tag';

const GET_PROCESS_INSTANCES = gql`
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

const GET_CHILD_INSTANCES = gql`
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

const GET_PROCESS_INSTANCE = gql`
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

const GET_COLUMN_PICKER_ATTRIBUTES = gql`
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

const GET_QUERY_TYPES = gql`
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

const GET_QUERY_FIELDS = gql`
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

const GET_INPUT_FIELDS_FROM_QUERY = gql`
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

const GET_INPUT_FIELDS_FROM_TYPES = gql`
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

const GET_USER_TASKS_BY_STATES = gql`
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

const GET_USER_TASK = gql`
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

const GET_TASKS_FOR_USER = gql`
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

const GET_JOBS_BY_PROC_INST_ID = gql`
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

const GET_JOBS_WITH_FILTERS = gql`
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

const ABORT_PROCESS_INSTANCE = gql`
  mutation abortProcessInstance($processId: String) {
    ProcessInstanceAbort(id: $processId)
  }
`;

const SKIP_PROCESS_INSTANCE = gql`
  mutation skipProcessInstance($processId: String) {
    ProcessInstanceSkip(id: $processId)
  }
`;

const RETRY_PROCESS_INSTANCE = gql`
  mutation retryProcessInstance($processId: String) {
    ProcessInstanceRetry(id: $processId)
  }
`;
const GET_PROCESS_INSTANCE_SVG = gql`
  query getProcessInstanceSVG($processId: String) {
    ProcessInstances(where: { id: { equal: $processId } }) {
      diagram
    }
  }
`;

const GET_PROCESS_INSTANCE_NODES = gql`
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

const TRIGGER_PROCESS_NODE_INSTANCE = gql`
  mutation handleNodeTrigger($processId: String, $nodeId: String) {
    NodeInstanceTrigger(id: $processId, nodeId: $nodeId)
  }
`;

const CANCEL_PROCESS_NODE_INSTANCE = gql`
  mutation handleNodeInstanceCancel(
    $processId: String
    $nodeInstanceId: String
  ) {
    NodeInstanceCancel(id: $processId, nodeInstanceId: $nodeInstanceId)
  }
`;

const RETRIGGER_PROCESS_NODE_INSTANCE = gql`
  mutation handleNodeInstanceRetrigger(
    $processId: String
    $nodeInstanceId: String
  ) {
    NodeInstanceRetrigger(id: $processId, nodeInstanceId: $nodeInstanceId)
  }
`;

const UPDATE_PROCESS_VARBALES = gql`
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
const CANCEL_JOB = gql`
  mutation jobCancel($jobId: String) {
    JobCancel(id: $jobId)
  }
`;

const RESCHEDULE_JOB = gql`
  mutation handleJobReschedule($jobId: String, $data: String) {
    JobReschedule(id: $jobId, data: $data)
  }
`;
