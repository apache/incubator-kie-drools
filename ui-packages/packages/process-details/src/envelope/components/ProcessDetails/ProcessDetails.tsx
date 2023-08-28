/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useEffect, useState } from 'react';
import { Flex, FlexItem } from '@patternfly/react-core/dist/js/layouts/Flex';
import { Grid, GridItem } from '@patternfly/react-core/dist/js/layouts/Grid';
import { Split, SplitItem } from '@patternfly/react-core/dist/js/layouts/Split';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';
import {
  Modal,
  ModalVariant
} from '@patternfly/react-core/dist/js/components/Modal';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import {
  OverflowMenu,
  OverflowMenuContent,
  OverflowMenuGroup
} from '@patternfly/react-core/dist/js/components/OverflowMenu';
import { Card } from '@patternfly/react-core/dist/js/components/Card';
import {
  Title,
  TitleSizes
} from '@patternfly/react-core/dist/js/components/Title';
import { SyncIcon } from '@patternfly/react-icons/dist/js/icons/sync-icon';
import { InfoCircleIcon } from '@patternfly/react-icons/dist/js/icons/info-circle-icon';
import { ItemDescriptor } from '@kogito-apps/components-common/dist/components/ItemDescriptor';
import { KogitoSpinner } from '@kogito-apps/components-common/dist/components/KogitoSpinner';
import { ServerErrors } from '@kogito-apps/components-common/dist/components/ServerErrors';
import {
  Job,
  ProcessInstance,
  ProcessInstanceState,
  TitleType,
  SvgSuccessResponse,
  SvgErrorResponse
} from '@kogito-apps/management-console-shared/dist/types';
import { setTitle } from '@kogito-apps/management-console-shared/dist/utils/Utils';
import { ProcessInfoModal } from '@kogito-apps/management-console-shared/dist/components/ProcessInfoModal';
import { DiagramPreviewSize, ProcessDetailsDriver } from '../../../api';
import ProcessDiagram from '../ProcessDiagram/ProcessDiagram';
import JobsPanel from '../JobsPanel/JobsPanel';
import ProcessDetailsErrorModal from '../ProcessDetailsErrorModal/ProcessDetailsErrorModal';
import SVG from 'react-inlinesvg';
import '../styles.css';
import ProcessDetailsPanel from '../ProcessDetailsPanel/ProcessDetailsPanel';
import ProcessDetailsNodeTrigger from '../ProcessDetailsNodeTrigger/ProcessDetailsNodeTrigger';
import ProcessVariables from '../ProcessVariables/ProcessVariables';
import ProcessDetailsMilestonesPanel from '../ProcessDetailsMilestonesPanel/ProcessDetailsMilestonesPanel';
import ProcessDetailsTimelinePanel from '../ProcessDetailsTimelinePanel/ProcessDetailsTimelinePanel';
import SwfCombinedEditor from '../SwfCombinedEditor/SwfCombinedEditor';

interface ProcessDetailsProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: ProcessDetailsDriver;
  processDetails: ProcessInstance;
  omittedProcessTimelineEvents: string[];
  diagramPreviewSize?: DiagramPreviewSize;
  showSwfDiagram: boolean;
  isStunnerEnabled?: boolean;
  singularProcessLabel: string;
  pluralProcessLabel: string;
}

type svgResponse = SvgSuccessResponse | SvgErrorResponse;

