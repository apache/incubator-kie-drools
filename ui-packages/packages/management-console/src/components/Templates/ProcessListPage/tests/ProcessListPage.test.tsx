import React from 'react';
import ProcessListPage from '../ProcessListPage';
import { GraphQL, getWrapperAsync } from '@kogito-apps/common';
import { MockedProvider } from '@apollo/react-testing';
import { BrowserRouter } from 'react-router-dom';
import { Button, EmptyStateBody, EmptyState } from '@patternfly/react-core';
import { act } from 'react-dom/test-utils';
jest.mock('../../../Organisms/ProcessListTable/ProcessListTable');
jest.mock('../../../Atoms/ProcessListBulkInstances/ProcessListBulkInstances');
jest.mock('../../../Atoms/ProcessListModal/ProcessListModal');
const MockedComponent = (): React.ReactElement => {
  return <></>;
};
jest.mock('@kogito-apps/common', () => ({
  ...jest.requireActual('@kogito-apps/common'),
  LoadMore: () => {
    return <MockedComponent />;
  }
}));
jest.mock('@patternfly/react-icons', () => ({
  ...jest.requireActual('@patternfly/react-icons'),
  ExclamationTriangleIcon: () => {
    return <MockedComponent />;
  },
  ExclamationCircleIcon: () => {
    return <MockedComponent />;
  }
}));

const routeComponentPropsMock1 = {
  history: {} as any,
  location: {
    pathname: '/ProcessInstances',
    state: {
      filters: {
        status: [GraphQL.ProcessInstanceState.Active],
        businessKey: []
      }
    }
  } as any,
  match: {
    params: {}
  } as any
};

const routeComponentPropsMock2 = {
  history: {} as any,
  location: {
    pathname: '/ProcessInstances',
    state: {
      filters: {
        status: [GraphQL.ProcessInstanceState.Active],
        businessKey: ['MQQ640']
      }
    }
  } as any,
  match: {
    params: {}
  } as any
};

const routeComponentPropsMock3 = {
  history: {} as any,
  location: {
    pathname: '/ProcessInstances',
    state: {
      filters: {
        status: [],
        businessKey: []
      }
    }
  } as any,
  match: {
    params: {}
  } as any
};

const mocks1 = [
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        state: [GraphQL.ProcessInstanceState.Active],
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
            businessKey: null,
            rootProcessInstanceId: null,
            parentProcessInstanceId: null,
            parentProcessInstance: null,
            roles: [],
            variables:
              '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2020-05-08T03:30:00.000+05:30","arrival":"2020-05-09T03:30:00.000+05:30"},"hotel":{"name":"Perfect hotel","address":{"street":"street","city":"Bengaluru","zipCode":"12345","country":"India"},"phone":"09876543","bookingNumber":"XX-012345","room":null},"trip":{"city":"Bengaluru","country":"India","begin":"2020-05-08T03:30:00.000+05:30","end":"2020-05-09T03:30:00.000+05:30","visaRequired":false},"traveller":{"firstName":"Ajay","lastName":"Jaganathan","email":"ajaganat@redhat.com","nationality":"Polish","address":{"street":"Bangalore","city":"Bangalore","zipCode":"560093","country":"Poland"}}}',
            state: GraphQL.ProcessInstanceState.Active,
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
      query: GraphQL.GetProcessInstancesWithBusinessKeyDocument,
      variables: {
        state: [GraphQL.ProcessInstanceState.Active],
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

const mocks3 = [
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        state: [GraphQL.ProcessInstanceState.Active],
        offset: 0,
        limit: 10
      }
    },
    error: new Error('something went wrong')
  }
];

