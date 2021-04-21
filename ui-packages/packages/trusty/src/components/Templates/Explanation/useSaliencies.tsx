import { useEffect, useState } from 'react';
import { RemoteData, RemoteDataStatus, Saliencies } from '../../../types';
import { AxiosRequestConfig } from 'axios';
import { EXECUTIONS_PATH, httpClient } from '../../../utils/api/httpClient';

const useSaliencies = (executionId: string) => {
  const [saliencies, setSaliencies] = useState<RemoteData<Error, Saliencies>>({
    status: RemoteDataStatus.NOT_ASKED
  });

  useEffect(() => {
    let isMounted = true;
    const config: AxiosRequestConfig = {
      url: `${EXECUTIONS_PATH}/decisions/${executionId}/explanations/saliencies`,
      method: 'get'
    };

    setSaliencies({ status: RemoteDataStatus.LOADING });
    httpClient(config)
      .then(response => {
        if (isMounted) {
          setSaliencies({
            status: RemoteDataStatus.SUCCESS,
            data: response.data
          });
        }
      })
      .catch(error => {
        setSaliencies({ status: RemoteDataStatus.FAILURE, error });
      });
    return () => {
      isMounted = false;
    };
  }, [executionId]);

  return saliencies;
};

export default useSaliencies;
