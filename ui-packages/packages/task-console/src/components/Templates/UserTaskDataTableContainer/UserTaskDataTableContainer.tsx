import {
  Card,
  Grid,
  GridItem,
  PageSection,
  Bullseye,
  Label
} from '@patternfly/react-core';
import React, { useEffect, useState } from 'react';
import UserTaskPageHeader from '../../Molecules/UserTaskPageHeader/UserTaskPageHeader';
import './UserTaskDataTable.css';
import {
  KogitoSpinner,
  DataTableColumn,
  DataTable,
  GraphQL
} from '@kogito-apps/common';
import {
  ouiaPageTypeAndObjectId,
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/ouia-tools';
import _ from 'lodash';

const UserTaskLoadingComponent = (
  <Bullseye>
    <KogitoSpinner spinnerText="Loading user tasks..." />
  </Bullseye>
);

const stateColumnTransformer = value => {
  // rowDataObj is the original data object to render the row
  if (!value) {
    return null;
  }

  return <Label>{value}</Label>;
};

const UserTaskDataTableContainer: React.FC<OUIAProps> = ({
  ouiaId,
  ouiaSafe
}) => {
  const [sortBy, setSortBy] = useState({});
  const [orderByObj, setOrderByObj] = useState({});

  const {
    loading,
    error,
    data,
    refetch,
    networkStatus
  } = GraphQL.useGetUserTasksByStatesQuery({
    variables: {
      state: ['Ready'], // FIXME: state should not be hard-coded.
      orderBy: { state: GraphQL.OrderBy.Asc }
    },
    fetchPolicy: 'network-only',
    notifyOnNetworkStatusChange: true
  });

  // In current implementation, 'state', 'actualOwner', 'description', 'name', 'priority', 'completed', 'started', 'referenceName', and 'lastUpdate' fileds are sortable.
  const columns: DataTableColumn[] = [
    {
      label: 'ProcessId',
      path: '$.processId'
    },
    {
      label: 'Name',
      path: '$.name',
      isSortable: true
    },
    {
      label: 'Priority',
      path: '$.priority'
    },
    {
      label: 'ProcessInstanceId',
      path: '$.processInstanceId'
    },
    {
      label: 'State',
      path: '$.state',
      bodyCellTransformer: stateColumnTransformer,
      isSortable: true
    }
  ];

  useEffect(() => {
    if (!_.isEmpty(orderByObj)) {
      // tslint:disable-next-line:no-floating-promises
      refetch({
        state: ['Ready'], // FIXME: state should not be hard-coded.
        orderBy: orderByObj
      });
    }
  }, [orderByObj]);

  useEffect(() => {
    return ouiaPageTypeAndObjectId('user-tasks');
  });

  const onSorting = (index, direction) => {
    if (index && direction) {
      const sortingColumn = _.last(columns[index].path.split('.'));
      setSortBy({ index, direction }); // This is required by PF4 Table Component
      setOrderByObj(_.set({}, sortingColumn, direction.toUpperCase()));
    }
  };

  return (
    <React.Fragment>
      <div
        {...componentOuiaProps(ouiaId, 'UserTaskDataTableContainer', ouiaSafe)}
      >
        <UserTaskPageHeader />
        <PageSection>
          <Grid hasGutter md={1}>
            <GridItem span={12}>
              <Card className="kogito-task-console--user-task_table-OverFlow">
                <DataTable
                  data={data ? data.UserTaskInstances : undefined}
                  isLoading={loading}
                  columns={columns}
                  networkStatus={networkStatus}
                  error={error}
                  refetch={refetch}
                  LoadingComponent={UserTaskLoadingComponent}
                  sortBy={sortBy}
                  onSorting={onSorting}
                />
              </Card>
            </GridItem>
          </Grid>
        </PageSection>
      </div>
    </React.Fragment>
  );
};

export default UserTaskDataTableContainer;
