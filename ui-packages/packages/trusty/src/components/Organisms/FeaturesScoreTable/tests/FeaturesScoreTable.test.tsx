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
    featureId: '66aaad87-25e0-4074-86e6-db804b0c72e6',
    featureScore: 0.6780527129423648
  },
  {
    featureName: 'Lender Ratings',
    featureId: 'ae35bfc0-52c0-4725-96b5-0f231a68345e',
    featureScore: -0.08937896629080377
  },
  {
    featureName: 'Employment Income',
    featureId: 'cfe35995-375d-4b30-801c-ae0b18d707f6',
    featureScore: -0.9240811677386516
  },
  {
    featureName: 'Liabilities 2',
    featureId: 'a14a8292-579e-4937-9133-7f4986b31072',
    featureScore: 0.7693802543201365
  },
  {
    featureName: 'Assets',
    featureId: 'a5621def-cfd5-4d4f-a984-a29970b39644',
    featureScore: 0.21272743757961554
  }
];
