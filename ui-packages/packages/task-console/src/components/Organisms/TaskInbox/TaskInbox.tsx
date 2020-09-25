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
  ServerErrors
} from '@kogito-apps/common';
import TaskConsoleContext, {
  IContext
} from '../../../context/TaskConsoleContext/TaskConsoleContext';
import Columns from '../../../util/Columns';
import UserTaskInstance = GraphQL.UserTaskInstance;
import _ from 'lodash';

const UserTaskLoadingComponent = (
  <Bullseye>
    <KogitoSpinner spinnerText="Loading user tasks..." />
  </Bullseye>
);

const TaskInbox: React.FC = props => {
  const context: IContext<UserTaskInstance> = useContext(TaskConsoleContext);
  const [defaultPageSize] = useState<number>(10);
  const [isLoaded, setIsLoaded] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [queryOffset, setOffset] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(defaultPageSize);
  const [isLoadingMore, setIsLoadingMore] = useState<boolean>(false);
  const [tableData, setTableData] = useState<any[]>([]);
  const [sorting, setSorting] = useState<boolean>(false);

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

  const onGetMoreInstances = (_queryOffset, _pageSize, _loadMore) => {
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
  const fetchUserTasks = (_queryOffset, _queryLimit) => {
    getUserTasks({
      variables: {
        user: context.getUser().id,
        groups: context.getUser().groups,
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
    if (isLoadingMore === undefined || !isLoadingMore) {
      setIsLoading(loading);
    }

    if (!loading && data !== undefined) {
      setSorting(false);

      if (_.isEmpty(context.getActiveQueryInfo().sortBy)) {
        const newData = tableData.concat(data.UserTaskInstances);
        setTableData(newData);
      } else {
        setTableData(data.UserTaskInstances);
      }

      if (queryOffset > 0 && tableData.length > 0) {
        setIsLoadingMore(false);
      }

      if (!isLoaded) {
        setIsLoaded(true);
      }
    }
  }, [data]);

  const onSorting = (index, direction) => {
    setSorting(true);
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
    return null;
  };

  useEffect(() => {
    if (!_.isEmpty(context.getActiveQueryInfo().sortBy)) {
      fetchUserTasks(0, context.getActiveQueryInfo().maxElements);
    }
  }, [context.getActiveQueryInfo().sortBy]);

  if (error) {
    return <ServerErrors error={error} variant={'large'} />;
  }

  if (!isLoaded || sorting) {
    return UserTaskLoadingComponent;
  }

  if (tableData.length === 0) {
    return (
      <KogitoEmptyState
        type={KogitoEmptyStateType.Search}
        title="No results found"
        body="Try using different filters"
      />
    );
  }
  const mustShowMore =
    isLoadingMore ||
    context.getActiveQueryInfo().maxElements === tableData.length;
  return (
    <React.Fragment>
      <DataTable
        data={tableData}
        isLoading={isLoading}
        columns={columns}
        networkStatus={networkStatus}
        error={error}
        refetch={refetch}
        LoadingComponent={UserTaskLoadingComponent}
        onSorting={onSorting}
        sortBy={context.getActiveQueryInfo().sortBy}
      />
      {mustShowMore && (
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
    </React.Fragment>
  );
};

export default TaskInbox;
