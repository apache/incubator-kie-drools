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

import { IActiveFilters } from '../context/TaskConsoleFilterContext/TaskConsoleFilterContext';
import { User } from '@kogito-apps/common';

const createSearchTextArray = (taskNames: string[]) => {
  const formattedTextArray = [];
  taskNames.forEach(word => {
    formattedTextArray.push({
      referenceName: {
        like: `*${word}*`
      }
    });
  });
  return {
    or: formattedTextArray
  };
};

const createUserAssignmentClause = (currentUser: User) => {
  return {
    or: [
      { actualOwner: { equal: currentUser.id } },
      {
        and: [
          { actualOwner: { isNull: true } },
          {
            not: { excludedUsers: { contains: currentUser.id } }
          },
          {
            or: [
              { potentialUsers: { contains: currentUser.id } },
              { potentialGroups: { containsAny: currentUser.groups } }
            ]
          }
        ]
      }
    ]
  };
};

export const buildTaskInboxWhereArgument = (
  currentUser: User,
  activeFilters: IActiveFilters
) => {
  if (activeFilters.filters) {
    const filtersClause = [];
    if (activeFilters.filters.status.length > 0) {
      filtersClause.push({
        state: { in: activeFilters.filters.status }
      });
    }
    if (activeFilters.filters.taskNames.length > 0) {
      filtersClause.push(
        createSearchTextArray(activeFilters.filters.taskNames)
      );
    }

    if (filtersClause.length > 0) {
      return {
        and: [
          createUserAssignmentClause(currentUser),
          {
            and: filtersClause
          }
        ]
      };
    }
  }
  return createUserAssignmentClause(currentUser);
};
