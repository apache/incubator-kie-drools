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
import Breadcrumbs from '../Breadcrumbs';
import { mount } from 'enzyme';
import { MemoryRouter } from 'react-router-dom';
import { MemoryRouterProps } from 'react-router';
import { TrustyContext } from '../../../Templates/TrustyApp/TrustyApp';

const setupWrapper = (routerEntries: MemoryRouterProps['initialEntries']) => {
  return mount(
    <MemoryRouter initialEntries={routerEntries}>
      <TrustyContext.Provider
        value={{
          config: {
            counterfactualEnabled: true,
            explanationEnabled: true,
            basePath: '',
            useHrefLinks: true
          }
        }}
      >
        <Breadcrumbs />
      </TrustyContext.Provider>
    </MemoryRouter>
  );
};

describe('Breadcrumbs', () => {
  test('renders correctly', () => {
    const wrapper = setupWrapper(['/audit']);
    const breadcrumbs = wrapper.find(Breadcrumbs);

    expect(breadcrumbs).toMatchSnapshot();
    expect(breadcrumbs.find('li.breadcrumb-item')).toHaveLength(0);
  });

  test('renders outcome details breadcrumbs links', () => {
    const executionId = 'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000';
    const wrapper = setupWrapper([
      {
        pathname: `/audit/decision/${executionId}/outcomes`,
        key: 'execution'
      }
    ]);
    const breadcrumbs = wrapper.find(Breadcrumbs);

    expect(breadcrumbs).toMatchSnapshot();
    expect(breadcrumbs.find('li.breadcrumb-item')).toHaveLength(3);
    expect(breadcrumbs.find('li.breadcrumb-item').at(0).text()).toMatch(
      'Audit investigation'
    );
    expect(breadcrumbs.find('li.breadcrumb-item').at(1).text()).toMatch(
      `Execution #${executionId.substring(0, 8)}`
    );
    expect(breadcrumbs.find('li.breadcrumb-item').at(2).text()).toMatch(
      'Outcomes'
    );
    expect(
      breadcrumbs.find('BreadcrumbItem').at(0).prop('isActive') as boolean
    ).toBeFalsy();
    expect(
      breadcrumbs.find('BreadcrumbItem').at(1).prop('isActive') as boolean
    ).toBeFalsy();
    expect(
      breadcrumbs.find('BreadcrumbItem').at(2).prop('isActive') as boolean
    ).toBeTruthy();
  });
});
