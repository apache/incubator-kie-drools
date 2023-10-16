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
import { mount, ReactWrapper } from 'enzyme';
import { CFGoal, CFGoalRole } from '../../../../types';
import CounterfactualOutcomeSelection from '../CounterfactualOutcomeSelection';

const goals: CFGoal[] = [
  {
    id: 'goal1',
    name: 'field1',
    role: CFGoalRole.ORIGINAL,
    value: {
      kind: 'UNIT',
      type: 'number',
      value: 100
    },
    originalValue: {
      kind: 'UNIT',
      type: 'number',
      value: 100
    }
  }
];

const onClose = jest.fn();
const verifyConfirmButtonIsDisabled = (
  wrapper: ReactWrapper,
  disabled: boolean
) => {
  expect(
    wrapper
      .find(
        'CounterfactualOutcomeSelection ButtonBase #confirm-outcome-selection'
      )
      .props()['aria-disabled']
  ).toEqual(disabled);
};

describe('CounterfactualOutcomeSelection', () => {
  test('renders correctly', () => {
    const wrapper = mount(
      <CounterfactualOutcomeSelection
        isOpen={true}
        onClose={onClose}
        goals={goals}
      />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test('Set goal value different to original', () => {
    const wrapper = mount(
      <CounterfactualOutcomeSelection
        isOpen={true}
        onClose={onClose}
        goals={goals}
      />
    );

    //Confirmation button is disabled when all Goals equal their original values
    verifyConfirmButtonIsDisabled(wrapper, true);

    wrapper
      .find('CounterfactualOutcomeSelection ButtonBase [aria-label="minus"]')
      .simulate('click');

    wrapper.update();

    //Confirmation button is enabled when a goals is unequal to its original value
    verifyConfirmButtonIsDisabled(wrapper, false);
  });

  test('Restore goal value to equal original value', () => {
    const wrapper = mount(
      <CounterfactualOutcomeSelection
        isOpen={true}
        onClose={onClose}
        goals={goals}
      />
    );

    //Confirmation button is disabled when all Goals equal their original values
    verifyConfirmButtonIsDisabled(wrapper, true);

    wrapper
      .find('CounterfactualOutcomeSelection ButtonBase [aria-label="minus"]')
      .simulate('click');

    wrapper.update();

    //Confirmation button is enabled when a goals is unequal to its original value
    verifyConfirmButtonIsDisabled(wrapper, false);

    wrapper
      .find('CounterfactualOutcomeSelection ButtonBase [aria-label="plus"]')
      .simulate('click');

    wrapper.update();

    //Confirmation button is disabled when a goals value returns to its original value
    verifyConfirmButtonIsDisabled(wrapper, true);
  });
});
