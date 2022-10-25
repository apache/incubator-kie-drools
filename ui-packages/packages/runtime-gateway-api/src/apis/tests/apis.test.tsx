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
// import wait from 'waait';
import {
  handleJobReschedule,
  handleProcessMultipleAction,
  jobCancel,
  performMultipleCancel,
  getTriggerableNodes,
  handleNodeTrigger,
  handleProcessVariableUpdate,
  handleNodeInstanceRetrigger,
  handleNodeInstanceCancel,
  handleProcessAbort,
  handleProcessSkip,
  handleProcessRetry,
  getProcessDetails,
  getJobs,
  getSVG
} from '../apis';
import {
  BulkProcessInstanceActionResponse,
  OperationType,
  ProcessInstanceState,
  NodeInstance,
  ProcessInstance,
  MilestoneStatus,
  JobStatus
} from '@kogito-apps/management-console-shared';
import { act } from 'react-dom/test-utils';
import reactApollo from 'react-apollo';

jest.mock('axios');
jest.mock('apollo-client');

jest.mock('react-apollo', () => {
  const ApolloClient = { query: jest.fn(), mutate: jest.fn() };
  return { useApolloClient: jest.fn(() => ApolloClient) };
});

const processInstance: ProcessInstance = {
  id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
  processId: 'hotelBooking',
  businessKey: 'T1234HotelBooking01',
  parentProcessInstanceId: null,
  processName: 'HotelBooking',
  rootProcessInstanceId: null,
  roles: [],
  state: ProcessInstanceState.Active,
  start: new Date('2020-02-19T11:11:56.282Z'),
  end: new Date('2020-02-19T11:11:56.282Z'),
  lastUpdate: new Date('2020-02-19T11:11:56.282Z'),
  serviceUrl: 'http://localhost:4000',
  endpoint: 'http://localhost:4000',
  error: {
    nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-34578e904e6b',
    message: 'some thing went wrong'
  },
  addons: [],
  variables:
    '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
  nodes: [
    {
      nodeId: '1',
      name: 'End Event 1',
      definitionId: 'EndEvent_1',
      id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
      enter: new Date('2020-02-19T11:11:56.282Z'),
      exit: new Date('2020-02-19T11:11:56.282Z'),
      type: 'EndNode'
    }
  ],
  childProcessInstances: []
};

const processInstances = [
  {
    id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
    processId: 'hotelBooking',
    businessKey: 'T1234HotelBooking01',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'HotelBooking',
    rootProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    roles: [],
    state: ProcessInstanceState.Error,
    start: new Date('2019-10-22T03:40:44.089Z'),
    end: new Date('2019-10-22T05:40:44.089Z'),
    lastUpdate: new Date('2019-10-22T05:40:44.089Z'),
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-34578e904e6b',
      message: 'some thing went wrong'
    },
    addons: [],
    variables:
      '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'End Event 1',
        definitionId: 'EndEvent_1',
        id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
        enter: new Date('2019-10-22T03:37:30.798Z'),
        exit: new Date('2019-10-22T03:37:30.798Z'),
        type: 'EndNode'
      }
    ]
  }
];

