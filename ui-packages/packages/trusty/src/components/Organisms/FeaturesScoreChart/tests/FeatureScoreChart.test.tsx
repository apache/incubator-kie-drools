import React from 'react';
import FeaturesScoreChart from '../FeaturesScoreChart';
import { shallow } from 'enzyme';

describe('FeaturesScoreChart', () => {
  test('renders a chart for a list of feature scores', () => {
    const wrapper = shallow(<FeaturesScoreChart featuresScore={scores} />);

    expect(wrapper).toMatchSnapshot();
  });
});

const scores = [
  {
    featureName: 'Liabilities',
    featureScore: 0.6780527129423648
  },
  {
    featureName: 'Lender Ratings',
    featureScore: -0.08937896629080377
  },
  {
    featureName: 'Employment Income',
    featureScore: -0.9240811677386516
  },
  {
    featureName: 'Liabilities 2',
    featureScore: 0.7693802543201365
  },
  {
    featureName: 'Assets',
    featureScore: 0.21272743757961554
  }
];
