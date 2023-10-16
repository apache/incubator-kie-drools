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
import useExecutions from '../useExecutions';
import * as api from '../../../../utils/api/httpClient';
import { act } from 'react-test-renderer';
import { RemoteDataStatus } from '../../../../types';
import { AxiosPromise } from 'axios';
import { TrustyContext } from '../../TrustyApp/TrustyApp';
import React from 'react';

const flushPromises = () => new Promise(setImmediate);
const apiMock = jest.spyOn(api, 'callOnceHandler');

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

describe('useExecutions', () => {
  it('returns a list of executions retrieved from APIs', async () => {
    const executionsResponse = {
      data: {
        total: 28,
        limit: 50,
        offset: 0,
        headers: [
          {
            executionId: 'b2b0ed8d-c1e2-46b5-ad4f-3ac54ff4beae',
            executionDate: '2020-06-01T12:33:57+0000',
            executionSucceeded: true,
            executorName: 'testUser',
            executedModelName: 'LoanEligibility',
            executionType: 'DECISION'
          },
          {
            executionId: '023a0d79-2be6-4ec8-9ef7-99a6796cb319',
            executionDate: '2020-06-01T12:33:57+0000',
            executionSucceeded: true,
            executorName: 'testUser',
            executedModelName: 'LoanEligibility',
            executionType: 'DECISION'
          },
          {
            executionId: '3a5d4a4e-7c5a-4ce7-85de-6024fbf1da39',
            executionDate: '2020-06-01T12:33:56+0000',
            executionSucceeded: true,
            executorName: 'testUser',
            executedModelName: 'LoanEligibility',
            executionType: 'DECISION'
          },
          {
            executionId: 'a4e0b8e8-9a6d-4a8e-ad5a-54e5c654a248',
            executionDate: '2020-06-01T12:33:23+0000',
            executionSucceeded: true,
            executorName: 'testUser',
            executedModelName: 'fraud-scoring',
            executionType: 'DECISION'
          },
          {
            executionId: 'f08adc80-2c2d-43f4-801c-4f08e10820a0',
            executionDate: '2020-06-01T12:33:18+0000',
            executionSucceeded: true,
            executorName: 'testUser',
            executedModelName: 'fraud-scoring',
            executionType: 'DECISION'
          }
        ]
      }
    };

    let apiMockConfig = {};

    apiMock.mockImplementation(() => (config) => {
      apiMockConfig = config;
      return Promise.resolve(executionsResponse) as AxiosPromise;
    });

    const { result } = renderHook(
      () => {
        return useExecutions({
          searchString: '',
          from: '',
          to: '',
          limit: 10,
          offset: 0
        });
      },
      { wrapper: contextWrapper }
    );
    expect(result.current.executions).toStrictEqual({
      status: RemoteDataStatus.LOADING
    });

    await act(async () => {
      await flushPromises();
    });

    expect(result.current.executions).toStrictEqual(
      Object.assign({ status: RemoteDataStatus.SUCCESS }, executionsResponse)
    );
    expect(apiMock).toHaveBeenCalledTimes(1);
    expect(apiMockConfig['baseURL']).toEqual('http://url-to-service');

    act(() => {
      result.current.loadExecutions();
    });

    expect(result.current.executions).toStrictEqual({
      status: RemoteDataStatus.LOADING
    });

    await act(async () => {
      await flushPromises();
    });

    expect(result.current.executions).toStrictEqual(
      Object.assign({ status: RemoteDataStatus.SUCCESS }, executionsResponse)
    );
    expect(apiMock).toHaveBeenCalledTimes(1);
  });

  it('returns a loading error when APIs call fails', async () => {
    let apiMockConfig = {};

    apiMock.mockImplementation(() => (config) => {
      apiMockConfig = config;
      return Promise.reject('error');
    });

    const { result } = renderHook(
      () => {
        return useExecutions({
          searchString: '',
          from: '',
          to: '',
          limit: 10,
          offset: 0
        });
      },
      { wrapper: contextWrapper }
    );
    expect(result.current.executions).toStrictEqual({
      status: RemoteDataStatus.LOADING
    });

    await act(async () => {
      await flushPromises();
    });

    expect(result.current.executions).toStrictEqual(
      Object.assign({ error: 'error', status: RemoteDataStatus.FAILURE })
    );
    expect(apiMock).toHaveBeenCalledTimes(1);
    expect(apiMockConfig['baseURL']).toEqual('http://url-to-service');
  });
});
