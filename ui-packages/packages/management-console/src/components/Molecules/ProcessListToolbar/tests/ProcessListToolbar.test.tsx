import React from 'react';
import { shallow, mount } from 'enzyme';
import ProcessListToolbar from '../ProcessListToolbar';
import { GraphQL } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;
import {
  Dropdown,
  KebabToggle,
  DropdownItem,
  ToolbarItem,
  SelectOption,
  Select
} from '@patternfly/react-core';
import { act } from 'react-dom/test-utils';
import wait from 'waait';
import axios from 'axios';
jest.mock('../../../Atoms/ProcessListModal/ProcessListModal');
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;
import * as Utils from '../../../../utils/Utils';

const initData = {
  ProcessInstances: [
    {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
      processId: 'travels',
      parentProcessInstanceId: null,
      parentProcessInstance: null,
      processName: 'travels',
      roles: [],
      state: ProcessInstanceState.Active,
      rootProcessInstanceId: null,
      endpoint: 'http://localhost:4000',
      serviceUrl: 'http://localhost:4000',
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
      lastUpdate: '2019-10-22T03:40:44.089Z',
      end: null,
      variables:
        '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
      nodes: [],
      isOpen: 'true',
      isSelected: false,
      childProcessInstances: [
        {
          id: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf862',
          processId: 'flightBooking',
          businessKey: 'T1234FlightBooking02',
          parentProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          parentProcessInstance: null,
          processName: 'FlightBooking',
          rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          roles: [],
          state: ProcessInstanceState.Error,
          serviceUrl: 'http://localhost:4000',
          start: '2019-10-22T03:40:44.089Z',
          end: '2019-10-22T05:40:44.089Z',
          latUpdate: '2019-10-22T03:40:44.089Z',
          endpoint: 'http://localhost:4000',
          error: {
            nodeDefinitionId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf823',
            message: 'some thing went wrong'
          },
          addons: ['process-management'],
          variables:
            '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
          nodes: [],
          childProcessInstances: [],
          isSelected: false
        },
        {
          id: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf86211223344',
          processId: 'flightBooking1111',
          businessKey: 'T1234FlightBooking021111',
          parentProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          parentProcessInstance: null,
          processName: 'FlightBooking',
          rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          roles: [],
          state: ProcessInstanceState.Error,
          serviceUrl: 'http://localhost:4000',
          start: '2019-10-22T03:40:44.089Z',
          end: '2019-10-22T05:40:44.089Z',
          latUpdate: '2019-10-22T03:40:44.089Z',
          endpoint: 'http://localhost:4000',
          error: {
            nodeDefinitionId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf823',
            message: 'some thing went wrong'
          },
          addons: ['process-management'],
          variables:
            '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
          nodes: [],
          childProcessInstances: [],
          isSelected: false
        }
      ]
    }
  ]
};

const props = {
  statusArray: [
    GraphQL.ProcessInstanceState.Active,
    GraphQL.ProcessInstanceState.Completed,
    GraphQL.ProcessInstanceState.Error,
    GraphQL.ProcessInstanceState.Aborted,
    GraphQL.ProcessInstanceState.Suspended
  ],
  filterClick: jest.fn(),
  setStatusArray: jest.fn(),
  filters: {
    status: [
      GraphQL.ProcessInstanceState.Active,
      GraphQL.ProcessInstanceState.Completed,
      GraphQL.ProcessInstanceState.Error,
      GraphQL.ProcessInstanceState.Aborted,
      GraphQL.ProcessInstanceState.Suspended
    ],
    businessKey: ['tra']
  },
  setFilters: jest.fn(),
  setInitData: jest.fn(),
  setSelectedInstances: jest.fn(),
  selectedInstances: [],
  initData,
  setSearchWord: jest.fn(),
  searchWord: 'Tra',
  isAllChecked: true,
  setIsAllChecked: jest.fn(),
  setSelectableInstances: jest.fn()
};

afterEach(() => {
  props.setStatusArray.mockClear();
  props.setSearchWord.mockClear();
  props.filterClick.mockClear();
});

