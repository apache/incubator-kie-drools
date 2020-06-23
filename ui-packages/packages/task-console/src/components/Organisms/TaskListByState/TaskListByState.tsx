import React, { useEffect, useState } from 'react';
import { DataList, Bullseye } from '@patternfly/react-core';
import TaskListItem from '../../Molecules/TaskListItem/TaskListItem';
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner
} from '@kogito-apps/common';
import '@patternfly/patternfly/patternfly-addons.css';
import { useGetUserTasksByStatesQuery } from '../../../graphql/types';

interface IOwnProps {
  currentState: string;
}

const TaskListByState: React.FC<IOwnProps> = ({ currentState }) => {
  const { loading, error, data, networkStatus } = useGetUserTasksByStatesQuery({
    variables: {
      state: [currentState]
    },
    fetchPolicy: 'network-only',
    notifyOnNetworkStatusChange: true
  });
  const [childList, setChildList] = useState<any>([]);

  useEffect(() => {
    setChildList(data);
  }, [data]);

  if (loading) {
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
        childList !== undefined &&
        childList.UserTaskInstances.map((item, index) => {
          return (
            <TaskListItem
              id={index}
              key={item.id}
              userTaskInstanceData={item}
            />
          );
        })}
      {loading && (
        <Bullseye>
          <KogitoSpinner spinnerText="Loading user tasks..." />
        </Bullseye>
      )}
    </DataList>
  );
};

export default TaskListByState;
