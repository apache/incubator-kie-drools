import React from 'react';
import { shallow } from 'enzyme';
import EvaluationStatus from '../EvaluationStatus';
import { evaluationStatus } from '../../../../types';

describe('Evaluation status', () => {
  test('renders an evaluating status', () => {
    const status = 'EVALUATING';
    const wrapper = shallow(<EvaluationStatus status={status} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('span').text()).toMatch(evaluationStatus[status]);
  });

  test('renders a failed status', () => {
    const status = 'FAILED';
    const wrapper = shallow(<EvaluationStatus status={status} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('span').text()).toMatch(evaluationStatus[status]);
  });

  test('renders a skipped status', () => {
    const status = 'SKIPPED';
    const wrapper = shallow(<EvaluationStatus status={status} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('span').text()).toMatch(evaluationStatus[status]);
  });

  test('renders a not evaluated status', () => {
    const status = 'NOT_EVALUATED';
    const wrapper = shallow(<EvaluationStatus status={status} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('span').text()).toMatch(evaluationStatus[status]);
  });

  test('renders a succeeded status', () => {
    const status = 'SUCCEEDED';
    const wrapper = shallow(<EvaluationStatus status={status} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('span').text()).toMatch(evaluationStatus[status]);
  });
});
