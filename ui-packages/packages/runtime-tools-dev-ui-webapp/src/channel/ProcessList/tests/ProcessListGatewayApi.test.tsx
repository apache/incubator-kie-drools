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
  OrderBy,
  ProcessInstanceFilter,
  SortBy
} from '@kogito-apps/process-list';
import {
  OperationType,
  ProcessInstance,
  ProcessInstanceState
} from '@kogito-apps/management-console-shared';
import {
  ProcessListGatewayApi,
  ProcessListGatewayApiImpl
} from '../ProcessListGatewayApi';
import { ProcessListQueries } from '../ProcessListQueries';
import {
  handleProcessAbort,
  handleProcessMultipleAction,
  handleProcessRetry,
  handleProcessSkip
} from '../../apis/apis';

export const processInstance: ProcessInstance = {
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

jest.mock('../../apis/apis', () => ({
  handleProcessSkip: jest.fn(),
  handleProcessRetry: jest.fn(),
  handleProcessAbort: jest.fn(),
  handleProcessMultipleAction: jest.fn()
}));

const getProcessInstancesMock = jest.fn();
const getChildProcessInstancesMock = jest.fn();

const MockProcessListQueries = jest.fn<ProcessListQueries, []>(() => ({
  getProcessInstances: getProcessInstancesMock,
  getChildProcessInstances: getChildProcessInstancesMock
}));

let queries: ProcessListQueries;
let gatewayApi: ProcessListGatewayApi;
const processListFilters: ProcessInstanceFilter = {
  status: [ProcessInstanceState.Active],
  businessKey: []
};
const sortBy: SortBy = { lastUpdate: OrderBy.DESC };
const rootProcessInstanceId: string = 'a1e139d5-4e77-48c9-84ae-34578e904e5a';
describe('ProcessListChannelApiImpl tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    queries = new MockProcessListQueries();
    gatewayApi = new ProcessListGatewayApiImpl(queries);
    getProcessInstancesMock.mockReturnValue(Promise.resolve([]));
    getChildProcessInstancesMock.mockReturnValue(Promise.resolve([]));
  });

  it('Initial load', () => {
    gatewayApi.initialLoad(processListFilters, sortBy);
    expect(gatewayApi.processListState).toStrictEqual({
      filters: processListFilters,
      sortBy: sortBy
    });
  });

  it('applyFilter', () => {
    gatewayApi.initialLoad(processListFilters, sortBy);
    gatewayApi.applyFilter(processListFilters);
    expect(gatewayApi.processListState.filters).toBe(processListFilters);
  });

  it('applySorting', () => {
    gatewayApi.initialLoad(processListFilters, sortBy);
    gatewayApi.applySorting(sortBy);
    expect(gatewayApi.processListState.sortBy).toBe(sortBy);
  });

  it('handleProcessSkip', async () => {
    await gatewayApi.handleProcessSkip(processInstance);
    expect(handleProcessSkip).toHaveBeenCalledWith(processInstance);
  });

  it('handleProcessRetry', async () => {
    await gatewayApi.handleProcessRetry(processInstance);
    expect(handleProcessRetry).toHaveBeenCalledWith(processInstance);
  });

  it('handleProcessAbort', async () => {
    await gatewayApi.handleProcessAbort(processInstance);
    expect(handleProcessAbort).toHaveBeenCalledWith(processInstance);
  });

  it('handle multi action', async () => {
    await gatewayApi.handleProcessMultipleAction(
      [processInstance],
      OperationType.ABORT
    );
    expect(handleProcessMultipleAction).toHaveBeenCalledWith(
      [processInstance],
      OperationType.ABORT
    );
  });

  it('process instance query', () => {
    gatewayApi.initialLoad(processListFilters, sortBy);
    gatewayApi.applySorting(sortBy);
    gatewayApi.query(0, 10);
    expect(queries.getProcessInstances).toHaveBeenCalledWith(
      0,
      10,
      processListFilters,
      sortBy
    );
  });

  it('process instance child query', () => {
    gatewayApi.getChildProcessesQuery(rootProcessInstanceId);
    expect(queries.getChildProcessInstances).toHaveBeenCalledWith(
      rootProcessInstanceId
    );
  });
});
