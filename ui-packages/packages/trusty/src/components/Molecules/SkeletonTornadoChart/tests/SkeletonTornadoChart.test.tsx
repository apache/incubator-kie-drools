import React from 'react';
import SkeletonTornadoChart from '../SkeletonTornadoChart';
import { shallow } from 'enzyme';

jest.mock('uuid', () => {
  let value = 0;
  return { v4: () => value++ };
});

describe('SkeletonTornadoChart', () => {
  test('renders a loading chart with 5 values', () => {
    const wrapper = shallow(
      <SkeletonTornadoChart valuesCount={5} height={400} />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
