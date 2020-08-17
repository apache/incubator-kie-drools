import React from 'react';
import { shallow } from 'enzyme';
import SkeletonStripe from '../SkeletonStripe';

describe('SkeletonStripe', () => {
  test('renders a small stripe', () => {
    const wrapper = shallow(<SkeletonStripe />);
    expect(wrapper.find('span').props().className).toMatch('skeleton__stripe');
    expect(wrapper).toMatchSnapshot();
  });

  test('renders a medium size stripe when size "md" is passed', () => {
    const wrapper = shallow(<SkeletonStripe size="md" />);
    expect(wrapper.find('span').props().className).toMatch(
      'skeleton__stripe skeleton__stripe--md'
    );
  });
  test('renders a large size stripe when size "lg" is passed', () => {
    const wrapper = shallow(<SkeletonStripe size="lg" />);
    expect(wrapper.find('span').props().className).toMatch(
      'skeleton__stripe skeleton__stripe--lg'
    );
  });
  test('renders an inline stripe when isInline is passed', () => {
    const wrapper = shallow(<SkeletonStripe isInline />);
    expect(wrapper.find('span').props().className).toMatch(
      'skeleton__stripe skeleton__stripe--inline'
    );
  });
  test('renders an inline stripe with custom styles', () => {
    const wrapper = shallow(<SkeletonStripe customStyle={{ width: 600 }} />);
    expect(wrapper.find('span').props().style).toStrictEqual({ width: 600 });
  });
});
