import React from 'react';
import ProcessListTable from '../ProcessListTable';
import { MockedProvider } from '@apollo/react-testing';
import { getWrapperAsync, GraphQL } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;
import GetProcessInstancesDocument = GraphQL.GetProcessInstancesDocument;
import { BrowserRouter } from 'react-router-dom';
jest.mock('../../../Molecules/ProcessListTableItems/ProcessListTableItems');
Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20
const MockedComponent = (): React.ReactElement => {
  return <></>;
};
jest.mock('@kogito-apps/common', () => ({
  ...jest.requireActual('@kogito-apps/common'),
  KogitoEmptyState: () => {
    return <MockedComponent />;
  }
}));

const initData1 = {
  ProcessInstances: [
    {
      id: '201a8a42-043e-375a-9f52-57c804b24db4',
      processId: 'travels',
      processName: 'travels',
      businessKey: 'MQQ640',
      parentProcessInstanceId: null,
      parentProcessInstance: null,
      roles: [],
      variables:
        '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2020-05-08T03:30:00.000+05:30","arrival":"2020-05-09T03:30:00.000+05:30"},"hotel":{"name":"Perfect hotel","address":{"street":"street","city":"Bengaluru","zipCode":"12345","country":"India"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"trip":{"city":"Bengaluru","country":"India","begin":"2020-05-08T03:30:00.000+05:30","end":"2020-05-09T03:30:00.000+05:30","visaRequired":false},"traveller":{"firstName":"Ajay","lastName":"Jaganathan","email":"ajaganat@redhat.com","nationality":"Polish","address":{"street":"Bangalore","city":"Bangalore","zipCode":"560093","country":"Poland"}}}',
      state: 'ACTIVE',
      start: '2020-05-07T06:50:18.274Z',
      lastUpdate: '2020-05-07T06:50:18.502Z',
      end: null,
      addons: [
        'process-management',
        'infinispan-persistence',
        'prometheus-monitoring'
      ],
      endpoint: 'http://localhost:8080/travels',
      serviceUrl: 'http://localhost:8080',
      error: null,
      childProcessInstances: [
        {
          id: 'bfde98ed-0cdd-4700-ae87-377f7ec430cd',
          processName: 'HotelBooking',
          businessKey: null
        },
        {
          id: 'e607b2a9-0aca-4788-9623-dd2e156ce9c4',
          processName: 'FlightBooking',
          businessKey: null
        }
      ],
      nodes: [
        {
          id: '39d5fe7c-4e37-44ce-8d25-05a4a29ec6ea',
          nodeId: '17',
          name: 'Book Hotel',
          enter: '2020-05-07T06:50:18.429Z',
          exit: '2020-05-07T06:50:18.439Z',
          type: 'SubProcessNode',
          definitionId: '_1A708F87-11C0-42A0-A464-0B7E259C426F'
        }
      ]
    }
  ]
};

const initData2 = {
  ProcessInstances: []
};

const mocks1 = [
  {
    request: {
      query: GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [ProcessInstanceState.Active] },
          parentProcessInstanceId: { isNull: true }
        },
        offset: 0,
        limit: 10
      }
    },
    result: {
      data: {
        ProcessInstances: [
          {
            id: '201a8a42-043e-375a-9f52-57c804b24db4',
            processId: 'travels',
            processName: 'travels',
            businessKey: 'MQQ640',
            rootProcessInstanceId: null,
            parentProcessInstanceId: null,
            parentProcessInstance: null,
            roles: [],
            variables:
              '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2020-05-08T03:30:00.000+05:30","arrival":"2020-05-09T03:30:00.000+05:30"},"hotel":{"name":"Perfect hotel","address":{"street":"street","city":"Bengaluru","zipCode":"12345","country":"India"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"trip":{"city":"Bengaluru","country":"India","begin":"2020-05-08T03:30:00.000+05:30","end":"2020-05-09T03:30:00.000+05:30","visaRequired":false},"traveller":{"firstName":"Ajay","lastName":"Jaganathan","email":"ajaganat@redhat.com","nationality":"Polish","address":{"street":"Bangalore","city":"Bangalore","zipCode":"560093","country":"Poland"}}}',
            state: 'ACTIVE',
            start: '2020-05-07T06:50:18.274Z',
            lastUpdate: '2020-05-07T06:50:18.502Z',
            end: null,
            addons: [
              'process-management',
              'infinispan-persistence',
              'prometheus-monitoring'
            ],
            endpoint: 'http://localhost:8080/travels',
            serviceUrl: 'http://localhost:8080',
            error: null,
            childProcessInstances: [
              {
                id: 'bfde98ed-0cdd-4700-ae87-377f7ec430cd',
                processName: 'HotelBooking',
                businessKey: null
              },
              {
                id: 'e607b2a9-0aca-4788-9623-dd2e156ce9c4',
                processName: 'FlightBooking',
                businessKey: null
              }
            ],
            nodes: [
              {
                id: '39d5fe7c-4e37-44ce-8d25-05a4a29ec6ea',
                nodeId: '17',
                name: 'Book Hotel',
                enter: '2020-05-07T06:50:18.429Z',
                exit: '2020-05-07T06:50:18.439Z',
                type: 'SubProcessNode',
                definitionId: '_1A708F87-11C0-42A0-A464-0B7E259C426F'
              }
            ]
          }
        ]
      }
    }
  }
];

