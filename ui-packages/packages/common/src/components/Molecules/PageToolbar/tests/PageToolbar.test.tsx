/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { shallow } from 'enzyme';
import PageToolbar from '../PageToolbar';
import { getWrapper } from '../../../../utils/OuiaUtils';
import { Dropdown } from '@patternfly/react-core';
import { setTestKogitoAppContextModeToTest } from '../../../../environment/auth/tests/utils/KogitoAppContextTestingUtils';

jest.mock('../../AboutModalBox/AboutModalBox');
jest.mock('../../PageToolbarUsersDropdownGroup/PageToolbarUsersDropdownGroup');
jest.mock('../../../Atoms/AddTestUser/AddTestUser');

describe('PageToolbar component tests', () => {
  beforeEach(() => {
    setTestKogitoAppContextModeToTest(true);
  });

  it('Snapshot testing', () => {
    const wrapper = getWrapper(<PageToolbar />, 'PageToolbar');

    expect(wrapper).toMatchSnapshot();
  });

  it('Testing select dropdown test', () => {
    let wrapper = shallow(<PageToolbar />);

    let dropdown = wrapper.find(Dropdown);

    expect(dropdown.prop('isOpen')).toBeFalsy();

    act(() => {
      dropdown.prop('onSelect')();
    });

    wrapper = wrapper.update();

    dropdown = wrapper.find(Dropdown);

    expect(dropdown.prop('isOpen')).toBeTruthy();
  });

  it('Testing toggle dropdown test', () => {
    let wrapper = shallow(<PageToolbar />);

    let dropdown = wrapper.find(Dropdown);

    expect(dropdown.prop('isOpen')).toBeFalsy();

    act(() => {
      dropdown.prop('toggle').props.onToggle(true);
    });

    wrapper = wrapper.update();

    dropdown = wrapper.find(Dropdown);

    expect(dropdown.prop('isOpen')).toBeTruthy();
  });

  it('handleAboutModalToggle test', () => {
    const wrapper = getWrapper(<PageToolbar />, 'PageToolbar');

    let aboutModalBox = wrapper.find('MockedAboutModalBox');

    expect(aboutModalBox.exists()).toBeTruthy();

    expect(aboutModalBox.prop('isOpenProp')).toBeFalsy();

    act(() => {
      // tslint:disable:no-string-literal
      aboutModalBox.props()['handleModalToggleProp']();
    });

    aboutModalBox = wrapper.update().find('MockedAboutModalBox');

    expect(aboutModalBox.prop('isOpenProp')).toBeTruthy();
  });

  it('Testing handleaddUserModalToggle - dev mode', () => {
    const wrapper = getWrapper(<PageToolbar />, 'PageToolbar');

    let addUserModal = wrapper.find('MockedAddTestUser');

    expect(addUserModal.exists()).toBeTruthy();

    expect(addUserModal.prop('isOpen')).toBeFalsy();

    act(() => {
      addUserModal.props()['toggleModal']();
    });

    addUserModal = wrapper.update().find('MockedAddTestUser');

    expect(addUserModal.prop('isOpen')).toBeTruthy();
  });

  it('Testing handleaddUserModalToggle test - prod mode', () => {
    setTestKogitoAppContextModeToTest(false);

    const wrapper = getWrapper(<PageToolbar />, 'PageToolbar');

    let addUserModal = wrapper.find('MockedAddTestUser');

    expect(addUserModal.exists()).toBeTruthy();

    expect(addUserModal.prop('isOpen')).toBeFalsy();

    act(() => {
      addUserModal.props()['toggleModal']();
    });

    addUserModal = wrapper.update().find('MockedAddTestUser');

    expect(addUserModal.prop('isOpen')).toBeFalsy();
  });

  it('Testing dropdown items - test mode', () => {
    const wrapper = shallow(<PageToolbar />);

    const dropdown = wrapper.find(Dropdown);

    const drodownItems = dropdown.prop('dropdownItems');

    expect(drodownItems.length).toStrictEqual(5);
  });

  it('Testing dropdown items - prod mode', () => {
    setTestKogitoAppContextModeToTest(false);

    const wrapper = shallow(<PageToolbar />);

    const dropdown = wrapper.find(Dropdown);

    const drodownItems = dropdown.prop('dropdownItems');

    expect(drodownItems.length).toStrictEqual(3);
  });
});
