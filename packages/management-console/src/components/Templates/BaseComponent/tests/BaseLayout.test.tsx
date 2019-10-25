import React from 'react';
import { shallow } from 'enzyme';
import BaseLayout from '../BaseLayout';

it('Sample test case', () => {
  let wrapper = shallow(<BaseLayout />);
  expect(wrapper).toMatchSnapshot();
});
