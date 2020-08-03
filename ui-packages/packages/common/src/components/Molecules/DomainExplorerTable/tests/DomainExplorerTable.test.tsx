import React from 'react';
import { getWrapperAsync } from '../../../../utils/OuiaUtils';

import DomainExplorerTable from '../DomainExplorerTable';
import { MockedProvider } from '@apollo/react-testing';
import { BrowserRouter } from 'react-router-dom';

global.Math.random = () => 0.7218415351930461;

jest.mock('../../ItemDescriptor/ItemDescriptor');

// tslint:disable: no-string-literal
// tslint:disable: no-unexpected-multiline
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
    handleRetry: jest.fn(),
    filterError: '',
    finalFilters: {},
    filterChips: ['metadata / processInstances / state: ACTIVE'],
    onDeleteChip: jest.fn()
  };
  it('Snapshot test with default props', async () => {
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
    const columnFilters = [
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
    ];
    const wrapper = await getWrapperAsync(
      <MockedProvider>
        <DomainExplorerTable {...{ ...props, columnFilters }} />
      </MockedProvider>,
      'DomainExplorerTable'
    );
    wrapper.update();
    // tslint:disable-next-line: no-string-literal
    expect(
      wrapper.find('.kogito-management-console--domain-explorer__table')
    ).toBeTruthy();
  });
  it('check zero offset', async () => {
    const offset = 0;
    const columnFilters = [
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
    ];
    const wrapper = await getWrapperAsync(
      <MockedProvider>
        <DomainExplorerTable {...{ ...props, columnFilters, offset }} />
      </MockedProvider>,
      'DomainExplorerTable'
    );
    wrapper.update();
    expect(wrapper.find('.kogito-common--domain-explorer__table')).toBeTruthy();
  });
  it('check false value of isLoadingMore', async () => {
    const isLoadingMore = false;
    const displayEmptyState = true;
    const displayTable = false;
    const wrapper = await getWrapperAsync(
      <MockedProvider>
        <DomainExplorerTable
          {...{ ...props, displayTable, isLoadingMore, displayEmptyState }}
        />
      </MockedProvider>,
      'DomainExplorerTable'
    );
    wrapper.update();
    expect(
      wrapper
        .find('h5')
        .first()
        .text()
    ).toEqual('No data available');
  });
  it('check null value for process instance attributes', async () => {
    const isLoadingMore = false;
    const displayEmptyState = true;
    const offset = 0;
    const columnFilters = [
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
    ];
    const tableLoading = true;
    const wrapper = await getWrapperAsync(
      <MockedProvider>
        <DomainExplorerTable
          {...{
            ...props,
            columnFilters,
            isLoadingMore,
            tableLoading,
            offset,
            displayEmptyState
          }}
        />
      </MockedProvider>,
      'DomainExplorerTable'
    );
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });
  it('check filter errors', async () => {
    const isLoadingMore = false;
    const displayTable = false;
    const filterError = 'some error';
    const wrapper = await getWrapperAsync(
      <BrowserRouter>
        <DomainExplorerTable
          {...{ ...props, isLoadingMore, displayTable, filterError }}
        />
      </BrowserRouter>,
      'DomainExplorerTable'
    );
    wrapper.update();
    expect(wrapper.find('h1').text()).toEqual('Error fetching data');
  });
  it('check empty filter chip', async () => {
    const filterChips = [];
    const displayTable = false;
    const wrapper = await getWrapperAsync(
      <BrowserRouter>
        <DomainExplorerTable {...{ ...props, displayTable, filterChips }} />
      </BrowserRouter>,
      'DomainExplorerTable'
    );
    wrapper.update();
    const event = {} as any;
    wrapper
      .find('button')
      .at(0)
      .props()
      ['onClick'](event);
  });
});
