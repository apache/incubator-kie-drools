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
