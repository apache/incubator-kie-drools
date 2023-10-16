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
import { act } from 'react-test-renderer';
import * as api from '../httpClient';
import useAPI from '../useAPI';
import { RemoteDataStatus } from '../../../types';
import ReactRouterDom from 'react-router-dom';
import { TrustyContext } from '../../../components/Templates/TrustyApp/TrustyApp';
import React from 'react';

const flushPromises = () => new Promise(setImmediate);
const apiMock = jest.spyOn(api, 'httpClient');
const historyMock = {
  push: jest.fn(),
  replace: jest.fn()
};

jest.mock('react-router-dom', () => ({
  ...(jest.requireActual('react-router-dom') as typeof ReactRouterDom),
  useHistory: jest.fn(() => historyMock)
}));

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

describe('useAPI', () => {
  test('redirects to the error page when a request fails', async () => {
    let apiMockConfig = {};
    apiMock.mockImplementation((config) => {
      apiMockConfig = config;
      return Promise.reject('error');
    });

    const { result } = renderHook(
      () => {
        return useAPI('url', 'get');
      },
      { wrapper: contextWrapper }
    );

    expect(result.current).toStrictEqual({ status: RemoteDataStatus.LOADING });

    await act(async () => {
      await flushPromises();
    });

    expect(result.current).toStrictEqual({
      status: RemoteDataStatus.FAILURE,
      error: 'error'
    });
    expect(apiMock).toHaveBeenCalledTimes(1);
    expect(apiMockConfig['baseURL']).toEqual('http://url-to-service');
    expect(historyMock.replace).toHaveBeenCalledTimes(1);
    expect(historyMock.replace).toBeCalledWith('/error');
  });
});
