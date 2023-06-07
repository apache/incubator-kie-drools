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
import { ProcessListState } from '@kogito-apps/management-console-shared';
import ProcessListContainer from '../ProcessListContainer';
import * as ProcessListContext from '../../../../channel/ProcessList/ProcessListContext';
import { ProcessListQueries } from '../../../../channel/ProcessList/ProcessListQueries';
import { ProcessListGatewayApiImpl } from '../../../../channel/ProcessList/ProcessListGatewayApi';
import DevUIAppContextProvider from '../../../contexts/DevUIAppContextProvider';
import { EmbeddedProcessList } from '@kogito-apps/process-list';
import { DefaultUser, User } from '@kogito-apps/consoles-common';

const MockQueries = jest.fn<ProcessListQueries, []>(() => ({
  getProcessInstances: jest.fn(),
  getChildProcessInstances: jest.fn(),
  handleProcessSkip: jest.fn(),
  handleProcessAbort: jest.fn(),
  handleProcessRetry: jest.fn(),
  handleProcessMultipleAction: jest.fn()
}));

jest
  .spyOn(ProcessListContext, 'useProcessListGatewayApi')
  .mockImplementation(() => new ProcessListGatewayApiImpl(new MockQueries()));

const user: User = new DefaultUser('jon', []);
const appContextProps = {
  devUIUrl: 'http://localhost:9000',
  openApiPath: '/mocked',
  isProcessEnabled: false,
  isTracingEnabled: false,
  omittedProcessTimelineEvents: [],
  isStunnerEnabled: false,
  availablePages: [],
  customLabels: {
    singularProcessLabel: 'test-singular',
    pluralProcessLabel: 'test-plural'
  },
  diagramPreviewSize: { width: 100, height: 100 }
};
describe('ProcessListContainer tests', () => {
  const props = {
    initialState: {} as ProcessListState
  };
  it('Snapshot', () => {
    const wrapper = mount(
      <DevUIAppContextProvider users={[user]} {...appContextProps}>
        <ProcessListContainer {...props} />
      </DevUIAppContextProvider>
    );

    expect(wrapper).toMatchSnapshot();

    const forwardRef = wrapper.find(EmbeddedProcessList);

    expect(forwardRef.props().driver).not.toBeNull();
    expect(forwardRef.props().targetOrigin).toBe('http://localhost:9000');
  });
});
