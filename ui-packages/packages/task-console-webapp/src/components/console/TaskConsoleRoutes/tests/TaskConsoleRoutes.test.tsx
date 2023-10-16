/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React from 'react';
import { mount } from 'enzyme';
import TaskConsoleRoutes from '../TaskConsoleRoutes';
import { MemoryRouter, Route } from 'react-router-dom';
import { TaskDetailsPage, TaskInboxPage } from '../../../pages';

jest.mock('../../../pages/TaskInboxPage/TaskInboxPage');
jest.mock('../../../pages/TaskDetailsPage/TaskDetailsPage');

describe('TaskConsoleRoutes tests', () => {
  it('Default route test', () => {
    const wrapper = mount(
      <MemoryRouter keyLength={0} initialEntries={['/']}>
        <TaskConsoleRoutes />
      </MemoryRouter>
    ).find('TaskConsoleRoutes');

    expect(wrapper).toMatchSnapshot();

    const route = wrapper.find(Route);
    expect(route.exists()).toBeTruthy();

    const taskInboxPage = wrapper.find(TaskInboxPage);
    expect(taskInboxPage.exists()).toBeTruthy();
  });

  it('TaskInbox route test', () => {
    const wrapper = mount(
      <MemoryRouter keyLength={0} initialEntries={['/TaskInbox']}>
        <TaskConsoleRoutes />
      </MemoryRouter>
    ).find('TaskConsoleRoutes');

    expect(wrapper).toMatchSnapshot();

    const route = wrapper.find(Route);
    expect(route.exists()).toBeTruthy();

    const taskInboxPage = wrapper.find(TaskInboxPage);
    expect(taskInboxPage.exists()).toBeTruthy();
  });

  it('TaskDetails route test', () => {
    const wrapper = mount(
      <MemoryRouter keyLength={0} initialEntries={['/TaskDetails/id']}>
        <TaskConsoleRoutes />
      </MemoryRouter>
    ).find('TaskConsoleRoutes');

    expect(wrapper).toMatchSnapshot();

    const route = wrapper.find(Route);
    expect(route.exists()).toBeTruthy();

    const taskInboxPage = wrapper.find(TaskDetailsPage);
    expect(taskInboxPage.exists()).toBeTruthy();
  });
});
