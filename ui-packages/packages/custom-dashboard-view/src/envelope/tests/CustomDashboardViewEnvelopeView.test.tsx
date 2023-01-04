/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import CustomDashboardView from '../components/CustomDashboardView/CustomDashboardView';
import CustomDashboardViewEnvelopeView, {
  CustomDashboardViewEnvelopeViewApi
} from '../CustomDashboardViewEnvelopeView';

describe('CustomDashboardViewEnvelopeView tests', () => {
  beforeEach(() => {
    jest.mock('../components/CustomDashboardView/CustomDashboardView');
    jest.mock('../../api/CustomDashboardViewDriver');
  });

  it('Snapshot', async () => {
    const channelApi = new MockedMessageBusClientApi();
    (
      channelApi.requests
        .customDashboardView__getCustomDashboardView as jest.Mock
    ).mockResolvedValue('its a yml file');
    const forwardRef = React.createRef<CustomDashboardViewEnvelopeViewApi>();

    let wrapper;
    await act(async () => {
      wrapper = mount(
        <CustomDashboardViewEnvelopeView
          channelApi={channelApi}
          ref={forwardRef}
        />
      ).find('CustomDashboardViewEnvelopeView');
    });

    expect(wrapper).toMatchSnapshot();

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize('name');
      }
    });

    wrapper = wrapper.update();

    expect(wrapper.find(CustomDashboardViewEnvelopeView)).toMatchSnapshot();

    const envelopeView = wrapper.find(CustomDashboardView);

    envelopeView.update();
    expect(
      envelopeView.find(CustomDashboardView).props()[
        'isEnvelopeConnectedToChannel'
      ]
    ).toEqual(true);
    expect(
      envelopeView.find('CustomDashboardView').props()['driver']
    ).not.toBeNull();
    expect(
      envelopeView.find('CustomDashboardView').props()['customDashboardName']
    ).toEqual('name');
  });
});
