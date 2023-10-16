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
import TaskConsole from '../TaskConsole';
import { ApolloClient } from 'apollo-client';
import { TestUserContext } from '@kogito-apps/consoles-common/dist/environment/context';
import TaskConsoleRoutes from '../../TaskConsoleRoutes/TaskConsoleRoutes';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('../../TaskConsoleNav/TaskConsoleNav');
jest.mock('../../TaskConsoleRoutes/TaskConsoleRoutes');

jest.mock(
  '@kogito-apps/consoles-common/dist/components/layout/PageLayout',
  () =>
    Object.assign({}, jest.requireActual('@kogito-apps/consoles-common'), {
      PageLayout: () => {
        return <MockedComponent />;
      }
    })
);

jest.mock('apollo-client');

describe('TaskConsole tests', () => {
  it('Snapshot', () => {
    const client = jest
      .fn()
      .mockImplementation() as unknown as ApolloClient<any>;
    const testContext = new TestUserContext();
    const props = {
      apolloClient: client,
      userContext: testContext
    };
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const wrapper = mount(
      <TaskConsole {...props}>
        <TaskConsoleRoutes />
      </TaskConsole>
    ).find('TaskConsole');

    expect(wrapper).toMatchSnapshot();
  });
});
