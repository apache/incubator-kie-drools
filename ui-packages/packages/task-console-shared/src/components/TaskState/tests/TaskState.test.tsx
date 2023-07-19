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
import { BanIcon } from '@patternfly/react-icons/dist/js/icons/ban-icon';
import { CheckCircleIcon } from '@patternfly/react-icons/dist/js/icons/check-circle-icon';
import { OnRunningIcon } from '@patternfly/react-icons/dist/js/icons/on-running-icon';
import _ from 'lodash';
import TaskState from '../TaskState';
import { Label } from '@patternfly/react-core/dist/js/components/Label';
import { mount } from 'enzyme';
import { UserTaskInstance } from '../../../types';

const userTask: UserTaskInstance = {
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

const DATE_FOR_TESTS = 1592000000000; // UTC Fri Jun 12 2020 22:13:20

jest.mock('@patternfly/react-icons/dist/js/icons/on-running-icon', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    OnRunningIcon: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock('@patternfly/react-icons/dist/js/icons/ban-icon', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    BanIcon: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock('@patternfly/react-icons/dist/js/icons/check-circle-icon', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    CheckCircleIcon: () => {
      return <MockedComponent />;
    }
  })
);

describe('TaskState', () => {
  it('Test show active task', () => {
    const wrapper = mount(<TaskState task={userTask} />).find('TaskState');

    expect(wrapper).toMatchSnapshot();

    const label = wrapper.find(Label);

    expect(label.exists()).toBeFalsy();

    const icon = wrapper.find(OnRunningIcon);

    expect(icon.exists()).toBeTruthy();
  });

  it('Test show aborted task', () => {
    const task = _.clone(userTask);
    task.state = 'Aborted';

    const wrapper = mount(<TaskState task={task} />).find('TaskState');

    expect(wrapper).toMatchSnapshot();

    const label = wrapper.find(Label);

    expect(label.exists()).toBeFalsy();

    const icon = wrapper.find(BanIcon);

    expect(icon.exists()).toBeTruthy();
  });

  it('Test show completed task', () => {
    const task = _.clone(userTask);
    task.state = 'Completed';
    task.completed = new Date(DATE_FOR_TESTS);

    const wrapper = mount(<TaskState task={task} />).find('TaskState');

    expect(wrapper).toMatchSnapshot();

    const label = wrapper.find(Label);

    expect(label.exists()).toBeFalsy();

    const icon = wrapper.find(CheckCircleIcon);

    expect(icon.exists()).toBeTruthy();
  });

  it('Test show active task in label', () => {
    const wrapper = mount(<TaskState task={userTask} variant={'label'} />).find(
      'TaskState'
    );

    expect(wrapper).toMatchSnapshot();

    const label = wrapper.find(Label);

    expect(label.exists()).toBeTruthy();
    expect(label.props().color).toBe('blue');
  });

  it('Test show aborted task in label', () => {
    const task = _.clone(userTask);
    task.state = 'Aborted';

    const wrapper = mount(<TaskState task={task} variant={'label'} />).find(
      'TaskState'
    );

    expect(wrapper).toMatchSnapshot();

    const label = wrapper.find(Label);

    expect(label.exists()).toBeTruthy();
    expect(label.props().color).toBe('red');

    const icon = wrapper.find(BanIcon);

    expect(icon.exists()).toBeTruthy();
  });

  it('Test show completed task in label', () => {
    const task = _.clone(userTask);
    task.state = 'Completed';
    task.completed = new Date(DATE_FOR_TESTS);

    const wrapper = mount(<TaskState task={task} variant={'label'} />).find(
      'TaskState'
    );

    expect(wrapper).toMatchSnapshot();

    const label = wrapper.find(Label);

    expect(label.exists()).toBeTruthy();
    expect(label.props().color).toBe('green');

    const icon = wrapper.find(CheckCircleIcon);

    expect(icon.exists()).toBeTruthy();
  });
});
