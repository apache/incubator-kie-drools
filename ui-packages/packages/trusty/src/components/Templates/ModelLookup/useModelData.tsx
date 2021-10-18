import { ModelData } from '../../../types';
import { EXECUTIONS_PATH } from '../../../utils/api/httpClient';
import useAPI from '../../../utils/api/useAPI';

const useModelData = (executionId: string) => {
  return useAPI<ModelData>(`${EXECUTIONS_PATH}/${executionId}/model`, 'get');
};

export default useModelData;
