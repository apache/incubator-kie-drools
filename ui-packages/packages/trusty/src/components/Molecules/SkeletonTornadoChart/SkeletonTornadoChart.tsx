import React from 'react';
import { v4 as uuid } from 'uuid';
import SkeletonStripe from '../../Atoms/SkeletonStripe/SkeletonStripe';
import './SkeletonTornadoChart.scss';

type SkeletonTornadoChartProps = {
  valuesCount: number;
  width?: number;
  height: number;
};

const SkeletonTornadoChart = (props: SkeletonTornadoChartProps) => {
  const { valuesCount = 10, width = '100%', height = 500 } = props;
  const stripes = [];

  for (let i = 0; i < valuesCount; i++) {
    // to resemble a tornado chart shape, the bars are distributed to the right
    // and left side of the y axis, so the width of the bars starts at 45% (almost
    // half the chart total width) and then decreases gradually without reaching 0%
    const stripeWidth = 45 - (40 / valuesCount) * i;
    // the bars are positioned alternately to the left and right of the axis
    // using left offset; positive bars starts at 50% (position of the axis),
    // negative ones at 50% minus their width
    const stripeLeft = i % 2 ? 50 : 50 - stripeWidth;
    // progressively distancing the bars from the top plus a small
    // initial offset (1%)
    const stripeTop = i * 10 + 1;
    stripes.push(
      <SkeletonStripe
        isInline={true}
        key={uuid()}
        customStyle={{
          width: stripeWidth + '%',
          left: stripeLeft + '%',
          top: stripeTop + '%'
        }}
      />
    );
  }

  return (
    <div className="skeleton-tornado" style={{ width, height }}>
      <div className="skeleton-tornado__legend">
        <SkeletonStripe isInline={true} />
        <SkeletonStripe isInline={true} />
      </div>
      <div className="skeleton-tornado__chart">{stripes}</div>
    </div>
  );
};

export default SkeletonTornadoChart;
