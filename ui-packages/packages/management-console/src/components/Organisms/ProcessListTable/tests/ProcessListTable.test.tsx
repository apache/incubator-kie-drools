import { GraphQL, KogitoSpinner } from '@kogito-apps/common';
import { mount } from 'enzyme';
import React from 'react';
import ProcessListTable from '../ProcessListTable';
import { BrowserRouter } from 'react-router-dom';
import { Button, Checkbox } from '@patternfly/react-core';
import _ from 'lodash';
import { act } from 'react-dom/test-utils';
import axios from 'axios';
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;
jest.mock('../../../Molecules/ProcessListChildTable/ProcessListChildTable');
jest.mock('../../../Atoms/ProcessListModal/ProcessListModal');
jest.mock('../../../Atoms/ErrorPopover/ErrorPopover');
jest.mock('../../../Molecules/DisablePopup/DisablePopup');
jest.mock('../../../Atoms/ProcessListActionsKebab/ProcessListActionsKebab');
Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20
const data = {
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
      state: GraphQL.ProcessInstanceState.Error,
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
      isSelected: false,
      childProcessInstances: [
        {
          id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21eajabbcc',
          processId: 'travels',
          businessKey: 'TP444',
          parentProcessInstanceId: '538f9feb-5a14-4096-b791-2055b38da7c6',
          parentProcessInstance: null,
          rootProcessInstanceId: '538f9feb-5a14-4096-b791-2055b38da7c6',
          processName: 'FlightBooking test 2',
          roles: [],
          state: GraphQL.ProcessInstanceState.Active,
          serviceUrl: 'http://localhost:4000',
          lastUpdate: '2019-10-22T03:40:44.089Z',
          endpoint: 'http://localhost:4000',
          addons: ['process-management'],
          error: {
            nodeDefinitionId: 'a1e139d5-81c77-48c9-84ae-34578e90433n',
            message: 'Something went wrong'
          },
          start: '2019-10-22T03:40:44.089Z',
          end: '2019-10-22T05:40:44.089Z',
          variables:
            '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
          nodes: [],
          isSelected: true,
          milestones: [],
          childProcessInstances: []
        }
      ]
    },
    {
      id: '538f9feb-5a14-4096-b791-2055b38da7c6',
      processId: 'travels',
      businessKey: 'Tra234',
      parentProcessInstanceId: null,
      parentProcessInstance: null,
      processName: 'travels',
      rootProcessInstanceId: null,
      roles: [],
      state: GraphQL.ProcessInstanceState.Error,
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
      isSelected: true,
      childProcessInstances: ['538f9feb-5a14-4096-b791-2055b38da7c6child']
    }
  ]
};

const props = {
  initData: data,
  setInitData: jest.fn(),
  loading: false,
  filters: {
    status: [GraphQL.ProcessInstanceState.Active],
    businessKey: []
  },
  expanded: {
    0: false,
    1: false
  },
  setExpanded: jest.fn(),
  setSelectedInstances: jest.fn(),
  selectedInstances: [],
  setSelectableInstances: jest.fn(),
  setIsAllChecked: jest.fn(),
  selectableInstances: 0,
  onSort: jest.fn(),
  sortBy: { lastUpdate: GraphQL.OrderBy.Desc }
};

