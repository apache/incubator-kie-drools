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
import axios from 'axios';
import { act } from 'react-dom/test-utils';
import _ from 'lodash';
import {
  DefaultUser,
  getWrapperAsync,
  GraphQL,
  User
} from '@kogito-apps/common';
import UserTaskInstance = GraphQL.UserTaskInstance;
import TaskForm from '../TaskForm';
import ApplyForVisaForm from '../../../../util/tests/mocks/ApplyForVisa';
import FormRenderer from '../../../Molecules/FormRenderer/FormRenderer';
import FormNotification from '../../../Atoms/FormNotification/FormNotification';
import { TaskFormSubmitHandler } from '../../../../util/uniforms/TaskFormSubmitHandler/TaskFormSubmitHandler';
import { getTaskSchemaEndPoint } from '../../../../util/Utils';
import TaskConsoleContextProvider from '../../../../context/TaskConsoleContext/TaskConsoleContextProvider';

jest.mock('../../../Molecules/FormRenderer/FormRenderer');
jest.mock('../../../Atoms/FormNotification/FormNotification');

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('axios');
jest.mock('@kogito-apps/common', () => ({
  ...jest.requireActual('@kogito-apps/common'),
  KogitoEmptyState: () => {
    return <MockedComponent />;
  }
}));

const mockedAxios = axios as jest.Mocked<typeof axios>;

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

const testUser: User = new DefaultUser('test', ['group1', 'group2']);

enum Mode {
  SUCCESS,
  ERROR,
  ERROR_WITH_MESSAGE
}

const testSubmitCallbacks = async (mode: Mode) => {
  const formSubmitSuccessCallback = jest.fn();
  const formSubmitErrorCallback = jest.fn();

  mockedAxios.get.mockResolvedValue({
    status: 200,
    data: _.cloneDeep(ApplyForVisaForm)
  });

  let wrapper = await getWrapperAsync(
    <TaskConsoleContextProvider user={testUser}>
      <TaskForm
        userTaskInstance={userTaskInstance}
        successCallback={formSubmitSuccessCallback}
        errorCallback={formSubmitErrorCallback}
      />
    </TaskConsoleContextProvider>,
    'TaskForm'
  );

  wrapper = wrapper.update().find(TaskForm);

  expect(wrapper).toMatchSnapshot();

  const renderer = wrapper.find(FormRenderer);

  expect(renderer.getElement()).not.toBeNull();

  // since the FormRenderer is mocked we are forcing the form submit callback
  const callback =
    mode === Mode.SUCCESS
      ? renderer.getElement().props.formSubmitHandler.successCallback
      : renderer.getElement().props.formSubmitHandler.errorCallback;

  expect(callback).not.toBeNull();

  act(() => {
    if (mode === Mode.ERROR_WITH_MESSAGE) {
      callback('phase', 'Extra error info.');
    } else {
      callback('phase');
    }
  });

  wrapper = wrapper.update().find(TaskForm);

  expect(wrapper).toMatchSnapshot();

  let notificationComponent = wrapper.find(FormNotification);

  let expectedCallback;
  let unExpectedCallback;
  let expectedMessage;

  switch (mode) {
    case Mode.SUCCESS: {
      expectedMessage =
        "Task 'Apply for visa' successfully transitioned to phase 'phase'.";
      expectedCallback = formSubmitSuccessCallback;
      unExpectedCallback = formSubmitErrorCallback;
      break;
    }
    case Mode.ERROR: {
      expectedMessage =
        "Task 'Apply for visa' couldn't transition to phase 'phase'.";
      expectedCallback = formSubmitErrorCallback;
      unExpectedCallback = formSubmitSuccessCallback;
      break;
    }
    case Mode.ERROR_WITH_MESSAGE: {
      expectedMessage =
        "Task 'Apply for visa' couldn't transition to phase 'phase'.";
      expectedCallback = formSubmitErrorCallback;
      unExpectedCallback = formSubmitSuccessCallback;
    }
  }

  expect(notificationComponent.exists()).toBeTruthy();

  const notification = notificationComponent.props().notification;

  expect(notification.message).toStrictEqual(expectedMessage);

  expect(notification.close).not.toBeNull();
  expect(notification.customAction).not.toBeNull();

  if (mode === Mode.ERROR_WITH_MESSAGE) {
    expect(notification.details).toStrictEqual('Extra error info.');
  }

  act(() => {
    notification.customAction.onClick();
  });

  expect(expectedCallback).toBeCalled();
  expect(unExpectedCallback).not.toBeCalled();

  wrapper = wrapper.update().find(TaskForm);

  notificationComponent = wrapper.find(FormNotification);

  expect(notificationComponent.exists()).toBeFalsy();
};

