/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import { mount } from 'enzyme';
import { act } from 'react-dom/test-utils';
import CloudEventFormEnvelopeView, {
  CloudEventFormEnvelopeViewApi
} from '../CloudEventFormEnvelopeView';
import { MockedMessageBusClientApi } from './mocks/Mocks';
import CloudEventForm from '../components/CloudEventForm/CloudEventForm';

jest.mock('../components/CloudEventForm/CloudEventForm');

describe('CloudEventFormEnvelopeView tests', () => {
  it('Snapshot', () => {
    const channelApi = new MockedMessageBusClientApi();

    const forwardRef = React.createRef<CloudEventFormEnvelopeViewApi>();

    let wrapper = mount(
      <CloudEventFormEnvelopeView channelApi={channelApi} ref={forwardRef} />
    ).find('CloudEventFormEnvelopeView');

    expect(wrapper).toMatchSnapshot();

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize({
          isNewInstanceEvent: true,
          defaultValues: {
            cloudEventSource: '/local/test',
            instanceId: '1234'
          }
        });
      }
    });

    wrapper = wrapper.update();

    const envelopeView = wrapper.find(CloudEventFormEnvelopeView);

    expect(envelopeView).toMatchSnapshot();

    const cloudEventForm = envelopeView.find(CloudEventForm);

    expect(cloudEventForm.exists()).toBeTruthy();
    expect(cloudEventForm.props().driver).not.toBeNull();
    expect(cloudEventForm.props().isNewInstanceEvent).toBeTruthy();
    expect(cloudEventForm.props().defaultValues).not.toBeNull();
  });
});
