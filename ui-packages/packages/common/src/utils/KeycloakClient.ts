import axios from 'axios';
import { TestUserContextImpl } from '../environment/auth/TestUserContext';
import { KeycloakUserContext } from '../environment/auth/KeycloakUserContext';
import { isTestUserSystemEnabled } from './Utils';
import { ANONYMOUS_USER, User, UserContext } from '../environment/auth/Auth';

declare global {
  interface Window {
    KOGITO_AUTH_ENABLED: boolean;
  }
}

export const isAuthEnabled = (): boolean => {
  return window.KOGITO_AUTH_ENABLED;
};

let currentSecurityContext: UserContext;
export const getLoadedSecurityContext = (): UserContext => {
  if (!currentSecurityContext) {
    if (isAuthEnabled()) {
      throw Error(
        'Cannot load security context! Please reload screen and log in again.'
      );
    }
    currentSecurityContext = getNonAuthUserContext();
  }
  return currentSecurityContext;
};

export const loadSecurityContext = async (onloadSuccess: () => void) => {
  if (isAuthEnabled()) {
    try {
      const response = await axios.get(`/api/user/me`, {
        headers: { 'Access-Control-Allow-Origin': '*' }
      });
      currentSecurityContext = new KeycloakUserContext(response.data);
      onloadSuccess();
    } catch (error) {
      currentSecurityContext = new KeycloakUserContext({
        userName: error.message,
        roles: [],
        token: ''
      });
    }
  } else {
    currentSecurityContext = getNonAuthUserContext();
    onloadSuccess();
  }
};

const getNonAuthUserContext = (): UserContext => {
  if (isTestUserSystemEnabled()) {
    return new TestUserContextImpl();
  } else {
    return {
      getCurrentUser(): User {
        return ANONYMOUS_USER;
      }
    };
  }
};
export const getToken = (): string => {
  if (isAuthEnabled()) {
    const ctx = getLoadedSecurityContext() as KeycloakUserContext;
    return ctx.getToken();
  }
};

export const appRenderWithAxiosInterceptorConfig = (
  appRender: (ctx: UserContext) => void
): void => {
  loadSecurityContext(() => {
    appRender(getLoadedSecurityContext());
  });
  if (isAuthEnabled()) {
    axios.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response && error.config && error.response.status === 401) {
          loadSecurityContext(() => {
            /* tslint:disable:no-string-literal */
            axios.defaults.headers.common['Authorization'] =
              'Bearer ' + getToken();
            /* tslint:enable:no-string-literal */
            return axios(error.config);
          });
        }
        return Promise.reject(error);
      }
    );
    axios.interceptors.request.use(
      (config) => {
        if (currentSecurityContext) {
          const t = getToken();
          /* tslint:disable:no-string-literal */
          config.headers['Authorization'] = 'Bearer ' + t;
          /* tslint:enable:no-string-literal */
          return config;
        }
      },
      (error) => {
        /* tslint:disable:no-floating-promises */
        Promise.reject(error);
        /* tslint:enable:no-floating-promises */
      }
    );
  }
};

export const handleLogout = (): void => {
  currentSecurityContext = undefined;
  window.location.replace(`/logout`);
};
