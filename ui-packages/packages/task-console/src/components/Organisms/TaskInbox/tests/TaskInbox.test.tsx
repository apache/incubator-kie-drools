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
  GraphQL,
  LoadMore,
  ServerErrors
} from '@kogito-apps/common';
import wait from 'waait';
import TaskInbox from '../TaskInbox';
import { act } from 'react-dom/test-utils';
import { DropdownToggleAction } from '@patternfly/react-core';
import {
  ITaskConsoleContext,
  TaskConsoleContextImpl
} from '../../../../context/TaskConsoleContext/TaskConsoleContext';
import {
  ITaskConsoleFilterContext,
  TaskConsoleFilterContextImpl
} from '../../../../context/TaskConsoleFilterContext/TaskConsoleFilterContext';
import { getTaskInboxWrapper } from './utils/TaskInboxTestingUtils';
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

let consoleContext: ITaskConsoleContext<GraphQL.UserTaskInstance>;
let filterContext: ITaskConsoleFilterContext;

describe('TaskInbox tests', () => {
  beforeEach(() => {
    consoleContext = new TaskConsoleContextImpl();
    filterContext = new TaskConsoleFilterContextImpl();
  });

  it('Test empty state', async () => {
    const mocks = [
      {
        request: {
          query: GraphQL.GetTasksForUserDocument,
          variables: {
            whereArgument: {
              and: [
                {
                  or: [
                    { actualOwner: { equal: 'john' } },
                    {
                      and: [
                        { actualOwner: { isNull: true } },
                        { not: { excludedUsers: { contains: 'john' } } },
                        {
                          or: [
                            { potentialUsers: { contains: 'john' } },
                            {
                              potentialGroups: {
                                containsAny: ['employees', 'developers']
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
    const wrapper = await getTaskInboxWrapper(
      mocks,
      consoleContext,
      filterContext
    );

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
                    { actualOwner: { equal: 'john' } },
                    {
                      and: [
                        { actualOwner: { isNull: true } },
                        { not: { excludedUsers: { contains: 'john' } } },
                        {
                          or: [
                            { potentialUsers: { contains: 'john' } },
                            {
                              potentialGroups: {
                                containsAny: ['employees', 'developers']
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
                    { actualOwner: { equal: 'john' } },
                    {
                      and: [
                        { actualOwner: { isNull: true } },
                        { not: { excludedUsers: { contains: 'john' } } },
                        {
                          or: [
                            { potentialUsers: { contains: 'john' } },
                            {
                              potentialGroups: {
                                containsAny: ['employees', 'developers']
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
    let wrapper = await getTaskInboxWrapper(
      mocks,
      consoleContext,
      filterContext
    );

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
                    { actualOwner: { equal: 'john' } },
                    {
                      and: [
                        { actualOwner: { isNull: true } },
                        { not: { excludedUsers: { contains: 'john' } } },
                        {
                          or: [
                            { potentialUsers: { contains: 'john' } },
                            {
                              potentialGroups: {
                                containsAny: ['employees', 'developers']
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
    const wrapper = await getTaskInboxWrapper(
      mocks,
      consoleContext,
      filterContext
    );

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
                    { actualOwner: { equal: 'john' } },
                    {
                      and: [
                        { actualOwner: { isNull: true } },
                        { not: { excludedUsers: { contains: 'john' } } },
                        {
                          or: [
                            { potentialUsers: { contains: 'john' } },
                            {
                              potentialGroups: {
                                containsAny: ['employees', 'developers']
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
                    { actualOwner: { equal: 'john' } },
                    {
                      and: [
                        { actualOwner: { isNull: true } },
                        { not: { excludedUsers: { contains: 'john' } } },
                        {
                          or: [
                            { potentialUsers: { contains: 'john' } },
                            {
                              potentialGroups: {
                                containsAny: ['employees', 'developers']
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
            whereArgument: {
              and: [
                {
                  or: [
                    { actualOwner: { equal: 'john' } },
                    { potentialUsers: { contains: 'john' } },
                    {
                      potentialGroups: {
                        containsAny: ['employees', 'developers']
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
    let wrapper = await getTaskInboxWrapper(
      mocks,
      consoleContext,
      filterContext
    );
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
