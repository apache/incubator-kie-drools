import React from 'react';
import ExplanationError from '../ExplanationError';
import { shallow } from 'enzyme';

describe('ExplanationError', () => {
  test('renders correctly', () => {
    const wrapper = shallow(<ExplanationError />);

    expect(wrapper).toMatchSnapshot();
  });

  test('displays explanation status details', () => {
    const message = 'The server exploded!';
    const wrapper = shallow(<ExplanationError statusDetail={message} />);

    expect(wrapper.find('.explanation-error-detail').text()).toMatch(message);
  });
});
