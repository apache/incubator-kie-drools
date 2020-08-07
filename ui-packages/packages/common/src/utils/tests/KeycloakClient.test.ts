import * as KeycloakClient from '../KeycloakClient';
import Keycloak from 'keycloak-js';
import axios from 'axios';

jest.mock('keycloak-js');
const mockedKeycloak = Keycloak as jest.Mocked<typeof Keycloak>;

const MockKeycloakInstance = jest.fn(() => ({
  init: KeycloakPromiseMock,
  logout: jest.fn(),
  tokenParsed: {
    preferred_username: 'jdoe'
  },
  token: 'testToken',
  clearToken: jest.fn(),
  updateToken: jest.fn()
}));

const KeycloakPromiseMock = jest.fn(() => ({
  success: jest.fn()
}));

describe('Tests for keycloak client functions', () => {
  const instance = new MockKeycloakInstance();
  const getKeycloakInstanceMock = jest.spyOn(
    KeycloakClient,
    'getKeycloakInstance'
  );
  // @ts-ignore
  getKeycloakInstanceMock.mockReturnValue(instance);

  it('Test isAuthEnabled with processEnv function', () => {
    process.env.KOGITO_AUTH_ENABLED = 'true';
    expect(KeycloakClient.isAuthEnabled()).toEqual('true');

    process.env.KOGITO_AUTH_ENABLED = 'false';
    expect(KeycloakClient.isAuthEnabled()).toEqual('false');

    // @ts-ignore
    window.KOGITO_AUTH_ENABLED = 'true';
    expect(KeycloakClient.isAuthEnabled()).toEqual('true');
  });

  it('Test getUserName function', () => {
    const isAuthEnabledMock = jest.spyOn(KeycloakClient, 'isAuthEnabled');
    isAuthEnabledMock.mockReturnValue(true);
    expect(KeycloakClient.getUserName()).toEqual('jdoe');

    isAuthEnabledMock.mockReturnValue(false);
    expect(KeycloakClient.getUserName()).toEqual('Anonymous');
  });

  it('Test getToken function', () => {
    expect(KeycloakClient.getToken()).toEqual('testToken');
  });

  it('Test handleLogout function', () => {
    KeycloakClient.handleLogout();
    expect(instance.logout.mock.calls.length).toBe(1);
  });

  it('Test appRenderWithoutAuthenticationEnabled function', () => {
    const isAuthEnabledMock = jest.spyOn(KeycloakClient, 'isAuthEnabled');
    isAuthEnabledMock.mockReturnValue(false);
    const renderMock = jest.fn();
    KeycloakClient.appRenderWithAxiosInterceptorConfig(renderMock);
    expect(renderMock).toBeCalled();
  });

  it('Test appRenderWithAuthenticationEnabled function', () => {
    const isAuthEnabledMock = jest.spyOn(KeycloakClient, 'isAuthEnabled');
    const getTokenMock = jest.spyOn(KeycloakClient, 'getToken');
    const renderMock = jest.fn();
    const mockInitPromise = new KeycloakPromiseMock();
    instance.init.mockReturnValue(mockInitPromise);
    const mockUpdateTokenPromise = new KeycloakPromiseMock();
    instance.updateToken.mockReturnValue(mockUpdateTokenPromise);

    // @ts-ignore
    mockedKeycloak.mockReturnValueOnce(instance);

    isAuthEnabledMock.mockReturnValue(true);
    getTokenMock.mockReturnValue('testToken');
    KeycloakClient.appRenderWithAxiosInterceptorConfig(renderMock);
    expect(mockInitPromise.success.mock.calls.length).toBe(1);

    const successCallback = mockInitPromise.success.mock.calls[0][0];
    successCallback(false);
    expect(renderMock).not.toBeCalled();
    successCallback(true);
    expect(renderMock).toBeCalled();

    // @ts-ignore
    // tslint:disable-next-line:no-floating-promises
    expect( axios.interceptors.response.handlers[0].rejected({
        response: {
          status: 401,
          config: 'http://originalRequest'
        }
      })
    ).rejects.toMatchObject({
      status: 401,
      config: 'http://originalRequest'
    });

    // @ts-ignore
    expect( axios.interceptors.request.handlers[0].fulfilled(
      { headers: { Authorization: 'Bearer No token' } })
    ).toMatchObject({
      headers: { Authorization: 'Bearer testToken' }
    });
    expect(getTokenMock.mock.calls.length).toBe(1);
  });
});