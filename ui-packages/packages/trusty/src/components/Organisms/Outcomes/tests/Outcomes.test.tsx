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
import Outcomes from '../Outcomes';
import { mount } from 'enzyme';
import { ItemObjectValue, Outcome } from '../../../../types';

jest.mock('uuid', () => {
  let value = 0;
  return { v4: () => value++ };
});

describe('Outcomes', () => {
  test('renders a list of simple outcomes as cards', () => {
    const wrapper = mount(<Outcomes {...outcomesProps} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('OutcomeCard')).toHaveLength(3);
    expect(wrapper.find('h4.outcome-cards__card__title').at(0).text()).toMatch(
      'Mortgage Approval'
    );
    expect(
      wrapper.find('.outcome__property__value--bigger').at(0).text()
    ).toMatch('true');
    expect(wrapper.find('h4.outcome-cards__card__title').at(1).text()).toMatch(
      'Risk Score'
    );
    expect(
      wrapper.find('.outcome__property__value--bigger').at(1).text()
    ).toMatch('21.7031851958099');
    expect(wrapper.find('h4.outcome-cards__card__title').at(2).text()).toMatch(
      'Client Score'
    );
    expect(
      wrapper.find('.outcome__property__value--bigger').at(2).text()
    ).toMatch('Null');
  });

  test('handles clicks on the explanation link', () => {
    const wrapper = mount(<Outcomes {...outcomesProps} />);

    wrapper
      .find('.outcome-cards__card__explanation-link')
      .at(0)
      .simulate('click');
    expect(outcomesProps.onExplanationClick).toHaveBeenCalledTimes(1);
    expect(outcomesProps.onExplanationClick).toHaveBeenCalledWith(
      '_12268B68-94A1-4960-B4C8-0B6071AFDE58'
    );
  });

  test('renders an outcome with a list of properties', () => {
    const wrapper = mount(<Outcomes {...outcomeMultiplePropertiesProps} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('OutcomeCard')).toHaveLength(1);
    expect(wrapper.find('.outcome-item')).toHaveLength(2);
    expect(wrapper.find('div.outcome__property__name').at(0).text()).toMatch(
      'Auth Code'
    );
    expect(wrapper.find('div.outcome__property__value').at(0).text()).toMatch(
      'Authorized'
    );
    expect(wrapper.find('div.outcome__property__name').at(1).text()).toMatch(
      'Amount'
    );
    expect(wrapper.find('div.outcome__property__value').at(1).text()).toMatch(
      '10000'
    );
  });

  test('renders an outcome with an array of values', () => {
    const wrapper = mount(<Outcomes {...outcomeValuesArrayProps} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('OutcomeCard')).toHaveLength(1);
    expect(wrapper.find('FormattedList')).toHaveLength(1);
    expect(wrapper.find('FormattedValue')).toHaveLength(4);
    expect(wrapper.find('div.outcome__property__name').at(0).text()).toMatch(
      'Result'
    );
  });

  test('renders a recommendation outcome as a list of cards', () => {
    const wrapper = mount(<Outcomes {...outcomesRecommendationProps} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('OutcomeCard')).toHaveLength(2);
    expect(wrapper.find('span.outcome-cards__card__label')).toHaveLength(2);
    expect(
      wrapper.find('span.outcome-cards__card__label').at(0).text()
    ).toMatch(outcomesRecommendationProps.outcomes[0].outcomeName);
  });

  test('renders an outcome with a composed structure', () => {
    const wrapper = mount(<Outcomes {...outcomeComposedProps} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('OutcomeCard')).toHaveLength(1);
    expect(wrapper.find('h4.outcome-cards__card__title').at(0).text()).toMatch(
      outcomeComposedProps.outcomes[0].outcomeName
    );
    expect(wrapper.find('.outcome--struct')).toHaveLength(4);
    expect(wrapper.find('.outcome-item')).toHaveLength(10);
    expect(wrapper.find('.outcome__title--struct')).toHaveLength(4);
    expect(wrapper.find('.outcome__title--struct').at(0).text()).toMatch(
      'Client Ratings'
    );
    expect(wrapper.find('.outcome__title--struct').at(1).text()).toMatch(
      'Rating Type A'
    );
    expect(wrapper.find('.outcome__title--struct').at(2).text()).toMatch(
      'Rating Type B'
    );
    expect(wrapper.find('.outcome__title--struct').at(3).text()).toMatch(
      'Sub-Rating Type C'
    );
    expect(wrapper.find('div.outcome__property__name').at(0).text()).toMatch(
      'Loan Amount'
    );
    expect(wrapper.find('div.outcome__property__value').at(0).text()).toMatch(
      '540000'
    );
    expect(wrapper.find('div.outcome__property__name').at(1).text()).toMatch(
      'Repayment Rate'
    );
    expect(wrapper.find('div.outcome__property__value').at(1).text()).toMatch(
      '900'
    );
    expect(wrapper.find('div.outcome__property__name').at(2).text()).toMatch(
      'Loan Eligibility'
    );
    expect(wrapper.find('div.outcome__property__value').at(2).text()).toMatch(
      'true'
    );
    expect(wrapper.find('div.outcome__property__name').at(3).text()).toMatch(
      'Loan amount'
    );
    expect(wrapper.find('div.outcome__property__value').at(3).text()).toMatch(
      '340000'
    );
    expect(wrapper.find('div.outcome__property__name').at(4).text()).toMatch(
      'Repayment rate'
    );
    expect(wrapper.find('div.outcome__property__value').at(4).text()).toMatch(
      '2000'
    );
    expect(wrapper.find('div.outcome__property__name').at(5).text()).toMatch(
      'Loan amount'
    );
    expect(wrapper.find('div.outcome__property__value').at(5).text()).toMatch(
      '390000'
    );
    expect(wrapper.find('div.outcome__property__name').at(6).text()).toMatch(
      'Repayment rate'
    );
    expect(wrapper.find('div.outcome__property__value').at(6).text()).toMatch(
      '5000'
    );
  });

  test('renders a skipped decision with no outcome', () => {
    const wrapper = mount(<Outcomes {...outcomeSkippedProps} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('OutcomeCard')).toHaveLength(1);
    expect(wrapper.find('EvaluationStatus')).toHaveLength(1);
    expect(wrapper.find('EvaluationStatus').prop('status')).toMatch('SKIPPED');
  });

  test('renders a single outcome without the list view', () => {
    const wrapper = mount(<Outcomes {...outcomeDetailProps} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('OutcomeCard')).toHaveLength(0);
    expect(wrapper.find('LightCard')).toHaveLength(1);
    expect(wrapper.find('OutcomeProperty')).toHaveLength(1);
    expect(wrapper.find('div.outcome__property__name').text()).toMatch(
      'Mortgage Approval'
    );
    expect(wrapper.find('div.outcome__property__value').text()).toMatch('true');
  });
});

