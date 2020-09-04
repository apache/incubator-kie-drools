import { renderHook } from '@testing-library/react-hooks';
import useSaliencies from '../useSaliencies';
import * as api from '../../../../utils/api/httpClient';
import { Saliencies } from '../../../../types';
import { act } from 'react-test-renderer';

const flushPromises = () => new Promise(setImmediate);
const apiMock = jest.spyOn(api, 'httpClient');

beforeEach(() => {
  apiMock.mockClear();
});

describe('useSaliencies', () => {
  test('retrieves explanation info of an execution', async () => {
    const saliencies = {
      data: {
        status: 'SUCCEEDED',
        saliencies: [
          {
            outcomeId: '12345',
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
    };

    // @ts-ignore
    apiMock.mockImplementation(() => Promise.resolve(saliencies));

    const { result } = renderHook(() => {
      // tslint:disable-next-line:react-hooks-nesting
      return useSaliencies('b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000');
    });

    expect(result.current).toStrictEqual({ status: 'LOADING' });

    await act(async () => {
      await flushPromises();
    });

    expect(result.current).toStrictEqual({
      status: 'SUCCESS',
      data: saliencies.data
    });
    expect(apiMock).toHaveBeenCalledTimes(1);
  });
});
