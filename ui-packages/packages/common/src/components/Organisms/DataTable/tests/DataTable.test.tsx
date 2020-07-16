import React from 'react';
import DataTable from '../DataTable';
import { gql } from 'apollo-boost';
import { MockedProvider } from '@apollo/react-testing';
import { getWrapperAsync } from '@kogito-apps/common';
import { Label } from '@patternfly/react-core';
import {
  ICell,
  ITransform,
  IFormatterValueType
} from '@patternfly/react-table';

jest.mock('uuid', () => {
  let value = 1;
  return () => value++;
});
const MockedComponent = (): React.ReactElement => {
  return <></>;
};
jest.mock('../../../Atoms/KogitoEmptyState/KogitoEmptyState', () => ({
  ...jest.requireActual('../../../Atoms/KogitoEmptyState/KogitoEmptyState'),
  KogitoEmptyState: () => {
    return <MockedComponent />;
  }
}));
jest.mock('../../../Atoms/KogitoSpinner/KogitoSpinner');

const data = [
  {
    id: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
    description: null,
    name: 'Apply for visa',
    priority: '1',
    processInstanceId: '9ae7ce3b-d49c-4f35-b843-8ac3d22fa427',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-02-19T11:11:56.282Z',
    excludedUsers: [],
    potentialGroups: [],
    potentialUsers: [],
    inputs:
      '{"Skippable":"true","trip":{"city":"Boston","country":"US","begin":"2020-02-19T23:00:00.000+01:00","end":"2020-02-26T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","traveller":{"firstName":"Rachel","lastName":"White","email":"rwhite@gorle.com","nationality":"Polish","address":{"street":"Cabalone","city":"Zerf","zipCode":"765756","country":"Poland"}},"Priority":"1"}"',
    outputs: '{}',
    referenceName: 'VisaApplication'
  },
  {
    id: '047ec38d-5d57-4330-8c8d-9bd67b53a529',
    description: '',
    name: 'Confirm travel',
    priority: '1',
    processInstanceId: '9ae407dd-cdfa-4722-8a49-0a6d2e14550d',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-02-19T10:59:34.185Z',
    excludedUsers: [],
    potentialGroups: [],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null}}',
    outputs: '{"ActorId":""}',
    referenceName: 'ConfirmTravel'
  }
];
const stateColumnTransformer: ITransform = (value: IFormatterValueType) => {
  if (!value) {
    return null;
  }
  const { title } = value;
  return {
    children: <Label>{title}</Label>
  };
};
const columns: ICell[] = [
  {
    title: 'ProcessId',
    data: 'processId'
  },
  {
    title: 'Name',
    data: 'name'
  },
  {
    title: 'Priority',
    data: 'priority'
  },
  {
    title: 'ProcessInstanceId',
    data: 'processInstanceId'
  },
  {
    title: 'State',
    data: 'state',
    cellTransforms: [stateColumnTransformer]
  }
];
const GET_USER_TASKS_BY_STATE = gql`
  query getUserTasksByState($state: String) {
    UserTaskInstances(where: { state: { in: $state } }) {
      id
      description
      name
      priority
      processInstanceId
      processId
      rootProcessInstanceId
      rootProcessId
      state
      actualOwner
      adminGroups
      adminUsers
      completed
      started
      excludedUsers
      potentialGroups
      potentialUsers
      inputs
      outputs
      referenceName
    }
  }
`;
const mocks = [
  {
    request: {
      query: GET_USER_TASKS_BY_STATE,
      variables: {
        state: ['Ready']
      }
    },
    result: {
      data: {
        UserTaskInstances: [
          {
            id: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
            description: null,
            name: 'Apply for visa',
            priority: '1',
            processInstanceId: '9ae7ce3b-d49c-4f35-b843-8ac3d22fa427',
            processId: 'travels',
            rootProcessInstanceId: null,
            rootProcessId: null,
            state: 'Ready',
            actualOwner: null,
            adminGroups: [],
            adminUsers: [],
            completed: null,
            started: '2020-02-19T11:11:56.282Z',
            excludedUsers: [],
            potentialGroups: [],
            potentialUsers: [],
            inputs:
              '{"Skippable":"true","trip":{"city":"Boston","country":"US","begin":"2020-02-19T23:00:00.000+01:00","end":"2020-02-26T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","traveller":{"firstName":"Rachel","lastName":"White","email":"rwhite@gorle.com","nationality":"Polish","address":{"street":"Cabalone","city":"Zerf","zipCode":"765756","country":"Poland"}},"Priority":"1"}"',
            outputs: '{}',
            referenceName: 'VisaApplication'
          },
          {
            id: '047ec38d-5d57-4330-8c8d-9bd67b53a529',
            description: '',
            name: 'Confirm travel',
            priority: '1',
            processInstanceId: '9ae407dd-cdfa-4722-8a49-0a6d2e14550d',
            processId: 'travels',
            rootProcessInstanceId: null,
            rootProcessId: null,
            state: 'Ready',
            actualOwner: null,
            adminGroups: [],
            adminUsers: [],
            completed: null,
            started: '2020-02-19T10:59:34.185Z',
            excludedUsers: [],
            potentialGroups: [],
            potentialUsers: [],
            inputs:
              '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null}}',
            outputs: '{"ActorId":""}',
            referenceName: 'ConfirmTravel'
          }
        ]
      }
    }
  }
];

describe('DataTable component tests', () => {
  it('Should render DataTable correctly', async () => {
    const props = {
      data,
      isLoading: false,
      columns,
      networkStatus: 1,
      error: undefined,
      refetch: jest.fn(),
      LoadingComponent: undefined,
      ErrorComponent: undefined
    };

    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks} addTypename={false}>
        <DataTable {...props} />
      </MockedProvider>,
      'DataTable'
    );

    expect(wrapper.find(DataTable)).toMatchSnapshot();
  });

  it('Should render ErrorComponent', async () => {
    const props = {
      data: undefined,
      isLoading: false,
      columns,
      networkStatus: 1,
      error: {},
      refetch: jest.fn(),
      LoadingComponent: undefined,
      ErrorComponent: undefined
    };

    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks} addTypename={false}>
        <DataTable {...props} />
      </MockedProvider>,
      'DataTable'
    );

    expect(wrapper.find(DataTable)).toMatchSnapshot();
  });

  it('Should render LoadingComponent', async () => {
    const props = {
      data: undefined,
      isLoading: true,
      columns,
      networkStatus: 1,
      error: undefined,
      refetch: jest.fn(),
      LoadingComponent: undefined,
      ErrorComponent: undefined
    };

    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks} addTypename={false}>
        <DataTable {...props} />
      </MockedProvider>,
      'DataTable'
    );

    expect(wrapper.find(DataTable)).toMatchSnapshot();
  });

  it('Should render DataTable correctly even no columns configuration provided', async () => {
    const props = {
      data,
      isLoading: false,
      networkStatus: 1,
      error: undefined,
      refetch: jest.fn(),
      LoadingComponent: undefined,
      ErrorComponent: undefined
    };

    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks} addTypename={false}>
        <DataTable {...props} />
      </MockedProvider>,
      'DataTable'
    );

    expect(wrapper.find(DataTable)).toMatchSnapshot();
  });
});
