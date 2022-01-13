import { renderHook } from '@testing-library/react-hooks';
import { act } from 'react-test-renderer';
import useInputData from '../useInputData';
import * as api from '../../../../utils/api/httpClient';
import { ItemObject, RemoteDataStatus } from '../../../../types';
import { AxiosPromise } from 'axios';
import { TrustyContext } from '../../TrustyApp/TrustyApp';
import React from 'react';

const flushPromises = () => new Promise(setImmediate);
const apiMock = jest.spyOn(api, 'httpClient');

const contextWrapper = ({ children }) => (
  <TrustyContext.Provider
    value={{
      config: {
        counterfactualEnabled: false,
        useHrefLinks: false,
        explanationEnabled: false,
        serverRoot: 'http://url-to-service',
        basePath: '/'
      }
    }}
  >
    {children}
  </TrustyContext.Provider>
);

beforeEach(() => {
  apiMock.mockClear();
});

describe('useInputData', () => {
  test('returns the inputs of a specific decision', async () => {
    const inputData = {
      data: {
        inputs: [
          {
            name: 'Asset Score',
            value: {
              kind: 'UNIT',
              type: 'number',
              value: 738
            }
          },
          {
            name: 'Asset Amount',
            value: {
              kind: 'UNIT',
              type: 'number',
              value: 70000
            }
          }
        ] as ItemObject[]
      }
    };

    apiMock.mockImplementation(
      () => Promise.resolve(inputData) as AxiosPromise
    );

    const { result } = renderHook(
      () => {
        // tslint:disable-next-line:react-hooks-nesting
        return useInputData('b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000');
      },
      { wrapper: contextWrapper }
    );

    expect(result.current).toStrictEqual({ status: RemoteDataStatus.LOADING });

    await act(async () => {
      await flushPromises();
    });

    expect(result.current).toStrictEqual(
      Object.assign(
        { status: RemoteDataStatus.SUCCESS },
        { data: inputData.data.inputs }
      )
    );
    expect(apiMock).toHaveBeenCalledTimes(1);
  });
});
