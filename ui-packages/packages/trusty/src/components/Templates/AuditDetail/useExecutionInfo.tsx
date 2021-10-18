import { Execution } from '../../../types';
import { EXECUTIONS_PATH } from '../../../utils/api/httpClient';
import useAPI from '../../../utils/api/useAPI';

const useExecutionInfo = (executionId: string) => {
  return useAPI<Execution>(
    `${EXECUTIONS_PATH}/decisions/${executionId}`,
    'get'
  );
};

export default useExecutionInfo;