describe('bulk cancel tests', () => {
  let client;
  let useApolloClient;
  const mockUseApolloClient = () => {
    act(() => {
      client = useApolloClient();
    });
  };

  beforeEach(() => {
    act(() => {
      useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
      mockUseApolloClient();
    });
  });
  const bulkJobs = [
    {
      id: 'dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
      processId: 'travels',
      processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
      rootProcessId: '',
      status: GraphQL.JobStatus.Scheduled,
      priority: 0,
      callbackEndpoint:
        'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
      repeatInterval: null,
      repeatLimit: null,
      scheduledId: null,
      retries: 0,
      lastUpdate: '2020-08-27T03:35:54.635Z',
      expirationTime: '2020-08-27T04:35:54.631Z',
      endpoint: 'http://localhost:4000/jobs',
      errorMessage: ''
    },
    {
      id: 'dad3aa88-5c1e-4858-a919-6123c675a0fa_01',
      processId: 'travels1',
      processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
      rootProcessId: '',
      status: GraphQL.JobStatus.Scheduled,
      priority: 0,
      callbackEndpoint:
        'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
      repeatInterval: null,
      repeatLimit: null,
      scheduledId: null,
      retries: 0,
      lastUpdate: '2020-08-27T03:35:54.635Z',
      expirationTime: '2020-08-27T04:35:54.631Z',
      endpoint: 'http://localhost:4000/jobs',
      errorMessage: ''
    }
  ];
  it('bulk cancel success and failed', async () => {
    const expectedResults = {
      successJobs: [
        {
          id: 'dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
          processId: 'travels',
          processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          rootProcessId: '',
          status: GraphQL.JobStatus.Scheduled,
          priority: 0,
          callbackEndpoint:
            'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
          repeatInterval: null,
          repeatLimit: null,
          scheduledId: null,
          retries: 0,
          lastUpdate: '2020-08-27T03:35:54.635Z',
          expirationTime: '2020-08-27T04:35:54.631Z',
          endpoint: 'http://localhost:4000/jobs',
          errorMessage: ''
        }
      ],
      failedJobs: [
        {
          id: 'dad3aa88-5c1e-4858-a919-6123c675a0fa_01',
          processId: 'travels1',
          processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
          rootProcessId: '',
          status: GraphQL.JobStatus.Scheduled,
          priority: 0,
          callbackEndpoint:
            'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
          repeatInterval: null,
          repeatLimit: null,
          scheduledId: null,
          retries: 0,
          lastUpdate: '2020-08-27T03:35:54.635Z',
          expirationTime: '2020-08-27T04:35:54.631Z',
          endpoint: 'http://localhost:4000/jobs'
        }
      ]
    };

    client.mutate.mockResolvedValueOnce(expectedResults.successJobs[0]);
    client.mutate.mockRejectedValueOnce(expectedResults.failedJobs[0]);
    const result = await performMultipleCancel(bulkJobs, client);

    expect(result[0].successJob).toEqual(expectedResults.successJobs[0]);
    expect(result[0].failedJob).toBeUndefined();
    expect(result[1].failedJob).toEqual(expectedResults.failedJobs[0]);
  });
});

