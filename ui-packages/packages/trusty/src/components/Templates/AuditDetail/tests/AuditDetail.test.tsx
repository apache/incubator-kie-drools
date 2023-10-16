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
import * as React from 'react';
import { mount } from 'enzyme';
import AuditDetail from '../AuditDetail';
import useExecutionInfo from '../useExecutionInfo';
import useDecisionOutcomes from '../useDecisionOutcomes';
import {
  Execution,
  Outcome,
  RemoteData,
  RemoteDataStatus
} from '../../../../types';
import { MemoryRouter } from 'react-router';
import { TrustyContext } from '../../TrustyApp/TrustyApp';

jest.mock('../useExecutionInfo');
jest.mock('../useDecisionOutcomes');
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useParams: () => ({
    executionId: 'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000'
  }),
  useRouteMatch: () => ({
    path: '/audit/decision/b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000',
    url: '/audit/:executionType/:executionId'
  })
}));

const setupWrapper = (
  outcomes: RemoteData<Error, Outcome[]>,
  counterfactualEnabled: boolean,
  explanationEnabled: boolean
) => {
  return mount(
    <TrustyContext.Provider
      value={{ config: { counterfactualEnabled, explanationEnabled } }}
    >
      <MemoryRouter
        initialEntries={[
          {
            pathname: '/audit/decision/b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000',
            key: 'audit-detail'
          }
        ]}
      >
        <AuditDetail />
      </MemoryRouter>
    </TrustyContext.Provider>
  );
};

const setupMockExecution = () => {
  const execution = {
    status: RemoteDataStatus.SUCCESS,
    data: {
      executionId: 'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000',
      executionDate: '2020-08-12T12:54:53.933Z',
      executionType: 'DECISION',
      executedModelName: 'fraud-score',
      executionSucceeded: true,
      executorName: 'Technical User'
    }
  } as RemoteData<Error, Execution>;
  const outcomes = {
    status: RemoteDataStatus.SUCCESS,
    data: [] as Outcome[]
  } as RemoteData<Error, Outcome[]>;

  (useExecutionInfo as jest.Mock).mockReturnValue(execution);
  (useDecisionOutcomes as jest.Mock).mockReturnValue(outcomes);

  return outcomes;
};

describe('AuditDetail', () => {
  test('renders loading animation while fetching data', () => {
    const execution = {
      status: RemoteDataStatus.LOADING
    } as RemoteData<Error, Execution>;
    const outcomes = {
      status: RemoteDataStatus.LOADING
    } as RemoteData<Error, Outcome[]>;

    (useExecutionInfo as jest.Mock).mockReturnValue(execution);
    (useDecisionOutcomes as jest.Mock).mockReturnValue(outcomes);

    const wrapper = setupWrapper(outcomes, false, true);

    expect(useExecutionInfo).toHaveBeenCalledWith(
      'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000'
    );
    expect(useDecisionOutcomes).toHaveBeenCalledWith(
      'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000'
    );

    expect(wrapper.find('ExecutionHeader')).toHaveLength(1);
    expect(wrapper.find('ExecutionHeader').prop('execution')).toStrictEqual(
      execution
    );
    expect(wrapper.find('.audit-detail__nav SkeletonFlexStripes')).toHaveLength(
      1
    );
    expect(wrapper.find('Switch Route')).toHaveLength(1);
    expect(
      wrapper.find('Route StackItem').at(0).find('SkeletonStripe')
    ).toHaveLength(1);
    expect(
      wrapper.find('Route StackItem').at(1).find('SkeletonCards')
    ).toHaveLength(1);
  });

  test('renders correctly an execution', () => {
    const execution = {
      status: RemoteDataStatus.SUCCESS,
      data: {
        executionId: 'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000',
        executionDate: '2020-08-12T12:54:53.933Z',
        executionType: 'DECISION',
        executedModelName: 'fraud-score',
        executionSucceeded: true,
        executorName: 'Technical User'
      }
    } as RemoteData<Error, Execution>;
    const outcomes = {
      status: RemoteDataStatus.SUCCESS,
      data: [
        {
          outcomeId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
          outcomeName: 'Mortgage Approval',
          evaluationStatus: 'SUCCEEDED',
          outcomeResult: {
            name: 'Mortgage Approval',
            type: 'boolean',
            value: true,
            components: []
          },
          messages: [],
          hasErrors: false
        },
        {
          outcomeId: '_9CFF8C35-4EB3-451E-874C-DB27A5A424C0',
          outcomeName: 'Risk Score',
          evaluationStatus: 'SUCCEEDED',
          outcomeResult: {
            name: 'Risk Score',
            type: 'number',
            value: 21.7031851958099,
            components: []
          },
          messages: [],
          hasErrors: false
        }
      ] as Outcome[]
    } as RemoteData<Error, Outcome[]>;

    (useExecutionInfo as jest.Mock).mockReturnValue(execution);
    (useDecisionOutcomes as jest.Mock).mockReturnValue(outcomes);

    const wrapper = setupWrapper(outcomes, false, true);

    expect(wrapper.find('ExecutionHeader')).toHaveLength(1);
    expect(wrapper.find('ExecutionHeader').prop('execution')).toStrictEqual(
      execution
    );
    expect(wrapper.find('Nav')).toHaveLength(1);
    expect(wrapper.find('NavItem')).toHaveLength(4);
    expect(wrapper.find('NavItem a').at(0).text()).toMatch('Outcomes');
    expect(wrapper.find('NavItem a').at(1).text()).toMatch('Outcomes details');
    expect(wrapper.find('Switch Route')).toHaveLength(1);
    expect(wrapper.find('Switch Route').prop('path')).toMatch(
      '/audit/decision/b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000/outcomes'
    );
    expect(wrapper.find('ExecutionDetail')).toHaveLength(1);
    expect(wrapper.find('ExecutionDetail').prop('outcomes')).toStrictEqual(
      outcomes
    );
  });

  test('does not contain the counterfactual section when it is disabled', () => {
    const outcomes = setupMockExecution();
    const wrapper = setupWrapper(outcomes, false, true);
    assertCounterfactualComponents(wrapper, false);
  });

  test('does contain the counterfactual section when it is enabled', () => {
    const outcomes = setupMockExecution();
    const wrapper = setupWrapper(outcomes, true, true);
    assertCounterfactualComponents(wrapper, true);
  });

  const assertCounterfactualComponents = (wrapper, counterfactualEnabled) => {
    expect(wrapper.find('CounterfactualUnsupportedBanner')).toHaveLength(
      counterfactualEnabled ? 1 : 0
    );
    expect(
      wrapper.find('li[data-ouia-component-id="counterfactual-analysis"]')
    ).toHaveLength(counterfactualEnabled ? 1 : 0);
  };
});
