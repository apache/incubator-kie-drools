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

import Moment from 'react-moment';
import {
  Card,
  CardBody,
  CardHeader,
  Title,
  Text,
  TextContent,
  TextVariants,
  Split,
  SplitItem,
  Stack,
  Dropdown,
  KebabToggle,
  DropdownItem,
  Tooltip,
  Button
} from '@patternfly/react-core';
import {
  UserIcon,
  CheckCircleIcon,
  ErrorCircleOIcon,
  OnRunningIcon,
  OutlinedClockIcon
} from '@patternfly/react-icons';
import React, { useCallback, useState } from 'react';
import '../styles.css';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import {
  setTitle,
  ProcessInfoModal,
  ProcessInstance,
  Job,
  JobsDetailsModal,
  JobsRescheduleModal,
  JobsCancelModal
} from '@kogito-apps/management-console-shared';
import { ProcessDetailsDriver } from '../../../api';
import {
  handleJobRescheduleUtil,
  handleNodeInstanceCancel,
  handleNodeInstanceRetrigger,
  handleRetry,
  handleSkip,
  jobCancel
} from '../../../utils/Utils';

export interface IOwnProps {
  data: Pick<
    ProcessInstance,
    'id' | 'nodes' | 'addons' | 'error' | 'serviceUrl' | 'processId' | 'state'
  >;
  driver: ProcessDetailsDriver;
  jobs: Job[];
  omittedProcessTimelineEvents?: string[];
}
enum TitleType {
  SUCCESS = 'success',
  FAILURE = 'failure'
}
const ProcessDetailsTimelinePanel: React.FC<IOwnProps & OUIAProps> = ({
  data,
  jobs,
  driver,
  omittedProcessTimelineEvents,
  ouiaId,
  ouiaSafe
}) => {
  const [kebabOpenArray, setKebabOpenArray] = useState([]);
  const [modalTitle, setModalTitle] = useState<JSX.Element>(null);
  const setCancelModalTitle = (): JSX.Element => {
    return null;
  };
  const [modalContent, setModalContent] = useState<string>('');
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [isDetailsModalOpen, setIsDetailsModalOpen] = useState<boolean>(false);
  const [isRescheduleModalOpen, setIsRescheduleModalOpen] = useState<boolean>(
    false
  );
  const [isCancelModalOpen, setIsCancelModalOpen] = useState<boolean>(false);
  const [selectedJob, setSelectedJob] = useState<any>({});
  const ignoredNodeTypes = ['Join', 'Split', 'EndNode'];
  const editableJobStatus: string[] = ['SCHEDULED', 'ERROR'];
  const [rescheduleError, setRescheduleError] = useState<string>('');

  const onKebabToggle = (isOpen: boolean, id) => {
    if (isOpen) {
      setKebabOpenArray([...kebabOpenArray, id]);
    } else {
      onDropdownSelect(id);
    }
  };

  const onDropdownSelect = id => {
    const tempKebabArray = [...kebabOpenArray];
    const index = tempKebabArray.indexOf(id);
    tempKebabArray.splice(index, 1);
    setKebabOpenArray(tempKebabArray);
  };

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

  const handleJobDetailsModalToggle = () => {
    setIsDetailsModalOpen(!isDetailsModalOpen);
  };

  const onShowMessage = (
    title: string,
    content: string,
    type: TitleType
  ): void => {
    setModalTitle(setTitle(type, title));
    setModalContent(content);
    handleModalToggle();
  };
  const handleJobDetails = (job: Job): void => {
    setSelectedJob(job);
    handleDetailsToggle();
  };
  const handleDetailsToggle = (): void => {
    setIsDetailsModalOpen(!isDetailsModalOpen);
  };

  const handleJobRescheduleBySelected = (job: Job): void => {
    setIsRescheduleModalOpen(!isRescheduleModalOpen);
    setSelectedJob(job);
  };

  const handleJobReschedule = async (
    repeatInterval,
    repeatLimit,
    scheduleDate
  ): Promise<void> => {
    await handleJobRescheduleUtil(
      repeatInterval,
      repeatLimit,
      scheduleDate,
      selectedJob,
      handleRescheduleAction,
      driver,
      setRescheduleError
    );
  };

  const handleCancelAction = async (job: Job): Promise<void> => {
    await jobCancel(driver, job, setCancelModalTitle, setModalContent);
    handleCancelModalToggle();
  };

  const handleCancelModalToggle = (): void => {
    setIsCancelModalOpen(!isCancelModalOpen);
  };

  const renderJobActions = (id, options) => {
    if (jobs.length > 0) {
      return jobs.map(job => {
        if (
          id === job.nodeInstanceId &&
          editableJobStatus.includes(job.status)
        ) {
          return [
            ...options,
            <DropdownItem
              key="job-details"
              id="job-details"
              component="button"
              onClick={() => handleJobDetails(job)}
            >
              Job Details
            </DropdownItem>,
            <DropdownItem
              key="job-reschedule"
              id="job-reschedule"
              component="button"
              onClick={() => handleJobRescheduleBySelected(job)}
            >
              Job Reschedule
            </DropdownItem>,
            <DropdownItem
              key="job-cancel"
              id="job-cancel"
              component="button"
              onClick={() => handleCancelAction(job)}
            >
              Job Cancel
            </DropdownItem>
          ];
        } else if (
          id === job.nodeInstanceId &&
          !editableJobStatus.includes(job.status)
        ) {
          return [
            ...options,
            <DropdownItem
              key="job-details"
              id="job-details"
              component="button"
              onClick={() => handleJobDetails(job)}
            >
              Job Details
            </DropdownItem>
          ];
        } else {
          return [];
        }
      });
    } else {
      return options;
    }
  };

  const dropdownItems = (processInstanceData, node): JSX.Element[] => {
    if (
      processInstanceData.error &&
      node.definitionId === processInstanceData.error.nodeDefinitionId
    ) {
      const options = [
        <DropdownItem
          key="retry"
          component="button"
          onClick={() =>
            handleRetry(
              processInstanceData,
              driver,
              () =>
                onShowMessage(
                  'Retry operation',
                  `The node ${node.name} was successfully re-executed.`,
                  TitleType.SUCCESS
                ),
              (errorMessage: string) =>
                onShowMessage(
                  'Retry operation',
                  `The node ${node.name} failed to re-execute. Message: ${errorMessage}`,
                  TitleType.FAILURE
                )
            )
          }
        >
          Retry
        </DropdownItem>,
        <DropdownItem
          key="skip"
          component="button"
          onClick={() =>
            handleSkip(
              processInstanceData,
              driver,
              () =>
                onShowMessage(
                  'Skip operation',
                  `The node ${node.name} was successfully skipped.`,
                  TitleType.SUCCESS
                ),
              (errorMessage: string) =>
                onShowMessage(
                  'Skip operation',
                  `The node ${node.name} failed to skip. Message: ${errorMessage}`,
                  TitleType.FAILURE
                )
            )
          }
        >
          Skip
        </DropdownItem>
      ];
      const items = renderJobActions(node.id, options);
      return items.flat();
    } else if (node.exit === null && !ignoredNodeTypes.includes(node.type)) {
      const options = [
        <DropdownItem
          key="retrigger"
          component="button"
          onClick={() =>
            handleNodeInstanceRetrigger(
              processInstanceData,
              driver,
              node,
              () =>
                onShowMessage(
                  'Node retrigger operation',
                  `The node ${node.name} was successfully retriggered.`,
                  TitleType.SUCCESS
                ),
              (errorMessage: string) =>
                onShowMessage(
                  'Node retrigger operation',
                  `The node ${node.name} failed to retrigger. Message: ${errorMessage}`,
                  TitleType.FAILURE
                )
            )
          }
        >
          Retrigger node
        </DropdownItem>,
        <DropdownItem
          key="cancel"
          component="button"
          onClick={() =>
            handleNodeInstanceCancel(
              processInstanceData,
              driver,
              node,
              () =>
                onShowMessage(
                  'Node cancel operation',
                  `The node ${node.name} was successfully canceled.`,
                  TitleType.SUCCESS
                ),
              (errorMessage: string) =>
                onShowMessage(
                  'Node cancel operation',
                  `The node ${node.name} failed to cancel. Message: ${errorMessage}`,
                  TitleType.FAILURE
                )
            )
          }
        >
          Cancel node
        </DropdownItem>
      ];
      const items = renderJobActions(node.id, options);
      return items.flat();
    } else {
      const items = renderJobActions(node.id, []);
      return items.flat();
    }
  };
  const processManagementKebabButtons = (node, index): JSX.Element => {
    const dropdownItemsValue: JSX.Element[] = dropdownItems(data, node);
    if (
      data.addons.includes('process-management') &&
      data.serviceUrl !== null &&
      dropdownItemsValue &&
      dropdownItemsValue.length !== 0
    ) {
      return (
        <Dropdown
          onSelect={() => onDropdownSelect('timeline-kebab-toggle-' + index)}
          toggle={
            <KebabToggle
              onToggle={isOpen =>
                onKebabToggle(isOpen, 'timeline-kebab-toggle-' + index)
              }
              id={'timeline-kebab-toggle-' + index}
            />
          }
          position="right"
          isOpen={kebabOpenArray.includes('timeline-kebab-toggle-' + index)}
          isPlain
          dropdownItems={dropdownItemsValue}
        />
      );
    }
  };

  const renderTimerIcon = (id: string): JSX.Element => {
    return jobs.map((job, idx) => {
      if (id === job.nodeInstanceId) {
        return (
          <Tooltip content={'Node has job'} key={idx}>
            <OutlinedClockIcon
              className="pf-u-ml-sm"
              color="var(--pf-global--icon--Color--dark)"
              onClick={() => handleJobDetails(job)}
            />
          </Tooltip>
        );
      }
    })[0];
  };

  const detailsAction: JSX.Element[] = [
    <Button
      key="confirm-selection"
      variant="primary"
      onClick={handleModalToggle}
    >
      OK
    </Button>
  ];

  const handleRescheduleAction = (): void => {
    setIsRescheduleModalOpen(!isRescheduleModalOpen);
  };

  const rescheduleActions: JSX.Element[] = [
    <Button
      key="cancel-reschedule"
      variant="secondary"
      onClick={handleRescheduleAction}
    >
      Cancel
    </Button>
  ];

  const compareNodes = useCallback((nodeA, nodeB) => {
    if (nodeA?.enter < nodeB?.enter) {
      return -1;
    } else if (nodeA?.enter > nodeB?.enter) {
      return 1;
    } else if (nodeA?.exit < nodeB?.exit) {
      return -1;
    } else if (nodeA?.exit > nodeB?.exit) {
      return 1;
    } else if (nodeA?.id < nodeB?.id) {
      return -1;
    } else if (nodeA?.id > nodeB?.id) {
      return 1;
    }

    return 0;
  }, []);

  return (
    <Card
      {...componentOuiaProps(ouiaId ? ouiaId : data.id, 'timeline', ouiaSafe)}
    >
      <ProcessInfoModal
        isModalOpen={isModalOpen}
        handleModalToggle={handleModalToggle}
        modalTitle={modalTitle}
        modalContent={modalContent}
      />
      <CardHeader>
        <Title headingLevel="h3" size="xl">
          Timeline
        </Title>
      </CardHeader>
      <CardBody>
        <Stack hasGutter className="kogito-process-details--timeline">
          {data.nodes &&
            data.nodes
              .filter(
                content => !omittedProcessTimelineEvents?.includes(content.name)
              )
              .sort(compareNodes)
              .map((content, idx) => {
                return (
                  <Split
                    hasGutter
                    className={'kogito-process-details--timeline-item'}
                    key={content.id}
                  >
                    <SplitItem>
                      {
                        <>
                          {data.error &&
                          content.definitionId ===
                            data.error.nodeDefinitionId ? (
                            <Tooltip content={data.error.message}>
                              <ErrorCircleOIcon
                                color="var(--pf-global--danger-color--100)"
                                className="kogito-process-details--timeline-status"
                              />
                            </Tooltip>
                          ) : content.exit === null ? (
                            <Tooltip content={'Active'}>
                              <OnRunningIcon className="kogito-process-details--timeline-status" />
                            </Tooltip>
                          ) : (
                            <Tooltip content={'Completed'}>
                              <CheckCircleIcon
                                color="var(--pf-global--success-color--100)"
                                className="kogito-process-details--timeline-status"
                              />
                            </Tooltip>
                          )}
                        </>
                      }
                    </SplitItem>
                    <SplitItem isFilled>
                      <TextContent>
                        <Text component={TextVariants.p}>
                          {content.name}
                          <span>
                            {content.type === 'HumanTaskNode' && (
                              <Tooltip content={'Human task'}>
                                <UserIcon
                                  className="pf-u-ml-sm"
                                  color="var(--pf-global--icon--Color--light)"
                                />
                              </Tooltip>
                            )}
                            {renderTimerIcon(content.id)}
                          </span>

                          <Text component={TextVariants.small}>
                            {content.exit === null ? (
                              'Active'
                            ) : (
                              <Moment fromNow>
                                {new Date(`${content.exit}`)}
                              </Moment>
                            )}
                          </Text>
                        </Text>
                      </TextContent>
                    </SplitItem>
                    <SplitItem>
                      {processManagementKebabButtons(content, idx)}
                    </SplitItem>
                  </Split>
                );
              })}
        </Stack>
      </CardBody>
      <JobsDetailsModal
        actionType="Job Details"
        modalTitle={setTitle('success', 'Job Details')}
        isModalOpen={isDetailsModalOpen}
        handleModalToggle={handleJobDetailsModalToggle}
        modalAction={detailsAction}
        job={selectedJob}
      />
      {Object.keys(selectedJob).length > 0 && (
        <JobsRescheduleModal
          actionType="Job Reschedule"
          isModalOpen={isRescheduleModalOpen}
          handleModalToggle={handleRescheduleAction}
          modalAction={rescheduleActions}
          job={selectedJob}
          rescheduleError={rescheduleError}
          setRescheduleError={setRescheduleError}
          handleJobReschedule={handleJobReschedule}
        />
      )}
      <JobsCancelModal
        actionType="Job Cancel"
        isModalOpen={isCancelModalOpen}
        handleModalToggle={handleCancelModalToggle}
        modalTitle={modalTitle}
        modalContent={modalContent}
      />
    </Card>
  );
};

export default ProcessDetailsTimelinePanel;
