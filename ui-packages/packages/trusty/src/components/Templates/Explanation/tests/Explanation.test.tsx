import * as React from 'react';
import { mount } from 'enzyme';
import { MemoryRouter } from 'react-router';
import { orderBy } from 'lodash';
import Explanation from '../Explanation';
import useOutcomeDetail from '../useOutcomeDetail';
import useSaliencies from '../useSaliencies';
import { ItemObject, Outcome, RemoteData, Saliencies } from '../../../../types';

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

describe('Explanation', () => {
  test('renders animations while fetching data', () => {
    const loadingOutcomes = {
      status: 'LOADING'
    } as RemoteData<Error, Outcome[]>;
    const loadingOutcomeDetail = {
      status: 'LOADING'
    } as RemoteData<Error, ItemObject[]>;
    const loadingSaliencies = {
      status: 'LOADING'
    } as RemoteData<Error, Saliencies>;

    (useOutcomeDetail as jest.Mock).mockReturnValue(loadingOutcomeDetail);
    (useSaliencies as jest.Mock).mockReturnValue(loadingSaliencies);

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
    expect(useSaliencies).toHaveBeenCalledWith(executionId);

    expect(
      wrapper.find(
        '.explanation-view__section--outcome-selector SkeletonStripe'
      )
    ).toHaveLength(1);
    expect(
      wrapper.find('.explanation-view__outcome SkeletonGrid')
    ).toHaveLength(1);
    expect(wrapper.find('SkeletonDoubleBarChart')).toHaveLength(1);
    expect(
      wrapper.find('.explanation-view__score-table SkeletonGrid')
    ).toHaveLength(1);
  });

  test('renders correctly the details of an outcome', () => {
    (useOutcomeDetail as jest.Mock).mockReturnValue(outcomeDetail);
    (useSaliencies as jest.Mock).mockReturnValue(saliencies);

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
    let sortedFeatures;
    if (saliencies.status === 'SUCCESS') {
      sortedFeatures = orderBy(
        saliencies.data.saliencies[0].featureImportance,
        item => Math.abs(item.featureScore),
        'asc'
      );
    }
    expect(useOutcomeDetail).toHaveBeenCalledWith(
      executionId,
      '_12268B68-94A1-4960-B4C8-0B6071AFDE58'
    );
    expect(useSaliencies).toHaveBeenCalledWith(executionId);

    expect(wrapper.find('ExplanationSwitch')).toHaveLength(1);
    expect(
      wrapper.find('ExplanationSwitch').prop('outcomesList')
    ).toStrictEqual(outcomes.status === 'SUCCESS' && outcomes.data);
    expect(wrapper.find('FeaturesScoreChartBySign')).toHaveLength(1);
    expect(
      wrapper.find('FeaturesScoreChartBySign').prop('featuresScore')
    ).toStrictEqual(sortedFeatures);
    expect(wrapper.find('FeaturesScoreTable')).toHaveLength(1);
    expect(
      wrapper.find('FeaturesScoreTable').prop('featuresScore')
    ).toStrictEqual(sortedFeatures);
    expect(wrapper.find('InputDataBrowser')).toHaveLength(1);
    expect(wrapper.find('InputDataBrowser').prop('inputData')).toStrictEqual(
      outcomeDetail
    );
  });

  test('renders an outcome with no explanation info', () => {
    (useOutcomeDetail as jest.Mock).mockReturnValue(outcomeDetail);
    (useSaliencies as jest.Mock).mockReturnValue(noSaliencies);

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

    expect(wrapper.find('FeaturesScoreChartBySign')).toHaveLength(0);
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
const saliencies = {
  status: 'SUCCESS',
  data: {
    status: 'SUCCEEDED',
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
const noSaliencies = {
  status: 'SUCCESS',
  data: {
    status: 'SUCCEEDED',
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
