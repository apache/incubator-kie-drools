import { Saliencies } from '../../../types';

import { EXECUTIONS_PATH } from '../../../utils/api/httpClient';
import useAPI from '../../../utils/api/useAPI';

const useSaliencies = (executionId: string) => {
  return useAPI<Saliencies>(
    `${EXECUTIONS_PATH}/decisions/${executionId}/explanations/saliencies`,
    'get'
  );
};

export default useSaliencies;
