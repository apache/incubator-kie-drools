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

import {
  Job,
  JobStatus
} from '@kogito-apps/management-console-shared/dist/types';
import { JobsManagementChannelApiImpl } from '../JobsManagementChannelApiImpl';
import { JobsManagementDriver } from '../../api';
import { MockedJobsManagementDriver } from './mocks/Mocks';

const filter: JobStatus[] = [JobStatus.Scheduled];

const sortBy: any = {
  orderBy: { lastUpdate: 'ASC' }
};

export const Jobs: Job = {
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
};

let driver: JobsManagementDriver;
let api: JobsManagementChannelApiImpl;

const jobsToBeActioned = [];

describe('JobsManagementChannelApiImpl tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    driver = new MockedJobsManagementDriver();
    api = new JobsManagementChannelApiImpl(driver);
  });

  it('jobList__initialLoad', () => {
    api.jobList__initialLoad(filter, sortBy);
    expect(driver.initialLoad).toHaveBeenCalledWith(filter, sortBy);
  });

  it('jobList__applyFilter', () => {
    api.jobList__applyFilter(filter);
    expect(driver.applyFilter).toHaveBeenCalledWith(filter);
  });

  it('jobList__bulkCancel', () => {
    api.jobList__bulkCancel(jobsToBeActioned);
    expect(driver.bulkCancel).toHaveBeenCalledWith(jobsToBeActioned);
  });

  it('jobList__cancelJob', () => {
    api.jobList_cancelJob(Jobs);
    expect(driver.cancelJob).toHaveBeenCalledWith(Jobs);
  });

  it('jobList__rescheduleJob', () => {
    const repeatInterval = 0;
    const repeatLimit = 0;
    const scheduleDate = new Date('2021-08-27T03:35:50.147Z');
    api.jobList_rescheduleJob(Jobs, repeatInterval, repeatLimit, scheduleDate);
    expect(driver.rescheduleJob).toHaveBeenCalledWith(
      Jobs,
      repeatInterval,
      repeatLimit,
      scheduleDate
    );
  });

  it('jobList__sortBy', () => {
    api.jobList_sortBy(sortBy);
    expect(driver.sortBy).toHaveBeenCalledWith(sortBy);
  });

  it('jobList__query', () => {
    api.jobList__query(0, 10);
    expect(driver.query).toHaveBeenCalledWith(0, 10);
  });
});
