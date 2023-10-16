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
import CounterfactualOutcomesSelected from '../CounterfactualOutcomesSelected';
import { CFGoal, CFGoalRole } from '../../../../types';

describe('CounterfactualOutcomesSelected', () => {
  test('renders correctly', () => {
    const wrapper = shallow(<CounterfactualOutcomesSelected goals={goals} />);

    expect(wrapper).toMatchSnapshot();
  });

  test('displays a list of changed outcomes', () => {
    const wrapper = shallow(<CounterfactualOutcomesSelected goals={goals} />);
    const listItems = wrapper.find('ListItem > span');
    expect(listItems).toHaveLength(3);
    expect(listItems.first().text()).toMatch('Selected Outcomes');
    expect(listItems.at(1).text()).toMatch('Score: 1,');
    expect(listItems.at(2).text()).toMatch('Approval: true');
  });

  test('displays nothing if no changed outcomes are provided', () => {
    const wrapper = shallow(<CounterfactualOutcomesSelected goals={noGoals} />);
    expect(wrapper.find('ListItem > span')).toHaveLength(0);
  });

  test('displays nothing if no outcomes are provided', () => {
    const wrapper = shallow(<CounterfactualOutcomesSelected goals={[]} />);
    expect(wrapper.find('ListItem > span')).toHaveLength(0);
  });
});

const goals: CFGoal[] = [
  {
    id: '1001',
    name: 'Score',
    role: CFGoalRole.FIXED,
    value: {
      kind: 'UNIT',
      type: 'number',
      value: 1
    },
    originalValue: {
      kind: 'UNIT',
      type: 'number',
      value: 0
    }
  },
  {
    id: '1002',
    name: 'Approval',
    role: CFGoalRole.FIXED,
    value: {
      kind: 'UNIT',
      type: 'boolean',
      value: true
    },
    originalValue: {
      kind: 'UNIT',
      type: 'boolean',
      value: false
    }
  },
  {
    id: '1003',
    name: 'Risk',
    role: CFGoalRole.ORIGINAL,
    value: {
      kind: 'UNIT',
      type: 'number',
      value: 33
    },
    originalValue: {
      kind: 'UNIT',
      type: 'number',
      value: 33
    }
  }
];

const noGoals: CFGoal[] = [
  {
    id: '1003',
    name: 'Risk',
    role: CFGoalRole.ORIGINAL,
    value: {
      kind: 'UNIT',
      type: 'number',
      value: 33
    },
    originalValue: {
      kind: 'UNIT',
      type: 'number',
      value: 33
    }
  }
];