const outcomesProps = {
  outcomes: [
    {
      outcomeId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
      outcomeName: 'Mortgage Approval',
      evaluationStatus: 'SUCCEEDED',
      outcomeResult: {
        kind: 'UNIT',
        type: 'boolean',
        value: true
      },
      messages: [],
      hasErrors: false
    },
    {
      outcomeId: '_9CFF8C35-4EB3-451E-874C-DB27A5A424C0',
      outcomeName: 'Risk Score',
      evaluationStatus: 'SUCCEEDED',
      outcomeResult: {
        kind: 'UNIT',
        type: 'number',
        value: 21.7031851958099
      },
      messages: [],
      hasErrors: false
    },
    {
      outcomeId: '_d361c79e-8c06-4504-bdb2-d6b90b915166',
      outcomeName: 'Client Score',
      evaluationStatus: 'SUCCEEDED',
      outcomeResult: {
        kind: 'UNIT',
        type: 'number',
        value: null
      },
      messages: [],
      hasErrors: false
    }
  ] as Outcome[],
  onExplanationClick: jest.fn(),
  listView: true
};

const outcomesRecommendationProps = {
  outcomes: [
    {
      outcomeId: '432343443',
      outcomeName: 'Recommended Loan Products',
      evaluationStatus: 'SUCCEEDED',
      hasErrors: false,
      messages: [],
      outcomeResult: {
        kind: 'COLLECTION',
        type: 'tProducts',
        value: [
          {
            kind: 'STRUCTURE',
            type: 'tProduct',
            value: {
              Product: {
                kind: 'UNIT',
                type: 'string',
                value: 'Lender B - ARM5/1-Standard'
              },
              Recommendation: {
                kind: 'UNIT',
                type: 'string',
                value: 'Good'
              },
              'Note Amount': {
                kind: 'UNIT',
                type: 'string',
                value: '$273,775.90'
              },
              'Interest Rate': {
                kind: 'UNIT',
                type: 'string',
                value: '3.8'
              },
              'Monthly Payment': {
                kind: 'UNIT',
                type: 'string',
                value: '$1,267.90'
              },
              'Cash to Close': {
                kind: 'UNIT',
                type: 'string',
                value: '$1,267.90'
              },
              'Required Credit Score': {
                kind: 'UNIT',
                type: 'number',
                value: 720
              }
            }
          },
          {
            kind: 'STRUCTURE',
            type: 'tProduct',
            value: {
              Product: {
                kind: 'UNIT',
                type: 'string',
                value: 'Lender C - Fixed30-Standard'
              }
            },
            Recommendation: {
              kind: 'UNIT',
              type: 'string',
              value: 'Best'
            },
            'Note Amount': {
              kind: 'UNIT',
              type: 'string',
              value: '$274,599.40',
              components: null
            },
            'Interest Rate': {
              kind: 'UNIT',
              type: 'string',
              value: '3.88'
            },
            'Monthly Payment': {
              kind: 'UNIT',
              type: 'string',
              value: '$1,291.27'
            },
            'Cash to Close': {
              kind: 'UNIT',
              type: 'string',
              value: '$75,491.99'
            },
            'Required Credit Score': {
              kind: 'UNIT',
              type: 'number',
              value: 680
            }
          }
        ]
      }
    }
  ] as Outcome[],
  onExplanationClick: jest.fn(),
  listView: true
};

