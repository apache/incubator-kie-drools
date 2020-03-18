import {
  Breadcrumb,
  BreadcrumbItem,
  Card,
  Grid,
  GridItem,
  PageSection
} from '@patternfly/react-core';
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import PageTitleComponent from '../../Molecules/PageTitleComponent/PageTitleComponent';
import DataToolbarComponent from '../../Molecules/DataToolbarComponent/DataToolbarComponent';
import './DataList.css';
import DataListComponent from '../../Organisms/DataListComponent/DataListComponent';
import EmptyStateComponent from '../../Atoms/EmptyStateComponent/EmptyStateComponent';
import ProcessBulkModalComponent from '../../Atoms/ProcessBulkModalComponent/ProcessBulkModalComponent';
import { useGetProcessInstancesLazyQuery } from '../../../graphql/types';
import axios from 'axios';
import { InfoCircleIcon } from '@patternfly/react-icons';

const DataListContainer: React.FC<{}> = () => {
  const [initData, setInitData] = useState<any>([]);
  const [checkedArray, setCheckedArray] = useState<any>(['ACTIVE']);
  const [isLoading, setIsLoading] = useState(false);
  const [isError, setIsError] = useState(false);
  const [isStatusSelected, setIsStatusSelected] = useState(true);
  const [filters, setFilters] = useState(checkedArray);
  const [abortedObj, setAbortedObj] = useState({});
  const [isAbortModalOpen, setIsAbortModalOpen] = useState(false);
  const [abortedMessageObj, setAbortedMessageObj] = useState({});
  const [completedMessageObj, setCompletedMessageObj] = useState({});
  const [titleType, setTitleType] = useState('');
  const [modalTitle, setModalTitle] = useState('');
  const [
    getProcessInstances,
    { loading, data }
  ] = useGetProcessInstancesLazyQuery({
    fetchPolicy: 'network-only',
    notifyOnNetworkStatusChange: true
  });

  const handleAbortModalToggle = () => {
    setIsAbortModalOpen(!isAbortModalOpen);
  };

  const onFilterClick = async (arr = checkedArray) => {
    setIsLoading(true);
    setIsError(false);
    setAbortedObj({});
    setAbortedMessageObj({});
    setCompletedMessageObj({});
    setIsStatusSelected(true);
    getProcessInstances({ variables: { state: arr } });
  };

  useEffect(() => {
    setAbortedObj({});
    setAbortedMessageObj({});
    setCompletedMessageObj({});
    setIsLoading(loading);
    if (!loading && data !== undefined) {
      data.ProcessInstances.map((instance: any) => {
        instance.isChecked = false;
        instance.isOpen = false;
      });
    }
    setInitData(data);
  }, [data]);

  const setTitle = (titleStatus, titleText) => {
    switch (titleStatus) {
      case 'success':
        return (
          <>
            <InfoCircleIcon
              className="pf-u-mr-sm"
              color="var(--pf-global--info-color--100)"
            />{' '}
            {titleText}{' '}
          </>
        );
      case 'failure':
        return (
          <>
            <InfoCircleIcon
              className="pf-u-mr-sm"
              color="var(--pf-global--danger-color--100)"
            />{' '}
            {titleText}{' '}
          </>
        );
    }
  };

  const handleAbortAll = () => {
    const tempAbortedObj = { ...abortedObj };
    const completedAndAborted = {};
    for (const [id, processInstance] of Object.entries(tempAbortedObj)) {
      initData.ProcessInstances.map(instance => {
        if (instance.id === id) {
          if (instance.state === 'COMPLETED' || instance.state === 'ABORTED') {
            completedAndAborted[id] = processInstance;
            delete tempAbortedObj[id];
          } else {
            instance.state = 'ABORTED';
          }
        }
        if (instance.childDataList !== undefined) {
          instance.childDataList.map(child => {
            if (child.id === id) {
              if (child.state === 'COMPLETED' || child.state === 'ABORTED') {
                completedAndAborted[id] = processInstance;
                delete tempAbortedObj[id];
              } else {
                child.state = 'ABORTED';
              }
            }
          });
        }
      });
    }
    const endpoint = initData.ProcessInstances[0].endpoint;
    const promiseArray = []
    Object.keys(tempAbortedObj).forEach((id: string) => {
      promiseArray.push(
        axios.delete(`${endpoint}/management/processes/${tempAbortedObj[id].processId}/instances/${tempAbortedObj[id].id}`)
      )
    })
    setModalTitle('Abort operation');
    Promise.all(promiseArray).then(() => {
      setTitleType('success');
      setAbortedMessageObj(tempAbortedObj);
      setCompletedMessageObj(completedAndAborted);
      handleAbortModalToggle();
    }).catch(() => {
      setTitleType('failure');
      setAbortedMessageObj(tempAbortedObj);
      setCompletedMessageObj(completedAndAborted);
      handleAbortModalToggle();
    })
  };
  return (
    <React.Fragment>
      <ProcessBulkModalComponent
        isModalLarge={false}
        modalTitle={
          titleType === 'success'
            ? setTitle(titleType, modalTitle)
            : setTitle(titleType, modalTitle)
        }
        isModalOpen={isAbortModalOpen}
        abortedMessageObj={abortedMessageObj}
        completedMessageObj={completedMessageObj}
        isAbortModalOpen={isAbortModalOpen}
        checkedArray={checkedArray}
        handleModalToggle={handleAbortModalToggle}
        isSingleAbort={false}
      />
      <PageSection variant="light">
        <PageTitleComponent title="Process Instances" />
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
                  setIsStatusSelected={setIsStatusSelected}
                  filters={filters}
                  setFilters={setFilters}
                  initData={initData}
                  setInitData={setInitData}
                  abortedObj={abortedObj}
                  setAbortedObj={setAbortedObj}
                  handleAbortAll={handleAbortAll}
                />
              )}
              {isStatusSelected ? (
                <DataListComponent
                  initData={initData}
                  setInitData={setInitData}
                  isLoading={isLoading}
                  setIsLoading={setIsLoading}
                  setIsError={setIsError}
                  checkedArray={checkedArray}
                  abortedObj={abortedObj}
                  setAbortedObj={setAbortedObj}
                />
              ) : (
                <EmptyStateComponent
                  iconType="warningTriangleIcon1"
                  title="No status is selected"
                  body="Try selecting at least one status to see results"
                  filterClick={onFilterClick}
                  setFilters={setFilters}
                  setCheckedArray={setCheckedArray}
                />
              )}
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    </React.Fragment>
  );
};

export default DataListContainer;
