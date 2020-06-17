import React from 'react';
import { shallow } from 'enzyme';
import BrandLogo from '../BrandLogo';

const props = {
  src: '../../../../static/user.svg',
  alt: 'userImage',
  brandClick: jest.fn()
};
describe('Brand component tests', () => {
  it('snapshot testing', () => {
    const wrapper = shallow(<BrandLogo {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
});
