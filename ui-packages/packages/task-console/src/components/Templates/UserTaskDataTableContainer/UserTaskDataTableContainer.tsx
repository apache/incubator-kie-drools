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
import { useGetUserTasksByStatesQuery } from '../../../graphql/types';
import {
  ouiaPageTypeAndObjectId,
  KogitoSpinner,
  DataTable
} from '@kogito-apps/common';
import {
  ICell,
  ITransform,
  IFormatterValueType
} from '@patternfly/react-table';

const UserTaskLoadingComponent = (
  <Bullseye>
    <KogitoSpinner spinnerText="Loading user tasks..." />
  </Bullseye>
);

const stateColumnTransformer: ITransform = (value: IFormatterValueType) => {
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
  } = useGetUserTasksByStatesQuery({
    variables: {
      state: ['Ready'] // FIXME: state should not be hard-coded.
    },
    fetchPolicy: 'network-only',
    notifyOnNetworkStatusChange: true
  });

  const columns: ICell[] = [
    {
      title: 'ProcessId',
      data: 'processId'
    },
    {
      title: 'Name',
      data: 'name'
    },
    {
      title: 'Priority',
      data: 'priority'
    },
    {
      title: 'ProcessInstanceId',
      data: 'processInstanceId'
    },
    {
      title: 'State',
      data: 'state',
      cellTransforms: [stateColumnTransformer]
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
