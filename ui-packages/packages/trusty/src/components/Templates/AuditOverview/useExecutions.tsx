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
import { useCallback, useContext, useEffect, useMemo, useState } from 'react';
import {
  callOnceHandler,
  EXECUTIONS_PATH
} from '../../../utils/api/httpClient';
import { Executions, RemoteData, RemoteDataStatus } from '../../../types';
import axios, { AxiosRequestConfig } from 'axios';
import { TrustyContext } from '../TrustyApp/TrustyApp';

type useExecutionsParameters = {
  searchString: string;
  from: string;
  to: string;
  limit: number;
  offset: number;
};

const useExecutions = (parameters: useExecutionsParameters) => {
  const { searchString, from, to, limit, offset } = parameters;
  const [executions, setExecutions] = useState<RemoteData<Error, Executions>>({
    status: RemoteDataStatus.NOT_ASKED
  });

  const baseUrl = useContext(TrustyContext).config.serverRoot;

  const getExecutions = useMemo(() => callOnceHandler(), []);

  const loadExecutions = useCallback(() => {
    let isMounted = true;
    setExecutions({ status: RemoteDataStatus.LOADING });

    const config: AxiosRequestConfig = {
      baseURL: baseUrl,
      url: EXECUTIONS_PATH,
      method: 'get',
      params: { search: searchString, from, to, limit, offset }
    };

    getExecutions(config)
      .then((response) => {
        if (isMounted) {
          setExecutions({
            status: RemoteDataStatus.SUCCESS,
            data: response.data
          });
        }
      })
      .catch((error) => {
        if (!axios.isCancel(error)) {
          setExecutions({ status: RemoteDataStatus.FAILURE, error });
        }
      });
    return () => {
      isMounted = false;
    };
  }, [searchString, from, to, limit, offset]);

  useEffect(() => {
    loadExecutions();
  }, [searchString, from, to, limit, offset, loadExecutions]);

  return { loadExecutions, executions };
};

export default useExecutions;
