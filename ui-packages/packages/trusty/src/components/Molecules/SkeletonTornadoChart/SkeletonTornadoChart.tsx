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
