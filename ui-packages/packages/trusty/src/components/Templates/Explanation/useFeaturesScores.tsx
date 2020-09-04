import { useEffect, useState } from 'react';
import { FeatureScores, RemoteData, Saliencies } from '../../../types';
import { orderBy, find } from 'lodash';

const useFeaturesScores = (
  saliencies: RemoteData<Error, Saliencies>,
  outcomeId: string
) => {
  const [featuresScores, setFeaturesScores] = useState<FeatureScores[]>([]);
  const [topFeaturesScores, setTopFeaturesScores] = useState<FeatureScores[]>(
    []
  );

  useEffect(() => {
    if (saliencies.status === 'SUCCESS' && outcomeId) {
      if (saliencies.data.status === 'SUCCEEDED') {
        const selectedExplanation = find(
          saliencies.data.saliencies,
          saliency => {
            return saliency.outcomeId === outcomeId;
          }
        );

        if (selectedExplanation) {
          const sortedFeatures = orderBy(
            selectedExplanation.featureImportance,
            item => Math.abs(item.featureScore),
            'asc'
          );
          setFeaturesScores(sortedFeatures as FeatureScores[]);
          if (sortedFeatures.length > 10) {
            setTopFeaturesScores(
              sortedFeatures.slice(sortedFeatures.length - 10)
            );
          }
        }
      }
    }
  }, [saliencies, outcomeId]);

  return { featuresScores, topFeaturesScores };
};

export default useFeaturesScores;
