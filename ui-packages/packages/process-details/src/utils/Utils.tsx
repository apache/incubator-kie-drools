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
import {
  JobStatus,
  Job,
  ProcessInstance,
  setTitle,
  NodeInstance
} from '@kogito-apps/management-console-shared';
import {
  BanIcon,
  CheckCircleIcon,
  ClockIcon,
  ErrorCircleOIcon,
  UndoIcon
} from '@patternfly/react-icons';
import { ProcessDetailsDriver } from '../api';

export const JobsIconCreator = (state: JobStatus): JSX.Element => {
  switch (state) {
    case JobStatus.Error:
      return (
        <>
          <ErrorCircleOIcon
            className="pf-u-mr-sm"
            color="var(--pf-global--danger-color--100)"
          />
          Error
        </>
      );
    case JobStatus.Canceled:
      return (
        <>
          <BanIcon className="pf-u-mr-sm" />
          Canceled
        </>
      );
    case JobStatus.Executed:
      return (
        <>
          <CheckCircleIcon
            className="pf-u-mr-sm"
            color="var(--pf-global--success-color--100)"
          />
          Executed
        </>
      );
    case JobStatus.Retry:
      return (
        <>
          <UndoIcon className="pf-u-mr-sm" />
          Retry
        </>
      );
    case JobStatus.Scheduled:
      return (
        <>
          <ClockIcon className="pf-u-mr-sm" />
          Scheduled
        </>
      );
  }
};

export const handleRetry = async (
  processInstance: ProcessInstance,
  drive: ProcessDetailsDriver,
  onRetrySuccess: () => void,
  onRetryFailure: (errorMessage: string) => void
) => {
  try {
    await drive.handleProcessRetry(processInstance);
    onRetrySuccess();
  } catch (error) {
    onRetryFailure(JSON.stringify(error.message));
  }
};

export const handleSkip = async (
  processInstance: ProcessInstance,
  drive: ProcessDetailsDriver,
  onSkipSuccess: () => void,
  onSkipFailure: (errorMessage: string) => void
) => {
  try {
    await drive.handleProcessSkip(processInstance);
    onSkipSuccess();
  } catch (error) {
    onSkipFailure(JSON.stringify(error.message));
  }
};

export const handleNodeInstanceRetrigger = (
  processInstance: ProcessInstance,
  driver: ProcessDetailsDriver,
  node: NodeInstance,
  onRetriggerSuccess: () => void,
  onRetriggerFailure: (errorMessage: string) => void
) => {
  driver
    .handleNodeInstanceRetrigger(processInstance, node)
    .then(() => {
      onRetriggerSuccess();
    })
    .catch((error) => {
      onRetriggerFailure(JSON.stringify(error.message));
    });
};

export const handleNodeInstanceCancel = (
  processInstance: ProcessInstance,
  drive: ProcessDetailsDriver,
  node: NodeInstance,
  onCancelSuccess: () => void,
  onCancelFailure: (errorMessage: string) => void
) => {
  drive
    .handleNodeInstanceCancel(processInstance, node)
    .then(() => {
      onCancelSuccess();
    })
    .catch((error) => {
      onCancelFailure(JSON.stringify(error.message));
    });
};

export const jobCancel = async (
  drive: ProcessDetailsDriver,
  job: Pick<Job, 'id' | 'endpoint'>,
  setModalTitle: (title: JSX.Element) => void,
  setModalContent: (content: string) => void
) => {
  const response = await drive.cancelJob(job);
  setModalTitle(setTitle(response.modalTitle, 'Job cancel'));
  setModalContent(response.modalContent);
};

export const handleJobRescheduleUtil = async (
  repeatInterval,
  repeatLimit,
  scheduleDate,
  selectedJob: Job,
  handleRescheduleAction: () => void,
  driver: ProcessDetailsDriver,
  setRescheduleError: (modalContent: string) => void
): Promise<void> => {
  const response = await driver.rescheduleJob(
    selectedJob,
    repeatInterval,
    repeatLimit,
    scheduleDate
  );
  if (response && response.modalTitle === 'success') {
    handleRescheduleAction();
  } else if (response && response.modalTitle === 'failure') {
    handleRescheduleAction();
    setRescheduleError(response.modalContent);
  }
};
