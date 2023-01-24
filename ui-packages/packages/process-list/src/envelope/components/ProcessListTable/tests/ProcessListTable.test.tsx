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
import { act } from 'react-dom/test-utils';
import { ProcessInstances } from './mocks/Mocks';
import { OrderBy } from '../../.././../api';
import { mount } from 'enzyme';
import ProcessListTable from '../ProcessListTable';
import { Button, Checkbox } from '@patternfly/react-core';
import _ from 'lodash';
import axios from 'axios';
import { BrowserRouter } from 'react-router-dom';
import TestProcessListDriver from '../../ProcessList/tests/mocks/TestProcessListDriver';
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;
jest.mock('../../ProcessListChildTable/ProcessListChildTable');
jest.mock('../../DisablePopup/DisablePopup');
jest.mock('../../ProcessListActionsKebab/ProcessListActionsKebab');
jest.mock('../../ErrorPopover/ErrorPopover');
Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@kogito-apps/components-common', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/components-common'), {
    LoadMore: () => {
      return <MockedComponent />;
    },
    KogitoEmptyState: () => {
      return <MockedComponent />;
    },
    KogitoSpinner: () => {
      return <MockedComponent />;
    }
  })
);

const mockMath = Object.create(global.Math);
mockMath.random = () => 0.5;
global.Math = mockMath;

jest.mock('@kogito-apps/management-console-shared', () =>
  Object.assign(jest.requireActual('@kogito-apps/management-console-shared'), {
    ProcessInfo: () => {
      return <MockedComponent />;
    }
  })
);

const props = {
  processInstances: ProcessInstances,
  isLoading: false,
  expanded: {
    0: false
  },
  setExpanded: jest.fn(),
  driver: new TestProcessListDriver([], []),
  onSort: jest.fn(),
  sortBy: { lastUpdate: OrderBy.DESC },
  setProcessInstances: jest.fn(),
  selectedInstances: [],
  setSelectedInstances: jest.fn(),
  selectableInstances: 0,
  setSelectableInstances: jest.fn(),
  setIsAllChecked: jest.fn(),
  singularProcessLabel: 'Workflow',
  pluralProcessLabel: 'Workflows'
};
describe('ProcessListTable test', () => {
  it('initial render with data', () => {
    const wrapper = mount(<ProcessListTable {...props} />).find(
      'ProcessListTable'
    );
    expect(wrapper).toMatchSnapshot();
  });
  it('loading state', () => {
    const wrapper = mount(
      <ProcessListTable
        {...{ ...props, isLoading: true, processInstances: [] }}
      />
    ).find('ProcessListTable');
    const kogitoSpinner = wrapper.find('KogitoSpinner');
    expect(kogitoSpinner.exists()).toBeTruthy();
    expect(kogitoSpinner).toMatchSnapshot();
  });

  it('no results found state', () => {
    const wrapper = mount(
      <ProcessListTable {...{ ...props, processInstances: [] }} />
    ).find('ProcessListTable');
    const kogitoEmptyState = wrapper.find('KogitoEmptyState');
    expect(kogitoEmptyState.exists()).toBeTruthy();
    expect(kogitoEmptyState).toMatchSnapshot();
  });
  it('expand parent process', async () => {
    let wrapper = mount(
      <ProcessListTable {...{ ...props, expanded: { 0: true } }} />
    ).find('ProcessListTable');
    await act(async () => {
      wrapper.find('td').at(0).find(Button).simulate('click');
    });
    wrapper = wrapper.update();
    expect(wrapper.find('MockedProcessListChildTable').exists()).toBeTruthy();
  });

  it('snapshot test for process list - with expanded', async () => {
    const clonedProps = _.cloneDeep(props);
    clonedProps.expanded = {
      0: true,
      1: false
    };
    clonedProps.selectedInstances = [{ ...ProcessInstances[0] }];
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
          ['onSkipClick'](props.processInstances[0]);
      });
      const skipSuccessWrapper = wrapper.update();
      expect(skipSuccessWrapper.find('ProcessInfoModal').exists()).toBeTruthy();
      expect(
        skipSuccessWrapper.find('ProcessInfoModal').props()['modalContent']
      ).toEqual('The workflow travels was successfully skipped.');
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
          ['onRetryClick'](props.processInstances[0]);
      });
      const retrySuccessWrapper = wrapper.update();
      expect(
        retrySuccessWrapper.find('ProcessInfoModal').exists()
      ).toBeTruthy();
      expect(
        retrySuccessWrapper.find('ProcessInfoModal').props()['modalContent']
      ).toEqual('The workflow travels was successfully re-executed.');
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
          ['onAbortClick'](props.processInstances[0]);
      });
      const abortSuccessWrapper = wrapper.update();
      expect(
        abortSuccessWrapper.find('ProcessInfoModal').exists()
      ).toBeTruthy();
      expect(
        abortSuccessWrapper.find('ProcessInfoModal').props()['modalContent']
      ).toEqual('The workflow travels was successfully aborted.');
    });
  });
});
