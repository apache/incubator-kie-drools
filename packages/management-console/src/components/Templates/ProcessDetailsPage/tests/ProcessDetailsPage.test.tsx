import React from 'react';
import ProcessDetailsPage from '../ProcessDetailsPage';
import { MockedProvider } from '@apollo/react-testing';
import gql from 'graphql-tag';
import { MemoryRouter } from 'react-router-dom';
import { getWrapper } from '@kogito-apps/common';
import * as H from 'history';

const props = {
  match: {
    params: { instanceID: '1232131' },
    url: '',
    isExact: false,
    path: ''
  },
  location: H.createLocation(''),
  history: H.createBrowserHistory()
};

const GET_PROCESS_INSTANCE = gql`
  query getProcessInstanceById($id: String) {
    ProcessInstances(where: { id: { equal: $id } }) {
      id
      processId
      parentProcessInstanceId
      roles
      variables
      state
      nodes {
        id
        name
        type
        enter
        exit
      }
    }
  }
`;

const mocks = [
  {
    request: {
      query: GET_PROCESS_INSTANCE,
      variables: {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e'
      }
    },
    result: {
      data: {
        ProcessInstances: {
          id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
          processId: 'flightBooking',
          parentProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          processName: 'FlightBooking',
          roles: [],
          state: 'COMPLETED',
          variables:
            '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
          nodes: [
            {
              name: 'End Event 1',
              definitionId: 'EndEvent_1',
              id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
              enter: '2019-10-22T04:43:01.144Z',
              exit: '2019-10-22T04:43:01.144Z',
              type: 'EndNode'
            },
            {
              name: 'Book flight',
              definitionId: 'ServiceTask_1',
              id: '2f588da5-a323-4111-9017-3093ef9319d1',
              enter: '2019-10-22T04:43:01.144Z',
              exit: '2019-10-22T04:43:01.144Z',
              type: 'WorkItemNode'
            },
            {
              name: 'StartProcess',
              definitionId: 'StartEvent_1',
              id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
              enter: '2019-10-22T04:43:01.144Z',
              exit: '2019-10-22T04:43:01.144Z',
              type: 'StartNode'
            }
          ],
          childProcessInstanceId: ['c54ca5b0-b975-46e2-a9a0-6a86bf7ac21eaccd']
        }
      }
    }
  }
];

describe('Process Details Page component', () => {
  it('Sample test case', () => {
    const wrapper = getWrapper(
      <MemoryRouter>
        <MockedProvider mocks={mocks} addTypename={false}>
          <ProcessDetailsPage {...props} />
        </MockedProvider>
      </MemoryRouter>
    , 'ProcessDetailsPage');
    expect(wrapper).toMatchSnapshot();
  });
});
