import { renderHook } from '@testing-library/react-hooks';
import useFeaturesScores from '../useFeaturesScores';
import { RemoteData, Saliencies } from '../../../../types';
import { orderBy } from 'lodash';

describe('useFeaturesScores', () => {
  test('retrieves feature scores of an execution', async () => {
    const { result } = renderHook(() => {
      // tslint:disable-next-line:react-hooks-nesting
      return useFeaturesScores(
        saliencies,
        'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000'
      );
    });
    let sortedFeatures;
    if (saliencies.status === 'SUCCESS') {
      sortedFeatures = orderBy(
        saliencies.data.saliencies[0].featureImportance,
        item => Math.abs(item.featureScore),
        'asc'
      );
    }

    expect(result.current.featuresScores).toStrictEqual(sortedFeatures);
  });
});

const saliencies = {
  status: 'SUCCESS',
  data: {
    status: 'SUCCEEDED',
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
          }
        ]
      }
    ]
  } as Saliencies
} as RemoteData<Error, Saliencies>;
