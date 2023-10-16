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
import { render } from '@testing-library/react';
import { MockedMessageBusClientApi } from './mocks/Mocks';
import CustomDashboardViewEnvelopeView, {
  CustomDashboardViewEnvelopeViewApi
} from '../CustomDashboardViewEnvelopeView';

describe('CustomDashboardViewEnvelopeView tests', () => {
  it('Snapshot', async () => {
    const channelApi = new MockedMessageBusClientApi();
    (
      channelApi.requests
        .customDashboardView__getCustomDashboardView as jest.Mock
    ).mockResolvedValue('its a yml file');
    const forwardRef = React.createRef<CustomDashboardViewEnvelopeViewApi>();
    let container;
    await act(async () => {
      container = render(
        <CustomDashboardViewEnvelopeView
          channelApi={channelApi}
          ref={forwardRef}
        />
      ).container;
    });
    expect(container).toMatchSnapshot();

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize('name', 'targetOrigin');
      }
    });

    const checkIframe = container.querySelector('iframe');

    expect(checkIframe).toMatchSnapshot();
    const iframeWrapper = container.querySelector('iframe');

    expect(iframeWrapper?.getAttribute('src')).toEqual(
      'resources/webapp/custom-dashboard-view/dashbuilder/index.html'
    );
  });
});
