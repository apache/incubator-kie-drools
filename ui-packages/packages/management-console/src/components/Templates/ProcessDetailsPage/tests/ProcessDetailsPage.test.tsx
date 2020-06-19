import React from 'react';
import * as H from 'history';
import ProcessDetailsPage from '../ProcessDetailsPage';
import { MockedProvider } from '@apollo/react-testing';
import { BrowserRouter } from 'react-router-dom';
import { getWrapperAsync, GraphQL } from '@kogito-apps/common';
import GetProcessInstanceByIdDocument = GraphQL.GetProcessInstanceByIdDocument;
import ProcessInstanceState = GraphQL.ProcessInstanceState;

const props = {
  match: {
    params: {
      instanceID: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
    },
    url: '',
    isExact: false,
    path: ''
  },
  location: H.createLocation(''),
  history: H.createBrowserHistory()
};
const props1 = {
  match: {
    params: {
      instanceID: '8035b580-6ae4-4aa8-9ec0-e18e19809e0bc'
    },
    url: '',
    isExact: false,
    path: ''
  },
  location: H.createLocation(''),
  history: H.createBrowserHistory()
};

const mocks1 = [
  {
    request: {
      query: GetProcessInstanceByIdDocument,
      variables: {
        id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
      }
    },
    result: {
      data: {
        ProcessInstances: [
          {
            id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
            processId: 'Travels',
            processName: 'travels',
            businessKey: null,
            parentProcessInstanceId: null,
            parentProcessInstance: null,
            roles: [],
            variables:
              '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
            state: ProcessInstanceState.Active,
            start: '2019-10-22T03:40:44.089Z',
            lastUpdate: '2019-10-22T03:40:44.089Z',
            end: null,
            endpoint: 'http://localhost:4000',
            addons: ['process-management'],
            serviceUrl: 'http://localhost:4000',
            error: {
              nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
              message: 'Something went wrong'
            },
            childProcessInstances: [],
            nodes: [
              {
                id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
                nodeId: '9',
                name: 'StartProcess',
                enter: '2019-10-22T04:43:01.135Z',
                exit: '2019-10-22T04:43:01.135Z',
                type: 'StartNode',
                definitionId: 'StartEvent_1'
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
      query: GetProcessInstanceByIdDocument,
      variables: {
        id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
      }
    },
    result: {
      data: {
        ProcessInstances: [
          {
            id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
            processId: 'Travels',
            processName: 'travels',
            businessKey: null,
            parentProcessInstanceId: null,
            parentProcessInstance: null,
            roles: [],
            variables:
              '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
            state: ProcessInstanceState.Error,
            start: '2019-10-22T03:40:44.089Z',
            lastUpdate: '2019-10-22T03:40:44.089Z',
            end: null,
            endpoint: 'http://localhost:4000',
            addons: [],
            serviceUrl: null,
            error: {
              nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
              message: 'Something went wrong'
            },
            childProcessInstances: [],
            nodes: [
              {
                id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
                nodeId: '9',
                name: 'StartProcess',
                enter: '2019-10-22T04:43:01.135Z',
                exit: '2019-10-22T04:43:01.135Z',
                type: 'StartNode',
                definitionId: 'StartEvent_1'
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
      query: GetProcessInstanceByIdDocument,
      variables: {
        id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
      }
    },
    result: {
      data: {
        ProcessInstances: [
          {
            id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
            processId: 'Travels',
            processName: 'travels',
            businessKey: null,
            parentProcessInstanceId: null,
            parentProcessInstance: null,
            roles: [],
            variables:
              '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
            state: ProcessInstanceState.Suspended,
            start: '2019-10-22T03:40:44.089Z',
            lastUpdate: '2019-10-22T03:40:44.089Z',
            end: null,
            endpoint: 'http://localhost:4000',
            addons: ['process-management'],
            serviceUrl: 'http://localhost:4000',
            error: {
              nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
              message: 'Something went wrong'
            },
            childProcessInstances: [],
            nodes: [
              {
                id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
                nodeId: '9',
                name: 'StartProcess',
                enter: '2019-10-22T04:43:01.135Z',
                exit: '2019-10-22T04:43:01.135Z',
                type: 'StartNode',
                definitionId: 'StartEvent_1'
              }
            ]
          }
        ]
      }
    }
  }
];

describe('Process Details Page component tests', () => {
  let originalLocalStorage;
  beforeEach(() => {
    originalLocalStorage = Storage.prototype.getItem;
  });

  afterEach(() => {
    Storage.prototype.getItem = originalLocalStorage;
  });
  Date.now = jest.fn(() => 1487076708000);
  Storage.prototype.getItem = jest.fn(() =>
    JSON.stringify({ prev: '/ProcessInstances' })
  );
  it('snapshot testing in Active state', async () => {
    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks1} addTypename={false}>
        <BrowserRouter>
          <ProcessDetailsPage {...props} />
        </BrowserRouter>
      </MockedProvider>,
      'ProcessDetailsPage'
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot testing in Error state', async () => {
    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks2} addTypename={false}>
        <BrowserRouter>
          <ProcessDetailsPage {...props} />
        </BrowserRouter>
      </MockedProvider>,
      'ProcessDetailsPage'
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot testing in Suspended state', async () => {
    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks3} addTypename={false}>
        <BrowserRouter>
          <ProcessDetailsPage {...props} />
        </BrowserRouter>
      </MockedProvider>,
      'ProcessDetailsPage'
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot testing for error occurance', async () => {
    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks3} addTypename={false}>
        <BrowserRouter>
          <ProcessDetailsPage {...props1} />
        </BrowserRouter>
      </MockedProvider>,
      'ProcessDetailsPage'
    );
    expect(wrapper).toMatchSnapshot();
  });
});
