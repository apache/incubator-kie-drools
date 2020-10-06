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
import * as KogitoAppContext from '../../../context/KogitoAppContext';
import { TestUserContextImpl } from '../../TestUserContext';

export const testIsAuthEnabledMock = jest.spyOn(Keycloak, 'isAuthEnabled');
testIsAuthEnabledMock.mockReturnValue(true);

const newContext = (): KogitoAppContext.AppContext => {
  const testUserSystem = new TestUserContextImpl();

  return new KogitoAppContext.AppContextImpl(testUserSystem, {
    mode: KogitoAppContext.EnvironmentMode.TEST
  });
};

export let testKogitoAppContext: KogitoAppContext.AppContext = newContext();

jest
  .spyOn(KogitoAppContext, 'useKogitoAppContext')
  .mockImplementation(() => testKogitoAppContext);

export const setTestKogitoAppContextModeToTest = (toggle: boolean) => {
  testKogitoAppContext.environment.mode = toggle
    ? KogitoAppContext.EnvironmentMode.TEST
    : KogitoAppContext.EnvironmentMode.PROD;
};

export const resetTestKogitoAppContext = () => {
  testKogitoAppContext = newContext();
};
