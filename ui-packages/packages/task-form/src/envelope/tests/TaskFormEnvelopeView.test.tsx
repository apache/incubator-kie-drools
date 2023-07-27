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
import wait from 'waait';
import { mount } from 'enzyme';
import { KogitoEmptyState } from '@kogito-apps/components-common/dist/components/KogitoEmptyState';
import { KogitoSpinner } from '@kogito-apps/components-common/dist/components/KogitoSpinner';
import {
  customForm,
  MockedMessageBusClientApi,
  taskForm__getCustomFormMock,
  taskForm__getTaskFormSchemaMock,
  testUserTask
} from './mocks/Mocks';
import TaskFormEnvelopeView, {
  TaskFormEnvelopeViewApi
} from '../TaskFormEnvelopeView';

import TaskForm from '../components/TaskForm/TaskForm';
import CustomTaskFormDisplayer from '../components/CustomTaskFormDisplayer/CustomTaskFormDisplayer';
import { ApplyForVisaForm } from '../components/utils/tests/mocks/ApplyForVisa';
import { TaskFormInitArgs } from '../../api';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};
jest.mock(
  '@kogito-apps/components-common/dist/components/KogitoEmptyState',
  () =>
    Object.assign({}, jest.requireActual('@kogito-apps/components-common'), {
      KogitoEmptyState: () => {
        return <MockedComponent />;
      }
    })
);

jest.mock('@kogito-apps/components-common/dist/components/KogitoSpinner', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/components-common'), {
    KogitoSpinner: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock('../components/TaskForm/TaskForm');
jest.mock('../components/CustomTaskFormDisplayer/CustomTaskFormDisplayer');

const initArgs: TaskFormInitArgs = {
  userTask: testUserTask,
  user: {
    id: 'test',
    groups: ['group1']
  }
};

describe('TaskFormEnvelopeView tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('Loading', () => {
    const channelApi = MockedMessageBusClientApi();
    const forwardRef = React.createRef<TaskFormEnvelopeViewApi>();

    const wrapper = mount(
      <TaskFormEnvelopeView channelApi={channelApi} ref={forwardRef} />
    );

    expect(wrapper).toMatchSnapshot();

    const spinner = wrapper.find(KogitoSpinner);
    expect(spinner.exists()).toBeTruthy();

    const taskForm = wrapper.find(TaskForm);
    expect(taskForm.exists()).toBeFalsy();

    const customTaskForm = wrapper.find(CustomTaskFormDisplayer);
    expect(customTaskForm.exists()).toBeFalsy();

    const emptyState = wrapper.find(KogitoEmptyState);
    expect(emptyState.exists()).toBeFalsy();
  });

  it('Empty State', async () => {
    const channelApi = MockedMessageBusClientApi();
    const forwardRef = React.createRef<TaskFormEnvelopeViewApi>();

    taskForm__getTaskFormSchemaMock.mockReturnValue(
      Promise.reject('No task form schema')
    );
    taskForm__getCustomFormMock.mockReturnValue(
      Promise.reject('No custom form')
    );

    let wrapper = mount(
      <TaskFormEnvelopeView channelApi={channelApi} ref={forwardRef} />
    );

    await act(async () => {
      await forwardRef.current.initialize(initArgs);
      wait();
    });

    wrapper = wrapper.update();

    expect(wrapper).toMatchSnapshot();

    const spinner = wrapper.find(KogitoSpinner);
    expect(spinner.exists()).toBeFalsy();

    const taskForm = wrapper.find(TaskForm);
    expect(taskForm.exists()).toBeFalsy();

    const customTaskForm = wrapper.find(CustomTaskFormDisplayer);
    expect(customTaskForm.exists()).toBeFalsy();

    const emptyState = wrapper.find(KogitoEmptyState);
    expect(emptyState.exists()).toBeTruthy();
  });

  it('Task Form', async () => {
    const channelApi = MockedMessageBusClientApi();
    const forwardRef = React.createRef<TaskFormEnvelopeViewApi>();

    taskForm__getTaskFormSchemaMock.mockReturnValue(
      Promise.resolve(ApplyForVisaForm)
    );
    taskForm__getCustomFormMock.mockReturnValue(
      Promise.reject('No custom form')
    );

    let wrapper = mount(
      <TaskFormEnvelopeView channelApi={channelApi} ref={forwardRef} />
    );

    await act(async () => {
      await forwardRef.current.initialize(initArgs);
      wait();
    });

    wrapper = wrapper.update();

    expect(wrapper).toMatchSnapshot();

    const spinner = wrapper.find(KogitoSpinner);
    expect(spinner.exists()).toBeFalsy();

    const taskForm = wrapper.find(TaskForm);
    expect(taskForm.exists()).toBeTruthy();

    const customTaskForm = wrapper.find(CustomTaskFormDisplayer);
    expect(customTaskForm.exists()).toBeFalsy();

    const emptyState = wrapper.find(KogitoEmptyState);
    expect(emptyState.exists()).toBeFalsy();
  });

  it('Custom Task Form', async () => {
    const channelApi = MockedMessageBusClientApi();
    const forwardRef = React.createRef<TaskFormEnvelopeViewApi>();

    taskForm__getTaskFormSchemaMock.mockReturnValue(
      Promise.resolve(ApplyForVisaForm)
    );
    taskForm__getCustomFormMock.mockReturnValue(Promise.resolve(customForm));

    let wrapper = mount(
      <TaskFormEnvelopeView channelApi={channelApi} ref={forwardRef} />
    );

    await act(async () => {
      await forwardRef.current.initialize(initArgs);
      wait();
    });

    wrapper = wrapper.update();

    expect(wrapper).toMatchSnapshot();

    const spinner = wrapper.find(KogitoSpinner);
    expect(spinner.exists()).toBeFalsy();

    const taskForm = wrapper.find(TaskForm);
    expect(taskForm.exists()).toBeFalsy();

    const customTaskForm = wrapper.find(CustomTaskFormDisplayer);
    expect(customTaskForm.exists()).toBeTruthy();

    const emptyState = wrapper.find(KogitoEmptyState);
    expect(emptyState.exists()).toBeFalsy();
  });
});
