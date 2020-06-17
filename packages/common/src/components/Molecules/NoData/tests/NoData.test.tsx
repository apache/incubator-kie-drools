import React from 'react';
import { shallow } from 'enzyme';
import NoData from '../NoData';
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
      title: 'NoData',
      prev: '/processInstances',
      description: 'some description',
      buttonText: 'button'
    }
  }
};

describe('NoData component tests', () => {
  it('snapshot tests with location object', () => {
    const wrapper = shallow(<NoData {...props1} />);
    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot tests without location object', () => {
    const wrapper = shallow(<NoData {...props2} />);
    expect(wrapper).toMatchSnapshot();
  });
  /* tslint:disable */
  it('redirect button click', () => {
    const wrapper = shallow(<NoData {...props2} />);
    wrapper.find(Button).simulate('click');
    expect(wrapper.find('Redirect').props()['to']).toEqual('/processInstances');
  });
});
