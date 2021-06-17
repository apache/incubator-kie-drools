/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { act } from 'react-dom/test-utils';
import { GraphQL, KogitoAppContextProvider } from '@kogito-apps/common';
import { mount } from 'enzyme';
import { MockedProvider } from '@apollo/react-testing';
import { TestingUserContext } from '../../../../../util/tests/utils/TestingUserContext';
import TaskConsoleContext, {
  ITaskConsoleContext
} from '../../../../../context/TaskConsoleContext/TaskConsoleContext';
import TaskConsoleFilterContext, {
  ITaskConsoleFilterContext
} from '../../../../../context/TaskConsoleFilterContext/TaskConsoleFilterContext';
import { MemoryRouter as Router } from 'react-router';
import TaskInbox from '../../TaskInbox';
import wait from 'waait';
import React from 'react';

export const getTaskInboxWrapper = async (
  mocks,
  consoleContext: ITaskConsoleContext<GraphQL.UserTaskInstance>,
  filterContext: ITaskConsoleFilterContext
) => {
  let wrapper;

  await act(async () => {
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks} addTypename={false}>
          <KogitoAppContextProvider userContext={new TestingUserContext()}>
            <TaskConsoleContext.Provider value={consoleContext}>
              <TaskConsoleFilterContext.Provider value={filterContext}>
                <Router keyLength={0}>
                  <TaskInbox />
                </Router>
              </TaskConsoleFilterContext.Provider>
            </TaskConsoleContext.Provider>
          </KogitoAppContextProvider>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('TaskInbox');
    });
    await wait();
  });

  return (wrapper = wrapper.update().find(TaskInbox));
};
