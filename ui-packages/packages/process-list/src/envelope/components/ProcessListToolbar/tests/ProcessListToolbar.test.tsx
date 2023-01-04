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
import { mount } from 'enzyme';
import ProcessListToolbar from '../ProcessListToolbar';
import { ProcessInstanceState } from '@kogito-apps/management-console-shared';
import { act } from 'react-dom/test-utils';
import {
  Dropdown,
  DropdownItem,
  KebabToggle,
  Select,
  SelectOption,
  TextInput,
  Toolbar,
  ToolbarFilter
} from '@patternfly/react-core';
import { ProcessInstances } from '../../ProcessListTable/tests/mocks/Mocks';
import { shallow } from 'enzyme';
import wait from 'waait';
import TestProcessListDriver from '../../ProcessList/tests/mocks/TestProcessListDriver';
import axios from 'axios';
import _ from 'lodash';
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;
const props = {
  filters: {
    status: [ProcessInstanceState.Active],
    businessKey: ['GTRR11']
  },
  setFilters: jest.fn(),
  applyFilter: jest.fn(),
  refresh: jest.fn(),
  processStates: [ProcessInstanceState.Active],
  setProcessStates: jest.fn(),
  selectedInstances: [ProcessInstances[0]],
  setSelectedInstances: jest.fn(),
  processInstances: ProcessInstances,
  setProcessInstances: jest.fn(),
  isAllChecked: false,
  setIsAllChecked: jest.fn(),
  driver: null,
  defaultStatusFilter: [ProcessInstanceState.Active]
};
beforeEach(() => {
  props.setProcessStates.mockClear();
  props.setFilters.mockClear();
});

