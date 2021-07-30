/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { act } from 'react-dom/test-utils';
import * as Keycloak from '../../../../utils/KeycloakClient';
import { PageSidebar } from '@patternfly/react-core';
import { mount } from 'enzyme';

import PageLayout from '../PageLayout';

const props = {
  children: <React.Fragment>children rendered</React.Fragment>,
  BrandSrc: '../../../../static/kogito.png',
  PageNav: <React.Fragment>page Navigation elements</React.Fragment>,
  BrandAltText: 'Kogito logo',
  withHeader: true,
  BrandClick: jest.fn()
};

jest.mock('../../PageToolbar/PageToolbar');

describe('PageLayout component tests', () => {
  const isAuthEnabledMock = jest.spyOn(Keycloak, 'isAuthEnabled');
  isAuthEnabledMock.mockReturnValue(false);

  it('snapshot tests', () => {
    const wrapper = mount(<PageLayout {...props} />).find('PageLayout');
    expect(wrapper).toMatchSnapshot();
  });

  it('open with PageSidebar closed', () => {
    let wrapper = mount(<PageLayout {...props} pageNavOpen={false} />).find(
      'PageLayout'
    );
    expect(wrapper).toMatchSnapshot();

    let pageSidebar = wrapper.find(PageSidebar);
    expect(pageSidebar.exists()).toBeTruthy();
    expect(pageSidebar.props().isNavOpen).toBeFalsy();

    const event = {
      target: {}
    } as React.MouseEvent<HTMLInputElement>;
    act(() => {
      wrapper.find('Button').prop('onClick')(event);
    });

    wrapper = wrapper.update();
    expect(wrapper).toMatchSnapshot();

    pageSidebar = wrapper.find(PageSidebar);
    expect(pageSidebar.exists()).toBeTruthy();
    expect(pageSidebar.props().isNavOpen).toBeTruthy();
  });

  it('check isNavOpen boolean', () => {
    const wrapper = mount(<PageLayout {...props} />).find('PageLayout');
    const event = {
      target: {}
    } as React.MouseEvent<HTMLInputElement>;
    act(() => {
      wrapper.find('Button').prop('onClick')(event);
      wrapper.update();
    });
    expect(wrapper.find('PageSidebar').prop('isNavOpen')).toBeTruthy();
  });
});
