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

import React from 'react';
import { Job } from '../../types';
import {
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import {
  TextContent,
  Text,
  TextVariants
} from '@patternfly/react-core/dist/js/components/Text';
import { Modal } from '@patternfly/react-core/dist/js/components/Modal';
import { Flex, FlexItem } from '@patternfly/react-core/dist/js/layouts/Flex';
import { Split, SplitItem } from '@patternfly/react-core/dist/js/layouts/Split';
import Moment from 'react-moment';
import '../styles.css';

interface IOwnProps {
  actionType: string;
  modalTitle: JSX.Element;
  isModalOpen: boolean;
  handleModalToggle: () => void;
  modalAction: JSX.Element[];
  job: Job;
}
export const JobsDetailsModal: React.FC<IOwnProps & OUIAProps> = ({
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
      <div className="kogito-management-console-shared--jobsModal__detailsModal">
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
                    className="kogito-management-console-shared--jobsModal__text"
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
