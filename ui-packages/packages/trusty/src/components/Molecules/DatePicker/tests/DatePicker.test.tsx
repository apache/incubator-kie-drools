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
