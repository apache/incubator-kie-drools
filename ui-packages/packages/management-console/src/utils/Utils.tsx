import React from 'react';
import axios from 'axios';
import {
  OnRunningIcon,
  CheckCircleIcon,
  BanIcon,
  PausedIcon,
  ErrorCircleOIcon,
  InfoCircleIcon
} from '@patternfly/react-icons';
import { GraphQL } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;
import ProcessInstance = GraphQL.ProcessInstance;

export const stateIconCreator = (state: ProcessInstanceState): JSX.Element => {
  switch (state) {
    case ProcessInstanceState.Active:
      return (
        <>
          <OnRunningIcon className="pf-u-mr-sm" />
          Active
        </>
      );
    case ProcessInstanceState.Completed:
      return (
        <>
          <CheckCircleIcon
            className="pf-u-mr-sm"
            color="var(--pf-global--success-color--100)"
          />
          Completed
        </>
      );
    case ProcessInstanceState.Aborted:
      return (
        <>
          <BanIcon className="pf-u-mr-sm" />
          Aborted
        </>
      );
    case ProcessInstanceState.Suspended:
      return (
        <>
          <PausedIcon className="pf-u-mr-sm" />
          Suspended
        </>
      );
    case ProcessInstanceState.Error:
      return (
        <>
          <ErrorCircleOIcon
            className="pf-u-mr-sm"
            color="var(--pf-global--danger-color--100)"
          />
          Error
        </>
      );
  }
};

export const setTitle = (
  titleStatus: string,
  titleText: string
): JSX.Element => {
  switch (titleStatus) {
    case 'success':
      return (
        <>
          <InfoCircleIcon
            className="pf-u-mr-sm"
            color="var(--pf-global--info-color--100)"
          />{' '}
          {titleText}{' '}
        </>
      );
    case 'failure':
      return (
        <>
          <InfoCircleIcon
            className="pf-u-mr-sm"
            color="var(--pf-global--danger-color--100)"
          />{' '}
          {titleText}{' '}
        </>
      );
  }
};

export const handleSkip = (
  processInstanceData: Pick<
    ProcessInstance,
    'id' | 'processId' | 'serviceUrl' | 'state'
  >,
  setModalTitle: (modalTitle: string) => void,
  setTitleType: (titleType: string) => void,
  setModalContent: (modalContent: string) => void,
  handleSkipModalToggle: () => void
): void => {
  setModalTitle('Skip operation');
  axios
    .post(
      `${processInstanceData.serviceUrl}/management/processes/${processInstanceData.processId}/instances/${processInstanceData.id}/skip`
    )
    .then(() => {
      setTitleType('success');
      setModalContent(
        'Process execution has successfully skipped node which was in error state.'
      );
      handleSkipModalToggle();
    })
    .catch(error => {
      setTitleType('failure');
      setModalContent(
        `Process execution failed to skip node which is in error state. Message: ${JSON.stringify(
          error.message
        )}`
      );
      handleSkipModalToggle();
    });
};

export const handleRetry = (
  processInstanceData: Pick<
    ProcessInstance,
    'id' | 'processId' | 'serviceUrl' | 'state'
  >,
  setModalTitle: (modalTitle: string) => void,
  setTitleType: (titleType: string) => void,
  setModalContent: (modalContent: string) => void,
  handleRetryModalToggle: () => void
): void => {
  setModalTitle('Retry operation');
  axios
    .post(
      `${processInstanceData.serviceUrl}/management/processes/${processInstanceData.processId}/instances/${processInstanceData.id}/retrigger`
    )
    .then(() => {
      setTitleType('success');
      setModalContent(
        'Process execution has successfully re-executed node which was in error state.'
      );
      handleRetryModalToggle();
    })
    .catch(error => {
      setTitleType('failure');
      setModalContent(
        `Process execution failed to re-execute node which is in error state. Message: ${JSON.stringify(
          error.message
        )}`
      );
      handleRetryModalToggle();
    });
};

export const handleAbort = (
  processInstanceData: Pick<
    ProcessInstance,
    'id' | 'processId' | 'serviceUrl' | 'state'
  >,
  setModalTitle: (modalTitle: string) => void,
  setTitleType: (titleType: string) => void,
  setModalContent: (modalContent: string) => void,
  handleAbortModalToggle: () => void
) => {
  setModalTitle('Abort operation');
  axios
    .delete(
      `${processInstanceData.serviceUrl}/management/processes/${processInstanceData.processId}/instances/${processInstanceData.id}`
    )
    .then(() => {
      setModalTitle('Process aborted');
      setModalContent(
        `${processInstanceData.processId} - process execution has been aborted.`
      );
      setTitleType('success');
      processInstanceData.state = ProcessInstanceState.Aborted;
      handleAbortModalToggle();
    })
    .catch(() => {
      setTitleType('failure');
      handleAbortModalToggle();
    });
};

export const isModalOpen = (
  modalTitle,
  isSkipModalOpen,
  isRetryModalOpen
): boolean => {
  if (modalTitle === 'Skip operation') {
    return isSkipModalOpen;
  } else if (modalTitle === 'Retry operation') {
    return isRetryModalOpen;
  }
};
export const modalToggle = (
  modalTitle,
  handleSkipModalToggle,
  handleRetryModalToggle
): (() => void) => {
  if (modalTitle === 'Skip operation') {
    return handleSkipModalToggle;
  } else if (modalTitle === 'Retry operation') {
    return handleRetryModalToggle;
  }
};
