import React, { useEffect } from 'react';
import { DataList, Bullseye } from '@patternfly/react-core';
import { ServerErrors, GraphQL } from '@kogito-apps/common';
import '../../Templates/DataListContainer/DataList.css';
import DataListItemComponent from '../../Molecules/DataListItemComponent/DataListItemComponent';
import SpinnerComponent from '../../Atoms/SpinnerComponent/SpinnerComponent';
import KogitoEmptyState from '../../Atoms/KogitoEmptyState/KogitoEmptyState';
import '@patternfly/patternfly/patternfly-addons.css';
import './DataListComponent.css';
import ProcessInstanceState = GraphQL.ProcessInstanceState;

interface IOwnProps {
  setInitData: any;
  initData: any;
  isLoading: boolean;
  setIsError: any;
  checkedArray: any;
  abortedObj: any;
  setAbortedObj: any;
  pageSize: number;
  isFilterClicked: boolean;
  filters: any;
  setIsAllChecked: any;
  selectedNumber: number;
  setSelectedNumber: (selectedNumber: number) => void;
}

const DataListComponent: React.FC<IOwnProps> = ({
  initData,
  setInitData,
  isLoading,
  setIsError,
  checkedArray,
  abortedObj,
  setAbortedObj,
  pageSize,
  isFilterClicked,
  filters,
  setIsAllChecked,
  selectedNumber,
  setSelectedNumber
}) => {
  const {
    loading,
    error,
    data,
    networkStatus
  } = GraphQL.useGetProcessInstancesQuery({
    variables: {
      state: [ProcessInstanceState.Active],
      offset: 0,
      limit: pageSize
    },
    fetchPolicy: 'network-only',
    notifyOnNetworkStatusChange: true
  });

  useEffect(() => {
    setIsError(false);
    setAbortedObj({});
    if (
      !loading &&
      data !== undefined &&
      filters.status.length === 1 &&
      filters.status.includes('ACTIVE') &&
      !isFilterClicked
    ) {
      data.ProcessInstances.map((instance: any) => {
        instance.isChecked = false;
        instance.isOpen = false;
      });
    }
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
    return <ServerErrors error={error} />;
  }

  return (
    <DataList aria-label="Process instance list">
      {!loading &&
        initData !== undefined &&
        initData.ProcessInstances.map((item, index) => {
          return (
            <DataListItemComponent
              id={index}
              key={item.id}
              processInstanceData={item}
              checkedArray={checkedArray}
              initData={initData}
              setInitData={setInitData}
              loadingInitData={loading}
              abortedObj={abortedObj}
              setAbortedObj={setAbortedObj}
              setIsAllChecked={setIsAllChecked}
              selectedNumber={selectedNumber}
              setSelectedNumber={setSelectedNumber}
            />
          );
        })}
      {initData !== undefined &&
        !isLoading &&
        initData.ProcessInstances.length === 0 && (
          <KogitoEmptyState
            iconType="searchIcon"
            title="No results found"
            body="Try using different filters"
          />
        )}
    </DataList>
  );
};

export default DataListComponent;
