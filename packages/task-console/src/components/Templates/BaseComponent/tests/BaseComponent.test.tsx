import React from 'react';
import { shallow } from 'enzyme';
import BaseComponent from '../BaseComponent';

describe('Base component test cases', () => {
  it('Snapshot testing', () => {
    const wrapper = shallow(<BaseComponent />);
    expect(wrapper).toMatchSnapshot();
  });
});
