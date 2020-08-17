import axios, { AxiosRequestConfig, CancelTokenSource } from 'axios';

const httpClient = axios.create({
  baseURL: process.env.KOGITO_TRUSTY_API_HTTP_URL,
  timeout: 5000,
  headers: {}
});

let call: CancelTokenSource;

const callOnce = (config: AxiosRequestConfig) => {
  if (call) {
    call.cancel('Request superseded');
  }
  call = axios.CancelToken.source();

  config.cancelToken = call.token;
  return httpClient(config);
};

const isCancelledRequest = axios.isCancel;

export { httpClient, callOnce, isCancelledRequest };
