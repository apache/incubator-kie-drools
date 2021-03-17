module.exports = ApplyForVisaForm = {
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
      input: true,
      required: ['city']
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
        }
      },
      disabled: true,
      input: true
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
      },
      output: true,
      input: true
    }
  },
  phases: ['complete', 'release']
};
