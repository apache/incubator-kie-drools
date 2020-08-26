import * as React from 'react';
import { mount } from 'enzyme';
import Explanation from '../Explanation';
import { MemoryRouter } from 'react-router';
import {
  FeatureScores,
  ItemObject,
  Outcome,
  RemoteData
} from '../../../../types';
import useOutcomeDetail from '../useOutcomeDetail';
import useFeaturesScores from '../useFeaturesScores';

const executionId = 'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000';

jest.mock('uuid', () => {
  let value = 0;
  return { v4: () => value++ };
});
jest.mock('../useOutcomeDetail');
jest.mock('../useFeaturesScores');
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useParams: () => ({
    executionId
  })
}));

describe('Explanation', () => {
  test('renders animations while fetching data', () => {
    const loadingOutcomes = {
      status: 'LOADING'
    } as RemoteData<Error, Outcome[]>;
    const loadingOutcomeDetail = {
      status: 'LOADING'
    } as RemoteData<Error, ItemObject[]>;
    const loadingFeaturesScores = {
      status: 'LOADING'
    } as RemoteData<Error, FeatureScores[]>;

    (useOutcomeDetail as jest.Mock).mockReturnValue(loadingOutcomeDetail);
    (useFeaturesScores as jest.Mock).mockReturnValue({
      featuresScores: loadingFeaturesScores,
      topFeaturesScores: []
    });

    const wrapper = mount(
      <MemoryRouter
        initialEntries={[
          {
            pathname: `/audit/decision/${executionId}/outcomes-details`,
            key: 'outcomes-detail'
          }
        ]}
      >
        <Explanation outcomes={loadingOutcomes} />
      </MemoryRouter>
    );

    expect(useOutcomeDetail).toHaveBeenCalledWith(executionId, null);
    expect(useFeaturesScores).toHaveBeenCalledWith(executionId);

    expect(
      wrapper.find(
        '.explanation-view__section--outcome-selector SkeletonStripe'
      )
    ).toHaveLength(1);
    expect(
      wrapper.find('.explanation-view__outcome SkeletonGrid')
    ).toHaveLength(1);
    expect(wrapper.find('SkeletonTornadoChart')).toHaveLength(1);
    expect(
      wrapper.find('.explanation-view__score-table SkeletonGrid')
    ).toHaveLength(1);
  });

  test('renders correctly the details of an outcome', () => {
    (useOutcomeDetail as jest.Mock).mockReturnValue(outcomeDetail);
    (useFeaturesScores as jest.Mock).mockReturnValue({
      featuresScores,
      topFeaturesScores: []
    });

    const wrapper = mount(
      <MemoryRouter
        initialEntries={[
          {
            pathname: `/audit/decision/${executionId}/outcomes-details`,
            key: 'outcomes-detail'
          }
        ]}
      >
        <Explanation outcomes={outcomes} />
      </MemoryRouter>
    );

    expect(useOutcomeDetail).toHaveBeenCalledWith(
      executionId,
      '_12268B68-94A1-4960-B4C8-0B6071AFDE58'
    );
    expect(useFeaturesScores).toHaveBeenCalledWith(executionId);

    expect(wrapper.find('ExplanationSwitch')).toHaveLength(1);
    expect(
      wrapper.find('ExplanationSwitch').prop('outcomesList')
    ).toStrictEqual(outcomes.status === 'SUCCESS' && outcomes.data);
    expect(wrapper.find('FeaturesScoreChart')).toHaveLength(1);
    expect(
      wrapper.find('FeaturesScoreChart').prop('featuresScore')
    ).toStrictEqual(featuresScores.status === 'SUCCESS' && featuresScores.data);
    expect(wrapper.find('FeaturesScoreTable')).toHaveLength(1);
    expect(
      wrapper.find('FeaturesScoreTable').prop('featuresScore')
    ).toStrictEqual(featuresScores.status === 'SUCCESS' && featuresScores.data);
    expect(wrapper.find('InputDataBrowser')).toHaveLength(1);
    expect(wrapper.find('InputDataBrowser').prop('inputData')).toStrictEqual(
      outcomeDetail
    );
  });

  test('renders an outcome with no explanation info', () => {
    const noFeatures = { status: 'SUCCESS', data: [] } as RemoteData<
      Error,
      FeatureScores[]
    >;
    (useOutcomeDetail as jest.Mock).mockReturnValue(outcomeDetail);
    (useFeaturesScores as jest.Mock).mockReturnValue({
      featuresScores: noFeatures,
      topFeaturesScores: []
    });

    const wrapper = mount(
      <MemoryRouter
        initialEntries={[
          {
            pathname: `/audit/decision/${executionId}/outcomes-details`,
            key: 'outcomes-detail'
          }
        ]}
      >
        <Explanation outcomes={outcomes} />
      </MemoryRouter>
    );

    expect(wrapper.find('FeaturesScoreChart')).toHaveLength(0);
    expect(wrapper.find('FeaturesScoreTable')).toHaveLength(0);
    expect(wrapper.find('ExplanationUnavailable')).toHaveLength(1);
  });
});

const outcomes = {
  status: 'SUCCESS',
  data: [
    {
      outcomeId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
      outcomeName: 'Mortgage Approval',
      evaluationStatus: 'SUCCEEDED',
      outcomeResult: {
        name: 'Mortgage Approval',
        typeRef: 'boolean',
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
        typeRef: 'number',
        value: 21.7031851958099,
        components: []
      },
      messages: [],
      hasErrors: false
    }
  ]
} as RemoteData<Error, Outcome[]>;
const outcomeDetail = {
  status: 'SUCCESS',
  data: [
    {
      name: 'Asset Score',
      typeRef: 'number',
      value: 738,
      components: []
    },
    {
      name: 'Asset Amount',
      typeRef: 'number',
      value: 70000,
      components: []
    }
  ]
} as RemoteData<Error, ItemObject[]>;
const featuresScores = {
  status: 'SUCCESS',
  data: [
    {
      featureName: 'Lender Ratings',
      featureScore: -0.08937896629080377
    },
    {
      featureName: 'Liabilities',
      featureScore: 0.6780527129423648
    }
  ]
} as RemoteData<Error, FeatureScores[]>;
