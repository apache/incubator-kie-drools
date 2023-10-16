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
import * as api from '../../../../utils/api/httpClient';
import useCounterfactualExecution from '../useCounterfactualExecution';
import {
  CFAnalysisResult,
  CFGoal,
  CFSearchInput,
  RemoteDataStatus
} from '../../../../types';
import { AxiosPromise } from 'axios';
import { TrustyContext } from '../../../Templates/TrustyApp/TrustyApp';
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

describe('useCounterfactualExecution', () => {
  it('starts a new CF analysis and retrieves results', async () => {
    jest.useFakeTimers();
    const CFAnalysisResponse = {
      data: {
        executionId: '7ffd3240-2ad4-4999-b67c-9437efcd449a',
        counterfactualId: 'bb3bd63b-aaaf-46c0-9de8-88a3b405d596'
      }
    };
    const CFResultsOne = {
      data: {
        ...CFAnalysisResponse.data,
        goals: [
          {
            kind: 'UNIT',
            name: 'canRequestLoan',
            type: 'boolean',
            components: null,
            value: true
          }
        ],
        searchDomains: [
          {
            fixed: false,
            kind: 'UNIT',
            name: 'monthlySalary',
            type: 'number',
            components: null,
            domain: {
              type: 'RANGE',
              lowerBound: 60,
              upperBound: 6000
            }
          }
        ],
        solutions: []
      }
    };
    const CFResultsTwo = {
      data: {
        ...CFResultsOne.data,
        solutions: [
          {
            type: 'counterfactual',
            valid: true,
            executionId: '7ffd3240-2ad4-4999-b67c-9437efcd449a',
            status: 'SUCCEEDED',
            statusDetails: null,
            counterfactualId: 'bb3bd63b-aaaf-46c0-9de8-88a3b405d596',
            solutionId: 'b39779a0-fd73-42ac-8562-6976ec26273b',
            sequenceId: 3,
            isValid: true,
            stage: 'INTERMEDIATE',
            inputs: [
              {
                name: 'monthlySalary',
                value: {
                  kind: 'UNIT',
                  type: 'Double',
                  value: 2428.5761968979696
                }
              }
            ],
            outputs: [
              {
                name: 'canRequestLoan',
                value: {
                  kind: 'UNIT',
                  type: 'Boolean',
                  value: true
                }
              }
            ]
          }
        ] as CFAnalysisResult[]
      }
    };
    const CFResultsThree = {
      data: {
        ...CFResultsOne.data,
        solutions: [
          ...CFResultsTwo.data.solutions,
          {
            ...CFResultsTwo.data.solutions[0],
            stage: 'FINAL'
          }
        ]
      }
    };

    apiMock
      .mockImplementationOnce(
        () => Promise.resolve(CFAnalysisResponse) as AxiosPromise
      )
      .mockImplementationOnce(
        () => Promise.resolve(CFResultsOne) as AxiosPromise
      )
      .mockImplementationOnce(
        () => Promise.resolve(CFResultsTwo) as AxiosPromise
      )
      .mockImplementationOnce(
        () => Promise.resolve(CFResultsThree) as AxiosPromise
      );

    const { result } = renderHook(
      () => {
        return useCounterfactualExecution(
          '7ffd3240-2ad4-4999-b67c-9437efcd449a'
        );
      },
      { wrapper: contextWrapper }
    );

    expect(result.current.cfAnalysis).toStrictEqual({
      status: RemoteDataStatus.NOT_ASKED
    });

    act(() => {
      result.current.runCFAnalysis({
        goals: [
          {
            id: '_46B5CA54-27CA-4950-B601-63F58BC3BDFE',
            role: 2,
            name: 'canRequestLoan',
            value: {
              kind: 'UNIT',
              type: 'boolean',
              value: true
            },
            originalValue: {
              kind: 'UNIT',
              type: 'boolean',
              value: false
            }
          }
        ] as CFGoal[],
        searchDomains: [
          {
            name: 'monthlySalary',
            value: {
              kind: 'UNIT',
              type: 'number',
              fixed: false,
              domain: {
                type: 'RANGE',
                lowerBound: 60,
                upperBound: 6000
              }
            }
          }
        ] as CFSearchInput[]
      });
    });

    await act(async () => {
      await flushPromises();
    });

    expect(result.current.cfAnalysis).toStrictEqual({
      status: RemoteDataStatus.SUCCESS,
      data: {
        counterfactualId: 'bb3bd63b-aaaf-46c0-9de8-88a3b405d596',
        executionId: '7ffd3240-2ad4-4999-b67c-9437efcd449a'
      }
    });

    expect(result.current.cfResults).toBeUndefined();

    expect(setInterval).toHaveBeenCalledTimes(1);
    expect(setInterval).toHaveBeenLastCalledWith(expect.any(Function), 3000);

    expect(apiMock).toHaveBeenCalledTimes(1);

    await act(async () => {
      jest.advanceTimersByTime(1000 * 3);
    });

    expect(apiMock).toHaveBeenCalledTimes(2);

    expect(result.current.cfResults).toStrictEqual(CFResultsOne.data);

    await act(async () => {
      jest.advanceTimersByTime(1000 * 3);
    });

    expect(apiMock).toHaveBeenCalledTimes(3);

    expect(result.current.cfResults).toStrictEqual(CFResultsTwo.data);

    await act(async () => {
      jest.advanceTimersByTime(1000 * 3);
    });

    expect(apiMock).toHaveBeenCalledTimes(4);
    expect(result.current.cfResults).toStrictEqual(CFResultsThree.data);

    await act(async () => {
      jest.advanceTimersByTime(1000 * 3);
    });

    expect(apiMock).toHaveBeenCalledTimes(4);
    expect(result.current.cfResults).toStrictEqual(CFResultsThree.data);

    jest.useRealTimers();
  });
});