describe('job cancel tests', () => {
  let client;
  let useApolloClient;
  const mockUseApolloClient = () => {
    act(() => {
      client = useApolloClient();
    });
  };

  beforeEach(() => {
    act(() => {
      useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
      mockUseApolloClient();
    });
  });
  const job = {
    id: 'T3113e-vbg43-2234-lo89-cpmw3214ra0fa_0',
    processId: 'travels',
    processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    rootProcessId: '',
    status: 'ERROR',
    priority: 0,
    callbackEndpoint:
      'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
    repeatInterval: null,
    repeatLimit: null,
    scheduledId: null,
    retries: 0,
    lastUpdate: '2020-08-27T03:35:54.635Z',
    expirationTime: '2020-08-27T04:35:54.631Z'
  };

  it('executes job cancel successfully', async () => {
    const expectedResult = {
      modalTitle: 'success',
      modalContent:
        'The job: T3113e-vbg43-2234-lo89-cpmw3214ra0fa_0 is canceled successfully'
    };
    client.mutate.mockResolvedValue({});
    await jobCancel(job, client)
      .then(value => {
        expect(value).toEqual(expectedResult);
      })
      .catch(reason => {
        expect(reason).toEqual(expectedResult);
      });
  });

  it('fails to execute job cancel', async () => {
    const expectedResult = {
      modalTitle: 'failure',
      modalContent:
        'The job: T3113e-vbg43-2234-lo89-cpmw3214ra0fa_0 failed to cancel. Error message: 404 error'
    };
    client.mutate.mockRejectedValue({ message: '404 error' });
    await jobCancel(job, client)
      .then(value => {
        expect(value).toEqual(expectedResult);
      })
      .catch(reason => {
        expect(reason).toEqual(expectedResult);
      });
  });

  it('test reschedule function', async () => {
    client.mutate.mockResolvedValue({
      status: 200,
      statusText: 'OK',
      data: {
        callbackEndpoint:
          'http://localhost:8080/management/jobs/travels/instances/9865268c-64d7-3a44-8972-7325b295f7cc/timers/58180644-2fdf-4261-83f2-f4e783d308a3_0',
        executionCounter: 0,
        executionResponse: null,
        expirationTime: '2020-10-16T10:17:22.879Z',
        id: '58180644-2fdf-4261-83f2-f4e783d308a3_0',
        lastUpdate: '2020-10-07T07:41:31.467Z',
        priority: 0,
        processId: 'travels',
        processInstanceId: '9865268c-64d7-3a44-8972-7325b295f7cc',
        repeatInterval: null,
        repeatLimit: null,
        retries: 0,
        rootProcessId: null,
        rootProcessInstanceId: null,
        scheduledId: null,
        status: 'SCHEDULED'
      }
    });
    const job = {
      id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      processId: 'travels',
      processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
      rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
      status: GraphQL.JobStatus.Executed,
      priority: 0,
      callbackEndpoint:
        'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      repeatInterval: 1,
      repeatLimit: 3,
      scheduledId: '0',
      retries: 0,
      lastUpdate: '2020-08-27T03:35:50.147Z',
      expirationTime: '2020-08-27T03:35:50.147Z'
    };
    const repeatInterval = 2;
    const repeatLimit = 1;
    const scheduleDate = new Date('2020-08-27T03:35:50.147Z');
    const modalTitle = 'success';
    const modalContent = `Reschedule of job: ${job.id} is successful`;
    await handleJobReschedule(
      job,
      repeatInterval,
      repeatLimit,
      scheduleDate,
      client
    )
      .then(value => {
        expect(value).toEqual({ modalTitle, modalContent });
      })
      .catch(reason => {
        expect(reason).toEqual({ modalTitle, modalContent });
      });

    // expect(result).toEqual({ modalTitle, modalContent });
  });
  it('test error response for reschedule function', async () => {
    client.mutate.mockRejectedValue({ message: '403 error' });
    const job = {
      id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      processId: 'travels',
      processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
      rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
      status: GraphQL.JobStatus.Executed,
      priority: 0,
      callbackEndpoint:
        'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      repeatInterval: 1,
      repeatLimit: 3,
      scheduledId: '0',
      retries: 0,
      lastUpdate: '2020-08-27T03:35:50.147Z',
      expirationTime: '2020-08-27T03:35:50.147Z'
    };
    const repeatInterval = null;
    const repeatLimit = null;
    const scheduleDate = new Date('2020-08-27T03:35:50.147Z');
    const modalTitle = 'failure';
    const modalContent = `Reschedule of job ${job.id} failed. Message: 403 error`;
    await handleJobReschedule(
      job,
      repeatInterval,
      repeatLimit,
      scheduleDate,
      client
    )
      .then(value => {
        expect(value).toEqual({ modalTitle, modalContent });
      })
      .catch(reason => {
        expect(reason).toEqual({ modalTitle, modalContent });
      });
  });
});

describe('handle skip test', () => {
  let client;
  let useApolloClient;
  const mockUseApolloClient = () => {
    act(() => {
      client = useApolloClient();
    });
  };

  beforeEach(() => {
    act(() => {
      useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
      mockUseApolloClient();
    });
  });

  it('on successful skip', async () => {
    client.mutate.mockResolvedValue({ data: 'success' });
    let result = null;
    await handleProcessSkip(processInstance, client)
      .then(() => {
        result = 'success';
      })
      .catch(error => {
        result = error.message;
      });
    expect(result).toEqual('success');
  });
  it('on failed skip', async () => {
    client.mutate.mockRejectedValue({ message: '404 error' });
    let result = null;
    await handleProcessSkip(processInstance, client)
      .then(() => {
        result = 'success';
      })
      .catch(error => {
        result = error.message;
      });
    expect(result).toEqual('404 error');
  });
});

