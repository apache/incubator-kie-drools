import { useEffect, useState } from 'react';
import { RemoteData, Saliencies } from '../../../types';
import { AxiosRequestConfig } from 'axios';
import { EXECUTIONS_PATH, httpClient } from '../../../utils/api/httpClient';

const useSaliencies = (executionId: string) => {
  const [saliencies, setSaliencies] = useState<RemoteData<Error, Saliencies>>({
    status: 'NOT_ASKED'
  });

  useEffect(() => {
    let isMounted = true;
    const config: AxiosRequestConfig = {
      url: `${EXECUTIONS_PATH}/decisions/${executionId}/explanations/saliencies`,
      method: 'get'
    };

    setSaliencies({ status: 'LOADING' });
    httpClient(config)
      .then(response => {
        if (isMounted) {
          setSaliencies({ status: 'SUCCESS', data: response.data });
        }
      })
      .catch(error => {
        setSaliencies({ status: 'FAILURE', error });
      });
    return () => {
      isMounted = false;
    };
  }, [executionId]);

  return saliencies;
};

export default useSaliencies;
