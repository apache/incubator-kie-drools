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
  ChartBarProps,
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
    const positives = featuresScore.filter(feature => feature.featureScore > 0);
    const negatives = featuresScore.filter(feature => feature.featureScore < 0);
    const maxNumberOfValues = Math.max(positives.length, negatives.length);
    const barWidth = (height - 90) / maxNumberOfValues / 2;
    return { positives, negatives, maxNumberOfValues, barWidth };
  }, [featuresScore, height]);

  const maxValue = useMemo(() => {
    const max = maxBy(featuresScore, item => {
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
              sortOrder="descending"
              yDomain={[0, maxValue]}
              barWidth={scores.barWidth}
              maxValue={maxValue}
              maxNumberOfValues={scores.maxNumberOfValues}
              padding={{ ...chartPadding, right: 30, left: 90 }}
              legendTitle="Positive Impact"
              legendColor="var(--pf-global--info-color--100)"
            />
          </SplitItem>
          <SplitItem isFilled={true}>
            <ScoresBarChart
              width={width}
              height={height}
              scores={scores.negatives}
              sortOrder="ascending"
              yDomain={[-maxValue, 0]}
              barWidth={scores.barWidth}
              maxValue={maxValue}
              maxNumberOfValues={scores.maxNumberOfValues}
              padding={{ ...chartPadding, right: 90, left: 30 }}
              legendTitle="Negative Impact"
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
  sortOrder: ChartBarProps['sortOrder'];
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
    sortOrder,
    yDomain,
    barWidth,
    maxValue,
    maxNumberOfValues,
    padding,
    legendTitle,
    legendColor
  } = props;

  const computeOpacity = useCallback(
    data => {
      const computedOpacity = Math.abs(
        Math.floor((data.datum.featureScore / maxValue) * 100) / 100
      );
      return computedOpacity < 0.25 ? 0.25 : computedOpacity;
    },
    [maxValue]
  );

  const computeColor = useCallback(data => {
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
        sortKey="featureScore"
        sortOrder={sortOrder}
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
