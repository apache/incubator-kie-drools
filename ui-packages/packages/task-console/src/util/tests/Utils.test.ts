/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import _ from 'lodash';
import { GraphQL, DefaultUser } from '@kogito-apps/common';
import {
  getTaskEndpointSecurityParams,
  getTaskSchemaEndPoint,
  resolveTaskPriority
} from '../Utils';
import UserTaskInstance = GraphQL.UserTaskInstance;

const userTaskInstance: UserTaskInstance = {
  id: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
  description: null,
  name: 'VisaApplication',
  referenceName: 'Apply for visa',
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
  started: '2020-02-19T11:11:56.282Z',
  excludedUsers: [],
  potentialGroups: [],
  potentialUsers: [],
  inputs:
    '{"Skippable":"true","trip":{"city":"Boston","country":"US","begin":"2020-02-19T23:00:00.000+01:00","end":"2020-02-26T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","traveller":{"firstName":"Rachel","lastName":"White","email":"rwhite@gorle.com","nationality":"Polish","address":{"street":"Cabalone","city":"Zerf","zipCode":"765756","country":"Poland"}},"Priority":"1"}',
  outputs: '{}',
  lastUpdate: '2020-02-19T11:11:56.282Z',
  endpoint:
    'http://localhost:8080/travels/9ae7ce3b-d49c-4f35-b843-8ac3d22fa427/VisaApplication/45a73767-5da3-49bf-9c40-d533c3e77ef3'
};

describe('Utils testing', () => {
  it('Test security params', () => {
    const result = getTaskEndpointSecurityParams(
      new DefaultUser('name', ['group1', 'group2'])
    );

    expect(result).toStrictEqual('user=name&group=group1&group=group2');
  });

  it('Test security params without groups', () => {
    const result = getTaskEndpointSecurityParams(new DefaultUser('name', []));

    expect(result).toStrictEqual('user=name');
  });

  it('Test task schema endpoint', () => {
    const result = getTaskSchemaEndPoint(
      userTaskInstance,
      new DefaultUser('name', ['group1', 'group2'])
    );

    expect(result).toStrictEqual(
      userTaskInstance.endpoint + '/schema?user=name&group=group1&group=group2'
    );
  });

  it('Test completed task schema endpoint', () => {
    const task = _.cloneDeep(userTaskInstance);
    task.completed = true;

    const result = getTaskSchemaEndPoint(
      task,
      new DefaultUser('name', ['group1', 'group2'])
    );

    expect(result).toStrictEqual(
      'http://localhost:8080/travels/VisaApplication/schema'
    );
  });

  it('Test resolve task priority', () => {
    expect(resolveTaskPriority('0')).toStrictEqual('0 - High');
    expect(resolveTaskPriority('5')).toStrictEqual('5 - Medium');
    expect(resolveTaskPriority('10')).toStrictEqual('10 - Low');
    expect(resolveTaskPriority('1')).toStrictEqual('1');
    expect(resolveTaskPriority()).toStrictEqual('-');
    expect(resolveTaskPriority('')).toStrictEqual('-');
  });
});
