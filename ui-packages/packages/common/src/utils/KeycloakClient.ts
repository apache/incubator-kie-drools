import axios from 'axios';

import Keycloak, { KeycloakInstance } from 'keycloak-js';

export const isAuthEnabled = (): boolean => {
  // @ts-ignore
  return window.KOGITO_AUTH_ENABLED || process.env.KOGITO_AUTH_ENABLED;
};

export const createKeycloakInstance = (): KeycloakInstance => {
  // @ts-ignore
  const authKeycloakRealm = window.KOGITO_AUTH_KEYCLOAK_REALM || process.env.KOGITO_KEYCLOAK_REALM;
  // @ts-ignore
  const authKeycloakUrl = window.KOGITO_AUTH_KEYCLOAK_URL || process.env.KOGITO_KEYCLOAK_URL;
  // @ts-ignore
  const authKeycloakClientId = window.KOGITO_AUTH_KEYCLOAK_CLIENT_ID || process.env.KOGITO_KEYCLOAK_CLIENT_ID;

  return Keycloak({
    realm: authKeycloakRealm,
    url: authKeycloakUrl + '/auth',
    clientId: authKeycloakClientId
  });
};

const keycloakInstance = createKeycloakInstance();

export const getKeycloakInstance = (): KeycloakInstance => {
  return keycloakInstance;
};
export const getUserName = (): string => {
  let username = 'Anonymous';
  if (isAuthEnabled()) {
    // @ts-ignore
    username = getKeycloakInstance().tokenParsed.preferred_username;
  }
  return username;
};

export const getToken = (): string => {
  // @ts-ignore
  return getKeycloakInstance().token;
};

export const appRenderWithAxiosInterceptorConfig = (
  appRender: () => void
): void => {
  if (isAuthEnabled()) {
    getKeycloakInstance()
      .init({ onLoad: 'login-required' })
      .success(authenticated => {
        if (authenticated) {
          appRender();
        }
      });

    axios.interceptors.request.use(
      config => {
        const token = getToken();
        if (token !== undefined) {
          /* tslint:disable:no-string-literal */
          config.headers['Authorization'] = 'Bearer ' + token;
          /* tslint:enable:no-string-literal */
        }
        return config;
      },
      error => {
        /* tslint:disable:no-floating-promises */
        Promise.reject(error);
        /* tslint:enable:no-floating-promises */
      }
    );

    axios.interceptors.response.use(
      response => {
        return response;
      },
      error => {
        const originalRequest = error.config;
        if (error.response.status === 401) {
          getKeycloakInstance()
            .updateToken(5)
            .success(() => {
              /* tslint:disable:no-string-literal */
              axios.defaults.headers.common['Authorization'] =
                'Bearer ' + getToken();
              /* tslint:enable:no-string-literal */
              return axios(originalRequest);
            });
        }
        return Promise.reject(error);
      }
    );
  } else {
    appRender();
  }
};

export const handleLogout = (): void => {
  getKeycloakInstance().logout();
};
