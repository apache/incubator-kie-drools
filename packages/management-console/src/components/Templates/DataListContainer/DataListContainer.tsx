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
import DataListToolbarComponent from '../../Molecules/DataListToolbarComponent/DataListToolbarComponent';
import './DataList.css';
import DataListComponent from '../../Organisms/DataListComponent/DataListComponent';

const DataListContainer: React.FC<{}> = () => {
  const [isActiveChecked, setIsActiveChecked] = useState<boolean>(true);
  const [isCompletedChecked, setIsCompletedChecked] = useState<boolean>(false);
  const [isAbortChecked, setisAbortChecked] = useState<boolean>(false);
  const [isErrorChecked, setisErrorChecked] = useState<boolean>(false);
  const [isSuspendedChecked, setisSuspendedChecked] = useState<boolean>(false);
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

  const handleChange = (checked, event) => {
    if (event.target.name === 'isActiveChecked') {
      setIsActiveChecked(isActiveChecked ? false : true);
      if (!isActiveChecked === true) {
        setCheckedArray([...checkedArray, 'ACTIVE']);
      } else if (!isActiveChecked === false) {
        const tempArr = checkedArray.slice();
        _.remove(tempArr, _temp => {
          return _temp === 'ACTIVE';
        });
        setCheckedArray(tempArr);
      }
    }

    if (event.target.name === 'isCompletedChecked') {
      setIsCompletedChecked(isCompletedChecked ? false : true);
      if (!isCompletedChecked === true) {
        setCheckedArray([...checkedArray, 'COMPLETED']);
      } else if (!isCompletedChecked === false) {
        const tempArr = checkedArray.slice();
        _.remove(tempArr, _temp => {
          return _temp === 'COMPLETED';
        });
        setCheckedArray(tempArr);
      }
    }
    if (event.target.name === 'isAbortChecked') {
      setisAbortChecked(isAbortChecked ? false : true);
      if (!isAbortChecked === true) {
        setCheckedArray([...checkedArray, 'ABORTED']);
      } else if (!isAbortChecked === false) {
        const tempArr = checkedArray.slice();
        _.remove(tempArr, _temp => {
          return _temp === 'ABORTED';
        });
        setCheckedArray(tempArr);
      }
    }
    if (event.target.name === 'isErrorChecked') {
      setisErrorChecked(isErrorChecked ? false : true);
      if (!isErrorChecked === true) {
        setCheckedArray([...checkedArray, 'ERROR']);
      } else if (!isErrorChecked === false) {
        const tempArr = checkedArray.slice();
        _.remove(tempArr, _temp => {
          return _temp === 'ERROR';
        });
        setCheckedArray(tempArr);
      }
    }
    if (event.target.name === 'isSuspendedChecked') {
      setisSuspendedChecked(isSuspendedChecked ? false : true);
      if (!isSuspendedChecked === true) {
        setCheckedArray([...checkedArray, 'SUSPENDED']);
      } else if (!isSuspendedChecked === false) {
        const tempArr = checkedArray.slice();
        _.remove(tempArr, _temp => {
          return _temp === 'SUSPENDED';
        });
        setCheckedArray(tempArr);
      }
    }
  };

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

  const removeChecked = async id => {
    const newCheckedArray = checkedArray.filter(x => x !== id);
    setCheckedArray(newCheckedArray);
    await onFilterClick(newCheckedArray);
    if (id.toString().toLowerCase() === 'active') {
      setIsActiveChecked(false);
    } else if (id.toString().toLowerCase() === 'completed') {
      setIsCompletedChecked(false);
    } else if (id.toString().toLowerCase() === 'aborted') {
      setisAbortChecked(false);
    } else if (id.toString().toLowerCase() === 'error') {
      setisErrorChecked(false);
    } else if (id.toString().toLowerCase() === 'suspended') {
      setisSuspendedChecked(false);
    }
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
                <DataListToolbarComponent
                  isActive={isActiveChecked}
                  isComplete={isCompletedChecked}
                  isAborted={isAbortChecked}
                  isError={isErrorChecked}
                  isSuspended={isSuspendedChecked}
                  handleChange={handleChange}
                  checkedArray={checkedArray}
                  filterClick={onFilterClick}
                  removeCheck={removeChecked}
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