const mocks2 = [
  {
    request: {
      query: GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [ProcessInstanceState.Active] },
          parentProcessInstanceId: { isNull: true }
        },
        offset: 0,
        limit: 10
      }
    },
    result: {
      data: {
        ProcessInstances: []
      }
    }
  }
];

const mocks3 = [
  {
    request: {
      query: GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: ['something'] },
          parentProcessInstanceId: { isNull: true }
        },
        offset: 0,
        limit: 10
      }
    },
    result: {
      data: {
        ProcessInstances: []
      },
      error: {
        errorMessage: 'some error message'
      }
    }
  }
];

const mocks4 = [
  {
    request: {
      query: GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [ProcessInstanceState.Active] },
          parentProcessInstanceId: { isNull: true },
          or: [{ businessKey: { like: 'MQQ640' } }]
        },
        offset: 0,
        limit: 10
      }
    },
    result: {
      data: {
        ProcessInstances: [
          {
            id: '201a8a42-043e-375a-9f52-57c804b24db4',
            processId: 'travels',
            processName: 'travels',
            businessKey: 'MQQ640',
            rootProcessInstanceId: null,
            parentProcessInstanceId: null,
            parentProcessInstance: null,
            roles: [],
            variables:
              '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2020-05-08T03:30:00.000+05:30","arrival":"2020-05-09T03:30:00.000+05:30"},"hotel":{"name":"Perfect hotel","address":{"street":"street","city":"Bengaluru","zipCode":"12345","country":"India"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"trip":{"city":"Bengaluru","country":"India","begin":"2020-05-08T03:30:00.000+05:30","end":"2020-05-09T03:30:00.000+05:30","visaRequired":false},"traveller":{"firstName":"Ajay","lastName":"Jaganathan","email":"ajaganat@redhat.com","nationality":"Polish","address":{"street":"Bangalore","city":"Bangalore","zipCode":"560093","country":"Poland"}}}',
            state: 'ACTIVE',
            start: '2020-05-07T06:50:18.274Z',
            lastUpdate: '2020-05-07T06:50:18.502Z',
            end: null,
            addons: [
              'process-management',
              'infinispan-persistence',
              'prometheus-monitoring'
            ],
            endpoint: 'http://localhost:8080/travels',
            serviceUrl: 'http://localhost:8080',
            error: null,
            childProcessInstances: [
              {
                id: 'bfde98ed-0cdd-4700-ae87-377f7ec430cd',
                processName: 'HotelBooking',
                businessKey: null
              },
              {
                id: 'e607b2a9-0aca-4788-9623-dd2e156ce9c4',
                processName: 'FlightBooking',
                businessKey: null
              }
            ],
            nodes: [
              {
                id: '39d5fe7c-4e37-44ce-8d25-05a4a29ec6ea',
                nodeId: '17',
                name: 'Book Hotel',
                enter: '2020-05-07T06:50:18.429Z',
                exit: '2020-05-07T06:50:18.439Z',
                type: 'SubProcessNode',
                definitionId: '_1A708F87-11C0-42A0-A464-0B7E259C426F'
              }
            ]
          }
        ]
      }
    }
  }
];

