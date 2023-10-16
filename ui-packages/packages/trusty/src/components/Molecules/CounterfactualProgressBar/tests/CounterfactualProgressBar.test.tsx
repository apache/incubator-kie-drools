/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
