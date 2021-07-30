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

import React, { useEffect, useState } from 'react';
import _ from 'lodash';
import { Bullseye } from '@patternfly/react-core';
import {
  DataTable,
  DataTableColumn,
  LoadMore,
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner,
  ServerErrors
} from '@kogito-apps/components-common';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import {
  QueryFilter,
  SortBy,
  TaskInboxDriver,
  TaskInboxState
} from '../../../api';
import TaskInboxToolbar from '../TaskInboxToolbar/TaskInboxToolbar';
import {
  getDateColumn,
  getDefaultColumn,
  getTaskDescriptionColumn,
  getTaskStateColumn
} from '../utils/TaskInboxUtils';

export interface TaskInboxProps {
  isEnvelopeConnectedToChannel: boolean;
  initialState?: TaskInboxState;
  driver: TaskInboxDriver;
  allTaskStates?: string[];
  activeTaskStates?: string[];
  currentUser?: string;
}

const UserTaskLoadingComponent = (
  <Bullseye>
    <KogitoSpinner
      spinnerText="Loading user tasks..."
      ouiaId="task-inbox-loading-tasks"
    />
  </Bullseye>
);

const TaskInbox: React.FC<TaskInboxProps & OUIAProps> = ({
  isEnvelopeConnectedToChannel,
  initialState,
  driver,
  allTaskStates,
  activeTaskStates,
  currentUser,
  ouiaId,
  ouiaSafe
}) => {
  const [queryFilter, setQueryFilter] = useState<QueryFilter>({
    taskStates: [],
    taskNames: []
  });
  const [allStates, setAllStates] = useState<string[]>([]);
  const [activeStates, setActiveStates] = useState<string[]>([]);
  const [sortBy, setSortBy] = useState<SortBy>({
    property: 'lastUpdate',
    direction: 'desc'
  });
  const [pageSize] = useState<number>(10);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isLoadingMore, setIsLoadingMore] = useState<boolean>(false);
  const [offset, setOffset] = useState<number>(0);
  const [error, setError] = useState<any>(undefined);
  const [showEmptyFiltersError, setShowEmptyFiltersError] = useState<boolean>(
    false
  );

  const [tasks, setTasks] = useState<UserTaskInstance[]>([]);

  const [columns] = useState<DataTableColumn[]>([
    getTaskDescriptionColumn((task: UserTaskInstance): void =>
      driver.openTask(task)
    ),
    getDefaultColumn('processId', 'Process', true),
    getDefaultColumn('priority', 'Priority', true),
    getTaskStateColumn(),
    getDateColumn('started', 'Started'),
    getDateColumn('lastUpdate', 'Last update')
  ]);

  const getTableSortBy = () => {
    return {
      index: columns.findIndex(column => column.path === sortBy.property),
      direction: sortBy.direction
    };
  };

  const initDefault = async () => {
    const defaultState: TaskInboxState = {
      filters: {
        taskStates: [...activeTaskStates],
        taskNames: []
      },
      sortBy,
      currentPage: { offset: 0, limit: 10 }
    };
    await driver.setInitialState(defaultState);
    setQueryFilter(defaultState.filters);
    setIsLoading(true);
    setSortBy(defaultState.sortBy);
    doQueryTasks(0, pageSize, true);
  };

  const initWithState = async (initialState: TaskInboxState) => {
    setQueryFilter(initialState.filters);
    setSortBy(initialState.sortBy);
    setOffset(initialState.currentPage.offset);

    setIsLoading(true);

    const limit = initialState.currentPage.offset + pageSize;

    doQueryTasks(0, limit, true);
  };

  const doQueryTasks = async (
    _offset: number,
    _limit: number,
    _resetTasks: boolean,
    _resetPagination: boolean = false,
    _loadMore: boolean = false
  ) => {
    setIsLoadingMore(_loadMore);
    setError(null);

    try {
      const response: UserTaskInstance[] = await driver.query(_offset, _limit);
      if (_resetTasks) {
        setTasks(response);
      } else {
        setTasks(tasks.concat(response));
      }

      if (_resetPagination) {
        setOffset(_offset);
      }
    } catch (err) {
      setError(err);
    } finally {
      setIsLoading(false);
      setIsLoadingMore(false);
    }
  };

  const doApplyFilter = async (filter: QueryFilter) => {
    setQueryFilter(filter);
    if (
      !filter ||
      (_.isEmpty(filter.taskStates) && _.isEmpty(filter.taskNames))
    ) {
      setShowEmptyFiltersError(true);
      return;
    }
    setShowEmptyFiltersError(false);
    setIsLoading(true);
    await driver.applyFilter(filter);
    doQueryTasks(0, pageSize, true, true);
  };

  const doRefresh = async () => {
    setIsLoading(true);
    doQueryTasks(0, pageSize, true, true);
  };

  const onSort = async (index: number, direction) => {
    const sortObj: SortBy = {
      property: columns[index].path,
      direction: direction.toLowerCase()
    };
    await driver.applySorting(sortObj);
    setSortBy(sortObj);
    setIsLoading(true);
    await doQueryTasks(0, pageSize, true, true);
  };

  useEffect(() => {
    if (!isEnvelopeConnectedToChannel) {
      setIsLoading(true);
      return;
    }
    setAllStates(allTaskStates);
    setActiveStates(activeTaskStates);
    if (!initialState) {
      initDefault();
    } else {
      initWithState(initialState);
    }
  }, [isEnvelopeConnectedToChannel]);

  useEffect(() => {
    if (isEnvelopeConnectedToChannel && currentUser.length > 0) {
      initDefault();
    }
  }, [isEnvelopeConnectedToChannel, currentUser]);

  if (error) {
    return <ServerErrors error={error} variant={'large'} />;
  }

  const mustShowMore = (): boolean => {
    if (!isLoadingMore) {
      const limit = offset * pageSize + pageSize;
      return !isLoading && limit === tasks.length;
    }
    return true;
  };

  return (
    <div {...componentOuiaProps(ouiaId, 'task-inbox', ouiaSafe)}>
      <TaskInboxToolbar
        activeFilter={queryFilter}
        allTaskStates={allStates}
        activeTaskStates={activeStates}
        applyFilter={doApplyFilter}
        refresh={doRefresh}
      />
      {showEmptyFiltersError ? (
        <KogitoEmptyState
          type={KogitoEmptyStateType.Reset}
          title="No status is selected"
          body="Try selecting at least one status to see results"
          onClick={() =>
            doApplyFilter({ taskStates: activeStates, taskNames: [] })
          }
          ouiaId="task-inbox-no-status"
        />
      ) : (
        <>
          <DataTable
            data={tasks}
            isLoading={isLoading}
            columns={columns}
            error={false}
            sortBy={getTableSortBy()}
            onSorting={onSort}
            LoadingComponent={UserTaskLoadingComponent}
          />
          {mustShowMore() && (
            <LoadMore
              offset={offset}
              setOffset={setOffset}
              getMoreItems={(_offset, _limit) =>
                doQueryTasks(_offset, _limit, false, true, true)
              }
              pageSize={pageSize}
              isLoadingMore={isLoadingMore}
            />
          )}
        </>
      )}
    </div>
  );
};

export default TaskInbox;
