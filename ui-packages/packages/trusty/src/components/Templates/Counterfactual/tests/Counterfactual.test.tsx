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
import React from 'react';
import { mount } from 'enzyme';
import {
  CFSupportMessage,
  ItemObject,
  Outcome,
  RemoteData,
  RemoteDataStatus
} from '../../../../types';
import { MemoryRouter } from 'react-router';
import useInputData from '../../InputData/useInputData';
import useDecisionOutcomes from '../../AuditDetail/useDecisionOutcomes';
import Counterfactual from '../Counterfactual';
import { TrustyContext } from '../../TrustyApp/TrustyApp';

const executionId = 'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000';

jest.mock('../../InputData/useInputData');
jest.mock('../../AuditDetail/useDecisionOutcomes');
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useParams: () => ({
    executionId
  })
}));

describe('Counterfactual', () => {
  test('renders loading animation while fetching data', () => {
    const loadingOutcomes = {
      status: RemoteDataStatus.LOADING
    } as RemoteData<Error, Outcome[]>;
    const loadingInputs = {
      status: RemoteDataStatus.LOADING
    } as RemoteData<Error, ItemObject[]>;

    (useDecisionOutcomes as jest.Mock).mockReturnValue(loadingOutcomes);
    (useInputData as jest.Mock).mockReturnValue(loadingInputs);

    const wrapper = mount(
      <MemoryRouter
        initialEntries={[
          {
            pathname: `/audit/decision/${executionId}/counterfactual-analysis`,
            key: 'counterfactual-analysis'
          }
        ]}
      >
        <Counterfactual />
      </MemoryRouter>
    );

    expect(useDecisionOutcomes).toHaveBeenCalledWith(executionId);
    expect(useInputData).toHaveBeenCalledWith(executionId);

    expect(wrapper.find('Title').text()).toMatch('Counterfactual analysis');
    expect(wrapper.find('CounterfactualAnalysis')).toHaveLength(0);

    expect(
      wrapper.find('.counterfactual__wrapper SkeletonFlexStripes')
    ).toHaveLength(1);
    expect(
      wrapper.find('.counterfactual__wrapper SkeletonDataList')
    ).toHaveLength(1);
  });

  test('renders the counterfactual analysis component', () => {
    const outcomesData = {
      status: RemoteDataStatus.SUCCESS,
      data: [
        {
          outcomeId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
          outcomeName: 'Mortgage Approval',
          evaluationStatus: 'SUCCEEDED',
          outcomeResult: {
            kind: 'UNIT',
            type: 'boolean',
            value: true
          },
          messages: [],
          hasErrors: false
        }
      ] as Outcome[]
    };
    const inputData = {
      status: RemoteDataStatus.SUCCESS,
      data: [
        {
          name: 'Asset Score',
          value: {
            kind: 'UNIT',
            type: 'number',
            value: 738
          }
        }
      ] as ItemObject[]
    };

    (useDecisionOutcomes as jest.Mock).mockReturnValue(outcomesData);
    (useInputData as jest.Mock).mockReturnValue(inputData);

    const wrapper = mount(
      <MemoryRouter
        initialEntries={[
          {
            pathname: `/audit/decision/${executionId}/counterfactual-analysis`,
            key: 'counterfactual-analysis'
          }
        ]}
      >
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
          <Counterfactual />
        </TrustyContext.Provider>
      </MemoryRouter>
    );

    expect(
      wrapper.find('.counterfactual__wrapper SkeletonFlexStripes')
    ).toHaveLength(0);
    expect(wrapper.find('CounterfactualAnalysis')).toHaveLength(1);
    expect(
      wrapper.find('CounterfactualAnalysis').props()['inputs']
    ).toStrictEqual(inputData.data);
    expect(
      wrapper.find('CounterfactualAnalysis').props()['outcomes']
    ).toStrictEqual(outcomesData.data);
    expect(
      wrapper.find('CounterfactualAnalysis').props()['executionId']
    ).toStrictEqual(executionId);
  });

  test('renders the counterfactual unsupported component::Unsupported input', () => {
    const outcomesData = {
      status: RemoteDataStatus.SUCCESS,
      data: [
        {
          outcomeId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
          outcomeName: 'Mortgage Approval',
          evaluationStatus: 'SUCCEEDED',
          outcomeResult: {
            kind: 'UNIT',
            type: 'boolean',
            value: true
          },
          messages: [],
          hasErrors: false
        }
      ] as Outcome[]
    };
    const inputData = {
      status: RemoteDataStatus.SUCCESS,
      data: [
        {
          name: 'Asset Score',
          value: {
            type: 'number',
            kind: 'UNIT',
            value: 123
          }
        },
        {
          name: 'Asset Type',
          value: {
            type: 'tAssetType',
            kind: 'STRUCTURE',
            value: {
              category: {
                kind: 'UNIT',
                type: 'string',
                value: 'property'
              }
            }
          }
        }
      ] as ItemObject[]
    };

    (useDecisionOutcomes as jest.Mock).mockReturnValue(outcomesData);
    (useInputData as jest.Mock).mockReturnValue(inputData);

    const wrapper = mount(
      <MemoryRouter
        initialEntries={[
          {
            pathname: `/audit/decision/${executionId}/counterfactual-analysis`,
            key: 'counterfactual-analysis'
          }
        ]}
      >
        <Counterfactual />
      </MemoryRouter>
    );

    expect(wrapper.find('CounterfactualUnsupported')).toHaveLength(1);
    const messages = wrapper.find('CounterfactualUnsupported').props()[
      'messages'
    ];
    expect(messages.length).toEqual(1);
    expect((messages[0] as CFSupportMessage).id).toEqual('message-inputs');
  });

  test('renders the counterfactual unsupported component::Only String inputs', () => {
    const outcomesData = {
      status: RemoteDataStatus.SUCCESS,
      data: [
        {
          outcomeId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
          outcomeName: 'Mortgage Approval',
          evaluationStatus: 'SUCCEEDED',
          outcomeResult: {
            kind: 'UNIT',
            type: 'boolean',
            value: true
          },
          messages: [],
          hasErrors: false
        }
      ] as Outcome[]
    };
    const inputData = {
      status: RemoteDataStatus.SUCCESS,
      data: [
        {
          name: 'Asset name',
          value: {
            type: 'string',
            kind: 'UNIT',
            value: 'Cheese'
          }
        },
        {
          name: 'Asset nickname',
          value: {
            type: 'string',
            kind: 'UNIT',
            value: 'Charlie Cheddar'
          }
        }
      ] as ItemObject[]
    };

    (useDecisionOutcomes as jest.Mock).mockReturnValue(outcomesData);
    (useInputData as jest.Mock).mockReturnValue(inputData);

    const wrapper = mount(
      <MemoryRouter
        initialEntries={[
          {
            pathname: `/audit/decision/${executionId}/counterfactual-analysis`,
            key: 'counterfactual-analysis'
          }
        ]}
      >
        <Counterfactual />
      </MemoryRouter>
    );

    expect(wrapper.find('CounterfactualUnsupported')).toHaveLength(1);
    const messages = wrapper.find('CounterfactualUnsupported').props()[
      'messages'
    ];
    expect(messages.length).toEqual(1);
    expect((messages[0] as CFSupportMessage).id).toEqual(
      'message-inputs-string'
    );
  });

  test('renders the counterfactual unsupported component::Unsupported outcome', () => {
    const outcomesData = {
      status: RemoteDataStatus.SUCCESS,
      data: [
        {
          outcomeId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
          outcomeName: 'Mortgage Approval',
          evaluationStatus: 'SUCCEEDED',
          outcomeResult: {
            kind: 'STRUCTURE',
            type: 'tMortgage',
            value: {
              approved: {
                kind: 'UNIT',
                type: 'boolean',
                value: true
              }
            }
          },
          messages: [],
          hasErrors: false
        }
      ] as Outcome[]
    };
    const inputData = {
      status: RemoteDataStatus.SUCCESS,
      data: [] as ItemObject[]
    };

    (useDecisionOutcomes as jest.Mock).mockReturnValue(outcomesData);
    (useInputData as jest.Mock).mockReturnValue(inputData);

    const wrapper = mount(
      <MemoryRouter
        initialEntries={[
          {
            pathname: `/audit/decision/${executionId}/counterfactual-analysis`,
            key: 'counterfactual-analysis'
          }
        ]}
      >
        <Counterfactual />
      </MemoryRouter>
    );

    expect(wrapper.find('CounterfactualUnsupported')).toHaveLength(1);
    const messages = wrapper.find('CounterfactualUnsupported').props()[
      'messages'
    ];
    expect(messages.length).toEqual(1);
    expect((messages[0] as CFSupportMessage).id).toEqual('message-outcomes');
  });

  test('renders the counterfactual unsupported component::Unsupported input and outcome', () => {
    const outcomesData = {
      status: RemoteDataStatus.SUCCESS,
      data: [
        {
          outcomeId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
          outcomeName: 'Mortgage Approval',
          evaluationStatus: 'SUCCEEDED',
          outcomeResult: {
            kind: 'STRUCTURE',
            type: 'tMortgage',
            value: {
              approved: {
                kind: 'UNIT',
                type: 'boolean',
                value: true
              }
            }
          },
          messages: [],
          hasErrors: false
        }
      ] as Outcome[]
    };
    const inputData = {
      status: RemoteDataStatus.SUCCESS,
      data: [
        {
          name: 'Asset Score',
          value: {
            type: 'number',
            kind: 'UNIT',
            value: 123
          }
        },
        {
          name: 'Asset Type',
          value: {
            type: 'tAssetType',
            kind: 'STRUCTURE',
            value: {
              category: {
                kind: 'UNIT',
                type: 'string',
                value: 'property'
              }
            }
          }
        }
      ] as ItemObject[]
    };

    (useDecisionOutcomes as jest.Mock).mockReturnValue(outcomesData);
    (useInputData as jest.Mock).mockReturnValue(inputData);

    const wrapper = mount(
      <MemoryRouter
        initialEntries={[
          {
            pathname: `/audit/decision/${executionId}/counterfactual-analysis`,
            key: 'counterfactual-analysis'
          }
        ]}
      >
        <Counterfactual />
      </MemoryRouter>
    );

    expect(wrapper.find('CounterfactualUnsupported')).toHaveLength(1);
    const messages = wrapper.find('CounterfactualUnsupported').props()[
      'messages'
    ];
    expect(messages.length).toEqual(1);
    expect((messages[0] as CFSupportMessage).id).toEqual(
      'message-inputs-and-outcomes'
    );
  });
});
