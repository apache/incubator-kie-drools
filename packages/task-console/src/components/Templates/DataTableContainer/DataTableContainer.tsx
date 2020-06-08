import {
  Breadcrumb,
  BreadcrumbItem,
  Card,
  Grid,
  GridItem,
  PageSection,
  InjectedOuiaProps,
  withOuiaContext,
  Bullseye
} from '@patternfly/react-core';
import _ from 'lodash';
import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import PageTitleComponent from '../../Molecules/PageTitleComponent/PageTitleComponent';
import './DataTable.css';
import DataTable from '../../Organisms/DataTable/DataTable';
import { useGetUserTasksByStatesQuery } from '../../../graphql/types';
import { ouiaPageTypeAndObjectId, KogitoSpinner } from '@kogito-apps/common';

const UserTaskLoadingComponent = (
  <Bullseye>
    <KogitoSpinner spinnerText="Loading user tasks..." />
  </Bullseye>
);

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

  const columns = [
    'ProcessId',
    'Name',
    'Priority',
    'ProcessInstanceId',
    'State'
  ];

  useEffect(() => {
    return ouiaPageTypeAndObjectId(ouiaContext, 'user-tasks');
  });

  return (
    <React.Fragment>
      <PageSection variant="light">
        <PageTitleComponent title="User Tasks" />
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
