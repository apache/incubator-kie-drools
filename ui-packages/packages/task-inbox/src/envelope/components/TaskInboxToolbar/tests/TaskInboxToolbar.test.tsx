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

import React, { FormEvent } from 'react';
import { mount } from 'enzyme';
import TaskInboxToolbar from '../TaskInboxToolbar';
import {
  getDefaultActiveTaskStates,
  getDefaultTaskStates
} from '../../utils/TaskInboxUtils';
import {
  Select,
  SelectOption
} from '@patternfly/react-core/dist/js/components/Select';
import { Chip } from '@patternfly/react-core/dist/js/components/Chip';
import { ToolbarFilter } from '@patternfly/react-core/dist/js/components/Toolbar';
import { TextInputBase } from '@patternfly/react-core/dist/js/components/TextInput';
import { act } from 'react-dom/test-utils';
import wait from 'waait';

const applyFilter = jest.fn();
const refresh = jest.fn();

let props;

const getTaskInboxToolbarWrapper = () => {
  return mount(<TaskInboxToolbar {...props} />).find('TaskInboxToolbar');
};

const selectTaskState = (wrapper, taskState: string) => {
  wrapper.find(Select).find('button').simulate('click');

  wrapper = wrapper.update();

  wrapper
    .find(Select)
    .find(SelectOption)
    .findWhere((node) => node.props().value === taskState)
    .find('input')
    .simulate('change');

  wrapper.find(Select).find('button').simulate('click');

  wrapper = wrapper.update();

  return wrapper;
};

const writeTaskName = async (wrapper, taskName: string) => {
  await act(async () => {
    const event = {
      target: {}
    } as FormEvent<HTMLInputElement>;

    wrapper.find(TextInputBase).props()['onChange']('', event);
    wrapper.find(TextInputBase).props()['onChange'](taskName, event);

    await wait();
  });

  wrapper = wrapper.update();

  return wrapper;
};

const callApplyFilter = (wrapper) => {
  wrapper.find('#apply-filter').find('button').simulate('click');
};

describe('TaskInboxToolbar test', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    props = {
      activeFilter: {
        taskStates: getDefaultActiveTaskStates(),
        taskNames: []
      },
      allTaskStates: getDefaultTaskStates(),
      activeTaskStates: getDefaultActiveTaskStates(),
      applyFilter,
      refresh
    };
  });

  it('Snapshot', () => {
    const wrapper = getTaskInboxToolbarWrapper();
    expect(wrapper).toMatchSnapshot();
  });

  it('Select status and apply filter', () => {
    let wrapper = getTaskInboxToolbarWrapper();

    wrapper = selectTaskState(wrapper, 'Ready');
    wrapper = selectTaskState(wrapper, 'Reserved');
    wrapper = selectTaskState(wrapper, 'Aborted');
    wrapper = selectTaskState(wrapper, 'Completed');
    wrapper = selectTaskState(wrapper, 'Skipped');

    callApplyFilter(wrapper);

    expect(applyFilter).toHaveBeenCalled();
    const filter = applyFilter.mock.calls[0][0];

    expect(filter).not.toBeNull();
    expect(filter).toHaveProperty('taskNames', []);
    expect(filter).toHaveProperty('taskStates');

    expect(filter.taskStates).toHaveLength(3);
    expect(filter.taskStates).toContain('Aborted');
    expect(filter.taskStates).toContain('Completed');
    expect(filter.taskStates).toContain('Skipped');
  });

  it('Write task name and apply filter', async () => {
    let wrapper = getTaskInboxToolbarWrapper();

    wrapper = await writeTaskName(wrapper, 'App');

    callApplyFilter(wrapper);

    expect(applyFilter).toHaveBeenCalled();
    const filter = applyFilter.mock.calls[0][0];

    expect(filter).not.toBeNull();
    expect(filter).toHaveProperty('taskNames');

    expect(filter.taskNames).toHaveLength(1);
    expect(filter.taskNames).toContain('App');

    expect(filter).toHaveProperty('taskStates');

    expect(filter.taskStates).toHaveLength(2);
    expect(filter.taskStates).toContain('Ready');
    expect(filter.taskStates).toContain('Reserved');
  });

  it('Reset filter', async () => {
    let wrapper = getTaskInboxToolbarWrapper();

    wrapper = await writeTaskName(wrapper, 'App');

    wrapper = selectTaskState(wrapper, 'Ready');
    wrapper = selectTaskState(wrapper, 'Reserved');
    wrapper = selectTaskState(wrapper, 'Aborted');
    wrapper = selectTaskState(wrapper, 'Completed');
    wrapper = selectTaskState(wrapper, 'Skipped');

    wrapper = wrapper.update();

    callApplyFilter(wrapper);

    expect(applyFilter).toHaveBeenCalled();
    const filter = applyFilter.mock.calls[0][0];

    expect(filter).not.toBeNull();
    expect(filter).toHaveProperty('taskNames');

    expect(filter.taskNames).toHaveLength(1);
    expect(filter.taskNames).toContain('App');

    expect(filter).toHaveProperty('taskStates');

    expect(filter.taskStates).toHaveLength(3);
    expect(filter.taskStates).toContain('Aborted');
    expect(filter.taskStates).toContain('Completed');
    expect(filter.taskStates).toContain('Skipped');

    wrapper = wrapper.update();

    const reset = wrapper
      .findWhere((node) => node.text() === 'Reset to default')
      .find('button')
      .first();

    reset.simulate('click');

    expect(applyFilter).toHaveBeenCalledTimes(2);
    const resetFilter = applyFilter.mock.calls[1][0];

    expect(resetFilter).not.toBeNull();
    expect(resetFilter).toHaveProperty('taskNames', []);

    expect(resetFilter).toHaveProperty('taskStates');

    expect(resetFilter.taskStates).toHaveLength(2);
    expect(resetFilter.taskStates).toContain('Ready');
    expect(resetFilter.taskStates).toContain('Reserved');
  });

  it('Remove toolbar chips', async () => {
    let wrapper = getTaskInboxToolbarWrapper();

    wrapper = selectTaskState(wrapper, 'Completed');
    wrapper = await writeTaskName(wrapper, 'App');

    wrapper = wrapper.update();

    callApplyFilter(wrapper);

    wrapper
      .find(ToolbarFilter)
      .at(0)
      .find(Chip)
      .at(1)
      .find('button')
      .simulate('click');

    wrapper = wrapper.update();

    expect(applyFilter).toHaveBeenCalledTimes(2);
    let filter = applyFilter.mock.calls[1][0];

    expect(filter).not.toBeNull();
    expect(filter).toHaveProperty('taskNames', ['App']);

    expect(filter).toHaveProperty('taskStates');
    expect(filter.taskStates).toHaveLength(2);

    wrapper
      .find(ToolbarFilter)
      .at(1)
      .find(Chip)
      .at(0)
      .find('button')
      .simulate('click');

    expect(applyFilter).toHaveBeenCalledTimes(3);
    filter = applyFilter.mock.calls[2][0];

    expect(filter).not.toBeNull();
    expect(filter).toHaveProperty('taskNames', []);

    expect(filter).toHaveProperty('taskStates');
    expect(filter.taskStates).toHaveLength(2);
  });

  it('Refresh clicked', () => {
    const wrapper = getTaskInboxToolbarWrapper();

    wrapper.find('#refresh').find('button').simulate('click');

    expect(refresh).toHaveBeenCalled();
  });
});
