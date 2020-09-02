import React from 'react';
import { OUIAProps, componentOuiaProps, GraphQL } from '@kogito-apps/common';
import {
  Modal,
  Title,
  TitleSizes,
  Button,
  TextContent,
  Flex,
  FlexItem,
  Split,
  SplitItem,
  Text,
  TextVariants
} from '@patternfly/react-core';
import Moment from 'react-moment';
interface JobsPanelDetailsModalProps {
  modalTitle: JSX.Element;
  isModalOpen: boolean;
  handleModalToggle: () => void;
  job: GraphQL.Job;
}
const JobsPanelDetailsModal: React.FC<JobsPanelDetailsModalProps &
  OUIAProps> = ({
  modalTitle,
  isModalOpen,
  handleModalToggle,
  job,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <Modal
      variant="large"
      aria-labelledby="Job details modal"
      aria-label="Job details modal"
      title=""
      header={
        <Title headingLevel="h1" size={TitleSizes['2xl']}>
          {modalTitle}
        </Title>
      }
      isOpen={isModalOpen}
      onClose={handleModalToggle}
      actions={[
        <Button
          key="confirm-selection"
          variant="primary"
          onClick={handleModalToggle}
        >
          OK
        </Button>
      ]}
      {...componentOuiaProps(ouiaId, 'job-details-modal', ouiaSafe)}
    >
      <div style={{ padding: '30px' }}>
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
                    style={{ whiteSpace: 'nowrap' }}
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
    </Modal>
  );
};

export default JobsPanelDetailsModal;
