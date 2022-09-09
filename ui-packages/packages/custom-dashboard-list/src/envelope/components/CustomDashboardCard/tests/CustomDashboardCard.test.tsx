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
import { mount } from 'enzyme';
import {
  MockedCustomDashboardListDriver,
  customDashboardInfos
} from '../../../tests/mocks/MockedCustomDashboardsListDriver';
import CustomDashboardCard from '../CustomDashboardCard';
import { Card } from '@patternfly/react-core';

describe('customDashboard card tests', () => {
  Date.now = jest.fn(() => 1487076708000);
  const driver = new MockedCustomDashboardListDriver();
  it('renders card - with tsx', () => {
    const wrapper = mount(
      <CustomDashboardCard
        driver={driver}
        customDashboardData={customDashboardInfos[0]}
      />
    );
    expect(wrapper).toMatchSnapshot();
  });
  it('renders card - with html', () => {
    const wrapper = mount(
      <CustomDashboardCard
        driver={driver}
        customDashboardData={customDashboardInfos[1]}
      />
    );
    expect(wrapper).toMatchSnapshot();
  });
  it('simulate click on card', () => {
    const openDashboardSpy = jest.spyOn(driver, 'openDashboard');
    const wrapper = mount(
      <CustomDashboardCard
        driver={driver}
        customDashboardData={customDashboardInfos[0]}
      />
    );
    wrapper.find(Card).simulate('click');
    expect(openDashboardSpy).toHaveBeenCalled();
  });
});
