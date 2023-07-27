/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import { shallow } from 'enzyme';
import { ItemDescriptor } from '../ItemDescriptor';

const item1 = {
  id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
  name: 'HotelBooking',
  description: 'T1234HotelBooking01'
};

const item2 = {
  id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
  name: 'HotelBooking'
};

const mockMath = Object.create(global.Math);
mockMath.random = () => 0.5;
global.Math = mockMath;
describe('ItemDescriptor component tests', () => {
  it('snapshot testing for business key available', () => {
    const wrapper = shallow(<ItemDescriptor itemDescription={item1} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing for business key null', () => {
    const wrapper = shallow(<ItemDescriptor itemDescription={item2} />);
    expect(wrapper).toMatchSnapshot();
  });
});
