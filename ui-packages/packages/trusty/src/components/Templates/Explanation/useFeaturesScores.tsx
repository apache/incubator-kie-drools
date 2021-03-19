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
  const [topFeaturesScoresBySign, setTopFeaturesScoresBySign] = useState<
    FeatureScores[]
  >([]);

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

          const positiveFeatures = sortedFeatures.filter(
            feature => feature.featureScore > 0
          );
          const negativeFeatures = sortedFeatures.filter(
            feature => feature.featureScore < 0
          );
          if (positiveFeatures.length > 5 || negativeFeatures.length > 5) {
            setTopFeaturesScoresBySign([
              ...(positiveFeatures.length > 5
                ? positiveFeatures.slice(positiveFeatures.length - 5)
                : positiveFeatures),
              ...(negativeFeatures.length > 5
                ? negativeFeatures.slice(negativeFeatures.length - 5)
                : negativeFeatures)
            ]);
          }
        }
      }
    }
  }, [saliencies, outcomeId]);

  return { featuresScores, topFeaturesScores, topFeaturesScoresBySign };
};

export default useFeaturesScores;
