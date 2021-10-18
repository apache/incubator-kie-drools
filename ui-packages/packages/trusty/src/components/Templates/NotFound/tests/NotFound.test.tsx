import React from 'react';
import { shallow } from 'enzyme';
import NotFound from '../NotFound';

describe('NotFound', () => {
  test('renders correctly', () => {
    const wrapper = shallow(<NotFound />);
    expect(wrapper).toMatchSnapshot();
  });
});
