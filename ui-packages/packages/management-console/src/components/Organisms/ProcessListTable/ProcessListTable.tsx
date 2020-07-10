import React, { useEffect, useState } from 'react';
import { DataList, Bullseye } from '@patternfly/react-core';
import {
  ServerErrors,
  GraphQL,
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner
} from '@kogito-apps/common';
import '../../Templates/ProcessListPage/ProcessListPage.css';
import ProcessListTableItems from '../../Molecules/ProcessListTableItems/ProcessListTableItems';
import '@patternfly/patternfly/patternfly-addons.css';
import './ProcessListTable.css';
import ProcessInstanceState = GraphQL.ProcessInstanceState;

type filterType = {
  status: GraphQL.ProcessInstanceState[];
  businessKey: string[];
};
interface IOwnProps {
  setInitData: any;
  setLimit: (limit: number) => void;
  initData: any;
  isLoading: boolean;
  setIsError: (isError: boolean) => void;
  abortedObj: any;
  setAbortedObj: any;
  pageSize: number;
  filters: filterType;
  setIsAllChecked: (isAllChecked: boolean) => void;
  selectedNumber: number;
  setSelectedNumber: (selectedNumber: number) => void;
}

const ProcessListTable: React.FC<IOwnProps> = ({
  initData,
  setInitData,
  setLimit,
  isLoading,
  setIsError,
  abortedObj,
  setAbortedObj,
  pageSize,
  filters,
  setIsAllChecked,
  selectedNumber,
  setSelectedNumber
}) => {
  const [checkedArray, setCheckedArray] = useState<ProcessInstanceState[]>(
    filters.status
  );
  useEffect(() => {
    setCheckedArray(filters.status);
  }, [filters]);

  const searchWordsArray = [];
  if (filters.businessKey.length > 0) {
    filters.businessKey.forEach((word: string) =>
      searchWordsArray.push({ businessKey: { like: word } })
    );
  }

  const { loading, error, data } = GraphQL.useGetProcessInstancesQuery({
    variables: {
      state: checkedArray,
      offset: 0,
      limit: pageSize
    },
    skip: filters.businessKey.length > 0,
    fetchPolicy: 'network-only'
  });

  const {
    loading: loading1,
    data: data1,
    error: error1
  } = GraphQL.useGetProcessInstancesWithBusinessKeyQuery({
    variables: {
      state: checkedArray,
      offset: 0,
      limit: pageSize,
      businessKeys: searchWordsArray
    },
    skip: filters.businessKey.length === 0,
    fetchPolicy: 'network-only'
  });

  useEffect(() => {
    if (!loading1 && data1 !== undefined) {
      data1.ProcessInstances.forEach((instance: any) => {
        instance.isChecked = false;
        instance.isOpen = false;
      });
    }
    setInitData(data1);
  }, [data1]);

  useEffect(() => {
    setIsError(false);
    setAbortedObj({});
    if (!loading && data !== undefined) {
      data.ProcessInstances.forEach((instance: any) => {
        instance.isChecked = false;
        instance.isOpen = false;
      });
      setLimit(data.ProcessInstances.length);
    }
    setInitData(data);
  }, [data]);

  if (loading || isLoading || loading1) {
    return (
      <Bullseye>
        <KogitoSpinner spinnerText="Loading process instances..." />
      </Bullseye>
    );
  }

  if (error || error1) {
    setIsError(true);
    if (error1) {
      return <ServerErrors error={error1} />;
    }
    if (error) {
      return <ServerErrors error={error} />;
    }
  }

  return (
    <DataList aria-label="Process instance list">
      {(!loading || !loading1) &&
        initData !== undefined &&
        initData.ProcessInstances.map((item, index) => {
          return (
            <ProcessListTableItems
              id={index}
              key={item.id}
              processInstanceData={item}
              initData={initData}
              setInitData={setInitData}
              loadingInitData={loading}
              abortedObj={abortedObj}
              setAbortedObj={setAbortedObj}
              setIsAllChecked={setIsAllChecked}
              selectedNumber={selectedNumber}
              setSelectedNumber={setSelectedNumber}
              filters={filters}
            />
          );
        })}
      {initData !== undefined &&
        !isLoading &&
        initData.ProcessInstances.length === 0 && (
          <KogitoEmptyState
            type={KogitoEmptyStateType.Search}
            title="No results found"
            body="Try using different filters"
          />
        )}
    </DataList>
  );
};

export default ProcessListTable;
