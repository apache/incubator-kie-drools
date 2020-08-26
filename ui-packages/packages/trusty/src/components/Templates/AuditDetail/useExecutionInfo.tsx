import { useEffect, useState } from 'react';
import { RemoteData, Execution } from '../../../types';
import { AxiosRequestConfig } from 'axios';
import { EXECUTIONS_PATH, httpClient } from '../../../utils/api/httpClient';

const useExecutionInfo = (executionId: string) => {
  const [execution, setExecution] = useState<RemoteData<Error, Execution>>({
    status: 'NOT_ASKED'
  });

  useEffect(() => {
    let isMounted = true;
    const config: AxiosRequestConfig = {
      url: `${EXECUTIONS_PATH}/decisions/${executionId}`,
      method: 'get'
    };
    setExecution({ status: 'LOADING' });
    httpClient(config)
      .then(response => {
        if (isMounted) {
          setExecution({ status: 'SUCCESS', data: response.data });
        }
      })
      .catch(error => {
        setExecution({ status: 'FAILURE', error });
      });
    return () => {
      isMounted = false;
    };
  }, [executionId]);

  return execution;
};

export default useExecutionInfo;
