import React from 'react';
import * as H from 'history';
import {
  DefaultUser,
  getWrapper,
  GraphQL,
  KogitoEmptyState,
  User
} from '@kogito-apps/common';
import UserTaskInstance = GraphQL.UserTaskInstance;
import TaskConsoleContext, {
  DefaultContext
} from '../../../../context/TaskConsoleContext/TaskConsoleContext';
import TaskConsoleContextProvider from '../../../../context/TaskConsoleContext/TaskConsoleContextProvider';
import UserTaskInstanceDetailsPage from '../UserTaskInstanceDetailsPage';
import { BrowserRouter } from 'react-router-dom';
import { Breadcrumb, Text } from '@patternfly/react-core';
import PageTitle from '../../../Molecules/PageTitle/PageTitle';
import TaskForm from '../../../Organisms/TaskForm/TaskForm';
import TaskDetails from '../../../Organisms/TaskDetails/TaskDetails';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('../../../Atoms/TaskState/TaskState');
jest.mock('../../../Molecules/PageTitle/PageTitle');
jest.mock('../../../Organisms/TaskDetails/TaskDetails');
jest.mock('../../../Organisms/TaskForm/TaskForm');

jest.mock('@kogito-apps/common', () => ({
  ...jest.requireActual('@kogito-apps/common'),
  KogitoEmptyState: () => {
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

describe('UserTaskInstanceDetailsPage tests', () => {
  it('Test empty state', () => {
    const wrapper = getWrapper(
      <TaskConsoleContextProvider user={testUser}>
        <UserTaskInstanceDetailsPage {...props} />
      </TaskConsoleContextProvider>,
      'UserTaskInstanceDetailsPage'
    );

    expect(wrapper).toMatchSnapshot();

    const emptyState = wrapper.find(KogitoEmptyState);
    expect(emptyState.exists()).toBeTruthy();

    expect(emptyState.props().body).toStrictEqual(
      "Cannot find task with id '45a73767-5da3-49bf-9c40-d533c3e77ef3'"
    );
    expect(emptyState.props().title).toStrictEqual('Cannot find task');
  });

  it('Test active task', () => {
    const context = new DefaultContext<UserTaskInstance>(testUser);

    context.setActiveItem(userTaskInstance);

    const wrapper = getWrapper(
      <TaskConsoleContext.Provider value={context}>
        <BrowserRouter>
          <UserTaskInstanceDetailsPage {...props} />
        </BrowserRouter>
      </TaskConsoleContext.Provider>,
      'UserTaskInstanceDetailsPage'
    );

    expect(wrapper).toMatchSnapshot();

    expect(wrapper.find(Breadcrumb).exists()).toBeTruthy();

    const title = wrapper.find(PageTitle);

    expect(title.exists()).toBeTruthy();

    expect(title.props().title).toStrictEqual(userTaskInstance.referenceName);
    expect(title.props().extra).not.toBeNull();

    const id = wrapper.find(Text);
    expect(id.exists()).toBeTruthy();
    expect(id.html()).toContain(`ID: ${userTaskInstance.id}`);

    const taskForm = wrapper.find(TaskForm);

    expect(taskForm.exists()).toBeTruthy();
    expect(taskForm.props().userTaskInstance).toStrictEqual(userTaskInstance);
    expect(taskForm.props().successCallback).not.toBeNull();
    expect(taskForm.props().errorCallback).not.toBeNull();

    const taskDetails = wrapper.find(TaskDetails);

    expect(taskDetails.exists()).toBeTruthy();
    expect(taskDetails.props().userTaskInstance).toStrictEqual(
      userTaskInstance
    );
  });
});