describe('handle retry test', () => {
  let client;
  let useApolloClient;
  const mockUseApolloClient = () => {
    act(() => {
      client = useApolloClient();
    });
  };

  beforeEach(() => {
    act(() => {
      useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
      mockUseApolloClient();
    });
  });

  it('on successful retrigger', async () => {
    client.mutate.mockResolvedValue({ data: 'success' });
    let result = null;
    await handleProcessRetry(processInstances[0], client)
      .then(() => {
        result = 'success';
      })
      .catch(error => {
        result = error.message;
      });
    expect(result).toEqual('success');
  });
  it('on failed retrigger', async () => {
    client.mutate.mockRejectedValue({ message: '404 error' });
    let result = null;
    await handleProcessRetry(processInstance[0], client)
      .then(() => {
        result = 'success';
      })
      .catch(error => {
        result = error.message;
      });
    expect(result).toEqual("Cannot read property 'id' of undefined");
  });
});

describe('handle abort test', () => {
  let client;
  let useApolloClient;
  const mockUseApolloClient = () => {
    act(() => {
      client = useApolloClient();
    });
  };

  beforeEach(() => {
    act(() => {
      useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
      mockUseApolloClient();
    });
  });
  it('on successful abort', async () => {
    client.mutate.mockResolvedValue({ data: 'success' });
    let result = null;
    await handleProcessAbort(processInstances[0], client)
      .then(() => {
        result = 'success';
      })
      .catch(error => {
        result = error.message;
      });
    expect(result).toEqual('success');
  });
  it('on failed abort', async () => {
    client.mutate.mockRejectedValue({ message: '404 error' });
    let result = null;
    await handleProcessAbort(processInstances[0], client)
      .then(() => {
        result = 'success';
      })
      .catch(error => {
        result = error.message;
      });
    expect(result).toEqual('404 error');
  });
});

describe('multiple action in process list', () => {
  let client;
  let useApolloClient;
  const mockUseApolloClient = () => {
    act(() => {
      client = useApolloClient();
    });
  };

  beforeEach(() => {
    act(() => {
      useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
      mockUseApolloClient();
    });
  });
  it('multiple skip test', async () => {
    client.mutate.mockResolvedValue({ data: 'success' });
    const result: BulkProcessInstanceActionResponse = await handleProcessMultipleAction(
      processInstances,
      OperationType.SKIP,
      client
    );
    expect(result.successProcessInstances.length).toEqual(1);
  });
  it('multiple skip test', async () => {
    client.mutate.mockRejectedValue({ message: '404 error' });
    const result: BulkProcessInstanceActionResponse = await handleProcessMultipleAction(
      processInstances,
      OperationType.SKIP,
      client
    );
    expect(result.failedProcessInstances[0].errorMessage).toEqual('404 error');
  });

  it('multiple retry test', async () => {
    client.mutate.mockResolvedValue({ data: 'success' });
    const result: BulkProcessInstanceActionResponse = await handleProcessMultipleAction(
      processInstances,
      OperationType.RETRY,
      client
    );
    expect(result.successProcessInstances.length).toEqual(1);
  });
  it('multiple retry test', async () => {
    client.mutate.mockRejectedValue({ message: '404 error' });
    const result: BulkProcessInstanceActionResponse = await handleProcessMultipleAction(
      processInstances,
      OperationType.RETRY,
      client
    );
    expect(result.failedProcessInstances[0].errorMessage).toEqual('404 error');
  });

  it('multiple abort test', async () => {
    client.mutate.mockResolvedValue({ data: 'success' });
    const result: BulkProcessInstanceActionResponse = await handleProcessMultipleAction(
      processInstances,
      OperationType.ABORT,
      client
    );
    expect(result.successProcessInstances.length).toEqual(1);
  });
  it('multiple abort test', async () => {
    client.mutate.mockRejectedValue({ message: '404 error' });
    const result: BulkProcessInstanceActionResponse = await handleProcessMultipleAction(
      processInstances,
      OperationType.ABORT,
      client
    );
    expect(result.failedProcessInstances[0].errorMessage).toEqual('404 error');
  });
});

