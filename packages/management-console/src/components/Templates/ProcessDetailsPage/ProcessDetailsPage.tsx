import {
  Breadcrumb,
  BreadcrumbItem,
  Grid,
  GridItem,
  Page,
  PageSection,
  Title,
  Card,
  Bullseye,
  Button,
  Split,
  SplitItem,
  OverflowMenu,
  OverflowMenuContent,
  OverflowMenuGroup
} from '@patternfly/react-core';
import React, { useState } from 'react';
import { Link, Redirect } from 'react-router-dom';
import ProcessDetails from '../../Organisms/ProcessDetails/ProcessDetails';
import ProcessDetailsProcessDiagram from '../../Organisms/ProcessDetailsProcessDiagram/ProcessDetailsProcessDiagram';
import ProcessDetailsProcessVariables from '../../Organisms/ProcessDetailsProcessVariables/ProcessDetailsProcessVariables';
import ProcessDetailsTimeline from '../../Organisms/ProcessDetailsTimeline/ProcessDetailsTimeline';
import './ProcessDetailsPage.css';
import {
  useGetProcessInstanceByIdQuery,
  ProcessInstanceState
} from '../../../graphql/types';
import ProcessDescriptor from '../../Molecules/ProcessDescriptor/ProcessDescriptor';
import SpinnerComponent from '../../Atoms/SpinnerComponent/SpinnerComponent';
import ServerErrorsComponent from '../../Molecules/ServerErrorsComponent/ServerErrorsComponent';
import PageTitleComponent from '../../Molecules/PageTitleComponent/PageTitleComponent';
import ProcessBulkModalComponent from '../../Atoms/ProcessBulkModalComponent/ProcessBulkModalComponent';
import axios from 'axios';
import { InfoCircleIcon } from '@patternfly/react-icons';

