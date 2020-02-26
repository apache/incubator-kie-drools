import React from 'react';
import { shallow } from 'enzyme';
import Dashboard from '../Dashboard';
import { MockedProvider } from '@apollo/react-testing';

const props: any = {
  location: {
    pathname: '/ProcessInstances'
  }
};

const mocks = [];

describe('Dashboard component tests', () => {
  it('snapshot testing', () => {
    const wrapper = shallow(
      <MockedProvider mocks={mocks}>
        <Dashboard {...props} />
      </MockedProvider>
    );
    expect(wrapper).toMatchSnapshot();
  });
});
