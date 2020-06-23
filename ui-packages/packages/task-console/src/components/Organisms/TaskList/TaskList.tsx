import React, { useEffect } from 'react';
import { DataList, Bullseye } from '@patternfly/react-core';
import TaskListItem from '../../Molecules/TaskListItem/TaskListItem';
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner
} from '@kogito-apps/common';
import '@patternfly/patternfly/patternfly-addons.css';
import { useGetUserTasksByStatesQuery } from '../.././../graphql/types';

interface IOwnProps {
  setInitData: any;
  initData: any;
  isLoading: boolean;
  setIsError: any;
  setIsLoading: any;
}

/* enum UserTaskState {
  Ready = 'Ready',
  Completed = 'Completed',
  Aborted = 'Aborted'
} */

const TaskList: React.FC<IOwnProps> = ({
  initData,
  setInitData,
  isLoading,
  setIsError
}) => {
  const { loading, error, data, networkStatus } = useGetUserTasksByStatesQuery({
    variables: {
      state: ['Ready']
    },
    fetchPolicy: 'network-only',
    notifyOnNetworkStatusChange: true
  });

  useEffect(() => {
    setIsError(false);
    setInitData(data);
  }, [data]);

  if (loading || isLoading) {
    return (
      <Bullseye>
        <KogitoSpinner spinnerText="Loading user tasks..." />
      </Bullseye>
    );
  }

  if (networkStatus === 4) {
    return (
      <Bullseye>
        <KogitoSpinner spinnerText="Loading user tasks..." />
      </Bullseye>
    );
  }

  if (error) {
    setIsError(true);
    return (
      <div className=".pf-u-my-xl">
        <KogitoEmptyState
          type={KogitoEmptyStateType.Refresh}
          title="Oops... error while loading"
          body="Try using the refresh action to reload user tasks"
        />
      </div>
    );
  }

  return (
    <DataList aria-label="User Task list">
      {!loading &&
        initData !== undefined &&
        initData.UserTaskInstances.map((item, index) => {
          return (
            <TaskListItem
              id={index}
              key={item.id}
              userTaskInstanceData={item}
            />
          );
        })}
      {initData !== undefined &&
        !isLoading &&
        initData.UserTaskInstances.length === 0 && (
          <KogitoEmptyState
            type={KogitoEmptyStateType.Search}
            title="No results found"
            body="Try using different filters"
          />
        )}
    </DataList>
  );
};

export default TaskList;
