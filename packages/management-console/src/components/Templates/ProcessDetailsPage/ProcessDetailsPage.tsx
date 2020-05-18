import {
  Breadcrumb,
  BreadcrumbItem,
  Grid,
  GridItem,
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
import { ServerErrors } from '@kogito-apps/common/src/components';
import React, { useState, useEffect } from 'react';
import { Link, Redirect } from 'react-router-dom';
import ProcessDetails from '../../Organisms/ProcessDetails/ProcessDetails';
import ProcessDetailsProcessVariables from '../../Organisms/ProcessDetailsProcessVariables/ProcessDetailsProcessVariables';
import ProcessDetailsTimeline from '../../Organisms/ProcessDetailsTimeline/ProcessDetailsTimeline';
import './ProcessDetailsPage.css';
import {
  useGetProcessInstanceByIdQuery,
  ProcessInstanceState
} from '../../../graphql/types';
import ProcessDescriptor from '../../Molecules/ProcessDescriptor/ProcessDescriptor';
import SpinnerComponent from '../../Atoms/SpinnerComponent/SpinnerComponent';
import PageTitleComponent from '../../Molecules/PageTitleComponent/PageTitleComponent';
import ProcessBulkModalComponent from '../../Atoms/ProcessBulkModalComponent/ProcessBulkModalComponent';
import {
  handleAbort,
  setTitle,
  isModalOpen,
  modalToggle
} from '../../../utils/Utils';

const ProcessDetailsPage = props => {
  const id = props.match.params.instanceID;
  const [isSkipModalOpen, setIsSkipModalOpen] = useState<boolean>(false);
  const [isRetryModalOpen, setIsRetryModalOpen] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<string>('');
  const [titleType, setTitleType] = useState<string>('');
  const [modalContent, setModalContent] = useState<string>('');
  const [isAbortModalOpen, setIsAbortModalOpen] = useState<boolean>(false);
  const currentPage = JSON.parse(window.localStorage.getItem('state'));

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

  useEffect(() => {
    window.onpopstate = () => {
      props.history.push({ state: { ...props.location.state } });
    };
  });

  const abortButton = () => {
    if (
      (data.ProcessInstances[0].state === ProcessInstanceState.Active ||
        data.ProcessInstances[0].state === ProcessInstanceState.Error ||
        data.ProcessInstances[0].state === ProcessInstanceState.Suspended) &&
      data.ProcessInstances[0].addons.includes('process-management') &&
      data.ProcessInstances[0].serviceUrl !== null
    ) {
      return (
        <Button
          variant="secondary"
          onClick={() =>
            handleAbort(
              data.ProcessInstances[0],
              setModalTitle,
              setTitleType,
              setModalContent,
              handleAbortModalToggle
            )
          }
        >
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
  const BreadCrumb = [];
  let BreadCrumbRoute = [];
  if (data) {
    const result = data.ProcessInstances;
    if (currentPage) {
      const tempPath = currentPage.prev.split('/');
      prevPath = tempPath.filter(item => item);
      BreadCrumb.push(...prevPath);
      let sum = '';
      BreadCrumbRoute = BreadCrumb.map(elem => (sum = sum + `/${elem}`));
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
              buttonText: currentPage
                ? `Go to ${prevPath[0]
                    .replace(/([A-Z])/g, ' $1')
                    .trim()
                    .toLowerCase()}`
                : 'Go to process instances',
              rememberedData: { ...props.location.state }
            }
          }}
        />
      );
    }
  }

  return (
    <>
      {!error ? (
        <>
          <PageSection variant="light">
            <ProcessBulkModalComponent
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
              isModalOpen={isModalOpen(
                modalTitle,
                isSkipModalOpen,
                isRetryModalOpen
              )}
              handleModalToggle={modalToggle(
                modalTitle,
                handleSkipModalToggle,
                handleRetryModalToggle
              )}
              checkedArray={data && [data.ProcessInstances[0].state]}
              modalTitle={setTitle(titleType, modalTitle)}
              modalContent={modalContent}
            />
            <PageTitleComponent title="Process Details" />
            {!loading ? (
              <Grid gutter="md" span={12} lg={6} xl={4}>
                <GridItem span={12}>
                  <Breadcrumb>
                    <BreadcrumbItem>
                      <Link to={'/'}>Home</Link>
                    </BreadcrumbItem>
                    {BreadCrumb.map((item, index) => {
                      if (index === 1) {
                        return (
                          <BreadcrumbItem key={index}>
                            <Link
                              to={
                                props.location.state && {
                                  pathname: BreadCrumbRoute[index],
                                  state: { ...props.location.state }
                                }
                              }
                            >
                              {item.replace(/([A-Z])/g, ' $1').trim()}
                            </Link>
                          </BreadcrumbItem>
                        );
                      } else {
                        return (
                          <BreadcrumbItem key={index}>
                            <Link to={BreadCrumbRoute[index]}>
                              {item.replace(/([A-Z])/g, ' $1').trim()}
                            </Link>
                          </BreadcrumbItem>
                        );
                      }
                    })}
                    <BreadcrumbItem isActive>
                      {data.ProcessInstances[0].processName}
                    </BreadcrumbItem>
                  </Breadcrumb>
                </GridItem>
              </Grid>
            ) : (
              ''
            )}
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
                      <Title
                        headingLevel="h2"
                        size="4xl"
                        className="kogito-management-console--details__title"
                      >
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
                {currentPage && (
                  <GridItem>
                    <ProcessDetails data={data} from={currentPage} />
                  </GridItem>
                )}
                <GridItem>
                  <ProcessDetailsProcessVariables data={data} />
                </GridItem>
                <GridItem>
                  <ProcessDetailsTimeline
                    data={data.ProcessInstances[0]}
                    setModalContent={setModalContent}
                    setModalTitle={setModalTitle}
                    setTitleType={setTitleType}
                    handleSkipModalToggle={handleSkipModalToggle}
                    handleRetryModalToggle={handleRetryModalToggle}
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
        </>
      ) : (
        <ServerErrors error={error} />
      )}
    </>
  );
};

export default ProcessDetailsPage;
