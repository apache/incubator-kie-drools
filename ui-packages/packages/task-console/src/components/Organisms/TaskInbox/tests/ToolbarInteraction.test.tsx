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
  GraphQL,
  KogitoEmptyState,
  getWrapperAsync,
  User,
  DefaultUser
} from '@kogito-apps/common';
import wait from 'waait';
import { act } from 'react-dom/test-utils';
import TaskConsoleContext, {
  DefaultContext
} from '../../../../context/TaskConsoleContext/TaskConsoleContext';
import { MockedProvider } from '@apollo/react-testing';
import { MemoryRouter as Router } from 'react-router';
import TaskInbox from '../TaskInbox';
import { Chip } from '@patternfly/react-core';

const testUser: User = new DefaultUser('test', ['group1', 'group2']);

const getWrapper = async (mocks, context) => {
  let wrapper;
  await act(async () => {
    wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks} addTypename={false}>
        <TaskConsoleContext.Provider value={context}>
          <Router keyLength={0}>
            <TaskInbox />
          </Router>
        </TaskConsoleContext.Provider>
      </MockedProvider>,
      'TaskInbox'
    );
    await wait();
  });
  return (wrapper = wrapper.update().find(TaskInbox));
};

it('show no filters selected state and select reset', async () => {
  const mocks = [
    {
      request: {
        query: GraphQL.GetTasksForUserDocument,
        variables: {
          whereArgument: {
            and: [
              {
                or: [
                  { actualOwner: { equal: 'test' } },
                  { potentialUsers: { contains: 'test' } },
                  {
                    potentialGroups: {
                      containsAny: ['group1', 'group2']
                    }
                  }
                ]
              },
              {
                and: [
                  {
                    state: { in: ['Ready', 'Reserved'] }
                  }
                ]
              }
            ]
          },
          offset: 0,
          limit: 10,
          orderBy: { lastUpdate: GraphQL.OrderBy.Desc }
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
          whereArgument: {
            and: [
              {
                or: [
                  { actualOwner: { equal: 'test' } },
                  { potentialUsers: { contains: 'test' } },
                  {
                    potentialGroups: {
                      containsAny: ['group1', 'group2']
                    }
                  }
                ]
              },
              {
                and: [
                  {
                    state: { in: ['Ready'] }
                  }
                ]
              }
            ]
          },
          offset: 0,
          limit: 10,
          orderBy: { lastUpdate: GraphQL.OrderBy.Desc }
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
          whereArgument: {
            or: [
              { actualOwner: { equal: 'test' } },
              { potentialUsers: { contains: 'test' } },
              {
                potentialGroups: {
                  containsAny: ['group1', 'group2']
                }
              }
            ]
          },
          offset: 0,
          limit: 10,
          orderBy: { lastUpdate: GraphQL.OrderBy.Desc }
        }
      },
      result: {
        data: {
          UserTaskInstances: []
        }
      }
    },
    {
      request: {
        query: GraphQL.GetTasksForUserDocument,
        variables: {
          whereArgument: {
            and: [
              {
                or: [
                  { actualOwner: { equal: 'test' } },
                  { potentialUsers: { contains: 'test' } },
                  {
                    potentialGroups: {
                      containsAny: ['group1', 'group2']
                    }
                  }
                ]
              },
              {
                and: [
                  {
                    state: { in: ['Ready', 'Reserved'] }
                  }
                ]
              }
            ]
          },
          offset: 0,
          limit: 10,
          orderBy: { lastUpdate: GraphQL.OrderBy.Desc }
        }
      },
      result: {
        data: {
          UserTaskInstances: userTasks.slice(0, 10)
        }
      }
    }
  ];
  const context = new DefaultContext<GraphQL.UserTaskInstance>(testUser);
  let wrapper = await getWrapper(mocks, context);
  await act(async () => {
    wrapper
      .find(Chip)
      .at(1)
      .find('button')
      .simulate('click');
  });
  await wait(0);
  wrapper = wrapper.update();
  await act(async () => {
    wrapper
      .find(Chip)
      .at(0)
      .find('button')
      .simulate('click');
  });
  await wait(0);
  const noFiltersSelectedWrapper = wrapper.update().find(KogitoEmptyState);
  expect(noFiltersSelectedWrapper).toMatchSnapshot();
  expect(wrapper.find(KogitoEmptyState).props()['title']).toEqual(
    'No status is selected'
  );
  expect(wrapper.find(KogitoEmptyState).props()['body']).toEqual(
    'Try selecting at least one status to see results'
  );

  // reset clicked
  await act(async () => {
    wrapper
      .find(KogitoEmptyState)
      .props()
      ['onClick']();
  });
  await wait(0);
  wrapper = wrapper.update();
  expect(context.getActiveFilters().filters.status).toEqual([
    'Ready',
    'Reserved'
  ]);
  expect(wrapper.find(KogitoEmptyState).exists()).toBeFalsy();
});
