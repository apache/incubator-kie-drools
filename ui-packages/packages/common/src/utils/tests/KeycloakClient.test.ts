import * as KeycloakClient from '../KeycloakClient';
import axios from 'axios';



describe('Tests for keycloak client functions', () => {
  const mockUserContext = {
    userName: 'jdoe',
    roles: ['role1'],
    token: 'testToken'
  };

  it('Test isAuthEnabled with processEnv function', () => {
    // @ts-ignore
    window.KOGITO_AUTH_ENABLED = 'true';
    expect(KeycloakClient.isAuthEnabled()).toEqual('true');
  });

  it('Test getLoadedSecurityContext function', () => {
    const isAuthEnabledMock = jest.spyOn(KeycloakClient, 'isAuthEnabled');

    isAuthEnabledMock.mockReturnValue(true);
    expect(KeycloakClient.getLoadedSecurityContext().userName).toEqual('Anonymous');
    KeycloakClient.loadSecurityContext(()=> {
      expect(KeycloakClient.getLoadedSecurityContext().userName).toEqual('jdoe');
    })
  });


  it('Test getUserName function', () => {
    const getLoadedSecurityContextMock = jest.spyOn(KeycloakClient, 'getLoadedSecurityContext');
    getLoadedSecurityContextMock.mockReturnValue(mockUserContext);

    expect(KeycloakClient.getUserName()).toEqual('jdoe');
  });

  it('Test getToken function', () => {
    const getLoadedSecurityContextMock = jest.spyOn(KeycloakClient, 'getLoadedSecurityContext');
    getLoadedSecurityContextMock.mockReturnValue(mockUserContext);

    expect(KeycloakClient.getToken()).toEqual('testToken');
  });

  it('Test getRoles function', () => {
    const getLoadedSecurityContextMock = jest.spyOn(KeycloakClient, 'getLoadedSecurityContext');
    getLoadedSecurityContextMock.mockReturnValue(mockUserContext);

    expect(KeycloakClient.getRoles()).toEqual(['role1']);
  });

  it('Test handleLogout function', () => {
    KeycloakClient.handleLogout();
  });

  it('Test appRenderWithoutAuthenticationEnabled function', () => {
    const isAuthEnabledMock = jest.spyOn(KeycloakClient, 'isAuthEnabled');
    isAuthEnabledMock.mockReturnValue(false);
    const renderMock = jest.fn();
    KeycloakClient.appRenderWithAxiosInterceptorConfig(renderMock);
    expect(renderMock).toBeCalled();
  });

  it('Test appRenderWithQuarkusAuthenticationEnabled function', () => {
    const isAuthEnabledMock = jest.spyOn(KeycloakClient, 'isAuthEnabled');
    const renderMock = jest.fn();
    const getTokenMock = jest.spyOn(KeycloakClient, 'getToken');

    getTokenMock.mockReturnValue('testToken');
    isAuthEnabledMock.mockReturnValue(true);
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
    const isAuthEnabledMock = jest.spyOn(KeycloakClient, 'isAuthEnabled');
    isAuthEnabledMock.mockReturnValue(true);

    const getMock = jest.spyOn(axios, 'get');
    getMock.mockResolvedValue({data: mockUserContext});

    KeycloakClient.loadSecurityContext(() => {
    const axiosCallback = getMock.mock.calls[0];
    expect(axiosCallback[0]).toBe("/api/user/me");
    });
   });
});