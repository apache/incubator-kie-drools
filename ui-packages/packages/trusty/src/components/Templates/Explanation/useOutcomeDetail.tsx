import { useEffect, useState } from 'react';
import { RemoteData, ItemObject } from '../../../types';
import { AxiosRequestConfig } from 'axios';
import { EXECUTIONS_PATH, httpClient } from '../../../utils/api/httpClient';

const useOutcomeDetail = (executionId: string, outcomeId: string | null) => {
  const [outcomeDetail, setOutcomeDetail] = useState<
    RemoteData<Error, ItemObject[]>
  >({
    status: 'NOT_ASKED'
  });

  useEffect(() => {
    let isMounted = true;
    if (executionId && outcomeId) {
      const config: AxiosRequestConfig = {
        url: `${EXECUTIONS_PATH}/decisions/${executionId}/outcomes/${outcomeId}`,
        method: 'get'
      };
      setOutcomeDetail({ status: 'LOADING' });
      httpClient(config)
        .then(response => {
          if (isMounted) {
            setOutcomeDetail({
              status: 'SUCCESS',
              data: response.data.outcomeInputs
            });
          }
        })
        .catch(error => {
          setOutcomeDetail({ status: 'FAILURE', error });
        });
    }
    return () => {
      isMounted = false;
    };
  }, [executionId, outcomeId]);

  return outcomeDetail;
};

export default useOutcomeDetail;
