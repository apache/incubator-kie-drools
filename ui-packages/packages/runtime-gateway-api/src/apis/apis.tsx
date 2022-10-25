/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { GraphQL } from '@kogito-apps/consoles-common';
import {
  BulkProcessInstanceActionResponse,
  JobCancel,
  NodeInstance,
  OperationType,
  ProcessInstance,
  TriggerableNode
} from '@kogito-apps/management-console-shared';
import { ApolloClient } from 'apollo-client';

//Rest Api to Cancel multiple Jobs
export const performMultipleCancel = async (
  jobsToBeActioned: (GraphQL.Job & { errorMessage?: string })[],
  client: ApolloClient<any>
): Promise<any> => {
  const multipleCancel: Promise<any>[] = [];
  for (const job of jobsToBeActioned) {
    multipleCancel.push(
      new Promise((resolve, reject) => {
        client
          .mutate({
            mutation: GraphQL.JobCancelDocument,
            variables: {
              jobId: job.id
            },
            fetchPolicy: 'no-cache'
          })
          .then(value => {
            resolve({ successJob: job });
          })
          .catch(reason => {
            job.errorMessage = JSON.stringify(reason.message);
            reject({ failedJob: job });
          });
      })
    );
  }
  return Promise.all(multipleCancel.map(mc => mc.catch(error => error))).then(
    result => {
      return Promise.resolve(result);
    }
  );
};

//Rest Api to Cancel a Job
export const jobCancel = async (
  job: Pick<GraphQL.Job, 'id' | 'endpoint'>,
  client: ApolloClient<any>
): Promise<JobCancel> => {
  let modalTitle: string;
  let modalContent: string;
  return new Promise<JobCancel>((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.JobCancelDocument,
        variables: {
          jobId: job.id
        },
        fetchPolicy: 'no-cache'
      })
      .then(value => {
        modalTitle = 'success';
        modalContent = `The job: ${job.id} is canceled successfully`;
        resolve({ modalTitle, modalContent });
      })
      .catch(reason => {
        modalTitle = 'failure';
        modalContent = `The job: ${job.id} failed to cancel. Error message: ${reason.message}`;
        reject({ modalTitle, modalContent });
      });
  });
};

// Rest Api to Reschedule a Job
export const handleJobReschedule = async (
  job,
  repeatInterval: number | string,
  repeatLimit: number | string,
  scheduleDate: Date,
  client: ApolloClient<any>
): Promise<{ modalTitle: string; modalContent: string }> => {
  let modalTitle: string;
  let modalContent: string;
  let parameter = {};
  if (repeatInterval === null && repeatLimit === null) {
    parameter = {
      expirationTime: new Date(scheduleDate)
    };
  } else {
    parameter = {
      expirationTime: new Date(scheduleDate),
      repeatInterval,
      repeatLimit
    };
  }

  return new Promise<JobCancel>((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.HandleJobRescheduleDocument,
        variables: {
          jobId: job.id,
          data: JSON.stringify(parameter)
        },
        fetchPolicy: 'no-cache'
      })
      .then(value => {
        modalTitle = 'success';
        modalContent = `Reschedule of job: ${job.id} is successful`;
        resolve({ modalTitle, modalContent });
      })
      .catch(reason => {
        modalTitle = 'failure';
        modalContent = `Reschedule of job ${job.id} failed. Message: ${reason.message}`;
        reject({ modalTitle, modalContent });
      });
  });
};

// Rest Api to skip a process in error state
export const handleProcessSkip = async (
  processInstance: ProcessInstance,
  client: ApolloClient<any>
): Promise<void> => {
  return new Promise<void>((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.AbortProcessInstanceDocument,
        variables: {
          processId: processInstance.id
        },
        fetchPolicy: 'no-cache'
      })
      .then(value => {
        resolve(value.data);
      })
      .catch(reason => reject(reason));
  });
};

// Rest Api to retrigger a process in error state
export const handleProcessRetry = async (
  processInstance: ProcessInstance,
  client: ApolloClient<any>
): Promise<void> => {
  return new Promise<void>((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.RetryProcessInstanceDocument,
        variables: {
          processId: processInstance.id
        },
        fetchPolicy: 'no-cache'
      })
      .then(value => {
        resolve(value.data);
      })
      .catch(reason => reject(reason));
  });
};

// Rest Api to abort a process
export const handleProcessAbort = async (
  processInstance: ProcessInstance,
  client: ApolloClient<any>
): Promise<void> => {
  return new Promise<void>((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.AbortProcessInstanceDocument,
        variables: {
          processId: processInstance.id
        },
        fetchPolicy: 'no-cache'
      })
      .then(value => {
        resolve(value.data);
      })
      .catch(reason => reject(reason));
  });
};

