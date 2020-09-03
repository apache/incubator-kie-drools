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
import userTasks from './mocks/testdata';
import {
  DataTable,
  DefaultUser,
  getWrapperAsync,
  GraphQL,
  KogitoEmptyState,
  LoadMore,
  ServerErrors,
  User
} from '@kogito-apps/common';
import { MockedProvider } from '@apollo/react-testing';
import wait from 'waait';
import TaskInbox from '../TaskInbox';
import TaskConsoleContextProvider from '../../../../context/TaskConsoleContext/TaskConsoleContextProvider';
import { MemoryRouter as Router } from 'react-router';
import { act } from 'react-dom/test-utils';
import { DropdownToggle } from '@patternfly/react-core';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@kogito-apps/common', () => ({
  ...jest.requireActual('@kogito-apps/common'),
  DataTable: () => {
    return <MockedComponent />;
  },
  KogitoEmptyState: () => {
    return <MockedComponent />;
  },
  KogitoSpinner: () => {
    return <MockedComponent />;
  },
  ServerErrors: () => {
    return <MockedComponent />;
  }
}));

const testUser: User = new DefaultUser('test', ['group1', 'group2']);

const getWrapper = async mocks => {
  let wrapper;

  await act(async () => {
    wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks} addTypename={false}>
        <TaskConsoleContextProvider user={testUser}>
          <Router keyLength={0}>
            <TaskInbox />
          </Router>
        </TaskConsoleContextProvider>
      </MockedProvider>,
      'TaskInbox'
    );
    await wait();
  });

  return (wrapper = wrapper.update().find(TaskInbox));
};

describe('TaskInbox tests', () => {
  it('Test empty state', async () => {
    const mocks = [
      {
        request: {
          query: GraphQL.GetTasksForUserDocument,
          variables: {
            user: testUser.id,
            groups: testUser.groups,
            offset: 0,
            limit: 10
          }
        },
        result: {
          data: {
            UserTaskInstances: []
          }
        }
      }
    ];

    const wrapper = await getWrapper(mocks);

    expect(wrapper).toMatchSnapshot();

    const emptyState = wrapper.find(KogitoEmptyState);

    expect(emptyState.exists()).toBeTruthy();
  });

  it('Test load data without LoadMore', async () => {
    const mocks = [
      {
        request: {
          query: GraphQL.GetTasksForUserDocument,
          variables: {
            user: testUser.id,
            groups: testUser.groups,
            offset: 0,
            limit: 10
          }
        },
        result: {
          data: {
            UserTaskInstances: userTasks.slice(0, 5)
          }
        }
      }
    ];

    const wrapper = await getWrapper(mocks);

    expect(wrapper).toMatchSnapshot();

    const dataTable = wrapper.find(DataTable);

    expect(dataTable.exists()).toBeTruthy();
    expect(dataTable.props().data).toHaveLength(5);

    const loadMore = wrapper.find(LoadMore);

    expect(loadMore.exists()).toBeFalsy();
  });

  it('Test load data with LoadMore', async () => {
    const mocks = [
      {
        request: {
          query: GraphQL.GetTasksForUserDocument,
          variables: {
            user: testUser.id,
            groups: testUser.groups,
            offset: 0,
            limit: 10
          }
        },
        result: {
          data: {
            UserTaskInstances: userTasks.slice(0, 10)
          }
        }
      },
      {
        request: {
          query: GraphQL.GetTasksForUserDocument,
          variables: {
            user: testUser.id,
            groups: testUser.groups,
            offset: 10,
            limit: 10
          }
        },
        result: {
          data: {
            UserTaskInstances: userTasks.slice(10, 20)
          }
        }
      }
    ];

    let wrapper = await getWrapper(mocks);

    expect(wrapper).toMatchSnapshot();

    let dataTable = wrapper.find(DataTable);

    expect(dataTable.exists()).toBeTruthy();
    expect(dataTable.props().data).toHaveLength(10);

    let loadMore = wrapper.find(LoadMore);

    expect(loadMore.exists()).toBeTruthy();

    await act(async () => {
      wrapper
        .find(DropdownToggle)
        .find('button')
        .at(0)
        .simulate('click');
    });

    wrapper = wrapper.update().find(TaskInbox);

    expect(wrapper).toMatchSnapshot();

    dataTable = wrapper.find(DataTable);
    expect(dataTable.exists()).toBeTruthy();
    expect(dataTable.props().data).toHaveLength(20);

    loadMore = wrapper.find(LoadMore);
    expect(loadMore.exists()).toBeTruthy();
  });

  it('Test load data with error', async () => {
    const mocks = [
      {
        request: {
          query: GraphQL.GetTasksForUserDocument,
          variables: {
            user: testUser.id,
            groups: testUser.groups,
            offset: 0,
            limit: 10
          }
        },
        error: {
          name: 'error',
          message: 'bla bla bla bla'
        }
      }
    ];

    const wrapper = await getWrapper(mocks);

    expect(wrapper).toMatchSnapshot();

    const dataTable = wrapper.find(DataTable);

    expect(dataTable.exists()).toBeFalsy();

    const serverError = wrapper.find(ServerErrors);

    expect(serverError.exists()).toBeTruthy();
  });
});
