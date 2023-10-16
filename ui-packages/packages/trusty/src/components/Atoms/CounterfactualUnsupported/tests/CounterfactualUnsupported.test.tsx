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
import CounterfactualUnsupported from '../CounterfactualUnsupported';
import { CFSupportMessage } from '../../../../types';

describe('CounterfactualUnsupported status', () => {
  test('renders messages', () => {
    const messages: CFSupportMessage[] = [
      { id: 'msg1', message: 'message1' },
      { id: 'msg2', message: 'message2' }
    ];

    const wrapper = shallow(<CounterfactualUnsupported messages={messages} />);

    expect(wrapper).toMatchSnapshot();

    const items = wrapper.find('EmptyStateBody p');
    expect(items.length).toBe(2);
    expect(items.get(0).props['data-ouia-component-id']).toBe('msg1');
    expect(items.get(1).props['data-ouia-component-id']).toBe('msg2');
  });
});
