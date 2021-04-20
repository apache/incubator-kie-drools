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
import wait from 'waait';
import { handleJobReschedule, jobCancel, performMultipleCancel } from '../apis';
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
