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

import axios from 'axios';
import { GraphQL } from '@kogito-apps/consoles-common';
import {
  ProcessInstanceState,
  MilestoneStatus
} from '@kogito-apps/management-console-shared';
import wait from 'waait';
import {
  getSvg,
  handleAbort,
  handleJobReschedule,
  jobCancel,
  performMultipleCancel
} from '../apis';
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;
describe('bulk cancel tests', () => {
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
    }
  ];
  it('bulk cancel success', async () => {
    const expectedResults = {
      successJobs: [
        {
          id: 'dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
          processId: 'travels',
          processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          rootProcessId: '',
          status: 'SCHEDULED',
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
      failedJobs: []
    };
    mockedAxios.delete.mockResolvedValue({});
    const result = await performMultipleCancel(bulkJobs);
    await wait(0);
    expect(result).toEqual(expectedResults);
  });
  it('bulk cancel failure', async () => {
    const expectedResults = {
      successJobs: [],
      failedJobs: [
        {
          id: 'dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
          processId: 'travels',
          processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          rootProcessId: '',
          status: 'SCHEDULED',
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
          errorMessage: undefined
        }
      ]
    };

    mockedAxios.delete.mockRejectedValue({});
    const result = await performMultipleCancel(bulkJobs);
    await wait(0);
    expect(result).toEqual(expectedResults);
  });
});

describe('job cancel tests', () => {
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
    mockedAxios.delete.mockResolvedValue({});
    const cancelResult = await jobCancel(job);
    await wait(0);
    expect(cancelResult).toEqual(expectedResult);
  });

  it('fails to execute job cancel', async () => {
    const expectedResult = {
      modalTitle: 'failure',
      modalContent:
        'The job: T3113e-vbg43-2234-lo89-cpmw3214ra0fa_0 failed to cancel. Error message: 404 error'
    };
    mockedAxios.delete.mockRejectedValue({ message: '404 error' });
    const cancelResult = await jobCancel(job);
    await wait(0);
    expect(cancelResult).toEqual(expectedResult);
  });

  it('test reschedule function', async () => {
    mockedAxios.patch.mockResolvedValue({
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
    const result = await handleJobReschedule(
      job,
      repeatInterval,
      repeatLimit,
      scheduleDate
    );
    expect(result).toEqual({ modalTitle, modalContent });
  });
  it('test error response for reschedule function', async () => {
    mockedAxios.patch.mockRejectedValue({ message: '403 error' });
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
    const result = await handleJobReschedule(
      job,
      repeatInterval,
      repeatLimit,
      scheduleDate
    );
    expect(result).toEqual({ modalTitle, modalContent });
  });
});

describe('test utility of svg panel', () => {
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

  const svgResponse =
    '<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="800" height="300" viewBox="0 0 1748 632"></g></g></svg>';
  it('handle api to get svg', async () => {
    mockedAxios.get.mockResolvedValue({
      data: svgResponse,
      status: 200,
      statusText: 'OK'
    });
    const svgResults = await getSvg(data);
    expect(svgResults).toEqual({ svg: svgResponse });
  });
  it('handle api to get svg', async () => {
    const errorResponse404 = {
      response: { status: 404 }
    };
    mockedAxios.get.mockRejectedValue(errorResponse404);
    await getSvg(data);
  });
  it('check api response when call to management console fails ', async () => {
    mockedAxios.get.mockImplementationOnce(() =>
      Promise.reject({
        error: mockedAxios.get.mockResolvedValue({
          data: svgResponse,
          status: 200,
          statusText: 'OK'
        })
      })
    );
    const svgResults = await getSvg(data);
    expect(svgResults).toEqual({ svg: svgResponse });
  });
  it('check api response when, call to both management console and runtimes fails ', async () => {
    mockedAxios.get.mockImplementationOnce(() =>
      Promise.reject({
        error: mockedAxios.get.mockRejectedValue({
          err: {
            response: { status: 500 }
          }
        })
      })
    );
    await getSvg(data);
  });

  describe('handle Abort tests', () => {
    const processInstanceData = {
      id: '123',
      processId: 'travels',
      processName: 'travels',
      serviceUrl: 'http://localhost:4000',
      state: ProcessInstanceState.Active
    };
    it('executes Abort process successfully', async () => {
      mockedAxios.delete.mockResolvedValue({});
      const abortResults = await handleAbort(processInstanceData);
      await wait(0);
      expect(abortResults).toEqual({
        title: 'Abort operation',
        content: `The process ${processInstanceData.processName} was successfully aborted.`,
        type: 'success'
      });
    });
    it('fails executing Abort process', async () => {
      mockedAxios.delete.mockRejectedValue({ message: '403 error' });
      const abortResults = await handleAbort(processInstanceData);
      await wait(0);
      expect(abortResults).toEqual({
        title: 'Abort operation',
        content: `Failed to abort process ${processInstanceData.processName}. Message: 403 error`,
        type: 'failure'
      });
    });
  });
});
