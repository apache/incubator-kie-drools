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

import { KeycloakUserContext } from '../KeycloakUserContext';
import * as Keycloak from '../../../utils/KeycloakClient';

const isAuthEnabled = jest.spyOn(Keycloak, 'isAuthEnabled');
const handleLogout = jest.spyOn(Keycloak, 'handleLogout');

const keycloakInfo = {
  userName: 'jdoe',
  roles: ['user', 'manager'],
  token: 'token',
  tokenMinValidity: 30,
  logout: () => Keycloak.handleLogout()
};

const prepareMock = (keycloakEnabled: boolean) => {
  isAuthEnabled.mockReturnValue(keycloakEnabled);
  handleLogout.mockReturnValue();
};

describe('KeycloakUserSystem tests', () => {
  it('KeycloakUserSystem basic testing ', () => {
    prepareMock(true);

    const userSystem: KeycloakUserContext = new KeycloakUserContext(
      keycloakInfo
    );
    expect(userSystem).not.toBeNull();

    const user = userSystem.getCurrentUser();

    expect(user).not.toBeNull();
    expect(user.id).toStrictEqual('jdoe');
    expect(user.groups).toHaveLength(2);
    expect(user.groups).toContainEqual('user');
    expect(user.groups).toContainEqual('manager');

    const token = userSystem.getToken();

    expect(token).toEqual('token');

    const getTokenMinValidity = userSystem.getTokenMinValidity();

    expect(getTokenMinValidity).toEqual(30);

    userSystem.logout();

    expect(handleLogout.mock.calls).toHaveLength(1);
  });
});
