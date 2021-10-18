import { useMemo } from 'react';
import { ItemObject, RemoteData, RemoteDataStatus } from '../../../types';
import { AxiosError } from 'axios';
import { EXECUTIONS_PATH } from '../../../utils/api/httpClient';
import useAPI from '../../../utils/api/useAPI';

const useInputData = (executionId: string) => {
  const inputData = useAPI<{ inputs: ItemObject[] }>(
    `${EXECUTIONS_PATH}/decisions/${executionId}/structuredInputs`,
    'get'
  );

  const onlyInputs: RemoteData<AxiosError, ItemObject[]> = useMemo(() => {
    return inputData.status === RemoteDataStatus.SUCCESS
      ? { ...inputData, data: inputData.data.inputs }
      : inputData;
  }, [inputData]);

  return onlyInputs;
};

export default useInputData;
