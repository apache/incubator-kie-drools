const inputData = [
  {
    name: 'Credit Score',
    typeRef: 'number',
    value: 738,
    components: null
  },
  {
    name: 'Down Payment',
    typeRef: 'number',
    value: 70000,
    components: null
  },
  {
    name: 'Property',
    typeRef: 'tProperty',
    value: null,
    components: [
      {
        name: 'Purchase Price',
        typeRef: 'number',
        value: 34000,
        components: null
      },
      {
        name: 'Monthly Tax Payment',
        typeRef: 'number',
        value: 0.2,
        components: null
      },
      {
        name: 'Monthly Insurance Payment',
        typeRef: 'number',
        value: 0.15,
        components: null
      },
      {
        name: 'Monthly HOA Payment',
        typeRef: 'number',
        value: 0.12,
        components: null
      },
      {
        name: 'Address',
        typeRef: 'tAddress',
        value: null,
        components: [
          {
            name: 'Street',
            typeRef: 'string',
            value: '272 10th St.',
            components: null
          },
          {
            name: 'Unit',
            typeRef: 'string',
            value: 'A',
            components: null
          },
          {
            name: 'City',
            typeRef: 'string',
            value: 'Malibu',
            components: null
          },
          {
            name: 'State',
            typeRef: 'string',
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
    value: null,
    components: [
      {
        name: 'Full Name',
        typeRef: 'string',
        value: 'Jim Osterberg',
        components: null
      },
      {
        name: 'Tax ID',
        typeRef: 'string',
        value: '11123322323',
        components: null
      },
      {
        name: 'Employment Income',
        typeRef: 'number',
        value: 99000,
        components: null
      },
      {
        name: 'Other Income',
        typeRef: 'number',
        value: 0,
        components: null
      },
      {
        name: 'Assets',
        typeRef: 'tAssets',
        value: null,
        components: [
          [
            {
              name: 'Type',
              typeRef: 'string',
              value: 'C',
              components: null
            },
            {
              name: 'Institution Account or Description',
              typeRef: 'string',
              value: 'Chase',
              components: null
            },
            {
              name: 'Value',
              typeRef: 'number',
              value: 45000,
              components: null
            }
          ],
          [
            {
              name: 'Type',
              typeRef: 'string',
              value: 'Other Non-Liquid',
              components: null
            },
            {
              name: 'Institution Account or Description',
              typeRef: 'string',
              value: 'Vanguard',
              components: null
            },
            {
              name: 'Value',
              typeRef: 'number',
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
    value: null,
    components: [
      [
        {
          name: 'Type',
          value: 'Credit Card',
          typeRef: 'string',
          components: null
        },
        {
          name: 'Payee',
          value: 'Chase',
          typeRef: 'string',
          components: null
        },
        {
          name: 'Monthly Payment',
          value: 300,
          typeRef: 'number',
          components: null
        },
        {
          name: 'Balance',
          value: 0,
          typeRef: 'number',
          components: null
        },
        {
          name: 'To be paid off',
          value: 'Yes',
          typeRef: 'string',
          components: null
        }
      ],
      [
        {
          name: 'Type',
          value: 'Lease',
          typeRef: 'string',
          components: null
        },
        {
          name: 'Payee',
          value: 'BMW Finance',
          typeRef: 'string',
          components: null
        },
        {
          name: 'Monthly Payment',
          value: 450,
          typeRef: 'number',
          components: null
        },
        {
          name: 'Balance',
          value: 0,
          typeRef: 'number',
          components: null
        },
        {
          name: 'To be paid off',
          value: 'No',
          typeRef: 'string',
          components: null
        }
      ]
    ]
  },
  {
    name: 'Lender Ratings',
    typeRef: 'tLenderRatings',
    value: null,
    components: [
      [
        {
          name: 'Lender Name',
          value: 'Gordon Cole',
          typeRef: 'string',
          components: null
        },
        {
          name: 'Customer Rating',
          value: 4.2,
          typeRef: 'number',
          components: null
        }
      ],
      [
        {
          name: 'Lender Name',
          value: 'Dale Cooper',
          typeRef: 'string',
          components: null
        },
        {
          name: 'Customer Rating',
          value: 3.6,
          typeRef: 'number',
          components: null
        }
      ],
      [
        {
          name: 'Lender Name',
          value: 'Chester Desmond',
          typeRef: 'string',
          components: null
        },
        {
          name: 'Customer Rating',
          value: 4.6,
          typeRef: 'number',
          components: null
        }
      ]
    ]
  }
];

exports.inputs = inputData;
