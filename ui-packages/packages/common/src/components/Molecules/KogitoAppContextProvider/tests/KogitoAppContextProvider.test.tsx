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
import { mount } from 'enzyme';

import KogitoAppContextProvider from '../KogitoAppContextProvider';
import {
  AppContext,
  default as KogitoAppContext
} from '../../../../environment/context/KogitoAppContext';
import { TEST_USERS } from '../../../../environment/auth/TestUserManager';
import { TestUserContextImpl } from '../../../../environment/auth/TestUserContext';

const MockedComponent = (props): React.ReactElement => {
  return <></>;
};

describe('KogitoAppContextProvider tests', () => {
  it('Snapshot testing', () => {
    const wrapper = mount(
      <KogitoAppContextProvider userContext={new TestUserContextImpl()}>
        <KogitoAppContext.Consumer>
          {(ctx) => <MockedComponent context={ctx} />}
        </KogitoAppContext.Consumer>
      </KogitoAppContextProvider>
    );

    expect(wrapper).toMatchSnapshot();

    const component = wrapper.find('MockedComponent');

    expect(component.exists()).toBeTruthy();

    const context: AppContext = component.prop('context');

    expect(context).not.toBeNull();
    expect(context.userContext).toBeInstanceOf(TestUserContextImpl);
    expect(context.getCurrentUser()).toStrictEqual(TEST_USERS[0]);
  });
});
