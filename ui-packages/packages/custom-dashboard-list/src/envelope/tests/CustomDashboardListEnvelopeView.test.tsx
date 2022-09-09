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
import CustomDashboardList from '../components/CustomDashboardList/CustomDashboardList';
import CustomDashboardListEnvelopeView, {
  CustomDashboardListEnvelopeViewApi
} from '../CustomDashboardListEnvelopeView';

describe('CustomDashboardListEnvelopeView tests', () => {
  jest.mock('../components/CustomDashboardList/CustomDashboardList');
  it('Snapshot', () => {
    const channelApi = new MockedMessageBusClientApi();

    const forwardRef = React.createRef<CustomDashboardListEnvelopeViewApi>();

    let wrapper = mount(
      <CustomDashboardListEnvelopeView
        channelApi={channelApi}
        ref={forwardRef}
      />
    ).find('CustomDashboardListEnvelopeView');

    expect(wrapper).toMatchSnapshot();

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize();
      }
    });

    wrapper = wrapper.update();

    const envelopeView = wrapper.find(CustomDashboardListEnvelopeView);

    expect(envelopeView).toMatchSnapshot();

    const dashboardList = envelopeView.find(CustomDashboardList);

    expect(dashboardList.exists()).toBeTruthy();
    expect(dashboardList.props().isEnvelopeConnectedToChannel).toBeTruthy();
    expect(dashboardList.props().driver).not.toBeNull();
  });
});
