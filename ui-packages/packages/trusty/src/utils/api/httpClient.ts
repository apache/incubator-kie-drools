import axios, { AxiosRequestConfig, CancelTokenSource } from 'axios';

export const httpClient = axios.create({
  // @ts-ignore
  baseURL: window.TRUSTY_ENDPOINT || process.env.KOGITO_TRUSTY_API_HTTP_URL,
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
