import React from 'react';
import { shallow } from 'enzyme';
import ProcessListToolbar from '../ProcessListToolbar';
import { GraphQL, getWrapper } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;
import { DataToolbarItem } from '@patternfly/react-core';

const initData = {
  ProcessInstances: [
    {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
      processId: 'travels',
      parentProcessInstanceId: null,
      parentProcessInstance: null,
      processName: 'travels',
      roles: [],
      isOpen: 'true',
      childDataList: [
        {
          id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
          processId: 'flightBooking',
          businessKey: null,
          parentProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          parentProcessInstance: {
            id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
            processName: 'travels',
            businessKey: null
          },
          processName: 'FlightBooking',
          rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          roles: [],
          state: 'COMPLETED',
          serviceUrl: 'http://localhost:4000',
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
          nodes: [
            {
              nodeId: '1',
              name: 'End Event 1',
              definitionId: 'EndEvent_1',
              id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
              enter: '2019-10-22T04:43:01.144Z',
              exit: '2019-10-22T04:43:01.144Z',
              type: 'EndNode'
            },
            {
              nodeId: '2',
              name: 'Book flight',
              definitionId: 'ServiceTask_1',
              id: '2f588da5-a323-4111-9017-3093ef9319d1',
              enter: '2019-10-22T04:43:01.144Z',
              exit: '2019-10-22T04:43:01.144Z',
              type: 'WorkItemNode'
            },
            {
              nodeId: '3',
              name: 'StartProcess',
              definitionId: 'StartEvent_1',
              id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
              enter: '2019-10-22T04:43:01.144Z',
              exit: '2019-10-22T04:43:01.144Z',
              type: 'StartNode'
            }
          ],
          childProcessInstances: []
        }
      ],
      state: ProcessInstanceState.Active,
      rootProcessInstanceId: null,
      endpoint: 'http://localhost:4000',
      addons: [
        'jobs-management',
        'prometheus-monitoring',
        'process-management'
      ],
      error: {
        nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
        message: 'Something went wrong'
      },
      start: '2019-10-22T03:40:44.089Z',
      end: null,
      variables:
        '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
      nodes: [
        {
          name: 'Book Flight',
          definitionId: 'CallActivity_2',
          id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
          enter: '2019-10-22T04:43:01.143Z',
          exit: '2019-10-22T04:43:01.146Z',
          type: 'SubProcessNode'
        },
        {
          name: 'Confirm travel',
          definitionId: 'UserTask_2',
          id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
          enter: '2019-10-22T04:43:01.148Z',
          exit: null,
          type: 'HumanTaskNode'
        },
        {
          name: 'Join',
          definitionId: 'ParallelGateway_2',
          id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
          enter: '2019-10-22T04:43:01.148Z',
          exit: '2019-10-22T04:43:01.148Z',
          type: 'Join'
        },
        {
          name: 'Book Hotel',
          definitionId: 'CallActivity_1',
          id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
          enter: '2019-10-22T04:43:01.146Z',
          exit: '2019-10-22T04:43:01.148Z',
          type: 'SubProcessNode'
        },
        {
          name: 'Book',
          definitionId: 'ParallelGateway_1',
          id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
          enter: '2019-10-22T04:43:01.143Z',
          exit: '2019-10-22T04:43:01.146Z',
          type: 'Split'
        },
        {
          name: 'Join',
          definitionId: 'ExclusiveGateway_2',
          id: 'b2761011-3043-4f48-82bd-1395bf651a91',
          enter: '2019-10-22T04:43:01.143Z',
          exit: '2019-10-22T04:43:01.143Z',
          type: 'Join'
        },
        {
          name: 'is visa required',
          definitionId: 'ExclusiveGateway_1',
          id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
          enter: '2019-10-22T04:43:01.143Z',
          exit: '2019-10-22T04:43:01.143Z',
          type: 'Split'
        },
        {
          name: 'Visa check',
          definitionId: 'BusinessRuleTask_1',
          id: '1baa5de4-47cc-45a8-8323-005388191e4f',
          enter: '2019-10-22T04:43:01.135Z',
          exit: '2019-10-22T04:43:01.143Z',
          type: 'RuleSetNode'
        },
        {
          name: 'StartProcess',
          definitionId: 'StartEvent_1',
          id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
          enter: '2019-10-22T04:43:01.135Z',
          exit: '2019-10-22T04:43:01.135Z',
          type: 'StartNode'
        }
      ],
      childProcessInstances: [
        {
          id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
          processName: 'FlightBooking'
        },
        {
          id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
          processName: 'HotelBooking'
        }
      ]
    }
  ]
};

