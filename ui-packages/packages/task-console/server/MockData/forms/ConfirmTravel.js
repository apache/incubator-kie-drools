const schema = {
  type: 'object',
  properties: {
    flight: {
      type: 'object',
      properties: {
        flightNumber: {
          type: 'string'
        },
        seat: {
          type: 'string'
        },
        gate: {
          type: 'string'
        },
        departure: {
          type: 'string',
          format: 'date-time'
        },
        arrival: {
          type: 'string',
          format: 'date-time'
        }
      },
      disabled: true
    },
    hotel: {
      type: 'object',
      properties: {
        name: {
          type: 'string'
        },
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
        phone: {
          type: 'string'
        },
        bookingNumber: {
          type: 'string'
        },
        room: {
          type: 'string'
        }
      },
      disabled: true
    }
  }
};


module.exports = ConfirmTravelForm = {
  schema: schema,

  actions: [
    {
      name: 'Complete',
      primary: true,
      outputs: []
    },
    {
      name: 'Release',
      phase: 'release',
      outputs: []
    }
  ]
};
