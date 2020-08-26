import React from 'react';
import SkeletonCards from '../SkeletonCards';
import { shallow } from 'enzyme';

jest.mock('uuid', () => {
  let value = 0;
  return { v4: () => value++ };
});

describe('SkeletonCards', () => {
  test('renders a list of cards', () => {
    const wrapper = shallow(<SkeletonCards quantity={3} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('.skeleton-cards')).toHaveLength(1);
    expect(wrapper.find('.skeleton-cards__card')).toHaveLength(3);
    expect(
      wrapper
        .find('.skeleton-cards__card')
        .at(0)
        .find('SkeletonStripe')
    ).toHaveLength(2);
    expect(
      wrapper
        .find('.skeleton-cards__card')
        .at(1)
        .find('SkeletonStripe')
    ).toHaveLength(2);
  });
});
