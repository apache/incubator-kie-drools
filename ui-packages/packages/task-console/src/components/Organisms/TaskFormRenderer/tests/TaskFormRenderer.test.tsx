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
import axios from 'axios';
import { act } from 'react-dom/test-utils';
import _ from 'lodash';
import wait from 'waait';
import {
  getWrapper,
  GraphQL,
  KogitoAppContextProvider,
  UserContext
} from '@kogito-apps/common';
import UserTaskInstance = GraphQL.UserTaskInstance;
import TaskFormRenderer from '../TaskFormRenderer';
import ApplyForVisaForm from '../../../../util/tests/mocks/ApplyForVisa';
import FormRenderer from '../../../Molecules/FormRenderer/FormRenderer';
import { TaskFormSubmitHandler } from '../../../../util/uniforms/TaskFormSubmitHandler/TaskFormSubmitHandler';
import { TestingUserContext } from '../../../../util/tests/utils/TestingUserContext';

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

const getTaskFormRendererWrapper = (
  userTaskInstance: GraphQL.UserTaskInstance,
  formSubmitSuccessCallback?: () => void,
  formSubmitErrorCallback?: () => void
) => {
  return getWrapper(
    <KogitoAppContextProvider userContext={userContext}>
      <TaskFormRenderer
        formSchema={_.cloneDeep(ApplyForVisaForm)}
        task={userTaskInstance}
        onSubmitSuccess={formSubmitSuccessCallback}
        onSubmitError={formSubmitErrorCallback}
      />
    </KogitoAppContextProvider>,
    'TaskFormRenderer'
  );
};

enum Mode {
  SUCCESS,
  ERROR,
  ERROR_WITH_MESSAGE
}

const testSubmitCallbacks = async (mode: Mode) => {
  const formSubmitSuccessCallback = jest.fn();
  const formSubmitErrorCallback = jest.fn();

  const wrapper = getTaskFormRendererWrapper(
    userTaskInstance,
    formSubmitSuccessCallback,
    formSubmitErrorCallback
  );

  const renderer = wrapper.find(FormRenderer);

  expect(renderer.exists()).toBeTruthy();

  // since the FormRenderer is mocked we are forcing the form submit callback
  const callback =
    mode === Mode.SUCCESS
      ? renderer.getElement().props.formSubmitHandler.taskformSubmit
          .successCallback
      : renderer.getElement().props.formSubmitHandler.taskformSubmit
          .errorCallback;

  expect(callback).not.toBeNull();

  act(() => {
    if (mode === Mode.ERROR_WITH_MESSAGE) {
      callback('phase', 'Extra error info.');
    } else {
      callback('phase');
    }
  });
};

let userContext: UserContext;

describe('TaskFormRenderer Test', () => {
  beforeEach(() => {
    userContext = new TestingUserContext();
  });

  it('Form rendering', () => {
    const wrapper = getTaskFormRendererWrapper(userTaskInstance);

    expect(wrapper).toMatchSnapshot();

    const renderer = wrapper.find(FormRenderer);

    expect(renderer.exists()).toBeTruthy();

    expect(renderer.props().formSubmitHandler).toBeInstanceOf(
      TaskFormSubmitHandler
    );
    expect(renderer.props().model).toStrictEqual(
      JSON.parse(userTaskInstance.inputs)
    );
  });

  it('Submit successful callback', async () => {
    await testSubmitCallbacks(Mode.SUCCESS);
  });

  it('Submit unsuccessful callback', async () => {
    await testSubmitCallbacks(Mode.ERROR);
  });

  it('Submit unsuccessful callback with extra message', async () => {
    await testSubmitCallbacks(Mode.ERROR_WITH_MESSAGE);
  });

  it('Submit success', async () => {
    const formSubmitSuccessCallback = jest.fn();
    const formSubmitErrorCallback = jest.fn();

    mockedAxios.post.mockResolvedValue({
      status: 200
    });

    let wrapper = getTaskFormRendererWrapper(
      userTaskInstance,
      formSubmitSuccessCallback,
      formSubmitErrorCallback
    );

    const renderer = wrapper.find(FormRenderer);

    expect(renderer.exists()).toBeTruthy();

    // since the FormRenderer is mocked we are forcing the form submit callback
    await act(async () => {
      const submitHandler: TaskFormSubmitHandler = renderer.props()
        .formSubmitHandler as TaskFormSubmitHandler;
      submitHandler.setSelectedPhase('phase');
      await submitHandler.doSubmit({});
      wait();
    });

    wrapper = wrapper.update().find(TaskFormRenderer);

    expect(wrapper.find(FormRenderer).exists()).toBeTruthy();

    expect(formSubmitSuccessCallback).toBeCalledWith('phase');
  });

  it('Submit error', async () => {
    const formSubmitSuccessCallback = jest.fn();
    const formSubmitErrorCallback = jest.fn();

    mockedAxios.post.mockResolvedValue({
      status: 500,
      data: 'Extra info!'
    });

    let wrapper = getTaskFormRendererWrapper(
      userTaskInstance,
      formSubmitSuccessCallback,
      formSubmitErrorCallback
    );

    let renderer = wrapper.find(FormRenderer);

    expect(renderer.exists()).toBeTruthy();

    // since the FormRenderer is mocked we are forcing the form submit callback
    await act(async () => {
      const submitHandler: TaskFormSubmitHandler = renderer.props()
        .formSubmitHandler as TaskFormSubmitHandler;
      submitHandler.setSelectedPhase('phase');
      await submitHandler.doSubmit({});
    });

    wrapper = wrapper.update().find(TaskFormRenderer);

    renderer = wrapper.find(FormRenderer);
    expect(renderer.exists()).toBeTruthy();
    expect(renderer.props().readOnly).toBeTruthy();

    expect(formSubmitErrorCallback).toBeCalledWith('phase', 'Extra info!');
  });
});
