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

import { ProcessInstanceState } from '@kogito-apps/management-console-shared';
import {
  buildProcessListWhereArgument,
  getOrderByObject,
  buildTaskInboxWhereArgument
} from '../QueryUtils';

describe('QueryUtils test', () => {
  it('buildWhereArgument', () => {
    const filtersWithoutBusinessKey = {
      status: [ProcessInstanceState.Active],
      businessKey: []
    };

    const filtersWithBusinessKey = {
      status: [ProcessInstanceState.Active],
      businessKey: ['GMR31']
    };
    const result1 = buildProcessListWhereArgument(filtersWithoutBusinessKey);
    const result2 = buildProcessListWhereArgument(filtersWithBusinessKey);
    expect(result1.or).toBe(undefined);
    expect(result2.or).toEqual([
      { businessKey: { like: filtersWithBusinessKey.businessKey[0] } }
    ]);
  });

  it('getOrderByObject', () => {
    const sortBy: any = {
      direction: 'desc',
      property: 'lastUpdate'
    };
    const result = getOrderByObject(sortBy);

    expect(result).toEqual({ lastUpdate: 'DESC' });
  });

  it('getOrderByObject - empty sortBy', () => {
    const sortBy: any = {};
    const result = getOrderByObject(sortBy);

    expect(result).toEqual({ lastUpdate: 'DESC' });
  });
  it('buildTaskInboxWhereArgument', () => {
    const currentUser = { id: '', groups: [] };
    const activeFilters = {
      taskNames: [],
      taskStates: ['Ready, Reserved']
    };

    const expectedResult = {
      and: [
        {
          or: [
            {
              actualOwner: { equal: '' }
            },
            {
              and: [
                {
                  actualOwner: { isNull: true }
                },
                {
                  not: { excludedUsers: { contains: '' } }
                },
                {
                  or: [
                    { potentialUsers: { contains: '' } },
                    { potentialGroups: { containsAny: [] } }
                  ]
                }
              ]
            }
          ]
        },
        {
          and: [
            {
              state: {
                in: ['Ready, Reserved']
              }
            }
          ]
        }
      ]
    };
    const result = buildTaskInboxWhereArgument(currentUser, activeFilters);
    expect(result).toEqual(expectedResult);
  });

  it('buildTaskInboxWhereArgument - with empty taskStates', () => {
    const currentUser = { id: '', groups: [] };
    const activeFilters = {
      taskNames: [],
      taskStates: []
    };

    const expectedResult = {
      or: [
        {
          actualOwner: { equal: '' }
        },
        {
          and: [
            {
              actualOwner: { isNull: true }
            },
            {
              not: { excludedUsers: { contains: '' } }
            },
            {
              or: [
                { potentialUsers: { contains: '' } },
                { potentialGroups: { containsAny: [] } }
              ]
            }
          ]
        }
      ]
    };
    const result = buildTaskInboxWhereArgument(currentUser, activeFilters);
    expect(result).toEqual(expectedResult);
  });

  it('buildTaskInboxWhereArgument- with taskName', () => {
    const currentUser = { id: '', groups: [] };
    const activeFilters = {
      taskNames: ['test'],
      taskStates: ['Ready, Reserved']
    };

    const expectedResult = {
      and: [
        {
          or: [
            {
              actualOwner: { equal: '' }
            },
            {
              and: [
                {
                  actualOwner: { isNull: true }
                },
                {
                  not: { excludedUsers: { contains: '' } }
                },
                {
                  or: [
                    { potentialUsers: { contains: '' } },
                    { potentialGroups: { containsAny: [] } }
                  ]
                }
              ]
            }
          ]
        },
        {
          and: [
            {
              state: {
                in: ['Ready, Reserved']
              }
            },
            {
              or: [
                {
                  referenceName: {
                    like: '*test*'
                  }
                }
              ]
            }
          ]
        }
      ]
    };
    const result = buildTaskInboxWhereArgument(currentUser, activeFilters);
    expect(result).toEqual(expectedResult);
  });
});
