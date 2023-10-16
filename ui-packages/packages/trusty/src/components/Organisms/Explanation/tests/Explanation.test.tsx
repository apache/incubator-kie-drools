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
import { orderBy } from 'lodash';
import Explanation from '../Explanation';
import useSaliencies from '../../../Templates/OutcomeDetails/useSaliencies';
import {
  RemoteData,
  RemoteDataStatus,
  Saliencies,
  SaliencyStatus
} from '../../../../types';

jest.mock('../../../Templates/OutcomeDetails/useSaliencies');

describe('Explanation', () => {
  test('displays a loading animation while fetching saliencies', () => {
    const loadingSaliencies = {
      status: RemoteDataStatus.LOADING
    } as RemoteData<Error, Saliencies>;

    (useSaliencies as jest.Mock).mockReturnValue(loadingSaliencies);

    const wrapper = mount(
      <Explanation executionId={executionId} outcomeId={outcomeId} />
    );
    expect(useSaliencies).toHaveBeenCalledWith(executionId);

    expect(wrapper.find('FeaturesScoreChartBySign')).toHaveLength(0);
    expect(wrapper.find('SkeletonDoubleBarChart')).toHaveLength(1);
    expect(wrapper.find('.explanation__score-table SkeletonGrid')).toHaveLength(
      1
    );
  });

  test('displays the features score chart and the scores table', () => {
    (useSaliencies as jest.Mock).mockReturnValue(saliencies);
    const wrapper = mount(
      <Explanation executionId={executionId} outcomeId={outcomeId} />
    );
    let sortedFeatures;
    if (saliencies.status === RemoteDataStatus.SUCCESS) {
      sortedFeatures = orderBy(
        saliencies.data.saliencies[0].featureImportance,
        (item) => Math.abs(item.featureScore),
        'asc'
      );
    }

    expect(useSaliencies).toHaveBeenCalledWith(executionId);
    expect(wrapper.find('FeaturesScoreChartBySign')).toHaveLength(1);
    expect(
      wrapper.find('FeaturesScoreChartBySign').prop('featuresScore')
    ).toStrictEqual(sortedFeatures);
    expect(
      wrapper.find('button.explanation__all-features-opener')
    ).toHaveLength(0);
    expect(wrapper.find('FeaturesScoreTable')).toHaveLength(1);
    expect(
      wrapper.find('FeaturesScoreTable').prop('featuresScore')
    ).toStrictEqual(sortedFeatures);
  });

  test('displays the top feature chart and a modal for the all the features', () => {
    (useSaliencies as jest.Mock).mockReturnValue(manySaliencies);
    const wrapper = mount(
      <Explanation executionId={executionId} outcomeId={outcomeId} />
    );

    expect(
      (
        wrapper
          .find('FeaturesScoreChartBySign')
          .prop('featuresScore') as Saliencies[]
      ).length
    ).toBe(10);

    expect(
      wrapper.find('button.explanation__all-features-opener')
    ).toHaveLength(1);
    expect(wrapper.find('Modal FeaturesScoreChartBySign')).toHaveLength(0);

    wrapper.find('button.explanation__all-features-opener').simulate('click');

    expect(wrapper.find('Modal FeaturesScoreChartBySign')).toHaveLength(1);
    expect(
      wrapper.find('Modal FeaturesScoreChartBySign').prop('large')
    ).toBeTruthy();
    expect(
      (
        wrapper
          .find('Modal FeaturesScoreChartBySign')
          .prop('featuresScore') as Saliencies[]
      ).length
    ).toBe(
      manySaliencies.status === RemoteDataStatus.SUCCESS &&
        manySaliencies.data.saliencies[0].featureImportance.length
    );
  });

  test('displays a message when explanation data is not available', () => {
    (useSaliencies as jest.Mock).mockReturnValue(noSaliencies);

    const wrapper = mount(
      <Explanation executionId={executionId} outcomeId={outcomeId} />
    );

    expect(wrapper.find('FeaturesScoreChartBySign')).toHaveLength(0);
    expect(wrapper.find('FeaturesScoreTable')).toHaveLength(0);
    expect(wrapper.find('ExplanationUnavailable')).toHaveLength(1);
  });

  test('displays a message when there is an error calculating saliencies', () => {
    (useSaliencies as jest.Mock).mockReturnValue(salienciesError);

    const wrapper = mount(
      <Explanation executionId={executionId} outcomeId={outcomeId} />
    );

    expect(wrapper.find('FeaturesScoreChartBySign')).toHaveLength(0);
    expect(wrapper.find('FeaturesScoreTable')).toHaveLength(0);
    expect(wrapper.find('ExplanationError')).toHaveLength(1);
  });
});

const outcomeId = '_12268B68-94A1-4960-B4C8-0B6071AFDE58';
const executionId = 'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000';

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

const manySaliencies = {
  status: RemoteDataStatus.SUCCESS,
  data: {
    status: SaliencyStatus.SUCCEEDED,
    saliencies: [
      {
        outcomeId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
        featureImportance: [
          {
            featureName: 'Credit Score',
            featureScore: 0.4573103752215111
          },
          {
            featureName: 'Monthly Tax Payment',
            featureScore: -0.289364091764976
          },
          {
            featureName: 'Lender Ratings 2',
            featureScore: -0.33296266552555354
          },
          {
            featureName: 'Other Income',
            featureScore: -0.6214477521190565
          },
          {
            featureName: 'Assets 2',
            featureScore: 0.7500446067777924
          },
          {
            featureName: 'Liabilities',
            featureScore: -0.5716156400259922
          },
          {
            featureName: 'Lender Ratings',
            featureScore: -0.7684617355194296
          },
          {
            featureName: 'Employment Income',
            featureScore: 0.1449725242892499
          },
          {
            featureName: 'Other Income 2',
            featureScore: -0.4401536891546012
          },
          {
            featureName: 'Down Payment',
            featureScore: 0.31179648507149205
          },
          {
            featureName: 'Monthly HOA Payment',
            featureScore: -0.06325411368631983
          },
          {
            featureName: 'Assets',
            featureScore: -0.26603295274531846
          },
          {
            featureName: 'Monthly Insurance Payment',
            featureScore: -0.8337877562422129
          },
          {
            featureName: 'Liabilities 2',
            featureScore: 0.10021553046567333
          }
        ]
      }
    ]
  } as Saliencies
} as RemoteData<Error, Saliencies>;

const noSaliencies = {
  status: RemoteDataStatus.SUCCESS,
  data: {
    status: SaliencyStatus.SUCCEEDED,
    saliencies: [
      {
        outcomeId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
        featureImportance: []
      },
      {
        outcomeId: '_9CFF8C35-4EB3-451E-874C-DB27A5A424C0',
        featureImportance: []
      }
    ]
  } as Saliencies
} as RemoteData<Error, Saliencies>;

const salienciesError = {
  status: RemoteDataStatus.SUCCESS,
  data: {
    status: SaliencyStatus.FAILED,
    saliencies: []
  } as Saliencies
} as RemoteData<Error, Saliencies>;
