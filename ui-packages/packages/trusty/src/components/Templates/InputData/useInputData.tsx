import { useEffect, useState } from 'react';
import { ItemObject, RemoteData, RemoteDataStatus } from '../../../types';
import { AxiosRequestConfig } from 'axios';
import { EXECUTIONS_PATH, httpClient } from '../../../utils/api/httpClient';

const useInputData = (executionId: string) => {
  const [inputData, setInputData] = useState<RemoteData<Error, ItemObject[]>>({
    status: RemoteDataStatus.NOT_ASKED
  });

  useEffect(() => {
    let isMounted = true;
    const config: AxiosRequestConfig = {
      url: `${EXECUTIONS_PATH}/decisions/${executionId}/structuredInputs`,
      method: 'get'
    };
    setInputData({ status: RemoteDataStatus.LOADING });
    httpClient(config)
      .then(response => {
        if (isMounted) {
          setInputData({
            status: RemoteDataStatus.SUCCESS,
            data: response.data.inputs
          });
        }
      })
      .catch(error => {
        setInputData({ status: RemoteDataStatus.FAILURE, error });
      });
    return () => {
      isMounted = false;
    };
  }, [executionId]);

  return inputData;
};

export default useInputData;
