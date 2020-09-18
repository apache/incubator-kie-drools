import React from 'react';
import { shallow } from 'enzyme';
import EndpointLink from '../EndpointLink';

const props1 = {
  serviceUrl: 'http://localhost:4000/',
  isLinkShown: true,
  ouiaId: 'endpoint-link-1'
};

const props2 = {
  serviceUrl: 'http://localhost:4000/',
  isLinkShown: false,
  ouiaId: 'endpoint-link-2'
};

const props3 = {
  serviceUrl: 'http://localhost:4000/',
  isLinkShown: false,
  linkLabel: 'This is a label',
  ouiaId: 'endpoint-link-3'
};

const props4 = {
  serviceUrl: null,
  isLinkShown: false,
  ouiaId: 'endpoint-link-4'
};
describe('EndpointLink component tests', () => {
  it('snapshot testing for link shown', () => {
    const wrapper = shallow(<EndpointLink {...props1} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing for link hidden', () => {
    const wrapper = shallow(<EndpointLink {...props2} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing for link hidden with custom link label', () => {
    const wrapper = shallow(<EndpointLink {...props3} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing no service URL and link hidden', () => {
    const wrapper = shallow(<EndpointLink {...props4} />);
    expect(wrapper).toMatchSnapshot();
  });
});