// function to handle multiple actions(abort, skip and retry) on processes
export const handleProcessMultipleAction = async (
  processInstances: ProcessInstance[],
  operationType: OperationType,
  client: ApolloClient<any>
): Promise<BulkProcessInstanceActionResponse> => {
  // eslint-disable-next-line no-async-promise-executor
  return new Promise(async (resolve, reject) => {
    let operation: (
      processInstance: ProcessInstance,
      client: ApolloClient<any>
    ) => Promise<void>;
    const successProcessInstances: ProcessInstance[] = [];
    const failedProcessInstances: ProcessInstance[] = [];
    switch (operationType) {
      case OperationType.ABORT:
        operation = handleProcessAbort;
        break;
      case OperationType.SKIP:
        operation = handleProcessSkip;
        break;
      case OperationType.RETRY:
        operation = handleProcessRetry;
        break;
    }
    for (const processInstance of processInstances) {
      await operation(processInstance, client)
        .then(() => {
          successProcessInstances.push(processInstance);
        })
        .catch(error => {
          processInstance.errorMessage = error.message;
          failedProcessInstances.push(processInstance);
        });
    }

    resolve({ successProcessInstances, failedProcessInstances });
  });
};

export const handleNodeTrigger = async (
  processInstance: ProcessInstance,
  node: TriggerableNode,
  client: ApolloClient<any>
): Promise<void> => {
  return new Promise((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.HandleNodeTriggerDocument,
        variables: {
          processId: processInstance.id,
          nodeId: node.nodeDefinitionId
        },
        fetchPolicy: 'no-cache'
      })
      .then(value => {
        resolve(value.data);
      })
      .catch(reason => reject(reason));
  });
};

// function containing Api call to update process variables
export const handleProcessVariableUpdate = async (
  processInstance: ProcessInstance,
  updatedJson: Record<string, unknown>,
  client: ApolloClient<any>
): Promise<Record<string, unknown>> => {
  return new Promise((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.HandleProcessVariableUpdateDocument,
        variables: {
          processId: processInstance.id,
          processInstanceVariables: JSON.stringify(updatedJson)
        },
        fetchPolicy: 'no-cache'
      })
      .then(value => {
        resolve(JSON.parse(value.data.ProcessInstanceUpdateVariables));
      })
      .catch(reason => reject(reason));
  });
};

export const handleNodeInstanceCancel = async (
  processInstance: ProcessInstance,
  node: NodeInstance,
  client: ApolloClient<any>
): Promise<void> => {
  return new Promise((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.HandleNodeInstanceCancelDocument,
        variables: {
          processId: processInstance.id,
          nodeInstanceId: node.id
        },
        fetchPolicy: 'no-cache'
      })
      .then(value => {
        resolve();
      })
      .catch(reason => reject(JSON.stringify(reason.message)));
  });
};

export const handleNodeInstanceRetrigger = async (
  processInstance: Pick<ProcessInstance, 'id' | 'serviceUrl' | 'processId'>,
  node: Pick<NodeInstance, 'id'>,
  client: ApolloClient<any>
): Promise<void> => {
  return new Promise((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.HandleNodeInstanceRetriggerDocument,
        variables: {
          processId: processInstance.id,
          nodeInstanceId: node.id
        },
        fetchPolicy: 'no-cache'
      })
      .then(value => {
        resolve();
      })
      .catch(reason => reject(JSON.stringify(reason.message)));
  });
};

export const getSVG = async (
  processInstance: ProcessInstance,
  client: ApolloClient<any>
): Promise<any> => {
  return client
    .query({
      query: GraphQL.GetProcessInstanceSvgDocument,
      variables: {
        processId: processInstance.id
      },
      fetchPolicy: 'network-only'
    })
    .then(value => {
      return { svg: value.data.ProcessInstances[0].diagram };
    })
    .catch(reason => {
      return { error: reason.message };
    });
};

export const getProcessDetails = async (
  id: string,
  client: ApolloClient<any>
): Promise<any> => {
  return new Promise((resolve, reject) => {
    client
      .query({
        query: GraphQL.GetProcessInstanceByIdDocument,
        variables: {
          id
        },
        fetchPolicy: 'network-only'
      })
      .then(value => {
        resolve(value.data.ProcessInstances[0]);
      })
      .catch(error => {
        reject(error['graphQLErrors'][0]['message'])
      });
  });
};

export const getJobs = async (
  id: string,
  client: ApolloClient<any>
): Promise<any> => {
  return client
    .query({
      query: GraphQL.GetJobsByProcessInstanceIdDocument,
      variables: {
        processInstanceId: id
      },
      fetchPolicy: 'network-only'
    })
    .then(value => {
      return value.data.Jobs;
    })
    .catch(error => {
      return error;
    });
};

export const getTriggerableNodes = async (
  processInstance: ProcessInstance,
  client: ApolloClient<any>
): Promise<any> => {
  return client
    .query({
      query: GraphQL.GetProcessInstanceNodeDefinitionsDocument,
      variables: {
        processId: processInstance.id
      },
      fetchPolicy: 'no-cache'
    })
    .then(value => {
      return value.data.ProcessInstances[0].nodeDefinitions;
    })
    .catch(reason => {
      return reason;
    });
};
