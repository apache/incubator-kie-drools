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
import Keycloak from 'keycloak-js';

describe('mocked function tests in KeycloakClient', () => {
  it('isAuthEnabled test', () => {
    process.env.KOGITO_ENV_MODE = 'PROD';
    const result = KeycloakClient.isAuthEnabled();
    expect(result).toBeTruthy();
  });
  it('check auth server health - resolved', async () => {
    const unMockedWindow = window;
    window.fetch = jest
      .fn()
      .mockImplementation(() => Promise.resolve({ ok: true, status: 200 }));
    await expect(KeycloakClient.checkAuthServerHealth()).resolves.not.toThrow();
    window = unMockedWindow;
  });
  it('check auth server health - rejected', async () => {
    const unMockedWindow = window;
    window.fetch = jest
      .fn()
      .mockImplementation(() => Promise.reject({ ok: true, status: 500 }));
    await expect(KeycloakClient.checkAuthServerHealth()).rejects.not.toThrow();
    window = unMockedWindow;
  });
  it('getKeycloakClient test', async () => {
    const result = KeycloakClient.getKeycloakClient();
    expect(result).toBeInstanceOf(Keycloak);
  });

  it('test setBearer token', () => {
    process.env.KOGITO_ENV_MODE = 'DEV';
    expect(
      KeycloakClient.setBearerToken({
        headers: 'Authorization undefined'
      })
    ).resolves.not.toThrow();
    process.env.KOGITO_ENV_MODE = 'PROD';
    KeycloakClient.setBearerToken({
      headers: 'Authorization token'
    });
  });

  it('test isKeycloakHealthCheckDisabled', () => {
    window['KOGITO_CONSOLES_KEYCLOAK_DISABLE_HEALTH_CHECK'] = true;
    expect(KeycloakClient.isKeycloakHealthCheckDisabled()).toBeTruthy();
  });

  it('test getUpdateTokenValidity', () => {
    window['KOGITO_CONSOLES_KEYCLOAK_UPDATE_TOKEN_VALIDITY'] = '30';
    expect(KeycloakClient.getUpdateTokenValidity()).toEqual(30);
    window['KOGITO_CONSOLES_KEYCLOAK_UPDATE_TOKEN_VALIDITY'] = 50;
    expect(KeycloakClient.getUpdateTokenValidity()).toEqual(50);
  });
});

