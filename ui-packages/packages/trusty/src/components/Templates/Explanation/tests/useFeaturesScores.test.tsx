import { renderHook } from '@testing-library/react-hooks';
import useFeaturesScores from '../useFeaturesScores';
import * as api from '../../../../utils/api/httpClient';
import { FeatureScores } from '../../../../types';
import { act } from 'react-test-renderer';
import { orderBy } from 'lodash';

const flushPromises = () => new Promise(setImmediate);
const apiMock = jest.spyOn(api, 'httpClient');

beforeEach(() => {
  apiMock.mockClear();
});

describe('useFeaturesScores', () => {
  test('retrieves feature scores of an execution', async () => {
    const scores = {
      data: {
        featureImportance: [
          {
            featureName: 'Liabilities',
            featureScore: 0.6780527129423648
          },
          {
            featureName: 'Lender Ratings',
            featureScore: -0.08937896629080377
          }
        ] as FeatureScores[]
      }
    };
    const sortedFeatures = orderBy(
      scores.data.featureImportance,
      item => Math.abs(item.featureScore),
      'asc'
    );

    // @ts-ignore
    apiMock.mockImplementation(() => Promise.resolve(scores));

    const { result } = renderHook(() => {
      // tslint:disable-next-line:react-hooks-nesting
      return useFeaturesScores('b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000');
    });

    expect(result.current.featuresScores).toStrictEqual({ status: 'LOADING' });

    await act(async () => {
      await flushPromises();
    });

    expect(result.current.featuresScores).toStrictEqual({
      status: 'SUCCESS',
      data: sortedFeatures
    });
    expect(apiMock).toHaveBeenCalledTimes(1);
  });
});
