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

import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import {
  getTaskEndpointSecurityParams,
  getTaskSchemaEndPoint,
  resolveTaskPriority,
  trimTaskEndpoint
} from '../Utils';

describe('Utils tests', () => {
  process.env = Object.assign(process.env, { CUSTOM_VAR: 'value' });
  const userTask: UserTaskInstance = {
    id: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
    description: null,
    referenceName: 'Apply for visa',
    name: 'VisaApplication',
    priority: '1',
    processInstanceId: '9ae7ce3b-d49c-4f35-b843-8ac3d22fa427',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: new Date('2020-02-19T11:11:56.282Z'),
    excludedUsers: [],
    potentialGroups: [],
    potentialUsers: [],
    inputs:
      '{"Skippable":"true","trip":{"city":"Boston","country":"US","begin":"2020-02-19T23:00:00.000+01:00","end":"2020-02-26T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","traveller":{"firstName":"Rachel","lastName":"White","email":"rwhite@gorle.com","nationality":"Polish","address":{"street":"Cabalone","city":"Zerf","zipCode":"765756","country":"Poland"}},"Priority":"1"}',
    outputs: '{}',
    lastUpdate: new Date('2020-02-19T11:11:56.282Z'),
    endpoint:
      'http://localhost:8080/travels/9ae7ce3b-d49c-4f35-b843-8ac3d22fa427/VisaApplication/45a73767-5da3-49bf-9c40-d533c3e77ef3'
  };
  it('getTaskSchemaEndPoint', () => {
    const currentUser = { id: '', groups: [] };
    const expectedResult =
      'http://localhost:8080/travels/9ae7ce3b-d49c-4f35-b843-8ac3d22fa427/VisaApplication/45a73767-5da3-49bf-9c40-d533c3e77ef3/schema?user=';
    const result = getTaskSchemaEndPoint(userTask, currentUser);
    expect(result).toEqual(expectedResult);
  });

  it('getTaskSchemaEndPoint - with completed', () => {
    const currentUser = { id: '', groups: [] };
    const expectedResult =
      'http://localhost:8080/travels/VisaApplication/schema';
    const result = getTaskSchemaEndPoint(
      { ...userTask, completed: true },
      currentUser
    );
    expect(result).toEqual(expectedResult);
  });

  it('getTaskEndpointSecurityParams', () => {
    const currentUser = { id: 'admin', groups: ['admin'] };
    const expectedResult = 'user=admin&group=admin';
    const result = getTaskEndpointSecurityParams(currentUser);
    expect(result).toEqual(expectedResult);
  });

  it('trimTaskEndpoint', () => {
    const expectedResult = 'http://localhost:8080/travels/...';
    const result = trimTaskEndpoint(userTask);
    expect(result).toEqual(expectedResult);
  });

  it('trimTaskEndpoint - without endpoint', () => {
    const expectedResult = '-';
    const result = trimTaskEndpoint({ ...userTask, endpoint: '' });
    expect(result).toEqual(expectedResult);
  });
  it('resolveTaskPriority', () => {
    const HighResult = resolveTaskPriority('0');
    expect(HighResult).toEqual('0 - High');
    const MediumResult = resolveTaskPriority('5');
    expect(MediumResult).toEqual('5 - Medium');
    const LowResult = resolveTaskPriority('10');
    expect(LowResult).toEqual('10 - Low');
    const OtherResult = resolveTaskPriority('15');
    expect(OtherResult).toEqual('15');
  });
});