const props1 = {
  setInitData: jest.fn(),
  setLimit: jest.fn(),
  isLoading: false,
  setIsError: jest.fn(),
  setIsLoading: jest.fn(),
  initData: initData1,
  checkedArray: ['ACTIVE'],
  selectedInstances: {
    '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
    } as any
  } as any,
  setSelectedInstances: jest.fn(),
  pageSize: 10,
  isLoadingMore: false,
  filteredData: initData1,
  setFilteredData: jest.fn(),
  isFilterClicked: false,
  filters: {
    status: [ProcessInstanceState.Active],
    businessKey: []
  },
  setIsAllChecked: jest.fn(),
  selectedNumber: 0,
  setSelectedNumber: jest.fn()
};

const props2 = {
  setInitData: jest.fn(),
  setLimit: jest.fn(),
  isLoading: false,
  setIsError: jest.fn(),
  setIsLoading: jest.fn(),
  initData: initData2,
  checkedArray: ['ACTIVE'],
  selectedInstances: {
    '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
    } as any
  } as any,
  setSelectedInstances: jest.fn(),
  pageSize: 10,
  isLoadingMore: false,
  filteredData: initData1,
  setFilteredData: jest.fn(),
  isFilterClicked: false,
  filters: {
    status: [ProcessInstanceState.Active],
    businessKey: []
  },
  setIsAllChecked: jest.fn(),
  selectedNumber: 0,
  setSelectedNumber: jest.fn()
};

const props3 = {
  setInitData: jest.fn(),
  setLimit: jest.fn(),
  isLoading: false,
  setIsError: jest.fn(),
  setIsLoading: jest.fn(),
  initData: initData2,
  checkedArray: ['ACTIVE'],
  selectedInstances: {
    '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
    } as any
  } as any,
  setSelectedInstances: jest.fn(),
  pageSize: 10,
  isLoadingMore: true,
  filteredData: initData1,
  setFilteredData: jest.fn(),
  isFilterClicked: false,
  filters: {
    status: [ProcessInstanceState.Active],
    businessKey: []
  },
  setIsAllChecked: jest.fn(),
  selectedNumber: 0,
  setSelectedNumber: jest.fn()
};

const props4 = {
  setInitData: jest.fn(),
  isLoading: false,
  setIsError: jest.fn(),
  setIsLoading: jest.fn(),
  initData: initData1,
  selectedInstances: {
    '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
    } as any
  } as any,
  setSelectedInstances: jest.fn(),
  pageSize: 10,
  isLoadingMore: false,
  filteredData: initData1,
  setFilteredData: jest.fn(),
  isFilterClicked: false,
  filters: {
    status: [ProcessInstanceState.Active],
    businessKey: ['MQQ640']
  },
  setIsAllChecked: jest.fn(),
  selectedNumber: 0,
  setSelectedNumber: jest.fn(),
  setLimit: jest.fn()
};

describe('ProcessListTable component tests', () => {
  it('Snapshot testing with success data', async () => {
    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks1} addTypename={false}>
        <BrowserRouter>
          {' '}
          <ProcessListTable {...props1} />
        </BrowserRouter>
      </MockedProvider>,
      'ProcessListTable'
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('Snapshot testing with success data(with businessKey)', async () => {
    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks4} addTypename={false}>
        <BrowserRouter>
          {' '}
          <ProcessListTable {...props4} />
        </BrowserRouter>
      </MockedProvider>,
      'ProcessListTable'
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('Snapshot testing with no data', async () => {
    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks2} addTypename={false}>
        <BrowserRouter>
          <ProcessListTable {...props2} />
        </BrowserRouter>
      </MockedProvider>,
      'ProcessListTable'
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('Snapshot testing with wrong query parameters', async () => {
    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks3} addTypename={false}>
        <BrowserRouter>
          <ProcessListTable {...props2} />
        </BrowserRouter>
      </MockedProvider>,
      'ProcessListTable'
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('Snapshot testing initial empty data', async () => {
    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks1} addTypename={false}>
        <BrowserRouter>
          <ProcessListTable {...props3} />
        </BrowserRouter>
      </MockedProvider>,
      'ProcessListTable'
    );
    expect(wrapper).toMatchSnapshot();
  });
});
