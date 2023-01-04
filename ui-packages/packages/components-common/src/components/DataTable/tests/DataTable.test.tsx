/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import DataTable, { DataTableColumn } from '../DataTable';
import { gql } from 'apollo-boost';
import { MockedProvider } from '@apollo/react-testing';
import { Label } from '@patternfly/react-core';
import { mount } from 'enzyme';

// tslint:disable: no-string-literal
// tslint:disable: no-unexpected-multiline

jest.mock('uuid', () => {
  let value = 1;
  return () => value++;
});
const MockedComponent = (): React.ReactElement => {
  return <></>;
};
jest.mock('../../KogitoEmptyState/KogitoEmptyState', () =>
  Object.assign(jest.requireActual('../../KogitoEmptyState/KogitoEmptyState'), {
    KogitoEmptyState: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock('../../KogitoSpinner/KogitoSpinner');

const data = [
  {
    id: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
    description: null,
    name: 'VisaApplication',
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
    referenceName: 'Apply for visa'
  },
  {
    id: '047ec38d-5d57-4330-8c8d-9bd67b53a529',
    description: '',
    name: 'ConfirmTravel',
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
    outputs: '{}',
    referenceName: 'Confirm travel'
  }
];

const stateColumnTransformer = (value) => {
  if (!value) {
    return null;
  }
  return <Label>{value}</Label>;
};

const columns: DataTableColumn[] = [
  {
    label: 'ProcessId',
    path: '$.processId'
  },
  {
    label: 'Name',
    path: '$.name',
    isSortable: true
  },
  {
    label: 'Priority',
    path: '$.priority'
  },
  {
    label: 'ProcessInstanceId',
    path: '$.processInstanceId'
  },
  {
    label: 'State',
    path: '$.state',
    bodyCellTransformer: stateColumnTransformer,
    isSortable: true
  }
];
const GET_USER_TASKS_BY_STATE = gql`
  query getUserTasksByState($state: String, $orderBy: UserTaskInstanceOrderBy) {
    UserTaskInstances(where: { state: { in: $state } }, orderBy: $orderBy) {
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
            name: 'VisaApplication',
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
            referenceName: 'Apply for visa'
          },
          {
            id: '047ec38d-5d57-4330-8c8d-9bd67b53a529',
            description: '',
            name: 'ConfirmTravel',
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
            outputs: '{}',
            referenceName: 'Confirm travel'
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

    const wrapper = await mount(
      <MockedProvider mocks={mocks} addTypename={false}>
        <DataTable {...props} />
      </MockedProvider>
    );
    wrapper.update();
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

    const wrapper = await mount(
      <MockedProvider mocks={mocks} addTypename={false}>
        <DataTable {...props} />
      </MockedProvider>
    );
    wrapper.update();

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

    const wrapper = await mount(
      <MockedProvider mocks={mocks} addTypename={false}>
        <DataTable {...props} />
      </MockedProvider>
    );
    wrapper.update();

    expect(wrapper.find(DataTable)).toMatchSnapshot();
  });

  it('check sorting functionality', async () => {
    const props = {
      data,
      isLoading: false,
      networkStatus: 1,
      columns,
      error: undefined,
      refetch: jest.fn(),
      LoadingComponent: undefined,
      ErrorComponent: undefined,
      onSorting: jest.fn(),
      sortBy: {}
    };

    const wrapper = await mount(
      <MockedProvider mocks={mocks} addTypename={false}>
        <DataTable {...props} />
      </MockedProvider>
    );
    wrapper.update();

    wrapper
      .find('[aria-label="Data Table"]')
      .at(0)
      .props()
      ['onSort']({}, 1, 'asc');

    expect(props.onSorting).toHaveBeenCalledTimes(1);
  });
});