const mocks5 = [
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        state: [],
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

const mocks4 = [
  {
    request: {
      query: GraphQL.GetProcessInstancesWithBusinessKeyDocument,
      variables: {
        state: [GraphQL.ProcessInstanceState.Active],
        offset: 0,
        limit: 10,
        businessKeys: [{ businessKey: { like: 'MQQ640' } }]
      }
    },
    error: new Error('something went wrong')
  }
];
describe('ProcessListPage component tests', () => {
  /* tslint:disable */
  it('on FilterClick tests- without businesskey', async () => {
    let wrapper = await getWrapperAsync(
      <BrowserRouter>
        <MockedProvider mocks={mocks1} addTypename={false}>
          <ProcessListPage {...routeComponentPropsMock1} />
        </MockedProvider>
      </BrowserRouter>,
      'ProcessListPage'
    );
    await act(async () => {
      wrapper
        .find('#apply-filter-button')
        .find(Button)
        .find('button')
        .simulate('click');
    });
    wrapper = wrapper.update();
    expect(
      wrapper.find('MockedProcessListTable').props()['initData'][
        'ProcessInstances'
      ][0]['id']
    ).toEqual(mocks1[0].result.data.ProcessInstances[0].id);
  });

  it('on FilterClick tests- with businesskey', async () => {
    let wrapper = await getWrapperAsync(
      <BrowserRouter>
        <MockedProvider mocks={mocks2} addTypename={false}>
          <ProcessListPage {...routeComponentPropsMock2} />
        </MockedProvider>
      </BrowserRouter>,
      'ProcessListPage'
    );
    await act(async () => {
      wrapper
        .find('#apply-filter-button')
        .find(Button)
        .find('button')
        .simulate('click');
    });
    wrapper = wrapper.update();
    expect(
      wrapper.find('MockedProcessListTable').props()['initData'][
        'ProcessInstances'
      ][0]['businessKey']
    ).toEqual(mocks2[0].result.data.ProcessInstances[0].businessKey);
  });

  it('error in query - without businesskey', async () => {
    let wrapper = await getWrapperAsync(
      <BrowserRouter>
        <MockedProvider mocks={mocks3} addTypename={false}>
          <ProcessListPage {...routeComponentPropsMock1} />
        </MockedProvider>
      </BrowserRouter>,
      'ProcessListPage'
    );
    await act(async () => {
      wrapper
        .find('#apply-filter-button')
        .find(Button)
        .find('button')
        .simulate('click');
    });
    wrapper = wrapper.update();
    wrapper = wrapper.find(EmptyState);
    expect(
      wrapper
        .find(EmptyStateBody)
        .children()
        .html()
        .includes('An error occurred while accessing data.')
    ).toBeTruthy();
    expect(wrapper).toMatchSnapshot();
  });

  it('error in query - with businesskey', async () => {
    let wrapper = await getWrapperAsync(
      <BrowserRouter>
        <MockedProvider mocks={mocks4} addTypename={false}>
          <ProcessListPage {...routeComponentPropsMock2} />
        </MockedProvider>
      </BrowserRouter>,
      'ProcessListPage'
    );
    await act(async () => {
      wrapper
        .find('#apply-filter-button')
        .find(Button)
        .find('button')
        .simulate('click');
    });
    wrapper = wrapper.update();
    wrapper = wrapper.find(EmptyState);
    expect(
      wrapper
        .find(EmptyStateBody)
        .children()
        .html()
        .includes('An error occurred while accessing data.')
    ).toBeTruthy();
    expect(wrapper).toMatchSnapshot();
  });

  it('Snapshot tests for no status selected', async () => {
    let wrapper = await getWrapperAsync(
      <BrowserRouter>
        <MockedProvider mocks={mocks5} addTypename={false}>
          <ProcessListPage {...routeComponentPropsMock3} />
        </MockedProvider>
      </BrowserRouter>,
      'ProcessListPage'
    );
    wrapper = wrapper.find(EmptyState);
    expect(wrapper).toMatchSnapshot();
  });

  it('reset click in no status found test', async () => {
    let wrapper = await getWrapperAsync(
      <BrowserRouter>
        <MockedProvider mocks={mocks5} addTypename={false}>
          <ProcessListPage {...routeComponentPropsMock3} />
        </MockedProvider>
      </BrowserRouter>,
      'ProcessListPage'
    );
    await act(async () => {
      wrapper
        .find(EmptyState)
        .find(Button)
        .find('button')
        .simulate('click');
    });
  });
});
