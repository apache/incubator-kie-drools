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

import axios from 'axios';
import {
  ANONYMOUS_USER,
  User,
  UserContext,
  KeycloakUserContext
} from '../environment/auth';

export const isAuthEnabled = (): boolean => {
  // @ts-ignore
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
  return {
    getCurrentUser(): User {
      return ANONYMOUS_USER;
    }
  };
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
      response => response,
      error => {
        if (error.response.status === 401) {
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
      config => {
        if (currentSecurityContext) {
          const t = getToken();
          /* tslint:disable:no-string-literal */
          config.headers['Authorization'] = 'Bearer ' + t;
          /* tslint:enable:no-string-literal */
          return config;
        }
      },
      error => {
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
