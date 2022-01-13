import { renderHook } from '@testing-library/react-hooks';
import useSaliencies from '../useSaliencies';
import * as api from '../../../../utils/api/httpClient';
import {
  RemoteDataStatus,
  Saliencies,
  SaliencyStatus
} from '../../../../types';
import { act } from 'react-test-renderer';
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

describe('useSaliencies', () => {
  test('retrieves explanation info of an execution', async () => {
    const saliencies = {
      data: {
        status: SaliencyStatus.SUCCEEDED,
        saliencies: [
          {
            outcomeId: '12345',
            featureImportance: [
              {
                featureName: 'Liabilities',
                featureScore: 0.6780527129423648
              },
              {
                featureName: 'Lender Ratings',
                featureScore: -0.08937896629080377
              }
            ]
          }
        ]
      } as Saliencies
    };

    apiMock.mockImplementation(
      () => Promise.resolve(saliencies) as AxiosPromise
    );

    const { result } = renderHook(
      () => {
        // tslint:disable-next-line:react-hooks-nesting
        return useSaliencies('b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000');
      },
      { wrapper: contextWrapper }
    );

    expect(result.current).toStrictEqual({ status: RemoteDataStatus.LOADING });

    await act(async () => {
      await flushPromises();
    });

    expect(result.current).toStrictEqual({
      status: RemoteDataStatus.SUCCESS,
      data: saliencies.data
    });
    expect(apiMock).toHaveBeenCalledTimes(1);
  });
});
