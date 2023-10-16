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
import useFeaturesScores from '../useFeaturesScores';
import {
  RemoteData,
  RemoteDataStatus,
  Saliencies,
  SaliencyStatus
} from '../../../../types';
import { orderBy } from 'lodash';

describe('useFeaturesScores', () => {
  test('retrieves feature scores for different outcomes', async () => {
    let outcomeId = 'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000';
    const hook = renderHook(() => {
      // tslint:disable-next-line:react-hooks-nesting
      return useFeaturesScores(saliencies, outcomeId);
    });

    expect(hook.result.current.featuresScores).toStrictEqual(
      getSortedScores(outcomeId)
    );
    expect(hook.result.current.topFeaturesScoresBySign).toStrictEqual(
      sortedScoresBySign
    );

    outcomeId = '_6O8O6B35-4EB3-451E-874C-DB27A5C5V6B7';

    hook.rerender();

    expect(hook.result.current.featuresScores).toStrictEqual(
      getSortedScores(outcomeId)
    );
    expect(hook.result.current.topFeaturesScoresBySign).toEqual([]);
  });
});

const getSortedScores = (selectedOutcomeId) => {
  if (saliencies.status === RemoteDataStatus.SUCCESS) {
    const values = saliencies.data.saliencies.find(
      (item) => item.outcomeId === selectedOutcomeId
    );
    return orderBy(
      values.featureImportance,
      (item) => Math.abs(item.featureScore),
      'asc'
    );
  }
};

const saliencies = {
  status: RemoteDataStatus.SUCCESS,
  data: {
    status: SaliencyStatus.SUCCEEDED,
    saliencies: [
      {
        outcomeId: 'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000',
        featureImportance: [
          {
            featureName: 'Liabilities',
            featureScore: 0.6780527129423648
          },
          {
            featureName: 'Lender Ratings',
            featureScore: -0.08937896629080377
          },
          {
            featureName: 'Monthly Tax Payment',
            featureScore: 0.09496803604258286
          },
          {
            featureName: 'Monthly Insurance Payment',
            featureScore: 0.6620138158951472
          },
          {
            featureName: 'Monthly HOA Payment',
            featureScore: 0.03133416572689707
          },
          {
            featureName: 'Credit Score',
            featureScore: 0.6673415076294651
          },
          {
            featureName: 'Down Payment',
            featureScore: 0.24368862959290616
          }
        ]
      },
      {
        outcomeId: '_6O8O6B35-4EB3-451E-874C-DB27A5C5V6B7',
        featureImportance: [
          {
            featureName: 'Credit Score',
            featureScore: 0.6780527129423648
          },
          {
            featureName: 'Assets',
            featureScore: -0.08937896629080377
          }
        ]
      }
    ]
  } as Saliencies
} as RemoteData<Error, Saliencies>;

const sortedScoresBySign = [
  {
    featureName: 'Monthly Tax Payment',
    featureScore: 0.09496803604258286
  },
  { featureName: 'Down Payment', featureScore: 0.24368862959290616 },
  {
    featureName: 'Monthly Insurance Payment',
    featureScore: 0.6620138158951472
  },
  { featureName: 'Credit Score', featureScore: 0.6673415076294651 },
  { featureName: 'Liabilities', featureScore: 0.6780527129423648 },
  { featureName: 'Lender Ratings', featureScore: -0.08937896629080377 }
];
