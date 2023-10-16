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
import { mount } from 'enzyme';
import { CFGoal, CFGoalRole } from '../../../../types';
import CounterfactualOutcomeEdit from '../CounterfactualOutcomeEdit';

const onUpdateGoal = jest.fn();

describe('CounterfactualOutcomeEdit', () => {
  test('renders an number input for numerical goals', () => {
    const wrapper = mount(
      <CounterfactualOutcomeEdit
        goal={goals[0]}
        index={0}
        onUpdateGoal={onUpdateGoal}
      />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test('updates a goal value using the minus stepper button', () => {
    const wrapper = mount(
      <CounterfactualOutcomeEdit
        goal={goals[0]}
        index={0}
        onUpdateGoal={onUpdateGoal}
      />
    );

    wrapper
      .find('CounterfactualOutcomeEdit ButtonBase [aria-label="minus"]')
      .simulate('click');

    expect(onUpdateGoal).toBeCalledWith({
      ...goals[0],
      value: {
        ...goals[0].value,
        value: 0.65
      }
    });
  });
  test('updates a goal value using the plus stepper button', () => {
    const wrapper = mount(
      <CounterfactualOutcomeEdit
        goal={goals[1]}
        index={1}
        onUpdateGoal={onUpdateGoal}
      />
    );

    wrapper
      .find('CounterfactualOutcomeEdit ButtonBase [aria-label="plus"]')
      .simulate('click');

    expect(onUpdateGoal).toBeCalledWith({
      ...goals[1],
      value: {
        ...goals[1].value,
        value: 2.03
      }
    });
  });
  test('updates a goal with an integer value', () => {
    const wrapper = mount(
      <CounterfactualOutcomeEdit
        goal={goals[2]}
        index={2}
        onUpdateGoal={onUpdateGoal}
      />
    );

    wrapper
      .find('CounterfactualOutcomeEdit ButtonBase [aria-label="plus"]')
      .simulate('click');

    expect(onUpdateGoal).toBeCalledWith({
      ...goals[2],
      value: {
        ...goals[2].value,
        value: 6
      }
    });
  });
});

const goals: CFGoal[] = [
  {
    id: 'goal1',
    name: 'field1',
    role: CFGoalRole.ORIGINAL,
    value: {
      kind: 'UNIT',
      type: 'number',
      value: 1.65
    },
    originalValue: {
      kind: 'UNIT',
      type: 'number',
      value: 1.65
    }
  },
  {
    id: 'goal1',
    name: 'field1',
    role: CFGoalRole.ORIGINAL,
    value: {
      kind: 'UNIT',
      type: 'number',
      value: 1.03
    },
    originalValue: {
      kind: 'UNIT',
      type: 'number',
      value: 1.03
    }
  },
  {
    id: 'goal3',
    name: 'field3',
    role: CFGoalRole.ORIGINAL,
    value: {
      kind: 'UNIT',
      type: 'number',
      value: 5
    },
    originalValue: {
      kind: 'UNIT',
      type: 'number',
      value: 5
    }
  }
];
