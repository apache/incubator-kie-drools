import React from 'react';
import { shallow } from 'enzyme';
import ExecutionId from '../ExecutionId';

describe('ExecutionId', () => {
  test('renders a shortened version of a UUID', () => {
    const wrapper = shallow(
      <ExecutionId id="95a770cf-54dd-4438-b1be-1165f3c61c39" />
    );
    expect(wrapper.find('.execution-id')).toHaveLength(1);
    expect(wrapper.text()).toMatch('#95a770cf');
  });
});
