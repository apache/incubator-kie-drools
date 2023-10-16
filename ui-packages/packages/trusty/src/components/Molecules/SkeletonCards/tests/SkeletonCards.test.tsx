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
import SkeletonCards from '../SkeletonCards';
import { shallow } from 'enzyme';

jest.mock('uuid', () => {
  let value = 0;
  return { v4: () => value++ };
});

describe('SkeletonCards', () => {
  test('renders a list of cards', () => {
    const wrapper = shallow(<SkeletonCards quantity={3} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('.skeleton-cards')).toHaveLength(1);
    expect(wrapper.find('.skeleton-cards__card')).toHaveLength(3);
    expect(
      wrapper.find('.skeleton-cards__card').at(0).find('SkeletonStripe')
    ).toHaveLength(2);
    expect(
      wrapper.find('.skeleton-cards__card').at(1).find('SkeletonStripe')
    ).toHaveLength(2);
  });
});
