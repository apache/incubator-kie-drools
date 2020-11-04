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
import * as H from 'history';
import {
  DefaultUser,
  GraphQL,
  KogitoEmptyState,
  User,
  getWrapperAsync,
  ServerErrors
} from '@kogito-apps/common';
import UserTaskInstance = GraphQL.UserTaskInstance;
import TaskConsoleContext, {
  DefaultContext
} from '../../../../context/TaskConsoleContext/TaskConsoleContext';
import UserTaskInstanceDetailsPage from '../UserTaskInstanceDetailsPage';
import { BrowserRouter } from 'react-router-dom';
import {
  Breadcrumb,
  DrawerPanelContent,
  DrawerCloseButton
} from '@patternfly/react-core';
import PageTitle from '../../../Molecules/PageTitle/PageTitle';
import TaskForm from '../../../Organisms/TaskForm/TaskForm';
import { act } from 'react-dom/test-utils';
import { MockedProvider } from '@apollo/react-testing';
import wait from 'waait';
import { GraphQLError } from 'graphql';
import FormNotification, {
  Notification
} from '../../../Atoms/FormNotification/FormNotification';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('../../../Atoms/FormNotification/FormNotification');
jest.mock('../../../Atoms/TaskState/TaskState');
jest.mock('../../../Molecules/PageTitle/PageTitle');
jest.mock('../../../Organisms/TaskDetails/TaskDetails');
jest.mock('../../../Organisms/TaskForm/TaskForm');

jest.mock('@kogito-apps/common', () => ({
  ...jest.requireActual('@kogito-apps/common'),
  KogitoEmptyState: () => {
    return <MockedComponent />;
  },
  ServerErrors: () => {
    return <MockedComponent />;
  }
}));

