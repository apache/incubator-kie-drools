const executionIdBase = require('./executionIdBase');

const twoSimpleOutcomes = [
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
  }
];

const outcome = [
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIdBase + '1000',
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: [
      {
        outcomeId: '432343443',
        outcomeName: 'Recommended Loan Products',
        evaluationStatus: 'SUCCEEDED',
        hasErrors: false,
        messages: [],
        outcomeResult: {
          name: 'Recommended Loan Products',
          type: 'tProducts',
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
            ],
            [
              {
                name: 'Product',
                value: 'Lender B - ARM5/1-NoPoints',
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
                value: '$271,776.00',
                typeRef: 'string',
                components: null
              },
              {
                name: 'Interest Rate',
                value: '4.00',
                typeRef: 'string',
                components: null
              },
              {
                name: 'Monthly Payment',
                value: '$1,297.50',
                typeRef: 'string',
                components: null
              },
              {
                name: 'Cash to Close',
                value: '$75,435.52',
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
                value: 'Lender A - Fixed30-NoPoints',
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
                value: '$271,925.00',
                typeRef: 'string',
                components: null
              },
              {
                name: 'Interest Rate',
                value: '4.08',
                typeRef: 'string',
                components: null
              },
              {
                name: 'Monthly Payment',
                value: '$1,310.00',
                typeRef: 'string',
                components: null
              },
              {
                name: 'Cash to Close',
                value: '$75,438.50',
                typeRef: 'string',
                components: null
              },
              {
                name: 'Required Credit Score',
                value: 680,
                typeRef: 'number',
                components: null
              }
            ],
            [
              {
                name: 'Product',
                value: 'Lender C - Fixed15-Standard',
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
                value: '$274,045.90',
                typeRef: 'string',
                components: null
              },
              {
                name: 'Interest Rate',
                value: '3.38',
                typeRef: 'string',
                components: null
              },
              {
                name: 'Monthly Payment',
                value: '$1,942.33',
                typeRef: 'string',
                components: null
              },
              {
                name: 'Cash to Close',
                value: '$1,942.33',
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
                value: 'Lender A - Fixed15-NoPoints',
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
                value: '$270,816.00',
                typeRef: 'string',
                components: null
              },
              {
                name: 'Interest Rate',
                value: '3.75',
                typeRef: 'string',
                components: null
              },
              {
                name: 'Monthly Payment',
                value: '$1,969.43',
                typeRef: 'string',
                components: null
              },
              {
                name: 'Cash to Close',
                value: '$75,416.32',
                typeRef: 'string',
                components: null
              },
              {
                name: 'Required Credit Score',
                value: 720,
                typeRef: 'number',
                components: null
              }
            ]
          ]
        }
      },
      {
        outcomeId: '849849489',
        outcomeName: 'Client Ratings',
        evaluationStatus: 'SUCCEEDED',
        hasErrors: false,
        messages: [],
        outcomeResult: {
          name: 'Client Ratings',
          type: 'tProducts',
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
                      value: 340000,
                      typeRef: 'number',
                      components: null
                    },
                    {
                      name: 'Repayment rate',
                      value: 2000,
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
    ]
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIdBase + '1001',
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: [
      {
        outcomeId: '_c6e56793-68d0-4683-b34b-5e9d69e7d0d4',
        outcomeName: 'Risk Score',
        evaluationStatus: 'SUCCEEDED',
        outcomeResult: {
          name: 'Risk Score',
          typeRef: 'number',
          value: 1,
          components: null
        },
        messages: [],
        hasErrors: false
      },
      {
        outcomeId: '_859bea4f-dfc4-480e-96f2-1a756d54b84b',
        outcomeName: 'Total Amount from Last 24 hours Transactions',
        evaluationStatus: 'SUCCEEDED',
        outcomeResult: {
          name: 'Total Amount from Last 24 hours Transactions',
          typeRef: 'number',
          value: 0,
          components: null
        },
        messages: [],
        hasErrors: false
      },
      {
        outcomeId: '_d361c79e-8c06-4504-bdb2-d6b90b915166',
        outcomeName: 'Last Transaction',
        evaluationStatus: 'SUCCEEDED',
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
            {
              name: 'Amount',
              typeRef: 'number',
              value: 10000,
              components: null
            },
            {
              name: 'Card Type',
              typeRef: 'tCardType',
              value: 'Debit',
              components: null
            },
            {
              name: 'Location',
              typeRef: 'tLocation',
              value: 'Local',
              components: null
            }
          ]
        },
        messages: [],
        hasErrors: false
      },
      {
        outcomeId: '_ff34378e-fe90-4c58-9f7f-b9ce5767a415',
        outcomeName: 'Merchant Blacklist',
        evaluationStatus: 'SUCCEEDED',
        outcomeResult: {
          name: 'Merchant Blacklist',
          typeRef: 'string',
          value: [
            'ILLICITCORP',
            'SLIMSHADY',
            'TAINTEDTHINGS',
            'UNSCRUPULOUS',
            'UNETHICALBIZ',
            'WECORRUPT',
            'WICKEDSTUFF',
            'VERYBADTHING'
          ],
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
      executionId: executionIdBase + '1002',
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: twoSimpleOutcomes
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIdBase + '1003',
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
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
        outcomeId: '_1CFF8C35-4EB2-351E-874C-DB27A2A424C0',
        outcomeName: 'Bank Score',
        evaluationStatus: 'SKIPPED',
        outcomeResult: null,
        messages: [],
        hasErrors: false
      }
    ]
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIdBase + '1004',
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
      executionId: executionIdBase + '1005',
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: twoSimpleOutcomes
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIdBase + '1006',
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: twoSimpleOutcomes
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIdBase + '1007',
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: twoSimpleOutcomes
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIdBase + '1008',
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: twoSimpleOutcomes
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIdBase + '1009',
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: twoSimpleOutcomes
  },
  {
    header: {
      executionDate: '2020-04-16',
      executionId: executionIdBase + '1010',
      executionSucceeded: true,
      executionType: 'DECISION',
      executorName: 'Technical User'
    },
    outcomes: twoSimpleOutcomes
  }
];

module.exports = outcome;
