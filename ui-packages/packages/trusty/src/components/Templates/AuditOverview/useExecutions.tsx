import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  EXECUTIONS_PATH,
  callOnceHandler
} from '../../../utils/api/httpClient';
import { RemoteData, Executions } from '../../../types';
import axios, { AxiosRequestConfig } from 'axios';

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
    status: 'NOT_ASKED'
  });

  const getExecutions = useMemo(() => callOnceHandler(), []);

  const loadExecutions = useCallback(() => {
    let isMounted = true;
    setExecutions({ status: 'LOADING' });

    const config: AxiosRequestConfig = {
      url: EXECUTIONS_PATH,
      method: 'get',
      params: { search: searchString, from, to, limit, offset }
    };

    getExecutions(config)
      .then(response => {
        if (isMounted) {
          setExecutions({ status: 'SUCCESS', data: response.data });
        }
      })
      .catch(error => {
        if (!axios.isCancel(error)) {
          setExecutions({ status: 'FAILURE', error });
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
