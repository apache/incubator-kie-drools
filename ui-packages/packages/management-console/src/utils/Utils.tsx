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
  processInstance: Pick<
    ProcessInstance,
    'id' | 'processId' | 'serviceUrl' | 'state'
  >,
  onRetrySuccess: () => void,
  onRetryFailure: (errorMessage: string) => void
) => {
  axios
    .delete(
      `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}`
    )
    .then(() => {
      processInstance.state = ProcessInstanceState.Aborted;
      onRetrySuccess();
    })
    .catch(error => {
      onRetryFailure(JSON.stringify(error.message));
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

export const handleNodeInstanceCancel = (
  processInstance: Pick<ProcessInstance, 'id' | 'serviceUrl' | 'processId'>,
  node: Pick<GraphQL.NodeInstance, 'id'>,
  onCancelSuccess: () => void,
  onCancelFailure: (errorMessage: string) => void
) => {
  axios
    .delete(
      `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/nodeInstances/${node.id}`
    )
    .then(() => {
      onCancelSuccess();
    })
    .catch(error => {
      onCancelFailure(JSON.stringify(error.message));
    });
};

export const handleAbortAll = (
  abortedObj,
  initData,
  setModalTitle,
  setTitleType,
  setAbortedMessageObj,
  setCompletedMessageObj,
  handleAbortModalToggle
) => {
  const tempAbortedObj = { ...abortedObj };
  const completedAndAborted = {};
  for (const [id, processInstance] of Object.entries(tempAbortedObj)) {
    initData.ProcessInstances.map(instance => {
      if (instance.id === id) {
        /* istanbul ignore else */
        if (
          instance.addons.includes('process-management') &&
          instance.serviceUrl !== null
        ) {
          if (
            instance.state === ProcessInstanceState.Completed ||
            instance.state === ProcessInstanceState.Aborted
          ) {
            completedAndAborted[id] = processInstance;
            delete tempAbortedObj[id];
          } else {
            instance.state = ProcessInstanceState.Aborted;
          }
        }
      }
      if (instance.childDataList !== undefined) {
        instance.childDataList.map(child => {
          if (child.id === id) {
            /* istanbul ignore else */
            if (
              instance.addons.includes('process-management') &&
              instance.serviceUrl !== null
            ) {
              if (
                child.state === ProcessInstanceState.Completed ||
                child.state === ProcessInstanceState.Aborted
              ) {
                completedAndAborted[id] = processInstance;
                delete tempAbortedObj[id];
              } else {
                child.state = ProcessInstanceState.Aborted;
              }
            }
          }
        });
      }
    });
  }
  const promiseArray = [];
  Object.keys(tempAbortedObj).forEach((id: string) => {
    promiseArray.push(
      axios.delete(
        `${tempAbortedObj[id].serviceUrl}/management/processes/${tempAbortedObj[id].processId}/instances/${tempAbortedObj[id].id}`
      )
    );
  });
  setModalTitle('Abort operation');
  Promise.all(promiseArray)
    .then(() => {
      setTitleType('success');
      setAbortedMessageObj(tempAbortedObj);
      setCompletedMessageObj(completedAndAborted);
      handleAbortModalToggle();
    })
    .catch(() => {
      setTitleType('failure');
      setAbortedMessageObj(tempAbortedObj);
      setCompletedMessageObj(completedAndAborted);
      handleAbortModalToggle();
    });
};
