import { EXECUTIONS_PATH } from '../../../utils/api/httpClient';
import { Outcome, RemoteData, RemoteDataStatus } from '../../../types';
import useAPI from '../../../utils/api/useAPI';
import { useMemo } from 'react';
import { AxiosError } from 'axios';

const useDecisionOutcomes = (executionId: string) => {
  const outcomes = useAPI<{ outcomes: Outcome[] }>(
    `${EXECUTIONS_PATH}/decisions/${executionId}/outcomes`,
    'get'
  );

  const onlyOutcomes: RemoteData<AxiosError, Outcome[]> = useMemo(() => {
    return outcomes.status === RemoteDataStatus.SUCCESS
      ? { ...outcomes, data: outcomes.data.outcomes }
      : outcomes;
  }, [outcomes]);

  return onlyOutcomes;
};

export default useDecisionOutcomes;
