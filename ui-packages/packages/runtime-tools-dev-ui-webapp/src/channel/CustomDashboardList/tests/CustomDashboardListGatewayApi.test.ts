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

import {
  CustomDashboardListGatewayApi,
  CustomDashboardListGatewayApiImpl,
  OnOpenDashboardListener
} from '../CustomDashboardListGatewayApi';
import { getCustomDashboard } from '../../apis';
import { CustomDashboardInfo } from '@kogito-apps/custom-dashboard-list';

jest.mock('../../apis/apis', () => ({
  getCustomDashboard: jest.fn()
}));

let gatewayApi: CustomDashboardListGatewayApi;

describe('CustomDashboardListGatewayApi tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    gatewayApi = new CustomDashboardListGatewayApiImpl();
  });

  it('applyFilter', async () => {
    const filter = {
      customDashboardNames: ['dashboard1']
    };
    gatewayApi.applyFilter(filter);
    expect(await gatewayApi.getCustomDashboardFilter()).toEqual(filter);
  });

  it('getCustomDashboard', async () => {
    const filter = {
      customDashboardNames: ['dashboard1']
    };
    gatewayApi.applyFilter(filter);
    gatewayApi.getCustomDashboardsQuery();
    expect(await gatewayApi.getCustomDashboardFilter()).toEqual(filter);
  });

  it('openDashboard', () => {
    const dashboardInfo: CustomDashboardInfo = {
      name: 'dashboard',
      path: '/user/html',
      lastModified: new Date(2020, 6, 12)
    };
    const listener: OnOpenDashboardListener = {
      onOpen: jest.fn()
    };

    const unsubscribe = gatewayApi.onOpenCustomDashboardListen(listener);

    gatewayApi.openDashboard(dashboardInfo);

    expect(listener.onOpen).toHaveBeenLastCalledWith(dashboardInfo);

    unsubscribe.unSubscribe();
  });
});
