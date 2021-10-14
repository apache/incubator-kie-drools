const executionIds = require('./executionIds');

const twoSimpleOutcomes = [
  {
    outcomeId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
    outcomeName: 'Mortgage Approval',
    evaluationStatus: 'SUCCEEDED',
    outcomeResult: {
      name: 'Mortgage Approval',
      typeRef: 'boolean',
      kind: 'UNIT',
      value: null,
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
      kind: 'UNIT',
      value: 21.7031851958099,
      components: null
    },
    messages: [],
    hasErrors: false
  }
];

const structuredOutcomes = [
  {
    outcomeId: '_c6e56793-68d0-4683-b34b-5e9d69e7d0d4',
    outcomeName: 'Structured outcome 1',
    evaluationStatus: 'SUCCEEDED',
    outcomeResult: {
      name: 'Structure1',
      typeRef: 'tStructure',
      kind: 'STRUCTURE',
      value: null,
      components: [
        {
          name: 'Structure1 field1',
          typeRef: 'tField1',
          kind: 'UNIT',
          value: 'value',
          components: null
        }
      ]
    },
    messages: [],
    hasErrors: false
  },
  {
    outcomeId: '_859bea4f-dfc4-480e-96f2-1a756d54b84b',
    outcomeName: 'Structured outcome 2',
    evaluationStatus: 'SUCCEEDED',
    outcomeResult: {
      name: 'Structure2',
      typeRef: 'tStructure',
      kind: 'STRUCTURE',
      value: null,
      components: [
        {
          name: 'Structure2 field1',
          typeRef: 'tField1',
          kind: 'UNIT',
          value: 'value',
          components: null
        }
      ]
    },
    messages: [],
    hasErrors: false
  }
];

