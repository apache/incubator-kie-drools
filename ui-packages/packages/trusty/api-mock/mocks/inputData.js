const inputData = [
  {
    name: 'Credit Score',
    typeRef: 'number',
    value: 738,
    components: []
  },
  {
    name: 'Down Payment',
    typeRef: 'number',
    value: 70000,
    components: []
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
        components: []
      },
      {
        name: 'Monthly Tax Payment',
        typeRef: 'number',
        value: 0.2,
        components: []
      },
      {
        name: 'Monthly Insurance Payment',
        typeRef: 'number',
        value: 0.15,
        components: []
      },
      {
        name: 'Monthly HOA Payment',
        typeRef: 'number',
        value: 0.12,
        components: []
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
            components: []
          },
          {
            name: 'Unit',
            typeRef: 'string',
            value: 'A',
            components: []
          },
          {
            name: 'City',
            typeRef: 'string',
            value: 'Malibu',
            components: []
          },
          {
            name: 'State',
            typeRef: 'string',
            value: 'CA',
            components: []
          },
          {
            name: 'ZIP',
            typeRef: 'string',
            value: '90903',
            components: []
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
        components: []
      },
      {
        name: 'Tax ID',
        typeRef: 'string',
        value: '11123322323',
        components: []
      },
      {
        name: 'Employment Income',
        typeRef: 'number',
        value: 99000,
        components: []
      },
      {
        name: 'Other Income',
        typeRef: 'number',
        value: 0,
        components: []
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
              components: []
            },
            {
              name: 'Institution Account or Description',
              typeRef: 'string',
              value: 'Chase',
              components: []
            },
            {
              name: 'Value',
              typeRef: 'number',
              value: 45000,
              components: []
            }
          ],
          [
            {
              name: 'Type',
              typeRef: 'string',
              value: 'Other Non-Liquid',
              components: []
            },
            {
              name: 'Institution Account or Description',
              typeRef: 'string',
              value: 'Vanguard',
              components: []
            },
            {
              name: 'Value',
              typeRef: 'number',
              value: 33000,
              components: []
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
          components: []
        },
        {
          name: 'Payee',
          value: 'Chase',
          typeRef: 'string',
          components: []
        },
        {
          name: 'Monthly Payment',
          value: 300,
          typeRef: 'number',
          components: []
        },
        {
          name: 'Balance',
          value: 0,
          typeRef: 'number',
          components: []
        },
        {
          name: 'To be paid off',
          value: 'Yes',
          typeRef: 'string',
          components: []
        }
      ],
      [
        {
          name: 'Type',
          value: 'Lease',
          typeRef: 'string',
          components: []
        },
        {
          name: 'Payee',
          value: 'BMW Finance',
          typeRef: 'string',
          components: []
        },
        {
          name: 'Monthly Payment',
          value: 450,
          typeRef: 'number',
          components: []
        },
        {
          name: 'Balance',
          value: 0,
          typeRef: 'number',
          components: []
        },
        {
          name: 'To be paid off',
          value: 'No',
          typeRef: 'string',
          components: []
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
          components: []
        },
        {
          name: 'Customer Rating',
          value: 4.2,
          typeRef: 'number',
          components: []
        }
      ],
      [
        {
          name: 'Lender Name',
          value: 'Dale Cooper',
          typeRef: 'string',
          components: []
        },
        {
          name: 'Customer Rating',
          value: 3.6,
          typeRef: 'number',
          components: []
        }
      ],
      [
        {
          name: 'Lender Name',
          value: 'Chester Desmond',
          typeRef: 'string',
          components: []
        },
        {
          name: 'Customer Rating',
          value: 4.6,
          typeRef: 'number',
          components: []
        }
      ]
    ]
  }
];

exports.inputs = inputData;
