import React from 'react';
import { shallow } from 'enzyme';
import SkeletonDoubleBarChart from '../SkeletonDoubleBarChart';

jest.mock('uuid', () => {
  let value = 0;
  return { v4: () => value++ };
});

describe('SkeletonDoubleBarChart', () => {
  test('renders a loading chart with 5 values', () => {
    const wrapper = shallow(
      <SkeletonDoubleBarChart valuesCount={5} height={400} />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
