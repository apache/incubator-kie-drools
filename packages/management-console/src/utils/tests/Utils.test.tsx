import {
  stateIconCreator,
  setTitle,
  handleSkip,
  handleRetry,
  handleAbort,
  isModalOpen,
  modalToggle
} from '../Utils';
import { GraphQL } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;
import axios from 'axios';
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

  it('isModalOpen tests', () => {
    const skipResult = isModalOpen('Skip operation', true, false);
    expect(skipResult).toBeTruthy();
    const retryResult = isModalOpen('Retry operation', false, true);
    expect(retryResult).toBeTruthy();
  });

  it('modalToggle tests', () => {
    const handleSkipModalToggle = jest.fn();
    const handleRetryModalToggle = jest.fn();
    const skipResult = modalToggle(
      'Skip operation',
      handleSkipModalToggle,
      handleRetryModalToggle
    );
    expect(skipResult).toEqual(handleSkipModalToggle);
    const retryResult = modalToggle(
      'Retry operation',
      handleSkipModalToggle,
      handleRetryModalToggle
    );
    expect(retryResult).toEqual(handleRetryModalToggle);
  });
  describe('handle skip tests', () => {
    const processInstanceData = {
      id: '123',
      processId: 'trav',
      serviceUrl: 'http://localhost:4000',
      state: ProcessInstanceState.Active
    };
    const setModalTitle = jest.fn();
    const setTitleType = jest.fn();
    const setModalContent = jest.fn();
    const handleSkipModalToggle = jest.fn();
    it('executes skip process successfully', () => {
      mockedAxios.post.mockResolvedValue({});
      handleSkip(
        processInstanceData,
        setModalTitle,
        setTitleType,
        setModalContent,
        handleSkipModalToggle
      );
    });
    it('fails executing skip process', () => {
      mockedAxios.post.mockRejectedValue({ error: { message: '403 error' } });
      handleSkip(
        processInstanceData,
        setModalTitle,
        setTitleType,
        setModalContent,
        handleSkipModalToggle
      );
    });
  });

  describe('handle Retry tests', () => {
    const processInstanceData = {
      id: '123',
      processId: 'trav',
      serviceUrl: 'http://localhost:4000',
      state: ProcessInstanceState.Active
    };
    const setModalTitle = jest.fn();
    const setTitleType = jest.fn();
    const setModalContent = jest.fn();
    const handleRetryModalToggle = jest.fn();
    it('executes skip process successfully', () => {
      mockedAxios.post.mockResolvedValue({});
      handleRetry(
        processInstanceData,
        setModalTitle,
        setTitleType,
        setModalContent,
        handleRetryModalToggle
      );
    });
    it('fails executing Retry process', () => {
      mockedAxios.post.mockRejectedValue({ error: { message: '403 error' } });
      handleRetry(
        processInstanceData,
        setModalTitle,
        setTitleType,
        setModalContent,
        handleRetryModalToggle
      );
    });
  });

  describe('handle Abort tests', () => {
    const processInstanceData = {
      id: '123',
      processId: 'trav',
      serviceUrl: 'http://localhost:4000',
      state: ProcessInstanceState.Active
    };
    const setModalTitle = jest.fn();
    const setTitleType = jest.fn();
    const setModalContent = jest.fn();
    const handleAbortModalToggle = jest.fn();
    it('executes Abort process successfully', () => {
      mockedAxios.delete.mockResolvedValue({});
      handleAbort(
        processInstanceData,
        setModalTitle,
        setTitleType,
        setModalContent,
        handleAbortModalToggle
      );
    });
    it('fails executing Abort process', () => {
      mockedAxios.delete.mockRejectedValue({});
      handleAbort(
        processInstanceData,
        setModalTitle,
        setTitleType,
        setModalContent,
        handleAbortModalToggle
      );
    });
  });
});
