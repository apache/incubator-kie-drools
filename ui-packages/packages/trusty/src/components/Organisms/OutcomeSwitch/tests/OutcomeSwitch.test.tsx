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
import { mount, shallow } from 'enzyme';
import { Outcome } from '../../../../types';
import OutcomeSwitch from '../OutcomeSwitch';

describe('ExplanationSwitch', () => {
  test('renders correctly', () => {
    const wrapper = shallow(<OutcomeSwitch {...props} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('SelectOption')).toHaveLength(2);
  });

  test('handles explanation selection', () => {
    const wrapper = mount(<OutcomeSwitch {...props} />);

    expect(wrapper.find('Select').prop('selections')).toMatch(
      props.currentExplanationId
    );

    wrapper.find('SelectToggle').simulate('click');

    wrapper
      .find(
        'SelectOption[value="_9CFF8C35-4EB3-451E-874C-DB27A5A424C0"] button'
      )
      .simulate('click');

    expect(wrapper.find('Select').prop('selections')).toMatch(
      '_9CFF8C35-4EB3-451E-874C-DB27A5A424C0'
    );
    expect(props.onDecisionSelection).toHaveBeenCalledTimes(1);
    expect(props.onDecisionSelection).toHaveBeenCalledWith(
      '_9CFF8C35-4EB3-451E-874C-DB27A5A424C0'
    );
  });
});

const props = {
  outcomesList: [
    {
      outcomeId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
      outcomeName: 'Mortgage Approval',
      evaluationStatus: 'SUCCEEDED',
      outcomeResult: {
        name: 'Mortgage Approval',
        type: 'boolean',
        value: true,
        components: []
      },
      messages: [],
      hasErrors: false
    },
    {
      outcomeId: '_9CFF8C35-4EB3-451E-874C-DB27A5A424C0',
      outcomeName: 'Risk Score',
      evaluationStatus: 'SUCCEEDED',
      outcomeResult: {
        name: 'Risk Score',
        type: 'number',
        value: 21.7031851958099,
        components: []
      },
      messages: [],
      hasErrors: false
    }
  ] as Outcome[],
  onDecisionSelection: jest.fn(),
  currentExplanationId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58'
};
