import * as KeycloakClient from '../KeycloakClient';
import axios from 'axios';
import { TestUserContextImpl } from '../../environment/auth/TestUserContext';
import { KeycloakUserContext } from '../../environment/auth/KeycloakUserContext';

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

  it('Test isAuthEnabled with processEnv function', () => {
    // @ts-ignore
    window.KOGITO_AUTH_ENABLED = 'true';
    expect(KeycloakClient.isAuthEnabled()).toEqual(true);
  });

  it('Test getLoadedSecurityContext - without auth', () => {
    isAuthEnabledMock.mockReturnValue(false);

    KeycloakClient.loadSecurityContext(() => {
      const context = KeycloakClient.getLoadedSecurityContext();

      expect(context).toBeInstanceOf(TestUserContextImpl);
      expect(context.getCurrentUser().id).toEqual('john');
      expect(context.getCurrentUser().groups).toHaveLength(1);
      expect(context.getCurrentUser().groups).toContain('employees');
    });
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

  it('Test getLoadedSecurityContext - with auth', () => {
    const getMock = jest.spyOn(axios, 'get');
    getMock.mockResolvedValue({ data: mockUserContext });

    KeycloakClient.loadSecurityContext(() => {
      expect(KeycloakClient.getLoadedSecurityContext()).toBeInstanceOf(
        KeycloakUserContext
      );

      const context = KeycloakClient.getLoadedSecurityContext() as KeycloakUserContext;
      expect(context.getCurrentUser().id).toEqual('jdoe');
      expect(context.getCurrentUser().groups).toHaveLength(1);
      expect(context.getCurrentUser().groups).toContain('role1');
      expect(context.getToken()).toEqual('testToken');
    });
  });

  it('Test handleLogout function', () => {
    KeycloakClient.handleLogout();
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
          status: 401,
          config: 'http://originalRequest'
        }
      })
    ).rejects.toMatchObject({
      status: 401,
      config: 'http://originalRequest'
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
