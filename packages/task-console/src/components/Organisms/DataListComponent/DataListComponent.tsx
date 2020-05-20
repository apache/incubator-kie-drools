import React, { useEffect } from 'react';
import { DataList, Bullseye } from '@patternfly/react-core';
import DataListItemComponent from '../../Molecules/DataListItemComponent/DataListItemComponent';
import SpinnerComponent from '../../Atoms/SpinnerComponent/SpinnerComponent';
import EmptyStateComponent from '../../Atoms/EmptyStateComponent/EmptyStateComponent';
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

const DataListComponent: React.FC<IOwnProps> = ({
  initData,
  setInitData,
  isLoading,
  setIsError
}) => {
  const {
    loading,
    error,
    data,
    refetch,
    networkStatus
  } = useGetUserTasksByStatesQuery({
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
        <SpinnerComponent spinnerText="Loading user tasks..." />
      </Bullseye>
    );
  }

  if (networkStatus === 4) {
    return (
      <Bullseye>
        <SpinnerComponent spinnerText="Loading user tasks..." />
      </Bullseye>
    );
  }

  if (error) {
    setIsError(true);
    return (
      <div className=".pf-u-my-xl">
        <EmptyStateComponent
          iconType="warningTriangleIcon"
          title="Oops... error while loading"
          body="Try using the refresh action to reload user tasks"
          refetch={refetch}
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
            <DataListItemComponent
              id={index}
              key={item.id}
              userTaskInstanceData={item}
            />
          );
        })}
      {initData !== undefined &&
        !isLoading &&
        initData.UserTaskInstances.length === 0 && (
          <EmptyStateComponent
            iconType="searchIcon"
            title="No results found"
            body="Try using different filters"
          />
        )}
    </DataList>
  );
};

export default DataListComponent;
