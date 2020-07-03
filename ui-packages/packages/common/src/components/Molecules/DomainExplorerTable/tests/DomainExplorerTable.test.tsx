import React from 'react';
import { getWrapperAsync } from '../../../../utils/OuiaUtils';

import DomainExplorerTable from '../DomainExplorerTable';
import { MockedProvider } from '@apollo/react-testing';

global.Math.random = () => 0.7218415351930461;

jest.mock('../../ProcessDescriptor/ProcessDescriptor');

describe('Domain Explorer Table Component', () => {
  let useEffect;
  const mockUseEffect = () => {
    useEffect.mockImplementationOnce(f => f());
  };
  beforeEach(() => {
    useEffect = jest.spyOn(React, 'useEffect');
    mockUseEffect();
    mockUseEffect();
  });
  it('Snapshot test with default props', async () => {
    const props = {
      columnFilters: [
        {
          flight: {
            arrival: 'Hello World',
            __typename: 'Flight',
            departure: 'Hello World',
            flightNumber: 'Hello World',
            gate: 'Hello World',
            seat: 'Hello World'
          },
          metadata: {
            processInstances: [
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              }
            ]
          }
        },
        {
          flight: {
            arrival: 'Hello World',
            __typename: 'Flight',
            departure: 'Hello World',
            flightNumber: 'Hello World',
            gate: 'Hello World',
            seat: 'Hello World'
          },
          metadata: {
            processInstances: [
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              }
            ]
          }
        }
      ],
      tableLoading: false,
      displayTable: true /*  */,
      displayEmptyState: false,
      parameters: [],
      selected: [],
      isLoadingMore: true,
      rows: [
        {
          cells: [
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World'
          ],
          isOpen: false,
          rowKey: '0.008857835601127073'
        },
        {
          parent: 0,
          rowKey: '0.6632979792309541',
          cells: [
            {
              title: ''
            }
          ]
        }
      ],
      setRows: jest.fn(),
      offset: 10,
      handleRetry: jest.fn()
    };
    const wrapper = await getWrapperAsync(
      <MockedProvider>
        <DomainExplorerTable {...props} />
      </MockedProvider>,
      'DomainExplorerTable'
    );
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });
  it('Test process instance state', async () => {
    const props = {
      columnFilters: [
        {
          flight: {
            arrival: 'Hello World',
            __typename: 'Flight',
            departure: 'Hello World',
            flightNumber: 'Hello World',
            gate: 'Hello World',
            seat: 'Hello World'
          },
          metadata: {
            processInstances: [
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ACTIVE'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'COMPLETED'
              }
            ]
          }
        },
        {
          flight: {
            arrival: 'Hello World',
            __typename: 'Flight',
            departure: 'Hello World',
            flightNumber: 'Hello World',
            gate: 'Hello World',
            seat: 'Hello World'
          },
          metadata: {
            processInstances: [
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ABORTED'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'SUSPENDED'
              }
            ]
          }
        },
        {
          flight: {
            arrival: 'Hello World',
            __typename: 'Flight',
            departure: 'Hello World',
            flightNumber: 'Hello World',
            gate: 'Hello World',
            seat: 'Hello World'
          },
          metadata: {
            processInstances: [
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'PENDING'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              }
            ]
          }
        }
      ],
      tableLoading: false,
      displayTable: true,
      displayEmptyState: false,
      parameters: [],
      selected: [],
      isLoadingMore: true,
      rows: [
        {
          cells: [
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World'
          ],
          isOpen: false,
          rowKey: '0.008857835601127073'
        },
        {
          parent: 0,
          rowKey: '0.6632979792309541',
          cells: [
            {
              title: ''
            }
          ]
        }
      ],
      setRows: jest.fn(),
      offset: 10,
      handleRetry: jest.fn()
    };

    const wrapper = await getWrapperAsync(
      <MockedProvider>
        <DomainExplorerTable {...props} />
      </MockedProvider>,
      'DomainExplorerTable'
    );
    wrapper.update();

    expect(
      wrapper.find('.kogito-management-console--domain-explorer__table')
    ).toBeTruthy();
  });
  it('check zero offset', async () => {
    const props = {
      columnFilters: [
        {
          flight: {
            arrival: 'Hello World',
            __typename: 'Flight',
            departure: 'Hello World',
            flightNumber: 'Hello World',
            gate: 'Hello World',
            seat: 'Hello World'
          },
          metadata: {
            processInstances: [
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ACTIVE'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'COMPLETED'
              }
            ]
          }
        },
        {
          flight: {
            arrival: 'Hello World',
            __typename: 'Flight',
            departure: 'Hello World',
            flightNumber: 'Hello World',
            gate: 'Hello World',
            seat: 'Hello World'
          },
          metadata: {
            processInstances: [
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'PENDING'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'SUSPENDED'
              }
            ]
          }
        }
      ],
      tableLoading: false,
      displayTable: true,
      displayEmptyState: false,
      parameters: [],
      selected: [],
      isLoadingMore: true,
      rows: [
        {
          cells: [
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World'
          ],
          isOpen: false,
          rowKey: '0.008857835601127073'
        },
        {
          parent: 0,
          rowKey: '0.6632979792309541',
          cells: [
            {
              title: ''
            }
          ]
        }
      ],
      setRows: jest.fn(),
      offset: 0,
      handleRetry: jest.fn()
    };
    const wrapper = await getWrapperAsync(
      <MockedProvider>
        <DomainExplorerTable {...props} />
      </MockedProvider>,
      'DomainExplorerTable'
    );
    wrapper.update();
    expect(
      wrapper.find('.kogito-management-console--domain-explorer__table')
    ).toBeTruthy();
  });
  it('check false value of isLoadingMore', async () => {
    const props = {
      columnFilters: [
        {
          flight: {
            arrival: 'Hello World',
            __typename: 'Flight',
            departure: 'Hello World',
            flightNumber: 'Hello World',
            gate: 'Hello World',
            seat: 'Hello World'
          },
          metadata: {
            processInstances: [
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ABORTED'
              }
            ]
          }
        },
        {
          flight: {
            arrival: 'Hello World',
            __typename: 'Flight',
            departure: 'Hello World',
            flightNumber: 'Hello World',
            gate: 'Hello World',
            seat: 'Hello World'
          },
          metadata: {
            processInstances: [
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              }
            ]
          }
        }
      ],
      tableLoading: true,
      displayTable: true,
      displayEmptyState: true,
      parameters: [],
      selected: [],
      isLoadingMore: false,
      rows: [
        {
          cells: [
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World'
          ],
          isOpen: false,
          rowKey: '0.008857835601127073'
        },
        {
          parent: 0,
          rowKey: '0.6632979792309541',
          cells: [
            {
              title: ''
            }
          ]
        }
      ],
      setRows: jest.fn(),
      offset: 0,
      handleRetry: jest.fn()
    };
    const wrapper = await getWrapperAsync(
      <MockedProvider>
        <DomainExplorerTable {...props} />
      </MockedProvider>,
      'DomainExplorerTable'
    );
    wrapper.update();
    expect(wrapper.find('h5').text()).toEqual('No data available');
  });
  it('check null value for process instance attributes', async () => {
    const props = {
      columnFilters: [
        {
          flight: {
            arrival: 'Hello World',
            __typename: 'Flight',
            departure: 'Hello World',
            flightNumber: 'Hello World',
            gate: 'Hello World',
            seat: 'Hello World'
          },
          metadata: {
            processInstances: [
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ABORTED'
              }
            ]
          }
        },
        {
          flight: {
            arrival: null,
            __typename: 'Flight',
            departure: 'Hello World',
            flightNumber: 'Hello World',
            gate: 'Hello World',
            seat: 'Hello World'
          },
          metadata: {
            processInstances: [
              {
                businessKey: null,
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              }
            ]
          }
        }
      ],
      tableLoading: true,
      displayTable: true,
      displayEmptyState: true,
      parameters: [],
      selected: [],
      isLoadingMore: false,
      rows: [
        {
          cells: [
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World'
          ],
          isOpen: false,
          rowKey: '0.008857835601127073'
        },
        {
          parent: 0,
          rowKey: '0.6632979792309541',
          cells: [
            {
              title: ''
            }
          ]
        }
      ],
      setRows: jest.fn(),
      offset: 0,
      handleRetry: jest.fn()
    };
    const wrapper = await getWrapperAsync(
      <MockedProvider>
        <DomainExplorerTable {...props} />
      </MockedProvider>,
      'DomainExplorerTable'
    );
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });
});
