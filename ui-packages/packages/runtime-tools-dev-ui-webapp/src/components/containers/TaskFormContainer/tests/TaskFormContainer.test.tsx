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

import React from 'react';
import { mount } from 'enzyme';
import TaskFormContainer from '../TaskFormContainer';
import { TaskFormGatewayApi } from '../../../../channel/TaskForms';
import * as TaskFormContext from '../../../../channel/TaskForms/TaskFormContext';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import { DefaultUser } from '@kogito-apps/consoles-common';

const testUserTask: UserTaskInstance = {
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

const mockFunc = () => new DefaultUser('jon snow', ['hero']);

const MockTaskFormGatewayApi = jest.fn<TaskFormGatewayApi, []>(() => ({
  getTaskFormSchema: jest.fn(),
  doSubmit: jest.fn()
}));

jest
  .spyOn(TaskFormContext, 'useTaskFormGatewayApi')
  .mockImplementation(() => new MockTaskFormGatewayApi(mockFunc));

describe('TaskFormContainer tests', () => {
  it('Snapshot', () => {
    const onSubmit = jest.fn();
    const onFailure = jest.fn();
    const wrapper = mount(
      <TaskFormContainer
        userTask={testUserTask}
        onSubmitSuccess={onSubmit}
        onSubmitError={onFailure}
      />
    );

    expect(wrapper).toMatchSnapshot();

    const forwardRef = wrapper.childAt(0);
    expect(forwardRef.props().driver).not.toBeNull();
    expect(forwardRef.props().targetOrigin).toBe('*');
  });
});
