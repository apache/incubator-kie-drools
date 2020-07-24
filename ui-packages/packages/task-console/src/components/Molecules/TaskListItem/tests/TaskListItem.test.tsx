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

import React from 'react';
import TaskListItem from '../TaskListItem';
import { MockedProvider } from '@apollo/react-testing';
import { BrowserRouter, Link } from 'react-router-dom';
import { getWrapperAsync } from '@kogito-apps/common';
import {
  GetProcessInstanceByIdDocument,
  UserTaskInstance
} from '../../../../graphql/types';
import TaskConsoleContext, { DefaultContext } from '../../../../context/TaskConsoleContext/TaskConsoleContext';
import { TaskInfo } from '../../../../model/TaskInfo';

const userTaskInstance: UserTaskInstance = {
  id: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
  description: null,
  name: 'Apply for visa',
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
  referenceName: 'VisaApplication',
  lastUpdate: '2020-02-19T11:11:56.282Z'
};

const processInstance = {
  id: '9ae7ce3b-d49c-4f35-b843-8ac3d22fa427',
  processId: 'travels',
  parentProcessInstanceId: null,
  parentProcessInstance: null,
  processName: 'travels',
  roles: [],
  state: 'ACTIVE',
  rootProcessInstanceId: null,
  addons: ['infinispan-persistence', 'prometheus-monitoring'],
  start: '2020-02-19T11:11:56.244Z',
  end: null,
  endpoint: 'http://localhost:4000/travels'
};

const mocks = [
  {
    request: {
      query: GetProcessInstanceByIdDocument,
      variables: {
        id: '9ae7ce3b-d49c-4f35-b843-8ac3d22fa427'
      }
    },
    result: {
      data: {
        ProcessInstances: [processInstance]
      }
    }
  }
];

describe('TaskListItem test', () => {
  test("Snapshot test", async () => {

    const context = new DefaultContext<TaskInfo>();

    const wrapper = await getWrapperAsync(<MockedProvider mocks={mocks} addTypename={false}>
      <TaskConsoleContext.Provider value={context}>
        <BrowserRouter>
          <TaskListItem id={1} userTaskInstanceData={userTaskInstance} />
        </BrowserRouter>
      </TaskConsoleContext.Provider>
    </MockedProvider>, 'TaskListItem');

    wrapper.update();
    expect(wrapper).toMatchSnapshot();

    const openTaskLink = wrapper.find(Link);

    expect(openTaskLink.exists()).toBeTruthy();

    openTaskLink.simulate('click');

    const taskInfo: TaskInfo = context.getActiveItem();

    expect(taskInfo).not.toBeNull();
    expect(taskInfo.task).toBe(userTaskInstance);
    expect(taskInfo.processInstanceEndPoint).toBe(processInstance.endpoint);
  })
})