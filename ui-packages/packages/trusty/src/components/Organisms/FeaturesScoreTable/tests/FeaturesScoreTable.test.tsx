import React from 'react';
import FeaturesScoreTable from '../FeaturesScoreTable';
import { mount, shallow } from 'enzyme';

describe('FeaturesScoreTable', () => {
  test('renders correctly a score table', () => {
    const wrapper = shallow(<FeaturesScoreTable featuresScore={scores} />);

    expect(wrapper).toMatchSnapshot();
  });
  test('renders two lists of positive and negative outcomes', () => {
    const wrapper = mount(<FeaturesScoreTable featuresScore={scores} />);

    expect(wrapper.find('DataList')).toHaveLength(2);
    expect(
      wrapper
        .find('DataList')
        .at(0)
        .find('DataListItem')
    ).toHaveLength(4);
    expect(
      wrapper
        .find('DataList')
        .at(1)
        .find('DataListItem')
    ).toHaveLength(3);
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
