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
import { getWrapper, GraphQL } from '@kogito-apps/common';

import UserTaskInstance = GraphQL.UserTaskInstance;
import TaskDetails from '../TaskDetails';
import { ReactWrapper } from 'enzyme';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('../../../Atoms/TaskState/TaskState');

jest.mock('@patternfly/react-core', () => ({
  ...jest.requireActual('@patternfly/react-core'),
  Tooltip: () => {
    return <MockedComponent />;
  }
}));

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

const getFormGroup = (wrapper: ReactWrapper, fieldId: string): ReactWrapper => {
  return wrapper.findWhere(element => element.prop('fieldId') === fieldId);
};

Date.now = jest.fn(() => 1601881200000); // UTC 2020-10-05 07:00:00
describe('TaskDetails testing', () => {
  it('Snapshot testing', () => {
    const wrapper = getWrapper(
      <TaskDetails userTaskInstance={userTaskInstance} />,
      'TaskDetails'
    );

    expect(wrapper).toMatchSnapshot();

    expect(getFormGroup(wrapper, 'name').exists()).toBeTruthy();
    expect(getFormGroup(wrapper, 'description').exists()).toBeFalsy();
    expect(getFormGroup(wrapper, 'id').exists()).toBeTruthy();
    expect(getFormGroup(wrapper, 'state').exists()).toBeTruthy();
    const owner: ReactWrapper = getFormGroup(wrapper, 'owner');
    expect(owner.exists()).toBeTruthy();
    expect(owner.html()).toContain('-');
    expect(getFormGroup(wrapper, 'state').exists()).toBeTruthy();
    expect(getFormGroup(wrapper, 'completed').exists()).toBeFalsy();
    expect(getFormGroup(wrapper, 'process').exists()).toBeTruthy();
    expect(getFormGroup(wrapper, 'processInstance').exists()).toBeTruthy();
    expect(getFormGroup(wrapper, 'lastUpdate').exists()).toBeTruthy();
  });

  it('Snapshot testing with description', () => {
    const wrapper = getWrapper(
      <TaskDetails
        userTaskInstance={{
          ...{ ...userTaskInstance, description: 'This is a description' }
        }}
      />,
      'TaskDetails'
    );

    expect(wrapper).toMatchSnapshot();

    const description = getFormGroup(wrapper, 'description');
    expect(description.exists()).toBeTruthy();
    expect(description.html()).toContain('This is a description');
  });

  it('Snapshot testing with owner', () => {
    const wrapper = getWrapper(
      <TaskDetails
        userTaskInstance={{
          ...{ ...userTaskInstance, actualOwner: 'John Snow' }
        }}
      />,
      'TaskDetails'
    );

    expect(wrapper).toMatchSnapshot();

    const owner = getFormGroup(wrapper, 'owner');
    expect(owner.exists()).toBeTruthy();
    expect(owner.html()).toContain('John Snow');
  });

  it('Snapshot testing with completed task', () => {
    const wrapper = getWrapper(
      <TaskDetails
        userTaskInstance={{
          ...{
            ...userTaskInstance,
            state: 'completed',
            completed: '2020-02-19T11:11:56.282Z'
          }
        }}
      />,
      'TaskDetails'
    );

    expect(wrapper).toMatchSnapshot();

    const completed = getFormGroup(wrapper, 'completed');
    expect(completed.exists()).toBeTruthy();
  });

  it('Snapshot testing with potential groups and potential user', () => {
    const wrapper = getWrapper(
      <TaskDetails
        userTaskInstance={{
          ...{
            ...userTaskInstance,
            potentialGroups: ['group1', 'group2'],
            potentialUsers: ['john', 'mary']
          }
        }}
      />,
      'TaskDetails'
    );
    expect(wrapper).toMatchSnapshot();
    const potentialGroups = getFormGroup(wrapper, 'potential_groups');
    const potentialUsers = getFormGroup(wrapper, 'potential_users');
    expect(potentialGroups.exists()).toBeTruthy();
    expect(
      potentialGroups
        .find('p')
        .children()
        .contains('group1, group2')
    ).toBeTruthy();
    expect(potentialUsers.exists()).toBeTruthy();
    expect(
      potentialUsers
        .find('p')
        .children()
        .contains('john, mary')
    ).toBeTruthy();
  });
});
