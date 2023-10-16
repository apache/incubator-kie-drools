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
import skeletonRows from '../skeletonRows';
import { shallow, mount } from 'enzyme';
import SkeletonStripe from '../../../components/Atoms/SkeletonStripe/SkeletonStripe';
import { Table, TableBody, TableHeader } from '@patternfly/react-table';

describe('skeletonRows', () => {
  test('returns an array of skeleton rows with the provided amount of rows and cols', () => {
    const rows = skeletonRows(2, 2);
    expect(rows).toHaveLength(2);
    expect(rows[0].cells).toHaveLength(2);
    expect(rows[0].key).toMatch('skeleton-0');
  });

  test('alternates medium and large stripes for each row', () => {
    const rows = skeletonRows(2, 2);
    const firstCell = shallow(rows[0].cells[0].title);
    const secondCell = shallow(rows[0].cells[1].title);
    const thirdCell = shallow(rows[1].cells[0].title);
    const fourthCell = shallow(rows[1].cells[1].title);

    expect(firstCell.find('span').props().className).toMatch(
      'skeleton__stripe skeleton__stripe--md'
    );
    expect(secondCell.find('span').props().className).toMatch(
      'skeleton__stripe skeleton__stripe--lg'
    );
    expect(thirdCell.find('span').props().className).toMatch(
      'skeleton__stripe skeleton__stripe--lg'
    );
    expect(fourthCell.find('span').props().className).toMatch(
      'skeleton__stripe skeleton__stripe--md'
    );
  });

  test('supports custom keys', () => {
    const rows = skeletonRows(2, 2, 'customKey');
    expect(rows[0].customKey).toMatch('skeleton-0');
  });

  test('renders correctly inside a Patternfly table', () => {
    const cols = ['column one', 'column two'];
    const rows = skeletonRows(2, 2);
    const wrapper = mount(
      <Table aria-label="Table Test" cells={cols} rows={rows}>
        <TableHeader />
        <TableBody />
      </Table>
    );
    expect(wrapper.find('tbody tr')).toHaveLength(2);
    expect(wrapper.find('tbody td')).toHaveLength(4);
    expect(wrapper.find('tbody td').find(SkeletonStripe)).toHaveLength(4);
  });
});