describe('test utilities of process variables', () => {
  const updatedProcessInstances = [
    {
      id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
      variables:
        '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}'
    }
  ];

  let client;
  let useApolloClient;
  const mockUseApolloClient = () => {
    act(() => {
      client = useApolloClient();
    });
  };

  beforeEach(() => {
    act(() => {
      useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
      mockUseApolloClient();
    });
  });

  it('test put method that updates process variables-success', async () => {
    client.mutate.mockResolvedValue({
      data: { ProcessInstanceUpdateVariables: processInstances[0].variables }
    });
    let result;
    await handleProcessVariableUpdate(
      processInstance,
      JSON.parse(updatedProcessInstances[0].variables),
      client
    )
      .then(data => {
        result = data;
      })
      .catch(error => {
        result = error.message;
      });
    expect(result).toEqual(JSON.parse(processInstances[0].variables));
  });
});

describe('retrieve list of triggerable nodes test', () => {
  let client;
  let useApolloClient;
  const mockUseApolloClient = () => {
    act(() => {
      client = useApolloClient();
    });
  };

  beforeEach(() => {
    act(() => {
      useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
      mockUseApolloClient();
    });
  });
  const nodeDefinitions = [
    {
      nodeDefinitionId: '_BDA56801-1155-4AF2-94D4-7DAADED2E3C0',
      name: 'Send visa application',
      id: 1,
      type: 'ActionNode',
      uniqueId: '1'
    },
    {
      nodeDefinitionId: '_175DC79D-C2F1-4B28-BE2D-B583DFABF70D',
      name: 'Book',
      id: 2,
      type: 'Split',
      uniqueId: '2'
    },
    {
      nodeDefinitionId: '_E611283E-30B0-46B9-8305-768A002C7518',
      name: 'visasrejected',
      id: 3,
      type: 'EventNode',
      uniqueId: '3'
    }
  ];

  it('successfully retrieves the list of nodes', async () => {
    client.query.mockResolvedValue({
      data: {
        ProcessInstances: [
          {
            nodeDefinitions
          }
        ]
      }
    });
    let result = null;
    await getTriggerableNodes(processInstance, client)
      .then(nodes => {
        result = nodes;
      })
      .catch(error => {
        result = error;
      });
    expect(result).toEqual(nodeDefinitions);
  });
  it('fails to retrieve the list of nodes', async () => {
    client.query.mockRejectedValue({ message: '403 error' });
    let result = null;
    await getTriggerableNodes(processInstance, client)
      .then(nodes => {
        result = nodes;
      })
      .catch(error => {
        result = error.message;
      });
    expect(result.message).toEqual('403 error');
  });
});

describe('handle node trigger click tests', () => {
  let client;
  let useApolloClient;
  const mockUseApolloClient = () => {
    act(() => {
      client = useApolloClient();
    });
  };

  beforeEach(() => {
    act(() => {
      useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
      mockUseApolloClient();
    });
  });
  const node = {
    nodeDefinitionId: '_BDA56801-1155-4AF2-94D4-7DAADED2E3C0',
    name: 'Send visa application',
    id: 1,
    type: 'ActionNode',
    uniqueId: '1'
  };
  it('executes node trigger successfully', async () => {
    let result = '';
    client.mutate.mockResolvedValue({});
    await handleNodeTrigger(processInstance, node, client)
      .then(() => {
        result = 'success';
      })
      .catch(error => {
        result = 'error';
      });
    expect(result).toEqual('success');
  });

  it('fails to execute node trigger', async () => {
    client.mutate.mockRejectedValue({ message: '404 error' });
    let result = '';
    await handleNodeTrigger(processInstance, node, client)
      .then(() => {
        result = 'success';
      })
      .catch(error => {
        result = 'error';
      });
    expect(result).toEqual('error');
  });
});

