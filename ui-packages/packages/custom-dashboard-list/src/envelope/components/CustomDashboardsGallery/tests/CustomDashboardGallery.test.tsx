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
import CustomDashboardsGallery from '../CustomDashboardsGallery';
import { render, screen } from '@testing-library/react';
import {
  customDashboardInfos,
  MockedCustomDashboardListDriver
} from '../../../tests/mocks/MockedCustomDashboardsListDriver';
import { KogitoSpinner } from '@kogito-apps/components-common/dist/components/KogitoSpinner';
import { KogitoEmptyState } from '@kogito-apps/components-common/dist/components/KogitoEmptyState';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

describe('customDashboard gallery tests', () => {
  Date.now = jest.fn(() => 1487076708000);
  const driver = new MockedCustomDashboardListDriver();
  it('renders gallery of customDashboard', () => {
    const { container } = render(
      <CustomDashboardsGallery
        driver={driver}
        isLoading={false}
        customDashboardsDatas={customDashboardInfos}
      />
    );
    expect(container).toMatchSnapshot();
  });

  it('renders loading component', () => {
    const { container } = render(
      <CustomDashboardsGallery
        driver={driver}
        isLoading={true}
        customDashboardsDatas={customDashboardInfos}
      />
    );
    expect(container).toMatchSnapshot();
    const checkLoading = screen.getByText('Loading customDashboard...');
    expect(checkLoading).toBeTruthy();
  });

  it('renders empty state component', () => {
    const { container } = render(
      <CustomDashboardsGallery
        driver={driver}
        isLoading={false}
        customDashboardsDatas={[]}
      />
    );
    expect(container).toMatchSnapshot();
    const checkEmptyState = screen.getByText('No results found');
    expect(checkEmptyState).toBeTruthy();
  });
});
