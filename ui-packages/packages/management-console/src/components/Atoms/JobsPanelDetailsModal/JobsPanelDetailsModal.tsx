import React from 'react';
import { GraphQL } from '@kogito-apps/common';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import {
  Modal,
  TextContent,
  Flex,
  FlexItem,
  Split,
  SplitItem,
  Text,
  TextVariants
} from '@patternfly/react-core';
import Moment from 'react-moment';
import './JobsPanelDetailsModal.css';

interface IOwnProps {
  actionType: string;
  modalTitle: JSX.Element;
  isModalOpen: boolean;
  handleModalToggle: () => void;
  modalAction: JSX.Element[];
  job: GraphQL.Job;
}
const JobsPanelDetailsModal: React.FC<IOwnProps & OUIAProps> = ({
  actionType,
  modalTitle,
  isModalOpen,
  modalAction,
  handleModalToggle,
  job,
  ouiaId,
  ouiaSafe
}) => {
  const modalContent = () => {
    return (
      <div className="kogito-management-console--jobsModal__detailsModal">
        <TextContent>
          <Flex direction={{ default: 'column' }}>
            <FlexItem>
              <Split hasGutter>
                <SplitItem>
                  <Text component={TextVariants.h6}>Process Id: </Text>{' '}
                </SplitItem>
                <SplitItem>{job.processId}</SplitItem>
              </Split>
            </FlexItem>
            <FlexItem>
              <Split hasGutter>
                <SplitItem>
                  {' '}
                  <Text component={TextVariants.h6}>
                    Process Instance Id:{' '}
                  </Text>{' '}
                </SplitItem>
                <SplitItem>{job.processInstanceId}</SplitItem>
              </Split>
            </FlexItem>
            <FlexItem>
              <Split hasGutter>
                <SplitItem>
                  <Text component={TextVariants.h6}>Status: </Text>{' '}
                </SplitItem>
                <SplitItem>{job.status}</SplitItem>
              </Split>
            </FlexItem>
            <FlexItem>
              <Split hasGutter>
                <SplitItem>
                  <Text component={TextVariants.h6}>Priority: </Text>{' '}
                </SplitItem>
                <SplitItem>{job.priority}</SplitItem>
              </Split>
            </FlexItem>
            {job.repeatInterval && (
              <FlexItem>
                <Split hasGutter>
                  <SplitItem>
                    <Text component={TextVariants.h6}>RepeatInterval: </Text>
                  </SplitItem>
                  <SplitItem>{job.repeatInterval}</SplitItem>
                </Split>
              </FlexItem>
            )}
            {job.repeatLimit && (
              <FlexItem>
                <Split hasGutter>
                  <SplitItem>
                    <Text component={TextVariants.h6}>RepeatLimit: </Text>
                  </SplitItem>
                  <SplitItem>{job.repeatLimit}</SplitItem>
                </Split>
              </FlexItem>
            )}
            <FlexItem>
              <Split hasGutter>
                <SplitItem>
                  <Text component={TextVariants.h6}>ScheduledId: </Text>
                </SplitItem>
                <SplitItem>{job.scheduledId}</SplitItem>
              </Split>
            </FlexItem>
            <FlexItem>
              <Split hasGutter>
                <SplitItem>
                  <Text component={TextVariants.h6}>Retries: </Text>
                </SplitItem>
                <SplitItem>{job.retries}</SplitItem>
              </Split>
            </FlexItem>
            <FlexItem>
              <Split hasGutter>
                <SplitItem>
                  <Text component={TextVariants.h6}>Execution counter: </Text>
                </SplitItem>
                <SplitItem>{job.executionCounter}</SplitItem>
              </Split>
            </FlexItem>
            <FlexItem>
              <Split hasGutter>
                <SplitItem>
                  <Text component={TextVariants.h6}>Last Updated: </Text>
                </SplitItem>
                <SplitItem>
                  <Moment fromNow>{new Date(`${job.lastUpdate}`)}</Moment>
                </SplitItem>
              </Split>
            </FlexItem>
            <FlexItem>
              <Split hasGutter>
                <SplitItem>
                  <Text
                    component={TextVariants.h6}
                    className="kogito-management-console--jobsModal__text"
                  >
                    Callback Endpoint:{' '}
                  </Text>
                </SplitItem>
                <SplitItem>{job.callbackEndpoint}</SplitItem>
              </Split>
            </FlexItem>
          </Flex>
        </TextContent>
      </div>
    );
  };
  return (
    <Modal
      variant={'large'}
      aria-labelledby={actionType + 'modal'}
      aria-label={actionType + 'modal'}
      title=""
      header={modalTitle}
      isOpen={isModalOpen}
      onClose={handleModalToggle}
      actions={modalAction}
      {...componentOuiaProps(ouiaId, 'job-details-modal', ouiaSafe)}
    >
      {modalContent()}
    </Modal>
  );
};

export default JobsPanelDetailsModal;
