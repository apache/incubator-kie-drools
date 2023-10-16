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
import React from 'react';
import _ from 'lodash';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import {
  EmptyState,
  EmptyStateSecondaryActions
} from '@patternfly/react-core/dist/js/components/EmptyState';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import EmptyTaskForm from '../EmptyTaskForm';
import { ApplyForVisaForm } from '../../utils/tests/mocks/ApplyForVisa';
import { mount } from 'enzyme';

const testTask: UserTaskInstance = {
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

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-core', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-core'), {
    Button: () => <MockedComponent />,
    EmptyStateIcon: () => <MockedComponent />
  })
);

let doSubmit;

const getEmptyTaskFormWrapper = (
  userTask: UserTaskInstance,
  schema: Record<string, any>,
  enabled: boolean
) => {
  doSubmit = jest.fn();

  return mount(
    <EmptyTaskForm
      userTask={userTask}
      formSchema={schema}
      enabled={enabled}
      submit={doSubmit}
    />
  ).find('EmptyTaskForm');
};

describe('EmptyTaskForm Test', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('Empty form rendering', async () => {
    const wrapper = getEmptyTaskFormWrapper(
      _.cloneDeep(testTask),
      _.cloneDeep(ApplyForVisaForm),
      true
    );

    expect(wrapper).toMatchSnapshot();

    const emptyState = wrapper.find(EmptyState);

    expect(emptyState.exists()).toBeTruthy();

    const actions = wrapper.find(EmptyStateSecondaryActions);
    expect(actions.exists()).toBeTruthy();

    const buttons = actions.find(Button);
    expect(buttons).toHaveLength(2);

    expect(buttons.get(0).props.isDisabled).toBeFalsy();
    expect(buttons.get(1).props.isDisabled).toBeFalsy();
  });

  it('Empty form completed task rendering', async () => {
    const task = _.cloneDeep(testTask);

    task.state = 'Completed';
    task.completed = task.started;

    const wrapper = getEmptyTaskFormWrapper(
      task,
      _.cloneDeep(ApplyForVisaForm),
      true
    );

    expect(wrapper).toMatchSnapshot();

    const emptyState = wrapper.find(EmptyState);

    expect(emptyState.exists()).toBeTruthy();

    const actions = wrapper.find(EmptyStateSecondaryActions);

    expect(actions.exists()).toBeFalsy();
  });

  it('Empty form without actions rendering', async () => {
    const schema = _.cloneDeep(ApplyForVisaForm);

    _.unset(schema, 'phases');

    const wrapper = getEmptyTaskFormWrapper(
      _.cloneDeep(testTask),
      schema,
      true
    );

    const emptyState = wrapper.find(EmptyState);

    expect(emptyState.exists()).toBeTruthy();

    const actions = wrapper.find(EmptyStateSecondaryActions);

    expect(actions.exists()).toBeFalsy();
  });

  it('Empty form disabled actions rendering', async () => {
    const wrapper = getEmptyTaskFormWrapper(
      _.cloneDeep(testTask),
      _.cloneDeep(ApplyForVisaForm),
      false
    );

    const emptyState = wrapper.find(EmptyState);

    expect(emptyState.exists()).toBeTruthy();

    const actions = wrapper.find(EmptyStateSecondaryActions);
    expect(actions.exists()).toBeTruthy();

    const buttons = actions.find(Button);
    expect(buttons).toHaveLength(2);

    expect(buttons.get(0).props.isDisabled).toBeTruthy();
    expect(buttons.get(1).props.isDisabled).toBeTruthy();
  });

  it('Empty form actions', async () => {
    const wrapper = getEmptyTaskFormWrapper(
      _.cloneDeep(testTask),
      _.cloneDeep(ApplyForVisaForm),
      true
    );

    const buttons = wrapper.find(Button);
    expect(buttons).toHaveLength(2);

    const completeButton = wrapper.findWhere(
      (node) => node.key() === 'submit-complete'
    );
    completeButton.props().onClick();
    expect(doSubmit).toHaveBeenLastCalledWith('complete');

    const releaseButton = wrapper.findWhere(
      (node) => node.key() === 'submit-release'
    );
    releaseButton.props().onClick();
    expect(doSubmit).toHaveBeenLastCalledWith('release');
  });
});