const ProcessDetailsPage = ({ match }) => {
  const [isSkipModalOpen, setIsSkipModalOpen] = useState<boolean>(false);
  const [isRetryModalOpen, setIsRetryModalOpen] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<string>('');
  const [titleType, setTitleType] = useState<string>('');
  const [modalContent, setModalContent] = useState<string>('');
  const id = match.params.instanceID;
  const [isAbortModalOpen, setIsAbortModalOpen] = useState<boolean>(false);
  const currentPage = JSON.parse(window.localStorage.getItem('state'))

  const { loading, error, data } = useGetProcessInstanceByIdQuery({
    variables: { id }
  });

  const handleAbortModalToggle = () => {
    setIsAbortModalOpen(!isAbortModalOpen);
  };

  const handleSkipModalToggle = () => {
    setIsSkipModalOpen(!isSkipModalOpen);
  };

  const handleRetryModalToggle = () => {
    setIsRetryModalOpen(!isRetryModalOpen);
  };

  const handleAbortInstance = () => {
    axios
      .delete(
        `${data.ProcessInstances[0].serviceUrl}/management/processes/${data.ProcessInstances[0].processId}/instances/${data.ProcessInstances[0].id}`
      )
      .then(() => {
        setTitleType('success');
        data.ProcessInstances[0].state = ProcessInstanceState.Aborted;
        handleAbortModalToggle();
      })
      .catch(() => {
        setTitleType('failure');
        handleAbortModalToggle();
      });
  };

  const handleSkip = () => {
    setModalTitle('Skip operation');
    axios
      .post(
        `${data.ProcessInstances[0].serviceUrl}/management/processes/${data.ProcessInstances[0].processId}/instances/${data.ProcessInstances[0].id}/skip`
      )
      .then(() => {
        setTitleType('success');
        setModalContent(
          'Process execution has successfully skipped node which was in error state.'
        );
        handleSkipModalToggle();
      })
      .catch(axiosError => {
        setTitleType('failure');
        setModalContent(
          `Process execution failed to skip node which is in error state. Message: ${JSON.stringify(
            axiosError.message
          )}`
        );

        handleSkipModalToggle();
      });
  };

  const handleRetry = () => {
    setModalTitle('Retry operation');
    axios
      .post(
        `${data.ProcessInstances[0].serviceUrl}/management/processes/${data.ProcessInstances[0].processId}/instances/${data.ProcessInstances[0].id}/retrigger`
      )
      .then(() => {
        setTitleType('success');
        setModalContent(
          `Process execution has successfully re-executed node which was in error state.`
        );
        handleRetryModalToggle();
      })
      .catch(axiosError => {
        setTitleType('failure');
        setModalContent(
          `Process execution failed to re-execute node which is in error state. Message: ${JSON.stringify(
            axiosError.message
          )}`
        );
        handleRetryModalToggle();
      });
  };

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

  const abortButton = () => {
    if (
      (data.ProcessInstances[0].state === ProcessInstanceState.Active ||
        data.ProcessInstances[0].state === ProcessInstanceState.Error ||
        data.ProcessInstances[0].state === ProcessInstanceState.Suspended) &&
      data.ProcessInstances[0].addons.includes('process-management') &&
      data.ProcessInstances[0].serviceUrl !== null
    ) {
      return (
        <Button variant="secondary" onClick={handleAbortInstance}>
          Abort
        </Button>
      );
    } else {
      return (
        <Button variant="secondary" isDisabled>
          Abort
        </Button>
      );
    }
  };

  let prevPath;
  const BreadCrumb = []
  let BreadCrumbRoute = []
  if (data) {
    const result = data.ProcessInstances;
    if (currentPage) {
      const tempPath = currentPage.prev.split('/')
      prevPath = tempPath.filter(item => item)
      BreadCrumb.push(...prevPath)
      let sum = '';
      BreadCrumbRoute = BreadCrumb.map(elem => sum = (sum) + `/${elem}`);
    }
    if (result.length === 0) {
      return (
        <Redirect
          to={{
            pathname: '/NoData',
            state: {
              prev: currentPage ? currentPage.prev : '/ProcessInstances',
              title: 'Process not found',
              description: `Process instance with the id ${id} not found`,
              buttonText: currentPage ? `Go to ${prevPath[0].replace(/([A-Z])/g, ' $1').trim().toLowerCase()}` : 'Go to process instances'
            }
          }}
        />
      );
    }
  }

  return (
    <>
      {!error ?
        (<>
          <PageSection variant="light">
            <ProcessBulkModalComponent
              isModalLarge={false}
              isModalOpen={isAbortModalOpen}
              handleModalToggle={handleAbortModalToggle}
              checkedArray={data && [data.ProcessInstances[0].state]}
              modalTitle={setTitle('success', 'Abort operation')}
              isSingleAbort={true}
              abortedMessageObj={
                data && {
                  [data.ProcessInstances[0].id]: data.ProcessInstances[0]
                }
              }
              completedMessageObj={{}}
              isAbortModalOpen={isAbortModalOpen}
            />
            <ProcessBulkModalComponent
              isModalLarge={false}
              isModalOpen={
                modalTitle === 'Skip operation'
                  ? isSkipModalOpen
                  : modalTitle === 'Retry operation' && isRetryModalOpen
              }
              handleModalToggle={
                modalTitle === 'Skip operation'
                  ? handleSkipModalToggle
                  : modalTitle === 'Retry operation'
                    ? handleRetryModalToggle
                    : null
              }
              checkedArray={data && [data.ProcessInstances[0].state]}
              modalTitle={setTitle(titleType, modalTitle)}
              modalContent={modalContent}
            />
            <PageTitleComponent title="Process Details" />
            {!loading ?
              (<Grid gutter="md" span={12} lg={6} xl={4}>
                <GridItem span={12}>
                  <Breadcrumb>
                    <BreadcrumbItem>
                      <Link to={'/'}>Home</Link>
                    </BreadcrumbItem>
                    {BreadCrumb.map((item, index) => {
                      return (
                        <BreadcrumbItem key={index}>
                          <Link to={BreadCrumbRoute[index]}>
                            {item.replace(/([A-Z])/g, ' $1').trim()}
                          </Link>
                        </BreadcrumbItem>
                      );
                      // }
                    })}
                    <BreadcrumbItem isActive>
                      {data.ProcessInstances[0].processName}
                    </BreadcrumbItem>
                  </Breadcrumb>
                </GridItem>
              </Grid>
              ) : ''}
          </PageSection>
          <PageSection>
            {!loading ? (
              <Grid gutter="md" span={12} lg={6} xl={4}>
                <GridItem span={12}>
                  <Split
                    gutter={'md'}
                    component={'div'}
                    className="pf-u-align-items-center"
                  >
                    <SplitItem isFilled={true}>
                      <Title headingLevel="h1" size="4xl">
                        <ProcessDescriptor
                          processInstanceData={data.ProcessInstances[0]}
                        />
                      </Title>
                    </SplitItem>
                    <SplitItem>
                      <OverflowMenu breakpoint="lg">
                        <OverflowMenuContent isPersistent>
                          <OverflowMenuGroup groupType="button" isPersistent>
                            {abortButton()}
                          </OverflowMenuGroup>
                        </OverflowMenuContent>
                      </OverflowMenu>
                    </SplitItem>
                  </Split>
                </GridItem>
                {currentPage && <GridItem>
                  <ProcessDetails data={data} from={currentPage} />
                </GridItem>}
                <GridItem>
                  <ProcessDetailsProcessVariables data={data} />
                </GridItem>
                <GridItem>
                  <ProcessDetailsTimeline
                    data={data.ProcessInstances[0]}
                    handleSkip={handleSkip}
                    handleRetry={handleRetry}
                  />
                </GridItem>
              </Grid>
            ) : (
                <Card>
                  <Bullseye>
                    <SpinnerComponent spinnerText="Loading process details..." />
                  </Bullseye>
                </Card>
              )}
          </PageSection>
        </>) : (<ServerErrorsComponent message={error.message} />)}
    </>);
};

export default ProcessDetailsPage;
