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
        .then(response => {
          if (isMounted) {
            setData({
              status: RemoteDataStatus.SUCCESS,
              data: response.data
            });
          }
        })
        .catch(error => {
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
