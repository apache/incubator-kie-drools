/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
