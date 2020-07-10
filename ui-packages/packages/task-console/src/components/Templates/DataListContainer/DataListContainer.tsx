import {
  Card,
  Grid,
  GridItem,
  PageSection,
  InjectedOuiaProps,
  withOuiaContext
} from '@patternfly/react-core';
import _ from 'lodash';
import React, { useState, useEffect } from 'react';
import UserTaskPageHeader from '../../Molecules/UserTaskPageHeader/UserTaskPageHeader';
import DataToolbarComponent from '../../Molecules/DataListToolbar/DataListToolbar';
import './DataList.css';
import TaskList from '../../Organisms/TaskList/TaskList';
import { useGetUserTasksByStatesLazyQuery } from '../../../graphql/types';
import {
  ouiaPageTypeAndObjectId,
  KogitoEmptyState,
  KogitoEmptyStateType
} from '@kogito-apps/common';

const DataListContainer: React.FC<InjectedOuiaProps> = ({ ouiaContext }) => {
  const [initData, setInitData] = useState<any>([]);
  const [checkedArray, setCheckedArray] = useState<any>(['Ready']);
  const [isLoading, setIsLoading] = useState(false);
  const [isError, setIsError] = useState(false);
  const [isStatusSelected, setIsStatusSelected] = useState(true);
  const [filters, setFilters] = useState(checkedArray);

  const [
    getProcessInstances,
    { loading, data }
  ] = useGetUserTasksByStatesLazyQuery({ fetchPolicy: 'network-only' });

  const onFilterClick = (arr = checkedArray) => {
    setIsLoading(true);
    setIsError(false);
    setIsStatusSelected(true);
    getProcessInstances({ variables: { state: arr } });
  };

  useEffect(() => {
    setIsLoading(loading);
    setInitData(data);
  }, [data]);

  useEffect(() => {
    return ouiaPageTypeAndObjectId(ouiaContext, 'user-tasks');
  });

  const resetClick = () => {
    setCheckedArray(['Ready']);
    setFilters({ ...filters, status: ['Ready'] });
    onFilterClick(['Ready']);
  };

  return (
    <React.Fragment>
      <UserTaskPageHeader />
      <PageSection>
        <Grid gutter="md">
          <GridItem span={12}>
            <Card className="dataList">
              {!isError && (
                <DataToolbarComponent
                  checkedArray={checkedArray}
                  filterClick={onFilterClick}
                  setCheckedArray={setCheckedArray}
                  setIsStatusSelected={setIsStatusSelected}
                  filters={filters}
                  setFilters={setFilters}
                />
              )}
              {isStatusSelected ? (
                <TaskList
                  initData={initData}
                  setInitData={setInitData}
                  isLoading={isLoading}
                  setIsLoading={setIsLoading}
                  setIsError={setIsError}
                />
              ) : (
                <KogitoEmptyState
                  type={KogitoEmptyStateType.Reset}
                  title="No status is selected"
                  body="Try selecting at least one status to see results"
                  onClick={resetClick}
                />
              )}
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    </React.Fragment>
  );
};

export default withOuiaContext(DataListContainer);
