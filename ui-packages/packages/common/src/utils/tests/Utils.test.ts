import {
  validateResponse,
  filterColumnSelection,
  deleteKey,
  clearEmpties,
  set
} from '../Utils';

describe('Tests for utility functions', () => {
  it('Test validateResponse function', () => {
    const content = {
      flight: {
        arrival: '2020-07-28T03:30:00.000+05:30',
        departure: '2020-07-23T03:30:00.000+05:30',
        flightNumber: 'MX555',
        gate: null,
        seat: null,
        __typename: 'Flight'
      },
      metadata: {
        processInstances: [
          {
            businessKey: 'KHRBIX',
            id: '1b1a0308-4b27-3441-b12e-f6e8a19d603a',
            lastUpdate: '2020-07-20T03:38:54.145Z',
            processName: 'travels',
            serviceUrl: 'http://localhost:8080',
            start: '2020-07-20T03:38:54.131Z',
            state: 'ACTIVE',
            __typename: 'ProcessInstanceMeta'
          }
        ]
      }
    };
    const paramFields = [
      { flight: ['arrival'] },
      { flight: ['departure'] },
      { flight: ['flightNumber'] },
      { flight: ['gate'] },
      { flight: ['seat'] },
      {
        metadata: [
          {
            processInstances: [
              'id',
              'processName',
              'state',
              'start',
              'lastUpdate',
              'businessKey',
              'serviceUrl'
            ]
          }
        ]
      }
    ];
    const resultObject = {
      flight: {
        __typename: 'Flight',
        arrival: '2020-07-28T03:30:00.000+05:30',
        departure: '2020-07-23T03:30:00.000+05:30',
        flightNumber: 'MX555',
        gate: null,
        seat: null
      },
      metadata: {
        processInstances: [
          {
            __typename: 'ProcessInstanceMeta',
            businessKey: 'KHRBIX',
            id: '1b1a0308-4b27-3441-b12e-f6e8a19d603a',
            lastUpdate: '2020-07-20T03:38:54.145Z',
            processName: 'travels',
            serviceUrl: 'http://localhost:8080',
            start: '2020-07-20T03:38:54.131Z',
            state: 'ACTIVE'
          }
        ]
      }
    };
    const result = validateResponse(content, paramFields);
    expect(result).toEqual(resultObject);
    const content2 = {
      flight: null,
      hotel: null,
      traveller: {
        address: {
          city: {
            test: null
          }
        }
      },
      metadata: {
        processInstances: [
          {
            businessKey: 'V6OOIQ',
            id: '997d0fbd-a5b6-39cc-8d15-4815dc87cc65',
            lastUpdate: '2020-07-20T03:34:42.916Z',
            processName: 'travels',
            serviceUrl: 'http://localhost:8080',
            start: '2020-07-20T03:34:42.887Z',
            state: 'ACTIVE',
            __typename: 'ProcessInstanceMeta'
          }
        ],
        __typename: 'KogitoMetadata'
      },
      __typename: 'Travels'
    };
    const paramFields2 = [
      { flight: ['arrival'] },
      { flight: ['departure'] },
      { flight: ['flightNumber'] },
      { flight: ['gate'] },
      { flight: ['seat'] },
      { hotel: [{ address: ['city'] }] },
      { hotel: [{ address: ['country'] }] },
      { hotel: [{ address: ['street'] }] },
      { traveller: [{ address: [{ city: ['test'] }] }] },
      {
        metadata: [
          {
            processInstances: [
              'id',
              'processName',
              'state',
              'start',
              'lastUpdate',
              'businessKey',
              'serviceUrl'
            ]
          }
        ]
      }
    ];
    const resultObject2 = {
      __typename: 'Travels',
      flight: {
        arrival: null,
        departure: null,
        flightNumber: null,
        gate: null,
        seat: null
      },
      hotel: {
        address: {
          city: null,
          country: null,
          street: null
        }
      },
      traveller: {
        address: {
          city: {
            test: null
          }
        }
      },
      metadata: {
        __typename: 'KogitoMetadata',
        processInstances: [
          {
            __typename: 'ProcessInstanceMeta',
            businessKey: 'V6OOIQ',
            id: '997d0fbd-a5b6-39cc-8d15-4815dc87cc65',
            lastUpdate: '2020-07-20T03:34:42.916Z',
            processName: 'travels',
            serviceUrl: 'http://localhost:8080',
            start: '2020-07-20T03:34:42.887Z',
            state: 'ACTIVE'
          }
        ]
      }
    };
    const result2 = validateResponse(content2, paramFields2);
    expect(result2).toEqual(resultObject2);
  });
  it('Test filterColumnSelection function', () => {
    const selectionArray = ['hotel', 'address'];
    const objValue = 'country';
    const resultObject = {
      hotel: [
        {
          address: ['country']
        }
      ]
    };
    const result = filterColumnSelection(selectionArray, objValue);
    expect(result).toEqual(resultObject);
  });
  it('Test filterColumnSelection with empty selectionArray', () => {
    const selectionArray = [];
    const objValue = 'country';
    filterColumnSelection(selectionArray, objValue);
  });
  it('Test deleteKey utility function', () => {
    const tempObj = {
      metadata: {
        processInstances: { state: { equal: 'ACTIVE' } }
      }
    };
    const removeString = ['metadata', 'processInstances', 'state'];
    const result = deleteKey(tempObj, removeString);
    expect(result).toEqual({
      metadata: {
        processInstances: {}
      }
    });
  });
  it('Test clearEmpties utility function', () => {
    const obj = { country: { equal: 'Australia' }, flight: {} };
    const result = clearEmpties(obj);
    expect(result).toEqual({ country: { equal: 'Australia' } });
    const obj2 = { country: { equal: 'Australia' }, flight: { arrival: {} } };
    const result2 = clearEmpties(obj2);
    expect(result2).toEqual({ country: { equal: 'Australia' } });
  });
  it('Test set function', () => {
    const obj = {};
    const keys = 'trip,country,equal';
    const value = 'India';
    set(obj, keys, value);
    expect(obj).toEqual({ trip: { country: { equal: 'India' } } });
  });
});
