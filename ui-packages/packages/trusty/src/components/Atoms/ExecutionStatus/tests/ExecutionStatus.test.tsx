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
import ExecutionStatus from '../ExecutionStatus';
import { shallow } from 'enzyme';

describe('Execution status', () => {
  test('renders a positive outcome', () => {
    const wrapper = shallow(<ExecutionStatus result="success" />);
    const icon = wrapper.find('CheckCircleIcon');

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('.execution-status span').text()).toMatch('Completed');
    expect(icon).toHaveLength(1);
    expect(
      icon.hasClass('execution-status__badge execution-status__badge--success')
    ).toBeTruthy();
  });

  test('renders a negative outcome', () => {
    const wrapper = shallow(<ExecutionStatus result="failure" />);
    const icon = wrapper.find('ErrorCircleOIcon');

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('.execution-status span').text()).toMatch('Error');
    expect(icon).toHaveLength(1);
    expect(
      icon.hasClass('execution-status__badge execution-status__badge--error')
    ).toBeTruthy();
  });
});
