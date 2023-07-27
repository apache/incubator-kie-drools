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

import {
  Job,
  JobCancel,
  JobStatus,
  NodeInstance,
  ProcessInstance
} from '@kogito-apps/management-console-shared/dist/types';
import { setTitle } from '@kogito-apps/management-console-shared/dist/utils/Utils';
import {
  handleJobRescheduleUtil,
  handleNodeInstanceCancel,
  handleNodeInstanceRetrigger,
  jobCancel,
  JobsIconCreator
} from '../Utils';
import TestProcessDetailsDriver from '../../envelope/tests/mocks/TestProcessDetailsDriver';
import { handleSkip, handleRetry } from '../Utils';
import wait from 'waait';
const children = 'children';

export const processInstance: ProcessInstance = {
  endpoint: '',
  id: '',
  lastUpdate: undefined,
  nodes: [],
  processId: '',
  start: undefined,
  state: undefined
};

export const node: NodeInstance = {
  definitionId: '',
  enter: undefined,
  id: '',
  name: '',
  nodeId: '',
  type: ''
};

export const job: Job = {
  callbackEndpoint: '',
  expirationTime: undefined,
  id: '',
  lastUpdate: undefined,
  priority: 0,
  processId: '',
  processInstanceId: '',
  repeatInterval: 0,
  repeatLimit: 0,
  retries: 0,
  scheduledId: '',
  status: undefined
};

const driver = new TestProcessDetailsDriver(
  '2d962eef-45b8-48a9-ad4e-9cde0ad6af89'
);

describe('process details package utils', () => {
  it('Jobs icon creator tests', () => {
    const jobsErrorResult = JobsIconCreator(JobStatus.Error);
    const jobsCanceledResult = JobsIconCreator(JobStatus.Canceled);
    const jobsScheduledResult = JobsIconCreator(JobStatus.Scheduled);
    const jobsExecutedResult = JobsIconCreator(JobStatus.Executed);
    const jobsRetryResult = JobsIconCreator(JobStatus.Retry);

    expect(jobsErrorResult.props[children][1]).toEqual('Error');
    expect(jobsCanceledResult.props[children][1]).toEqual('Canceled');
    expect(jobsScheduledResult.props[children][1]).toEqual('Scheduled');
    expect(jobsRetryResult.props[children][1]).toEqual('Retry');
    expect(jobsExecutedResult.props[children][1]).toEqual('Executed');
  });
});

describe('handleSkip tests', () => {
  const onSkipSuccess = jest.fn();
  const onSkipFailure = jest.fn();
  it('success test', async () => {
    const mockDriverHandleSkipSuccess = jest.spyOn(driver, 'handleProcessSkip');
    mockDriverHandleSkipSuccess.mockResolvedValue();
    await handleSkip(processInstance, driver, onSkipSuccess, onSkipFailure);
    await wait(0);
    expect(onSkipSuccess).toHaveBeenCalled();
  });

  it('fails executing skip process', async () => {
    const mockDriverHandleSkipFailed = jest.spyOn(driver, 'handleProcessSkip');
    mockDriverHandleSkipFailed.mockRejectedValue({ message: '403 error' });
    await handleSkip(processInstance, driver, onSkipSuccess, onSkipFailure);
    await wait(0);
    expect(onSkipFailure.mock.calls[0][0]).toEqual('"403 error"');
    expect(onSkipFailure).toHaveBeenCalled();
  });
});

describe('handleRetry tests', () => {
  const onSkipSuccess = jest.fn();
  const onSkipFailure = jest.fn();
  it('success test', async () => {
    const mockDriverHandleProcessRetrySuccess = jest.spyOn(
      driver,
      'handleProcessRetry'
    );
    mockDriverHandleProcessRetrySuccess.mockResolvedValue();
    await handleRetry(processInstance, driver, onSkipSuccess, onSkipFailure);
    await wait(0);
    expect(onSkipSuccess).toHaveBeenCalled();
  });

  it('fails executing retry process', async () => {
    const mockDriverHandleProcessRetryFailed = jest.spyOn(
      driver,
      'handleProcessRetry'
    );
    mockDriverHandleProcessRetryFailed.mockRejectedValue({
      message: '403 error'
    });
    await handleRetry(processInstance, driver, onSkipSuccess, onSkipFailure);
    await wait(0);
    expect(onSkipFailure.mock.calls[0][0]).toEqual('"403 error"');
    expect(onSkipFailure).toHaveBeenCalled();
  });
});

describe('handleNodeInstanceCancel tests', () => {
  const onSkipSuccess = jest.fn();
  const onSkipFailure = jest.fn();
  it('success test', async () => {
    const mockDriverHandleNodeInstanceCancelSuccess = jest.spyOn(
      driver,
      'handleNodeInstanceCancel'
    );
    mockDriverHandleNodeInstanceCancelSuccess.mockResolvedValue();
    await handleNodeInstanceCancel(
      processInstance,
      driver,
      node,
      onSkipSuccess,
      onSkipFailure
    );
    await wait(0);
    expect(onSkipSuccess).toHaveBeenCalled();
  });

  it('fails executing retry process', async () => {
    const mockDriverHandleNodeInstanceCancelFailed = jest.spyOn(
      driver,
      'handleNodeInstanceCancel'
    );
    mockDriverHandleNodeInstanceCancelFailed.mockRejectedValue({
      message: '403 error'
    });
    await handleNodeInstanceCancel(
      processInstance,
      driver,
      node,
      onSkipSuccess,
      onSkipFailure
    );
    await wait(0);
    expect(onSkipFailure.mock.calls[0][0]).toEqual('"403 error"');
    expect(onSkipFailure).toHaveBeenCalled();
  });
});

