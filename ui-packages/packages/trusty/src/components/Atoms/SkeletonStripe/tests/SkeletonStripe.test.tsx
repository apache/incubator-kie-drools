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
import SkeletonStripe from '../SkeletonStripe';

describe('SkeletonStripe', () => {
  test('renders a small stripe', () => {
    const wrapper = shallow(<SkeletonStripe />);
    expect(wrapper.find('span').props().className).toMatch('skeleton__stripe');
    expect(wrapper).toMatchSnapshot();
  });

  test('renders a medium size stripe when size "md" is passed', () => {
    const wrapper = shallow(<SkeletonStripe size="md" />);
    expect(wrapper.find('span').props().className).toMatch(
      'skeleton__stripe skeleton__stripe--md'
    );
  });
  test('renders a large size stripe when size "lg" is passed', () => {
    const wrapper = shallow(<SkeletonStripe size="lg" />);
    expect(wrapper.find('span').props().className).toMatch(
      'skeleton__stripe skeleton__stripe--lg'
    );
  });
  test('renders an inline stripe when isInline is passed', () => {
    const wrapper = shallow(<SkeletonStripe isInline />);
    expect(wrapper.find('span').props().className).toMatch(
      'skeleton__stripe skeleton__stripe--inline'
    );
  });
  test('renders an inline stripe with custom styles', () => {
    const wrapper = shallow(<SkeletonStripe customStyle={{ width: 600 }} />);
    expect(wrapper.find('span').props().style).toStrictEqual({ width: 600 });
  });
});