describe('handle node instance trigger click tests', () => {
  let client;
  let useApolloClient;
  const mockUseApolloClient = () => {
    act(() => {
      client = useApolloClient();
    });
  };

  beforeEach(() => {
    act(() => {
      useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
      mockUseApolloClient();
    });
  });
  const node: NodeInstance = {
    definitionId: '_BDA56801-1155-4AF2-94D4-7DAADED2E3C0',
    name: 'Send visa application',
    id: '1',
    type: 'ActionNode',
    nodeId: 'nodeOne',
    enter: new Date('2020-11-11')
  };
  it('executes node instance trigger successfully', async () => {
    let result = '';
    client.mutate.mockResolvedValue({});
    await handleNodeInstanceRetrigger(processInstance, node, client)
      .then(() => {
        result = 'success';
      })
      .catch(error => {
        result = 'error';
      });
    expect(result).toEqual('success');
  });

  it('fails to execute instance node trigger', async () => {
    client.mutate.mockRejectedValue({ message: '404 error' });
    let result = '';
    await handleNodeInstanceRetrigger(processInstance, node, client)
      .then(() => {
        result = 'success';
      })
      .catch(error => {
        result = 'error';
      });
    expect(result).toEqual('error');
  });
});

describe('handle node instance cancel', () => {
  let client;
  let useApolloClient;
  const mockUseApolloClient = () => {
    act(() => {
      client = useApolloClient();
    });
  };

  beforeEach(() => {
    act(() => {
      useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
      mockUseApolloClient();
    });
  });
  const node: NodeInstance = {
    definitionId: '_BDA56801-1155-4AF2-94D4-7DAADED2E3C0',
    name: 'Send visa application',
    id: '1',
    type: 'ActionNode',
    nodeId: 'nodeOne',
    enter: new Date('2020-11-11')
  };
  it('executes handle node instance cancel successfully', async () => {
    let result = '';
    client.mutate.mockResolvedValue({});
    await handleNodeInstanceCancel(processInstance, node, client)
      .then(() => {
        result = 'success';
      })
      .catch(error => {
        result = 'error';
      });
    expect(result).toEqual('success');
  });

  it('fails to handle node instance cancel', async () => {
    client.mutate.mockRejectedValue({ message: '404 error' });
    let result = '';
    await handleNodeInstanceCancel(processInstance, node, client)
      .then(() => {
        result = 'success';
      })
      .catch(error => {
        result = 'error';
      });
    expect(result).toEqual('error');
  });
});

