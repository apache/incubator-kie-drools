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
import { act } from 'react-dom/test-utils';
import { mount } from 'enzyme';
import { MockedMessageBusClientApi } from './mocks/Mocks';
import ProcessListEnvelopeView, {
  ProcessListEnvelopeViewApi
} from '../ProcessListEnvelopeView';
import ProcessListPage from '../components/ProcessList/ProcessList';

jest.mock('../components/ProcessList/ProcessList');

describe('ProcessListEnvelopeView tests', () => {
  it('Snapshot', () => {
    const channelApi = new MockedMessageBusClientApi();

    const forwardRef = React.createRef<ProcessListEnvelopeViewApi>();

    let wrapper = mount(
      <ProcessListEnvelopeView channelApi={channelApi} ref={forwardRef} />
    );

    expect(wrapper.find('ProcessListEnvelopeView')).toMatchSnapshot();

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize({
          initialState: {
            filters: {
              status: []
            },
            sortBy: {}
          },
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows',
          isWorkflow: true
        });
      }
      wrapper = wrapper.update();
    });

    expect(wrapper.update().find(ProcessListEnvelopeView)).toMatchSnapshot();

    const processList = wrapper.find(ProcessListPage);

    expect(processList.exists()).toBeTruthy();
    expect(processList.props().isEnvelopeConnectedToChannel).toBeTruthy();
    expect(processList.props().driver).not.toBeNull();
  });
});
