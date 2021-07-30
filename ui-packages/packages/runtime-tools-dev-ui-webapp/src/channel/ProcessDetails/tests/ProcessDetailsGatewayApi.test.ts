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

import { ProcessDetailsQueries } from '../ProcessDetailsQueries';
import {
  Job,
  JobStatus,
  MilestoneStatus,
  ProcessInstance,
  ProcessInstanceState,
  TriggerableNode
} from '@kogito-apps/management-console-shared';
import {
  OnOpenProcessInstanceDetailsListener,
  ProcessDetailsGatewayApi,
  ProcessDetailsGatewayApiImpl
} from '../ProcessDetailsGatewayApi';
import { GraphQL } from '@kogito-apps/consoles-common';
import {
  handleJobReschedule,
  jobCancel,
  getSvg,
  handleProcessAbort,
  getTriggerableNodes,
  handleNodeTrigger,
  handleProcessVariableUpdate
} from '../../apis/apis';

jest.mock('../../apis/apis', () => ({
  handleJobReschedule: jest.fn(),
  jobCancel: jest.fn(),
  getSvg: jest.fn(),
  handleProcessAbort: jest.fn(),
  getTriggerableNodes: jest.fn(),
  handleNodeTrigger: jest.fn(),
  handleProcessVariableUpdate: jest.fn()
}));

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
const getProcessDetailsMock = jest.fn();

const MockProcessDetailsQueries = jest.fn<ProcessDetailsQueries, []>(() => ({
  getProcessDetails: getProcessDetailsMock,
  getJobs: getJobsMock
}));

let queries: ProcessDetailsQueries;
let gatewayApi: ProcessDetailsGatewayApi;
const id = '8035b580-6ae4-4aa8-9ec0-e18e19809e0b';

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

const data: ProcessInstance = {
  id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
  processId: 'hotelBooking',
  processName: 'HotelBooking',
  businessKey: 'T1234HotelBooking01',
  parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
  parentProcessInstance: null,
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
describe('ProcessDetailsGatewayApi tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    queries = new MockProcessDetailsQueries();
    gatewayApi = new ProcessDetailsGatewayApiImpl(queries);
  });

  it('getProcessDiagram', () => {
    gatewayApi.getProcessDiagram(data);

    expect(getSvg).toHaveBeenCalledWith(data);
  });

  it('cancelJob', async () => {
    const modalTitle = 'failure';
    const modalContent =
      'The job: eff4ee-11qw23-6675-pokau97-qwedjut45a0fj_0 failed to cancel. Error message: Network Error';
    //@ts-ignore
    jobCancel.mockReturnValueOnce({ modalTitle, modalContent });
    const result = await gatewayApi.cancelJob(job);
    expect(jobCancel).toHaveBeenCalledWith(job);
    expect(result).toStrictEqual({ modalTitle, modalContent });
  });

  it('rescheduleJob', async () => {
    const modalTitle = 'success';
    const modalContent = `Reschedule of job: 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fj_0' is successful`;
    //@ts-ignore
    handleJobReschedule.mockReturnValueOnce({ modalTitle, modalContent });
    const repeatInterval = 0;
    const repeatLimit = 0;
    const scheduleDate = new Date('2021-08-27T03:35:50.147Z');
    const rescheduleResult = await gatewayApi.rescheduleJob(
      job,
      repeatInterval,
      repeatLimit,
      scheduleDate
    );
    expect(handleJobReschedule).toHaveBeenCalledWith(
      job,
      repeatInterval,
      repeatLimit,
      scheduleDate
    );
    expect(rescheduleResult).toStrictEqual({
      modalTitle,
      modalContent
    });
  });

  it('handleProcessAbort', async () => {
    await gatewayApi.handleProcessAbort(data);
    expect(handleProcessAbort).toHaveBeenCalledWith(data);
  });

  it('get triggerable node', () => {
    gatewayApi.getTriggerableNodes(data);
    expect(getTriggerableNodes).toHaveBeenCalledWith(data);
  });

  it('handle node trigger test', () => {
    const node: TriggerableNode = {
      id: 1234,
      name: 'book_travel',
      type: 'start',
      uniqueId: 'avg3-wwr2-bgh5t6',
      nodeDefinitionId: '_aabfr245kgtgiy'
    };
    gatewayApi.handleNodeTrigger(data, node);
    expect(handleNodeTrigger).toHaveBeenCalledWith(data, node);
  });

  it('handleProcessVariableUpdate', async () => {
    await gatewayApi.handleProcessVariableUpdate(data, {});
    expect(handleProcessVariableUpdate).toHaveBeenCalledWith(data, {});
  });

  it('processDetailsQuery- success response', () => {
    getProcessDetailsMock.mockReturnValue(Promise.resolve([]));
    gatewayApi.processDetailsQuery(id);

    expect(queries.getProcessDetails).toHaveBeenCalledWith(id);
  });

  it('jobsQuery- success response', () => {
    getJobsMock.mockReturnValue(Promise.resolve([]));
    gatewayApi.jobsQuery(id);

    expect(queries.getJobs).toHaveBeenCalledWith(id);
  });

  it('openProcessDetails', () => {
    const listener: OnOpenProcessInstanceDetailsListener = {
      onOpen: jest.fn()
    };

    const unsubscribe = gatewayApi.onOpenProcessInstanceDetailsListener(
      listener
    );

    gatewayApi.openProcessInstanceDetails('testId');

    expect(listener.onOpen).toHaveBeenLastCalledWith('testId');

    unsubscribe.unSubscribe();
  });
});
