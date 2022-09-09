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
import CustomDashboardListContainer from '../CustomDashboardListContainer';
import { CustomDashboardListGatewayApiImpl } from '../../../../channel/CustomDashboardList/CustomDashboardListGatewayApi';
import * as CustomDashboardListContext from '../../../../channel/CustomDashboardList/CustomDashboardListContext';

jest
  .spyOn(CustomDashboardListContext, 'useCustomDashboardListGatewayApi')
  .mockImplementation(() => new CustomDashboardListGatewayApiImpl());

describe('CustomDashboardListContainer tests', () => {
  it('Snapshot', () => {
    const wrapper = mount(<CustomDashboardListContainer />);

    expect(wrapper).toMatchSnapshot();

    const forwardRef = wrapper.childAt(0);

    expect(forwardRef.props().driver).not.toBeNull();

    expect(forwardRef.props().targetOrigin).toBe('*');
  });
});
