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
import { useContext, useEffect, useState } from 'react';
import { AxiosError, AxiosRequestConfig } from 'axios';
import { RemoteData, RemoteDataStatus } from '../../types';
import { httpClient } from './httpClient';
import { useHistory } from 'react-router-dom';
import { TrustyContext } from '../../components/Templates/TrustyApp/TrustyApp';

const useAPI = <T>(
  url: string,
  method: AxiosRequestConfig['method']
): RemoteData<AxiosError, T> => {
  const [data, setData] = useState<RemoteData<AxiosError, T>>({
    status: RemoteDataStatus.NOT_ASKED
  });
  const history = useHistory();
  const baseURL = useContext(TrustyContext).config.serverRoot;

  useEffect(() => {
    let isMounted = true;
    if (url && method) {
      const config: AxiosRequestConfig = {
        url,
        method,
        baseURL:
          baseURL ||
          window.TRUSTY_ENDPOINT ||
          process.env.KOGITO_TRUSTY_API_HTTP_URL
      };

      setData({ status: RemoteDataStatus.LOADING });
      httpClient(config)
        .then((response) => {
          if (isMounted) {
            setData({
              status: RemoteDataStatus.SUCCESS,
              data: response.data
            });
          }
        })
        .catch((error) => {
          setData({ status: RemoteDataStatus.FAILURE, error });
          history.replace('/error');
        });
    }

    return () => {
      isMounted = false;
    };
  }, [url, method, history]);

  return data;
};

export default useAPI;
