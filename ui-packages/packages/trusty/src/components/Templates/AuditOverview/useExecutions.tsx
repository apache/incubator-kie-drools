import { useCallback, useEffect, useState } from 'react';
import { getExecutions } from '../../../utils/api/auditApi';
import { isCancelledRequest } from '../../../utils/api/httpClient';
import { RemoteData, Executions } from '../../../types';

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
    status: 'NOT_ASKED'
  });

  const loadExecutions = useCallback(() => {
    let isMounted = true;
    setExecutions({ status: 'LOADING' });
    getExecutions(searchString, from, to, limit, offset)
      .then(response => {
        if (isMounted) {
          setExecutions({ status: 'SUCCESS', data: response.data });
        }
      })
      .catch(error => {
        if (!isCancelledRequest(error)) {
          setExecutions({ status: 'FAILURE', error });
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
