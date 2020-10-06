import React from 'react';
import { shallow } from 'enzyme';
import PageNotFound from '../PageNotFound';
import { Button } from '@patternfly/react-core';
import * as H from 'history';
import { match } from 'react-router';

const path = '/xy';
const match: match = {
  isExact: false,
  path,
  url: path,
  params: {}
};
const location = H.createLocation('/processInstances');

const props1 = {
  defaultPath: '/processInstances',
  defaultButton: '',
  location,
  history: H.createMemoryHistory({ keyLength: 0 }),
  match
};

const props2 = {
  defaultPath: '/processInstances',
  defaultButton: '',
  location: {
    state: {
      prev: '/processInstances',
      description: 'some description',
      buttonText: 'button'
    },
    pathname: '',
    search: '',
    hash: ''
  },
  history: H.createMemoryHistory({ keyLength: 0 }),
  match
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
