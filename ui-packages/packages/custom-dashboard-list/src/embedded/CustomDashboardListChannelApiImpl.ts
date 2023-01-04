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
  CustomDashboardListDriver,
  CustomDashboardListChannelApi,
  CustomDashboardFilter,
  CustomDashboardInfo
} from '../api';

/**
 * Implementation of the CustomDashboardListChannelApiImpl delegating to a CustomDashboardListDriver
 */
export class CustomDashboardListChannelApiImpl
  implements CustomDashboardListChannelApi
{
  constructor(private readonly driver: CustomDashboardListDriver) {}

  customDashboardList__getFilter(): Promise<CustomDashboardFilter> {
    return this.driver.getCustomDashboardFilter();
  }

  customDashboardList__applyFilter(
    customDashboardFilter: CustomDashboardFilter
  ): Promise<void> {
    return this.driver.applyFilter(customDashboardFilter);
  }

  customDashboardList__getCustomDashboardQuery(): Promise<
    CustomDashboardInfo[]
  > {
    return this.driver.getCustomDashboardsQuery();
  }

  customDashboardList__openDashboard(
    customDashboardInfo: CustomDashboardInfo
  ): Promise<void> {
    return this.driver.openDashboard(customDashboardInfo);
  }
}
