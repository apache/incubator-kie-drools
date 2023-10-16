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
import { render, screen } from '@testing-library/react';
import { MockedMessageBusClientApi } from './mocks/Mocks';
import CustomDashboardList from '../components/CustomDashboardList/CustomDashboardList';
import CustomDashboardListEnvelopeView, {
  CustomDashboardListEnvelopeViewApi
} from '../CustomDashboardListEnvelopeView';

describe('CustomDashboardListEnvelopeView tests', () => {
  jest.mock('../components/CustomDashboardList/CustomDashboardList');
  it('Snapshot', async () => {
    const channelApi = new MockedMessageBusClientApi();

    const forwardRef = React.createRef<CustomDashboardListEnvelopeViewApi>();
    let container;
    await act(async () => {
      container = render(
        <CustomDashboardListEnvelopeView
          channelApi={channelApi}
          ref={forwardRef}
        />
      ).container;
    });

    expect(container).toMatchSnapshot();

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize();
      }
    });

    const CustomDashboardListEnvelopeViewCheck =
      container.getElementsByClassName('pf-m-toggle-group-container');
    expect(CustomDashboardListEnvelopeViewCheck).toMatchSnapshot();

    const dashboardList = screen.getByText('Loading Dashboard...');

    expect(dashboardList).toBeTruthy();

    const listTable = container.querySelector('table');

    expect(listTable).toBeTruthy();
  });
});
