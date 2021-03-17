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
import wait from 'waait';
import {
  getWrapperAsync,
  GraphQL,
  KogitoAppContextProvider,
  UserContext,
  KogitoEmptyState
} from '@kogito-apps/common';
import UserTaskInstance = GraphQL.UserTaskInstance;
import TaskForm from '../TaskForm';
import ApplyForVisaForm from '../../../../util/tests/mocks/ApplyForVisa';
import { getTaskSchemaEndPoint } from '../../../../util/Utils';
import TaskConsoleContextProvider from '../../../../context/TaskConsoleContext/TaskConsoleContextProvider';
import { TestingUserContext } from '../../../../util/tests/utils/TestingUserContext';
import TaskConsoleContext, {
  ITaskConsoleContext,
  TaskConsoleContextImpl
} from '../../../../context/TaskConsoleContext/TaskConsoleContext';
import TaskFormRenderer from '../../TaskFormRenderer/TaskFormRenderer';
import EmptyTaskForm from '../../EmptyTaskForm/EmptyTaskForm';

jest.mock('../../TaskFormRenderer/TaskFormRenderer');
jest.mock('../../EmptyTaskForm/EmptyTaskForm');
jest.mock('../../../Atoms/FormNotification/FormNotification');

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('axios');
jest.mock('@kogito-apps/common', () => ({
  ...jest.requireActual('@kogito-apps/common'),
  KogitoEmptyState: () => {
    return <MockedComponent />;
  },
  KogitoSpinner: () => {
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

const getWrapper = async (
  userTaskInstance: GraphQL.UserTaskInstance,
  formSubmitSuccessCallback?: () => void,
  formSubmitErrorCallback?: () => void
) => {
  let wrapper;

  await act(async () => {
    wrapper = await getWrapperAsync(
      <KogitoAppContextProvider userContext={userContext}>
        <TaskConsoleContextProvider>
          <TaskForm
            userTaskInstance={userTaskInstance}
            onSubmitSuccess={formSubmitSuccessCallback}
            onSubmitError={formSubmitErrorCallback}
          />
        </TaskConsoleContextProvider>
      </KogitoAppContextProvider>,
      'TaskForm'
    );
    await wait();
  });

  return (wrapper = wrapper.update().find(TaskForm));
};

let userContext: UserContext;

describe('TaskForm Test', () => {
  beforeEach(() => {
    userContext = new TestingUserContext();
  });

  it('Test rendering form from task in context', async () => {
    mockedAxios.get.mockResolvedValue({
      status: 200,
      data: _.cloneDeep(ApplyForVisaForm)
    });
    const context: ITaskConsoleContext<UserTaskInstance> = new TaskConsoleContextImpl();
    context.setActiveItem(userTaskInstance);
    const wrapper = await getWrapperAsync(
      <KogitoAppContextProvider userContext={new TestingUserContext()}>
        <TaskConsoleContext.Provider value={context}>
          <TaskForm onSubmitSuccess={jest.fn()} onSubmitError={jest.fn()} />
        </TaskConsoleContext.Provider>
      </KogitoAppContextProvider>,
      'TaskForm'
    );

    wrapper.update();

    expect(wrapper).toMatchSnapshot();

    const renderer = wrapper.find(TaskFormRenderer);

    expect(renderer.exists()).toBeTruthy();

    expect(renderer.props().task).toBe(userTaskInstance);
    expect(renderer.props().formSchema).toStrictEqual(ApplyForVisaForm);
    expect(renderer.props().onSubmitSuccess).not.toBeNull();
    expect(renderer.props().onSubmitError).not.toBeNull();
  });

  it('Test rendering form', async () => {
    mockedAxios.get.mockResolvedValue({
      status: 200,
      data: _.cloneDeep(ApplyForVisaForm)
    });
    const wrapper = await getWrapper(userTaskInstance);

    wrapper.update();

    expect(wrapper).toMatchSnapshot();

    const renderer = wrapper.find(TaskFormRenderer);

    expect(renderer.exists()).toBeTruthy();

    expect(renderer.props().task).toBe(userTaskInstance);
    expect(renderer.props().formSchema).toStrictEqual(ApplyForVisaForm);
    expect(renderer.props().onSubmitSuccess).not.toBeNull();
    expect(renderer.props().onSubmitError).not.toBeNull();
  });

  it('Empty form rendering', async () => {
    const formSchema = _.cloneDeep(ApplyForVisaForm);
    delete formSchema.properties;

    mockedAxios.get.mockResolvedValue({
      status: 200,
      data: formSchema
    });

    const wrapper = await getWrapper(userTaskInstance);

    wrapper.update();

    expect(wrapper).toMatchSnapshot();

    const renderer = wrapper.find(TaskFormRenderer);

    expect(renderer.exists()).toBeFalsy();

    const emptyForm = wrapper.find(EmptyTaskForm);

    expect(emptyForm.exists()).toBeTruthy();
  });

  it('Test rendering form with HTTP error', async () => {
    mockedAxios.get.mockResolvedValue({
      status: 500
    });

    let wrapper = await getWrapper(userTaskInstance);

    wrapper = wrapper.update().find(TaskForm);

    expect(wrapper).toMatchSnapshot();

    expect(wrapper.find(TaskFormRenderer).exists()).toBeFalsy();
    expect(wrapper.find(KogitoEmptyState).exists()).toBeTruthy();
  });

  it('Test rendering form with JS error', async () => {
    mockedAxios.get.mockImplementationOnce(() =>
      Promise.reject(new Error('This is an error loading the form'))
    );

    const wrapper = await getWrapper(userTaskInstance, jest.fn(), jest.fn());

    expect(wrapper).toMatchSnapshot();

    expect(wrapper.find(TaskFormRenderer).exists()).toBeFalsy();
    expect(wrapper.find(KogitoEmptyState).exists()).toBeTruthy();
  });

  it('Test render completed task', async () => {
    const task = _.cloneDeep(userTaskInstance);
    task.completed = true;

    mockedAxios.get.mockResolvedValue({
      status: 200,
      data: _.cloneDeep(ApplyForVisaForm)
    });

    const axiosCalls = mockedAxios.get.mock.calls.length;

    const wrapper = await getWrapper(task);

    wrapper.update();

    expect(wrapper).toMatchSnapshot();

    expect(mockedAxios.get).toBeCalledTimes(axiosCalls + 1);

    const requestParams = mockedAxios.get.mock.calls[axiosCalls];

    const requestURL = requestParams[0];

    expect(requestURL).not.toBe(task.endpoint + '/schema');
    expect(requestURL).toEqual(
      getTaskSchemaEndPoint(task, userContext.getCurrentUser())
    );
  });

  it('Test submit success', async () => {
    const formSubmitSuccessCallback = jest.fn();
    const formSubmitErrorCallback = jest.fn();

    mockedAxios.get.mockResolvedValue({
      status: 200,
      data: _.cloneDeep(ApplyForVisaForm)
    });

    mockedAxios.post.mockResolvedValue({
      status: 200,
      data: _.cloneDeep(ApplyForVisaForm)
    });

    let wrapper = await getWrapper(
      userTaskInstance,
      formSubmitSuccessCallback,
      formSubmitErrorCallback
    );

    wrapper = wrapper.update().find(TaskForm);

    expect(wrapper).toMatchSnapshot();

    const renderer = wrapper.find(TaskFormRenderer);

    expect(renderer.exists()).toBeTruthy();

    // since the TaskFormRenderer is mocked we are forcing the form submit callback
    await act(async () => {
      renderer.props().onSubmitSuccess('phase');
    });

    wrapper = wrapper.update().find(TaskForm);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find(TaskFormRenderer).exists()).toBeTruthy();

    expect(formSubmitSuccessCallback).toBeCalledWith('phase');
  });

  it('Test submit error', async () => {
    const formSubmitSuccessCallback = jest.fn();
    const formSubmitErrorCallback = jest.fn();

    mockedAxios.get.mockResolvedValue({
      status: 200,
      data: _.cloneDeep(ApplyForVisaForm)
    });

    let wrapper = await getWrapper(
      userTaskInstance,
      formSubmitSuccessCallback,
      formSubmitErrorCallback
    );

    wrapper = wrapper.update().find(TaskForm);

    expect(wrapper).toMatchSnapshot();

    const renderer = wrapper.find(TaskFormRenderer);

    expect(renderer.exists()).toBeTruthy();

    // since the FormRenderer is mocked we are forcing the form submit callback
    act(() => {
      renderer.props().onSubmitError('phase', 'Extra info!');
    });

    wrapper = wrapper.update().find(TaskForm);

    expect(wrapper).toMatchSnapshot();

    expect(formSubmitErrorCallback).toBeCalledWith('phase', 'Extra info!');
  });
});
