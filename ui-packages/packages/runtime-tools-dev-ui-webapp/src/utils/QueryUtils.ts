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

import _ from 'lodash';
import { User } from '@kogito-apps/consoles-common';
import { SortBy, QueryFilter } from '@kogito-apps/task-inbox';
import { ProcessInstanceFilter } from '@kogito-apps/process-list';

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
  activeFilters: QueryFilter
) => {
  /* istanbul ignore else*/
  if (activeFilters) {
    const filtersClause = [];
    if (activeFilters.taskStates.length > 0) {
      filtersClause.push({
        state: { in: activeFilters.taskStates }
      });
    }
    if (activeFilters.taskNames.length > 0) {
      filtersClause.push(createSearchTextArray(activeFilters.taskNames));
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

export const getOrderByObject = (sortBy: SortBy) => {
  if (!_.isEmpty(sortBy)) {
    return _.set({}, sortBy.property, sortBy.direction.toUpperCase());
  }
  return {
    lastUpdate: 'DESC'
  };
};

const formatSearchWords = (searchWords: string[]) => {
  const tempSearchWordsArray = [];
  searchWords.forEach(word => {
    tempSearchWordsArray.push({ businessKey: { like: word } });
  });
  return tempSearchWordsArray;
};

export const buildProcessListWhereArgument = (
  filters: ProcessInstanceFilter
) => {
  if (filters.businessKey.length === 0) {
    return {
      parentProcessInstanceId: { isNull: true },
      state: { in: filters.status }
    };
  } else {
    return {
      parentProcessInstanceId: { isNull: true },
      state: { in: filters.status },
      or: formatSearchWords(filters.businessKey)
    };
  }
};
