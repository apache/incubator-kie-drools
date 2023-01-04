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
import ProcessDefinitionListToolbar from '../ProcessDefinitionListToolbar';
import { act } from 'react-dom/test-utils';
import { Button, ToolbarFilter, Tooltip } from '@patternfly/react-core';

describe('ProcessDefinition list toolbar tests', () => {
  it('render toolbar', () => {
    const wrapper = mount(
      <ProcessDefinitionListToolbar
        applyFilter={jest.fn()}
        setFilterProcessNames={jest.fn()}
        filterProcessNames={[]}
        singularProcessLabel={'Workflow'}
      />
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('apply filter click', () => {
    const applyFilter = jest.fn();
    const wrapper = mount(
      <ProcessDefinitionListToolbar
        applyFilter={applyFilter}
        setFilterProcessNames={jest.fn()}
        filterProcessNames={[]}
        singularProcessLabel={'Workflow'}
      />
    );
    act(() => {
      wrapper
        .find('TextInputBase')
        .props()
        ['onChange']({
          target: {
            value: 'process1'
          }
        } as any);
    });
    wrapper.find('#apply-filter').find('button').simulate('click');
    expect(applyFilter).toHaveBeenCalled();
  });

  it('reset click', () => {
    const applyFilter = jest.fn();
    const wrapper = mount(
      <ProcessDefinitionListToolbar
        applyFilter={applyFilter}
        setFilterProcessNames={jest.fn()}
        filterProcessNames={[]}
        singularProcessLabel={'Workflow'}
      />
    );
    act(() => {
      wrapper.find('Toolbar').props()['clearAllFilters']();
    });
    expect(applyFilter).toHaveBeenCalled();
  });

  it('refresh click', () => {
    const applyFilter = jest.fn();
    const wrapper = mount(
      <ProcessDefinitionListToolbar
        applyFilter={applyFilter}
        setFilterProcessNames={jest.fn()}
        filterProcessNames={[]}
        singularProcessLabel={'Workflow'}
      />
    );
    act(() => {
      wrapper.find(Tooltip).find(Button).simulate('click');
    });
    expect(applyFilter).toHaveBeenCalled();
  });

  it('enter clicked', () => {
    const applyFilter = jest.fn();
    const wrapper = mount(
      <ProcessDefinitionListToolbar
        applyFilter={applyFilter}
        setFilterProcessNames={jest.fn()}
        filterProcessNames={[]}
        singularProcessLabel={'Workflow'}
      />
    );
    act(() => {
      wrapper
        .find('TextInputBase')
        .props()
        ['onKeyPress']({
          key: 'Enter',
          target: {
            value: 'process1'
          }
        } as any);
    });
    wrapper.find('#apply-filter').find('button').simulate('click');
    expect(applyFilter).toHaveBeenCalled();
  });

  it('on delete chip', () => {
    const applyFilter = jest.fn();
    const wrapper = mount(
      <ProcessDefinitionListToolbar
        applyFilter={applyFilter}
        setFilterProcessNames={jest.fn()}
        filterProcessNames={['process1']}
        singularProcessLabel={'Workflow'}
      />
    );
    act(() => {
      wrapper
        .find(ToolbarFilter)
        .props()
        ['deleteChip']('Process name', 'process1');
    });
    expect(applyFilter).toHaveBeenCalled();
  });
});
