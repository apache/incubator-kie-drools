import { validateResponse, filterColumnSelection } from '../Utils';

describe('Tests for utility functions', () => {
  it('Test validateResponse function', () => {
    const content = {
      flight: null,
      metadata: {
        processInstances: [
          {
            businessKey: 'A3ULLW',
            id: 'cd814f7e-898c-3881-aa1b-6a068ed86183',
            lastUpdate: '2020-06-25T07:37:47.636Z',
            processName: 'travels',
            serviceUrl: 'http://localhost:8080',
            start: '2020-06-25T07:37:47.593Z',
            state: 'ACTIVE'
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
        arrival: null,
        departure: null,
        flightNumber: null,
        gate: null,
        seat: null
      },
      metadata: {
        processInstances: [
          {
            businessKey: 'A3ULLW',
            id: 'cd814f7e-898c-3881-aa1b-6a068ed86183',
            lastUpdate: '2020-06-25T07:37:47.636Z',
            processName: 'travels',
            serviceUrl: 'http://localhost:8080',
            start: '2020-06-25T07:37:47.593Z',
            state: 'ACTIVE'
          }
        ]
      }
    };
    const result = validateResponse(content, paramFields);
    expect(result).toEqual(resultObject);
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
});
