import { useEffect, useState } from 'react';
import { RemoteData, FeatureScores } from '../../../types';
import { AxiosRequestConfig } from 'axios';
import { EXECUTIONS_PATH, httpClient } from '../../../utils/api/httpClient';
import { orderBy } from 'lodash';

const useFeaturesScores = (executionId: string) => {
  const [featuresScores, setFeaturesScores] = useState<
    RemoteData<Error, FeatureScores[]>
  >({
    status: 'NOT_ASKED'
  });
  const [topFeaturesScores, setTopFeaturesScores] = useState<FeatureScores[]>(
    []
  );

  useEffect(() => {
    let isMounted = true;
    const config: AxiosRequestConfig = {
      url: `${EXECUTIONS_PATH}/decisions/${executionId}/featureImportance`,
      method: 'get'
    };

    setFeaturesScores({ status: 'LOADING' });
    httpClient(config)
      .then(response => {
        if (isMounted) {
          if (response.data.featureImportance) {
            const sortedFeatures = orderBy(
              response.data.featureImportance,
              item => Math.abs(item.featureScore),
              'asc'
            );
            setFeaturesScores({
              status: 'SUCCESS',
              data: sortedFeatures
            });
            if (sortedFeatures.length > 10) {
              setTopFeaturesScores(
                sortedFeatures.slice(sortedFeatures.length - 10)
              );
            }
          } else {
            setFeaturesScores({ status: 'SUCCESS', data: [] });
          }
        }
      })
      .catch(error => {
        setFeaturesScores({ status: 'FAILURE', error });
      });
    return () => {
      isMounted = false;
    };
  }, [executionId]);

  return { featuresScores, topFeaturesScores };
};

export default useFeaturesScores;
