import React from 'react';
import axios from 'axios';
import SVG from 'react-inlinesvg';
import {
  OnRunningIcon,
  CheckCircleIcon,
  BanIcon,
  PausedIcon,
  ErrorCircleOIcon,
  InfoCircleIcon,
  UndoIcon,
  ClockIcon
} from '@patternfly/react-icons';
import { GraphQL } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;
import ProcessInstance = GraphQL.ProcessInstance;
import JobStatus = GraphQL.JobStatus;
import { Title, TitleSizes } from '@patternfly/react-core';
import {
  BulkListItem,
  OperationType
} from '../components/Atoms/BulkList/BulkList';
export interface TriggerableNode {
  id: number;
  name: string;
  type: string;
  uniqueId: string;
  nodeDefinitionId: string;
}
/* tslint:disable:no-floating-promises */
export const ProcessInstanceIconCreator = (
  state: ProcessInstanceState
): JSX.Element => {
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

export const setTitle = (
  titleStatus: string,
  titleText: string
): JSX.Element => {
  let icon = null;

  switch (titleStatus) {
    case 'success':
      icon = (
        <InfoCircleIcon
          className="pf-u-mr-sm"
          color="var(--pf-global--info-color--100)"
        />
      );
      break;
    case 'failure':
      icon = (
        <InfoCircleIcon
          className="pf-u-mr-sm"
          color="var(--pf-global--danger-color--100)"
        />
      );
      break;
  }

  return (
    <Title headingLevel="h1" size={TitleSizes['2xl']}>
      {icon}
      <span>{titleText}</span>
    </Title>
  );
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
    .catch((error) => {
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
    .catch((error) => {
      onCancelFailure(JSON.stringify(error.message));
    });
};

export const performMultipleAction = async (
  instanceToBeActioned: (GraphQL.ProcessInstance & { errorMessage?: string })[],
  multiActionResult: (
    successInstances: GraphQL.ProcessInstance[],
    failedInstances: (GraphQL.ProcessInstance & { errorMessage?: string })[]
  ) => void,
  processType: OperationType
) => {
  const successInstances = [];
  const failedInstances = [];
  for (const instance of instanceToBeActioned) {
    if (processType === OperationType.ABORT) {
      await handleAbort(
        instance,
        () => {
          successInstances.push(instance);
        },
        (errorMessage) => {
          instance.errorMessage = errorMessage;
          failedInstances.push(instance);
        }
      );
    } else if (processType === OperationType.SKIP) {
      await handleSkip(
        instance,
        () => {
          successInstances.push(instance);
        },
        (errorMessage) => {
          instance.errorMessage = errorMessage;
          failedInstances.push(instance);
        }
      );
    } else if (processType === OperationType.RETRY) {
      await handleRetry(
        instance,
        () => {
          successInstances.push(instance);
        },
        (errorMessage) => {
          instance.errorMessage = errorMessage;
          failedInstances.push(instance);
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

export const getJobsDescription = (job: GraphQL.Job) => {
  return {
    id: job.id,
    name: job.processId
  };
};

// function containing Api call to update process variables
export const handleVariableUpdate = async (
  processInstance: Pick<ProcessInstance, 'id' | 'endpoint'>,
  updateJson: Record<string, unknown>,
  setDisplayLabel: (displayLabel: boolean) => void,
  setDisplaySuccess: (displaySuccess: boolean) => void,
  setUpdateJson: (updateJson: Record<string, unknown>) => void,
  setVariableError: (error: string) => void
): Promise<void> => {
  try {
    await axios
      .put(`${processInstance.endpoint}/${processInstance.id}`, updateJson)
      .then((res) => {
        setUpdateJson(res.data);
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

export const handleJobReschedule = async (
  job,
  repeatInterval,
  repeatLimit,
  rescheduleClicked,
  setErrorMessage,
  setRescheduleClicked,
  scheduleDate,
  refetch
): Promise<any> => {
  let parameter = {};
  if (repeatInterval === null && repeatLimit === null) {
    parameter = {
      expirationTime: new Date(scheduleDate)
    };
  } else {
    parameter = {
      expirationTime: new Date(scheduleDate),
      repeatInterval,
      repeatLimit
    };
  }
  try {
    await axios.patch(`${job.endpoint}/${job.id}`, parameter).then((res) => {
      setRescheduleClicked(!rescheduleClicked);
      refetch();
    });
  } catch (error) {
    setRescheduleClicked(!rescheduleClicked);
    setErrorMessage(error.message);
    refetch();
  }
};

export const handleNodeTrigger = async (
  processInstance: Pick<ProcessInstance, 'id' | 'serviceUrl' | 'processId'>,
  node: Pick<TriggerableNode, 'nodeDefinitionId'>,
  onTriggerSuccess: () => void,
  onTriggerFailure: (error: string) => void
): Promise<void> => {
  try {
    await axios.post(
      `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/nodes/${node.nodeDefinitionId}`
    );
    onTriggerSuccess();
  } catch (error) {
    onTriggerFailure(JSON.stringify(error.message));
  }
};

export const getTriggerableNodes = async (
  processInstance: Pick<GraphQL.ProcessInstance, 'processId' | 'serviceUrl'>,
  successCallback: (resultNodes: TriggerableNode[]) => void,
  failureCallback: (errorMessage: string) => void
): Promise<void> => {
  try {
    const result = await axios.get(
      `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/nodes`
    );
    successCallback(result.data);
  } catch (error) {
    failureCallback(error.message);
  }
};

export const jobCancel = async (
  job: Pick<GraphQL.Job, 'id' | 'endpoint'>,
  setModalTitle: (title: JSX.Element) => void,
  setModalContent: (content: string) => void,
  refetch
) => {
  try {
    await axios.delete(`${job.endpoint}/${job.id}`);
    setModalTitle(setTitle('success', 'Job cancel'));
    setModalContent(`The job: ${job.id} is canceled successfully`);
    refetch();
  } catch (error) {
    setModalTitle(setTitle('failure', 'Job cancel'));
    setModalContent(
      `The job: ${job.id} failed to cancel. Error message: ${error.message}`
    );
    refetch();
  }
};

export const performMultipleCancel = async (
  jobsToBeActioned: (GraphQL.Job & { errorMessage?: string })[],
  multiActionResult: (
    successJobs: GraphQL.Job[],
    failedJobs: GraphQL.Job[]
  ) => void
) => {
  const successJobs = [];
  const failedJobs = [];
  for (const job of jobsToBeActioned) {
    try {
      await axios.delete(`${job.endpoint}/${job.id}`);
      successJobs.push(job);
    } catch (error) {
      job.errorMessage = JSON.stringify(error.message);
      failedJobs.push(job);
    }
  }
  multiActionResult(successJobs, failedJobs);
};

export const getSvg = async (data, setSvg, setSvgError): Promise<void> => {
  setSvg(null);
  await axios
    .get(
      `/svg/processes/${data.ProcessInstances[0].processId}/instances/${data.ProcessInstances[0].id}`
    )
    .then((res) => {
      const temp = <SVG src={res.data} />;
      setSvg(temp);
    })
    .catch(async (error) => {
      if (data.ProcessInstances[0].serviceUrl) {
        axios
          .get(
            `${data.ProcessInstances[0].serviceUrl}/svg/processes/${data.ProcessInstances[0].processId}/instances/${data.ProcessInstances[0].id}`
          )
          .then((res) => {
            const temp = <SVG src={res.data} />;
            setSvg(temp);
          })
          .catch((err) => {
            if (err.response && err.response.status !== 404) {
              setSvgError(err.message);
            }
          });
      }
    });
};

export const formatForBulkListProcessInstance = (
  processInstanceList: (GraphQL.ProcessInstance & { errorMessage?: string })[]
): BulkListItem[] => {
  const formattedItems: BulkListItem[] = [];
  processInstanceList.forEach(
    (item: GraphQL.ProcessInstance & { errorMessage?: string }) => {
      const formattedObj: BulkListItem = {
        id: item.id,
        description: item.businessKey,
        name: item.processName,
        errorMessage: item.errorMessage ? item.errorMessage : null
      };
      formattedItems.push(formattedObj);
    }
  );
  return formattedItems;
};

export const formatForBulkListJob = (
  jobsList: (GraphQL.Job & { errorMessage?: string })[]
): BulkListItem[] => {
  const formattedItems: BulkListItem[] = [];
  jobsList.forEach((item: GraphQL.Job & { errorMessage?: string }) => {
    const formattedObj: BulkListItem = {
      id: item.id,
      name: item.processId,
      description: item.id,
      errorMessage: item.errorMessage ? item.errorMessage : null
    };
    formattedItems.push(formattedObj);
  });
  return formattedItems;
};

export const checkProcessInstanceState = (
  processInstance: Pick<
    GraphQL.ProcessInstance,
    'state' | 'addons' | 'serviceUrl'
  >
): boolean => {
  if (
    (processInstance.state === 'ERROR' ||
      processInstance.state === 'ACTIVE' ||
      processInstance.state === 'SUSPENDED') &&
    processInstance.addons.includes('process-management') &&
    processInstance.serviceUrl
  ) {
    return false;
  } else {
    return true;
  }
};

export const alterOrderByObj = (orderByObj): GraphQL.ProcessInstanceOrderBy => {
  if (orderByObj['id']) {
    orderByObj['processName'] = orderByObj['id'];
    delete orderByObj['id'];
  } else if (orderByObj['status']) {
    orderByObj['state'] = orderByObj['status'];
    delete orderByObj['status'];
  } else if (orderByObj['created']) {
    orderByObj['start'] = orderByObj['created'];
    delete orderByObj['created'];
  }
  return orderByObj;
};
