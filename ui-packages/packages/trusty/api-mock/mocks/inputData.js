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

const simpleInputData = [
  {
    name: 'Prior refusal?',
    value: {
      kind: 'UNIT',
      type: 'boolean',
      value: false
    }
  },
  {
    name: 'Credit Score',
    value: {
      kind: 'UNIT',
      type: 'number',
      value: 738
    }
  },
  {
    name: 'Down Payment',
    value: {
      kind: 'UNIT',
      type: 'number',
      value: 70000
    }
  },
  {
    name: 'Favorite cheese',
    value: {
      kind: 'UNIT',
      type: 'string',
      value: 'Cheddar'
    }
  }
];

const structuredInput = [
  {
    name: 'Property',
    value: {
      kind: 'STRUCTURE',
      type: 'tProperty',
      value: {
        'Purchase Price': {
          kind: 'UNIT',
          type: 'number',
          value: 34000
        },
        'Monthly Tax Payment': {
          kind: 'UNIT',
          type: 'number',
          value: 0.2
        },
        'Monthly Insurance Payment': {
          kind: 'UNIT',
          type: 'number',
          value: 0.15
        },
        'Monthly HOA Payment': {
          kind: 'UNIT',
          type: 'number',
          value: 0.12
        },
        Address: {
          kind: 'STRUCTURE',
          type: 'tAddress',
          value: {
            Street: {
              kind: 'UNIT',
              type: 'string',
              value: '272 10th St.'
            },
            Unit: {
              kind: 'UNIT',
              type: 'string',
              value: 'A'
            },
            City: {
              kind: 'UNIT',
              type: 'string',
              value: 'Malibu'
            },
            State: {
              kind: 'UNIT',
              type: 'string',
              value: 'CA'
            },
            ZIP: {
              kind: 'UNIT',
              type: 'string',
              value: '90903'
            }
          }
        }
      }
    }
  },
  {
    name: 'Borrower',
    value: {
      kind: 'STRUCTURE',
      type: 'tBorrower',
      value: {
        'Full Name': {
          kind: 'UNIT',
          type: 'string',
          value: 'Jim Osterberg'
        },
        'Tax ID': {
          kind: 'UNIT',
          type: 'string',
          value: '11123322323'
        },
        'Employment Income': {
          kind: 'UNIT',
          type: 'number',
          value: 99000
        },
        'Other Income': {
          kind: 'UNIT',
          type: 'number',
          value: 0
        },
        Assets: {
          kind: 'COLLECTION',
          type: 'tAssets',
          value: [
            {
              kind: 'STRUCTURE',
              type: 'tAsset',
              value: {
                Type: {
                  kind: 'UNIT',
                  type: 'string',
                  value: 'C'
                },
                'Institution Account or Description': {
                  kind: 'UNIT',
                  type: 'string',
                  value: 'Chase'
                },
                Value: {
                  kind: 'UNIT',
                  type: 'number',
                  value: 45000
                }
              }
            },
            {
              kind: 'STRUCTURE',
              type: 'tAsset',
              value: {
                Type: {
                  kind: 'UNIT',
                  type: 'string',
                  value: 'Other Non-Liquid'
                },
                'Institution Account or Description': {
                  kind: 'UNIT',
                  type: 'string',
                  value: 'Vanguard'
                },
                Value: {
                  kind: 'UNIT',
                  type: 'number',
                  value: 33000
                }
              }
            }
          ]
        }
      }
    }
  },
  {
    name: 'Liabilities',
    value: {
      kind: 'COLLECTION',
      type: 'tLiabilities',
      value: [
        {
          kind: 'STRUCTURE',
          type: 'tLiability',
          value: {
            Type: {
              kind: 'UNIT',
              type: 'string',
              value: 'Credit Card'
            },
            Payee: {
              kind: 'UNIT',
              type: 'string',
              value: 'Chase'
            },
            'Monthly Payment': {
              kind: 'UNIT',
              type: 'number',
              value: 300
            },
            Balance: {
              kind: 'UNIT',
              type: 'number',
              value: 0
            },
            'To be paid off': {
              kind: 'UNIT',
              type: 'string',
              value: 'Yes'
            }
          }
        },
        {
          kind: 'STRUCTURE',
          type: 'tLiability',
          value: {
            Type: {
              kind: 'UNIT',
              type: 'string',
              value: 'Lease'
            },
            Payee: {
              kind: 'UNIT',
              type: 'string',
              value: 'BMW Finance'
            },
            'Monthly Payment': {
              kind: 'UNIT',
              type: 'number',
              value: 450
            },
            Balance: {
              kind: 'UNIT',
              type: 'number',
              value: 0
            },
            'To be paid off': {
              kind: 'UNIT',
              type: 'string',
              value: 'No'
            }
          }
        }
      ]
    }
  },
  {
    name: 'Lender Ratings',
    value: {
      kind: 'COLLECTION',
      type: 'tLenderRatings',
      value: [
        {
          kind: 'STRUCTURE',
          type: 'tLenderRating',
          value: {
            'Lender Name': {
              kind: 'UNIT',
              type: 'string',
              value: 'Gordon Cole'
            },
            'Customer Rating': {
              kind: 'UNIT',
              type: 'number',
              value: 4.2
            }
          }
        },
        {
          kind: 'STRUCTURE',
          type: 'tLenderRating',
          value: {
            'Lender Name': {
              kind: 'UNIT',
              type: 'string',
              value: 'Dale Cooper'
            },
            'Customer Rating': {
              kind: 'UNIT',
              type: 'number',
              value: 3.6
            }
          }
        },
        {
          kind: 'STRUCTURE',
          type: 'tLenderRating',
          value: {
            'Lender Name': {
              kind: 'UNIT',
              type: 'string',
              value: 'Chester Desmond'
            },
            'Customer Rating': {
              kind: 'UNIT',
              type: 'number',
              value: 4.6
            }
          }
        }
      ]
    }
  }
];

const allStringInputs = [
  {
    name: 'Input 1',
    value: {
      kind: 'UNIT',
      type: 'string',
      value: 'value1'
    }
  },
  {
    name: 'Input 2',
    value: {
      kind: 'UNIT',
      type: 'string',
      value: 'value2'
    }
  }
];

const inputs = [
  {
    executionId: executionIds[0],
    inputs: simpleInputData
  },
  {
    executionId: executionIds[1],
    inputs: simpleInputData
  },
  {
    executionId: executionIds[2],
    inputs: structuredInput
  },
  {
    executionId: executionIds[3],
    inputs: structuredInput
  },
  {
    executionId: executionIds[4],
    inputs: allStringInputs
  },
  {
    executionId: executionIds[5],
    inputs: simpleInputData
  },
  {
    executionId: executionIds[6],
    inputs: simpleInputData
  },
  {
    executionId: executionIds[7],
    inputs: simpleInputData
  },
  {
    executionId: executionIds[8],
    inputs: simpleInputData
  },
  {
    executionId: executionIds[9],
    inputs: simpleInputData
  }
];

module.exports = inputs;
