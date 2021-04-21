import { renderHook } from '@testing-library/react-hooks';
import { act } from 'react-test-renderer';
import * as api from '../../../../utils/api/httpClient';
import useExecutionInfo from '../useExecutionInfo';
import { Execution, RemoteDataStatus } from '../../../../types';

const flushPromises = () => new Promise(setImmediate);
const apiMock = jest.spyOn(api, 'httpClient');

beforeEach(() => {
  apiMock.mockClear();
});

describe('useExecutionInfo', () => {
  test('retrieves general information about an execution', async () => {
    const execution = {
      data: {
        executionId: 'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000',
        executionDate: '2020-08-12T12:54:53.933Z',
        executionType: 'DECISION',
        executedModelName: 'fraud-score',
        executionSucceeded: true,
        executorName: 'Technical User'
      } as Execution
    };

    // @ts-ignore
    apiMock.mockImplementation(() => Promise.resolve(execution));

    const { result } = renderHook(() => {
      // tslint:disable-next-line:react-hooks-nesting
      return useExecutionInfo('b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000');
    });

    expect(result.current).toStrictEqual({ status: RemoteDataStatus.LOADING });

    await act(async () => {
      await flushPromises();
    });

    expect(result.current).toStrictEqual(
      Object.assign({ status: RemoteDataStatus.SUCCESS }, execution)
    );
    expect(apiMock).toHaveBeenCalledTimes(1);
  });
});