describe('ProcessListToolbar test', () => {
  it('Snapshot tests', () => {
    const wrapper = mount(<ProcessListToolbar {...props} />).find(
      'ProcessListToolbar'
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('on select status', async () => {
    let wrapper = mount(<ProcessListToolbar {...props} />).find(
      'ProcessListToolbar'
    );
    await act(async () => {
      wrapper.find(Select).find('button').simulate('click');
    });
    wrapper = wrapper.update();
    await act(async () => {
      wrapper.find(SelectOption).at(1).find('input').simulate('change');
    });
    expect(props.setProcessStates.mock.calls[0][0]).toStrictEqual([
      'ACTIVE',
      'COMPLETED'
    ]);
    await act(async () => {
      wrapper.find(SelectOption).at(0).find('input').simulate('change');
    });
    wrapper = wrapper.update();
    expect(props.setProcessStates).toHaveBeenCalled();
  });

  it('delete a status chip', async () => {
    const wrapper = mount(<ProcessListToolbar {...props} />).find(
      'ProcessListToolbar'
    );
    await act(async () => {
      wrapper
        .find(ToolbarFilter)
        .at(0)
        .props()
        ['deleteChip']('Status', 'ACTIVE');
    });
    expect(props.applyFilter).toHaveBeenCalled();
    expect(props.setFilters).toHaveBeenCalled();
    expect(props.setProcessStates).toHaveBeenCalled();
    expect(props.setProcessStates.mock.calls[0][0]).toStrictEqual([]);
  });

  it('delete a status chip', async () => {
    const wrapper = mount(
      <ProcessListToolbar
        {...{
          ...props,
          filters: { ...props.filters, businessKey: ['GR1122', 'MTY11'] }
        }}
      />
    ).find('ProcessListToolbar');
    await act(async () => {
      wrapper
        .find(ToolbarFilter)
        .at(1)
        .props()
        ['deleteChip']('Business key', 'GR1122');
    });
    expect(props.applyFilter).toHaveBeenCalled();
    expect(props.setFilters).toHaveBeenCalled();
    expect(props.setFilters.mock.calls[0][0]['businessKey'][0]).toEqual(
      'MTY11'
    );
  });

  it('enter click on apply filter(business key)', () => {
    const wrapper = mount(<ProcessListToolbar {...props} />).find(
      'ProcessListToolbar'
    );
    wrapper.find(TextInput).simulate('keypress', { key: 'Enter' });
    expect(props.applyFilter).toHaveBeenCalled();
  });

  it('reset filters', () => {
    const wrapper = mount(<ProcessListToolbar {...props} />).find(
      'ProcessListToolbar'
    );
    wrapper.find(Toolbar).props()['clearAllFilters']();
    expect(props.setProcessStates.mock.calls[0][0]).toEqual(['ACTIVE']);
    expect(props.setFilters.mock.calls[0][0]).toEqual({
      status: ['ACTIVE'],
      businessKey: []
    });
  });

  it('apply filter click', () => {
    const wrapper = mount(<ProcessListToolbar {...props} />).find(
      'ProcessListToolbar'
    );
    wrapper.find('#apply-filter-button').at(1).simulate('click');
    expect(props.setFilters).toHaveBeenCalled();
    expect(props.setFilters.mock.calls[0][0]).toStrictEqual({
      status: ['ACTIVE'],
      businessKey: ['GTRR11']
    });
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
    const abortProps = _.cloneDeep(props);
    abortProps.driver = new TestProcessListDriver([], []);
    const driverhandleProcessMultipleActionMock = jest.spyOn(
      abortProps.driver,
      'handleProcessMultipleAction'
    );
    it('multi abort click success', async () => {
      abortProps.selectedInstances = [ProcessInstances[0]];
      mockedAxios.delete.mockResolvedValue({});
      let wrapper = mount(<ProcessListToolbar {...abortProps} />).find(
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
      expect(driverhandleProcessMultipleActionMock).toHaveBeenCalled();
    });
    it('multi abort click fail', async () => {
      abortProps.selectedInstances = [ProcessInstances[0]];
      mockedAxios.delete.mockRejectedValue({});
      let wrapper = mount(<ProcessListToolbar {...abortProps} />).find(
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
    });
  });

  describe('multi Skip click tests', () => {
    const skipProps = _.cloneDeep(props);
    skipProps.driver = new TestProcessListDriver([], []);
    const driverhandleProcessMultipleActionMock = jest.spyOn(
      skipProps.driver,
      'handleProcessMultipleAction'
    );
    it('multi skip click success', async () => {
      skipProps.selectedInstances = [ProcessInstances[0]];
      mockedAxios.post.mockResolvedValue({});
      let wrapper = mount(<ProcessListToolbar {...skipProps} />).find(
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
      expect(driverhandleProcessMultipleActionMock).toHaveBeenCalled();
    });
    it('multi skip click fail', async () => {
      skipProps.selectedInstances = [ProcessInstances[0]];
      mockedAxios.post.mockRejectedValue({});
      let wrapper = mount(<ProcessListToolbar {...skipProps} />).find(
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
      expect(driverhandleProcessMultipleActionMock).toHaveBeenCalled();
    });
  });

  describe('multi Retry click tests', () => {
    const retryProps = _.cloneDeep(props);
    retryProps.driver = new TestProcessListDriver([], []);
    const driverhandleProcessMultipleActionMock = jest.spyOn(
      retryProps.driver,
      'handleProcessMultipleAction'
    );
    it('multi retry click success', async () => {
      retryProps.selectedInstances = [ProcessInstances[0]];
      mockedAxios.post.mockResolvedValue({});
      let wrapper = mount(<ProcessListToolbar {...retryProps} />).find(
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
      expect(driverhandleProcessMultipleActionMock).toHaveBeenCalled();
    });
    it('multi retry click fail', async () => {
      retryProps.selectedInstances = [ProcessInstances[0]];
      mockedAxios.post.mockRejectedValue({});
      let wrapper = mount(<ProcessListToolbar {...retryProps} />).find(
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
      expect(driverhandleProcessMultipleActionMock).toHaveBeenCalled();
    });
  });
  it('reset click tests', () => {
    const wrapper = mount(<ProcessListToolbar {...props} />).find(
      'ProcessListToolbar'
    );
    wrapper.find('ProcessInfoModal').props()['resetSelected']();
    expect(props.setSelectedInstances).toHaveBeenCalled();
    expect(props.setIsAllChecked).toHaveBeenCalled();
  });
});