const mGraphQLResponseProcess = {
  data: {
    ProcessInstances: [
      {
        id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
        processId: 'hotelBooking',
        processName: 'HotelBooking',
        businessKey: 'T1234HotelBooking01',
        parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
        parentProcessInstance: {
          id: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
          processName: 'travels',
          businessKey: 'T1234'
        },
        roles: [],
        variables:
          '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
        state: ProcessInstanceState.Completed,
        start: new Date('2019-10-22T03:40:44.089Z'),
        lastUpdate: new Date('Thu, 22 Apr 2021 14:53:04 GMT'),
        end: new Date('2019-10-22T05:40:44.089Z'),
        addons: [],
        endpoint: 'http://localhost:4000',
        serviceUrl: null,
        error: {
          nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-34578e904e6b',
          message: 'some thing went wrong',
          __typename: 'ProcessInstanceError'
        },
        childProcessInstances: [],
        nodes: [
          {
            id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
            nodeId: '1',
            name: 'End Event 1',
            enter: new Date('2019-10-22T03:37:30.798Z'),
            exit: new Date('2019-10-22T03:37:30.798Z'),
            type: 'EndNode',
            definitionId: 'EndEvent_1',
            __typename: 'NodeInstance'
          },
          {
            id: '41b3f49e-beb3-4b5f-8130-efd28f82b971',
            nodeId: '2',
            name: 'Book hotel',
            enter: new Date('2019-10-22T03:37:30.795Z'),
            exit: new Date('2019-10-22T03:37:30.798Z'),
            type: 'WorkItemNode',
            definitionId: 'ServiceTask_1',
            __typename: 'NodeInstance'
          },
          {
            id: '4165a571-2c79-4fd0-921e-c6d5e7851b67',
            nodeId: '2',
            name: 'StartProcess',
            enter: new Date('2019-10-22T03:37:30.793Z'),
            exit: new Date('2019-10-22T03:37:30.795Z'),
            type: 'StartNode',
            definitionId: 'StartEvent_1',
            __typename: 'NodeInstance'
          }
        ],
        milestones: [
          {
            id: '27107f38-d888-4edf-9a4f-11b9e6d75i86',
            name: 'Manager decision',
            status: MilestoneStatus.Completed,
            __typename: 'Milestone'
          },
          {
            id: '27107f38-d888-4edf-9a4f-11b9e6d75m36',
            name: 'Milestone 1: Order placed',
            status: MilestoneStatus.Active,
            __typename: 'Milestone'
          },
          {
            id: '27107f38-d888-4edf-9a4f-11b9e6d75m66',
            name: 'Milestone 2: Order shipped',
            status: MilestoneStatus.Available,
            __typename: 'Milestone'
          }
        ],
        nodeDefinitions: [
          {
            id: '27107f38-123-4edf-9a4f-11b9e6d75i86',
            name: 'ndoe one',
            type: 'human task',
            uniqueId: '27107f38-123-4edf-9a4f-11b9e6d75i86',
            nodeDefinitionId: 'nodeDefinitionId-test'
          }
        ]
      }
    ]
  }
};
const mGraphQLResponse = {
  data: {
    Jobs: [
      {
        callbackEndpoint:
          'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
        endpoint: 'http://localhost:4000/jobs',
        executionCounter: 0,
        expirationTime: new Date('2020-08-29T04:35:54.631Z'),
        id: 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0',
        lastUpdate: new Date('2020-06-29T03:35:54.635Z'),
        priority: 0,
        processId: 'travels',
        processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
        repeatInterval: null,
        repeatLimit: null,
        retries: 2,
        rootProcessId: '',
        scheduledId: null,
        status: JobStatus.Scheduled
      }
    ]
  }
};

describe('process details tests', () => {
  let client;
  let useApolloClient;
  const mockUseApolloClient = () => {
    act(() => {
      client = useApolloClient();
    });
  };

  const id = '8035b580-6ae4-4aa8-9ec0-e18e19809e0b';

  beforeEach(() => {
    act(() => {
      useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
      mockUseApolloClient();
    });
  });

  it('test getProcessDetails method with success response', () => {
    client.query.mockResolvedValue(mGraphQLResponseProcess);
    getProcessDetails(id, client)
      .then(value => {
        expect(value).toEqual(mGraphQLResponseProcess.data.ProcessInstances[0]);
      })
      .catch(error => {});
  });
  it('test getProcessDetails method with failure response', () => {
    getProcessDetails(id, client)
      .then(value => {
        //do nothing
      })
      .catch(error => {
        expect(error['graphQLErrors'][0]['message']).toEqual("error");
      });
  });
  it('test getTriggerableNodes method with success response', () => {
    client.query.mockResolvedValue(
      mGraphQLResponseProcess.data.ProcessInstances[0]
    );
    getTriggerableNodes(processInstance, client)
      .then(value => {
        expect(value).toEqual(
          mGraphQLResponseProcess.data.ProcessInstances[0].nodeDefinitions
        );
      })
      .catch(error => {});
  });
  it('test getJobs method with success response', () => {
    client.query.mockResolvedValue(mGraphQLResponse);
    getJobs(id, client).then(value => {
      expect(value).toEqual(mGraphQLResponse.data.Jobs);
    });
  });
});

