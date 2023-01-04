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
  ProcessInstanceState,
  ProcessInstance
} from '@kogito-apps/management-console-shared';
import {
  alterOrderByObj,
  checkProcessInstanceState,
  formatForBulkListProcessInstance,
  getProcessInstanceDescription,
  ProcessInstanceIconCreator
} from '../ProcessListUtils';
import { ProcessInstances } from '../../ProcessListTable/tests/mocks/Mocks';
import { OrderBy } from '../../../../api';
describe('uitility function testing', () => {
  it('state icon creator tests', () => {
    const activeTestResult = ProcessInstanceIconCreator(
      ProcessInstanceState.Active
    );
    const completedTestResult = ProcessInstanceIconCreator(
      ProcessInstanceState.Completed
    );
    const errorTestResult = ProcessInstanceIconCreator(
      ProcessInstanceState.Error
    );
    const suspendedTestResult = ProcessInstanceIconCreator(
      ProcessInstanceState.Suspended
    );
    const abortedTestResult = ProcessInstanceIconCreator(
      ProcessInstanceState.Aborted
    );

    expect(activeTestResult.props['children'][1]).toEqual('Active');
    expect(completedTestResult.props['children'][1]).toEqual('Completed');
    expect(errorTestResult.props['children'][1]).toEqual('Error');
    expect(suspendedTestResult.props['children'][1]).toEqual('Suspended');
    expect(abortedTestResult.props['children'][1]).toEqual('Aborted');
  });

  it('test alterOrderByObj method', () => {
    const orderById = { id: OrderBy.DESC };
    const orderByStatus = { status: OrderBy.DESC };
    const orderByCreated = { created: OrderBy.DESC };
    expect(alterOrderByObj(orderById)).toEqual({
      processName: OrderBy.DESC
    });
    expect(alterOrderByObj(orderByStatus)).toEqual({
      state: OrderBy.DESC
    });
    expect(alterOrderByObj(orderByCreated)).toEqual({
      start: OrderBy.DESC
    });
  });
  it('test getProcessInstanceDescription', () => {
    const processInstance: ProcessInstance = {
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
    const result = getProcessInstanceDescription(processInstance);
    expect(result).toStrictEqual({
      id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
      name: 'Travels',
      description: 'GRT32'
    });
  });

  it('test checkProcessInstanceState method', () => {
    const testProcessInstance1 = {
      state: ProcessInstanceState.Active,
      addons: ['process-management'],
      serviceUrl: 'http://localhost:4000'
    };
    const testProcessInstance2 = {
      state: ProcessInstanceState.Aborted,
      addons: [],
      serviceUrl: null
    };
    const falseResult = checkProcessInstanceState(testProcessInstance1);
    const trueResult = checkProcessInstanceState(testProcessInstance2);
    expect(falseResult).toBeFalsy();
    expect(trueResult).toBeTruthy();
  });

  it('test format process instance for bulklist function', () => {
    const testProcessInstance = [
      { ...ProcessInstances[0], errorMessage: '404 error' }
    ];
    const testResultWithError =
      formatForBulkListProcessInstance(testProcessInstance);
    expect(testResultWithError).toEqual([
      {
        id: testProcessInstance[0].id,
        name: testProcessInstance[0].processName,
        description: testProcessInstance[0].businessKey,
        errorMessage: testProcessInstance[0].errorMessage
      }
    ]);
    const testResultWithoutError = formatForBulkListProcessInstance([
      { ...ProcessInstances[0], errorMessage: null }
    ]);
    expect(testResultWithoutError).toEqual([
      {
        id: testProcessInstance[0].id,
        name: testProcessInstance[0].processName,
        description: testProcessInstance[0].businessKey,
        errorMessage: null
      }
    ]);
  });
});
