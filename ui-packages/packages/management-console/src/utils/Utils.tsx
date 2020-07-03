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
  processInstance: Pick<ProcessInstance, 'id' | 'processId' | 'serviceUrl'>,
  onSkipSuccess: () => void,
  onSkipFailure: (errorMessage: string) => void
): void => {
  axios
    .post(
      `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/skip`
    )
    .then(() => {
      onSkipSuccess();
    })
    .catch(error => {
      onSkipFailure(JSON.stringify(error.message));
    });
};

export const handleRetry = (
  processInstance: Pick<ProcessInstance, 'id' | 'processId' | 'serviceUrl'>,
  onRetrySuccess: () => void,
  onRetryFailure: (errorMessage: string) => void
): void => {
  axios
    .post(
      `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/retrigger`
    )
    .then(() => {
      onRetrySuccess();
    })
    .catch(error => {
      onRetryFailure(JSON.stringify(error.message));
    });
};

export const handleAbort = (
  processInstanceData: Pick<
    ProcessInstance,
    'id' | 'processId' | 'serviceUrl' | 'state'
  >,
  setModalTitle: (modalTitle: string) => void,
  setTitleType: (titleType: string) => void,
  handleAbortModalToggle: () => void
) => {
  setModalTitle('Abort operation');
  axios
    .delete(
      `${processInstanceData.serviceUrl}/management/processes/${processInstanceData.processId}/instances/${processInstanceData.id}`
    )
    .then(() => {
      setTitleType('success');
      processInstanceData.state = ProcessInstanceState.Aborted;
      handleAbortModalToggle();
    })
    .catch(() => {
      setTitleType('failure');
      handleAbortModalToggle();
    });
};

export const handleNodeInstanceRetrigger = (
  processInstance: Pick<ProcessInstance, 'id' | 'serviceUrl' | 'processId'>,
  node: Pick<GraphQL.NodeInstance, 'id'>,
  onRetriggerSuccess: () => void,
  onRetriggerFailure: (errorMessage: string) => void
) => {
  axios
    .post(
      `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/nodeInstances/${node.id}`
    )
    .then(() => {
      onRetriggerSuccess();
    })
    .catch(error => {
      onRetriggerFailure(JSON.stringify(error.message));
    });
};