jest.mock('@patternfly/react-core', () => ({
  ...jest.requireActual('@patternfly/react-core'),
  Breadcrumb: () => {
    return <MockedComponent />;
  },
  BreadcrumbItem: () => {
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

const testUser: User = new DefaultUser('test', ['group1', 'group2']);

const props = {
  match: {
    params: {
      taskId: '45a73767-5da3-49bf-9c40-d533c3e77ef3'
    },
    url: '',
    isExact: false,
    path: ''
  },
  location: H.createLocation(''),
  history: H.createBrowserHistory()
};

const getWrapper = async (mocks, context) => {
  let wrapper = null;
  await act(async () => {
    wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks} addTypename={false}>
        <TaskConsoleContext.Provider value={context}>
          <BrowserRouter>
            <UserTaskInstanceDetailsPage {...props} />
          </BrowserRouter>
        </TaskConsoleContext.Provider>
      </MockedProvider>,
      'UserTaskInstanceDetailsPage'
    );
    await wait();
  });

  return (wrapper = wrapper.update().find('UserTaskInstanceDetailsPage'));
};

const pushSpy = jest.spyOn(props.history, 'push');

describe('UserTaskInstanceDetailsPage tests', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  it('Test empty state', async () => {
    const mocks = [
      {
        request: {
          query: GraphQL.GetUserTaskByIdDocument,
          variables: {
            id: props.match.params.taskId
          }
        },
        result: {
          data: {
            UserTaskInstances: []
          }
        }
      }
    ];
    const context = new DefaultContext<UserTaskInstance>(testUser);
    context.setActiveItem(null);
    const wrapper = await getWrapper(mocks, context);
    expect(wrapper).toMatchSnapshot();

    const emptyState = wrapper.find(KogitoEmptyState);
    expect(emptyState.exists()).toBeTruthy();

    expect(emptyState.props().body).toStrictEqual(
      "Cannot find task with id '45a73767-5da3-49bf-9c40-d533c3e77ef3'"
    );
    expect(emptyState.props().title).toStrictEqual('Cannot find task');
  });

  it('Test error state', async () => {
    const mocks = [
      {
        request: {
          query: GraphQL.GetUserTaskByIdDocument,
          variables: {
            id: props.match.params.taskId
          }
        },
        result: {
          errors: [new GraphQLError('Error occured in server!')]
        }
      }
    ];
    const context = new DefaultContext<UserTaskInstance>(testUser);
    const wrapper = await getWrapper(mocks, context);
    const serverErrors = wrapper.find(ServerErrors);
    expect(serverErrors).toMatchSnapshot();
    expect(serverErrors.exists()).toBeTruthy();
    expect(serverErrors.props().error.message).toEqual(
      'GraphQL error: Error occured in server!'
    );
  });

  it('Test active task', async () => {
    const context = new DefaultContext<UserTaskInstance>(testUser);
    const mocks = [
      {
        request: {
          query: GraphQL.GetUserTaskByIdDocument,
          variables: {
            id: props.match.params.taskId
          }
        },
        result: {
          data: {
            UserTaskInstances: [userTaskInstance]
          }
        }
      }
    ];
    context.setActiveItem(userTaskInstance);
    const wrapper = await getWrapper(mocks, context);
    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find(Breadcrumb).exists()).toBeTruthy();

    const title = wrapper.find(PageTitle);

    expect(title.exists()).toBeTruthy();

    expect(title.props().title).toStrictEqual(userTaskInstance.referenceName);
    expect(title.props().extra).not.toBeNull();

    const taskForm = wrapper.find(TaskForm);
    expect(taskForm.exists()).toBeTruthy();
    expect(taskForm.props().userTaskInstance).toStrictEqual(userTaskInstance);
    expect(taskForm.props().onSubmitSuccess).not.toBeNull();
    expect(taskForm.props().onSubmitError).not.toBeNull();
  });

  it('Test submit success notification', async () => {
    const context = new DefaultContext<UserTaskInstance>(testUser);
    context.setActiveItem(userTaskInstance);
    const mocks = [];
    let wrapper = await getWrapper(mocks, context);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find(Breadcrumb).exists()).toBeTruthy();

    const taskForm = wrapper.find(TaskForm);

    await act(async () => {
      taskForm.props().onSubmitSuccess('phase');
    });

    wrapper = wrapper.update().find(UserTaskInstanceDetailsPage);

    expect(wrapper).toMatchSnapshot();

    const notificationComponent = wrapper.find(FormNotification);
    expect(notificationComponent.exists()).toBeTruthy();

    const notification = notificationComponent.props().notification;

    expect(notification).not.toBeNull();
    expect(notification.type).toStrictEqual('success');
    expect(notification.message).toStrictEqual(
      "Task 'Apply for visa' successfully transitioned to phase 'phase'."
    );
    expect(notification.details).toBeUndefined();
    expect(notification.customAction).not.toBeNull();

    await act(async () => {
      notification.close();
    });

    wrapper = wrapper.update().find(UserTaskInstanceDetailsPage);
    expect(wrapper).toMatchSnapshot();

    expect(wrapper.find(FormNotification).exists()).toBeFalsy();
  });

  it('Test submit notification - go to inbox link', async () => {
    const context = new DefaultContext<UserTaskInstance>(testUser);
    context.setActiveItem(userTaskInstance);
    const mocks = [];
    let wrapper = await getWrapper(mocks, context);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find(Breadcrumb).exists()).toBeTruthy();

    const taskForm = wrapper.find(TaskForm);

    await act(async () => {
      taskForm.props().onSubmitSuccess('phase');
    });

    wrapper = wrapper.update().find(UserTaskInstanceDetailsPage);

    const notificationComponent = wrapper.find(FormNotification);
    expect(notificationComponent.exists()).toBeTruthy();

    const notification: Notification = notificationComponent.props()
      .notification;

    expect(notification).not.toBeNull();
    expect(notification.customAction).not.toBeNull();

    await act(async () => {
      notification.customAction.onClick();
    });

    wrapper = wrapper.update();

    expect(wrapper.find(FormNotification).exists()).toBeFalsy();

    expect(pushSpy).toBeCalledWith('/');
  });

  it('Test submit error notification', async () => {
    const context = new DefaultContext<UserTaskInstance>(testUser);
    context.setActiveItem(userTaskInstance);
    const mocks = [];
    let wrapper = await getWrapper(mocks, context);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find(Breadcrumb).exists()).toBeTruthy();

    const taskForm = wrapper.find(TaskForm);

    await act(async () => {
      taskForm.props().onSubmitError('phase', 'Extra info!');
    });

    wrapper = wrapper.update().find(UserTaskInstanceDetailsPage);

    expect(wrapper).toMatchSnapshot();

    let notificationComponent = wrapper.find(FormNotification);
    expect(notificationComponent.exists()).toBeTruthy();

    const notification = notificationComponent.props().notification;

    expect(notification).not.toBeNull();
    expect(notification.type).toStrictEqual('error');
    expect(notification.message).toStrictEqual(
      "Task 'Apply for visa' couldn't transition to phase 'phase'."
    );
    expect(notification.details).not.toBeUndefined();
    expect(notification.customAction).not.toBeNull();

    await act(async () => {
      notification.close();
    });

    wrapper = wrapper.update().find(UserTaskInstanceDetailsPage);
    expect(wrapper).toMatchSnapshot();

    notificationComponent = wrapper.find(FormNotification);
    expect(notificationComponent.exists()).toBeFalsy();
  });

  it('test task details drawer', async () => {
    const context = new DefaultContext<UserTaskInstance>(testUser);
    const mocks = [
      {
        request: {
          query: GraphQL.GetUserTaskByIdDocument,
          variables: {
            id: props.match.params.taskId
          }
        },
        result: {
          data: {
            UserTaskInstances: [userTaskInstance]
          }
        }
      }
    ];
    context.setActiveItem(null);
    let wrapper = await getWrapper(mocks, context);
    // open details drawer
    await act(async () => {
      wrapper
        .find('#view-details')
        .find('button')
        .simulate('click');
    });
    wrapper = wrapper.update();
    const detailsPanel = wrapper.find(DrawerPanelContent);
    expect(detailsPanel).toMatchSnapshot();
    expect(detailsPanel.exists()).toBeTruthy();
    expect(
      detailsPanel.find('MockedTaskDetails').props().userTaskInstance
    ).toStrictEqual(userTaskInstance);
    // close details drawer
    await act(async () => {
      detailsPanel
        .find(DrawerCloseButton)
        .find('button')
        .simulate('click');
    });
    wrapper = wrapper.update();
    expect(
      wrapper
        .find(DrawerPanelContent)
        .find('MockedTaskDetails')
        .exists()
    ).toBeFalsy();
  });
});