const ProcessDetails: React.FC<ProcessDetailsProps> = ({
  isEnvelopeConnectedToChannel,
  driver,
  processDetails,
  omittedProcessTimelineEvents,
  diagramPreviewSize,
  showSwfDiagram,
  singularProcessLabel,
  pluralProcessLabel,
  isStunnerEnabled
}) => {
  const [data, setData] = useState<ProcessInstance>({} as ProcessInstance);
  const [jobs, setJobs] = useState<Job[]>([]);
  const [updateJson, setUpdateJson] = useState<any>({});
  const [displayLabel, setDisplayLabel] = useState<boolean>(false);
  const [displaySuccess, setDisplaySuccess] = useState<boolean>(false);
  const [errorModalOpen, setErrorModalOpen] = useState<boolean>(false);
  const [confirmationModal, setConfirmationModal] = useState<boolean>(false);
  const [variableError, setVariableError] = useState('');
  const [svg, setSvg] = useState<JSX.Element>(null);
  const [svgError, setSvgError] = useState<string>('');
  const [error, setError] = useState<string>('');
  const [svgErrorModalOpen, setSvgErrorModalOpen] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isInfoModalOpen, setIsInfoModalOpen] = useState<boolean>(false);
  const [infoModalTitle, setInfoModalTitle] = useState<string>('');
  const [titleType, setTitleType] = useState<string>('');
  const [infoModalContent, setInfoModalContent] = useState<string>('');
  const handleReload = async (): Promise<void> => {
    setIsLoading(true);
    try {
      const processResponse: ProcessInstance = await driver.processDetailsQuery(
        processDetails.id
      );
      processResponse && setData(processResponse);
      getAllJobs();
      setIsLoading(false);
    } catch (errorString) {
      setError(errorString);
      setIsLoading(false);
    }
  };

  const getAllJobs = async (): Promise<void> => {
    const jobsResponse: Job[] = await driver.jobsQuery(processDetails.id);
    jobsResponse && setJobs(jobsResponse);
  };

  const handleSvgErrorModal = (): void => {
    setSvgErrorModalOpen(!svgErrorModalOpen);
  };

  const errorModalAction: JSX.Element[] = [
    <Button
      data-testid="svg-error-modal"
      key="confirm-selection"
      variant="primary"
      onClick={handleSvgErrorModal}
    >
      OK
    </Button>
  ];

  useEffect(() => {
    const handleSvgApi = async (): Promise<void> => {
      if (data && data.id === processDetails.id) {
        const response: svgResponse = await driver.getProcessDiagram(data);
        if (response && response.svg) {
          const temp = <SVG src={response.svg} />;
          setSvg(temp);
        } else if (response && response.error) {
          setSvgError(response.error);
        }
      }
    };
    const getVariableJSON = (): void => {
      if (data && data.id === processDetails.id) {
        setUpdateJson(JSON.parse(data.variables));
      }
    };
    /* istanbul ignore else*/
    if (isEnvelopeConnectedToChannel) {
      handleSvgApi();
      getVariableJSON();
    }
  }, [data]);

  useEffect(() => {
    if (svgError && svgError.length > 0 && !showSwfDiagram) {
      setSvgErrorModalOpen(true);
    }
  }, [svgError]);

  useEffect(() => {
    if (variableError && variableError.length > 0) {
      setErrorModalOpen(true);
    }
  }, [variableError]);

  useEffect(() => {
    /* istanbul ignore else*/
    if (isEnvelopeConnectedToChannel) {
      setData(processDetails);
      getAllJobs();
    }
  }, [isEnvelopeConnectedToChannel]);

  /* istanbul ignore next */
  const handleSave = (): void => {
    driver
      .handleProcessVariableUpdate(data, updateJson)
      .then((updatedJson: Record<string, unknown>) => {
        setUpdateJson(updatedJson);
        setDisplayLabel(false);
        setDisplaySuccess(true);
        setTimeout(() => {
          setDisplaySuccess(false);
        }, 2000);
      })
      .catch((errorMessage: string) => {
        setVariableError(errorMessage);
      });
  };

  const updateVariablesButton = (): JSX.Element => {
    /* istanbul ignore else*/
    if (data.serviceUrl !== null) {
      return (
        <Button
          variant="secondary"
          id="save-button"
          className="kogito-process-details--details__buttonMargin"
          onClick={handleSave}
          isDisabled={!displayLabel}
          data-testid="save-button"
        >
          Save
        </Button>
      );
    }
  };

  const handleRefresh = (): void => {
    if (displayLabel === true) {
      setConfirmationModal(true);
    } else {
      handleReload();
    }
  };

  const refreshButton = (): JSX.Element => {
    return (
      <Button
        variant="plain"
        onClick={() => {
          handleRefresh();
        }}
        id="refresh-button"
        data-testid="refresh-button"
        aria-label={'Refresh list'}
      >
        <SyncIcon />
      </Button>
    );
  };

  const handleInfoModalToggle = (): void => {
    setIsInfoModalOpen(!isInfoModalOpen);
  };

  const onAbortClick = async (
    processInstance: ProcessInstance
  ): Promise<void> => {
    try {
      await driver.handleProcessAbort(processInstance);
      setTitleType(TitleType.SUCCESS);
      setInfoModalTitle('Abort operation');
      setInfoModalContent(
        `The ${singularProcessLabel.toLowerCase()} ${
          processInstance.processName
        } was successfully aborted.`
      );
    } catch (abortError) {
      setTitleType(TitleType.FAILURE);
      setInfoModalTitle('Abort operation');
      setInfoModalContent(
        `Failed to abort ${singularProcessLabel.toLowerCase()} ${
          processInstance.processName
        }. Message: ${abortError.message}`
      );
    } finally {
      handleInfoModalToggle();
    }
  };

  const abortButton = (): JSX.Element => {
    if (
      (data.state === ProcessInstanceState.Active ||
        data.state === ProcessInstanceState.Error ||
        data.state === ProcessInstanceState.Suspended) &&
      data.addons.includes('process-management') &&
      data.serviceUrl !== null
    ) {
      return (
        <Button
          variant="secondary"
          id="abort-button"
          data-testid="abort-button"
          onClick={() => onAbortClick(data)}
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

  const renderProcessDiagram = (): JSX.Element => {
    return (
      <Flex>
        <FlexItem>
          {svg !== null && svg.props.src && (
            <Card>
              {' '}
              <ProcessDiagram
                svg={svg}
                width={diagramPreviewSize?.width}
                height={diagramPreviewSize?.height}
              />{' '}
            </Card>
          )}
        </FlexItem>
      </Flex>
    );
  };

  const renderSwfDiagram = (): JSX.Element => {
    return (
      <Flex>
        <FlexItem>
          <SwfCombinedEditor
            isStunnerEnabled={isStunnerEnabled}
            height={diagramPreviewSize?.height}
            width={diagramPreviewSize?.width}
            workflowInstance={data}
          />
        </FlexItem>
      </Flex>
    );
  };

  const renderProcessTimeline = (): JSX.Element => {
    return (
      <FlexItem>
        <ProcessDetailsTimelinePanel
          data={data}
          jobs={jobs}
          driver={driver}
          omittedProcessTimelineEvents={omittedProcessTimelineEvents}
        />
      </FlexItem>
    );
  };

  const renderProcessDetails = (): JSX.Element => {
    return (
      <Flex direction={{ default: 'column' }} flex={{ default: 'flex_1' }}>
        <FlexItem>
          <ProcessDetailsPanel processInstance={data} driver={driver} />
        </FlexItem>
        {data.milestones.length > 0 && (
          <FlexItem>
            <ProcessDetailsMilestonesPanel milestones={data.milestones} />
          </FlexItem>
        )}
      </Flex>
    );
  };

  const renderProcessVariables = (): JSX.Element => {
    return (
      <Flex direction={{ default: 'column' }} flex={{ default: 'flex_1' }}>
        {Object.keys(updateJson).length > 0 && (
          <FlexItem>
            <ProcessVariables
              displayLabel={displayLabel}
              displaySuccess={displaySuccess}
              setUpdateJson={setUpdateJson}
              setDisplayLabel={setDisplayLabel}
              updateJson={updateJson}
              processInstance={data}
            />
          </FlexItem>
        )}
      </Flex>
    );
  };

  const renderPanels = (): JSX.Element => {
    if (showSwfDiagram) {
      return (
        <Flex direction={{ default: 'column' }}>
          {renderSwfDiagram()}
          <Flex>
            {renderProcessDetails()}
            {renderProcessVariables()}
          </Flex>
        </Flex>
      );
    } else if (svg !== null && svg.props.src) {
      return (
        <Flex direction={{ default: 'column' }}>
          {renderProcessDiagram()}
          <Flex>
            {renderProcessDetails()}
            {renderProcessVariables()}
          </Flex>
        </Flex>
      );
    } else {
      return (
        <>
          {renderProcessDetails()}
          {renderProcessVariables()}
        </>
      );
    }
  };

  const handleConfirmationModal = (): void => {
    setConfirmationModal(!confirmationModal);
  };

  const handleConfirm = (): void => {
    window.location.reload();
    handleConfirmationModal();
  };

  const handleCancel = (): void => {
    handleConfirmationModal();
  };

  const RenderConfirmationModal = (): JSX.Element => {
    return (
      <Modal
        title=""
        header={
          <>
            <Title headingLevel="h1" size={TitleSizes['2xl']}>
              <InfoCircleIcon
                className="pf-u-mr-sm"
                color="var(--pf-global--warning-color--100)"
              />
              Refresh
            </Title>
          </>
        }
        variant={ModalVariant.small}
        isOpen={confirmationModal}
        onClose={handleConfirmationModal}
        actions={[
          <Button
            key="Ok"
            variant="primary"
            id="confirm-button"
            onClick={handleConfirm}
          >
            Ok
          </Button>,
          <Button
            key="Cancel"
            variant="link"
            id="cancel-button"
            onClick={handleCancel}
          >
            Cancel
          </Button>
        ]}
        aria-label="Confirmation modal"
        aria-labelledby="Confirmation modal"
      >
        This action discards changes made on process variables.
      </Modal>
    );
  };

  const handleErrorModal = (): void => {
    setErrorModalOpen(!errorModalOpen);
  };

  const handleRetry = (): void => {
    handleErrorModal();
    setVariableError(null);
    // tslint:disable-next-line: no-floating-promises
    handleSave();
  };

  const handleDiscard = (): void => {
    handleErrorModal();
    handleRefresh();
  };

  const errorModal = (): JSX.Element => {
    return (
      <Modal
        title=""
        header={
          <>
            <Title headingLevel="h1" size={TitleSizes['2xl']}>
              <InfoCircleIcon
                className="pf-u-mr-sm"
                color="var(--pf-global--danger-color--100)"
              />
              Error
            </Title>
          </>
        }
        variant={ModalVariant.small}
        isOpen={errorModalOpen}
        onClose={handleErrorModal}
        actions={[
          <Button
            key="Retry"
            variant="primary"
            id="retry-button"
            onClick={handleRetry}
          >
            Retry
          </Button>,
          <Button
            key="Discard"
            variant="link"
            id="discard-button"
            onClick={handleDiscard}
          >
            Discard
          </Button>
        ]}
        aria-label="Error modal"
        aria-labelledby="Error modal"
      >
        {variableError}
      </Modal>
    );
  };

  return (
    <>
      {!error ? (
        <>
          {!isLoading && Object.keys(data).length > 0 ? (
            <>
              <Grid hasGutter md={1} span={12} lg={6} xl={4}>
                <GridItem span={12}>
                  <Split
                    hasGutter={true}
                    component={'div'}
                    className="pf-u-align-items-center"
                  >
                    <SplitItem isFilled={true}>
                      <Title
                        headingLevel="h2"
                        size="4xl"
                        className="kogito-process-details--details__title"
                      >
                        <ItemDescriptor
                          itemDescription={{
                            id: data.id,
                            name: data.processName,
                            description: data.businessKey
                          }}
                        />
                      </Title>
                    </SplitItem>
                    <SplitItem>
                      <OverflowMenu breakpoint="lg">
                        <OverflowMenuContent isPersistent>
                          <OverflowMenuGroup groupType="button" isPersistent>
                            <>
                              {updateVariablesButton()}
                              {abortButton()}
                              {refreshButton()}
                            </>
                          </OverflowMenuGroup>
                        </OverflowMenuContent>
                      </OverflowMenu>
                    </SplitItem>
                  </Split>
                </GridItem>
              </Grid>
              <Flex
                direction={{ default: 'column', lg: 'row' }}
                className="kogito-process-details--details__marginSpaces"
              >
                {renderPanels()}
                <Flex
                  direction={{ default: 'column' }}
                  flex={{ default: 'flex_1' }}
                >
                  {renderProcessTimeline()}
                  <FlexItem>
                    <JobsPanel jobs={jobs} driver={driver} />
                  </FlexItem>
                  {data.addons.includes('process-management') &&
                    data.state !== ProcessInstanceState.Completed &&
                    data.state !== ProcessInstanceState.Aborted &&
                    data.serviceUrl &&
                    data.addons.includes('process-management') && (
                      <FlexItem>
                        <ProcessDetailsNodeTrigger
                          driver={driver}
                          processInstanceData={data}
                        />
                      </FlexItem>
                    )}
                </Flex>
                {errorModal()}
                {RenderConfirmationModal()}
              </Flex>
            </>
          ) : (
            <Card>
              <KogitoSpinner spinnerText="Loading process details..." />
            </Card>
          )}
          {svgErrorModalOpen && (
            <ProcessDetailsErrorModal
              errorString={svgError}
              errorModalOpen={svgErrorModalOpen}
              errorModalAction={errorModalAction}
              handleErrorModal={handleSvgErrorModal}
              label="svg error modal"
              title={setTitle('failure', 'Process Diagram')}
            />
          )}
        </>
      ) : (
        <>
          {isEnvelopeConnectedToChannel && (
            <Card className="kogito-process-details__card-size">
              <Bullseye>
                <ServerErrors error={error} variant="large" />
              </Bullseye>
            </Card>
          )}
        </>
      )}
      <ProcessInfoModal
        isModalOpen={isInfoModalOpen}
        handleModalToggle={handleInfoModalToggle}
        modalTitle={setTitle(titleType, infoModalTitle)}
        modalContent={infoModalContent}
        processName={data && data.processName}
      />
    </>
  );
};

export default ProcessDetails;
