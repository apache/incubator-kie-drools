import React from 'react';
import ProcessListPage from '../ProcessListPage';
import { GraphQL, getWrapperAsync } from '@kogito-apps/common';
import { MockedProvider } from '@apollo/react-testing';
import { BrowserRouter } from 'react-router-dom';
import { Button, EmptyStateBody, EmptyState } from '@patternfly/react-core';
import { act } from 'react-dom/test-utils';
import * as H from 'history';
import { match } from 'react-router';
jest.mock('../../../Organisms/ProcessListTable/ProcessListTable');
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

const match: match<{ domainName: string }> = {
  isExact: false,
  path: '/ProcessInstances',
  url: '/ProcessInstances',
  params: { domainName: 'domain-name' }
};

const routeComponentPropsMock1 = {
  history: H.createMemoryHistory(),
  location: {
    pathname: '/ProcessInstances',
    state: {
      filters: {
        status: [GraphQL.ProcessInstanceState.Active],
        businessKey: []
      }
    },
    hash: '',
    search: ''
  },
  match
};

const routeComponentPropsMock2 = {
  history: H.createMemoryHistory(),
  location: {
    pathname: '/ProcessInstances',
    state: {
      filters: {
        status: [GraphQL.ProcessInstanceState.Active],
        businessKey: ['MQQ640']
      }
    },
    hash: '',
    search: ''
  },
  match
};

const routeComponentPropsMock3 = {
  history: H.createMemoryHistory(),
  location: {
    pathname: '/ProcessInstances',
    state: {
      filters: {
        status: [],
        businessKey: []
      }
    },
    hash: '',
    search: ''
  },
  match
};

const mocks1 = [
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [GraphQL.ProcessInstanceState.Active] },
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
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [GraphQL.ProcessInstanceState.Active] },
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

const mocks3 = [
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [GraphQL.ProcessInstanceState.Active] },
          parentProcessInstanceId: { isNull: true }
        },
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
        where: {
          state: { in: [] },
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

const mocks4 = [
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [GraphQL.ProcessInstanceState.Active] },
          parentProcessInstanceId: { isNull: true },
          or: [{ businessKey: { like: 'MQQ640' } }]
        },
        offset: 0,
        limit: 10
      }
    },
    error: new Error('something went wrong')
  }
];
describe('ProcessListPage component tests', () => {
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
    const wrapper = await getWrapperAsync(
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
