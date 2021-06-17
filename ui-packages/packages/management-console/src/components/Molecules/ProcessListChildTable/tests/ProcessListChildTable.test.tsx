import { MockedProvider } from '@apollo/react-testing';
import { GraphQL } from '@kogito-apps/common';
import { mount } from 'enzyme';
import { Checkbox } from '@patternfly/react-core';
import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import ProcessListChildTable from '../ProcessListChildTable';
import _ from 'lodash';
import { act } from 'react-dom/test-utils';
import wait from 'waait';
jest.mock('../../../Atoms/ErrorPopover/ErrorPopover');
jest.mock('../../DisablePopup/DisablePopup');
jest.mock('../../../Atoms/ProcessListActionsKebab/ProcessListActionsKebab');
Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20
describe('Process List Child Table tests', () => {
  const childData = {
    id: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    processId: 'travels',
    businessKey: 'T1234',
    parentProcessInstanceId: '538f9feb-5a14-4096-b791-2055b38da7c6',
    parentProcessInstance: {
      id: '538f9feb-5a14-4096-b791-2055b38da7c6',
      processId: 'travels',
      businessKey: 'Tra234',
      parentProcessInstanceId: null,
      parentProcessInstance: null,
      processName: 'travels',
      rootProcessInstanceId: null,
      roles: [],
      state: GraphQL.ProcessInstanceState.Active,
      addons: [
        'jobs-management',
        'prometheus-monitoring',
        'process-management'
      ],
      start: '2019-10-22T03:40:44.089Z',
      error: {
        nodeDefinitionId: '__a1e139d5-4e77-48c9-84ae-34578e9817n',
        message: 'Something went wrong'
      },
      lastUpdate: '2019-10-22T03:40:44.089Z',
      serviceUrl: 'http://localhost:4000',
      endpoint: 'http://localhost:4000',
      variables:
        '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-23T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-23T22:00:00Z[UTC]","city":"New York","country":"US","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"New York","country":"US","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Berlin","country":"Germany","street":"Bakers","zipCode":"100200"},"email":"cristiano@redhat.com","firstName":"Cristiano","lastName":"Nicolai","nationality":"German"}}',
      nodes: [],
      milestones: [],
      childProcessInstances: []
    },
    processName: 'travels',
    roles: [],
    state: GraphQL.ProcessInstanceState.Active,
    rootProcessInstanceId: '538f9feb-5a14-4096-b791-2055b38da7c6',
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    start: '2019-10-22T03:40:44.089Z',
    end: '2019-10-22T05:40:44.089Z',
    error: {
      nodeDefinitionId: '_2140F05A-364F-40B3-BB7B-B12927065DF8',
      message: 'Something went wrong'
    },
    lastUpdate: '2019-10-22T03:40:44.089Z',
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
    nodes: []
  };

  const props = {
    parentProcessId: '538f9feb-5a14-4096-b791-2055b38da7c6',
    filters: {
      status: [GraphQL.ProcessInstanceState.Active],
      businessKey: []
    },
    initData: {
      ProcessInstances: [
        {
          id: '538f9feb-5a14-4096-b791-2055b38da7c6',
          processId: 'travels',
          businessKey: 'Tra234',
          parentProcessInstanceId: null,
          parentProcessInstance: null,
          processName: 'travels',
          rootProcessInstanceId: null,
          roles: [],
          state: GraphQL.ProcessInstanceState.Active,
          addons: [
            'jobs-management',
            'prometheus-monitoring',
            'process-management'
          ],
          start: '2019-10-22T03:40:44.089Z',
          error: {
            nodeDefinitionId: '__a1e139d5-4e77-48c9-84ae-34578e9817n',
            message: 'Something went wrong'
          },
          lastUpdate: '2019-10-22T03:40:44.089Z',
          serviceUrl: 'http://localhost:4000',
          endpoint: 'http://localhost:4000',
          variables:
            '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-23T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-23T22:00:00Z[UTC]","city":"New York","country":"US","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"New York","country":"US","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Berlin","country":"Germany","street":"Bakers","zipCode":"100200"},"email":"cristiano@redhat.com","firstName":"Cristiano","lastName":"Nicolai","nationality":"German"}}',
          nodes: [],
          milestones: [],
          childProcessInstances: [childData]
        }
      ]
    },
    setInitData: jest.fn(),
    setSelectedInstances: jest.fn(),
    selectedInstances: [],
    setSelectableInstances: jest.fn()
  };

  const mockData = [
    {
      request: {
        query: GraphQL.GetChildInstancesDocument,
        variables: {
          rootProcessInstanceId: props.parentProcessId
        }
      },
      result: {
        data: {
          ProcessInstances: [childData]
        }
      }
    }
  ];

  const mockDatawithError = [
    {
      request: {
        query: GraphQL.GetChildInstancesDocument,
        variables: {
          rootProcessInstanceId: props.parentProcessId
        }
      },
      result: {
        data: undefined,
        error: {
          message: 'Expected a value of type JobStatus but received: CANCELLED'
        }
      }
    }
  ];
  const mockDataWihEmptyResults = [
    {
      request: {
        query: GraphQL.GetChildInstancesDocument,
        variables: {
          rootProcessInstanceId: props.parentProcessId
        }
      },
      result: {
        data: {
          ProcessInstances: []
        }
      }
    }
  ];
  it('snapshot of children in table', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mockData} addTypename={false}>
          <BrowserRouter>
            <ProcessListChildTable {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper.update();
      wrapper = wrapper.find('ProcessListChildTable');
    });

    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot for error', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mockDatawithError} addTypename={false}>
          <BrowserRouter>
            <ProcessListChildTable {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessListChildTable');
    });
    const serverErrorComponent = wrapper.find('ServerErrors');
    expect(serverErrorComponent.exists()).toBeTruthy();
    expect(serverErrorComponent).toMatchSnapshot();
  });

  it('snapshot empty results', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mockDataWihEmptyResults} addTypename={false}>
          <BrowserRouter>
            <ProcessListChildTable {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper.update();
    });
    const emptyState = wrapper.find('EmptyState');
    expect(emptyState.exists()).toBeTruthy();
    expect(emptyState).toMatchSnapshot();
  });
  it('checkbox click tests - selected', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mockData} addTypename={false}>
          <BrowserRouter>
            <ProcessListChildTable {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper.update();
      wrapper
        .find(Checkbox)
        .find('input')
        .simulate('change', { target: { checked: true } });
    });
    expect(props.setSelectedInstances).toHaveBeenCalled();
  });
  it('checkbox click tests - unselected', async () => {
    const clonedProps = _.cloneDeep(props);
    clonedProps.initData.ProcessInstances[0].childProcessInstances[0][
      'isSelected'
    ] = true;
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mockData} addTypename={false}>
          <BrowserRouter>
            <ProcessListChildTable {...clonedProps} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessListChildTable');
      wrapper
        .find(Checkbox)
        .find('input')
        .simulate('change', { target: { checked: false } });
    });
    expect(clonedProps.setSelectedInstances).toHaveBeenCalled();
  });
});
