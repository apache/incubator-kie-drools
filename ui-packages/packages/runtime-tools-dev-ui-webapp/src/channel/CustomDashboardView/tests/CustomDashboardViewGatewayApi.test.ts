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
  CustomDashboardViewGatewayApi,
  CustomDashboardViewGatewayApiImpl
} from '../CustomDashboardViewGatewayApi';
import { getCustomDashboardContent } from '../../apis';

jest.mock('../../apis/apis', () => ({
  getCustomDashboardContent: jest.fn()
}));

let gatewayApi: CustomDashboardViewGatewayApi;

describe('CustomDashboardViewGatewayApiImpl tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    gatewayApi = new CustomDashboardViewGatewayApiImpl();
  });
  const result = "it's a yml file";
  it('getCustomDashboardContent', async () => {
    const name = 'name';
    await gatewayApi.getCustomDashboardContent(name);
    expect(getCustomDashboardContent).toHaveBeenCalledWith(name);
  });
});
