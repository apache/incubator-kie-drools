const executionIds = require('./executionIds');

const simpleInputData = [
  {
    name: 'Credit Score',
    typeRef: 'number',
    kind: 'UNIT',
    value: 738,
    components: null
  },
  {
    name: 'Down Payment',
    typeRef: 'number',
    kind: 'UNIT',
    value: 70000,
    components: null
  },
  {
    name: 'Favorite cheese',
    typeRef: 'string',
    kind: 'UNIT',
    value: 'Cheddar',
    components: null
  },
  {
    name: 'Property',
    typeRef: 'tProperty',
    kind: 'STRUCTURE',
    value: null,
    components: [
      {
        name: 'Purchase Price',
        typeRef: 'number',
        kind: 'UNIT',
        value: 34000,
        components: null
      },
      {
        name: 'Monthly Tax Payment',
        typeRef: 'number',
        kind: 'UNIT',
        value: 0.2,
        components: null
      },
      {
        name: 'Monthly Insurance Payment',
        typeRef: 'number',
        kind: 'UNIT',
        value: 0.15,
        components: null
      },
      {
        name: 'Monthly HOA Payment',
        typeRef: 'number',
        kind: 'UNIT',
        value: 0.12,
        components: null
      },
      {
        name: 'Address',
        typeRef: 'tAddress',
        kind: 'STRUCTURE',
        value: null,
        components: [
          {
            name: 'Street',
            typeRef: 'string',
            kind: 'UNIT',
            value: '272 10th St.',
            components: null
          },
          {
            name: 'Unit',
            typeRef: 'string',
            kind: 'UNIT',
            value: 'A',
            components: null
          },
          {
            name: 'City',
            typeRef: 'string',
            kind: 'UNIT',
            value: 'Malibu',
            components: null
          },
          {
            name: 'State',
            typeRef: 'string',
            kind: 'UNIT',
            value: 'CA',
            components: null
          },
          {
            name: 'ZIP',
            typeRef: 'string',
            value: '90903',
            components: null
          }
        ]
      }
    ]
  },
  {
    name: 'Borrower',
    typeRef: 'tBorrower',
    kind: 'STRUCTURE',
    value: null,
    components: [
      {
        name: 'Full Name',
        typeRef: 'string',
        kind: 'UNIT',
        value: 'Jim Osterberg',
        components: null
      },
      {
        name: 'Tax ID',
        typeRef: 'string',
        kind: 'UNIT',
        value: '11123322323',
        components: null
      },
      {
        name: 'Employment Income',
        typeRef: 'number',
        kind: 'UNIT',
        value: 99000,
        components: null
      },
      {
        name: 'Other Income',
        typeRef: 'number',
        kind: 'UNIT',
        value: 0,
        components: null
      },
      {
        name: 'Assets',
        typeRef: 'tAssets',
        kind: 'STRUCTURE',
        value: null,
        components: [
          [
            {
              name: 'Type',
              typeRef: 'string',
              kind: 'UNIT',
              value: 'C',
              components: null
            },
            {
              name: 'Institution Account or Description',
              typeRef: 'string',
              kind: 'UNIT',
              value: 'Chase',
              components: null
            },
            {
              name: 'Value',
              typeRef: 'number',
              kind: 'UNIT',
              value: 45000,
              components: null
            }
          ],
          [
            {
              name: 'Type',
              typeRef: 'string',
              kind: 'UNIT',
              value: 'Other Non-Liquid',
              components: null
            },
            {
              name: 'Institution Account or Description',
              typeRef: 'string',
              kind: 'UNIT',
              value: 'Vanguard',
              components: null
            },
            {
              name: 'Value',
              typeRef: 'number',
              kind: 'UNIT',
              value: 33000,
              components: null
            }
          ]
        ]
      }
    ]
  },
  {
    name: 'Liabilities',
    typeRef: 'tLiabilities',
    kind: 'STRUCTURE',
    value: null,
    components: [
      [
        {
          name: 'Type',
          typeRef: 'string',
          kind: 'UNIT',
          value: 'Credit Card',
          components: null
        },
        {
          name: 'Payee',
          typeRef: 'string',
          kind: 'UNIT',
          value: 'Chase',
          components: null
        },
        {
          name: 'Monthly Payment',
          typeRef: 'number',
          kind: 'UNIT',
          value: 300,
          components: null
        },
        {
          name: 'Balance',
          typeRef: 'number',
          kind: 'UNIT',
          value: 0,
          components: null
        },
        {
          name: 'To be paid off',
          typeRef: 'string',
          kind: 'UNIT',
          value: 'Yes',
          components: null
        }
      ],
      [
        {
          name: 'Type',
          typeRef: 'string',
          kind: 'UNIT',
          value: 'Lease',
          components: null
        },
        {
          name: 'Payee',
          typeRef: 'string',
          kind: 'UNIT',
          value: 'BMW Finance',
          components: null
        },
        {
          name: 'Monthly Payment',
          typeRef: 'number',
          kind: 'UNIT',
          value: 450,
          components: null
        },
        {
          name: 'Balance',
          typeRef: 'number',
          kind: 'UNIT',
          value: 0,
          components: null
        },
        {
          name: 'To be paid off',
          typeRef: 'string',
          kind: 'UNIT',
          value: 'No',
          components: null
        }
      ]
    ]
  },
  {
    name: 'Lender Ratings',
    typeRef: 'tLenderRatings',
    kind: 'STRUCTURE',
    value: null,
    components: [
      [
        {
          name: 'Lender Name',
          typeRef: 'string',
          kind: 'UNIT',
          value: 'Gordon Cole',
          components: null
        },
        {
          name: 'Customer Rating',
          typeRef: 'number',
          kind: 'UNIT',
          value: 4.2,
          components: null
        }
      ],
      [
        {
          name: 'Lender Name',
          typeRef: 'string',
          kind: 'UNIT',
          value: 'Dale Cooper',
          components: null
        },
        {
          name: 'Customer Rating',
          typeRef: 'number',
          kind: 'UNIT',
          value: 3.6,
          components: null
        }
      ],
      [
        {
          name: 'Lender Name',
          typeRef: 'string',
          kind: 'UNIT',
          value: 'Chester Desmond',
          components: null
        },
        {
          name: 'Customer Rating',
          typeRef: 'number',
          kind: 'UNIT',
          value: 4.6,
          components: null
        }
      ]
    ]
  }
];

const structuredInput = [
  {
    name: 'Structured input 1',
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
  }
];

const allStringInputs = [
  {
    name: 'Input 1',
    typeRef: 'string',
    kind: 'UNIT',
    value: 'value1',
    components: null
  },
  {
    name: 'Input 2',
    typeRef: 'string',
    kind: 'UNIT',
    value: 'value2',
    components: null
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
