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

import { ProcessListChannelApiImpl } from '../ProcessListChannelApiImpl';
import { ProcessListDriver } from '../../api';
import { MockedProcessListDriver } from './utils/Mocks';
import {
  OperationType,
  ProcessInstance,
  ProcessInstanceState,
  OrderBy
} from '@kogito-apps/management-console-shared';

const initialState = {
  filters: {
    status: [ProcessInstanceState.Active],
    businessKey: []
  },
  sortBy: {
    lastUpdate: OrderBy.DESC
  }
};

export const processInstance: ProcessInstance = {
  id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
  processId: 'Travels',
  businessKey: 'GRT32',
  parentProcessInstanceId: null,
  processName: 'Travels',
  rootProcessInstanceId: null,
  roles: [],
  state: ProcessInstanceState.Active,
  start: new Date('2020-02-19T11:11:56.282Z'),
  end: new Date('2020-02-19T11:11:56.282Z'),
  lastUpdate: new Date('2020-02-19T11:11:56.282Z'),
  serviceUrl: null,
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

let driver: ProcessListDriver;
let api: ProcessListChannelApiImpl;

describe('ProcessListChannelApiImpl tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    driver = new MockedProcessListDriver();
    api = new ProcessListChannelApiImpl(driver);
  });

  it('processList__initialLoad', () => {
    api.processList__initialLoad(initialState.filters, initialState.sortBy);
    expect(driver.initialLoad).toHaveBeenCalledWith(
      initialState.filters,
      initialState.sortBy
    );
  });

  it('processList__applyFilter', () => {
    api.processList__applyFilter(initialState.filters);
    expect(driver.applyFilter).toHaveBeenCalledWith(initialState.filters);
  });

  it('processList__applySorting', () => {
    api.processList__applySorting(initialState.sortBy);
    expect(driver.applySorting).toHaveBeenCalledWith(initialState.sortBy);
  });
  it('processList__handleProcessSkip', () => {
    api.processList__handleProcessSkip(processInstance);
    expect(driver.handleProcessSkip).toHaveBeenCalledWith(processInstance);
  });
  it('processList__handleProcessRetry', () => {
    api.processList__handleProcessRetry(processInstance);
    expect(driver.handleProcessRetry).toHaveBeenCalledWith(processInstance);
  });
  it('processList__handleProcessAbort', () => {
    api.processList__handleProcessAbort(processInstance);
    expect(driver.handleProcessAbort).toHaveBeenCalledWith(processInstance);
  });
  it('processList__handleProcessMultipleAction', () => {
    api.processList__handleProcessMultipleAction(
      [processInstance],
      OperationType.ABORT
    );
    expect(driver.handleProcessMultipleAction).toHaveBeenCalledWith(
      [processInstance],
      OperationType.ABORT
    );
  });
  it('processList__getProcessInstancesquery', () => {
    api.processList__query(0, 10);
    expect(driver.query).toHaveBeenCalledWith(0, 10);
  });

  it('processList__getChildProcessInstancesQuery', () => {
    const rootProcessInstanceId = 'ddCd2-3eqqw-12ffg-ppmwn-old34';
    api.processList__getChildProcessesQuery(rootProcessInstanceId);
    expect(driver.getChildProcessesQuery).toHaveBeenCalledWith(
      rootProcessInstanceId
    );
  });
});
