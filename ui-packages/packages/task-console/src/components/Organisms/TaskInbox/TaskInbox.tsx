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

import React, { useContext, useEffect, useState } from 'react';
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
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/common';
import TaskConsoleContext, {
  IContext
} from '../../../context/TaskConsoleContext/TaskConsoleContext';
import Columns from '../../../util/Columns';
import UserTaskInstance = GraphQL.UserTaskInstance;
import _ from 'lodash';
import TaskInboxToolbar from '../../Molecules/TaskInboxToolbar/TaskInboxToolbar';
import { SortByDirection } from '@patternfly/react-table';
import { getActiveTaskStates } from '../../../util/Utils';

const UserTaskLoadingComponent = (
  <Bullseye>
    <KogitoSpinner spinnerText="Loading user tasks..." />
  </Bullseye>
);

const TaskInbox: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const context: IContext<UserTaskInstance> = useContext(TaskConsoleContext);
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
    Columns.getDefaultColumn('processId', 'Process', false),
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

    if (!_.isEmpty(context.getActiveQueryInfo().sortBy)) {
      newQueryOffset = 0;
      newQueryLimit = tableData.length + newQueryLimit;
    }

    context.getActiveQueryInfo().offset = newQueryOffset;
    context.getActiveQueryInfo().maxElements += _pageSize;

    fetchUserTasks(newQueryOffset, newQueryLimit);
  };

  const resetAllFilters = (): void => {
    context.getActiveQueryInfo().offset = 0;
    context.getActiveQueryInfo().maxElements = 10;
    setOffset(0);
    setPageSize(10);
  };

  const applyFilter = (): void => {
    setIsTableDataLoaded(false);
    resetAllFilters();
    fetchUserTasks(0, 10);
  };

  const resetFilters = (): void => {
    context.getActiveFilters().filters.status = getActiveTaskStates();
    context.getActiveFilters().selectedStatus = getActiveTaskStates();
    context.getActiveFilters().filters.taskNames = [];
    onSorting(5, SortByDirection.desc);
    applyFilter();
  };

  const createSearchTextArray = () => {
    const formattedTextArray = [];
    context.getActiveFilters().filters.taskNames.forEach(word => {
      formattedTextArray.push({
        referenceName: {
          like: word
        }
      });
    });
    return {
      or: formattedTextArray
    };
  };

  const createUserAssignmentClause = () => {
    return {
      or: [
        { actualOwner: { equal: context.getUser().id } },
        { potentialUsers: { contains: context.getUser().id } },
        { potentialGroups: { containsAny: context.getUser().groups } }
      ]
    };
  };

  const createWhereArgument = () => {
    const filtersClause = [];
    if (context.getActiveFilters().filters.status.length > 0) {
      filtersClause.push({
        state: { in: context.getActiveFilters().filters.status }
      });
    }
    if (context.getActiveFilters().filters.taskNames.length > 0) {
      filtersClause.push(createSearchTextArray());
    }
    if (filtersClause.length > 0) {
      return {
        and: [
          createUserAssignmentClause(),
          {
            and: filtersClause
          }
        ]
      };
    }
    return createUserAssignmentClause();
  };

  const fetchUserTasks = (_queryOffset: number, _queryLimit: number): void => {
    getUserTasks({
      variables: {
        whereArgument: createWhereArgument(),
        offset: _queryOffset,
        limit: _queryLimit,
        orderBy: getSortByObject()
      }
    });
  };

  useEffect(() => {
    if (!context.getActiveQueryInfo().maxElements) {
      context.getActiveQueryInfo().maxElements = pageSize;
    }
    if (context.getActiveQueryInfo().offset) {
      setOffset(context.getActiveQueryInfo().offset);
    }
    fetchUserTasks(0, context.getActiveQueryInfo().maxElements);
  }, []);

  useEffect(() => {
    if (!loading && data !== undefined) {
      if (isLoadingMore && _.isEmpty(context.getActiveQueryInfo().sortBy)) {
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
      context.getActiveQueryInfo().sortBy = { index, direction };
    } else {
      context.getActiveQueryInfo().sortBy = null;
    }
  };

  const getSortByObject = () => {
    if (!_.isEmpty(context.getActiveQueryInfo().sortBy)) {
      return _.set(
        {},
        columns[context.getActiveQueryInfo().sortBy.index].path,
        context.getActiveQueryInfo().sortBy.direction.toUpperCase()
      );
    }
    return {
      lastUpdate: 'DESC'
    };
  };

  useEffect(() => {
    if (!_.isEmpty(context.getActiveQueryInfo().sortBy)) {
      fetchUserTasks(0, context.getActiveQueryInfo().maxElements);
    }
  }, [context.getActiveQueryInfo().sortBy]);

  if (error) {
    return <ServerErrors error={error} variant={'large'} />;
  }

  if (!isLoaded) {
    return UserTaskLoadingComponent;
  }

  const mustShowMore: boolean =
    isLoadingMore ||
    context.getActiveQueryInfo().maxElements === tableData.length;

  const showNoFiltersSelected: boolean =
    context.getActiveFilters().filters.status.length === 0 &&
    context.getActiveFilters().filters.taskNames.length === 0;

  return (
    <div {...componentOuiaProps(ouiaId, 'task-inbox', ouiaSafe)}>
      <TaskInboxToolbar applyFilter={applyFilter} resetFilter={resetFilters} />
      {showNoFiltersSelected ? (
        <KogitoEmptyState
          type={KogitoEmptyStateType.Reset}
          title="No status is selected"
          body="Try selecting at least one status to see results"
          onClick={resetFilters}
        />
      ) : isTableDataLoaded ? (
        tableData.length === 0 ? (
          <KogitoEmptyState
            type={KogitoEmptyStateType.Search}
            title="No results found"
            body="Try using different filters"
          />
        ) : (
          <DataTable
            data={tableData}
            isLoading={false}
            columns={columns}
            networkStatus={networkStatus}
            error={error}
            refetch={refetch}
            LoadingComponent={UserTaskLoadingComponent}
            onSorting={onSorting}
            sortBy={context.getActiveQueryInfo().sortBy}
          />
        )
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