describe('ProcessListToolbar component tests', () => {
  it('Snapshot tests', () => {
    const wrapper = mount(<ProcessListToolbar {...props} />).find(
      'ProcessListToolbar'
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('Snapshot tests for disabled filter button', () => {
    const wrapper = mount(
      <ProcessListToolbar
        {...{
          ...props,
          statusArray: [],
          filters: { status: [], businessKey: [] }
        }}
      />
    ).find('ProcessListToolbar');
    expect(wrapper.find(ToolbarItem)).toMatchSnapshot();
  });

  it('clearAll tests', () => {
    const wrapper = shallow(<ProcessListToolbar {...props} />);
    wrapper.find('#data-toolbar-with-filter').props()['clearAllFilters']();
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

  it('onSelect tests', async () => {
    let wrapper = mount(
      <ProcessListToolbar
        {...{
          ...props,
          statusArray: [
            ProcessInstanceState.Active,
            ProcessInstanceState.Completed,
            ProcessInstanceState.Error
          ],
          filters: {
            status: [
              ProcessInstanceState.Active,
              ProcessInstanceState.Completed,
              ProcessInstanceState.Error
            ],
            businessKey: ['tra']
          }
        }}
      />
    ).find('ProcessListToolbar');
    expect(wrapper.find('#status-select').exists()).toBeTruthy();
    await act(async () => {
      wrapper.find(Select).find('button').simulate('click');
    });
    wrapper = wrapper.update();
    await act(async () => {
      wrapper.find(SelectOption).at(1).find('input').simulate('change');
    });
    expect(props.setStatusArray.mock.calls[0][0]).toEqual(['ACTIVE', 'ERROR']);
    await act(async () => {
      wrapper.find(SelectOption).at(4).find('input').simulate('change');
    });
    expect(props.setStatusArray.mock.calls[1][0]).toEqual([
      'ACTIVE',
      'COMPLETED',
      'ERROR',
      'SUSPENDED'
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

  it('onDelete tests - for status delete last', () => {
    const wrapper = shallow(
      <ProcessListToolbar
        {...{ ...props, statusArray: [ProcessInstanceState.Error] }}
      />
    );
    wrapper
      .find('#datatoolbar-filter-status')
      .props()
      ['deleteChip']('Status', 'ERROR');
    expect(props.setStatusArray).toHaveBeenCalledWith([]);
    expect(props.setFilters).toHaveBeenCalled();
    expect(props.filterClick).not.toBeCalled();
  });

  it('onRefresh click', () => {
    const wrapper = shallow(<ProcessListToolbar {...props} />);
    wrapper.find('#refresh-button').simulate('click');
    expect(props.filterClick).toHaveBeenCalledWith(props.statusArray);
  });

  it('onStatusToggle click', () => {
    const wrapper = shallow(<ProcessListToolbar {...props} />);
    wrapper.find('#status-select').props()['onToggle'](true);
    wrapper.update();
    expect(wrapper.find('SelectOption').length).toEqual(5);
  });

  describe('handleTextBoxchange click', () => {
    it('word is passed', () => {
      const wrapper = shallow(
        <ProcessListToolbar {...{ ...props, searchWord: 'tra' }} />
      );
      wrapper.find('#businessKey').simulate('change', 'tra');
      expect(props.setSearchWord.mock.calls).toEqual([['tra']]);
    });
    it('word is empty', () => {
      const wrapper = shallow(
        <ProcessListToolbar {...{ ...props, searchWord: '' }} />
      );
      wrapper.find('#businessKey').simulate('change', '');
      expect(props.setSearchWord.mock.calls).toEqual([[''], ['']]);
    });
  });

  it('handleEnterClick test', () => {
    const wrapper = shallow(<ProcessListToolbar {...props} />);
    wrapper.find('#businessKey').simulate('keypress', { key: 'Enter' });
    expect(props.filterClick).toHaveBeenCalled();
  });

  describe('select multiple checkbox tests', () => {
    const wrapper = shallow(<ProcessListToolbar {...props} />);
    const wrapper1 = shallow(
      <ProcessListToolbar {...{ ...props, isAllChecked: false }} />
    );
    it('none selected click', () => {
      wrapper
        .find('#bulk-select')
        .props()
        ['children']['props']['dropdownItems'][0]['props']['onClick']();
      expect(props.setSelectedInstances).toHaveBeenCalled();
    });

    it('parent selected click', () => {
      wrapper
        .find('#bulk-select')
        .props()
        ['children']['props']['dropdownItems'][1]['props']['onClick']();
      expect(props.setSelectedInstances).toHaveBeenCalled();
    });

    it('all selected click', () => {
      wrapper
        .find('#bulk-select')
        .props()
        ['children']['props']['dropdownItems'][2]['props']['onClick']();
      expect(props.setSelectedInstances).toHaveBeenCalled();
    });
    it('bulk select checkbox click', () => {
      wrapper
        .find('#bulk-select')
        .props()
        ['children']['props']['toggle']['props']['splitButtonItems'][0][
          'props'
        ]['onChange']();
      expect(props.setSelectedInstances).toHaveBeenCalled();
    });
    it('bulk select checkbox click', () => {
      wrapper1
        .find('#bulk-select')
        .props()
        ['children']['props']['toggle']['props']['splitButtonItems'][0][
          'props'
        ]['onChange']();
      expect(props.setSelectedInstances).toHaveBeenCalled();
    });

    it('drowdown toggle checkbox click', () => {
      wrapper
        .find('#bulk-select')
        .props()
        ['children']['props']['toggle']['props']['onToggle']();
    });
  });

  describe('multi Abort click tests', () => {
    const handleMultiAbortSpyOn = jest.spyOn(Utils, 'performMultipleAction');
    it('multi abort click success', async () => {
      props.selectedInstances = [
        initData.ProcessInstances[0],
        initData.ProcessInstances[0].childProcessInstances[0],
        initData.ProcessInstances[0].childProcessInstances[1]
      ];
      mockedAxios.delete.mockResolvedValue({});
      let wrapper = mount(<ProcessListToolbar {...props} />).find(
        'ProcessListToolbar'
      );
      await act(async () => {
        wrapper
          .find('#process-management-buttons')
          .at(0)
          .find(Dropdown)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
        wrapper
          .find('#process-management-buttons')
          .at(0)
          .find(DropdownItem)
          .at(0)
          .simulate('click');
        await wait(0);
      });
      expect(handleMultiAbortSpyOn).toHaveBeenCalled();
    });
    it('multi abort click fail', async () => {
      props.selectedInstances = [
        initData.ProcessInstances[0],
        initData.ProcessInstances[0].childProcessInstances[0],
        initData.ProcessInstances[0].childProcessInstances[1]
      ];
      mockedAxios.delete.mockRejectedValue({});
      let wrapper = mount(<ProcessListToolbar {...props} />).find(
        'ProcessListToolbar'
      );
      await act(async () => {
        wrapper
          .find('#process-management-buttons')
          .at(0)
          .find(Dropdown)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
        wrapper
          .find('#process-management-buttons')
          .at(0)
          .find(DropdownItem)
          .at(0)
          .simulate('click');
        await wait(0);
      });
      expect(handleMultiAbortSpyOn).toHaveBeenCalled();
    });
  });

  describe('multi Skip click tests', () => {
    const handleMultiSkipSpyOn = jest.spyOn(Utils, 'performMultipleAction');
    it('multi skip click success', async () => {
      props.selectedInstances = [
        initData.ProcessInstances[0],
        initData.ProcessInstances[0].childProcessInstances[0],
        initData.ProcessInstances[0].childProcessInstances[1]
      ];
      mockedAxios.post.mockResolvedValue({});
      let wrapper = mount(<ProcessListToolbar {...props} />).find(
        'ProcessListToolbar'
      );
      await act(async () => {
        wrapper
          .find('#process-management-buttons')
          .at(0)
          .find(Dropdown)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
        wrapper
          .find('#process-management-buttons')
          .at(0)
          .find(DropdownItem)
          .at(1)
          .simulate('click');
        await wait(0);
      });
      expect(handleMultiSkipSpyOn).toHaveBeenCalled();
    });
    it('multi skip click fail', async () => {
      props.selectedInstances = [
        initData.ProcessInstances[0],
        initData.ProcessInstances[0].childProcessInstances[0],
        initData.ProcessInstances[0].childProcessInstances[1]
      ];
      mockedAxios.post.mockRejectedValue({});
      let wrapper = mount(<ProcessListToolbar {...props} />).find(
        'ProcessListToolbar'
      );
      await act(async () => {
        wrapper
          .find('#process-management-buttons')
          .at(0)
          .find(Dropdown)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
        wrapper
          .find('#process-management-buttons')
          .at(0)
          .find(DropdownItem)
          .at(1)
          .simulate('click');
        await wait(0);
      });
      expect(handleMultiSkipSpyOn).toHaveBeenCalled();
    });
  });

  describe('multi Retry click tests', () => {
    const handleMultiRetrySpyOn = jest.spyOn(Utils, 'performMultipleAction');
    it('multi retry click success', async () => {
      props.selectedInstances = [
        initData.ProcessInstances[0],
        initData.ProcessInstances[0].childProcessInstances[0],
        initData.ProcessInstances[0].childProcessInstances[1]
      ];
      mockedAxios.post.mockResolvedValue({});
      let wrapper = mount(<ProcessListToolbar {...props} />).find(
        'ProcessListToolbar'
      );
      await act(async () => {
        wrapper
          .find('#process-management-buttons')
          .at(0)
          .find(Dropdown)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
        wrapper
          .find('#process-management-buttons')
          .at(0)
          .find(DropdownItem)
          .at(2)
          .simulate('click');
        await wait(0);
      });
      expect(handleMultiRetrySpyOn).toHaveBeenCalled();
    });
    it('multi retry click fail', async () => {
      props.selectedInstances = [
        initData.ProcessInstances[0],
        initData.ProcessInstances[0].childProcessInstances[0],
        initData.ProcessInstances[0].childProcessInstances[1]
      ];
      mockedAxios.post.mockRejectedValue({});
      let wrapper = mount(<ProcessListToolbar {...props} />).find(
        'ProcessListToolbar'
      );
      await act(async () => {
        wrapper
          .find('#process-management-buttons')
          .at(0)
          .find(Dropdown)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
        wrapper
          .find('#process-management-buttons')
          .at(0)
          .find(DropdownItem)
          .at(2)
          .simulate('click');
        await wait(0);
      });
      expect(handleMultiRetrySpyOn).toHaveBeenCalled();
    });
  });

  it('reset click tests', () => {
    const wrapper = mount(<ProcessListToolbar {...props} />).find(
      'ProcessListToolbar'
    );
    wrapper.find('MockedProcessListModal').props()['resetSelected']();
    expect(props.setSelectedInstances).toHaveBeenCalled();
    expect(props.setIsAllChecked).toHaveBeenCalled();
  });
});
