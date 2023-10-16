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
import { MemoryRouter } from 'react-router';
import {
  ItemObject,
  Outcome,
  RemoteData,
  RemoteDataStatus,
  Saliencies,
  SaliencyStatus
} from '../../../../types';
import useSaliencies from '../useSaliencies';
import OutcomeDetails from '../OutcomeDetails';
import useOutcomeDetail from '../useOutcomeDetail';
import { TrustyContext } from '../../TrustyApp/TrustyApp';

const executionId = 'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000';

jest.mock('uuid', () => {
  let value = 0;
  return { v4: () => value++ };
});
jest.mock('../useOutcomeDetail');
jest.mock('../useSaliencies');
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useParams: () => ({
    executionId
  })
}));

const setupWrapper = (
  outcomes: RemoteData<Error, Outcome[]>,
  counterfactualEnabled: boolean,
  explanationEnabled: boolean
) => {
  return mount(
    <MemoryRouter
      initialEntries={[
        {
          pathname: `/audit/decision/${executionId}/outcomes-details`,
          key: 'outcomes-detail'
        }
      ]}
    >
      <TrustyContext.Provider
        value={{ config: { counterfactualEnabled, explanationEnabled } }}
      >
        <OutcomeDetails outcomes={outcomes} />
      </TrustyContext.Provider>
    </MemoryRouter>
  );
};

describe('OutcomeDetails', () => {
  test('renders animations while fetching data', () => {
    const loadingOutcomes = {
      status: RemoteDataStatus.LOADING
    } as RemoteData<Error, Outcome[]>;
    const loadingOutcomeDetail = {
      status: RemoteDataStatus.LOADING
    } as RemoteData<Error, ItemObject[]>;
    const loadingSaliencies = {
      status: RemoteDataStatus.LOADING
    } as RemoteData<Error, Saliencies>;

    (useOutcomeDetail as jest.Mock).mockReturnValue(loadingOutcomeDetail);
    (useSaliencies as jest.Mock).mockReturnValue(loadingSaliencies);

    const wrapper = setupWrapper(loadingOutcomes, false, true);

    expect(useOutcomeDetail).toHaveBeenCalledWith(executionId, null);

    expect(
      wrapper.find('.outcome-details__section--outcome-selector SkeletonStripe')
    ).toHaveLength(1);
    expect(wrapper.find('.outcome-details__outcome SkeletonGrid')).toHaveLength(
      1
    );
  });

  test('renders correctly the details of an outcome', () => {
    (useOutcomeDetail as jest.Mock).mockReturnValue(outcomeDetail);
    (useSaliencies as jest.Mock).mockReturnValue(saliencies);

    const wrapper = setupWrapper(outcomes, false, true);
    const outcomeId =
      outcomes.status === RemoteDataStatus.SUCCESS &&
      outcomes.data[0].outcomeId;

    expect(useOutcomeDetail).toHaveBeenCalledWith(executionId, outcomeId);

    expect(wrapper.find('OutcomeSwitch')).toHaveLength(1);
    expect(wrapper.find('OutcomeSwitch').prop('outcomesList')).toStrictEqual(
      outcomes.status === RemoteDataStatus.SUCCESS && outcomes.data
    );

    expect(wrapper.find('Outcomes')).toHaveLength(1);
    expect(wrapper.find('Outcomes').props()['outcomes']).toStrictEqual(
      outcomes.status === RemoteDataStatus.SUCCESS && [outcomes.data[0]]
    );

    expect(wrapper.find('Explanation')).toHaveLength(1);
    expect(wrapper.find('Explanation').prop('executionId')).toStrictEqual(
      executionId
    );
    expect(wrapper.find('Explanation').prop('outcomeId')).toStrictEqual(
      outcomeId
    );

    expect(wrapper.find('InputDataBrowser')).toHaveLength(1);
    expect(wrapper.find('InputDataBrowser').prop('inputData')).toStrictEqual(
      outcomeDetail
    );
  });

  test('does not contain the explanation section when it is disabled', () => {
    (useOutcomeDetail as jest.Mock).mockReturnValue(outcomeDetail);
    (useSaliencies as jest.Mock).mockReturnValue(saliencies);

    const wrapper = setupWrapper(outcomes, false, false);

    expect(wrapper.find('Explanation')).toHaveLength(0);
  });
});

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
  ]
} as RemoteData<Error, Outcome[]>;
const outcomeDetail = {
  status: RemoteDataStatus.SUCCESS,
  data: [
    {
      name: 'Asset Score',
      type: 'number',
      value: 738,
      components: []
    },
    {
      name: 'Asset Amount',
      type: 'number',
      value: 70000,
      components: []
    }
  ]
} as RemoteData<Error, ItemObject[]>;
const saliencies = {
  status: RemoteDataStatus.SUCCESS,
  data: {
    status: SaliencyStatus.SUCCEEDED,
    saliencies: [
      {
        outcomeId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
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
      },
      {
        outcomeId: '_9CFF8C35-4EB3-451E-874C-DB27A5A424C0',
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
} as RemoteData<Error, Saliencies>;
