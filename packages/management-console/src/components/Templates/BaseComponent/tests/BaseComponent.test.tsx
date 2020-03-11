import React from 'react';
import { shallow } from 'enzyme';
import BaseComponent from '../BaseComponent';

it('Sample test case', () => {
  const wrapper = shallow(<BaseComponent />);
  expect(wrapper).toMatchSnapshot();
});
