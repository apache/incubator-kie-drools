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

import * as KeycloakClient from '../KeycloakClient';
import axios from 'axios';
import { ANONYMOUS_USER, KeycloakUserContext } from '../../environment/auth';

const isAuthEnabledMock = jest.spyOn(KeycloakClient, 'isAuthEnabled');

describe('Tests for keycloak client functions', () => {
  const mockUserContext = {
    userName: 'jdoe',
    roles: ['role1'],
    token: 'testToken'
  };

  beforeEach(() => {
    isAuthEnabledMock.mockReturnValue(true);
  });

  describe('Wrong API usage tests', () => {
    test('getLoadedSecurityContext called before login - with auth', () => {
      expect(() => KeycloakClient.getLoadedSecurityContext()).toThrowError(
        'Cannot load security context! Please reload screen and log in again.'
      );
    });

    test('getLoadedSecurityContext called before login - without auth anonymous', () => {
      isAuthEnabledMock.mockReturnValue(false);

      const context = KeycloakClient.getLoadedSecurityContext();

      expect(context.getCurrentUser()).toBe(ANONYMOUS_USER);
    });
  });

  it('Test isAuthEnabled with processEnv function', () => {
    // @ts-ignore
    window.KOGITO_AUTH_ENABLED = 'true';
    expect(KeycloakClient.isAuthEnabled()).toEqual(true);
  });

  it('Test getLoadedSecurityContext - without auth anonymous', async () => {
    isAuthEnabledMock.mockReturnValue(false);

    // eslint-disable-next-line
    await KeycloakClient.loadSecurityContext(() => {});

    const context = KeycloakClient.getLoadedSecurityContext();

    expect(context.getCurrentUser()).toBe(ANONYMOUS_USER);
  });

  it('Test getLoadedSecurityContext - without auth test user system enabled', async () => {
    isAuthEnabledMock.mockReturnValue(false);

    // eslint-disable-next-line
    await KeycloakClient.loadSecurityContext(() => {});

    const context = KeycloakClient.getLoadedSecurityContext();

    expect(context.getCurrentUser().id).toEqual('john');
    expect(context.getCurrentUser().groups).toHaveLength(1);
    expect(context.getCurrentUser().groups).toContain('employees');
  });

  it('Test getLoadedSecurityContext - with auth Not logged', () => {
    KeycloakClient.loadSecurityContext(() => {
      expect(() => {
        KeycloakClient.getLoadedSecurityContext();
      }).toThrowError(
        'Cannot load security context! Please reload screen and log in again.'
      );
    });
  });

  it('Test getLoadedSecurityContext - with auth', async () => {
    const getMock = jest.spyOn(axios, 'get');
    getMock.mockResolvedValue({ data: mockUserContext });

    // eslint-disable-next-line
    await KeycloakClient.loadSecurityContext(() => {});

    expect(KeycloakClient.getLoadedSecurityContext()).toBeInstanceOf(
      KeycloakUserContext
    );

    const context = KeycloakClient.getLoadedSecurityContext() as KeycloakUserContext;
    expect(context.getCurrentUser().id).toEqual('jdoe');
    expect(context.getCurrentUser().groups).toHaveLength(1);
    expect(context.getCurrentUser().groups).toContain('role1');
    expect(context.getToken()).toEqual('testToken');
  });

  it('Test handleLogout function', () => {
    KeycloakClient.handleLogout();
  });

  it('Test getToken function', async () => {
    const getMock = jest.spyOn(axios, 'get');
    getMock.mockResolvedValue({ data: mockUserContext });

    // eslint-disable-next-line
    await KeycloakClient.loadSecurityContext(() => {});

    expect(KeycloakClient.getToken()).toEqual('testToken');
  });

  it('Test appRenderWithoutAuthenticationEnabled function', () => {
    isAuthEnabledMock.mockReturnValue(false);
    const renderMock = jest.fn();
    KeycloakClient.appRenderWithAxiosInterceptorConfig(renderMock);
    expect(renderMock).toBeCalled();
  });

  it('Test appRenderWithQuarkusAuthenticationEnabled function', () => {
    const renderMock = jest.fn();
    const getTokenMock = jest.spyOn(KeycloakClient, 'getToken');

    getTokenMock.mockReturnValue('testToken');
    KeycloakClient.appRenderWithAxiosInterceptorConfig(renderMock);

    expect(
      // @ts-ignore
      // tslint:disable-next-line:no-floating-promises
      axios.interceptors.response.handlers[0].rejected({
        response: {
          error: {
            status: 401,
            config: 'http://originalRequest'
          }
        }
      })
    ).rejects.toMatchObject({
      response: {
        error: {
          status: 401,
          config: 'http://originalRequest'
        }
      }
    });

    expect(
      // @ts-ignore
      axios.interceptors.request.handlers[0].fulfilled({
        headers: { Authorization: 'Bearer No token' }
      })
    ).toMatchObject({
      headers: { Authorization: 'Bearer testToken' }
    });
    expect(getTokenMock.mock.calls.length).toBe(1);
  });

  it('Test loadUserContext function', () => {
    const getMock = jest.spyOn(axios, 'get');
    getMock.mockResolvedValue({ data: mockUserContext });

    KeycloakClient.loadSecurityContext(() => {
      const axiosCallback = getMock.mock.calls[0];
      expect(axiosCallback[0]).toBe('/api/user/me');
    });
  });
});