const outcomes = [
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIds[0],
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: [
      {
        outcomeId: '_3QDC8C35-4EB3-451E-874C-DB27A5D6T8Q2',
        outcomeName: 'Recommended Loan Products',
        evaluationStatus: 'SUCCEEDED',
        hasErrors: false,
        messages: [],
        outcomeResult: {
          name: 'Recommended Loan Products',
          type: 'tProducts',
          kind: 'STRUCTURE',
          value: null,
          components: [
            [
              {
                name: 'Product',
                typeRef: 'string',
                kind: 'UNIT',
                value: 'Lender B - ARM5/1-Standard',
                components: null
              },
              {
                name: 'Recommendation',
                typeRef: 'string',
                kind: 'UNIT',
                value: 'Good',
                components: null
              },
              {
                name: 'Note Amount',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$273,775.90',
                components: null
              },
              {
                name: 'Interest Rate',
                typeRef: 'string',
                kind: 'UNIT',
                value: '3.8',
                components: null
              },
              {
                name: 'Monthly Payment',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$1,267.90',
                components: null
              },
              {
                name: 'Cash to Close',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$1,267.90',
                components: null
              },
              {
                name: 'Required Credit Score',
                typeRef: 'number',
                kind: 'UNIT',
                value: 720,
                components: null
              }
            ],
            [
              {
                name: 'Product',
                typeRef: 'string',
                kind: 'UNIT',
                value: 'Lender C - Fixed30-Standard',
                components: null
              },
              {
                name: 'Recommendation',
                typeRef: 'string',
                kind: 'UNIT',
                value: 'Best',
                components: null
              },
              {
                name: 'Note Amount',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$274,599.40',
                components: null
              },
              {
                name: 'Interest Rate',
                typeRef: 'string',
                kind: 'UNIT',
                value: '3.88',
                components: null
              },
              {
                name: 'Monthly Payment',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$1,291.27',
                components: null
              },
              {
                name: 'Cash to Close',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$75,491.99',
                components: null
              },
              {
                name: 'Required Credit Score',
                typeRef: 'number',
                kind: 'UNIT',
                value: 680,
                components: null
              }
            ],
            [
              {
                name: 'Product',
                typeRef: 'string',
                kind: 'UNIT',
                value: 'Lender B - ARM5/1-NoPoints',
                components: null
              },
              {
                name: 'Recommendation',
                typeRef: 'string',
                kind: 'UNIT',
                value: 'Good',
                components: null
              },
              {
                name: 'Note Amount',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$271,776.00',
                components: null
              },
              {
                name: 'Interest Rate',
                typeRef: 'string',
                kind: 'UNIT',
                value: '4.00',
                components: null
              },
              {
                name: 'Monthly Payment',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$1,297.50',
                components: null
              },
              {
                name: 'Cash to Close',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$75,435.52',
                components: null
              },
              {
                name: 'Required Credit Score',
                typeRef: 'number',
                kind: 'UNIT',
                value: 720,
                components: null
              }
            ],
            [
              {
                name: 'Product',
                typeRef: 'string',
                kind: 'UNIT',
                value: 'Lender A - Fixed30-NoPoints',
                components: null
              },
              {
                name: 'Recommendation',
                typeRef: 'string',
                kind: 'UNIT',
                value: 'Best',
                components: null
              },
              {
                name: 'Note Amount',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$271,925.00',
                components: null
              },
              {
                name: 'Interest Rate',
                typeRef: 'string',
                kind: 'UNIT',
                value: '4.08',
                components: null
              },
              {
                name: 'Monthly Payment',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$1,310.00',
                components: null
              },
              {
                name: 'Cash to Close',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$75,438.50',
                components: null
              },
              {
                name: 'Required Credit Score',
                typeRef: 'number',
                kind: 'UNIT',
                value: 680,
                components: null
              }
            ],
            [
              {
                name: 'Product',
                typeRef: 'string',
                kind: 'UNIT',
                value: 'Lender C - Fixed15-Standard',
                components: null
              },
              {
                name: 'Recommendation',
                typeRef: 'string',
                kind: 'UNIT',
                value: 'Best',
                components: null
              },
              {
                name: 'Note Amount',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$274,045.90',
                components: null
              },
              {
                name: 'Interest Rate',
                typeRef: 'string',
                kind: 'UNIT',
                value: '3.38',
                components: null
              },
              {
                name: 'Monthly Payment',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$1,942.33',
                components: null
              },
              {
                name: 'Cash to Close',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$1,942.33',
                components: null
              },
              {
                name: 'Required Credit Score',
                typeRef: 'number',
                kind: 'UNIT',
                value: 720,
                components: null
              }
            ],
            [
              {
                name: 'Product',
                typeRef: 'string',
                kind: 'UNIT',
                value: 'Lender A - Fixed15-NoPoints',
                components: null
              },
              {
                name: 'Recommendation',
                typeRef: 'string',
                kind: 'UNIT',
                value: 'Best',
                components: null
              },
              {
                name: 'Note Amount',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$270,816.00',
                components: null
              },
              {
                name: 'Interest Rate',
                typeRef: 'string',
                kind: 'UNIT',
                value: '3.75',
                components: null
              },
              {
                name: 'Monthly Payment',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$1,969.43',
                components: null
              },
              {
                name: 'Cash to Close',
                typeRef: 'string',
                kind: 'UNIT',
                value: '$75,416.32',
                components: null
              },
              {
                name: 'Required Credit Score',
                typeRef: 'number',
                kind: 'UNIT',
                value: 720,
                components: null
              }
            ]
          ]
        }
      },
      {
        outcomeId: '_6O8O6B35-4EB3-451E-874C-DB27A5C5V6B7',
        outcomeName: 'Client Ratings',
        evaluationStatus: 'SUCCEEDED',
        hasErrors: false,
        messages: [],
        outcomeResult: {
          name: 'Client Ratings',
          type: 'tProducts',
          kind: 'STRUCTURE',
          value: null,
          components: [
            {
              name: 'Rating Type A',
              typeRef: 'string',
              kind: 'UNIT',
              value: null,
              components: [
                {
                  name: 'Loan Amount',
                  typeRef: 'number',
                  kind: 'UNIT',
                  value: 540000,
                  components: null
                },
                {
                  name: 'Repayment Rate',
                  typeRef: 'number',
                  kind: 'UNIT',
                  value: 900,
                  components: null
                },
                {
                  name: 'Loan Eligibility',
                  typeRef: 'boolean',
                  kind: 'UNIT',
                  value: true,
                  components: null
                }
              ]
            },
            {
              name: 'Rating Type B',
              typeRef: 'number',
              kind: 'STRUCTURE',
              value: null,
              components: [
                {
                  name: 'Loan amount',
                  typeRef: 'number',
                  kind: 'UNIT',
                  value: 340000,
                  components: null
                },
                {
                  name: 'Repayment rate',
                  typeRef: 'number',
                  kind: 'UNIT',
                  value: 2000,
                  components: null
                },
                {
                  name: 'Sub-Rating Type C',
                  typeRef: 'number',
                  kind: 'STRUCTURE',
                  value: null,
                  components: [
                    {
                      name: 'Loan amount',
                      typeRef: 'number',
                      kind: 'UNIT',
                      value: 340000,
                      components: null
                    },
                    {
                      name: 'Repayment rate',
                      typeRef: 'number',
                      kind: 'UNIT',
                      value: 2000,
                      components: null
                    }
                  ]
                }
              ]
            }
          ]
        }
      },
      {
        outcomeId: '_12345678-9012-3456-7890-123456789012',
        outcomeName: 'Cheese manufacturer',
        evaluationStatus: 'SUCCEEDED',
        hasErrors: false,
        messages: [],
        outcomeResult: {
          name: 'Cheese manufacturer',
          typeRef: 'string',
          kind: 'UNIT',
          value: 'Acme Cheese Specialists',
          components: null
        }
      },
      {
        outcomeId: '_11145678-9012-3456-7890-123456789012',
        outcomeName: 'Nullable Cheese manufacturer',
        evaluationStatus: 'SUCCEEDED',
        hasErrors: false,
        messages: [],
        outcomeResult: {
          name: 'Nullable Cheese manufacturer',
          typeRef: 'string',
          kind: 'UNIT',
          value: null,
          components: null
        }
      }
    ]
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIds[1],
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: structuredOutcomes
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIds[2],
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: twoSimpleOutcomes
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIds[3],
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: structuredOutcomes
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIds[4],
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: [
      {
        outcomeId: '_9CFF8C35-4EB3-451E-874C-DB27A5A424C0',
        outcomeName: 'Risk Score',
        evaluationStatus: 'SUCCEEDED',
        outcomeResult: {
          name: 'Risk Score',
          typeRef: 'number',
          kind: 'UNIT',
          value: 21.7031851958099,
          components: null
        },
        messages: [],
        hasErrors: false
      }
    ]
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIds[5],
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: twoSimpleOutcomes
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIds[6],
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: twoSimpleOutcomes
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIds[7],
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: twoSimpleOutcomes
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIds[8],
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: twoSimpleOutcomes
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIds[9],
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: twoSimpleOutcomes
  }
];

module.exports = outcomes;
