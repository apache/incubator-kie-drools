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
import {
  CustomDashboardFilter,
  CustomDashboardInfo,
  CustomDashboardListDriver
} from '../../api';
import { CustomDashboardListChannelApiImpl } from '../CustomDashboardListChannelApiImpl';
import { MockedCustomDashboardListDriver } from './utils/Mocks';

let driver: CustomDashboardListDriver;
let api: CustomDashboardListChannelApiImpl;

describe('CustomDashboardListChannelApiImpl tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    driver = new MockedCustomDashboardListDriver();
    api = new CustomDashboardListChannelApiImpl(driver);
  });

  it('CustomDashboardList__getFilter', () => {
    api.customDashboardList__getFilter();
    expect(driver.getCustomDashboardFilter).toHaveBeenCalled();
  });

  it('CustomDashboardList__applyFilter', () => {
    const filter: CustomDashboardFilter = {
      customDashboardNames: ['dashboard1']
    };
    api.customDashboardList__applyFilter(filter);
    expect(driver.applyFilter).toHaveBeenCalledWith(filter);
  });

  it('customDashboardList__getCustomDashboardQuery', () => {
    api.customDashboardList__getCustomDashboardQuery();
    expect(driver.getCustomDashboardsQuery).toHaveBeenCalled();
  });

  it('customDashboardList__openDashboard', () => {
    const data: CustomDashboardInfo = {
      name: 'dashboard1',
      path: '/user/home',
      lastModified: new Date(new Date('2022-07-11T18:30:00.000Z'))
    };
    api.customDashboardList__openDashboard(data);
    expect(driver.openDashboard).toHaveBeenCalledWith(data);
  });
});
