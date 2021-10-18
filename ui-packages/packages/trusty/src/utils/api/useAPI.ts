import { useEffect, useState } from 'react';
import { AxiosError, AxiosRequestConfig } from 'axios';
import { RemoteData, RemoteDataStatus } from '../../types';
import { httpClient } from './httpClient';
import { useHistory } from 'react-router-dom';

const useAPI = <T>(
  url: string,
  method: AxiosRequestConfig['method']
): RemoteData<AxiosError, T> => {
  const [data, setData] = useState<RemoteData<AxiosError, T>>({
    status: RemoteDataStatus.NOT_ASKED
  });
  const history = useHistory();

  useEffect(() => {
    let isMounted = true;
    if (url && method) {
      const config: AxiosRequestConfig = {
        url,
        method
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
