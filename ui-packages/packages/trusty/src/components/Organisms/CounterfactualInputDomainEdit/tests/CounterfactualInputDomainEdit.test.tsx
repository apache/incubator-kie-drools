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
