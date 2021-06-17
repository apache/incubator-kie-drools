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

import React, { useEffect, useState } from 'react';
import { Bullseye } from '@patternfly/react-core';
import {
  DataTable,
  DataTableColumn,
  GraphQL,
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner,
  LoadMore,
  ServerErrors,
  AppContext,
  useKogitoAppContext
} from '@kogito-apps/common';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import Columns from '../../../util/Columns';
import _ from 'lodash';
import TaskInboxToolbar from '../../Molecules/TaskInboxToolbar/TaskInboxToolbar';
import { SortByDirection } from '@patternfly/react-table';
import { getActiveTaskStates } from '../../../util/Utils';
import {
  ITaskConsoleFilterContext,
  useTaskConsoleFilterContext
} from '../../../context/TaskConsoleFilterContext/TaskConsoleFilterContext';
import { buildTaskInboxWhereArgument } from '../../../util/QueryUtils';

const UserTaskLoadingComponent = (
  <Bullseye>
    <KogitoSpinner
      spinnerText="Loading user tasks..."
      ouiaId="task-inbox-loading-tasks"
    />
  </Bullseye>
);

const TaskInbox: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const appContext: AppContext = useKogitoAppContext();
  const filterContext: ITaskConsoleFilterContext = useTaskConsoleFilterContext();

  const [defaultPageSize] = useState<number>(10);
  const [isLoaded, setIsLoaded] = useState<boolean>(false);
  const [queryOffset, setOffset] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(defaultPageSize);
  const [isLoadingMore, setIsLoadingMore] = useState<boolean>(false);
  const [tableData, setTableData] = useState<any[]>([]);
  const [isTableDataLoaded, setIsTableDataLoaded] = useState<boolean>(false);
  const [
    getUserTasks,
    { loading, error, data, refetch, networkStatus }
  ] = GraphQL.useGetTasksForUserLazyQuery({
    fetchPolicy: 'network-only',
    notifyOnNetworkStatusChange: true
  });

  const columns: DataTableColumn[] = [
    Columns.getTaskDescriptionColumn(true),
    Columns.getDefaultColumn('processId', 'Process', true),
    Columns.getDefaultColumn('priority', 'Priority', true),
    Columns.getTaskStateColumn(true),
    Columns.getDateColumn('started', 'Started', true),
    Columns.getDateColumn('lastUpdate', 'Last update', true)
  ];

  const onGetMoreInstances = (
    _queryOffset: number,
    _pageSize: number,
    _loadMore: boolean
  ): void => {
    let newQueryLimit = _pageSize;
    let newQueryOffset = _queryOffset;

    setIsLoadingMore(_loadMore);

    if (_queryOffset !== queryOffset) {
      setOffset(_queryOffset);
    }

    if (_pageSize !== pageSize) {
      setPageSize(_pageSize);
    }

    if (!_.isEmpty(filterContext.getActiveQueryInfo().sortBy)) {
      newQueryOffset = 0;
      newQueryLimit = tableData.length + newQueryLimit;
    }

    filterContext.getActiveQueryInfo().offset = newQueryOffset;
    filterContext.getActiveQueryInfo().maxElements += _pageSize;

    fetchUserTasks(newQueryOffset, newQueryLimit);
  };

  const resetAllFilters = (): void => {
    filterContext.getActiveQueryInfo().offset = 0;
    filterContext.getActiveQueryInfo().maxElements = 10;
    setOffset(0);
    setPageSize(10);
  };

  const applyFilter = (): void => {
    setIsTableDataLoaded(false);
    resetAllFilters();
    fetchUserTasks(0, 10);
  };

  const resetFilters = (): void => {
    filterContext.getActiveFilters().filters.status = getActiveTaskStates();
    filterContext.getActiveFilters().selectedStatus = getActiveTaskStates();
    filterContext.getActiveFilters().filters.taskNames = [];
    onSorting(5, SortByDirection.desc);
    applyFilter();
  };

  const fetchUserTasks = (_queryOffset: number, _queryLimit: number): void => {
    getUserTasks({
      variables: {
        whereArgument: buildTaskInboxWhereArgument(
          appContext.getCurrentUser(),
          filterContext.getActiveFilters()
        ),
        offset: _queryOffset,
        limit: _queryLimit,
        orderBy: getSortByObject()
      }
    });
  };

  useEffect(() => {
    if (!filterContext.getActiveQueryInfo().maxElements) {
      filterContext.getActiveQueryInfo().maxElements = pageSize;
    }
    if (filterContext.getActiveQueryInfo().offset) {
      setOffset(filterContext.getActiveQueryInfo().offset);
    }
    fetchUserTasks(0, filterContext.getActiveQueryInfo().maxElements);
  }, []);

  useEffect(() => {
    if (!loading && data !== undefined) {
      if (
        isLoadingMore &&
        _.isEmpty(filterContext.getActiveQueryInfo().sortBy)
      ) {
        const newData = tableData.concat(data.UserTaskInstances);
        setTableData(newData);
      } else {
        setTableData(data.UserTaskInstances);
      }
      setIsLoadingMore(false);
      setIsTableDataLoaded(true);
      if (!isLoaded) {
        setIsLoaded(true);
      }
    }
  }, [data]);

  const onSorting = (index: number, direction: SortByDirection): void => {
    setIsTableDataLoaded(false);
    if (direction) {
      filterContext.getActiveQueryInfo().sortBy = { index, direction };
    } else {
      filterContext.getActiveQueryInfo().sortBy = null;
    }
  };

  const getSortByObject = () => {
    if (!_.isEmpty(filterContext.getActiveQueryInfo().sortBy)) {
      return _.set(
        {},
        columns[filterContext.getActiveQueryInfo().sortBy.index].path,
        filterContext.getActiveQueryInfo().sortBy.direction.toUpperCase()
      );
    }
    return {
      lastUpdate: 'DESC'
    };
  };

  useEffect(() => {
    if (!_.isEmpty(filterContext.getActiveQueryInfo().sortBy)) {
      fetchUserTasks(0, filterContext.getActiveQueryInfo().maxElements);
    }
  }, [filterContext.getActiveQueryInfo().sortBy]);

  if (error) {
    return <ServerErrors error={error} variant={'large'} />;
  }

  if (!isLoaded) {
    return UserTaskLoadingComponent;
  }

  const mustShowMore: boolean =
    isLoadingMore ||
    filterContext.getActiveQueryInfo().maxElements === tableData.length;

  const showNoFiltersSelected: boolean =
    filterContext.getActiveFilters().filters.status.length === 0 &&
    filterContext.getActiveFilters().filters.taskNames.length === 0;

  return (
    <div {...componentOuiaProps(ouiaId, 'task-inbox', ouiaSafe)}>
      <TaskInboxToolbar applyFilter={applyFilter} resetFilter={resetFilters} />
      {showNoFiltersSelected ? (
        <KogitoEmptyState
          type={KogitoEmptyStateType.Reset}
          title="No status is selected"
          body="Try selecting at least one status to see results"
          onClick={resetFilters}
          ouiaId="task-inbox-no-status"
        />
      ) : isTableDataLoaded ? (
        <DataTable
          data={tableData}
          isLoading={false}
          columns={columns}
          networkStatus={networkStatus}
          error={error}
          refetch={refetch}
          LoadingComponent={UserTaskLoadingComponent}
          onSorting={onSorting}
          sortBy={filterContext.getActiveQueryInfo().sortBy}
        />
      ) : (
        UserTaskLoadingComponent
      )}
      {mustShowMore && isTableDataLoaded && !showNoFiltersSelected && (
        <LoadMore
          offset={queryOffset}
          setOffset={setOffset}
          getMoreItems={(_initval, _pageSize) =>
            onGetMoreInstances(_initval, _pageSize, true)
          }
          pageSize={pageSize}
          isLoadingMore={isLoadingMore}
        />
      )}
    </div>
  );
};

export default TaskInbox;
