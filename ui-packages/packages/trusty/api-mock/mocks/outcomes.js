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
const executionIds = require('./executionIds');

const twoSimpleOutcomes = [
  {
    outcomeId: '_12268B68-94A1-4960-B4C8-0B6071AFDE58',
    outcomeName: 'Mortgage Approval',
    evaluationStatus: 'SUCCEEDED',
    outcomeResult: {
      kind: 'UNIT',
      type: 'boolean',
      value: null
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
  }
];

const structuredOutcomes = [
  {
    outcomeId: '_c6e56793-68d0-4683-b34b-5e9d69e7d0d4',
    outcomeName: 'Structured outcome 1',
    evaluationStatus: 'SUCCEEDED',
    outcomeResult: {
      kind: 'STRUCTURE',
      type: 'tStructure',
      value: {
        'Structure1 field1': {
          kind: 'UNIT',
          type: 'tField1',
          value: 'value1'
        }
      }
    },
    messages: [],
    hasErrors: false
  },
  {
    outcomeId: '_859bea4f-dfc4-480e-96f2-1a756d54b84b',
    outcomeName: 'Structured outcome 2',
    evaluationStatus: 'SUCCEEDED',
    outcomeResult: {
      kind: 'STRUCTURE',
      type: 'tStructure',
      value: {
        'Structure2 field1': {
          kind: 'UNIT',
          type: 'tField1',
          value: 'value2'
        }
      }
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
                },
                Recommendation: {
                  kind: 'UNIT',
                  type: 'string',
                  value: 'Best'
                },
                'Note Amount': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '$274,599.40'
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
            },
            {
              kind: 'STRUCTURE',
              type: 'tProduct',
              value: {
                Product: {
                  kind: 'UNIT',
                  type: 'string',
                  value: 'Lender B - ARM5/1-NoPoints'
                },
                Recommendation: {
                  kind: 'UNIT',
                  type: 'string',
                  value: 'Good'
                },
                'Note Amount': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '$271,776.00'
                },
                'Interest Rate': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '4.00'
                },
                'Monthly Payment': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '$1,297.50'
                },
                'Cash to Close': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '$75,435.52'
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
                  value: 'Lender A - Fixed30-NoPoints'
                },
                Recommendation: {
                  kind: 'UNIT',
                  type: 'string',
                  value: 'Best'
                },
                'Note Amount': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '$271,925.00'
                },
                'Interest Rate': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '4.08'
                },
                'Monthly Payment': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '$1,310.00'
                },
                'Cash to Close': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '$75,438.50'
                },
                'Required Credit Score': {
                  kind: 'UNIT',
                  type: 'number',
                  value: 680
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
                  value: 'Lender C - Fixed15-Standard'
                },
                Recommendation: {
                  kind: 'UNIT',
                  type: 'string',
                  value: 'Best'
                },
                'Note Amount': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '$274,045.90'
                },
                'Interest Rate': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '3.38'
                },
                'Monthly Payment': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '$1,942.33'
                },
                'Cash to Close': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '$1,942.33'
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
                  value: 'Lender A - Fixed15-NoPoints'
                },
                Recommendation: {
                  kind: 'UNIT',
                  type: 'string',
                  value: 'Best'
                },
                'Note Amount': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '$270,816.00'
                },
                'Interest Rate': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '3.75'
                },
                'Monthly Payment': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '$1,969.43'
                },
                'Cash to Close': {
                  kind: 'UNIT',
                  type: 'string',
                  value: '$75,416.32'
                },
                'Required Credit Score': {
                  kind: 'UNIT',
                  type: 'number',
                  value: 720
                }
              }
            }
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
                      value: 340000
                    },
                    'Repayment rate': {
                      kind: 'UNIT',
                      type: 'number',
                      value: 2000
                    }
                  }
                }
              }
            }
          }
        }
      },
      {
        outcomeId: '_12345678-9012-3456-7890-123456789012',
        outcomeName: 'Cheese manufacturer',
        evaluationStatus: 'SUCCEEDED',
        hasErrors: false,
        messages: [],
        outcomeResult: {
          kind: 'UNIT',
          type: 'string',
          value: 'Acme Cheese Specialists'
        }
      },
      {
        outcomeId: '_11145678-9012-3456-7890-123456789012',
        outcomeName: 'Nullable Cheese manufacturer',
        evaluationStatus: 'SUCCEEDED',
        hasErrors: false,
        messages: [],
        outcomeResult: {
          kind: 'UNIT',
          type: 'string',
          value: null
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
          kind: 'UNIT',
          type: 'number',
          value: 21.7031851958099
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
