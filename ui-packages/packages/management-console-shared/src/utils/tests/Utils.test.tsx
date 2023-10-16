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
import { ProcessInstance, ProcessInstanceState } from '../../types';
import {
  constructObject,
  formatForBulkListJob,
  getProcessInstanceDescription,
  ProcessInstanceIconCreator,
  setTitle
} from '../Utils';
const children = 'children';

export enum JobStatus {
  Error = 'ERROR',
  Executed = 'EXECUTED',
  Scheduled = 'SCHEDULED',
  Retry = 'RETRY',
  Canceled = 'CANCELED'
}
describe('Management-console-shared utils tests', () => {
  it('set title tests', () => {
    const successResult = setTitle('success', 'Abort operation');
    const failureResult = setTitle('failure', 'Skip operation');
    expect(successResult.props[children][1].props.children).toEqual(
      'Abort operation'
    );
    expect(failureResult.props[children][1].props.children).toEqual(
      'Skip operation'
    );
  });

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

  it('Test constructObject function', () => {
    const obj = {};
    const keys = 'trip,country,equal';
    const value = 'India';
    constructObject(obj, keys, value);
    expect(obj).toEqual({ trip: { country: { equal: 'India' } } });
  });

  it('test format job for bulklist function', () => {
    const testJob = [
      {
        id: 'dad3aa88-5c1e-4858-a919-uey23c675a0fa_0',
        processId: 'travels',
        processInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
        rootProcessId: '',
        status: JobStatus.Scheduled,
        priority: 0,
        callbackEndpoint:
          'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
        repeatInterval: null,
        repeatLimit: null,
        scheduledId: null,
        retries: 5,
        lastUpdate: new Date('2020-08-27T03:35:54.635Z'),
        expirationTime: new Date('2020-08-27T04:35:54.631Z'),
        endpoint: 'http://localhost:4000/jobs',
        nodeInstanceId: '08c153e8-2766-4675-81f7-29943efdf411',
        executionCounter: 1,
        errorMessage: '403 error'
      }
    ];
    const testResultWithError = formatForBulkListJob(testJob);
    expect(testResultWithError).toEqual([
      {
        id: testJob[0].id,
        name: testJob[0].processId,
        description: testJob[0].id,
        errorMessage: testJob[0].errorMessage
      }
    ]);
    const testResultWithoutError = formatForBulkListJob([
      { ...testJob[0], errorMessage: null }
    ]);
    expect(testResultWithoutError).toEqual([
      {
        id: testJob[0].id,
        name: testJob[0].processId,
        description: testJob[0].id,
        errorMessage: null
      }
    ]);
  });
});
