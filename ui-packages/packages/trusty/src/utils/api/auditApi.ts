import { callOnce } from './httpClient';
import { AxiosRequestConfig } from 'axios';

const EXECUTIONS_PATH = '/executions';

export const getExecutions = (
  searchString: string,
  from: string,
  to: string,
  limit: number,
  offset: number
) => {
  const config: AxiosRequestConfig = {
    url: EXECUTIONS_PATH,
    method: 'get',
    params: { search: searchString, from, to, limit, offset }
  };
  return callOnce(config);
};
