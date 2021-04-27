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
import { getWrapper } from '@kogito-apps/components-common';
import ProcessListToolbar from '../ProcessListToolbar';
import { ProcessInstanceState } from '@kogito-apps/management-console-shared';
import { act } from 'react-dom/test-utils';
import {
  Select,
  SelectOption,
  TextInput,
  Toolbar,
  ToolbarFilter
} from '@patternfly/react-core';

const props = {
  filters: {
    status: [ProcessInstanceState.Active],
    businessKey: ['GTRR11']
  },
  setFilters: jest.fn(),
  applyFilter: jest.fn(),
  refresh: jest.fn(),
  processStates: [ProcessInstanceState.Active],
  setProcessStates: jest.fn()
};
beforeEach(() => {
  props.setProcessStates.mockClear();
  props.setFilters.mockClear();
});

describe('ProcessListToolbar test', () => {
  it('Snapshot tests', () => {
    const wrapper = getWrapper(
      <ProcessListToolbar {...props} />,
      'ProcessListToolbar'
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('on select status', async () => {
    let wrapper = getWrapper(
      <ProcessListToolbar {...props} />,
      'ProcessListToolbar'
    );
    await act(async () => {
      wrapper
        .find(Select)
        .find('button')
        .simulate('click');
    });
    wrapper = wrapper.update();
    await act(async () => {
      wrapper
        .find(SelectOption)
        .at(1)
        .find('input')
        .simulate('change');
    });
    expect(props.setProcessStates.mock.calls[0][0]).toStrictEqual([
      'ACTIVE',
      'COMPLETED'
    ]);
    await act(async () => {
      wrapper
        .find(SelectOption)
        .at(0)
        .find('input')
        .simulate('change');
    });
    wrapper = wrapper.update();
    expect(props.setProcessStates).toHaveBeenCalled();
  });

  it('delete a status chip', async () => {
    const wrapper = getWrapper(
      <ProcessListToolbar {...props} />,
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
    const wrapper = getWrapper(
      <ProcessListToolbar
        {...{
          ...props,
          filters: { ...props.filters, businessKey: ['GR1122', 'MTY11'] }
        }}
      />,
      'ProcessListToolbar'
    );
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
    const wrapper = getWrapper(
      <ProcessListToolbar {...props} />,
      'ProcessListToolbar'
    );
    wrapper.find(TextInput).simulate('keypress', { key: 'Enter' });
    expect(props.applyFilter).toHaveBeenCalled();
  });

  it('reset filters', () => {
    const wrapper = getWrapper(
      <ProcessListToolbar {...props} />,
      'ProcessListToolbar'
    );
    wrapper
      .find(Toolbar)
      .props()
      ['clearAllFilters']();
    expect(props.setProcessStates.mock.calls[0][0]).toEqual(['ACTIVE']);
    expect(props.setFilters.mock.calls[0][0]).toEqual({
      status: ['ACTIVE'],
      businessKey: []
    });
  });

  it('apply filter click', () => {
    const wrapper = getWrapper(
      <ProcessListToolbar {...props} />,
      'ProcessListToolbar'
    );
    wrapper
      .find('#apply-filter-button')
      .at(1)
      .simulate('click');
    expect(props.setFilters).toHaveBeenCalled();
    expect(props.setFilters.mock.calls[0][0]).toStrictEqual({
      status: ['ACTIVE'],
      businessKey: ['GTRR11']
    });
  });
});
