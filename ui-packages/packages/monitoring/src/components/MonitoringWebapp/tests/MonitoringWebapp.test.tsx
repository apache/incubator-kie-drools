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
import MonitoringWebapp from '../MonitoringWebapp';

describe('Monitoring Webapp Tests', () => {
  it('Snapshot - Monitoring Webapp', async () => {
    const wrapper = mount(<MonitoringWebapp></MonitoringWebapp>);
    expect(wrapper).toMatchSnapshot();
  });
  it('Snapshot - Monitoring Webapp with parameters', async () => {
    const wrapper = mount(
      <MonitoringWebapp
        dataIndexUrl="http://localhost:8180"
        dashboard="someDashboard.yml"
        workflow="test123"
      ></MonitoringWebapp>
    );
    expect(wrapper).toMatchSnapshot();
  });
});
