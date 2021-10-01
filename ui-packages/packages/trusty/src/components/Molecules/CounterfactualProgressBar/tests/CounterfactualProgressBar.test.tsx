import React from 'react';
import { mount } from 'enzyme';
import CounterfactualProgressBar from '../CounterfactualProgressBar';
import { act } from 'react-dom/test-utils';

describe('CounterfactualProgressBar', () => {
  test('renders a progress bar::60s', () => {
    doTest(60, 5);
  });

  test('renders a progress bar::30s', () => {
    doTest(30, 5);
  });

  function doTest(timeLimit: number, firstAdvance: number) {
    jest.useFakeTimers();

    const wrapper = mount(
      <CounterfactualProgressBar maxRunningTimeSeconds={timeLimit} />
    );

    expect(setInterval).toHaveBeenCalledTimes(1);
    expect(setInterval).toHaveBeenLastCalledWith(expect.any(Function), 1000);
    expect(wrapper.find('Progress').props()['value']).toEqual(0);

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

    const advanceToEnd = timeLimit - firstAdvance;
    act(() => {
      jest.advanceTimersByTime(1000 * advanceToEnd);
    });
    wrapper.update();

    expect(wrapper.find('Progress').props()['value']).toEqual(100);
    expect(wrapper.find('Progress').props()['label']).toMatch(`Wrapping up`);

    jest.useRealTimers();
  }
});
