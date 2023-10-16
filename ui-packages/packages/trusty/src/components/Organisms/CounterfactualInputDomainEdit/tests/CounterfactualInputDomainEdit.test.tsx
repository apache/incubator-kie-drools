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
import CounterfactualInputDomainEdit from '../CounterfactualInputDomainEdit';
import { CFSearchInput } from '../../../../types';
import { CFDispatch } from '../../CounterfactualAnalysis/CounterfactualAnalysis';

const input: CFSearchInput = {
  name: 'field',
  value: {
    kind: 'UNIT',
    type: 'number',
    domain: { type: 'RANGE' },
    originalValue: {
      kind: 'UNIT',
      type: 'number',
      value: 123
    }
  }
};

const onClose = jest.fn();
const dispatch = jest.fn();

describe('CounterfactualInputDomainEdit', () => {
  test('renders correctly', () => {
    const wrapper = mount(
      <CounterfactualInputDomainEdit
        input={input}
        inputIndex={0}
        onClose={onClose}
      />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test('set min value', () => {
    const wrapper = mount(
      <CounterfactualInputDomainEdit
        input={input}
        inputIndex={0}
        onClose={onClose}
      />
    );

    setLowerBound('1', wrapper);

    wrapper
      .find('CounterfactualInputDomainEdit ActionListItem:first-child Button')
      .simulate('click');

    wrapper.update();

    expect(
      wrapper.find('CounterfactualNumericalDomainEdit Alert').props()['title']
    ).toBe('Please provide both min and max values');
  });

  test('set max value', () => {
    const wrapper = mount(
      <CounterfactualInputDomainEdit
        input={input}
        inputIndex={0}
        onClose={onClose}
      />
    );

    setUpperBound('10', wrapper);

    wrapper
      .find('CounterfactualInputDomainEdit ActionListItem:first-child Button')
      .simulate('click');

    wrapper.update();

    expect(
      wrapper.find('CounterfactualNumericalDomainEdit Alert').props()['title']
    ).toBe('Please provide both min and max values');
  });

  test('set min and max value', () => {
    const wrapper = mount(
      <CFDispatch.Provider value={dispatch}>
        <CounterfactualInputDomainEdit
          input={input}
          inputIndex={0}
          onClose={onClose}
        />
      </CFDispatch.Provider>
    );

    setLowerBound('1', wrapper);
    setUpperBound('10', wrapper);

    wrapper
      .find('CounterfactualInputDomainEdit ActionListItem:first-child Button')
      .simulate('click');

    expect(dispatch).toBeCalledWith({
      type: 'CF_SET_INPUT_DOMAIN',
      payload: {
        inputIndex: 0,
        domain: { type: 'RANGE', lowerBound: 1, upperBound: 10 }
      }
    });
  });

  test('set min and max value::invalid::min greater than max', () => {
    const wrapper = mount(
      <CFDispatch.Provider value={dispatch}>
        <CounterfactualInputDomainEdit
          input={input}
          inputIndex={0}
          onClose={onClose}
        />
      </CFDispatch.Provider>
    );

    setLowerBound('10', wrapper);
    setUpperBound('1', wrapper);

    wrapper
      .find('CounterfactualInputDomainEdit ActionListItem:first-child Button')
      .simulate('click');

    wrapper.update();

    expect(
      wrapper.find('CounterfactualNumericalDomainEdit Alert').props()['title']
    ).toBe('Minimum value cannot be higher than maximum value');
  });

  test('set min and max value::invalid::min equal to max', () => {
    const wrapper = mount(
      <CFDispatch.Provider value={dispatch}>
        <CounterfactualInputDomainEdit
          input={input}
          inputIndex={0}
          onClose={onClose}
        />
      </CFDispatch.Provider>
    );

    setLowerBound('1', wrapper);
    setUpperBound('1', wrapper);

    wrapper
      .find('CounterfactualInputDomainEdit ActionListItem:first-child Button')
      .simulate('click');

    wrapper.update();

    expect(
      wrapper.find('CounterfactualNumericalDomainEdit Alert').props()['title']
    ).toBe('Minimum value cannot equal maximum value');
  });

  const setLowerBound = (value: string, wrapper) => {
    const element = wrapper
      .find('CounterfactualNumericalDomainEdit SplitItem')
      .at(0)
      .find('input');
    element.getDOMNode().value = value;
    element.simulate('change', value);
  };

  const setUpperBound = (value: string, wrapper) => {
    const element = wrapper
      .find('CounterfactualNumericalDomainEdit SplitItem')
      .at(1)
      .find('input');
    element.getDOMNode().value = value;
    element.simulate('change', value);
  };
});
