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
import ApplyForVisaForm from '../../../../util/tests/mocks/ApplyForVisa';
import { TestingUserContext } from '../../../../util/tests/utils/TestingUserContext';
import {
  Button,
  EmptyState,
  EmptyStateSecondaryActions
} from '@patternfly/react-core';
import EmptyTaskForm from '../EmptyTaskForm';

jest.mock('../../../Molecules/FormRenderer/FormRenderer');
jest.mock('../../../Atoms/FormNotification/FormNotification');

jest.mock('axios');

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

const getEmptyTaskFormWrapper = (
  userTaskInstance: GraphQL.UserTaskInstance,
  formSubmitSuccessCallback?: () => void,
  formSubmitErrorCallback?: () => void
) => {
  return getWrapper(
    <KogitoAppContextProvider userContext={userContext}>
      <EmptyTaskForm
        formSchema={_.cloneDeep(ApplyForVisaForm)}
        task={userTaskInstance}
        onSubmitSuccess={formSubmitSuccessCallback}
        onSubmitError={formSubmitErrorCallback}
      />
    </KogitoAppContextProvider>,
    'EmptyTaskForm'
  );
};

let userContext: UserContext;

describe('EmptyTaskForm Test', () => {
  beforeEach(() => {
    userContext = new TestingUserContext();
  });

  it('Empty form rendering', async () => {
    const wrapper = getEmptyTaskFormWrapper(userTaskInstance);

    expect(wrapper).toMatchSnapshot();

    const emptyState = wrapper.find(EmptyState);

    expect(emptyState.exists()).toBeTruthy();

    const actions = wrapper.find(EmptyStateSecondaryActions);

    expect(actions.exists()).toBeTruthy();
  });

  it('Empty form without actions rendering', async () => {
    const task = _.cloneDeep(userTaskInstance);

    task.state = 'Completed';
    task.completed = new Date(task.started);

    const wrapper = getEmptyTaskFormWrapper(task);

    expect(wrapper).toMatchSnapshot();

    const emptyState = wrapper.find(EmptyState);

    expect(emptyState.exists()).toBeTruthy();

    const actions = wrapper.find(EmptyStateSecondaryActions);

    expect(actions.exists()).toBeFalsy();
  });

  it('Submit success', async () => {
    const formSubmitSuccessCallback = jest.fn();
    const formSubmitErrorCallback = jest.fn();

    mockedAxios.post.mockResolvedValue({
      status: 200
    });

    let wrapper = getEmptyTaskFormWrapper(
      userTaskInstance,
      formSubmitSuccessCallback,
      formSubmitErrorCallback
    );

    expect(wrapper).toMatchSnapshot();

    let actions = wrapper.find(EmptyStateSecondaryActions);

    expect(actions.exists()).toBeTruthy();

    await act(async () => {
      const button = actions.find(Button).first();
      expect(button.exists()).toBeTruthy();
      expect(button.props().isDisabled).toBeFalsy();
      button.simulate('click');
      wait();
    });

    wrapper = wrapper.update().find(EmptyTaskForm);

    expect(wrapper).toMatchSnapshot();

    actions = wrapper.find(EmptyStateSecondaryActions);
    expect(actions.exists()).toBeTruthy();

    const button = actions.find(Button).first();
    expect(button.exists()).toBeTruthy();
    expect(button.props().isDisabled).toBeTruthy();

    expect(formSubmitSuccessCallback).toBeCalledWith('complete');
  });

  it('Submit error', async () => {
    const formSubmitSuccessCallback = jest.fn();
    const formSubmitErrorCallback = jest.fn();

    mockedAxios.post.mockResolvedValue({
      status: 500,
      data: 'Extra info!'
    });

    let wrapper = getEmptyTaskFormWrapper(
      userTaskInstance,
      formSubmitSuccessCallback,
      formSubmitErrorCallback
    );

    expect(wrapper).toMatchSnapshot();

    let actions = wrapper.find(EmptyStateSecondaryActions);

    expect(actions.exists()).toBeTruthy();

    await act(async () => {
      const button = actions.find(Button).first();
      expect(button.exists()).toBeTruthy();
      expect(button.props().isDisabled).toBeFalsy();
      button.simulate('click');
      wait();
    });

    wrapper = wrapper.update().find(EmptyTaskForm);

    expect(wrapper).toMatchSnapshot();

    actions = wrapper.find(EmptyStateSecondaryActions);
    expect(actions.exists()).toBeTruthy();

    const button = actions.find(Button).first();
    expect(button.exists()).toBeTruthy();
    expect(button.props().isDisabled).toBeTruthy();

    expect(formSubmitErrorCallback).toBeCalledWith('complete', 'Extra info!');
  });
});
