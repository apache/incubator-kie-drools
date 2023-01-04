import * as KeycloakClient from '../KeycloakClient';
import * as Utils from '../Utils';
import axios from 'axios';
import { TestUserContextImpl } from '../../environment/auth/TestUserContext';
import { KeycloakUserContext } from '../../environment/auth/KeycloakUserContext';
import { ANONYMOUS_USER } from '../../environment/auth/Auth';

declare global {
  interface Window {
    KOGITO_AUTH_ENABLED: boolean;
  }
}

const isAuthEnabledMock = jest.spyOn(KeycloakClient, 'isAuthEnabled');
const isTestUserSystemEnabledMock = jest.spyOn(
  Utils,
  'isTestUserSystemEnabled'
);

describe('Tests for keycloak client functions', () => {
  const mockUserContext = {
    userName: 'jdoe',
    roles: ['role1'],
    token: 'testToken'
  };

  beforeEach(() => {
    isAuthEnabledMock.mockReturnValue(true);
    isTestUserSystemEnabledMock.mockReturnValue(false);
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

      expect(context).not.toBeInstanceOf(TestUserContextImpl);
      expect(context.getCurrentUser()).toBe(ANONYMOUS_USER);
    });
  });

  it('Test isAuthEnabled with processEnv function', () => {
    window.KOGITO_AUTH_ENABLED = true;
    expect(KeycloakClient.isAuthEnabled()).toEqual(true);
  });

  it('Test getLoadedSecurityContext - without auth anonymous', async () => {
    isAuthEnabledMock.mockReturnValue(false);

    // eslint-disable-next-line
    await KeycloakClient.loadSecurityContext(() => {});

    const context = KeycloakClient.getLoadedSecurityContext();

    expect(context).not.toBeInstanceOf(TestUserContextImpl);
    expect(context.getCurrentUser()).toBe(ANONYMOUS_USER);
  });

  it('Test getLoadedSecurityContext - without auth test user system enabled', async () => {
    isAuthEnabledMock.mockReturnValue(false);
    isTestUserSystemEnabledMock.mockReturnValue(true);

    // eslint-disable-next-line
    await KeycloakClient.loadSecurityContext(() => {});

    const context = KeycloakClient.getLoadedSecurityContext();

    expect(context).toBeInstanceOf(TestUserContextImpl);
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

    const context =
      KeycloakClient.getLoadedSecurityContext() as KeycloakUserContext;
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
      (axios.interceptors.response as any).handlers[0].rejected({
        response: {
          status: 401,
          config: 'http://originalRequest'
        }
      })
    ).rejects.toMatchObject({
      response: {
        status: 401,
        config: 'http://originalRequest'
      }
    });

    expect(
      (axios.interceptors.request as any).handlers[0].fulfilled({
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
