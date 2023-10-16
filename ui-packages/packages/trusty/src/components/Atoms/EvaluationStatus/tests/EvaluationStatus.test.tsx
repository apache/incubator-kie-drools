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
