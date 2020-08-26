import React from 'react';
import ExplanationUnavailable from '../ExplanationUnavailable';
import { shallow } from 'enzyme';

describe('ExplanationUnavailable', () => {
  test('renders correctly', () => {
    const wrapper = shallow(<ExplanationUnavailable />);

    expect(wrapper).toMatchSnapshot();
  });
});
