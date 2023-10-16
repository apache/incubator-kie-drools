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
