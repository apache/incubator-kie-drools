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
import React, { useCallback, useMemo } from 'react';
import {
  Chart,
  ChartAxis,
  ChartBar,
  ChartGroup,
  ChartLabel,
  ChartLegend,
  ChartTooltip,
  ChartTooltipProps
} from '@patternfly/react-charts';
import { Selection } from 'victory-core';
import { maxBy } from 'lodash';
import formattedScore from '../../../utils/formattedScore/formattedScore';
import { FeatureScores } from '../../../types';
import './FeaturesScoreChart.scss';

type FeaturesScoreChartProps = {
  featuresScore: FeatureScores[];
  large?: boolean;
};
const FeaturesScoreChart = (props: FeaturesScoreChartProps) => {
  const { featuresScore, large = false } = props;
  const width = large ? 1400 : 800;
  const height = large ? 50 * featuresScore.length : 500;

  const maxValue = useMemo(() => {
    const max = maxBy(featuresScore, (item) => {
      return Math.abs(item.featureScore);
    });
    return max ? max.featureScore : 1;
  }, [featuresScore]);

  const labels = useMemo(() => {
    const labelList: string[] = [];
    featuresScore.forEach((item) => {
      labelList.push(formattedScore(item.featureScore));
    });
    return labelList;
  }, [featuresScore]);

  const computeOpacity = useCallback(
    (data) => {
      const computedOpacity = Math.abs(
        Math.floor((data.datum.featureScore / maxValue) * 100) / 100
      );
      return computedOpacity < 0.25 ? 0.25 : computedOpacity;
    },
    [maxValue]
  );

  const computeColor = useCallback((data) => {
    return data.datum.featureScore >= 0
      ? 'var(--pf-global--info-color--100)'
      : 'var(--pf-global--palette--orange-300)';
  }, []);

  return (
    <Chart
      ariaDesc="Importance of different features on the decision"
      width={width}
      height={height}
      domainPadding={{ x: [20, 20], y: 80 }}
      domain={{ y: [-maxValue, maxValue] }}
      horizontal
      padding={{ top: 60, right: 30, bottom: 30, left: 30 }}
      animate={{
        duration: 400,
        onLoad: { duration: 400 }
      }}
    >
      <ChartAxis tickFormat={() => ''} />

      <ChartBar
        data={featuresScore}
        x="featureName"
        y="featureScore"
        labels={labels}
        labelComponent={<CustomLabel />}
        alignment="middle"
        barWidth={25}
        events={[
          {
            target: 'data',
            eventHandlers: {
              onMouseOver: (event) => {
                const { x, y } = Selection.getSVGEventCoordinates(event);
                return [
                  {
                    target: 'labels',
                    mutation: () => ({
                      x,
                      y,
                      active: true
                    })
                  }
                ];
              },
              onMouseMove: (event) => {
                const { x, y } = Selection.getSVGEventCoordinates(event);
                return [
                  {
                    target: 'labels',
                    mutation: () => ({
                      x,
                      y,
                      active: true
                    })
                  }
                ];
              },
              onTouchMove: (event) => {
                const { x, y } = Selection.getSVGEventCoordinates(event);
                return [
                  {
                    target: 'labels',
                    mutation: () => ({
                      x,
                      y,
                      active: true
                    })
                  }
                ];
              },
              onTouchStart: (event) => {
                const { x, y } = Selection.getSVGEventCoordinates(event);
                return [
                  {
                    target: 'labels',
                    mutation: () => ({
                      x,
                      y,
                      active: true
                    })
                  }
                ];
              },
              onMouseOut: () => {
                return [
                  {
                    target: 'labels',
                    mutation: () => ({
                      active: false
                    })
                  }
                ];
              },
              onTouchEnd: () => {
                return [
                  {
                    target: 'labels',
                    mutation: () => ({
                      active: false
                    })
                  }
                ];
              }
            }
          }
        ]}
        style={{
          data: {
            fill: computeColor,
            opacity: computeOpacity
          }
        }}
      />
      <ChartGroup>
        {featuresScore.length > 0 &&
          featuresScore.map((item, index) => {
            return (
              <ChartLabel
                className={'feature-chart-axis-label'}
                datum={{ x: index + 1, y: 0 }}
                text={item.featureName}
                direction="rtl"
                textAnchor={item.featureScore >= 0 ? 'start' : 'end'}
                dx={-10 * Math.sign(item.featureScore) || -10}
                key={item.featureName}
              />
            );
          })}
      </ChartGroup>

      <ChartGroup>
        {featuresScore.length > 0 &&
          featuresScore.map((item, index) => {
            return (
              <ChartLabel
                className={'feature-chart-score-label'}
                datum={{ x: index + 1, y: item.featureScore }}
                text={formattedScore(item.featureScore)}
                textAnchor={item.featureScore >= 0 ? 'start' : 'end'}
                dx={10 * Math.sign(item.featureScore) || 10}
                key={item.featureName}
              />
            );
          })}
      </ChartGroup>

      <ChartLegend
        data={[{ name: 'Negative Impact' }, { name: 'Positive Impact' }]}
        colorScale={[
          'var(--pf-global--palette--orange-300)',
          'var(--pf-global--info-color--100)'
        ]}
        x={width / 2 - 150}
        y={10}
      />
    </Chart>
  );
};

const CustomLabel = (props: ChartTooltipProps) => {
  return (
    <ChartTooltip
      {...props}
      text={(data) => {
        return data.datum.featureName + '\n' + data.datum.featureScore;
      }}
      pointerWidth={10}
      orientation={'bottom'}
      dy={25}
      dx={0}
    />
  );
};

export default FeaturesScoreChart;
