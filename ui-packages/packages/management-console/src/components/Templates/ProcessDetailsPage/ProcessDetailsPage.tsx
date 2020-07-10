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
  OverflowMenuGroup,
  InjectedOuiaProps,
  withOuiaContext
} from '@patternfly/react-core';
import {
  ServerErrors,
  ouiaPageTypeAndObjectId,
  ProcessDescriptor,
  KogitoSpinner,
  GraphQL
} from '@kogito-apps/common';
import React, { useState, useEffect } from 'react';
import { Link, Redirect, RouteComponentProps } from 'react-router-dom';
import ProcessDetails from '../../Organisms/ProcessDetails/ProcessDetails';
import ProcessDetailsProcessVariables from '../../Organisms/ProcessDetailsProcessVariables/ProcessDetailsProcessVariables';
import ProcessDetailsTimeline from '../../Organisms/ProcessDetailsTimeline/ProcessDetailsTimeline';
import './ProcessDetailsPage.css';
import PageTitle from '../../Molecules/PageTitle/PageTitle';
import ProcessListModal from '../../Atoms/ProcessListModal/ProcessListModal';
import { handleAbort, setTitle } from '../../../utils/Utils';
import ProcessInstanceState = GraphQL.ProcessInstanceState;

interface MatchProps {
  instanceID: string;
}

enum TitleType {
  SUCCESS = 'success',
  FAILURE = 'failure'
}

const ProcessDetailsPage: React.FC<
  RouteComponentProps<MatchProps, {}, {}> & InjectedOuiaProps
> = ({ ouiaContext, ...props }) => {
  const id = props.match.params.instanceID;
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<string>('');
  const [titleType, setTitleType] = useState<string>('');
  const [modalContent, setModalContent] = useState<string>('');
  const currentPage = JSON.parse(window.localStorage.getItem('state'));

  const { loading, error, data } = GraphQL.useGetProcessInstanceByIdQuery({
    variables: { id }
  });
  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

  useEffect(() => {
    window.onpopstate = () => {
      props.history.push({ state: { ...props.location.state } });
    };
  });

  useEffect(() => {
    return ouiaPageTypeAndObjectId(ouiaContext, 'process-instances', id);
  });

  const onShowMessage = (
    title: string,
    content: string,
    type: TitleType
  ): void => {
    setTitleType(type);
    setModalTitle(title);
    setModalContent(content);
    handleModalToggle();
  };
  const onAbortClick = () => {
    handleAbort(
      data.ProcessInstances[0],
      () =>
        onShowMessage(
          'Abort operation',
          `The process ${data.ProcessInstances[0].processName} was successfully aborted.`,
          TitleType.SUCCESS
        ),
      (errorMessage: string) =>
        onShowMessage(
          'Abort operation',
          `Failed to abort process ${data.ProcessInstances[0].processName}. Message: ${errorMessage}`,
          TitleType.FAILURE
        )
    );
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
        <Button variant="secondary" id="abort-button" onClick={onAbortClick}>
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
    /* istanbul ignore else */
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
            <ProcessListModal
              isModalOpen={isModalOpen}
              handleModalToggle={handleModalToggle}
              checkedArray={
                data &&
                data.ProcessInstances &&
                data.ProcessInstances[0] && [data.ProcessInstances[0].state]
              }
              modalTitle={setTitle(titleType, modalTitle)}
              modalContent={modalContent}
            />
            <PageTitle title="Process Details" />
            {!loading ? (
              <Grid gutter="md" span={12} lg={6} xl={4}>
                <GridItem span={12}>
                  <Breadcrumb>
                    <BreadcrumbItem>
                      <Link to={'/'}>Home</Link>
                    </BreadcrumbItem>
                    {BreadCrumb.map((item, index) => {
                      // checking the url if it contains /ProcessInstances to return the state back
                      if (
                        index === 1 ||
                        (index === 0 && item === 'ProcessInstances')
                      ) {
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
                  <ProcessDetailsTimeline data={data.ProcessInstances[0]} />
                </GridItem>
              </Grid>
            ) : (
              <Card>
                <Bullseye>
                  <KogitoSpinner spinnerText="Loading process details..." />
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

export default withOuiaContext(ProcessDetailsPage);
