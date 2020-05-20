import React, {useEffect, useState} from 'react';
import {DataList, Bullseye} from '@patternfly/react-core';
import DataListItemComponent from '../../Molecules/DataListItemComponent/DataListItemComponent';
import SpinnerComponent from '../../Atoms/SpinnerComponent/SpinnerComponent';
import EmptyStateComponent from '../../Atoms/EmptyStateComponent/EmptyStateComponent';
import '@patternfly/patternfly/patternfly-addons.css';
import {useGetUserTasksByStatesQuery} from '../.././../graphql/types';



interface IOwnProps {
  currentState: string;
}

const DataListComponentByState: React.FC<IOwnProps> = ({
  currentState
}) => {
  const {
    loading,
    error,
    data,
    refetch,
    networkStatus
  } = useGetUserTasksByStatesQuery({
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
      childList !== undefined &&
      childList.UserTaskInstances.map((item, index) => {
        return (
          <DataListItemComponent
            id={index}
            key={item.id}
            userTaskInstanceData={item}
          />
        );
      })}
      {loading && (
        <Bullseye>
          <SpinnerComponent spinnerText="Loading user tasks..." />
        </Bullseye>
      )}
    </DataList>
  );
};

export default DataListComponentByState;
