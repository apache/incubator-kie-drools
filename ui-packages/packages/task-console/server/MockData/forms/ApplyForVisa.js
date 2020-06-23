const schema = {
  type: 'object',
  properties: {
    trip: {
      type: 'object',
      properties: {
        city: {
          type: 'string'
        },
        country: {
          type: 'string'
        },
        begin: {
          type: 'string',
          format: 'date-time'
        },
        end: {
          type: 'string',
          format: 'date-time'
        },
        visaRequired: {
          type: 'boolean'
        }
      },
      required:['city']
    },
    traveller: {
      type: 'object',
      properties: {
        firstName: { type: 'string' },
        lastName: { type: 'string' },
        email: { type: 'string', format: 'email' },
        nationality: { type: 'string' },
        address: {
          type: 'object',
          properties: {
            street: {
              type: 'string'
            },
            city: {
              type: 'string'
            },
            zipCode: {
              type: 'string'
            },
            country: {
              type: 'string'
            }
          }
        },
      },
      disabled: true
    },
    visaApplication: {
      type: 'object',
      properties: {
        firstName: { type: 'string' },
        lastName: { type: 'string' },
        city: {
          type: 'string'
        },
        country: {
          type: 'string'
        },
        duration: {
          type: 'integer'
        },
        passportNumber: { type: 'string' },
        nationality: { type: 'string' }
      }
    }
  }
};

module.exports = ApplyForVisaForm = {

  schema: schema,

  actions: [
    {
      name: 'Complete',
      primary: true,
      outputs: ['visaApplication'],
    },
    {
      name: 'Release',
      phase: 'release',
      outputs: [],
    }
  ]
};
