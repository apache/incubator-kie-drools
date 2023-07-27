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
import { act } from 'react-dom/test-utils';
import _ from 'lodash';
import TaskFormRenderer from '../TaskFormRenderer';
import { mount } from 'enzyme';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import { ApplyForVisaForm } from '../../utils/tests/mocks/ApplyForVisa';
import { FormRenderer } from '@kogito-apps/components-common/dist/components/FormRenderer';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@kogito-apps/components-common/dist/components/FormRenderer', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/components-common'), {
    FormRenderer: () => {
      return <MockedComponent />;
    }
  })
);

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

const formData = {
  trip: {
    city: 'Boston',
    country: 'US',
    begin: '2020-02-19T23:00:00.000+01:00',
    end: '2020-02-26T23:00:00.000+01:00',
    visaRequired: true
  },
  traveller: {
    firstName: 'Rachel',
    lastName: 'White',
    email: 'rwhite@gorle.com',
    nationality: 'Polish',
    address: {
      street: 'Cabalone',
      city: 'Zerf',
      zipCode: '765756',
      country: 'Poland'
    }
  },
  visaApplication: {
    firstName: 'Rachel',
    lastName: 'White',
    city: 'Boston',
    country: 'US',
    duration: '3',
    passportNumber: '123456789',
    nationality: 'Polish'
  }
};

let doSubmit;

const getTaskFormRendererWrapper = (
  userTask: UserTaskInstance,
  formSchema: Record<string, any>,
  enabled: boolean,
  formData?: any
) => {
  return mount(
    <TaskFormRenderer
      formSchema={formSchema}
      userTask={userTask}
      formData={formData}
      enabled={enabled}
      submit={doSubmit}
    />
  ).find('TaskFormRenderer');
};

describe('TaskFormRenderer Test', () => {
  beforeEach(() => {
    doSubmit = jest.fn();
    jest.clearAllMocks();
  });

  it('Form rendering', () => {
    const wrapper = getTaskFormRendererWrapper(
      userTaskInstance,
      _.cloneDeep(ApplyForVisaForm),
      true
    );

    expect(wrapper).toMatchSnapshot();

    const renderer = wrapper.find(FormRenderer);

    expect(renderer.exists()).toBeTruthy();

    expect(renderer.props().model).toStrictEqual(
      JSON.parse(userTaskInstance.inputs)
    );
    expect(renderer.props().readOnly).toBeFalsy();
  });

  it('Form rendering disabled with data', () => {
    const wrapper = getTaskFormRendererWrapper(
      userTaskInstance,
      _.cloneDeep(ApplyForVisaForm),
      false,
      formData
    );

    const renderer = wrapper.find(FormRenderer);

    expect(renderer.exists()).toBeTruthy();
    expect(renderer.props().model).toStrictEqual(formData);
    expect(renderer.props().readOnly).toBeTruthy();
  });

  it('Form rendering disabled - completed task', () => {
    const userTask = _.cloneDeep(userTaskInstance);
    userTask.state = 'Completed';
    userTask.completed = userTask.started;

    const wrapper = getTaskFormRendererWrapper(
      userTask,
      _.cloneDeep(ApplyForVisaForm),
      true,
      formData
    );

    const renderer = wrapper.find(FormRenderer);

    expect(renderer.exists()).toBeTruthy();
    expect(renderer.props().model).toStrictEqual(formData);
    expect(renderer.props().readOnly).toBeTruthy();
  });

  it('Form rendering disabled - no phases', () => {
    const formSchema = _.cloneDeep(ApplyForVisaForm);
    _.unset(formSchema, 'phases');

    const wrapper = getTaskFormRendererWrapper(
      userTaskInstance,
      formSchema,
      true,
      formData
    );

    const renderer = wrapper.find(FormRenderer);

    expect(renderer.exists()).toBeTruthy();
    expect(renderer.props().model).toStrictEqual(formData);
    expect(renderer.props().readOnly).toBeTruthy();
  });

  it('Form submit callbacks', () => {
    let wrapper = getTaskFormRendererWrapper(
      userTaskInstance,
      _.cloneDeep(ApplyForVisaForm),
      true
    );

    let renderer = wrapper.find(FormRenderer);

    expect(renderer.exists()).toBeTruthy();
    expect(renderer.props().readOnly).toBeFalsy();

    const actions = renderer.props().formActions;

    act(() => {
      actions[0].execute();
    });

    wrapper = wrapper.update();

    renderer = wrapper.find(TaskFormRenderer).find(FormRenderer);
    renderer.props().onSubmit(formData);

    expect(doSubmit).toHaveBeenCalledWith('complete', formData);
  });
});
