import { useApolloClient } from '@apollo/react-hooks';
import {
  Breadcrumb,
  BreadcrumbItem,
  Card,
  Grid,
  GridItem,
  PageSection
} from '@patternfly/react-core';
import gql from 'graphql-tag';
import _ from 'lodash';
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import DataListTitleComponent from '../../Molecules/DataListTitleComponent/DataListTitleComponent';
import DataToolbarComponent from '../../Molecules/DataToolbarComponent/DataToolbarComponent';
import './DataList.css';
import DataListComponent from '../../Organisms/DataListComponent/DataListComponent';

const DataListContainer: React.FC<{}> = () => {
  const [initData, setInitData] = useState<any>([]);
  const [checkedArray, setCheckedArray] = useState<any>(['ACTIVE']);
  const [isLoading, setIsLoading] = useState(false);
  const [isError, setIsError] = useState(false);
  const client = useApolloClient();

  /* tslint:disable:no-string-literal */
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
        addons
        endpoint
        error {
          nodeDefinitionId
          message
        }
      }
    }
  `;

  const onFilterClick = async (arr = checkedArray) => {
    setIsLoading(true);
    setIsError(false);
    await client
      .query({
        query: GET_INSTANCES,
        variables: {
          state: arr
        },
        fetchPolicy: 'network-only'
      })
      .then(result => {
        setIsLoading(result.loading);
        setInitData(result.data);
      });
  };

  const BreadcrumbStyle = {
    paddingBottom: '20px'
  };

  return (
    <React.Fragment>
      <PageSection variant="light">
        <DataListTitleComponent />
        <Breadcrumb>
          <BreadcrumbItem>
            <Link to={'/'}>Home</Link>
          </BreadcrumbItem>
          <BreadcrumbItem isActive>Process instances</BreadcrumbItem>
        </Breadcrumb>
      </PageSection>
      <PageSection>
        <Grid gutter="md">
          <GridItem span={12}>
            <Card className="dataList">
              {!isError && (
                <DataToolbarComponent
                  checkedArray={checkedArray}
                  filterClick={onFilterClick}
                  setCheckedArray={setCheckedArray}
                />
              )}
              <DataListComponent
                initData={initData}
                setInitData={setInitData}
                isLoading={isLoading}
                setIsError={setIsError}
              />
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    </React.Fragment>
  );
};

export default DataListContainer;
