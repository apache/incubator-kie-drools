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
import React, { useState } from 'react';
import './ProcessDetailsTimeline.css';
import { GraphQL } from '@kogito-apps/common';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import {
  handleRetry,
  handleSkip,
  handleNodeInstanceRetrigger,
  setTitle,
  handleNodeInstanceCancel,
  jobCancel
} from '../../../utils/Utils';
import ProcessInstance = GraphQL.ProcessInstance;
import ProcessListModal from '../../Atoms/ProcessListModal/ProcessListModal';
import JobsPanelDetailsModal from '../../Atoms/JobsPanelDetailsModal/JobsPanelDetailsModal';
import JobsRescheduleModal from '../../Atoms/JobsRescheduleModal/JobsRescheduleModal';
import { refetchContext } from '../../contexts';
import JobsCancelModal from '../../Atoms/JobsCancelModal/JobsCancelModal';

interface JobResponseMeta {
  data: any;
  loading: boolean;
  refetch: () => void;
}
export interface IOwnProps {
  data: Pick<
    ProcessInstance,
    'id' | 'nodes' | 'addons' | 'error' | 'serviceUrl' | 'processId' | 'state'
  >;
  jobsResponse: JobResponseMeta;
}
enum TitleType {
  SUCCESS = 'success',
  FAILURE = 'failure'
}
const ProcessDetailsTimeline: React.FC<IOwnProps & OUIAProps> = ({
  data,
  jobsResponse,
  ouiaId,
  ouiaSafe
}) => {
  const [kebabOpenArray, setKebabOpenArray] = useState([]);
  const [modalTitle, setModalTitle] = useState<string>('');
  const [cancelModalTitle, setCancelModalTitle] = useState<JSX.Element>(null);
  const [titleType, setTitleType] = useState<string>('');
  const [modalContent, setModalContent] = useState<string>('');
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [isDetailsModalOpen, setIsDetailsModalOpen] = useState<boolean>(false);
  const [isRescheduleModalOpen, setIsRescheduleModalOpen] =
    useState<boolean>(false);
  const [isCancelModalOpen, setIsCancelModalOpen] = useState<boolean>(false);
  const [selectedJob, setSelectedJob] = useState<any>({});
  const ignoredNodeTypes = ['Join', 'Split', 'EndNode'];
  const editableJobStatus: string[] = ['SCHEDULED', 'ERROR'];

  const onKebabToggle = (isOpen: boolean, id) => {
    if (isOpen) {
      setKebabOpenArray([...kebabOpenArray, id]);
    } else {
      onDropdownSelect(id);
    }
  };

  const onDropdownSelect = (id) => {
    const tempKebabArray = [...kebabOpenArray];
    const index = tempKebabArray.indexOf(id);
    tempKebabArray.splice(index, 1);
    setKebabOpenArray(tempKebabArray);
  };

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

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
  const handleJobDetails = (job: GraphQL.Job): void => {
    setSelectedJob(job);
    handleDetailsToggle();
  };
  const handleDetailsToggle = (): void => {
    setIsDetailsModalOpen(!isDetailsModalOpen);
  };

  const handleJobReschedule = (job: GraphQL.Job): void => {
    setSelectedJob(job);
    handleRescheduleToggle();
  };

  const handleCancelAction = async (job: GraphQL.Job): Promise<void> => {
    await jobCancel(
      job,
      setCancelModalTitle,
      setModalContent,
      jobsResponse.refetch
    );
    handleCancelModalToggle();
  };

  const handleRescheduleToggle = (): void => {
    setIsRescheduleModalOpen(!isRescheduleModalOpen);
  };

  const handleCancelModalToggle = (): void => {
    setIsCancelModalOpen(!isCancelModalOpen);
  };

  const detailsAction: JSX.Element[] = [
    <Button
      key="confirm-selection"
      variant="primary"
      onClick={handleDetailsToggle}
    >
      OK
    </Button>
  ];

  const rescheduleActions: JSX.Element[] = [
    <Button
      key="cancel-reschedule"
      variant="secondary"
      onClick={handleRescheduleToggle}
    >
      Cancel
    </Button>
  ];

  const renderJobActions = (id, options) => {
    if (jobsResponse.data.Jobs.length > 0) {
      return jobsResponse.data.Jobs.map((job) => {
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
              onClick={() => handleJobReschedule(job)}
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
              onToggle={(isOpen) =>
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
    return jobsResponse.data.Jobs.map((job, idx) => {
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
    });
  };

  return (
    <Card
      {...componentOuiaProps(ouiaId ? ouiaId : data.id, 'timeline', ouiaSafe)}
    >
      <ProcessListModal
        isModalOpen={isModalOpen}
        handleModalToggle={handleModalToggle}
        modalTitle={setTitle(titleType, modalTitle)}
        modalContent={modalContent}
      />
      <CardHeader>
        <Title headingLevel="h3" size="xl">
          Timeline
        </Title>
      </CardHeader>
      <CardBody>
        <Stack hasGutter className="kogito-management-console--timeline">
          {data.nodes &&
            data.nodes.map((content, idx) => {
              return (
                <Split
                  hasGutter
                  className={'kogito-management-console--timeline-item'}
                  key={content.id}
                >
                  <SplitItem>
                    {
                      <>
                        {data.error &&
                        content.definitionId === data.error.nodeDefinitionId ? (
                          <Tooltip content={data.error.message}>
                            <ErrorCircleOIcon
                              color="var(--pf-global--danger-color--100)"
                              className="kogito-management-console--timeline-status"
                            />
                          </Tooltip>
                        ) : content.exit === null ? (
                          <Tooltip content={'Active'}>
                            <OnRunningIcon className="kogito-management-console--timeline-status" />
                          </Tooltip>
                        ) : (
                          <Tooltip content={'Completed'}>
                            <CheckCircleIcon
                              color="var(--pf-global--success-color--100)"
                              className="kogito-management-console--timeline-status"
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
      <JobsPanelDetailsModal
        actionType="Job Details"
        modalTitle={setTitle('success', 'Job Details')}
        isModalOpen={isDetailsModalOpen}
        handleModalToggle={handleDetailsToggle}
        modalAction={detailsAction}
        job={selectedJob}
      />
      {Object.keys(selectedJob).length > 0 && (
        <refetchContext.Provider value={jobsResponse.refetch}>
          <JobsRescheduleModal
            actionType="Job Reschedule"
            modalTitle={setTitle('success', 'Job Reschedule')}
            isModalOpen={isRescheduleModalOpen}
            handleModalToggle={handleRescheduleToggle}
            modalAction={rescheduleActions}
            job={selectedJob}
            setRescheduleClicked={setIsRescheduleModalOpen}
            rescheduleClicked={isRescheduleModalOpen}
          />
        </refetchContext.Provider>
      )}
      <JobsCancelModal
        actionType="Job Cancel"
        isModalOpen={isCancelModalOpen}
        handleModalToggle={handleCancelModalToggle}
        modalTitle={cancelModalTitle}
        modalContent={modalContent}
      />
    </Card>
  );
};

export default ProcessDetailsTimeline;
