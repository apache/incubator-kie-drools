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
import TaskInboxSwitchUser from '../TaskInboxSwitchUser';
import DevUIAppContextProvider from '../../../../contexts/DevUIAppContextProvider';
import { act } from 'react-dom/test-utils';

describe('TaskInboxSwitchUser tests', () => {
  it('Snapshot test with default props', () => {
    const wrapper = mount(
      <DevUIAppContextProvider users={[{ id: 'John snow', groups: ['admin'] }]}>
        <TaskInboxSwitchUser user="John" />
      </DevUIAppContextProvider>
    );
    expect(wrapper.find(TaskInboxSwitchUser)).toMatchSnapshot();
  });

  it('Trigger onSelect event', () => {
    const wrapper = mount(
      <DevUIAppContextProvider users={[{ id: 'John snow', groups: ['admin'] }]}>
        <TaskInboxSwitchUser user="John" />
      </DevUIAppContextProvider>
    );

    const event: any = { target: { innerHTML: 'admin' } };
    act(() => {
      wrapper.find('Dropdown').props()['onSelect'](event);
    });
    wrapper.update();
    expect(
      wrapper
        .find('Toggle')
        .find('button')
        .find('.pf-c-dropdown__toggle-text')
        .text()
    ).toEqual('admin');
  });

  it('Trigger toggle event', () => {
    const wrapper = mount(
      <DevUIAppContextProvider users={[{ id: 'John snow', groups: ['admin'] }]}>
        <TaskInboxSwitchUser user="John" />
      </DevUIAppContextProvider>
    );

    act(() => {
      wrapper.find('DropdownToggle').props()['onToggle']();
    });
  });
});
