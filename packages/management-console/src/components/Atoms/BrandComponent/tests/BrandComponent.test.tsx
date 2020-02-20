import React from 'react';
import { shallow } from 'enzyme';
import BrandComponent from '../BrandComponent';

describe('Brand component tests', () => {
  it('snapshot testing', () => {
    const wrapper = shallow(<BrandComponent />);
    expect(wrapper).toMatchSnapshot();
  });
});
