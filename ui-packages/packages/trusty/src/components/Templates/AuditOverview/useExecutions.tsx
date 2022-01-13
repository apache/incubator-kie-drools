import { useCallback, useContext, useEffect, useMemo, useState } from 'react';
import {
  callOnceHandler,
  EXECUTIONS_PATH
} from '../../../utils/api/httpClient';
import { Executions, RemoteData, RemoteDataStatus } from '../../../types';
import axios, { AxiosRequestConfig } from 'axios';
import { TrustyContext } from '../TrustyApp/TrustyApp';

type useExecutionsParameters = {
  searchString: string;
  from: string;
  to: string;
  limit: number;
  offset: number;
};

const useExecutions = (parameters: useExecutionsParameters) => {
  const { searchString, from, to, limit, offset } = parameters;
  const [executions, setExecutions] = useState<RemoteData<Error, Executions>>({
    status: RemoteDataStatus.NOT_ASKED
  });

  const baseUrl = useContext(TrustyContext).config.serverRoot;

  const getExecutions = useMemo(() => callOnceHandler(), []);

  const loadExecutions = useCallback(() => {
    let isMounted = true;
    setExecutions({ status: RemoteDataStatus.LOADING });

    const config: AxiosRequestConfig = {
      baseURL: baseUrl,
      url: EXECUTIONS_PATH,
      method: 'get',
      params: { search: searchString, from, to, limit, offset }
    };

    getExecutions(config)
      .then(response => {
        if (isMounted) {
          setExecutions({
            status: RemoteDataStatus.SUCCESS,
            data: response.data
          });
        }
      })
      .catch(error => {
        if (!axios.isCancel(error)) {
          setExecutions({ status: RemoteDataStatus.FAILURE, error });
        }
      });
    return () => {
      isMounted = false;
    };
  }, [searchString, from, to, limit, offset]);

  useEffect(() => {
    loadExecutions();
  }, [searchString, from, to, limit, offset, loadExecutions]);

  return { loadExecutions, executions };
};

export default useExecutions;
