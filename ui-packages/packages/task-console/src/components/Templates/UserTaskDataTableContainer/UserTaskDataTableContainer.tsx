import {
  Card,
  Grid,
  GridItem,
  PageSection,
  InjectedOuiaProps,
  withOuiaContext,
  Bullseye,
  Label
} from '@patternfly/react-core';
import React, { useEffect } from 'react';
import UserTaskPageHeader from '../../Molecules/UserTaskPageHeader/UserTaskPageHeader';
import './UserTaskDataTable.css';
import {
  ouiaPageTypeAndObjectId,
  KogitoSpinner,
  DataTableColumn,
  DataTable,
  GraphQL
} from '@kogito-apps/common';

const UserTaskLoadingComponent = (
  <Bullseye>
    <KogitoSpinner spinnerText="Loading user tasks..." />
  </Bullseye>
);

const stateColumnTransformer = (value, rowDataObj) => {
  // rowDataObj is the original data object to render the row
  if (!value) {
    return null;
  }
  const { title } = value;
  return {
    children: <Label>{title}</Label>
  };
};

const UserTaskDataTableContainer: React.FC<InjectedOuiaProps> = ({
  ouiaContext
}) => {
  const {
    loading,
    error,
    data,
    refetch,
    networkStatus
  } = GraphQL.useGetUserTasksByStatesQuery({
    variables: {
      state: ['Ready'] // FIXME: state should not be hard-coded.
    },
    fetchPolicy: 'network-only',
    notifyOnNetworkStatusChange: true
  });

  const columns: DataTableColumn[] = [
    {
      label: 'ProcessId',
      path: '$.processId'
    },
    {
      label: 'Name',
      path: '$.name'
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
      bodyCellTransformer: stateColumnTransformer
    }
  ];

  useEffect(() => {
    return ouiaPageTypeAndObjectId(ouiaContext, 'user-tasks');
  });

  return (
    <React.Fragment>
      <UserTaskPageHeader />
      <PageSection>
        <Grid gutter="md">
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
              />
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    </React.Fragment>
  );
};

export default withOuiaContext(UserTaskDataTableContainer);
