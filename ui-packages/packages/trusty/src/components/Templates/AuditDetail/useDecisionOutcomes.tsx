import { useEffect, useState } from 'react';
import { EXECUTIONS_PATH, httpClient } from '../../../utils/api/httpClient';
import { RemoteData, Outcome, RemoteDataStatus } from '../../../types';
import { AxiosRequestConfig } from 'axios';

const useDecisionOutcomes = (executionId: string) => {
  const [outcomes, setOutcomes] = useState<RemoteData<Error, Outcome[]>>({
    status: RemoteDataStatus.NOT_ASKED
  });

  useEffect(() => {
    let isMounted = true;
    const config: AxiosRequestConfig = {
      url: `${EXECUTIONS_PATH}/decisions/${executionId}/outcomes`,
      method: 'get'
    };

    setOutcomes({ status: RemoteDataStatus.LOADING });
    httpClient(config)
      .then(response => {
        if (isMounted) {
          setOutcomes({
            status: RemoteDataStatus.SUCCESS,
            data: response.data.outcomes
          });
        }
      })
      .catch(error => {
        setOutcomes({ status: RemoteDataStatus.FAILURE, error });
      });
    return () => {
      isMounted = false;
    };
  }, [executionId]);

  return outcomes;
};

export default useDecisionOutcomes;