describe('TaskForm Test', () => {
  it('Test rendering form', async () => {
    mockedAxios.get.mockResolvedValue({
      status: 200,
      data: _.cloneDeep(ApplyForVisaForm)
    });
    const wrapper = await getWrapperAsync(
      <TaskConsoleContextProvider user={testUser}>
        <TaskForm userTaskInstance={userTaskInstance} />
      </TaskConsoleContextProvider>,
      'TaskForm'
    );

    wrapper.update();

    expect(wrapper).toMatchSnapshot();

    const renderer = wrapper.find(FormRenderer);

    expect(renderer).not.toBeNull();

    expect(renderer.props().formSubmitHandler).toBeInstanceOf(
      TaskFormSubmitHandler
    );
    expect(renderer.props().model).toStrictEqual(
      JSON.parse(userTaskInstance.inputs)
    );
  });

  it('Test rendering form with error', async () => {
    mockedAxios.get.mockResolvedValue({
      status: 500
    });

    let wrapper = await getWrapperAsync(
      <TaskConsoleContextProvider user={testUser}>
        <TaskForm userTaskInstance={userTaskInstance} />
      </TaskConsoleContextProvider>,
      'TaskForm'
    );

    wrapper = wrapper.update().find(TaskForm);

    expect(wrapper).toMatchSnapshot();
  });

  it('Test successful callback', async () => {
    await testSubmitCallbacks(Mode.SUCCESS);
  });

  it('Test unsuccessful callback', async () => {
    await testSubmitCallbacks(Mode.ERROR);
  });

  it('Test unsuccessful callback with extra message', async () => {
    await testSubmitCallbacks(Mode.ERROR_WITH_MESSAGE);
  });

  it('Test render completed task', async () => {
    const task = _.cloneDeep(userTaskInstance);
    task.completed = true;

    mockedAxios.get.mockResolvedValue({
      status: 200,
      data: _.cloneDeep(ApplyForVisaForm)
    });

    const axiosCalls = mockedAxios.get.mock.calls.length;

    const wrapper = await getWrapperAsync(
      <TaskConsoleContextProvider user={testUser}>
        <TaskForm userTaskInstance={task} />
      </TaskConsoleContextProvider>,
      'TaskForm'
    );

    wrapper.update();

    expect(wrapper).toMatchSnapshot();

    expect(mockedAxios.get).toBeCalledTimes(axiosCalls + 1);

    const requestParams = mockedAxios.get.mock.calls[axiosCalls];

    const requestURL = requestParams[0];

    expect(requestURL).not.toBe(task.endpoint + '/schema');
    expect(requestURL).toEqual(getTaskSchemaEndPoint(task, testUser));
  });

  it('Test submit close notification', async () => {
    const formSubmitSuccessCallback = jest.fn();
    const formSubmitErrorCallback = jest.fn();

    mockedAxios.get.mockResolvedValue({
      status: 200,
      data: _.cloneDeep(ApplyForVisaForm)
    });

    let wrapper = await getWrapperAsync(
      <TaskConsoleContextProvider user={testUser}>
        <TaskForm
          userTaskInstance={userTaskInstance}
          successCallback={formSubmitSuccessCallback}
          errorCallback={formSubmitErrorCallback}
        />
      </TaskConsoleContextProvider>,
      'TaskForm'
    );

    wrapper = wrapper.update().find(TaskForm);

    expect(wrapper).toMatchSnapshot();

    const renderer = wrapper.find(FormRenderer);

    expect(renderer.getElement()).not.toBeNull();

    // since the FormRenderer is mocked we are forcing the form submit callback
    act(() => {
      renderer.getElement().props.formSubmitHandler.successCallback('phase');
    });

    wrapper = wrapper.update().find(TaskForm);

    expect(wrapper).toMatchSnapshot();

    let notificationComponent = wrapper.find(FormNotification);

    expect(notificationComponent.exists()).toBeTruthy();

    const notification = notificationComponent.props().notification;

    expect(notification.close).not.toBeNull();

    act(() => {
      notification.close();
    });

    wrapper = wrapper.update().find(TaskForm);

    notificationComponent = wrapper.find(FormNotification);

    expect(notificationComponent.exists()).toBeFalsy();
  });
});
