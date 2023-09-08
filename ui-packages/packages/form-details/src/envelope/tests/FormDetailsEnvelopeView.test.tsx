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
import FormDetails from '../components/FormDetails/FormDetails';
import FormDetailsEnvelopeView, {
  FormDetailsEnvelopeViewApi
} from '../FormDetailsEnvelopeView';

jest.mock('../components/FormDetails/FormDetails');

describe('FormDetailsEnvelopeView tests', () => {
  it('Snapshot', () => {
    const channelApi = new MockedMessageBusClientApi();

    const forwardRef = React.createRef<FormDetailsEnvelopeViewApi>();

    let wrapper = mount(
      <FormDetailsEnvelopeView
        channelApi={channelApi}
        ref={forwardRef}
        targetOrigin="http://localhost:9000"
      />
    ).find('FormDetailsEnvelopeView');

    expect(wrapper).toMatchSnapshot();

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize();
      }
    });

    wrapper = wrapper.update();

    const envelopeView = wrapper.find(FormDetailsEnvelopeView);

    expect(envelopeView).toMatchSnapshot();

    const formDetails = envelopeView.find(FormDetails);

    expect(formDetails.exists()).toBeTruthy();
    expect(formDetails.props().isEnvelopeConnectedToChannel).toBeTruthy();
    expect(formDetails.props().driver).not.toBeNull();
  });
});
