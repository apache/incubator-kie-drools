import axios from 'axios';

export interface UserContext {
  userName: string;
  roles: string[];
  token: string;
}

export const isAuthEnabled = (): boolean => {
  // @ts-ignore
  return window.KOGITO_AUTH_ENABLED;
};


let currentSecurityContext;
export const getLoadedSecurityContext = (): UserContext => {
  if (!currentSecurityContext) {
    return {
      userName: 'Anonymous',
      roles: [],
      token: ''
    };
  }
  return currentSecurityContext;
}

export const loadSecurityContext = async (
  onloadSuccess: () => void
) => {
  if (isAuthEnabled()) {
      try {
        const response = await axios.get(`/api/user/me`, {
          headers: {'Access-Control-Allow-Origin': '*'}
        });
        currentSecurityContext = response.data;
        onloadSuccess();
      } catch (error) {
        currentSecurityContext = {
          userName: error.message,
          roles: [],
          token: ''
        };
      }
  } else {
    currentSecurityContext = {
      userName: 'Anonymous',
      roles: [],
      token: ''
    };
    onloadSuccess();
  }
};

export const getUserName = (): string => {
  return getLoadedSecurityContext().userName;
};

export const getToken = (): string => {
  return getLoadedSecurityContext().token;
};

export const getRoles = (): string[] => {
  return getLoadedSecurityContext().roles;
};

export const appRenderWithAxiosInterceptorConfig = (
  appRender: () => void
): void => {
  loadSecurityContext(() => {
        appRender();
      });
  if (isAuthEnabled()) {
    axios.interceptors.response.use(response => response,
      (error) => {
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
      });
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
}

export const handleLogout = (): void => {
   currentSecurityContext = undefined;
   window.location.replace(`/logout`);
};
