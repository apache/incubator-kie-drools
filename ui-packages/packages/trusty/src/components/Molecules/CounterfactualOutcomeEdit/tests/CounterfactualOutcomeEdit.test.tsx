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
