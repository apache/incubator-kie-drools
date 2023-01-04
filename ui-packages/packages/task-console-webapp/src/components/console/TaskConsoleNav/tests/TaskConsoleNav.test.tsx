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
import TaskConsoleNav from '../TaskConsoleNav';
import { MemoryRouter } from 'react-router-dom';

describe('TaskConsoleNav tests', () => {
  it('Snapshot', () => {
    const wrapper = mount(
      <MemoryRouter>
        <TaskConsoleNav />
      </MemoryRouter>
    ).find('TaskConsoleNav');

    expect(wrapper).toMatchSnapshot();

    const taskInboxNav = wrapper.findWhere(
      (nested) => nested.key() === 'task-inbox-nav'
    );

    expect(taskInboxNav.exists()).toBeTruthy();
    expect(taskInboxNav.props().isActive).toBeFalsy();
  });

  it('Snapshot with pathname', () => {
    const wrapper = mount(
      <MemoryRouter>
        <TaskConsoleNav pathname={'/TaskInbox'} />
      </MemoryRouter>
    ).find('TaskConsoleNav');

    expect(wrapper).toMatchSnapshot();

    const taskInboxNav = wrapper.findWhere(
      (nested) => nested.key() === 'task-inbox-nav'
    );

    expect(taskInboxNav.exists()).toBeTruthy();
    expect(taskInboxNav.props().isActive).toBeTruthy();
  });
});
