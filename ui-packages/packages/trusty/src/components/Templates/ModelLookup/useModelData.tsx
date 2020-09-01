import { useEffect, useState } from 'react';
import { RemoteData, ModelData } from '../../../types';
import { AxiosRequestConfig } from 'axios';
import { EXECUTIONS_PATH, httpClient } from '../../../utils/api/httpClient';

const useModelData = (executionId: string) => {
  const [modelData, setModelData] = useState<RemoteData<Error, ModelData>>({
    status: 'NOT_ASKED'
  });

  useEffect(() => {
    let isMounted = true;
    const config: AxiosRequestConfig = {
      url: `${EXECUTIONS_PATH}/${executionId}/model`,
      method: 'get'
    };
    setModelData({ status: 'LOADING' });
    httpClient(config)
      .then(response => {
        if (isMounted) {
          setModelData({ status: 'SUCCESS', data: response.data });
        }
      })
      .catch(error => {
        setModelData({ status: 'FAILURE', error });
      });
    return () => {
      isMounted = false;
    };
  }, [executionId]);

  return modelData;
};

export default useModelData;
