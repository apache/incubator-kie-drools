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
  LoadMore,
  ServerErrors,
  User
} from '@kogito-apps/common';
import { MockedProvider } from '@apollo/react-testing';
import wait from 'waait';
import TaskInbox from '../TaskInbox';
import { MemoryRouter as Router } from 'react-router';
import { act } from 'react-dom/test-utils';
import { DropdownToggleAction } from '@patternfly/react-core';
import TaskConsoleContext, {
  DefaultContext
} from '../../../../context/TaskConsoleContext/TaskConsoleContext';
jest.mock('../../../Molecules/TaskInboxToolbar/TaskInboxToolbar');

/* tslint:disable */
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

describe('TaskInbox tests', () => {
  it('Test load data without LoadMore', async () => {
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
                    {
                      and: [
                        { actualOwner: { isNull: true } },
                        { not: { excludedUsers: { contains: 'test' } } },
                        {
                          or: [
                            { potentialUsers: { contains: 'test' } },
                            {
                              potentialGroups: {
                                containsAny: ['group1', 'group2']
                              }
                            }
                          ]
                        }
                      ]
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
            orderBy: { lastUpdate: 'DESC' }
          }
        },
        result: {
          data: {
            UserTaskInstances: userTasks.slice(0, 5)
          }
        }
      }
    ];
    const context = new DefaultContext<GraphQL.UserTaskInstance>(testUser);
    const wrapper = await getWrapper(mocks, context);

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
            whereArgument: {
              and: [
                {
                  or: [
                    { actualOwner: { equal: 'test' } },
                    {
                      and: [
                        { actualOwner: { isNull: true } },
                        { not: { excludedUsers: { contains: 'test' } } },
                        {
                          or: [
                            { potentialUsers: { contains: 'test' } },
                            {
                              potentialGroups: {
                                containsAny: ['group1', 'group2']
                              }
                            }
                          ]
                        }
                      ]
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
                    {
                      and: [
                        { actualOwner: { isNull: true } },
                        { not: { excludedUsers: { contains: 'test' } } },
                        {
                          or: [
                            { potentialUsers: { contains: 'test' } },
                            {
                              potentialGroups: {
                                containsAny: ['group1', 'group2']
                              }
                            }
                          ]
                        }
                      ]
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
            limit: 20,
            orderBy: { lastUpdate: GraphQL.OrderBy.Desc }
          }
        },
        result: {
          data: {
            UserTaskInstances: userTasks.slice(0, 20)
          }
        }
      }
    ];
    const context = new DefaultContext<GraphQL.UserTaskInstance>(testUser);
    let wrapper = await getWrapper(mocks, context);

    expect(wrapper).toMatchSnapshot();

    let dataTable = wrapper.find(DataTable);

    expect(dataTable.exists()).toBeTruthy();
    expect(dataTable.props().data).toHaveLength(10);
    let loadMore = wrapper.find(LoadMore);

    expect(loadMore.exists()).toBeTruthy();

    await act(async () => {
      wrapper
        .find(DropdownToggleAction)
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
            whereArgument: {
              and: [
                {
                  or: [
                    { actualOwner: { equal: 'test' } },
                    {
                      and: [
                        { actualOwner: { isNull: true } },
                        { not: { excludedUsers: { contains: 'test' } } },
                        {
                          or: [
                            { potentialUsers: { contains: 'test' } },
                            {
                              potentialGroups: {
                                containsAny: ['group1', 'group2']
                              }
                            }
                          ]
                        }
                      ]
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
            orderBy: { lastUpdate: 'DESC' }
          }
        },
        error: {
          name: 'error',
          message: 'bla bla bla bla'
        }
      }
    ];
    const context = new DefaultContext<GraphQL.UserTaskInstance>(testUser);
    const wrapper = await getWrapper(mocks, context);

    expect(wrapper).toMatchSnapshot();

    const dataTable = wrapper.find(DataTable);

    expect(dataTable.exists()).toBeFalsy();

    const serverError = wrapper.find(ServerErrors);

    expect(serverError.exists()).toBeTruthy();
  });

  it('test sorting -> with direction', async () => {
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
                    {
                      and: [
                        { actualOwner: { isNull: true } },
                        { not: { excludedUsers: { contains: 'test' } } },
                        {
                          or: [
                            { potentialUsers: { contains: 'test' } },
                            {
                              potentialGroups: {
                                containsAny: ['group1', 'group2']
                              }
                            }
                          ]
                        }
                      ]
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
                    {
                      and: [
                        { actualOwner: { isNull: true } },
                        { not: { excludedUsers: { contains: 'test' } } },
                        {
                          or: [
                            { potentialUsers: { contains: 'test' } },
                            {
                              potentialGroups: {
                                containsAny: ['group1', 'group2']
                              }
                            }
                          ]
                        }
                      ]
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
            orderBy: { state: GraphQL.OrderBy.Asc }
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
            offset: 0,
            limit: 10,
            orderBy: { state: GraphQL.OrderBy.Asc }
          }
        },
        result: {
          data: {
            UserTaskInstances: userTasks.slice(0, 20)
          }
        }
      }
    ];
    const context = new DefaultContext<GraphQL.UserTaskInstance>(testUser);
    let wrapper = await getWrapper(mocks, context);
    // sortby value check
    await act(async () => {
      wrapper
        .find(DataTable)
        .props()
        ['onSorting'](3, 'asc');
    });
    await wait(20);
    wrapper = wrapper.update();
    expect(wrapper.find(DataTable).props()['sortBy']).toEqual({
      index: 3,
      direction: 'asc'
    });
    // after loadmore click - sortby value exists
    await act(async () => {
      wrapper
        .find(DropdownToggleAction)
        .find('button')
        .simulate('click');
    });
    await wait(10);
    expect(wrapper.find(DataTable).props()['sortBy']).toEqual({
      index: 3,
      direction: 'asc'
    });
  });
});
