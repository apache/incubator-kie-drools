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
import { Alert, Form } from '@patternfly/react-core';
import { mount } from 'enzyme';
import AddTestUser from '../AddTestUser';
import {
  resetTestKogitoAppContext,
  testIsTestUserSystemEnabledMock,
  testKogitoAppContext
} from '../../../../environment/auth/tests/utils/KogitoAppContextTestingUtils';
import { TestUserContext } from '../../../../environment/auth/TestUserContext';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-core', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-core'), {
    Alert: () => <MockedComponent />,
    Button: () => <MockedComponent />,
    Checkbox: () => <MockedComponent />,
    TextInput: () => <MockedComponent />
  })
);

const findFormInput = (wrapper, id: string) => {
  return wrapper.findWhere((element) => element.prop('id') === id);
};

const findFormGroup = (wrapper, id: string) => {
  return wrapper.findWhere((element) => element.prop('fieldId') === id);
};

describe('AddTestUser tests', () => {
  beforeEach(() => {
    testIsTestUserSystemEnabledMock.mockReturnValue(true);
    resetTestKogitoAppContext(false);
  });

  it('Snapshot test - TestUserSystem enabled', () => {
    const wrapper = mount(
      <AddTestUser isOpen={true} toggleModal={jest.fn()} />
    ).find('Stack');

    expect(wrapper).toMatchSnapshot();

    expect(wrapper.find(Alert).exists()).toBeTruthy();
    expect(wrapper.find(Form).exists()).toBeTruthy();
  });

  it('Snapshot test - TestUserSystem enabled - closed modal', () => {
    const wrapper = mount(
      <AddTestUser isOpen={false} toggleModal={jest.fn()} />
    ).find('Stack');

    expect(wrapper).toMatchSnapshot();

    expect(wrapper.find(Alert).exists()).toBeFalsy();
    expect(wrapper.find(Form).exists()).toBeFalsy();
  });

  it('Snapshot test - TestUserSystem disabled', () => {
    testIsTestUserSystemEnabledMock.mockReturnValue(false);

    const wrapper = mount(
      <AddTestUser isOpen={true} toggleModal={jest.fn()} />
    ).find('Stack');

    expect(wrapper).toMatchSnapshot();

    expect(wrapper.find(Alert).exists()).toBeFalsy();
    expect(wrapper.find(Form).exists()).toBeFalsy();
  });

  it('Cancel test', () => {
    const toggleModal = jest.fn();

    const wrapper = mount(
      <AddTestUser isOpen={true} toggleModal={toggleModal} />
    ).find('Form');

    expect(wrapper).toMatchSnapshot();

    const cancelButton = wrapper.findWhere(
      (element) => element.prop('id') === 'cancel-add-test-user'
    );

    expect(cancelButton.exists()).toBeTruthy();

    act(() => {
      cancelButton.prop('onClick')();
    });

    expect(toggleModal).toBeCalled();
  });

  it('Add test with validation error', () => {
    const toggleModal = jest.fn();

    let wrapper = mount(
      <AddTestUser isOpen={true} toggleModal={toggleModal} />
    ).find('Form');

    expect(wrapper).toMatchSnapshot();

    const addButton = wrapper.findWhere(
      (element) => element.prop('id') === 'add-test-user'
    );

    expect(addButton.exists()).toBeTruthy();

    act(() => {
      addButton.prop('onClick')();
    });

    expect(toggleModal).not.toBeCalled();

    wrapper = wrapper.update().find('Form');

    expect(wrapper).toMatchSnapshot();

    const userIdFG = findFormGroup(wrapper, 'userId');
    const userId = findFormInput(wrapper, 'userId');

    expect(userIdFG.prop('validated')).toStrictEqual('error');
    expect(userIdFG.prop('helperTextInvalid')).toStrictEqual(
      'User Id cannot be empty.'
    );
    expect(userId.prop('validated')).toStrictEqual('error');

    const groupsFG = findFormGroup(wrapper, 'groups');
    const groups = findFormInput(wrapper, 'groups');

    expect(groupsFG.prop('validated')).toStrictEqual('error');
    expect(groupsFG.prop('helperTextInvalid')).toStrictEqual(
      'User groups cannot be empty.'
    );
    expect(groups.prop('validated')).toStrictEqual('error');
  });

  it('Add test with successful validation', () => {
    const toggleModal = jest.fn();

    let wrapper = mount(
      <AddTestUser isOpen={true} toggleModal={toggleModal} />
    ).find('Form');

    expect(wrapper).toMatchSnapshot();

    let userId = findFormInput(wrapper, 'userId');
    let groups = findFormInput(wrapper, 'groups');

    act(() => {
      userId.prop('onChange')('userId');
      groups.prop('onChange')('group1,group2');
    });

    wrapper = wrapper.update().find('Form');
    expect(wrapper).toMatchSnapshot();

    const userIdFG = findFormGroup(wrapper, 'userId');
    userId = findFormInput(wrapper, 'userId');

    expect(userIdFG.prop('validated')).toStrictEqual('success');
    expect(userIdFG.prop('helperTextInvalid')).toBeNull();
    expect(userId.prop('validated')).toStrictEqual('success');

    const groupsFG = findFormGroup(wrapper, 'groups');
    groups = findFormInput(wrapper, 'groups');

    expect(groupsFG.prop('validated')).toStrictEqual('success');
    expect(groupsFG.prop('helperTextInvalid')).toBeNull();
    expect(groups.prop('validated')).toStrictEqual('success');

    const addButton = wrapper.findWhere(
      (element) => element.prop('id') === 'add-test-user'
    );

    act(() => {
      addButton.prop('onClick')();
    });

    expect(toggleModal).toBeCalled();
  });

  it('Test userId field validations', () => {
    const toggleModal = jest.fn();

    let wrapper = mount(
      <AddTestUser isOpen={true} toggleModal={toggleModal} />
    ).find('Form');

    expect(wrapper).toMatchSnapshot();

    let userId = findFormInput(wrapper, 'userId');

    act(() => {
      userId.prop('onChange')('      ');
    });

    wrapper = wrapper.update().find('Form');
    expect(wrapper).toMatchSnapshot();

    let userIdFG = findFormGroup(wrapper, 'userId');
    userId = findFormInput(wrapper, 'userId');

    expect(userIdFG.prop('validated')).toStrictEqual('error');
    expect(userIdFG.prop('helperTextInvalid')).toStrictEqual(
      'User Id cannot be empty.'
    );
    expect(userId.prop('validated')).toStrictEqual('error');

    act(() => {
      userId.prop('onChange')('mary');
    });

    wrapper = wrapper.update().find('Form');
    expect(wrapper).toMatchSnapshot();

    userIdFG = findFormGroup(wrapper, 'userId');
    userId = findFormInput(wrapper, 'userId');

    expect(userIdFG.prop('validated')).toStrictEqual('error');
    expect(userIdFG.prop('helperTextInvalid')).toStrictEqual(
      "Already exists a system user identified by 'mary'. Please choose another user id."
    );
    expect(userId.prop('validated')).toStrictEqual('error');

    act(() => {
      userId.prop('onChange')('jsnow');
    });

    wrapper = wrapper.update().find('Form');
    expect(wrapper).toMatchSnapshot();

    userIdFG = findFormGroup(wrapper, 'userId');
    userId = findFormInput(wrapper, 'userId');

    expect(userIdFG.prop('validated')).toStrictEqual('success');
    expect(userIdFG.prop('helperTextInvalid')).toBeNull();
    expect(userId.prop('validated')).toStrictEqual('success');

    const testUserSystem = testKogitoAppContext.userContext as TestUserContext;
    testUserSystem.getUserManager().addUser('test', []);

    act(() => {
      userId.prop('onChange')('test');
    });

    wrapper = wrapper.update().find('Form');
    expect(wrapper).toMatchSnapshot();

    userIdFG = findFormGroup(wrapper, 'userId');
    userId = findFormInput(wrapper, 'userId');

    expect(userIdFG.prop('validated')).toStrictEqual('warning');
    expect(userIdFG.prop('helperTextInvalid')).toStrictEqual(
      "Already exists a user identified by 'test'. Press 'Add' to replace it."
    );
    expect(userId.prop('validated')).toStrictEqual('warning');
  });

  it('Test groups field validations', () => {
    const toggleModal = jest.fn();

    let wrapper = mount(
      <AddTestUser isOpen={true} toggleModal={toggleModal} />
    ).find('Form');

    expect(wrapper).toMatchSnapshot();

    let groups = findFormInput(wrapper, 'groups');

    act(() => {
      groups.prop('onChange')('');
    });

    wrapper = wrapper.update().find('Form');

    expect(wrapper).toMatchSnapshot();

    let groupsFG = findFormGroup(wrapper, 'groups');
    groups = findFormInput(wrapper, 'groups');

    expect(groupsFG.prop('validated')).toStrictEqual('error');
    expect(groupsFG.prop('helperTextInvalid')).toStrictEqual(
      'User groups cannot be empty.'
    );
    expect(groups.prop('validated')).toStrictEqual('error');

    act(() => {
      groups.prop('onChange')('  ,');
    });

    wrapper = wrapper.update().find('Form');

    expect(wrapper).toMatchSnapshot();

    groupsFG = findFormGroup(wrapper, 'groups');
    groups = findFormInput(wrapper, 'groups');

    expect(groupsFG.prop('validated')).toStrictEqual('error');
    expect(groupsFG.prop('helperTextInvalid')).toStrictEqual(
      'User groups cannot be empty.'
    );
    expect(groups.prop('validated')).toStrictEqual('error');

    act(() => {
      groups.prop('onChange')('admin,manager');
    });

    wrapper = wrapper.update().find('Form');

    expect(wrapper).toMatchSnapshot();

    groupsFG = findFormGroup(wrapper, 'groups');
    groups = findFormInput(wrapper, 'groups');

    expect(groupsFG.prop('validated')).toStrictEqual('success');
    expect(groupsFG.prop('helperTextInvalid')).toBeNull();
    expect(groups.prop('validated')).toStrictEqual('success');
  });
});
