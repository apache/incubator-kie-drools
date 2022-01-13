import axios, { AxiosRequestConfig, CancelTokenSource } from 'axios';

declare global {
  interface Window {
    TRUSTY_ENDPOINT: string;
  }
}

export const httpClient = axios.create({
  timeout: 5000,
  headers: {}
});

export const EXECUTIONS_PATH = '/executions';

export const callOnceHandler = () => {
  let caller: CancelTokenSource;

  return (config: AxiosRequestConfig) => {
    if (caller) {
      caller.cancel('Request superseded');
    }
    caller = axios.CancelToken.source();

    config.cancelToken = caller.token;
    return httpClient(config);
  };
};
