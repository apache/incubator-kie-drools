import React from 'react';
import UserTaskDataTableContainer from '../UserTaskDataTableContainer';
import { getWrapperAsync } from '@kogito-apps/common';
import { MemoryRouter as Router } from 'react-router-dom';
import { MockedProvider } from '@apollo/react-testing';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};
// tslint:disable-next-line: no-var-requires
const mockGraphqlTypes = require('../../../../graphql/types');

jest.mock('../../../Molecules/UserTaskPageHeader/UserTaskPageHeader');
jest.mock('@kogito-apps/common', () => ({
  ...jest.requireActual('@kogito-apps/common'),
  DataTable: () => {
    return <MockedComponent />;
  }
}));
jest.unmock('../../../../graphql/types');

const mockUserTaskInstancesData = {
  UserTaskInstances: [
    {
      __typename: 'UserTaskInstance',
      actualOwner: null,
      adminGroups: [],
      adminUsers: [],
      completed: null,
      description: '',
      excludedUsers: [],
      id: '047ec38d-5d57-4330-8c8d-9bd67b53a529',
      inputs:
        '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null}}',
      name: 'Confirm travel',
      outputs: '{"ActorId":""}',
      potentialGroups: [],
      potentialUsers: [],
      priority: '1',
      processId: 'travels',
      processInstanceId: '9ae407dd-cdfa-4722-8a49-0a6d2e14550d',
      referenceName: 'ConfirmTravel',
      rootProcessId: null,
      rootProcessInstanceId: null,
      started: '2020-02-19T10:59:34.185Z',
      state: 'Ready'
    },
    {
      __typename: 'UserTaskInstance',
      actualOwner: null,
      adminGroups: [],
      adminUsers: [],
      completed: null,
      description: null,
      excludedUsers: [],
      id: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
      inputs:
        '{"Skippable":"true","trip":{"city":"Boston","country":"US","begin":"2020-02-19T23:00:00.000+01:00","end":"2020-02-26T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","traveller":{"firstName":"Rachel","lastName":"White","email":"rwhite@gorle.com","nationality":"Polish","address":{"street":"Cabalone","city":"Zerf","zipCode":"765756","country":"Poland"}},"Priority":"1"}"',
      name: 'Apply for visa',
      outputs: '{}',
      potentialGroups: [],
      potentialUsers: [],
      priority: '1',
      processId: 'travels',
      processInstanceId: '9ae7ce3b-d49c-4f35-b843-8ac3d22fa427',
      referenceName: 'VisaApplication',
      rootProcessId: null,
      rootProcessInstanceId: null,
      started: '2020-02-19T11:11:56.282Z',
      state: null
    }
  ]
};

describe('UserTaskDataTableContainer component tests', () => {
  it('Should render UserTaskDataTableContainer correctly', async () => {
    mockGraphqlTypes.useGetUserTasksByStatesQuery = jest.fn().mockReturnValue({
      loading: false,
      error: undefined,
      refetch: jest.fn(),
      networkStatus: 1,
      data: mockUserTaskInstancesData
    });

    const wrapper = await getWrapperAsync(
      <Router>
        <MockedProvider>
          <UserTaskDataTableContainer />
        </MockedProvider>
      </Router>,
      'UserTaskDataTableContainer'
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('Should render UserTaskDataTableContainer with no data', async () => {
    mockGraphqlTypes.useGetUserTasksByStatesQuery = jest.fn().mockReturnValue({
      loading: false,
      error: undefined,
      refetch: jest.fn(),
      networkStatus: 1
    });

    const wrapper = await getWrapperAsync(
      <Router>
        <MockedProvider>
          <UserTaskDataTableContainer />
        </MockedProvider>
      </Router>,
      'UserTaskDataTableContainer'
    );
    expect(wrapper).toMatchSnapshot();
    expect(mockGraphqlTypes.useGetUserTasksByStatesQuery).toBeCalled();
  });
});