describe('jobCancel tests', () => {
  it('success test', async () => {
    const successTitle = 'Success';
    const successContent = 'good job';
    const setModalTitle: (title: JSX.Element) => void = jest.fn();
    const setModalContent: (content: string) => void = jest.fn();
    const mockDriverJobCancelSuccess = jest.spyOn(driver, 'cancelJob');

    const jobCancelInstance: JobCancel = {
      modalContent: successContent,
      modalTitle: successTitle
    };
    mockDriverJobCancelSuccess.mockResolvedValue(jobCancelInstance);
    await jobCancel(driver, job, setModalTitle, setModalContent);
    await wait(0);
    expect(setModalTitle).toHaveBeenCalledWith(
      setTitle(successTitle, 'Job cancel')
    );
    expect(setModalContent).toHaveBeenCalledWith(successContent);
  });

  it('failed test', async () => {
    const failedTitle = 'failed';
    const failedContent = 'not good job';
    const setModalTitle: (title: JSX.Element) => void = jest.fn();
    const setModalContent: (content: string) => void = jest.fn();
    const mockDriverJobCancelSuccess = jest.spyOn(driver, 'cancelJob');

    const jobCancelInstance: JobCancel = {
      modalContent: failedContent,
      modalTitle: failedTitle
    };
    mockDriverJobCancelSuccess.mockResolvedValue(jobCancelInstance);
    await jobCancel(driver, job, setModalTitle, setModalContent);
    await wait(0);
    expect(setModalTitle).toHaveBeenCalledWith(
      setTitle(failedTitle, 'Job cancel')
    );
    expect(setModalContent).toHaveBeenCalledWith(failedContent);
  });
});

describe('handleNodeInstanceRetrigger tests', () => {
  const onSkipSuccess = jest.fn();
  const onSkipFailure = jest.fn();
  it('success test', async () => {
    const mockDriverHandleNodeInstanceCancelSuccess = jest.spyOn(
      driver,
      'handleNodeInstanceRetrigger'
    );
    mockDriverHandleNodeInstanceCancelSuccess.mockResolvedValue();
    await handleNodeInstanceRetrigger(
      processInstance,
      driver,
      node,
      onSkipSuccess,
      onSkipFailure
    );
    await wait(0);
    expect(onSkipSuccess).toHaveBeenCalled();
  });

  it('fails executing retry process', async () => {
    const mockDriverHandleNodeInstanceCancelFailed = jest.spyOn(
      driver,
      'handleNodeInstanceRetrigger'
    );
    mockDriverHandleNodeInstanceCancelFailed.mockRejectedValue({
      message: '403 error'
    });
    await handleNodeInstanceRetrigger(
      processInstance,
      driver,
      node,
      onSkipSuccess,
      onSkipFailure
    );
    await wait(0);
    expect(onSkipFailure.mock.calls[0][0]).toEqual('"403 error"');
    expect(onSkipFailure).toHaveBeenCalled();
  });
});

describe('test utils of jobs', () => {
  const repeatInterval = null;
  const repeatLimit = null;
  const scheduleDate = '2020-08-27T03:35:50.147Z';
  const handleRescheduleAction = jest.fn();
  const setRescheduleError: (modalContent: string) => void = jest.fn();

  it('test reschedule function', async () => {
    const successTitle = 'success';
    const successContent = 'good job';
    const mockDriverRescheduleJobSuccess = jest.spyOn(driver, 'rescheduleJob');
    mockDriverRescheduleJobSuccess.mockResolvedValue({
      modalTitle: successTitle,
      modalContent: successContent
    });
    await handleJobRescheduleUtil(
      repeatInterval,
      repeatLimit,
      scheduleDate,
      job,
      handleRescheduleAction,
      driver,
      setRescheduleError
    );
    expect(handleRescheduleAction).toHaveBeenCalled();
  });
  it('test error response for reschedule function', async () => {
    const failedTitle = 'failure';
    const failedContent = 'not good job';
    const mockDriverRescheduleJobSuccess = jest.spyOn(driver, 'rescheduleJob');
    mockDriverRescheduleJobSuccess.mockResolvedValue({
      modalTitle: failedTitle,
      modalContent: failedContent
    });

    await handleJobRescheduleUtil(
      repeatInterval,
      repeatLimit,
      scheduleDate,
      job,
      handleRescheduleAction,
      driver,
      setRescheduleError
    );
    expect(handleRescheduleAction).toHaveBeenCalled();
    expect(setRescheduleError).toHaveBeenCalledWith(failedContent);
  });
});
