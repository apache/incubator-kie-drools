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

import jp from 'jsonpath';
import { DefaultUser, User } from '@kogito-apps/common';
import { buildTaskInboxWhereArgument } from '../QueryUtils';

const user: User = new DefaultUser('john', ['employees', 'developers']);
const taskStates: string[] = ['Ready', 'Reserved'];
const taskNames: string[] = ['Apply', 'Confirm'];

const checkUserAssignments = clause => {
  expect(clause).not.toBeUndefined();
  expect(jp.value(clause, '$.or')).toHaveLength(2);

  // Check user is actualOwner clause
  expect(jp.value(clause, '$.or[0].actualOwner.equal')).toStrictEqual(user.id);

  expect(jp.value(clause, '$.or[1].and')).toHaveLength(3);

  // Check task has no actualOwner
  expect(jp.value(clause, '$.or[1].and[0].actualOwner.isNull')).toBeTruthy();

  // Check user is not task excludedUsers list
  expect(
    jp.value(clause, '$.or[1].and[1].not.excludedUsers.contains')
  ).toStrictEqual(user.id);

  // Check potential assignments
  expect(jp.value(clause, '$.or[1].and[2].or')).toHaveLength(2);
  expect(
    jp.value(clause, '$.or[1].and[2].or[0].potentialUsers.contains')
  ).toStrictEqual(user.id);
  expect(
    jp.value(clause, '$.or[1].and[2].or[1].potentialGroups.containsAny')
  ).toStrictEqual(user.groups);
};

const checkStateFilter = stateClause => {
  expect(stateClause).not.toBeNull();
  expect(jp.value(stateClause, '$.state.in')).toStrictEqual(taskStates);
};

const checkTaskNamesFilter = taskNameClause => {
  expect(taskNameClause).not.toBeNull();
  expect(jp.value(taskNameClause, '$.or')).toHaveLength(2);

  for (let i = 0; i < taskNames.length; i++) {
    expect(
      jp.value(taskNameClause, `$.or[${i}].referenceName.like`)
    ).toStrictEqual(`*${taskNames[i]}*`);
  }
};

describe('QueryUtils test', () => {
  it('TaskInbox where argument without filters', () => {
    const whereArgument: any = buildTaskInboxWhereArgument(user, {});

    expect(whereArgument.and).toBeUndefined();
    checkUserAssignments(whereArgument);
  });

  it('TaskInbox where argument with state filters', () => {
    const whereArgument: any = buildTaskInboxWhereArgument(user, {
      filters: {
        status: taskStates,
        taskNames: []
      }
    });

    expect(whereArgument.and).not.toBeUndefined();
    expect(whereArgument.and).toHaveLength(2);

    checkUserAssignments(whereArgument.and[0]);

    const filtersClause = whereArgument.and[1].and;

    expect(filtersClause).not.toBeNull();
    expect(filtersClause).toHaveLength(1);

    checkStateFilter(filtersClause[0]);
  });

  it('TaskInbox where argument with task name filters', () => {
    const whereArgument: any = buildTaskInboxWhereArgument(user, {
      filters: {
        status: [],
        taskNames: taskNames
      }
    });

    expect(whereArgument.and).not.toBeUndefined();
    expect(whereArgument.and).toHaveLength(2);

    checkUserAssignments(whereArgument.and[0]);

    const filtersClause = whereArgument.and[1].and;

    expect(filtersClause).not.toBeNull();
    expect(filtersClause).toHaveLength(1);

    checkTaskNamesFilter(filtersClause[0]);
  });

  it('TaskInbox where argument with full filters', () => {
    const whereArgument: any = buildTaskInboxWhereArgument(user, {
      filters: {
        status: taskStates,
        taskNames: taskNames
      }
    });

    expect(whereArgument.and).not.toBeUndefined();
    expect(whereArgument.and).toHaveLength(2);

    checkUserAssignments(whereArgument.and[0]);

    const filtersClause = whereArgument.and[1].and;

    expect(filtersClause).not.toBeNull();
    expect(filtersClause).toHaveLength(2);

    checkStateFilter(filtersClause[0]);
    checkTaskNamesFilter(filtersClause[1]);
  });
});
