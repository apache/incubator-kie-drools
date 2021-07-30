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

import { GraphQL } from '@kogito-apps/consoles-common';
import {
  BulkProcessInstanceActionResponse,
  NodeInstance,
  OperationType,
  ProcessInstance,
  TriggerableNode
} from '@kogito-apps/management-console-shared';
import axios from 'axios';

//Rest Api to Cancel multiple Jobs
export const performMultipleCancel = async (
  jobsToBeActioned: (GraphQL.Job & { errorMessage?: string })[]
) => {
  const successJobs = [];
  const failedJobs = [];
  for (const job of jobsToBeActioned) {
    try {
      await axios.delete(`${job.endpoint}/${job.id}`);
      successJobs.push(job);
    } catch (error) {
      job.errorMessage = JSON.stringify(error.message);
      failedJobs.push(job);
    }
  }
  return { successJobs, failedJobs };
};

//Rest Api to Cancel a Job
export const jobCancel = async (
  job: Pick<GraphQL.Job, 'id' | 'endpoint'>
): Promise<{ modalTitle: string; modalContent: string }> => {
  let modalTitle: string;
  let modalContent: string;
  try {
    await axios.delete(`${job.endpoint}/${job.id}`);
    modalTitle = 'success';
    modalContent = `The job: ${job.id} is canceled successfully`;
    return { modalTitle, modalContent };
  } catch (error) {
    modalTitle = 'failure';
    modalContent = `The job: ${job.id} failed to cancel. Error message: ${error.message}`;
    return { modalTitle, modalContent };
  }
};

// Rest Api to Reschedule a Job
export const handleJobReschedule = async (
  job,
  repeatInterval: number | string,
  repeatLimit: number | string,
  scheduleDate: Date
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
  try {
    await axios.patch(`${job.endpoint}/${job.id}`, parameter);
    modalTitle = 'success';
    modalContent = `Reschedule of job: ${job.id} is successful`;
    return { modalTitle, modalContent };
  } catch (error) {
    modalTitle = 'failure';
    modalContent = `Reschedule of job ${job.id} failed. Message: ${error.message}`;
    return { modalTitle, modalContent };
  }
};

// Rest Api to fetch Process Diagram
export const getSvg = async (data: ProcessInstance): Promise<any> => {
  return axios
    .get(`/svg/processes/${data.processId}/instances/${data.id}`)
    .then(res => {
      return { svg: res.data };
    })
    .catch(async error => {
      /* istanbul ignore else*/
      if (data.serviceUrl) {
        return axios
          .get(
            `${data.serviceUrl}/svg/processes/${data.processId}/instances/${data.id}`
          )
          .then(res => {
            return { svg: res.data };
          })
          .catch(err => {
            /* istanbul ignore else*/
            if (err.response && err.response.status !== 404) {
              return { error: err.message };
            }
          });
      }
    });
};

// Rest Api to skip a process in error state
export const handleProcessSkip = async (
  processInstance: ProcessInstance
): Promise<void> => {
  return new Promise((resolve, reject) => {
    axios
      .post(
        `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/skip`
      )
      .then(() => {
        resolve();
      })
      .catch(error => reject(error));
  });
};

// Rest Api to retrigger a process in error state
export const handleProcessRetry = async (
  processInstance: ProcessInstance
): Promise<void> => {
  return new Promise((resolve, reject) => {
    axios
      .post(
        `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/retrigger`
      )
      .then(() => {
        resolve();
      })
      .catch(error => reject(error));
  });
};

// Rest Api to abort a process
export const handleProcessAbort = (
  processInstance: ProcessInstance
): Promise<void> => {
  return new Promise((resolve, reject) => {
    axios
      .delete(
        `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}`
      )
      .then(() => {
        resolve();
      })
      .catch(error => reject(error));
  });
};

// function to handle multiple actions(abort, skip and retry) on processes
export const handleProcessMultipleAction = async (
  processInstances: ProcessInstance[],
  operationType: OperationType
): Promise<BulkProcessInstanceActionResponse> => {
  // eslint-disable-next-line no-async-promise-executor
  return new Promise(async (resolve, reject) => {
    let operation: (processInstance: ProcessInstance) => Promise<void>;
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
      await operation(processInstance)
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
export const getTriggerableNodes = async (
  processInstance: ProcessInstance
): Promise<TriggerableNode[]> => {
  return new Promise((resolve, reject) => {
    axios
      .get(
        `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/nodes`
      )
      .then(result => {
        resolve(result.data);
      })
      .catch(error => {
        reject(error);
      });
  });
};

export const handleNodeTrigger = async (
  processInstance: ProcessInstance,
  node: TriggerableNode
): Promise<void> => {
  return new Promise((resolve, reject) => {
    axios
      .post(
        `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/nodes/${node.nodeDefinitionId}`
      )
      .then(() => {
        resolve();
      })
      .catch(error => {
        reject(error);
      });
  });
};

// function containing Api call to update process variables
export const handleProcessVariableUpdate = (
  processInstance: ProcessInstance,
  updatedJson: Record<string, unknown>
): Promise<Record<string, unknown>> => {
  return new Promise((resolve, reject) => {
    axios
      .put(`${processInstance.endpoint}/${processInstance.id}`, updatedJson)
      .then(response => {
        resolve(response.data);
      })
      .catch(error => {
        reject(error.message);
      });
  });
};

export const handleNodeInstanceCancel = async (
  processInstance: ProcessInstance,
  node: NodeInstance
): Promise<void> => {
  return new Promise((resolve, reject) => {
    axios
      .delete(
        `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/nodeInstances/${node.id}`
      )
      .then(() => {
        resolve();
      })
      .catch(error => {
        reject(error);
      });
  });
};

export const handleNodeInstanceRetrigger = (
  processInstance: Pick<ProcessInstance, 'id' | 'serviceUrl' | 'processId'>,
  node: Pick<NodeInstance, 'id'>
): Promise<void> => {
  return new Promise((resolve, reject) => {
    axios
      .post(
        `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/nodeInstances/${node.id}`
      )
      .then(() => {
        resolve();
      })
      .catch(error => {
        reject(JSON.stringify(error.message));
      });
  });
};
