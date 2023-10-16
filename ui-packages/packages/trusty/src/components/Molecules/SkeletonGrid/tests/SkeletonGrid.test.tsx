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
import SkeletonGrid from '../SkeletonGrid';
import { shallow } from 'enzyme';

describe('SkeletonGrid', () => {
  test('renders a 2x2 grid', () => {
    const wrapper = shallow(<SkeletonGrid rowsCount={2} colsDefinition={2} />);

    expect(wrapper).toMatchSnapshot();
  });
  test('renders a grid with custom columns sizes', () => {
    const wrapper = shallow(
      <SkeletonGrid rowsCount={2} colsDefinition={[1, 2, 1]} />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
