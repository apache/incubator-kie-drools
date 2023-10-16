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
import { act } from 'react-dom/test-utils';
import { mount } from 'enzyme';
import { MockedMessageBusClientApi } from './mocks/Mocks';
import ProcessFormEnvelopeView, {
  ProcessFormEnvelopeViewApi
} from '../ProcessFormEnvelopeView';
import ProcessForm from '../components/ProcessForm/ProcessForm';

jest.mock('../components/ProcessForm/ProcessForm');

describe('ProcessFormEnvelopeView tests', () => {
  it('Snapshot', () => {
    const channelApi = MockedMessageBusClientApi();
    const forwardRef = React.createRef<ProcessFormEnvelopeViewApi>();

    let wrapper = mount(
      <ProcessFormEnvelopeView channelApi={channelApi} ref={forwardRef} />
    ).find('ProcessFormEnvelopeView');

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize({
          processName: 'process1',
          endpoint: 'http://localhost:4000'
        });
      }
    });

    wrapper = wrapper.update();

    expect(wrapper.find(ProcessFormEnvelopeView)).toMatchSnapshot();

    const processForm = wrapper.find(ProcessForm);

    expect(processForm.exists()).toBeTruthy();
    expect(processForm.props().isEnvelopeConnectedToChannel).toBeTruthy();
    expect(processForm.props().driver).not.toBeNull();
  });
});
