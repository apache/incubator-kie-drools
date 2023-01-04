import {
  ProcessInstanceIconCreator,
  setTitle,
  handleSkip,
  handleRetry,
  handleAbort,
  handleNodeInstanceRetrigger,
  handleNodeInstanceCancel,
  handleVariableUpdate,
  performMultipleAction,
  JobsIconCreator,
  handleJobReschedule,
  handleNodeTrigger,
  getTriggerableNodes,
  jobCancel,
  getJobsDescription,
  performMultipleCancel,
  getSvg,
  formatForBulkListProcessInstance,
  formatForBulkListJob,
  checkProcessInstanceState,
  alterOrderByObj
} from '../Utils';
import { GraphQL } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;
import JobStatus = GraphQL.JobStatus;
import axios from 'axios';
import wait from 'waait';
import { OperationType } from '../../components/Atoms/BulkList/BulkList';
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;
const children = 'children';

describe('uitility function testing', () => {
  it('state icon creator tests', () => {
    const activeTestResult = ProcessInstanceIconCreator(
      ProcessInstanceState.Active
    );
    const completedTestResult = ProcessInstanceIconCreator(
      ProcessInstanceState.Completed
    );
    const errorTestResult = ProcessInstanceIconCreator(
      ProcessInstanceState.Error
    );
    const suspendedTestResult = ProcessInstanceIconCreator(
      ProcessInstanceState.Suspended
    );
    const abortedTestResult = ProcessInstanceIconCreator(
      ProcessInstanceState.Aborted
    );

    expect(activeTestResult.props[children][1]).toEqual('Active');
    expect(completedTestResult.props[children][1]).toEqual('Completed');
    expect(errorTestResult.props[children][1]).toEqual('Error');
    expect(suspendedTestResult.props[children][1]).toEqual('Suspended');
    expect(abortedTestResult.props[children][1]).toEqual('Aborted');
  });
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
  it('set title tests', () => {
    const successResult = setTitle('success', 'Abort operation');
    const failureResult = setTitle('failure', 'Skip operation');
    expect(successResult.props[children][1].props.children).toEqual(
      'Abort operation'
    );
    expect(failureResult.props[children][1].props.children).toEqual(
      'Skip operation'
    );
  });

  describe('handle skip tests', () => {
    const processInstanceData = {
      id: '123',
      processId: 'trav',
      serviceUrl: 'http://localhost:4000',
      state: ProcessInstanceState.Active
    };
    it('executes skip process successfully', async () => {
      mockedAxios.post.mockResolvedValue({});
      const onSkipSuccess = jest.fn();
      const onSkipFailure = jest.fn();
      await handleSkip(processInstanceData, onSkipSuccess, onSkipFailure);
      await wait(0);
      expect(onSkipSuccess).toHaveBeenCalled();
    });
    it('fails executing skip process', async () => {
      const onSkipSuccess = jest.fn();
      const onSkipFailure = jest.fn();
      mockedAxios.post.mockRejectedValue({ message: '403 error' });
      await handleSkip(processInstanceData, onSkipSuccess, onSkipFailure);
      await wait(0);
      expect(onSkipFailure.mock.calls[0][0]).toEqual('"403 error"');
      expect(onSkipFailure).toHaveBeenCalled();
    });
  });

  describe('handle Retry tests', () => {
    const processInstanceData = {
      id: '123',
      processId: 'trav',
      serviceUrl: 'http://localhost:4000',
      state: ProcessInstanceState.Active
    };

    it('executes retry process successfully', async () => {
      const onRetrySuccess = jest.fn();
      const onRetryFailure = jest.fn();
      mockedAxios.post.mockResolvedValue({});
      await handleRetry(processInstanceData, onRetrySuccess, onRetryFailure);
      await wait(0);
      expect(onRetrySuccess).toHaveBeenCalled();
    });
    it('fails executing Retry process', async () => {
      const onRetrySuccess = jest.fn();
      const onRetryFailure = jest.fn();
      mockedAxios.post.mockRejectedValue({ message: '403 error' });
      await handleRetry(processInstanceData, onRetrySuccess, onRetryFailure);
      await wait(0);
      expect(onRetryFailure.mock.calls[0][0]).toEqual('"403 error"');
      expect(onRetryFailure).toHaveBeenCalled();
    });
  });

  describe('handle Abort tests', () => {
    const processInstanceData = {
      id: '123',
      processId: 'trav',
      serviceUrl: 'http://localhost:4000',
      state: ProcessInstanceState.Active
    };
    it('executes Abort process successfully', async () => {
      const onAbortSuccess = jest.fn();
      const onAbortFailure = jest.fn();
      mockedAxios.delete.mockResolvedValue({});
      await handleAbort(processInstanceData, onAbortSuccess, onAbortFailure);
      await wait(0);
      expect(onAbortSuccess).toHaveBeenCalled();
    });
    it('fails executing Abort process', async () => {
      const onAbortSuccess = jest.fn();
      const onAbortFailure = jest.fn();
      mockedAxios.delete.mockRejectedValue({ message: '403 error' });
      await handleAbort(processInstanceData, onAbortSuccess, onAbortFailure);
      await wait(0);
      expect(onAbortFailure.mock.calls[0][0]).toEqual('"403 error"');
      expect(onAbortFailure).toHaveBeenCalled();
    });
  });

  describe('retrigger click tests', () => {
    const processInstanceData = {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
      processId: 'trav',
      serviceUrl: 'http://localhost:4000',
      state: ProcessInstanceState.Active,
      nodes: [
        {
          nodeId: '2',
          name: 'Confirm travel',
          definitionId: 'UserTask_2',
          id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
          enter: '2019-10-22T04:43:01.148Z',
          exit: null,
          type: 'HumanTaskNode'
        }
      ]
    };
    const nodeObject = {
      nodeId: '2',
      name: 'Confirm travel',
      definitionId: 'UserTask_2',
      id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
      enter: '2019-10-22T04:43:01.148Z',
      exit: null,
      type: 'HumanTaskNode'
    };
    it('executes retrigger node process successfully', async () => {
      const onRetriggerSuccess = jest.fn();
      const onRetriggerFailure = jest.fn();
      mockedAxios.post.mockResolvedValue({});
      handleNodeInstanceRetrigger(
        processInstanceData,
        nodeObject,
        onRetriggerSuccess,
        onRetriggerFailure
      );
      await wait(0);
      expect(onRetriggerSuccess).toHaveBeenCalled();
    });
    it('fails executing retrigger node process', async () => {
      mockedAxios.post.mockRejectedValue({ message: '403 error' });
      const onRetriggerSuccess = jest.fn();
      const onRetriggerFailure = jest.fn();
      handleNodeInstanceRetrigger(
        processInstanceData,
        nodeObject,
        onRetriggerSuccess,
        onRetriggerFailure
      );
      await wait(0);
      expect(onRetriggerFailure.mock.calls[0][0]).toEqual('"403 error"');
      expect(onRetriggerFailure).toHaveBeenCalled();
    });
  });

  describe('Cancel click tests', () => {
    const processInstanceData = {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
      processId: 'trav',
      serviceUrl: 'http://localhost:4000',
      state: ProcessInstanceState.Error,
      nodes: [
        {
          nodeId: '2',
          name: 'Confirm travel',
          definitionId: 'UserTask_2',
          id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
          enter: '2019-10-22T04:43:01.148Z',
          exit: null,
          type: 'HumanTaskNode'
        }
      ]
    };
    const nodeObject = {
      nodeId: '2',
      name: 'Confirm travel',
      definitionId: 'UserTask_2',
      id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
      enter: '2019-10-22T04:43:01.148Z',
      exit: null,
      type: 'HumanTaskNode'
    };
    it('executes cancel node process successfully', async () => {
      const onCancelSuccess = jest.fn();
      const onCancelFailure = jest.fn();
      mockedAxios.delete.mockResolvedValue({});
      handleNodeInstanceCancel(
        processInstanceData,
        nodeObject,
        onCancelSuccess,
        onCancelFailure
      );
      await wait(0);
      expect(onCancelSuccess).toHaveBeenCalled();
    });
    it('fails executing cancel node process', async () => {
      mockedAxios.delete.mockRejectedValue({ message: '403 error' });
      const onCancelSuccess = jest.fn();
      const onCancelFailure = jest.fn();
      handleNodeInstanceCancel(
        processInstanceData,
        nodeObject,
        onCancelSuccess,
        onCancelFailure
      );
      await wait(0);
      expect(onCancelFailure.mock.calls[0][0]).toEqual('"403 error"');
      expect(onCancelFailure).toHaveBeenCalled();
    });
  });

  describe('handle multiple abort click tests', () => {
    const instanceToBeActioned = [
      {
        id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
        processId: 'trav',
        serviceUrl: 'http://localhost:4000',
        state: GraphQL.ProcessInstanceState.Active,
        endpoint: 'http://localhost:4000',
        nodes: [],
        start: '2020-10-22T04:43:01.148Z',
        lastUpdate: '2020-11-22T04:43:01.148Z'
      }
    ];
    it('executes multi-abort process successfully', async () => {
      const onMultiActionResult = jest.fn();
      mockedAxios.delete.mockResolvedValue({});
      await performMultipleAction(
        instanceToBeActioned,
        onMultiActionResult,
        OperationType.ABORT
      );
      await wait(0);
      expect(onMultiActionResult.mock.calls[0][0]).toBeDefined();
      expect(onMultiActionResult).toHaveBeenCalled();
    });
    it('catched an error in the instance(abort)', async () => {
      const onMultiActionResult = jest.fn();
      mockedAxios.delete.mockRejectedValue({ message: '404 error' });
      await performMultipleAction(
        instanceToBeActioned,
        onMultiActionResult,
        OperationType.ABORT
      );
      await wait(0);
      expect(onMultiActionResult.mock.calls[0][1][0].errorMessage).toEqual(
        '"404 error"'
      );
    });
  });

  describe('handle multiple skip click tests', () => {
    const instanceToBeActioned = [
      {
        id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
        processId: 'trav',
        serviceUrl: 'http://localhost:4000',
        state: GraphQL.ProcessInstanceState.Error,
        endpoint: 'http://localhost:4000',
        nodes: [],
        start: '2020-10-22T04:43:01.148Z',
        lastUpdate: '2020-11-22T04:43:01.148Z'
      }
    ];
    it('executes multi-skip process successfully', async () => {
      const onMultiActionResult = jest.fn();
      mockedAxios.post.mockResolvedValue({});
      await performMultipleAction(
        instanceToBeActioned,
        onMultiActionResult,
        OperationType.SKIP
      );
      await wait(0);
      expect(onMultiActionResult.mock.calls[0][0]).toBeDefined();
      expect(onMultiActionResult).toHaveBeenCalled();
    });
    it('catched an error in the instance(skip)', async () => {
      const onMultiActionResult = jest.fn();
      mockedAxios.post.mockRejectedValue({ message: '404 error' });
      await performMultipleAction(
        instanceToBeActioned,
        onMultiActionResult,
        OperationType.SKIP
      );
      await wait(0);
      expect(onMultiActionResult.mock.calls[0][1][0].errorMessage).toEqual(
        '"404 error"'
      );
    });
  });

  describe('handle multiple retry click tests', () => {
    const instanceToBeActioned = [
      {
        id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
        processId: 'trav',
        serviceUrl: 'http://localhost:4000',
        state: GraphQL.ProcessInstanceState.Error,
        endpoint: 'http://localhost:4000',
        nodes: [],
        start: '2020-10-22T04:43:01.148Z',
        lastUpdate: '2020-11-22T04:43:01.148Z'
      }
    ];
    it('executes multi-retry process successfully', async () => {
      const onMultiActionResult = jest.fn();
      mockedAxios.post.mockResolvedValue({});
      await performMultipleAction(
        instanceToBeActioned,
        onMultiActionResult,
        OperationType.RETRY
      );
      await wait(0);
      expect(onMultiActionResult.mock.calls[0][0]).toBeDefined();
      expect(onMultiActionResult).toHaveBeenCalled();
    });
    it('catched an error in the instance(retry)', async () => {
      const onMultiActionResult = jest.fn();
      mockedAxios.post.mockRejectedValue({ message: '404 error' });
      await performMultipleAction(
        instanceToBeActioned,
        onMultiActionResult,
        OperationType.RETRY
      );
      await wait(0);
      expect(onMultiActionResult.mock.calls[0][1][0].errorMessage).toEqual(
        '"404 error"'
      );
    });
  });
  describe('test utilities of process variables', () => {
    it('test put method that updates process variables', async () => {
      mockedAxios.put.mockResolvedValue({
        status: 200,
        statusText: 'OK',
        data: {
          flight: {
            flightNumber: 'MX555',
            seat: null,
            gate: null,
            departure: '2020-09-23T03:30:00.000+05:30',
            arrival: '2020-09-28T03:30:00.000+05:30'
          },
          hotel: {
            name: 'Perfect hotel',
            address: {
              street: 'street',
              city: 'Sydney',
              zipCode: '12345',
              country: 'Australia'
            },
            phone: '09876543',
            bookingNumber: 'XX-012345',
            room: null
          },
          traveller: {
            firstName: 'Saravana',
            lastName: 'Srinivasan',
            email: 'Saravana@gmai.com',
            nationality: 'US',
            address: {
              street: 'street',
              city: 'city',
              zipCode: '123156',
              country: 'US'
            }
          },
          trip: {
            city: 'Sydney',
            country: 'Australia',
            begin: '2020-09-23T03:30:00.000+05:30',
            end: '2020-09-28T03:30:00.000+05:30',
            visaRequired: false
          }
        }
      });
      const setUpdateJson = jest.fn();
      const setDisplayLabel = jest.fn();
      const setDisplaySuccess = jest.fn();
      const setVariableError = jest.fn();
      const processInstance = {
        id: '0e5f1dde-cc5a-4b1f-8e06-dbb27bc489b4',
        endpoint: 'http://localhost:8080/travels'
      };
      const updateJson = {
        flight: {
          flightNumber: 'MX5555',
          seat: null,
          gate: null,
          departure: '2020-09-23T03:30:00.000+05:30',
          arrival: '2020-09-28T03:30:00.000+05:30'
        },
        hotel: {
          name: 'Perfect hotel',
          address: {
            street: 'street',
            city: 'Sydney',
            zipCode: '12345',
            country: 'Australia'
          },
          phone: '09876543',
          bookingNumber: 'XX-012345',
          room: null
        },
        traveller: {
          firstName: 'Saravana',
          lastName: 'Srinivasan',
          email: 'Saravana@gmai.com',
          nationality: 'US',
          address: {
            street: 'street',
            city: 'city',
            zipCode: '123156',
            country: 'US'
          }
        },
        trip: {
          city: 'Sydney',
          country: 'Australia',
          begin: '2020-09-23T03:30:00.000+05:30',
          end: '2020-09-28T03:30:00.000+05:30',
          visaRequired: false
        }
      };
      await handleVariableUpdate(
        processInstance,
        updateJson,
        setUpdateJson,
        setDisplaySuccess,
        setDisplayLabel,
        setVariableError
      );
      expect(setDisplaySuccess).toHaveBeenCalled();
      expect(setUpdateJson).toHaveBeenCalled();
    });
  });

  describe('test utilities of jobs', () => {
    it('test reschedule function', async () => {
      mockedAxios.patch.mockResolvedValue({
        status: 200,
        statusText: 'OK',
        data: {
          callbackEndpoint:
            'http://localhost:8080/management/jobs/travels/instances/9865268c-64d7-3a44-8972-7325b295f7cc/timers/58180644-2fdf-4261-83f2-f4e783d308a3_0',
          executionCounter: 0,
          executionResponse: null,
          expirationTime: '2020-10-16T10:17:22.879Z',
          id: '58180644-2fdf-4261-83f2-f4e783d308a3_0',
          lastUpdate: '2020-10-07T07:41:31.467Z',
          priority: 0,
          processId: 'travels',
          processInstanceId: '9865268c-64d7-3a44-8972-7325b295f7cc',
          repeatInterval: null,
          repeatLimit: null,
          retries: 0,
          rootProcessId: null,
          rootProcessInstanceId: null,
          scheduledId: null,
          status: 'SCHEDULED'
        }
      });
      const job = {
        id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
        processId: 'travels',
        processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
        rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
        status: GraphQL.JobStatus.Executed,
        priority: 0,
        callbackEndpoint:
          'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
        repeatInterval: 1,
        repeatLimit: 3,
        scheduledId: '0',
        retries: 0,
        lastUpdate: '2020-08-27T03:35:50.147Z',
        expirationTime: '2020-08-27T03:35:50.147Z'
      };
      const repeatInterval = 2;
      const repeatLimit = 1;
      const rescheduleClicked = false;
      const setRescheduleClicked = jest.fn();
      const scheduleDate = '2020-08-27T03:35:50.147Z';
      const refetch = jest.fn();
      const setErrorMessage = jest.fn();
      await handleJobReschedule(
        job,
        repeatInterval,
        repeatLimit,
        rescheduleClicked,
        setErrorMessage,
        setRescheduleClicked,
        scheduleDate,
        refetch
      );
      expect(setRescheduleClicked).toHaveBeenCalled();
    });
    it('test error response for reschedule function', async () => {
      mockedAxios.patch.mockRejectedValue({ message: '403 error' });
      const job = {
        id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
        processId: 'travels',
        processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
        rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
        status: GraphQL.JobStatus.Executed,
        priority: 0,
        callbackEndpoint:
          'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
        repeatInterval: 1,
        repeatLimit: 3,
        scheduledId: '0',
        retries: 0,
        lastUpdate: '2020-08-27T03:35:50.147Z',
        expirationTime: '2020-08-27T03:35:50.147Z'
      };
      const repeatInterval = null;
      const repeatLimit = null;
      const rescheduleClicked = false;
      const setRescheduleClicked = jest.fn();
      const scheduleDate = '2020-08-27T03:35:50.147Z';
      const refetch = jest.fn();
      const setErrorMessage = jest.fn();
      await handleJobReschedule(
        job,
        repeatInterval,
        repeatLimit,
        rescheduleClicked,
        setErrorMessage,
        setRescheduleClicked,
        scheduleDate,
        refetch
      );
      expect(setRescheduleClicked).toHaveBeenCalled();
    });
  });

  describe('handle node trigger click tests', () => {
    const ProcessInstanceData = {
      id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
      processId: 'hotelBooking',
      serviceUrl: 'http://localhost:4000'
    };
    const node = {
      nodeDefinitionId: '_4165a571-2c79-4fd0-921e-c6d5e7851b67'
    };
    it('executes node trigger successfully', async () => {
      const onTriggerSuccess = jest.fn();
      const onTriggerFailure = jest.fn();
      mockedAxios.post.mockResolvedValue({});
      await handleNodeTrigger(
        ProcessInstanceData,
        node,
        onTriggerSuccess,
        onTriggerFailure
      );
      await wait(0);
      expect(onTriggerSuccess).toHaveBeenCalled();
    });
    it('fails to execute node trigger', async () => {
      const onTriggerSuccess = jest.fn();
      const onTriggerFailure = jest.fn();
      mockedAxios.post.mockRejectedValue({ message: '404 error' });
      await handleNodeTrigger(
        ProcessInstanceData,
        node,
        onTriggerSuccess,
        onTriggerFailure
      );
      await wait(0);
      expect(onTriggerFailure).toHaveBeenCalled();
      expect(onTriggerFailure.mock.calls[0][0]).toEqual('"404 error"');
    });
  });

  describe('retrieve list of triggerable nodes test', () => {
    const mockTriggerableNodes = [
      {
        nodeDefinitionId: '_BDA56801-1155-4AF2-94D4-7DAADED2E3C0',
        name: 'Send visa application',
        id: 1,
        type: 'ActionNode',
        uniqueId: '1'
      },
      {
        nodeDefinitionId: '_175DC79D-C2F1-4B28-BE2D-B583DFABF70D',
        name: 'Book',
        id: 2,
        type: 'Split',
        uniqueId: '2'
      },
      {
        nodeDefinitionId: '_E611283E-30B0-46B9-8305-768A002C7518',
        name: 'visasrejected',
        id: 3,
        type: 'EventNode',
        uniqueId: '3'
      }
    ];

    const processInstance = {
      processId: 'travels',
      serviceUrl: 'http://localhost:4000'
    };
    it('successfully retrieves the list of nodes', async () => {
      mockedAxios.get.mockResolvedValue({
        data: mockTriggerableNodes
      });
      const successCallback = jest.fn();
      const failureCallback = jest.fn();
      await getTriggerableNodes(
        processInstance,
        successCallback,
        failureCallback
      );
      expect(successCallback).toHaveBeenCalled();
      expect(successCallback.mock.calls[0][0]).toStrictEqual(
        mockTriggerableNodes
      );
    });
    it('fails to retrieve the list of nodes', async () => {
      mockedAxios.get.mockRejectedValue({ message: '403 error' });
      const successCallback = jest.fn();
      const failureCallback = jest.fn();
      await getTriggerableNodes(
        processInstance,
        successCallback,
        failureCallback
      );
      expect(failureCallback).toHaveBeenCalled();
      expect(failureCallback.mock.calls[0][0]).toEqual('403 error');
    });
  });

  describe('job cancel tests', () => {
    const job = {
      id: 'T3113e-vbg43-2234-lo89-cpmw3214ra0fa_0',
      processId: 'travels',
      processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
      rootProcessId: '',
      status: 'ERROR',
      priority: 0,
      callbackEndpoint:
        'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
      repeatInterval: null,
      repeatLimit: null,
      scheduledId: null,
      retries: 0,
      lastUpdate: '2020-08-27T03:35:54.635Z',
      expirationTime: '2020-08-27T04:35:54.631Z'
    };
    const onJobCancelSuccess = jest.fn();
    const onJobCancelFailure = jest.fn();
    const refetch = jest.fn();
    it('executes job cancel successfully', async () => {
      mockedAxios.delete.mockResolvedValue({});
      await jobCancel(job, onJobCancelSuccess, onJobCancelFailure, refetch);
      await wait(0);
      expect(onJobCancelSuccess).toHaveBeenCalled();
    });

    it('fails to execute job cancel', async () => {
      mockedAxios.delete.mockRejectedValue({ message: '404 error' });
      await jobCancel(job, onJobCancelSuccess, onJobCancelFailure, refetch);
      await wait(0);
      expect(onJobCancelFailure).toHaveBeenCalled();
      expect(onJobCancelFailure.mock.calls[0][0]).toEqual(
        'The job: T3113e-vbg43-2234-lo89-cpmw3214ra0fa_0 is canceled successfully'
      );
    });
  });
  it('get jobs description tests', () => {
    const job = {
      id: 'dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
      processId: 'travels',
      processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
      rootProcessId: '',
      status: GraphQL.JobStatus.Scheduled,
      priority: 0,
      callbackEndpoint:
        'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
      repeatInterval: null,
      repeatLimit: null,
      scheduledId: null,
      retries: 0,
      lastUpdate: '2020-08-27T03:35:54.635Z',
      expirationTime: '2020-08-27T04:35:54.631Z',
      endpoint: 'http://localhost:4000/jobs'
    };
    const result = getJobsDescription(job);
    expect(result).toEqual({
      id: 'dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
      name: 'travels'
    });
  });
  describe('bulk cancel tests', () => {
    const bulkJobs = [
      {
        id: 'dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
        processId: 'travels',
        processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
        rootProcessId: '',
        status: GraphQL.JobStatus.Scheduled,
        priority: 0,
        callbackEndpoint:
          'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
        repeatInterval: null,
        repeatLimit: null,
        scheduledId: null,
        retries: 0,
        lastUpdate: '2020-08-27T03:35:54.635Z',
        expirationTime: '2020-08-27T04:35:54.631Z',
        endpoint: 'http://localhost:4000/jobs',
        errorMessage: ''
      }
    ];
    it('bulk cancel success', async () => {
      mockedAxios.delete.mockResolvedValue({});
      const result = jest.fn();
      await performMultipleCancel(bulkJobs, result);
      await wait(0);
      expect(result.mock.calls[0][0]).toBeDefined();
      expect(result.mock.calls[0][1]).toEqual([]);
    });
    it('bulk cancel failure', async () => {
      mockedAxios.delete.mockRejectedValue({});
      const result = jest.fn();
      await performMultipleCancel(bulkJobs, result);
      await wait(0);
      expect(result.mock.calls[0][0]).toEqual([]);
      expect(result.mock.calls[0][1]).toBeDefined();
    });
  });
  describe('test utility of svg panel', () => {
    const data = {
      ProcessInstances: [
        {
          callbackEndpoint:
            'http://localhost:8080/management/jobs/travels/instances/9865268c-64d7-3a44-8972-7325b295f7cc/timers/58180644-2fdf-4261-83f2-f4e783d308a3_0',
          executionCounter: 0,
          executionResponse: null,
          expirationTime: '2020-10-16T10:17:22.879Z',
          id: '58180644-2fdf-4261-83f2-f4e783d308a3_0',
          lastUpdate: '2020-10-07T07:41:31.467Z',
          priority: 0,
          processId: 'travels',
          processInstanceId: '9865268c-64d7-3a44-8972-7325b295f7cc',
          repeatInterval: null,
          repeatLimit: null,
          retries: 0,
          rootProcessId: null,
          rootProcessInstanceId: null,
          scheduledId: null,
          status: 'SCHEDULED'
        }
      ]
    };
    const setSvg = jest.fn();
    const setSvgError = jest.fn();
    it('handle api to get svg', async () => {
      mockedAxios.get.mockResolvedValue({
        data: '<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="800" height="300" viewBox="0 0 1748 632"></g></g></svg>',
        status: 200,
        statusText: 'OK'
      });
      await getSvg(data, setSvg, setSvgError);
      expect(setSvg).toHaveBeenCalled();
    });
    it('handle api to get svg', async () => {
      const errorResponse404 = {
        response: { status: 404 }
      };
      mockedAxios.get.mockRejectedValue(errorResponse404);
      await getSvg(data, setSvg, setSvgError);
      expect(setSvg).toHaveBeenCalledWith(null);
    });
    it('check api response when call to management console fails ', async () => {
      mockedAxios.get.mockImplementationOnce(() =>
        Promise.reject({
          error: mockedAxios.get.mockResolvedValue({
            data: '<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="800" height="300" viewBox="0 0 1748 632"></g></g></svg>',
            status: 200,
            statusText: 'OK'
          })
        })
      );
      await getSvg(data, setSvg, setSvgError);
      expect(setSvg).toHaveBeenCalled();
    });
    it('check api response when, call to both management console and runtimes fails ', async () => {
      mockedAxios.get.mockImplementationOnce(() =>
        Promise.reject({
          error: mockedAxios.get.mockRejectedValue({
            err: {
              response: { status: 500 }
            }
          })
        })
      );
      await getSvg(data, setSvg, setSvgError);
      expect(setSvg).toHaveBeenCalled();
    });
  });
  it('test format process instance for bulklist function', () => {
    const testProcessInstance = [
      {
        id: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
        processId: 'travels',
        businessKey: 'T1234',
        parentProcessInstanceId: null,
        parentProcessInstance: null,
        processName: 'travels',
        roles: [],
        state: GraphQL.ProcessInstanceState.Active,
        rootProcessInstanceId: null,
        addons: [
          'jobs-management',
          'prometheus-monitoring',
          'process-management'
        ],
        start: '2019-10-22T03:40:44.089Z',
        lastUpdate: '2019-10-22T03:40:44.089Z',
        end: '2019-10-22T05:40:44.089Z',
        serviceUrl: 'http://localhost:4000',
        endpoint: 'http://localhost:4000',
        nodes: [],
        milestones: [],
        childProcessInstances: [],
        errorMessage: '404 error'
      }
    ];
    const testResultWithError =
      formatForBulkListProcessInstance(testProcessInstance);
    expect(testResultWithError).toEqual([
      {
        id: testProcessInstance[0].id,
        name: testProcessInstance[0].processName,
        description: testProcessInstance[0].businessKey,
        errorMessage: testProcessInstance[0].errorMessage
      }
    ]);
    const testResultWithoutError = formatForBulkListProcessInstance([
      { ...testProcessInstance[0], errorMessage: null }
    ]);
    expect(testResultWithoutError).toEqual([
      {
        id: testProcessInstance[0].id,
        name: testProcessInstance[0].processName,
        description: testProcessInstance[0].businessKey,
        errorMessage: null
      }
    ]);
  });

  it('test format job for bulklist function', () => {
    const testJob = [
      {
        id: 'dad3aa88-5c1e-4858-a919-uey23c675a0fa_0',
        processId: 'travels',
        processInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
        rootProcessId: '',
        status: GraphQL.JobStatus.Scheduled,
        priority: 0,
        callbackEndpoint:
          'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
        repeatInterval: null,
        repeatLimit: null,
        scheduledId: null,
        retries: 5,
        lastUpdate: '2020-08-27T03:35:54.635Z',
        expirationTime: '2020-08-27T04:35:54.631Z',
        endpoint: 'http://localhost:4000/jobs',
        nodeInstanceId: '08c153e8-2766-4675-81f7-29943efdf411',
        executionCounter: 1,
        errorMessage: '403 error'
      }
    ];
    const testResultWithError = formatForBulkListJob(testJob);
    expect(testResultWithError).toEqual([
      {
        id: testJob[0].id,
        name: testJob[0].processId,
        description: testJob[0].id,
        errorMessage: testJob[0].errorMessage
      }
    ]);
    const testResultWithoutError = formatForBulkListJob([
      { ...testJob[0], errorMessage: null }
    ]);
    expect(testResultWithoutError).toEqual([
      {
        id: testJob[0].id,
        name: testJob[0].processId,
        description: testJob[0].id,
        errorMessage: null
      }
    ]);
  });
  it('test checkProcessInstanceState method', () => {
    const testProcessInstance1 = {
      state: GraphQL.ProcessInstanceState.Active,
      addons: ['process-management'],
      serviceUrl: 'http://localhost:4000'
    };
    const testProcessInstance2 = {
      state: GraphQL.ProcessInstanceState.Aborted,
      addons: [],
      serviceUrl: null
    };
    const falseResult = checkProcessInstanceState(testProcessInstance1);
    const trueResult = checkProcessInstanceState(testProcessInstance2);
    expect(falseResult).toBeFalsy();
    expect(trueResult).toBeTruthy();
  });

  it('test alterOrderByObj method', () => {
    const orderById = { id: GraphQL.OrderBy.Desc };
    const orderByStatus = { status: GraphQL.OrderBy.Desc };
    const orderByCreated = { created: GraphQL.OrderBy.Desc };
    expect(alterOrderByObj(orderById)).toEqual({
      processName: GraphQL.OrderBy.Desc
    });
    expect(alterOrderByObj(orderByStatus)).toEqual({
      state: GraphQL.OrderBy.Desc
    });
    expect(alterOrderByObj(orderByCreated)).toEqual({
      start: GraphQL.OrderBy.Desc
    });
  });
});
