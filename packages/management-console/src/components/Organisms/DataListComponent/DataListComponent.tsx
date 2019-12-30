import React, { useEffect } from 'react';
import { DataList, Bullseye } from '@patternfly/react-core';
import DataListItemComponent from '../../Molecules/DataListItemComponent/DataListItemComponent';
import gql from 'graphql-tag';
import { useQuery } from '@apollo/react-hooks';
import SpinnerComponent from '../../Atoms/SpinnerComponent/SpinnerComponent';
import EmptyStateComponent from '../../Atoms/EmptyStateComponent/EmptyStateComponent';
import '@patternfly/patternfly/patternfly-addons.css';

interface IOwnProps {
  setInitData: any;
  initData: any;
  isLoading: boolean;
  setIsError: any;
  setIsLoading: any;
}

enum ProcessInstanceState {
  Pending = 'PENDING',
  Active = 'ACTIVE',
  Completed = 'COMPLETED',
  Aborted = 'ABORTED',
  Suspended = ' SUSPENDED',
  Error = 'ERROR'
}

const DataListComponent: React.FC<IOwnProps> = ({
  initData,
  setInitData,
  isLoading,
  setIsError
}) => {
  const GET_INSTANCES = gql`
    query getInstances($state: [ProcessInstanceState!]) {
      ProcessInstances(
        where: {
          parentProcessInstanceId: { isNull: true }
          state: { in: $state }
        }
      ) {
        id
        processId
        processName
        parentProcessInstanceId
        roles
        state
        start
        lastUpdate
        addons
        endpoint
        error {
          nodeDefinitionId
          message
        }
      }
    }
  `;
  const { loading, error, data, refetch, networkStatus } = useQuery(
    GET_INSTANCES,
    {
      variables: {
        state: ['ACTIVE']
      },
      fetchPolicy: 'network-only',
      notifyOnNetworkStatusChange: true
    }
  );
  useEffect(() => {
    setIsError(false);
    setInitData(data);
  }, [data]);

  if (loading || isLoading) {
    return (
      <Bullseye>
        <SpinnerComponent spinnerText="Loading process instances..." />
      </Bullseye>
    );
  }
  if (networkStatus === 4) {
    return (
      <Bullseye>
        <SpinnerComponent spinnerText="Loading process instances..." />
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
          body="Try using the refresh action to reload process instances"
          refetch={refetch}
        />
      </div>
    );
  }

  return (
    <DataList aria-label="Expandable data list example">
      {!loading &&
        initData !== undefined &&
        initData.ProcessInstances.map((item, index) => {
          return (
            <DataListItemComponent
              id={index}
              key={item.id}
              processInstanceData={item}
            />
          );
        })}
      {initData !== undefined &&
        !isLoading &&
        initData.ProcessInstances.length === 0 && (
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
