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
import { FeatureScores } from '../../../types';
import { maxBy } from 'lodash';
import {
  Chart,
  ChartAxis,
  ChartBar,
  ChartGroup,
  ChartLabel,
  ChartLegend,
  ChartProps
} from '@patternfly/react-charts';
import { Split, SplitItem } from '@patternfly/react-core';

type FeaturesScoreChartBySignProps = {
  featuresScore: FeatureScores[];
  large?: boolean;
};

const FeaturesScoreChartBySign = (props: FeaturesScoreChartBySignProps) => {
  const { featuresScore, large = false } = props;
  const width = large ? 750 : 480;
  const height = large ? 50 * featuresScore.length : 500;
  const chartPadding = { top: 60, bottom: 30 };

  const scores = useMemo(() => {
    const positives = featuresScore
      .filter((feature) => feature.featureScore > 0)
      .sort((a, b) => b.featureScore - a.featureScore);
    const negatives = featuresScore
      .filter((feature) => feature.featureScore < 0)
      .sort((a, b) => a.featureScore - b.featureScore);
    const maxNumberOfValues = Math.max(positives.length, negatives.length);
    const barWidth = (height - 90) / maxNumberOfValues / 2;
    return { positives, negatives, maxNumberOfValues, barWidth };
  }, [featuresScore, height]);

  const maxValue = useMemo(() => {
    const max = maxBy(featuresScore, (item) => {
      return Math.abs(item.featureScore);
    });
    return max ? Math.abs(max.featureScore) : 1;
  }, [featuresScore]);

  return (
    <>
      {scores && (
        <Split>
          <SplitItem isFilled={true}>
            <ScoresBarChart
              width={width}
              height={height}
              scores={scores.positives}
              yDomain={[0, maxValue]}
              barWidth={scores.barWidth}
              maxValue={maxValue}
              maxNumberOfValues={scores.maxNumberOfValues}
              padding={{ ...chartPadding, right: 30, left: 90 }}
              legendTitle="Positive impact"
              legendColor="var(--pf-global--info-color--100)"
            />
          </SplitItem>
          <SplitItem isFilled={true}>
            <ScoresBarChart
              width={width}
              height={height}
              scores={scores.negatives}
              yDomain={[-maxValue, 0]}
              barWidth={scores.barWidth}
              maxValue={maxValue}
              maxNumberOfValues={scores.maxNumberOfValues}
              padding={{ ...chartPadding, right: 90, left: 30 }}
              legendTitle="Negative impact"
              legendColor="var(--pf-global--palette--orange-300)"
            />
          </SplitItem>
        </Split>
      )}
    </>
  );
};

export default FeaturesScoreChartBySign;

type ScoresBarChartProps = {
  width: number;
  height: number;
  scores: FeatureScores[];
  yDomain: [number, number];
  barWidth: number;
  maxValue: number;
  maxNumberOfValues: number;
  padding: ChartProps['padding'];
  legendTitle: string;
  legendColor: string;
};

const ScoresBarChart = (props: ScoresBarChartProps) => {
  const {
    width,
    height,
    scores,
    yDomain,
    barWidth,
    maxValue,
    maxNumberOfValues,
    padding,
    legendTitle,
    legendColor
  } = props;

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
      domainPadding={{
        x: [-barWidth, barWidth],
        y: 20
      }}
      domain={{ x: [0, maxNumberOfValues], y: yDomain }}
      horizontal
      padding={padding}
      animate={{
        duration: 400,
        onLoad: { duration: 400 }
      }}
    >
      <ChartAxis tickFormat={() => ''} invertAxis={true} />

      <ChartBar
        data={scores}
        x="featureName"
        y="featureScore"
        alignment="middle"
        barWidth={barWidth}
        style={{
          data: {
            fill: computeColor,
            opacity: computeOpacity
          }
        }}
      />
      <ChartGroup>
        {scores.length > 0 &&
          scores.map((item, index) => {
            return (
              <ChartLabel
                className={'feature-chart-axis-label'}
                datum={{ x: index + 1, y: 0 }}
                text={item.featureName.split(' ')}
                direction="rtl"
                textAnchor={item.featureScore >= 0 ? 'start' : 'end'}
                dx={-10 * Math.sign(item.featureScore) || -10}
                key={item.featureName}
              />
            );
          })}
      </ChartGroup>

      <ChartLegend
        data={[{ name: legendTitle }]}
        colorScale={[legendColor]}
        x={width / 2 - 75}
        y={10}
      />
    </Chart>
  );
};