const props = {
  statusArray: ['ACTIVE', 'COMPLETED', 'ERROR', 'ABORTED', 'SUSPENDED'],
  filterClick: jest.fn(),
  setStatusArray: jest.fn(),
  setIsStatusSelected: jest.fn(),
  filters: {
    status: ['ACTIVE', 'COMPLETED', 'ERROR', 'ABORTED', 'SUSPENDED'],
    businessKey: ['tra']
  },
  setFilters: jest.fn(),
  setInitData: jest.fn(),
  handleAbortAll: jest.fn(),
  setAbortedObj: jest.fn(),
  abortedObj: { '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': 'travels' },
  initData,
  setOffset: jest.fn(),
  getProcessInstances: jest.fn(),
  setLimit: jest.fn(),
  pageSize: 10,
  setFilteredData: jest.fn(),
  setSearchWord: jest.fn(),
  searchWord: 'Tra',
  setIsClearAllClicked: jest.fn(),
  handleCheckAll: jest.fn(),
  isAllChecked: true,
  setIsAllChecked: jest.fn(),
  setSelectedNumber: jest.fn(),
  selectedNumber: 0,
  setModalTitle: jest.fn(),
  setTitleType: jest.fn(),
  setAbortedMessageObj: jest.fn(),
  setCompletedMessageObj: jest.fn(),
  handleAbortModalToggle: jest.fn()
};

const props1 = {
  statusArray: ['ACTIVE', 'COMPLETED', 'ERROR'],
  filterClick: jest.fn(),
  setStatusArray: jest.fn(),
  setIsStatusSelected: jest.fn(),
  filters: {
    status: ['ACTIVE', 'COMPLETED', 'ERROR'],
    businessKey: ['tra']
  },
  setFilters: jest.fn(),
  setInitData: jest.fn(),
  handleAbortAll: jest.fn(),
  setAbortedObj: jest.fn(),
  abortedObj: {},
  initData,
  setOffset: jest.fn(),
  getProcessInstances: jest.fn(),
  setLimit: jest.fn(),
  pageSize: 10,
  setFilteredData: jest.fn(),
  setSearchWord: jest.fn(),
  searchWord: '',
  setIsClearAllClicked: jest.fn(),
  handleCheckAll: jest.fn(),
  isAllChecked: false,
  setIsAllChecked: jest.fn(),
  setSelectedNumber: jest.fn(),
  selectedNumber: 0,
  setModalTitle: jest.fn(),
  setTitleType: jest.fn(),
  setAbortedMessageObj: jest.fn(),
  setCompletedMessageObj: jest.fn(),
  handleAbortModalToggle: jest.fn()
};

const props2 = {
  statusArray: [],
  filterClick: jest.fn(),
  setStatusArray: jest.fn(),
  setIsStatusSelected: jest.fn(),
  filters: { status: [], businessKey: [] },
  setFilters: jest.fn(),
  setInitData: jest.fn(),
  handleAbortAll: jest.fn(),
  setAbortedObj: jest.fn(),
  abortedObj: { '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': 'travels' },
  initData,
  setOffset: jest.fn(),
  getProcessInstances: jest.fn(),
  setLimit: jest.fn(),
  pageSize: 10,
  setFilteredData: jest.fn(),
  setSearchWord: jest.fn(),
  searchWord: '',
  setIsClearAllClicked: jest.fn(),
  handleCheckAll: jest.fn(),
  isAllChecked: false,
  setIsAllChecked: jest.fn(),
  setSelectedNumber: jest.fn(),
  selectedNumber: 0,
  setModalTitle: jest.fn(),
  setTitleType: jest.fn(),
  setAbortedMessageObj: jest.fn(),
  setCompletedMessageObj: jest.fn(),
  handleAbortModalToggle: jest.fn()
};

/* tslint:disable */