// describe('get node definitions tests', () => {
//   let client;
//   let useApolloClient;
//   const mockUseApolloClient = () => {
//     act(() => {
//       client = useApolloClient();
//     });
//   };
//
//   beforeEach(() => {
//     act(() => {
//       useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
//       mockUseApolloClient();
//     });
//   });
// });

describe('get svg tests', () => {
  const data: any = {
    id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
    processId: 'hotelBooking',
    processName: 'HotelBooking',
    businessKey: 'T1234HotelBooking01',
    parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    parentProcessInstance: {
      id: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
      processName: 'travels',
      businessKey: 'T1234'
    },
    roles: [],
    variables:
      '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
    state: ProcessInstanceState.Completed,
    start: new Date('2019-10-22T03:40:44.089Z'),
    lastUpdate: new Date('Thu, 22 Apr 2021 14:53:04 GMT'),
    end: new Date('2019-10-22T05:40:44.089Z'),
    addons: [],
    endpoint: 'http://localhost:4000',
    serviceUrl: 'http://localhost:4000',
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-34578e904e6b',
      message: 'some thing went wrong',
      __typename: 'ProcessInstanceError'
    },
    childProcessInstances: [],
    nodes: [
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
        nodeId: '1',
        name: 'End Event 1',
        enter: new Date('2019-10-22T03:37:30.798Z'),
        exit: new Date('2019-10-22T03:37:30.798Z'),
        type: 'EndNode',
        definitionId: 'EndEvent_1',
        __typename: 'NodeInstance'
      },
      {
        id: '41b3f49e-beb3-4b5f-8130-efd28f82b971',
        nodeId: '2',
        name: 'Book hotel',
        enter: new Date('2019-10-22T03:37:30.795Z'),
        exit: new Date('2019-10-22T03:37:30.798Z'),
        type: 'WorkItemNode',
        definitionId: 'ServiceTask_1',
        __typename: 'NodeInstance'
      },
      {
        id: '4165a571-2c79-4fd0-921e-c6d5e7851b67',
        nodeId: '2',
        name: 'StartProcess',
        enter: new Date('2019-10-22T03:37:30.793Z'),
        exit: new Date('2019-10-22T03:37:30.795Z'),
        type: 'StartNode',
        definitionId: 'StartEvent_1',
        __typename: 'NodeInstance'
      }
    ],
    milestones: [
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75i86',
        name: 'Manager decision',
        status: MilestoneStatus.Completed,
        __typename: 'Milestone'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m36',
        name: 'Milestone 1: Order placed',
        status: MilestoneStatus.Active,
        __typename: 'Milestone'
      },
      {
        id: '27107f38-d888-4edf-9a4f-11b9e6d75m66',
        name: 'Milestone 2: Order shipped',
        status: MilestoneStatus.Available,
        __typename: 'Milestone'
      }
    ]
  };

  const svgResponse = {
    ProcessInstances: [
      {
        diagram:
          '<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="800" height="300" viewBox="0 0 1748 632"></g></g></svg>'
      }
    ]
  };

  let client;
  let useApolloClient;
  const mockUseApolloClient = () => {
    act(() => {
      client = useApolloClient();
    });
  };

  beforeEach(() => {
    act(() => {
      useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
      mockUseApolloClient();
    });
  });

  it('handle api to get svg', async () => {
    client.query.mockResolvedValue({
      data: svgResponse,
      status: 200,
      statusText: 'OK'
    });
    getSVG(data, client).then(value => {
      expect(value.svg).toEqual(svgResponse.ProcessInstances[0].diagram);
    });
  });

  it('handle error api to get svg', async () => {
    let result = null;
    const errorResponse404 = {
      message: {
        response: { status: 404 }
      }
    };
    client.query.mockRejectedValue(errorResponse404);
    getSVG(data, client)
      .then(value => {
        result = value;
      })
      .catch(reason => {
        expect(errorResponse404.message).toEqual(reason.error);
      });
    expect(null).toEqual(result);
  });
});
