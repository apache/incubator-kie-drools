import React from 'react';
import Outcomes from '../Outcomes';
import { mount } from 'enzyme';
import { Outcome, ItemObject } from '../../../../types';

jest.mock('uuid', () => {
  let value = 0;
  return { v4: () => value++ };
});

describe('Outcomes', () => {
  test('renders a list of simple outcomes as cards', () => {
    const wrapper = mount(<Outcomes {...outcomesProps} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('OutcomeCard')).toHaveLength(3);
    expect(
      wrapper
        .find('h4.outcome-cards__card__title')
        .at(0)
        .text()
    ).toMatch('Mortgage Approval');
    expect(
      wrapper
        .find('.outcome__property__value--bigger')
        .at(0)
        .text()
    ).toMatch('true');
    expect(
      wrapper
        .find('h4.outcome-cards__card__title')
        .at(1)
        .text()
    ).toMatch('Risk Score');
    expect(
      wrapper
        .find('.outcome__property__value--bigger')
        .at(1)
        .text()
    ).toMatch('21.7031851958099');
    expect(
      wrapper
        .find('h4.outcome-cards__card__title')
        .at(2)
        .text()
    ).toMatch('Client Score');
    expect(
      wrapper
        .find('.outcome__property__value--bigger')
        .at(2)
        .text()
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
    expect(
      wrapper
        .find('div.outcome__property__name')
        .at(0)
        .text()
    ).toMatch('Auth Code');
    expect(
      wrapper
        .find('div.outcome__property__value')
        .at(0)
        .text()
    ).toMatch('Authorized');
    expect(
      wrapper
        .find('div.outcome__property__name')
        .at(1)
        .text()
    ).toMatch('Amount');
    expect(
      wrapper
        .find('div.outcome__property__value')
        .at(1)
        .text()
    ).toMatch('10000');
  });

  test('renders an outcome with an array of values', () => {
    const wrapper = mount(<Outcomes {...outcomeValuesArrayProps} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('OutcomeCard')).toHaveLength(1);
    expect(wrapper.find('FormattedList')).toHaveLength(1);
    expect(wrapper.find('FormattedValue')).toHaveLength(4);
    expect(
      wrapper
        .find('div.outcome__property__name')
        .at(0)
        .text()
    ).toMatch('Result');
  });

  test('renders a recommendation outcome as a list of cards', () => {
    const wrapper = mount(<Outcomes {...outcomesRecommendationProps} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('OutcomeCard')).toHaveLength(2);
    expect(wrapper.find('span.outcome-cards__card__label')).toHaveLength(2);
    expect(
      wrapper
        .find('span.outcome-cards__card__label')
        .at(0)
        .text()
    ).toMatch(outcomesRecommendationProps.outcomes[0].outcomeName);
  });

  test('renders an outcome with a composed structure', () => {
    const wrapper = mount(<Outcomes {...outcomeComposedProps} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('OutcomeCard')).toHaveLength(1);
    expect(
      wrapper
        .find('h4.outcome-cards__card__title')
        .at(0)
        .text()
    ).toMatch(outcomeComposedProps.outcomes[0].outcomeName);
    expect(wrapper.find('.outcome--struct')).toHaveLength(4);
    expect(wrapper.find('.outcome-item')).toHaveLength(10);
    expect(wrapper.find('.outcome__title--struct')).toHaveLength(4);
    expect(
      wrapper
        .find('.outcome__title--struct')
        .at(0)
        .text()
    ).toMatch('Client Ratings');
    expect(
      wrapper
        .find('.outcome__title--struct')
        .at(1)
        .text()
    ).toMatch('Rating Type A');
    expect(
      wrapper
        .find('.outcome__title--struct')
        .at(2)
        .text()
    ).toMatch('Rating Type B');
    expect(
      wrapper
        .find('.outcome__title--struct')
        .at(3)
        .text()
    ).toMatch('Sub-Rating Type C');
    expect(
      wrapper
        .find('div.outcome__property__name')
        .at(0)
        .text()
    ).toMatch('Loan Amount');
    expect(
      wrapper
        .find('div.outcome__property__value')
        .at(0)
        .text()
    ).toMatch('540000');
    expect(
      wrapper
        .find('div.outcome__property__name')
        .at(1)
        .text()
    ).toMatch('Repayment Rate');
    expect(
      wrapper
        .find('div.outcome__property__value')
        .at(1)
        .text()
    ).toMatch('900');
    expect(
      wrapper
        .find('div.outcome__property__name')
        .at(2)
        .text()
    ).toMatch('Loan Eligibility');
    expect(
      wrapper
        .find('div.outcome__property__value')
        .at(2)
        .text()
    ).toMatch('true');
    expect(
      wrapper
        .find('div.outcome__property__name')
        .at(3)
        .text()
    ).toMatch('Loan amount');
    expect(
      wrapper
        .find('div.outcome__property__value')
        .at(3)
        .text()
    ).toMatch('340000');
    expect(
      wrapper
        .find('div.outcome__property__name')
        .at(4)
        .text()
    ).toMatch('Repayment rate');
    expect(
      wrapper
        .find('div.outcome__property__value')
        .at(4)
        .text()
    ).toMatch('2000');
    expect(
      wrapper
        .find('div.outcome__property__name')
        .at(5)
        .text()
    ).toMatch('Loan amount');
    expect(
      wrapper
        .find('div.outcome__property__value')
        .at(5)
        .text()
    ).toMatch('390000');
    expect(
      wrapper
        .find('div.outcome__property__name')
        .at(6)
        .text()
    ).toMatch('Repayment rate');
    expect(
      wrapper
        .find('div.outcome__property__value')
        .at(6)
        .text()
    ).toMatch('5000');
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
        name: 'Mortgage Approval',
        typeRef: 'boolean',
        value: true,
        components: null
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
        typeRef: 'number',
        value: 21.7031851958099,
        components: null
      },
      messages: [],
      hasErrors: false
    },
    {
      outcomeId: '_d361c79e-8c06-4504-bdb2-d6b90b915166',
      outcomeName: 'Client Score',
      evaluationStatus: 'SUCCEEDED',
      outcomeResult: {
        name: 'Client Score',
        typeRef: 'number',
        value: null,
        components: null
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
        name: 'Recommended Loan Products',
        typeRef: 'tProducts',
        value: null,
        components: [
          [
            {
              name: 'Product',
              value: 'Lender B - ARM5/1-Standard',
              typeRef: 'string',
              components: null
            },
            {
              name: 'Recommendation',
              value: 'Good',
              typeRef: 'string',
              components: null
            },
            {
              name: 'Note Amount',
              value: '$273,775.90',
              typeRef: 'string',
              components: null
            },
            {
              name: 'Interest Rate',
              value: '3.8',
              typeRef: 'string',
              components: null
            },
            {
              name: 'Monthly Payment',
              value: '$1,267.90',
              typeRef: 'string',
              components: null
            },
            {
              name: 'Cash to Close',
              value: '$1,267.90',
              typeRef: 'string',
              components: null
            },
            {
              name: 'Required Credit Score',
              value: 720,
              typeRef: 'number',
              components: null
            }
          ],
          [
            {
              name: 'Product',
              value: 'Lender C - Fixed30-Standard',
              typeRef: 'string',
              components: null
            },
            {
              name: 'Recommendation',
              value: 'Best',
              typeRef: 'string',
              components: null
            },
            {
              name: 'Note Amount',
              value: '$274,599.40',
              typeRef: 'string',
              components: null
            },
            {
              name: 'Interest Rate',
              value: '3.88',
              typeRef: 'string',
              components: null
            },
            {
              name: 'Monthly Payment',
              value: '$1,291.27',
              typeRef: 'string',
              components: null
            },
            {
              name: 'Cash to Close',
              value: '$75,491.99',
              typeRef: 'string',
              components: null
            },
            {
              name: 'Required Credit Score',
              value: 680,
              typeRef: 'number',
              components: null
            }
          ]
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
        name: 'Client Ratings',
        typeRef: 'tProducts',
        value: null,
        components: [
          {
            name: 'Rating Type A',
            value: null,
            typeRef: 'string',
            components: [
              {
                name: 'Loan Amount',
                value: 540000,
                typeRef: 'number',
                components: null
              },
              {
                name: 'Repayment Rate',
                value: 900,
                typeRef: 'number',
                components: null
              },
              {
                name: 'Loan Eligibility',
                value: true,
                typeRef: 'boolean',
                components: null
              }
            ]
          },
          {
            name: 'Rating Type B',
            value: null,
            typeRef: 'number',
            components: [
              {
                name: 'Loan amount',
                value: 340000,
                typeRef: 'number',
                components: null
              },
              {
                name: 'Repayment rate',
                value: 2000,
                typeRef: 'number',
                components: null
              },
              {
                name: 'Sub-Rating Type C',
                value: null,
                typeRef: 'number',
                components: [
                  {
                    name: 'Loan amount',
                    value: 390000,
                    typeRef: 'number',
                    components: null
                  },
                  {
                    name: 'Repayment rate',
                    value: 5000,
                    typeRef: 'number',
                    components: null
                  }
                ]
              }
            ]
          }
        ]
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
      outcomeResult: {} as ItemObject,
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
        name: 'Last Transaction',
        typeRef: 'tTransaction',
        value: null,
        components: [
          {
            name: 'Auth Code',
            typeRef: 'tAuthCode',
            value: 'Authorized',
            components: null
          },
          { name: 'Amount', typeRef: 'number', value: 10000, components: null }
        ]
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
        name: 'Merchant Blacklist',
        typeRef: 'string',
        value: ['ILLICITCORP', 'SLIMSHADY', 'TAINTEDTHINGS'],
        components: null
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
        name: 'Mortgage Approval',
        typeRef: 'boolean',
        value: true,
        components: null
      },
      messages: [],
      hasErrors: false
    }
  ] as Outcome[]
};
