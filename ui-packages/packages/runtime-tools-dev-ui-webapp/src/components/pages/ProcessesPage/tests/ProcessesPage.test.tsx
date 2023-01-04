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
import ProcessesPage from '../ProcessesPage';
import { BrowserRouter, MemoryRouter } from 'react-router-dom';
import * as H from 'history';
import { act } from 'react-dom/test-utils';
import * as RuntimeToolsDevUIAppContext from '../../../contexts/DevUIAppContext';
import DevUIAppContextProvider from '../../../contexts/DevUIAppContextProvider';
jest.mock('../../../containers/ProcessListContainer/ProcessListContainer');
jest.mock(
  '../../../containers/ProcessDefinitionListContainer/ProcessDefinitionListContainer'
);
describe('ProcessesPage tests', () => {
  const props = {
    match: {
      params: {
        instanceID: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
      },
      url: '',
      isExact: false,
      path: ''
    },
    location: H.createLocation(''),
    history: H.createBrowserHistory()
  };

  it('Snapshot - processList page', () => {
    const wrapper = mount(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <ProcessesPage {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(wrapper.find(ProcessesPage)).toMatchSnapshot();
    expect(wrapper.find('MockedProcessListContainer').exists()).toBeTruthy();
  });

  it('Snapshot - processDefinitionList page', async () => {
    const wrapper = mount(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
        customLabels={{
          singularProcessLabel: 'Process',
          pluralProcessLabel: 'Processes'
        }}
      >
        <MemoryRouter>
          <ProcessesPage {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );
    await act(async () => {
      wrapper.find('TabButton').at(1).find('button').simulate('click');
    });
    await act(async () => {
      wrapper = wrapper.update();
    });
    expect(wrapper.find('MockedProcessListContainer').exists()).toBeTruthy();
  });
});
