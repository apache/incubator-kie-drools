import { useMemo } from 'react';
import { ItemObject, RemoteData, RemoteDataStatus } from '../../../types';
import { AxiosError } from 'axios';
import { EXECUTIONS_PATH } from '../../../utils/api/httpClient';
import useAPI from '../../../utils/api/useAPI';

const useOutcomeDetail = (executionId: string, outcomeId: string | null) => {
  const url =
    executionId && outcomeId
      ? `${EXECUTIONS_PATH}/decisions/${executionId}/outcomes/${outcomeId}`
      : null;

  const outcomeDetail = useAPI<{ outcomeInputs: ItemObject[] }>(url, 'get');

  const onlyInputs: RemoteData<AxiosError, ItemObject[]> = useMemo(() => {
    return outcomeDetail.status === RemoteDataStatus.SUCCESS
      ? { ...outcomeDetail, data: outcomeDetail.data.outcomeInputs }
      : outcomeDetail;
  }, [outcomeDetail]);

  return onlyInputs;
};

export default useOutcomeDetail;
