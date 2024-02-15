/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { OpenAPI } from 'openapi-types';
import { GraphQL } from '@kogito-apps/consoles-common/dist/graphql';
import {
  BulkProcessInstanceActionResponse,
  NodeInstance,
  ProcessInstance,
  TriggerableNode
} from '@kogito-apps/management-console-shared/dist/types';
import { OperationType } from '@kogito-apps/management-console-shared/dist/components/BulkList';
import axios from 'axios';
import uuidv4 from 'uuid';
import {
  Form,
  FormContent,
  FormInfo
} from '@kogito-apps/components-common/dist/types';
import SwaggerParser from '@apidevtools/swagger-parser';
import { createProcessDefinitionList } from '../../utils/Utils';
import { ProcessDefinition } from '@kogito-apps/process-definition-list';
import { CustomDashboardInfo } from '@kogito-apps/custom-dashboard-list';
import {
  CloudEventRequest,
  KOGITO_BUSINESS_KEY
} from '@kogito-apps/cloud-event-form/dist';

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
    .then((res) => {
      return { svg: res.data };
    })
    .catch(async (error) => {
      /* istanbul ignore else*/
      if (data.serviceUrl) {
        return axios
          .get(
            `${data.serviceUrl}/svg/processes/${data.processId}/instances/${data.id}`
          )
          .then((res) => {
            return { svg: res.data };
          })
          .catch((err) => {
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
      .catch((error) => reject(error));
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
      .catch((error) => {
        reject(error);
      });
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
      .catch((error) => reject(error));
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
        .catch((error) => {
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
      .then((result) => {
        resolve(result.data);
      })
      .catch((error) => {
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
      .catch((error) => {
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
      .then((response) => {
        resolve(response.data);
      })
      .catch((error) => {
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
      .catch((error) => {
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
      .catch((error) => {
        reject(JSON.stringify(error.message));
      });
  });
};

export const getForms = (formFilter: string[]): Promise<FormInfo[]> => {
  return new Promise((resolve, reject) => {
    axios
      .get('/forms/list', {
        params: {
          names: formFilter.join(';')
        }
      })
      .then((result) => {
        resolve(result.data);
      })
      .catch((error) => reject(error));
  });
};

export const getFormContent = (formName: string): Promise<Form> => {
  return new Promise((resolve, reject) => {
    axios
      .get(`/forms/${formName}`)
      .then((result) => {
        resolve(result.data);
      })
      .catch((error) => reject(error));
  });
};

export const saveFormContent = (
  formName: string,
  content: FormContent
): Promise<void> => {
  return new Promise((resolve, reject) => {
    axios
      .post(`/forms/${formName}`, content)
      .then((result) => {
        resolve();
      })
      .catch((error) => reject(error));
  });
};

export const getProcessDefinitionList = (
  devUIUrl: string,
  openApiPath: string
): Promise<ProcessDefinition[]> => {
  return new Promise((resolve, reject) => {
    SwaggerParser.parse(`${devUIUrl}/${openApiPath}`)
      .then((response) => {
        const processDefinitionObjs = [];
        const paths = response.paths;
        const regexPattern = /^\/[^\n/]+\/schema/;
        Object.getOwnPropertyNames(paths)
          .filter((path) => regexPattern.test(path.toString()))
          .forEach((url) => {
            let processArray = url.split('/');
            processArray = processArray.filter((name) => name.length !== 0);
            /* istanbul ignore else*/
            if (
              Object.prototype.hasOwnProperty.call(
                paths[`/${processArray[0]}`],
                'post'
              )
            ) {
              processDefinitionObjs.push({ [url]: paths[url] });
            }
          });
        resolve(createProcessDefinitionList(processDefinitionObjs, devUIUrl));
      })
      .catch((err) => reject(err));
  });
};

export const getProcessSchema = (
  processDefinitionData: ProcessDefinition
): Promise<Record<string, any>> => {
  return new Promise((resolve, reject) => {
    axios
      .get(`${processDefinitionData.endpoint}/schema`)
      .then((response) => {
        /* istanbul ignore else*/
        if (response.status === 200) {
          resolve(response.data);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
};

export const getCustomForm = (
  processDefinitionData: ProcessDefinition
): Promise<Form> => {
  return new Promise((resolve, reject) => {
    const lastIndex = processDefinitionData.endpoint.lastIndexOf(
      `/${processDefinitionData.processName}`
    );
    const baseEndpoint = processDefinitionData.endpoint.slice(0, lastIndex);
    axios
      .get(`${baseEndpoint}/forms/${processDefinitionData.processName}`)
      .then((response) => {
        /* istanbul ignore else*/
        if (response.status === 200) {
          resolve(response.data);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
};

export const startProcessInstance = (
  formData: any,
  businessKey: string,
  processDefinitionData: ProcessDefinition
): Promise<string> => {
  return new Promise((resolve, reject) => {
    const requestURL = `${processDefinitionData.endpoint}${
      businessKey.length > 0 ? `?businessKey=${businessKey}` : ''
    }`;
    axios
      .post(requestURL, formData, {
        headers: {
          'Content-Type': 'application/json'
        }
      })
      .then((response) => {
        resolve(response.data.id);
      })
      .catch((error) => reject(error));
  });
};

export const triggerStartCloudEvent = (
  event: CloudEventRequest,
  devUIUrl: string
): Promise<string> => {
  if (!event.headers.extensions[KOGITO_BUSINESS_KEY]) {
    event.headers.extensions[KOGITO_BUSINESS_KEY] = String(
      Math.floor(Math.random() * 100000)
    );
  }

  return new Promise((resolve, reject) => {
    doTriggerCloudEvent(event, devUIUrl)
      .then((response) =>
        resolve(event.headers.extensions[KOGITO_BUSINESS_KEY])
      )
      .catch((error) => reject(error));
  });
};

export const triggerCloudEvent = (
  event: CloudEventRequest,
  devUIUrl: string
): Promise<any> => {
  return doTriggerCloudEvent(event, devUIUrl);
};

const doTriggerCloudEvent = (
  event: CloudEventRequest,
  devUIUrl: string
): Promise<any> => {
  const cloudEvent = {
    ...event.headers.extensions,
    specversion: '1.0',
    id: uuidv4(),
    source: event.headers.source ?? '',
    type: event.headers.type,
    data: event.data ? JSON.parse(event.data) : {}
  };

  if (devUIUrl.endsWith('/')) {
    devUIUrl = devUIUrl.slice(0, devUIUrl.length - 1);
  }

  const url = `${devUIUrl}${event.endpoint.startsWith('/') ? '' : '/'}${
    event.endpoint
  }`;

  return axios.request({
    url,
    method: event.method,
    data: cloudEvent
  });
};

export const startWorkflowRest = (
  data: Record<string, any>,
  endpoint: string,
  businessKey: string
): Promise<string> => {
  const requestURL = `${endpoint}${
    businessKey.length > 0 ? `?businessKey=${businessKey}` : ''
  }`;
  return new Promise((resolve, reject) => {
    axios
      .post(requestURL, { workflowdata: data })
      .then((response: any) => {
        resolve(response.data.id);
      })
      .catch((err) => reject(err));
  });
};

export const getCustomDashboard = (
  customDashboardFilter: string[]
): Promise<CustomDashboardInfo[]> => {
  return new Promise((resolve, reject) => {
    axios
      .get('/customDashboard/list', {
        params: {
          names: customDashboardFilter.join(';')
        }
      })
      .then((result) => {
        resolve(result.data);
      })
      .catch((error) => reject(error));
  });
};

export const getCustomDashboardContent = (name: string): Promise<string> => {
  return new Promise((resolve, reject) => {
    axios
      .get(`/customDashboard/${name}`)
      .then((result) => {
        resolve(result.data);
      })
      .catch((error) => reject(error));
  });
};

export const getCustomWorkflowSchemaFromApi = async (
  api: OpenAPI.Document,
  workflowName: string
): Promise<Record<string, any>> => {
  let schema = {};

  try {
    const schemaFromRequestBody =
      api.paths['/' + workflowName].post.requestBody.content['application/json']
        .schema;

    if (schemaFromRequestBody.type) {
      schema = {
        type: schemaFromRequestBody.type,
        properties: schemaFromRequestBody.properties
      };
    } else {
      schema = (api as any).components.schemas[workflowName + '_input'];
    }
  } catch (e) {
    console.log(e);
    schema = (api as any).components.schemas[workflowName + '_input'];
  }

  // Components can contain the content of internal refs ($ref)
  // This keeps the refs working while avoiding circular refs with the workflow itself
  if (schema) {
    const { [workflowName + '_input']: _, ...schemas } =
      (api as any).components?.schemas ?? {};
    (schema as any)['components'] = { schemas };
  }

  return schema ?? null;
};

export const getCustomWorkflowSchema = async (
  devUIUrl: string,
  openApiPath: string,
  workflowName: string
): Promise<Record<string, any>> => {
  return new Promise((resolve, reject) => {
    SwaggerParser.parse(`${devUIUrl}/${openApiPath}`)
      .then(async (response: any) => {
        resolve(await getCustomWorkflowSchemaFromApi(response, workflowName));
      })
      .catch((err) => reject(err));
  });
};
