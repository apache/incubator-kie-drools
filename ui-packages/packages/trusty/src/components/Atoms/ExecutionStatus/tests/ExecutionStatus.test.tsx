import React from 'react';
import ExecutionStatus from '../ExecutionStatus';
import { shallow } from 'enzyme';

describe('Execution status', () => {
  test('renders a positive outcome', () => {
    const wrapper = shallow(<ExecutionStatus result="success" />);
    const icon = wrapper.find('CheckCircleIcon');

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('span').text()).toMatch('Completed');
    expect(icon).toHaveLength(1);
    expect(
      icon.hasClass('execution-status-badge execution-status-badge--success')
    ).toBeTruthy();
  });

  test('renders a negative outcome', () => {
    const wrapper = shallow(<ExecutionStatus result="failure" />);
    const icon = wrapper.find('ErrorCircleOIcon');

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('span').text()).toMatch('Error');
    expect(icon).toHaveLength(1);
    expect(
      icon.hasClass('execution-status-badge execution-status-badge--error')
    ).toBeTruthy();
  });
});
