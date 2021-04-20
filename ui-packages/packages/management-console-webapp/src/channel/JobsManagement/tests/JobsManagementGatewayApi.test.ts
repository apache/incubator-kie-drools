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

import { JobsManagementQueries } from '../JobsManagementQueries';
import { Job, JobStatus } from '@kogito-apps/management-console-shared';
import {
  JobsManagementGatewayApi,
  JobsManagementGatewayApiImpl
} from '../JobsManagementGatewayApi';
import { GraphQL } from '@kogito-apps/consoles-common';

export const JobData: Job = {
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
  status: GraphQL.JobStatus.Scheduled
};

const getJobsMock = jest.fn();

const MockJobsManagementQueries = jest.fn<JobsManagementQueries, []>(() => ({
  getJobs: getJobsMock
}));

let queries: JobsManagementQueries;
let gatewayApi: JobsManagementGatewayApi;
const filters = [GraphQL.JobStatus.Scheduled];
const orderBy: any = { lastUpdate: GraphQL.OrderBy.Asc };
const JobsManagementState = {
  filters: ['SCHEDULED'],
  orderBy: { lastUpdate: 'ASC' }
};

const job = {
  callbackEndpoint:
    'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
  endpoint: 'http://localhost:4000/jobs',
  executionCounter: 0,
  expirationTime: new Date('2020-08-29T04:35:54.631Z'),
  id: 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fj_0',
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
describe('JobsManagementChannelApiImpl tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    queries = new MockJobsManagementQueries();
    gatewayApi = new JobsManagementGatewayApiImpl(queries);
    getJobsMock.mockReturnValue(Promise.resolve([]));
  });

  it('initialLoad', () => {
    gatewayApi.initialLoad(filters, orderBy);
    expect(gatewayApi.jobsManagementState).toStrictEqual(JobsManagementState);
  });

  it('applyFilter', () => {
    gatewayApi.initialLoad(filters, orderBy);

    gatewayApi.applyFilter(filters);

    expect(gatewayApi.jobsManagementState.filters).toBe(filters);
  });

  it('cancelJob', async () => {
    const modalTitle = 'failure';
    const modalContent =
      'The job: eff4ee-11qw23-6675-pokau97-qwedjut45a0fj_0 failed to cancel. Error message: Network Error';
    const result = await gatewayApi.cancelJob(job);

    expect(result).toEqual({ modalTitle, modalContent });
  });

  it('bulkCancel', async () => {
    const successJobs = [];
    const failedJobs = [job];
    const result = await gatewayApi.bulkCancel([job]);

    expect(result).toEqual({ successJobs, failedJobs });
  });

  it('sortBy', () => {
    gatewayApi.sortBy(orderBy);

    expect(gatewayApi.jobsManagementState.orderBy).toBe(orderBy);
  });

  it('query', () => {
    gatewayApi.initialLoad(filters, orderBy);
    gatewayApi.query(0, 10);

    expect(queries.getJobs).toHaveBeenCalledWith(
      0,
      10,
      JobsManagementState.filters,
      JobsManagementState.orderBy
    );
  });
});
