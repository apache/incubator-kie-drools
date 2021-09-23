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
    kind: 'UNIT',
    typeRef: 'number',
    value: 1,
    originalValue: 0,
    role: CFGoalRole.FIXED
  },
  {
    id: '1002',
    name: 'Approval',
    kind: 'UNIT',
    typeRef: 'boolean',
    value: true,
    originalValue: false,
    role: CFGoalRole.FIXED
  },
  {
    id: '1003',
    name: 'Risk',
    kind: 'UNIT',
    typeRef: 'number',
    value: 33,
    originalValue: 33,
    role: CFGoalRole.ORIGINAL
  }
];

const noGoals: CFGoal[] = [
  {
    id: '1003',
    name: 'Risk',
    kind: 'UNIT',
    typeRef: 'number',
    value: 33,
    originalValue: 33,
    role: CFGoalRole.ORIGINAL
  }
];
