import React from 'react';
import DatePicker from '../DatePicker';
import { mount } from 'enzyme';
import { render, screen, waitFor } from '@testing-library/react';

describe('DatePicker', () => {
  test('renders correctly', () => {
    const props = {
      id: 'datepicker',
      onDateUpdate: jest.fn()
    };
    const wrapper = mount(<DatePicker {...props} />);
    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('input#datepicker')).toHaveLength(1);
  });

  test('displays dates in the correct format', async () => {
    const props = {
      id: 'datepicker',
      value: '2020-06-01',
      onDateUpdate: jest.fn()
    };

    render(<DatePicker {...props} />);
    await waitFor(() =>
      expect(screen.getByDisplayValue('June 1, 2020')).toBeTruthy()
    );
  });
});
