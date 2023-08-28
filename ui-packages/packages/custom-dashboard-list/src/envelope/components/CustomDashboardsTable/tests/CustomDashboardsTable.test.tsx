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
import { render, screen } from '@testing-library/react';
import CustomDashboardsTable from '../CustomDashboardsTable';
import {
  customDashboardInfos,
  MockedCustomDashboardListDriver
} from '../../../tests/mocks/MockedCustomDashboardsListDriver';
import { DataTable } from '@kogito-apps/components-common/dist/components/DataTable';

Date.now = jest.fn(() => 1487076708000); //14.02.2017

describe('customDashboard table test', () => {
  const driver = new MockedCustomDashboardListDriver();
  it('renders table', () => {
    const { container } = render(
      <CustomDashboardsTable
        driver={driver}
        isLoading={false}
        customDashboardData={customDashboardInfos}
        setDashboardsData={jest.fn()}
      />
    );
    expect(container).toMatchSnapshot();
    const checkTable = container.querySelector('table');
    expect(checkTable).toBeTruthy();
  });
});
