import React from 'react';
import { shallow } from 'enzyme';
import Dashboard from '../Dashboard';

const props: any = {
  location: {
    pathname: '/ProcessInstances'
  }
}

describe('Dashboard component tests', () => {
  it('snapshot testing', () => {
    const wrapper = shallow(<Dashboard {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
});
