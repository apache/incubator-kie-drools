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
import {
  ProcessInstanceBulkList,
  OperationType
} from '../components/Molecules/ProcessListToolbar/ProcessListToolbar';

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

export const handleSkip = async (
  processInstance: Pick<ProcessInstance, 'id' | 'processId' | 'serviceUrl'>,
  onSkipSuccess: () => void,
  onSkipFailure: (errorMessage: string) => void
) => {
  try {
    await axios.post(
      `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/skip`
    );
    onSkipSuccess();
  } catch (error) {
    onSkipFailure(JSON.stringify(error.message));
  }
};

export const handleRetry = async (
  processInstance: Pick<ProcessInstance, 'id' | 'processId' | 'serviceUrl'>,
  onRetrySuccess: () => void,
  onRetryFailure: (errorMessage: string) => void
) => {
  try {
    await axios.post(
      `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/retrigger`
    );
    onRetrySuccess();
  } catch (error) {
    onRetryFailure(JSON.stringify(error.message));
  }
};

export const handleAbort = async (
  processInstance: Pick<
    ProcessInstance,
    'id' | 'processId' | 'serviceUrl' | 'state'
  >,
  onRetrySuccess: () => void,
  onRetryFailure: (errorMessage: string) => void
) => {
  try {
    await axios.delete(
      `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}`
    );
    processInstance.state = ProcessInstanceState.Aborted;
    onRetrySuccess();
  } catch (error) {
    onRetryFailure(JSON.stringify(error.message));
  }
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

export const performMultipleAction = async (
  instanceToBeActioned: ProcessInstanceBulkList,
  multiActionResult: (
    successInstances: ProcessInstanceBulkList,
    failedInstances: ProcessInstanceBulkList
  ) => void,
  processType: OperationType
) => {
  const successInstances = {};
  const failedInstances = {};
  for (const id of Object.keys(instanceToBeActioned)) {
    if (processType === OperationType.ABORT) {
      await handleAbort(
        instanceToBeActioned[id],
        () => {
          successInstances[id] = instanceToBeActioned[id];
        },
        errorMessage => {
          failedInstances[id] = instanceToBeActioned[id];
          failedInstances[id].errorMessage = errorMessage;
        }
      );
    } else if (processType === OperationType.SKIP) {
      await handleSkip(
        instanceToBeActioned[id],
        () => {
          successInstances[id] = instanceToBeActioned[id];
        },
        errorMessage => {
          failedInstances[id] = instanceToBeActioned[id];
          failedInstances[id].errorMessage = errorMessage;
        }
      );
    } else if (processType === OperationType.RETRY) {
      await handleRetry(
        instanceToBeActioned[id],
        () => {
          successInstances[id] = instanceToBeActioned[id];
        },
        errorMessage => {
          failedInstances[id] = instanceToBeActioned[id];
          failedInstances[id].errorMessage = errorMessage;
        }
      );
    }
  }
  multiActionResult(successInstances, failedInstances);
};

export const getProcessInstanceDescription = (
  processInstance: GraphQL.ProcessInstance
) => {
  return {
    id: processInstance.id,
    name: processInstance.processName,
    description: processInstance.businessKey
  };
};

// function containing Api call to update process variables
export const handleVariableUpdate = async (
  processInstance: Pick<ProcessInstance, 'id' | 'endpoint'>,
  updateJson: object,
  setDisplayLabel: (displayLabel: boolean) => void,
  setDisplaySuccess: (displaySuccess: boolean) => void,
  setVariableError: (error: string) => void
) => {
  try {
    await axios
      .post(`${processInstance.endpoint}/${processInstance.id}`, updateJson)
      .then(() => {
        setDisplayLabel(false);
        setDisplaySuccess(true);
        setTimeout(() => {
          setDisplaySuccess(false);
        }, 2000);
      });
  } catch (error) {
    setVariableError(error.message);
  }
};
