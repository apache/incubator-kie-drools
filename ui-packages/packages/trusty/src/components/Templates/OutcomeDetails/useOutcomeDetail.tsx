/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
