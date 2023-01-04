import React from 'react';
import { mount } from 'enzyme';

import DomainExplorerTable from '../DomainExplorerTable';
import { MockedProvider } from '@apollo/react-testing';
import { BrowserRouter } from 'react-router-dom';
import wait from 'waait';
import { act } from 'react-dom/test-utils';

global.Math.random = () => 0.7218415351930461;

jest.mock('../../ItemDescriptor/ItemDescriptor');

describe('Domain Explorer Table Component', () => {
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
    onDeleteChip: jest.fn(),
    setOrderByObj: jest.fn(),
    setRunQuery: jest.fn(),
    setSortBy: jest.fn(),
    sortBy: {
      direction: 'asc',
      index: 6
    }
  };
  it('Snapshot test with default props', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider>
          <DomainExplorerTable {...props} />
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorerTable');
    });
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
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider>
          <DomainExplorerTable {...{ ...props, columnFilters }} />
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorerTable');
    });
    wrapper.update();
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
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider>
          <DomainExplorerTable {...{ ...props, columnFilters, offset }} />
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorerTable');
    });
    wrapper.update();
    expect(wrapper.find('.kogito-common--domain-explorer__table')).toBeTruthy();
  });
  it('check false value of isLoadingMore', async () => {
    const isLoadingMore = false;
    const displayEmptyState = true;
    const displayTable = true;
    const filterError = null;
    const selected = ['metadata / processInstances / state: ACTIVE'];
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider>
          <DomainExplorerTable
            {...{
              ...props,
              displayTable,
              isLoadingMore,
              displayEmptyState,
              filterError,
              selected
            }}
          />
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorerTable');
    });
    wrapper.update();
    expect(wrapper.find('h5').first().text()).toEqual('No results found');
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
    let wrapper;
    await act(async () => {
      wrapper = mount(
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
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorerTable');
    });
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });
  it('check filter errors', async () => {
    const isLoadingMore = false;
    const displayTable = false;
    const filterError = 'some error';
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <DomainExplorerTable
            {...{ ...props, isLoadingMore, displayTable, filterError }}
          />
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorerTable');
    });
    wrapper.update();
    expect(wrapper.find('h1').text()).toEqual('Error fetching data');
  });
  it('check empty filter chip', async () => {
    const filterChips = [];
    const displayTable = false;
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <DomainExplorerTable {...{ ...props, displayTable, filterChips }} />
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorerTable');
    });
    wrapper.update();
    const event = {} as any;
    wrapper.find('button').at(0).props()['onClick'](event);
  });
  it('check sort functionality', async () => {
    const isLoadingMore = false;
    const displayEmptyState = true;
    const displayTable = true;
    const filterError = null;
    const selected = ['metadata / processInstances / state: ACTIVE'];
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider>
          <DomainExplorerTable
            {...{
              ...props,
              displayTable,
              isLoadingMore,
              displayEmptyState,
              filterError,
              selected
            }}
          />
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorerTable');
    });
    wrapper.update();
    const obj = {
      target: {
        innerText: 'Traveller / firstName'
      }
    };
    // tslint:disable-next-line: no-unused-expression
    wrapper
      .find('.kogito-common--domain-explorer__table')
      .at(0)
      .props()
      ['onSort'](obj, 1, 'asc');
  });
});
