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
  CustomDashboardFilter,
  CustomDashboardInfo,
  CustomDashboardListDriver
} from '../../../api';

export const customDashboardInfos: CustomDashboardInfo[] = [
  {
    name: 'dashboard1',
    path: '/user/home',
    lastModified: new Date('2022-07-11T18:30:00.000Z')
  },
  {
    name: 'dashboard2',
    path: '/user/home',
    lastModified: new Date('2022-07-11T18:30:00.000Z')
  }
];
export class MockedCustomDashboardListDriver
  implements CustomDashboardListDriver
{
  applyFilter(customDashboardFilter: CustomDashboardFilter): Promise<void> {
    return Promise.resolve();
  }

  getCustomDashboardFilter(): Promise<CustomDashboardFilter> {
    return Promise.resolve({ customDashboardNames: [] });
  }

  getCustomDashboardsQuery(): Promise<CustomDashboardInfo[]> {
    return Promise.resolve(customDashboardInfos);
  }

  openDashboard(customDashboardInfo: CustomDashboardInfo): Promise<void> {
    return Promise.resolve();
  }
}