describe('Tests for keycloak client functions', () => {
  const mockUserContext = {
    userName: 'jdoe',
    roles: ['role1'],
    token: 'testToken'
  };

  beforeEach(() => {
    isAuthEnabledMock.mockReturnValue(true);
    window['KOGITO_CONSOLES_KEYCLOAK_UPDATE_TOKEN_VALIDITY'] = 30;
  });

  const isAuthEnabledMock = jest.spyOn(KeycloakClient, 'isAuthEnabled');
  const getKeyCloakClientMock: any = jest.spyOn(
    KeycloakClient,
    'getKeycloakClient'
  );
  const checkAuthServerHealthMock = jest.spyOn(
    KeycloakClient,
    'checkAuthServerHealth'
  );

  const updateKeycloakTokenMock: any = jest.spyOn(
    KeycloakClient,
    'updateKeycloakToken'
  );

  const setBearerTokenMock: any = jest.spyOn(KeycloakClient, 'setBearerToken');
  describe('Wrong API usage tests', () => {
    test('getLoadedSecurityContext called before login - without auth anonymous', () => {
      isAuthEnabledMock.mockReturnValue(false);

      const context = KeycloakClient.getLoadedSecurityContext();

      expect(context.getCurrentUser()).toBe(ANONYMOUS_USER);
    });
  });

  it('Test getLoadedSecurityContext - without auth anonymous', async () => {
    isAuthEnabledMock.mockReturnValue(false);

    // eslint-disable-next-line
    await KeycloakClient.loadSecurityContext(
      () => {
        // renders the app
      },
      () => {
        // renders nothing
      }
    );

    const context = KeycloakClient.getLoadedSecurityContext();

    expect(context.getCurrentUser()).toBe(ANONYMOUS_USER);
  });

  it('keycloak with health check - success', () => {
    isAuthEnabledMock.mockReturnValue(true);
    window['KOGITO_CONSOLES_KEYCLOAK_DISABLE_HEALTH_CHECK'] = true;
    getKeyCloakClientMock.mockReturnValue({
      init: () => new Promise((resolve, reject) => resolve(true)),
      logout: () => {
        //logs out the user
      }
    });
    // @ts-ignore
    const mockInitializeKeycloak = jest.spyOn(
      KeycloakClient,
      'initializeKeycloak'
    );
    const success = jest.fn();
    const failure = jest.fn();
    KeycloakClient.loadSecurityContext(success, failure);
    expect(mockInitializeKeycloak).toHaveBeenCalledWith(success);
    mockInitializeKeycloak.mockClear();
  });

  it('keycloak with health check - fails', () => {
    isAuthEnabledMock.mockReturnValue(true);
    window['KOGITO_CONSOLES_KEYCLOAK_DISABLE_HEALTH_CHECK'] = false;
    getKeyCloakClientMock.mockReturnValue({
      init: () => new Promise((resolve, reject) => resolve(true)),
      logout: () => {
        //logs out the user
      }
    });
    checkAuthServerHealthMock.mockRejectedValue({});
    // @ts-ignore
    const mockInitializeKeycloak = jest.spyOn(
      KeycloakClient,
      'initializeKeycloak'
    );
    const success = jest.fn();
    const failure = jest.fn();
    KeycloakClient.loadSecurityContext(success, failure);
    expect(checkAuthServerHealthMock).rejects.not.toThrow();
    mockInitializeKeycloak.mockClear();
  });
  it('keycloak with no health check', () => {
    isAuthEnabledMock.mockReturnValue(true);
    window['KOGITO_CONSOLES_KEYCLOAK_DISABLE_HEALTH_CHECK'] = false;
    getKeyCloakClientMock.mockReturnValue({
      init: () => new Promise((resolve, reject) => resolve(true)),
      logout: () => {
        // logs out the user
      }
    });
    checkAuthServerHealthMock.mockResolvedValue({});
    const success = jest.fn();
    const failure = jest.fn();
    KeycloakClient.loadSecurityContext(success, failure);
  });

  it('Test getLoadedSecurityContext - without auth test user system enabled', async () => {
    isAuthEnabledMock.mockReturnValue(false);
    await KeycloakClient.loadSecurityContext(
      () => {
        //success callback
      },
      () => {
        // failure callback
      }
    );
    const context = KeycloakClient.getLoadedSecurityContext();
    expect(context.getCurrentUser().id).toEqual('Dev User');
    expect(context.getCurrentUser().groups).toHaveLength(0);
  });

  it('Test getLoadedSecurityContext - with auth Not logged', () => {
    KeycloakClient.loadSecurityContext(
      () => {
        expect(() => {
          KeycloakClient.getLoadedSecurityContext();
        }).toThrowError(
          'Cannot load security context! Please reload screen and log in again.'
        );
      },
      () => {
        // do nothing
      }
    );
  });

  it('Test getLoadedSecurityContext - with auth', async () => {
    const getMock = jest.spyOn(axios, 'get');
    getMock.mockResolvedValue({ data: mockUserContext });

    // eslint-disable-next-line
    await KeycloakClient.loadSecurityContext(
      () => {
        // success callback
      },
      () => {
        //failure callback
      }
    );
    expect(KeycloakClient.getLoadedSecurityContext()).toHaveProperty(
      'getCurrentUser'
    );
    const context = KeycloakClient.getLoadedSecurityContext() as KeycloakUserContext;
    expect(context.getCurrentUser().id).toEqual('Dev User');
    expect(context.getCurrentUser().groups).toHaveLength(0);
    expect(context.getCurrentUser().groups).toEqual([]);
  });

  it('Test handleLogout function', () => {
    KeycloakClient.handleLogout();
  });

  it('Test appRenderWithoutAuthenticationEnabled function', () => {
    isAuthEnabledMock.mockReturnValue(false);
    const renderMock = jest.fn();
    KeycloakClient.appRenderWithAxiosInterceptorConfig(renderMock, () => {
      // renders error
    });
    expect(renderMock).toBeCalled();
  });

  it('Test appRenderWithQuarkusAuthenticationEnabled function', async () => {
    const renderSuccessMock = jest.fn();
    const renderFailureMock = jest.fn();

    const getTokenMock = jest.spyOn(KeycloakClient, 'getToken');
    getTokenMock.mockReturnValue('testToken');
    updateKeycloakTokenMock.mockResolvedValue({});
    setBearerTokenMock.mockResolvedValue({
      headers: { Authorization: 'Bearer testToken' }
    });
    KeycloakClient.appRenderWithAxiosInterceptorConfig(
      renderSuccessMock,
      renderFailureMock
    );
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
      await axios.interceptors.request.handlers[0].fulfilled({
        headers: { Authorization: 'Bearer testToken' }
      })
    ).toMatchObject({
      headers: { Authorization: 'Bearer testToken' }
    });
    expect(setBearerTokenMock.mock.calls.length).toBe(3);
  });
});
