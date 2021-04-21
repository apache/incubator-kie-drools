import { useEffect, useState } from 'react';
import { ModelData, RemoteData, RemoteDataStatus } from '../../../types';
import { AxiosRequestConfig } from 'axios';
import { EXECUTIONS_PATH, httpClient } from '../../../utils/api/httpClient';

const useModelData = (executionId: string) => {
  const [modelData, setModelData] = useState<RemoteData<Error, ModelData>>({
    status: RemoteDataStatus.NOT_ASKED
  });

  useEffect(() => {
    let isMounted = true;
    const config: AxiosRequestConfig = {
      url: `${EXECUTIONS_PATH}/${executionId}/model`,
      method: 'get'
    };
    setModelData({ status: RemoteDataStatus.LOADING });
    httpClient(config)
      .then(response => {
        if (isMounted) {
          setModelData({
            status: RemoteDataStatus.SUCCESS,
            data: response.data
          });
        }
      })
      .catch(error => {
        setModelData({ status: RemoteDataStatus.FAILURE, error });
      });
    return () => {
      isMounted = false;
    };
  }, [executionId]);

  return modelData;
};

export default useModelData;