describe('ProcessListToolbar component tests', () => {
  it('Snapshot tests', () => {
    const wrapper = getWrapper(
      <ProcessListToolbar {...props} />,
      'ProcessListToolbar'
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('Snapshot tests for disabled filter button', () => {
    let wrapper = getWrapper(
      <ProcessListToolbar {...props2} />,
      'ProcessListToolbar'
    );
    wrapper = wrapper.find(DataToolbarItem);
    expect(wrapper).toMatchSnapshot();
  });

  it('clearAll tests', () => {
    const wrapper = shallow(<ProcessListToolbar {...props} />);
    wrapper
      .find('#data-toolbar-with-filter')
      .props()
      ['clearAllFilters']();
    expect(props.setSearchWord).toHaveBeenCalledTimes(1);
    expect(props.setStatusArray.mock.calls).toEqual([[['ACTIVE']]]);
    expect(props.setFilters.mock.calls).toEqual([
      [{ status: ['ACTIVE'], businessKey: [] }]
    ]);
    expect(props.setSearchWord.mock.calls).toEqual([['']]);
  });

  it('filter click test', () => {
    const wrapper = shallow(<ProcessListToolbar {...props} />);
    wrapper.find('#apply-filter-button').simulate('click');
    expect(props.setFilters).toHaveBeenCalled();
    expect(props.setFilters.mock.calls[1]).toEqual([
      {
        status: ['ACTIVE', 'COMPLETED', 'ERROR', 'ABORTED', 'SUSPENDED'],
        businessKey: ['Tra']
      }
    ]);
  });

  it('onSelect tests', () => {
    const wrapper = shallow(<ProcessListToolbar {...props1} />);
    expect(wrapper.find('#status-select').exists()).toBeTruthy();
    wrapper
      .find('#status-select')
      .simulate('select', { target: { id: 'COMPLETED' } });
    wrapper
      .find('#status-select')
      .simulate('select', { target: { id: 'SUSPENDED' } });
    expect(props1.setStatusArray.mock.calls).toEqual([
      [['ACTIVE', 'ERROR']],
      [['ACTIVE', 'COMPLETED', 'ERROR', 'SUSPENDED']]
    ]);
  });

  it('onDelete tests - for status', () => {
    const wrapper = shallow(<ProcessListToolbar {...props} />);
    wrapper
      .find('#datatoolbar-filter-status')
      .props()
      ['deleteChip']('Status', 'ACTIVE');
    expect(props.filterClick).toHaveBeenCalled();
    expect(props.setStatusArray).toHaveBeenCalled();
    expect(props.setFilters).toHaveBeenCalled();
  });
  it('onDelete tests - for businessKey', () => {
    const wrapper = shallow(<ProcessListToolbar {...props} />);
    wrapper
      .find('#datatoolbar-filter-businesskey')
      .props()
      ['deleteChip']('Business key', 'Tra');
    expect(props.filters.businessKey.length).toEqual(0);
    expect(props.filterClick).toHaveBeenCalled();
  });

  it('onRefresh click', () => {
    const wrapper = shallow(<ProcessListToolbar {...props} />);
    wrapper.find('#refresh-button').simulate('click');
    expect(props.filterClick).toHaveBeenCalledWith(props.statusArray);
  });

  it('onStatusToggle click', () => {
    const wrapper = shallow(<ProcessListToolbar {...props1} />);
    wrapper
      .find('#status-select')
      .props()
      ['onToggle'](true);
    wrapper.update();
    expect(wrapper.find('SelectOption').length).toEqual(5);
  });

  describe('handleTextBoxchange click', () => {
    it('word is passed', () => {
      const wrapper = shallow(<ProcessListToolbar {...props1} />);
      wrapper.find('#businessKey').simulate('change', 'tra');
      expect(props1.setSearchWord.mock.calls).toEqual([['tra']]);
    });
    it('word is empty', () => {
      const wrapper = shallow(<ProcessListToolbar {...props2} />);
      wrapper.find('#businessKey').simulate('change', '');
      expect(props2.setSearchWord.mock.calls).toEqual([[''], ['']]);
    });
  });

  it('handleEnterClick test', () => {
    const wrapper = shallow(<ProcessListToolbar {...props} />);
    wrapper.find('#businessKey').simulate('keypress', { key: 'Enter' });
    expect(props.filterClick).toHaveBeenCalled();
  });

  describe('select multiple checkbox tests', () => {
    const wrapper = shallow(<ProcessListToolbar {...props} />);
    const wrapper1 = shallow(<ProcessListToolbar {...props1} />);
    it('none selected click', () => {
      wrapper
        .find('#bulk-select')
        .props()
        ['children']['props']['dropdownItems'][0]['props']['onClick']();
      expect(props.setInitData).toHaveBeenCalled();
      expect(props.setAbortedObj).toHaveBeenCalled();
    });

    it('parent selected click', () => {
      wrapper
        .find('#bulk-select')
        .props()
        ['children']['props']['dropdownItems'][1]['props']['onClick']();
      expect(props.setIsAllChecked).toHaveBeenCalled();
      expect(props.setSelectedNumber).toHaveBeenCalled();
      expect(props.setInitData).toHaveBeenCalled();
      expect(props.setAbortedObj).toHaveBeenCalled();
    });

    it('all selected click', () => {
      wrapper
        .find('#bulk-select')
        .props()
        ['children']['props']['dropdownItems'][2]['props']['onClick']();
      expect(props.setIsAllChecked).toHaveBeenCalled();
      expect(props.setSelectedNumber).toHaveBeenCalled();
      expect(props.setInitData).toHaveBeenCalled();
      expect(props.setAbortedObj).toHaveBeenCalled();
    });
    it('bulk select checkbox click', () => {
      wrapper
        .find('#bulk-select')
        .props()
        ['children']['props']['toggle']['props']['splitButtonItems'][0][
          'props'
        ]['onChange']();
      4;
      expect(props.setIsAllChecked).toHaveBeenCalled();
      expect(props.setSelectedNumber).toHaveBeenCalled();
      expect(props.setInitData).toHaveBeenCalled();
      expect(props.setAbortedObj).toHaveBeenCalled();
    });
    it('bulk select checkbox click', () => {
      wrapper1
        .find('#bulk-select')
        .props()
        ['children']['props']['toggle']['props']['splitButtonItems'][0][
          'props'
        ]['onChange']();
      expect(props.setIsAllChecked).toHaveBeenCalled();
      expect(props.setSelectedNumber).toHaveBeenCalled();
      expect(props.setInitData).toHaveBeenCalled();
      expect(props.setAbortedObj).toHaveBeenCalled();
    });

    it('drowdown toggle checkbox click', () => {
      wrapper
        .find('#bulk-select')
        .props()
        ['children']['props']['toggle']['props']['onToggle']();
    });
  });
});
