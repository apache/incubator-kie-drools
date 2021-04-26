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
import {
  Grid,
  GridItem,
  Title,
  Split,
  SplitItem,
  OverflowMenu,
  OverflowMenuContent,
  OverflowMenuGroup,
  Flex,
  FlexItem,
  Card,
  Button,
  Modal,
  ModalVariant,
  TitleSizes,
  Bullseye
} from '@patternfly/react-core';
import { SyncIcon, InfoCircleIcon } from '@patternfly/react-icons';
import {
  ItemDescriptor,
  KogitoSpinner,
  ServerErrors
} from '@kogito-apps/components-common';
import {
  ProcessInstance,
  ProcessInstanceState
} from '@kogito-apps/management-console-shared';
import { ProcessDetailsDriver } from '../../../api';
import '../styles.css';

interface ProcessDetailsProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: ProcessDetailsDriver;
  id: string;
}

const ProcessDetails: React.FC<ProcessDetailsProps> = ({
  isEnvelopeConnectedToChannel,
  driver,
  id
}) => {
  const [data, setData] = useState<any>({});
  //@ts-ignore
  const [updateJson, setUpdateJson] = useState<any>({});
  //@ts-ignore
  const [displayLabel, setDisplayLabel] = useState<boolean>(false);
  //@ts-ignore
  const [displaySuccess, setDisplaySuccess] = useState<boolean>(false);
  const [errorModalOpen, setErrorModalOpen] = useState<boolean>(false);
  const [confirmationModal, setConfirmationModal] = useState<boolean>(false);
  const [variableError, setVariableError] = useState();
  //@ts-ignore
  const [svg, setSvg] = useState(null);
  const [error, setError] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const initLoad = async (): Promise<void> => {
    setIsLoading(true);
    try {
      const response: ProcessInstance = await driver.processDetailsQuery(id);
      response && setData(response);
      setIsLoading(false);
    } catch (error) {
      setError(error);
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (isEnvelopeConnectedToChannel) {
      initLoad();
    }
  }, [isEnvelopeConnectedToChannel]);

  const handleSave = async (): Promise<void> => {
    setDisplaySuccess(false);
  };

  const updateVariablesButton = (): JSX.Element => {
    if (data.serviceUrl !== null) {
      return (
        <Button
          variant="secondary"
          id="save-button"
          className="kogito-process-details--details__buttonMargin"
          onClick={handleSave}
          isDisabled={!displayLabel}
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
      initLoad();
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
        aria-label={'Refresh list'}
      >
        <SyncIcon />
      </Button>
    );
  };

  const onAbortClick = async (): Promise<void> => {
    return null;
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

  const renderProcessDiagram = (): JSX.Element => {
    return (
      <Flex>
        <FlexItem>
          {svg !== null && svg.props.src && <Card> Process Diagram</Card>}
        </FlexItem>
      </Flex>
    );
  };

  const renderProcessDetails = (): JSX.Element => {
    return (
      <Flex direction={{ default: 'column' }} flex={{ default: 'flex_1' }}>
        <FlexItem>Process Details</FlexItem>
        {data.milestones.length > 0 && <FlexItem>Milestones</FlexItem>}
      </Flex>
    );
  };

  const renderProcessVariables = (): JSX.Element => {
    return (
      <Flex direction={{ default: 'column' }} flex={{ default: 'flex_1' }}>
        {Object.keys(updateJson).length > 0 && (
          <FlexItem>Process Variables</FlexItem>
        )}
      </Flex>
    );
  };
  const renderPanels = (): JSX.Element => {
    if (svg !== null && svg.props.src) {
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
                  <FlexItem>Process Timeline</FlexItem>
                  <FlexItem>Jobs Panel</FlexItem>
                  {data.addons.includes('process-management') &&
                    data.state !== ProcessInstanceState.Completed &&
                    data.state !== ProcessInstanceState.Aborted &&
                    data.serviceUrl &&
                    data.addons.includes('process-management') && (
                      <FlexItem>Node Trigger</FlexItem>
                    )}
                </Flex>
                {errorModal()}
                {RenderConfirmationModal()}
              </Flex>
            </>
          ) : (
            <KogitoSpinner spinnerText="Loading process details..." />
          )}
        </>
      ) : (
        <>
          {isEnvelopeConnectedToChannel && (
            <Bullseye>
              <ServerErrors error={error} variant="large" />
            </Bullseye>
          )}
        </>
      )}
    </>
  );
};

export default ProcessDetails;
