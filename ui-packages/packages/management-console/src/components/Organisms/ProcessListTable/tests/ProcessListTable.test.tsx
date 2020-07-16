import React from 'react';
import ProcessListTable from '../ProcessListTable';
import { MockedProvider } from '@apollo/react-testing';
import { getWrapperAsync, GraphQL } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;
import GetProcessInstancesDocument = GraphQL.GetProcessInstancesDocument;
import GetProcessInstancesWithBusinessKeyDocument = GraphQL.GetProcessInstancesWithBusinessKeyDocument;
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
        },
        {
          id: '1d3d7ebe-79ec-4848-b1d6-d7c5a371b4dd',
          nodeId: '8',
          name: 'Confirm travel',
          enter: '2020-05-07T06:50:18.443Z',
          exit: null,
          type: 'HumanTaskNode',
          definitionId: '_2140F05A-364F-40B3-BB7B-B12927065DF8'
        },
        {
          id: '4fc774f5-c429-4b97-8981-bd60d1f59954',
          nodeId: '7',
          name: 'Join',
          enter: '2020-05-07T06:50:18.442Z',
          exit: '2020-05-07T06:50:18.443Z',
          type: 'Join',
          definitionId: '_5D0733B5-53FE-40E9-9900-4CC13419C67A'
        },
        {
          id: '60309b28-1dbe-49c3-b0d8-8cefb86be8b4',
          nodeId: '10',
          name: 'Book Flight',
          enter: '2020-05-07T06:50:18.439Z',
          exit: '2020-05-07T06:50:18.442Z',
          type: 'SubProcessNode',
          definitionId: '_F543B3F0-AB44-4A5B-BF17-8D9DEB505815'
        },
        {
          id: 'efa5a6c0-9470-4405-8609-620bdbfbb6d9',
          nodeId: '2',
          name: 'Book',
          enter: '2020-05-07T06:50:18.428Z',
          exit: '2020-05-07T06:50:18.439Z',
          type: 'Split',
          definitionId: '_175DC79D-C2F1-4B28-BE2D-B583DFABF70D'
        },
        {
          id: 'acc87f1f-e436-4364-b4d3-9069ddb359c9',
          nodeId: '14',
          name: 'Join',
          enter: '2020-05-07T06:50:18.428Z',
          exit: '2020-05-07T06:50:18.428Z',
          type: 'Join',
          definitionId: '_B34ADDEE-DEA5-47C5-A913-F8B85ED5641F'
        },
        {
          id: '6c20729a-3e2b-44c5-af5c-8554f21a7ebf',
          nodeId: '15',
          name: 'is visa required',
          enter: '2020-05-07T06:50:18.427Z',
          exit: '2020-05-07T06:50:18.428Z',
          type: 'Split',
          definitionId: '_5EA95D17-59A6-4567-92DF-74D36CE7F35A'
        },
        {
          id: '3fb92a0a-e88b-45a4-a48b-65f11a679b96',
          nodeId: '5',
          name: 'Visa check',
          enter: '2020-05-07T06:50:18.278Z',
          exit: '2020-05-07T06:50:18.427Z',
          type: 'RuleSetNode',
          definitionId: '_54ABE1ED-61BE-45F9-812C-795A5D4ED35E'
        },
        {
          id: '2860e474-dffc-498c-8731-e6b6f0b5d4d4',
          nodeId: '16',
          name: 'StartProcess',
          enter: '2020-05-07T06:50:18.276Z',
          exit: '2020-05-07T06:50:18.278Z',
          type: 'StartNode',
          definitionId: '_1B11BEC9-402A-4E73-959A-296BD334CAB0'
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
        state: [ProcessInstanceState.Active],
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
              },
              {
                id: '1d3d7ebe-79ec-4848-b1d6-d7c5a371b4dd',
                nodeId: '8',
                name: 'Confirm travel',
                enter: '2020-05-07T06:50:18.443Z',
                exit: null,
                type: 'HumanTaskNode',
                definitionId: '_2140F05A-364F-40B3-BB7B-B12927065DF8'
              },
              {
                id: '4fc774f5-c429-4b97-8981-bd60d1f59954',
                nodeId: '7',
                name: 'Join',
                enter: '2020-05-07T06:50:18.442Z',
                exit: '2020-05-07T06:50:18.443Z',
                type: 'Join',
                definitionId: '_5D0733B5-53FE-40E9-9900-4CC13419C67A'
              },
              {
                id: '60309b28-1dbe-49c3-b0d8-8cefb86be8b4',
                nodeId: '10',
                name: 'Book Flight',
                enter: '2020-05-07T06:50:18.439Z',
                exit: '2020-05-07T06:50:18.442Z',
                type: 'SubProcessNode',
                definitionId: '_F543B3F0-AB44-4A5B-BF17-8D9DEB505815'
              },
              {
                id: 'efa5a6c0-9470-4405-8609-620bdbfbb6d9',
                nodeId: '2',
                name: 'Book',
                enter: '2020-05-07T06:50:18.428Z',
                exit: '2020-05-07T06:50:18.439Z',
                type: 'Split',
                definitionId: '_175DC79D-C2F1-4B28-BE2D-B583DFABF70D'
              },
              {
                id: 'acc87f1f-e436-4364-b4d3-9069ddb359c9',
                nodeId: '14',
                name: 'Join',
                enter: '2020-05-07T06:50:18.428Z',
                exit: '2020-05-07T06:50:18.428Z',
                type: 'Join',
                definitionId: '_B34ADDEE-DEA5-47C5-A913-F8B85ED5641F'
              },
              {
                id: '6c20729a-3e2b-44c5-af5c-8554f21a7ebf',
                nodeId: '15',
                name: 'is visa required',
                enter: '2020-05-07T06:50:18.427Z',
                exit: '2020-05-07T06:50:18.428Z',
                type: 'Split',
                definitionId: '_5EA95D17-59A6-4567-92DF-74D36CE7F35A'
              },
              {
                id: '3fb92a0a-e88b-45a4-a48b-65f11a679b96',
                nodeId: '5',
                name: 'Visa check',
                enter: '2020-05-07T06:50:18.278Z',
                exit: '2020-05-07T06:50:18.427Z',
                type: 'RuleSetNode',
                definitionId: '_54ABE1ED-61BE-45F9-812C-795A5D4ED35E'
              },
              {
                id: '2860e474-dffc-498c-8731-e6b6f0b5d4d4',
                nodeId: '16',
                name: 'StartProcess',
                enter: '2020-05-07T06:50:18.276Z',
                exit: '2020-05-07T06:50:18.278Z',
                type: 'StartNode',
                definitionId: '_1B11BEC9-402A-4E73-959A-296BD334CAB0'
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
        state: [ProcessInstanceState.Active],
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
        state: ['something'],
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
      query: GetProcessInstancesWithBusinessKeyDocument,
      variables: {
        state: [ProcessInstanceState.Active],
        offset: 0,
        limit: 10,
        businessKeys: [{ businessKey: { like: 'MQQ640' } }]
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
              },
              {
                id: '1d3d7ebe-79ec-4848-b1d6-d7c5a371b4dd',
                nodeId: '8',
                name: 'Confirm travel',
                enter: '2020-05-07T06:50:18.443Z',
                exit: null,
                type: 'HumanTaskNode',
                definitionId: '_2140F05A-364F-40B3-BB7B-B12927065DF8'
              },
              {
                id: '4fc774f5-c429-4b97-8981-bd60d1f59954',
                nodeId: '7',
                name: 'Join',
                enter: '2020-05-07T06:50:18.442Z',
                exit: '2020-05-07T06:50:18.443Z',
                type: 'Join',
                definitionId: '_5D0733B5-53FE-40E9-9900-4CC13419C67A'
              },
              {
                id: '60309b28-1dbe-49c3-b0d8-8cefb86be8b4',
                nodeId: '10',
                name: 'Book Flight',
                enter: '2020-05-07T06:50:18.439Z',
                exit: '2020-05-07T06:50:18.442Z',
                type: 'SubProcessNode',
                definitionId: '_F543B3F0-AB44-4A5B-BF17-8D9DEB505815'
              },
              {
                id: 'efa5a6c0-9470-4405-8609-620bdbfbb6d9',
                nodeId: '2',
                name: 'Book',
                enter: '2020-05-07T06:50:18.428Z',
                exit: '2020-05-07T06:50:18.439Z',
                type: 'Split',
                definitionId: '_175DC79D-C2F1-4B28-BE2D-B583DFABF70D'
              },
              {
                id: 'acc87f1f-e436-4364-b4d3-9069ddb359c9',
                nodeId: '14',
                name: 'Join',
                enter: '2020-05-07T06:50:18.428Z',
                exit: '2020-05-07T06:50:18.428Z',
                type: 'Join',
                definitionId: '_B34ADDEE-DEA5-47C5-A913-F8B85ED5641F'
              },
              {
                id: '6c20729a-3e2b-44c5-af5c-8554f21a7ebf',
                nodeId: '15',
                name: 'is visa required',
                enter: '2020-05-07T06:50:18.427Z',
                exit: '2020-05-07T06:50:18.428Z',
                type: 'Split',
                definitionId: '_5EA95D17-59A6-4567-92DF-74D36CE7F35A'
              },
              {
                id: '3fb92a0a-e88b-45a4-a48b-65f11a679b96',
                nodeId: '5',
                name: 'Visa check',
                enter: '2020-05-07T06:50:18.278Z',
                exit: '2020-05-07T06:50:18.427Z',
                type: 'RuleSetNode',
                definitionId: '_54ABE1ED-61BE-45F9-812C-795A5D4ED35E'
              },
              {
                id: '2860e474-dffc-498c-8731-e6b6f0b5d4d4',
                nodeId: '16',
                name: 'StartProcess',
                enter: '2020-05-07T06:50:18.276Z',
                exit: '2020-05-07T06:50:18.278Z',
                type: 'StartNode',
                definitionId: '_1B11BEC9-402A-4E73-959A-296BD334CAB0'
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
  abortedObj: { '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': 'travels' },
  setAbortedObj: jest.fn(),
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
  abortedObj: { '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': 'travels' },
  setAbortedObj: jest.fn(),
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
  abortedObj: { '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': 'travels' },
  setAbortedObj: jest.fn(),
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
  abortedObj: { '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': 'travels' },
  setAbortedObj: jest.fn(),
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
