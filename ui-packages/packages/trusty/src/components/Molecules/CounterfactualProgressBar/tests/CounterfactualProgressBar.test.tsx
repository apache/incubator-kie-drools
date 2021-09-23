import React from 'react';
import { mount } from 'enzyme';
import CounterfactualProgressBar from '../CounterfactualProgressBar';
import { act } from 'react-dom/test-utils';

describe('CounterfactualProgressBar', () => {
  test('renders a progress bar', () => {
    jest.useFakeTimers();

    const timeLimit = 60;
    const wrapper = mount(<CounterfactualProgressBar />);

    expect(setInterval).toHaveBeenCalledTimes(1);
    expect(setInterval).toHaveBeenLastCalledWith(expect.any(Function), 1000);
    expect(wrapper.find('Progress').props()['value']).toEqual(0);

    const firstAdvance = 10;
    act(() => {
      jest.advanceTimersByTime(1000 * firstAdvance);
    });
    wrapper.update();

    expect(wrapper.find('Progress').props()['value']).toEqual(
      (firstAdvance * 100) / timeLimit
    );
    expect(wrapper.find('Progress').props()['label']).toMatch(
      `${timeLimit - firstAdvance} seconds remaining`
    );

    const advanceToEnd = 50;
    act(() => {
      jest.advanceTimersByTime(1000 * advanceToEnd);
    });
    wrapper.update();

    expect(wrapper.find('Progress').props()['value']).toEqual(100);
    expect(wrapper.find('Progress').props()['label']).toMatch(`Wrapping up`);

    jest.useRealTimers();
  });
});
