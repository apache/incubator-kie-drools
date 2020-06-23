import {
  Breadcrumb,
  BreadcrumbItem,
  Card,
  Grid,
  GridItem,
  PageSection,
  InjectedOuiaProps,
  withOuiaContext,
  Bullseye,
  Label
} from '@patternfly/react-core';
import _ from 'lodash';
import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import PageTitle from '../../Molecules/PageTitle/PageTitle';
import './DataTable.css';
import DataTable from '../../Organisms/DataTable/DataTable';
import { useGetUserTasksByStatesQuery } from '../../../graphql/types';
import { ouiaPageTypeAndObjectId, KogitoSpinner } from '@kogito-apps/common';
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

const DataTableContainer: React.FC<InjectedOuiaProps> = ({ ouiaContext }) => {
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
      <PageSection variant="light">
        <PageTitle title="User Tasks" />
        <Breadcrumb>
          <BreadcrumbItem>
            <Link to={'/'}>Home</Link>
          </BreadcrumbItem>
          <BreadcrumbItem isActive>User Tasks</BreadcrumbItem>
        </Breadcrumb>
      </PageSection>
      <PageSection>
        <Grid gutter="md">
          <GridItem span={12}>
            <Card className="data-table">
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

export default withOuiaContext(DataTableContainer);
