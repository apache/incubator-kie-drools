import {
  stateIconCreator,
  setTitle,
  handleSkip,
  handleRetry,
  handleAbort,
  handleNodeInstanceRetrigger
} from '../Utils';
import { GraphQL } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;
import axios from 'axios';
import wait from 'waait';
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;
const children = 'children';
describe('uitility function testing', () => {
  it('state icon creator tests', () => {
    const activeTestResult = stateIconCreator(ProcessInstanceState.Active);
    const completedTestResult = stateIconCreator(
      ProcessInstanceState.Completed
    );
    const errorTestResult = stateIconCreator(ProcessInstanceState.Error);
    const suspendedTestResult = stateIconCreator(
      ProcessInstanceState.Suspended
    );
    const abortedTestResult = stateIconCreator(ProcessInstanceState.Aborted);
    expect(activeTestResult.props[children][1]).toEqual('Active');
    expect(completedTestResult.props[children][1]).toEqual('Completed');
    expect(errorTestResult.props[children][1]).toEqual('Error');
    expect(suspendedTestResult.props[children][1]).toEqual('Suspended');
    expect(abortedTestResult.props[children][1]).toEqual('Aborted');
  });

  it('set title tests', () => {
    const successResult = setTitle('success', 'Abort operation');
    const failureResult = setTitle('failure', 'Skip operation');
    expect(successResult.props[children][2]).toEqual('Abort operation');
    expect(failureResult.props[children][2]).toEqual('Skip operation');
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
      handleSkip(processInstanceData, onSkipSuccess, onSkipFailure);
      await wait(0);
      expect(onSkipSuccess).toHaveBeenCalled();
    });
    it('fails executing skip process', async () => {
      const onSkipSuccess = jest.fn();
      const onSkipFailure = jest.fn();
      mockedAxios.post.mockRejectedValue({ message: '403 error' });
      handleSkip(processInstanceData, onSkipSuccess, onSkipFailure);
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
      handleRetry(processInstanceData, onRetrySuccess, onRetryFailure);
      await wait(0);
      expect(onRetrySuccess).toHaveBeenCalled();
    });
    it('fails executing Retry process', async () => {
      const onRetrySuccess = jest.fn();
      const onRetryFailure = jest.fn();
      mockedAxios.post.mockRejectedValue({ message: '403 error' });
      handleRetry(processInstanceData, onRetrySuccess, onRetryFailure);
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
      const setModalTitle = jest.fn();
      const setTitleType = jest.fn();
      const handleAbortModalToggle = jest.fn();
      mockedAxios.delete.mockResolvedValue({});
      handleAbort(
        processInstanceData,
        setModalTitle,
        setTitleType,
        handleAbortModalToggle
      );
      await wait(0);
      expect(setModalTitle.mock.calls[0][0]).toEqual('Abort operation');
      expect(setTitleType.mock.calls[0][0]).toEqual('success');
      expect(handleAbortModalToggle).toHaveBeenCalled();
    });
    it('fails executing Abort process', async () => {
      const setModalTitle = jest.fn();
      const setTitleType = jest.fn();
      const handleAbortModalToggle = jest.fn();
      mockedAxios.delete.mockRejectedValue({});
      handleAbort(
        processInstanceData,
        setModalTitle,
        setTitleType,
        handleAbortModalToggle
      );
      await wait(0);
      expect(setModalTitle.mock.calls[0][0]).toEqual('Abort operation');
      expect(setTitleType.mock.calls[0][0]).toEqual('failure');
      expect(handleAbortModalToggle).toHaveBeenCalled();
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
});
