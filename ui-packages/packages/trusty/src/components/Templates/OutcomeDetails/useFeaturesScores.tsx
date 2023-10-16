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
import { useEffect, useState } from 'react';
import {
  FeatureScores,
  RemoteData,
  RemoteDataStatus,
  Saliencies,
  SaliencyStatus
} from '../../../types';
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
    if (saliencies.status === RemoteDataStatus.SUCCESS && outcomeId) {
      if (saliencies.data.status === SaliencyStatus.SUCCEEDED) {
        const selectedExplanation = find(
          saliencies.data.saliencies,
          (saliency) => {
            return saliency.outcomeId === outcomeId;
          }
        );

        if (selectedExplanation) {
          const sortedFeatures = orderBy(
            selectedExplanation.featureImportance,
            (item) => Math.abs(item.featureScore),
            'asc'
          );
          setFeaturesScores(sortedFeatures as FeatureScores[]);
          if (sortedFeatures.length > 10) {
            setTopFeaturesScores(
              sortedFeatures.slice(sortedFeatures.length - 10)
            );
          }

          const positiveFeatures = sortedFeatures.filter(
            (feature) => feature.featureScore > 0
          );
          const negativeFeatures = sortedFeatures.filter(
            (feature) => feature.featureScore < 0
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
          } else {
            setTopFeaturesScoresBySign([]);
          }
        }
      }
    }
  }, [saliencies, outcomeId]);

  return { featuresScores, topFeaturesScores, topFeaturesScoresBySign };
};

export default useFeaturesScores;
