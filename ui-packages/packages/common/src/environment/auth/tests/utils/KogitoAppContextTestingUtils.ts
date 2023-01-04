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

import * as Keycloak from '../../../../utils/KeycloakClient';
import * as Utils from '../../../../utils/Utils';
import * as KogitoAppContext from '../../../context/KogitoAppContext';
import { TestUserContextImpl } from '../../TestUserContext';
import { KeycloakUserContext } from '../../KeycloakUserContext';

export const testIsAuthEnabledMock = jest.spyOn(Keycloak, 'isAuthEnabled');
testIsAuthEnabledMock.mockReturnValue(true);

export const testHandleLogoutMock = jest.spyOn(Keycloak, 'handleLogout');
testHandleLogoutMock.mockImplementation(jest.fn());

export const testIsTestUserSystemEnabledMock = jest.spyOn(
  Utils,
  'isTestUserSystemEnabled'
);
testIsTestUserSystemEnabledMock.mockReturnValue(false);

const newContext = (authEnabled: boolean): KogitoAppContext.AppContext => {
  const testUserSystem = authEnabled
    ? new KeycloakUserContext({
        userName: 'jdoe',
        roles: ['user', 'manager'],
        token: 'token'
      })
    : new TestUserContextImpl();

  return new KogitoAppContext.AppContextImpl(testUserSystem);
};

export let testKogitoAppContext: KogitoAppContext.AppContext =
  newContext(false);

jest
  .spyOn(KogitoAppContext, 'useKogitoAppContext')
  .mockImplementation(() => testKogitoAppContext);

export const resetTestKogitoAppContext = (authEnabled: boolean) => {
  testKogitoAppContext = newContext(authEnabled);
};
