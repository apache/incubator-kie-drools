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
import FormsList from '../components/FormsList/FormsList';
import FormsListEnvelopeView, {
  FormsListEnvelopeViewApi
} from '../FormsListEnvelopeView';

jest.mock('../components/FormsList/FormsList');

describe('FormsListEnvelopeView tests', () => {
  it('Snapshot', () => {
    const channelApi = new MockedMessageBusClientApi();

    const forwardRef = React.createRef<FormsListEnvelopeViewApi>();

    let wrapper = mount(
      <FormsListEnvelopeView channelApi={channelApi} ref={forwardRef} />
    ).find('FormsListEnvelopeView');

    expect(wrapper).toMatchSnapshot();

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize();
      }
    });

    wrapper = wrapper.update();

    const envelopeView = wrapper.find(FormsListEnvelopeView);

    expect(envelopeView).toMatchSnapshot();

    const formsList = envelopeView.find(FormsList);

    expect(formsList.exists()).toBeTruthy();
    expect(formsList.props().isEnvelopeConnectedToChannel).toBeTruthy();
    expect(formsList.props().driver).not.toBeNull();
  });
});
