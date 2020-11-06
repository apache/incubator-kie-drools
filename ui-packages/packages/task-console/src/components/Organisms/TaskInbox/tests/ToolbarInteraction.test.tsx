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
import userTasks from './mocks/testdata';
import wait from 'waait';
import { act } from 'react-dom/test-utils';
import { Chip } from '@patternfly/react-core';
import { GraphQL, KogitoEmptyState } from '@kogito-apps/common';
import {
  ITaskConsoleFilterContext,
  TaskConsoleFilterContextImpl
} from '../../../../context/TaskConsoleFilterContext/TaskConsoleFilterContext';
import {
  ITaskConsoleContext,
  TaskConsoleContextImpl
} from '../../../../context/TaskConsoleContext/TaskConsoleContext';
import { getTaskInboxWrapper } from './utils/TaskInboxTestingUtils';

let consoleContext: ITaskConsoleContext<GraphQL.UserTaskInstance>;
let filterContext: ITaskConsoleFilterContext;

describe('TaskInbox toolbar interaction tests', () => {
  beforeEach(() => {
    consoleContext = new TaskConsoleContextImpl();
    filterContext = new TaskConsoleFilterContextImpl();
  });

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
      }
    ];
    let wrapper = await getTaskInboxWrapper(
      mocks,
      consoleContext,
      filterContext
    );
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
    expect(filterContext.getActiveFilters().filters.status).toEqual([
      'Ready',
      'Reserved'
    ]);
    expect(wrapper.find(KogitoEmptyState).exists()).toBeFalsy();
  });
});