const outcomeComposedProps = {
  outcomes: [
    {
      outcomeId: '849849489',
      outcomeName: 'Client Ratings',
      evaluationStatus: 'SUCCEEDED',
      hasErrors: false,
      messages: [],
      outcomeResult: {
        kind: 'STRUCTURE',
        type: 'tRatings',
        value: {
          'Rating Type A': {
            kind: 'STRUCTURE',
            type: 'tRating',
            value: {
              'Loan Amount': {
                kind: 'UNIT',
                type: 'number',
                value: 540000
              },
              'Repayment Rate': {
                kind: 'UNIT',
                type: 'number',
                value: 900
              },
              'Loan Eligibility': {
                kind: 'UNIT',
                type: 'boolean',
                value: true
              }
            }
          },
          'Rating Type B': {
            kind: 'STRUCTURE',
            type: 'tRating',
            value: {
              'Loan amount': {
                kind: 'UNIT',
                type: 'number',
                value: 340000
              },
              'Repayment rate': {
                kind: 'UNIT',
                type: 'number',
                value: 2000
              },
              'Sub-Rating Type C': {
                kind: 'STRUCTURE',
                type: 'tRating',
                value: {
                  'Loan amount': {
                    kind: 'UNIT',
                    type: 'number',
                    value: 390000
                  },
                  'Repayment rate': {
                    kind: 'UNIT',
                    type: 'number',
                    value: 5000
                  }
                }
              }
            }
          }
        }
      }
    }
  ] as Outcome[],
  onExplanationClick: jest.fn(),
  listView: true
};

const outcomeSkippedProps = {
  outcomes: [
    {
      outcomeId: '_1CFF8C35-4EB2-351E-874C-DB27A2A424C0',
      outcomeName: 'Bank Score',
      evaluationStatus: 'SKIPPED',
      outcomeResult: {} as ItemObjectValue,
      messages: [],
      hasErrors: false
    } as Outcome
  ],
  onExplanationClick: jest.fn(),
  listView: true
};

const outcomeMultiplePropertiesProps = {
  outcomes: [
    {
      outcomeId: '_d361c79e-8c06-4504-bdb2-d6b90b915166',
      outcomeName: 'Last Transaction',
      evaluationStatus: 'SUCCEEDED',
      hasErrors: false,
      messages: [],
      outcomeResult: {
        kind: 'STRUCTURE',
        type: 'tTransaction',
        value: {
          'Auth Code': {
            kind: 'UNIT',
            type: 'tAuthCode',
            value: 'Authorized'
          },
          Amount: {
            kind: 'UNIT',
            type: 'number',
            value: 10000
          }
        }
      }
    }
  ] as Outcome[],
  onExplanationClick: jest.fn(),
  listView: true
};

const outcomeValuesArrayProps = {
  outcomes: [
    {
      outcomeId: '_ff34378e-fe90-4c58-9f7f-b9ce5767a415',
      outcomeName: 'Merchant Blacklist',
      evaluationStatus: 'SUCCEEDED',
      messages: [],
      hasErrors: false,
      outcomeResult: {
        kind: 'UNIT',
        type: 'string',
        value: ['ILLICITCORP', 'SLIMSHADY', 'TAINTEDTHINGS']
      }
    }
  ] as Outcome[],
  onExplanationClick: jest.fn(),
  listView: true
};

const outcomeDetailProps = {
  outcomes: [
    {
      outcomeId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
      outcomeName: 'Mortgage Approval',
      evaluationStatus: 'SUCCEEDED',
      outcomeResult: {
        kind: 'UNIT',
        type: 'boolean',
        value: true
      },
      messages: [],
      hasErrors: false
    }
  ] as Outcome[]
};