describe('ProcessListPage tests', () => {
  it('snapshot test for process list - without expanded', () => {
    const wrapper = mount(
      <BrowserRouter>
        <ProcessListTable {...props} />
      </BrowserRouter>
    ).find('ProcessListTable');
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot test for disabled process and non "error" state', () => {
    const clonedProps = _.cloneDeep(props);
    clonedProps.initData.ProcessInstances[0].state =
      GraphQL.ProcessInstanceState.Active;
    clonedProps.initData.ProcessInstances[0].addons = [];
    clonedProps.initData.ProcessInstances[0].serviceUrl = null;
    const wrapper = mount(
      <BrowserRouter>
        <ProcessListTable {...clonedProps} />
      </BrowserRouter>
    ).find('ProcessListTable');
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot for loading state', () => {
    const wrapper = mount(
      <BrowserRouter>
        <ProcessListTable {...{ ...props, loading: true, initData: {} }} />
      </BrowserRouter>
    ).find('ProcessListTable');
    const spinner = wrapper.find(KogitoSpinner);
    expect(spinner.exists()).toBeTruthy();
    expect(spinner).toMatchSnapshot();
  });
  it('snapshot test for process list - with expanded', async () => {
    const clonedProps = _.cloneDeep(props);
    clonedProps.expanded = {
      0: true,
      1: false
    };
    clonedProps.selectedInstances = [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21eajabbcc',
        processId: 'travels',
        businessKey: 'TP444',
        parentProcessInstanceId: '538f9feb-5a14-4096-b791-2055b38da7c6',
        parentProcessInstance: null,
        rootProcessInstanceId: '538f9feb-5a14-4096-b791-2055b38da7c6',
        processName: 'FlightBooking test 2',
        roles: [],
        state: GraphQL.ProcessInstanceState.Completed,
        serviceUrl: null,
        lastUpdate: '2019-10-22T03:40:44.089Z',
        endpoint: 'http://localhost:4000',
        addons: [],
        error: {
          nodeDefinitionId: 'a1e139d5-81c77-48c9-84ae-34578e90433n',
          message: 'Something went wrong'
        },
        start: '2019-10-22T03:40:44.089Z',
        end: '2019-10-22T05:40:44.089Z',
        variables:
          '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
        nodes: [],
        isSelected: true,
        milestones: [],
        childProcessInstances: []
      }
    ];
    clonedProps.selectableInstances = 1;

    const wrapper = mount(
      <BrowserRouter>
        <ProcessListTable {...clonedProps} />
      </BrowserRouter>
    ).find('ProcessListTable');
    await act(async () => {
      wrapper.find('CollapseColumn').at(0).find(Button).simulate('click');
    });
    const ProcessListChildTable = wrapper
      .update()
      .find('MockedProcessListChildTable');
    expect(ProcessListChildTable.exists()).toBeTruthy();
    expect(ProcessListChildTable).toMatchSnapshot();
  });
  it('checkbox click tests - selected', async () => {
    const clonedProps = _.cloneDeep(props);
    let wrapper = mount(
      <BrowserRouter>
        <ProcessListTable {...clonedProps} />
      </BrowserRouter>
    ).find('ProcessListTable');
    await act(async () => {
      wrapper
        .find(Checkbox)
        .at(0)
        .find('input')
        .simulate('change', { target: { checked: true } });
    });
    wrapper = wrapper.update();
    expect(props.setSelectedInstances).toHaveBeenCalled();
  });
  it('checkbox click tests - unselected', async () => {
    let wrapper = mount(
      <BrowserRouter>
        <ProcessListTable {...props} />
      </BrowserRouter>
    ).find('ProcessListTable');
    await act(async () => {
      wrapper
        .find(Checkbox)
        .at(1)
        .find('input')
        .simulate('change', { target: { checked: false } });
    });
    wrapper = wrapper.update();
    expect(props.setSelectedInstances).toHaveBeenCalled();
  });
  describe('skip call tests', () => {
    const wrapper = mount(
      <BrowserRouter>
        <ProcessListTable {...props} />
      </BrowserRouter>
    ).find('ProcessListTable');
    it('on skip success', async () => {
      mockedAxios.post.mockResolvedValue({});
      await act(async () => {
        wrapper
          .find('MockedProcessListActionsKebab')
          .at(0)
          .props()
          ['onSkipClick'](props.initData.ProcessInstances[0]);
      });
      const skipSuccessWrapper = wrapper.update();
      expect(
        skipSuccessWrapper.find('MockedProcessListModal').exists()
      ).toBeTruthy();
      expect(
        skipSuccessWrapper.find('MockedProcessListModal').props()[
          'modalContent'
        ]
      ).toEqual('The process travels was successfully skipped.');
    });
    it('on skip failure', async () => {
      mockedAxios.post.mockRejectedValue({ message: '404 error' });
      await act(async () => {
        wrapper
          .find('MockedProcessListActionsKebab')
          .at(0)
          .props()
          ['onSkipClick'](props.initData.ProcessInstances[0]);
      });
      const skipFailureWrapper = wrapper.update();
      expect(
        skipFailureWrapper.find('MockedProcessListModal').exists()
      ).toBeTruthy();
      expect(
        skipFailureWrapper.find('MockedProcessListModal').props()[
          'modalContent'
        ]
      ).toEqual('The process travels failed to skip. Message: "404 error"');
    });
  });

  describe('Retry call tests', () => {
    const wrapper = mount(
      <BrowserRouter>
        <ProcessListTable {...props} />
      </BrowserRouter>
    ).find('ProcessListTable');
    it('on retry success', async () => {
      mockedAxios.post.mockResolvedValue({});
      await act(async () => {
        wrapper
          .find('MockedProcessListActionsKebab')
          .at(0)
          .props()
          ['onRetryClick'](props.initData.ProcessInstances[0]);
      });
      const retrySuccessWrapper = wrapper.update();
      expect(
        retrySuccessWrapper.find('MockedProcessListModal').exists()
      ).toBeTruthy();
      expect(
        retrySuccessWrapper.find('MockedProcessListModal').props()[
          'modalContent'
        ]
      ).toEqual('The process travels was successfully re-executed.');
    });
    it('on retry failure', async () => {
      mockedAxios.post.mockRejectedValue({ message: '404 error' });
      await act(async () => {
        wrapper
          .find('MockedProcessListActionsKebab')
          .at(0)
          .props()
          ['onRetryClick'](props.initData.ProcessInstances[0]);
      });
      const retryFailureWrapper = wrapper.update();
      expect(
        retryFailureWrapper.find('MockedProcessListModal').exists()
      ).toBeTruthy();
      expect(
        retryFailureWrapper.find('MockedProcessListModal').props()[
          'modalContent'
        ]
      ).toEqual(
        'The process travels failed to re-execute. Message: "404 error"'
      );
    });
  });
  describe('Abort call tests', () => {
    const wrapper = mount(
      <BrowserRouter>
        <ProcessListTable {...props} />
      </BrowserRouter>
    ).find('ProcessListTable');
    it('on Abort success', async () => {
      mockedAxios.delete.mockResolvedValue({});
      await act(async () => {
        wrapper
          .find('MockedProcessListActionsKebab')
          .at(0)
          .props()
          ['onAbortClick'](props.initData.ProcessInstances[0]);
      });
      const abortSuccessWrapper = wrapper.update();
      expect(
        abortSuccessWrapper.find('MockedProcessListModal').exists()
      ).toBeTruthy();
      expect(
        abortSuccessWrapper.find('MockedProcessListModal').props()[
          'modalContent'
        ]
      ).toEqual('The process travels was successfully aborted.');
    });
    it('on retry failure', async () => {
      mockedAxios.delete.mockRejectedValue({ message: '404 error' });
      await act(async () => {
        wrapper
          .find('MockedProcessListActionsKebab')
          .at(0)
          .props()
          ['onAbortClick'](props.initData.ProcessInstances[0]);
      });
      const abortFailureWrapper = wrapper.update();
      expect(
        abortFailureWrapper.find('MockedProcessListModal').exists()
      ).toBeTruthy();
      expect(
        abortFailureWrapper.find('MockedProcessListModal').props()[
          'modalContent'
        ]
      ).toEqual('Failed to abort process travels. Message: "404 error"');
    });
  });
});
