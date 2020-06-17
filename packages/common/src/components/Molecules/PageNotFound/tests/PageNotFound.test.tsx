import React from 'react';
import { shallow } from 'enzyme';
import PageNotFound from '../PageNotFound';
import { Button } from '@patternfly/react-core';

const props1 = {
  defaultPath: '/processInstances',
  defaultButton: '',
  location: {}
};

const props2 = {
  defaultPath: '/processInstances',
  defaultButton: '',
  location: {
    state: {
      prev: '/processInstances',
      description: 'some description',
      buttonText: 'button'
    }
  }
};

describe('PageNotFound component tests', () => {
  it('snapshot testing without location object', () => {
    const wrapper = shallow(<PageNotFound {...props1} />);
    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot testing with location object', () => {
    const wrapper = shallow(<PageNotFound {...props2} />);
    expect(wrapper).toMatchSnapshot();
  });
  /* tslint:disable */
  it('redirect button click', () => {
    const wrapper = shallow(<PageNotFound {...props2} />);
    wrapper.find(Button).simulate('click');
    expect(wrapper.find('Redirect').props()['to']).toEqual('/processInstances');
  });
});
