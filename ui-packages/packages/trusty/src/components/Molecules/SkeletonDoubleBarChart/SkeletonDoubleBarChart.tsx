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
import { Split, SplitItem } from '@patternfly/react-core';
import SkeletonStripe from '../../Atoms/SkeletonStripe/SkeletonStripe';
import './SkeletonDoubleBarChart.scss';

type SkeletonDoubleBarChartProps = {
  valuesCount: number;
  width?: number;
  height: number;
};

const SkeletonDoubleBarChart = (props: SkeletonDoubleBarChartProps) => {
  const { valuesCount = 10, width = '100%', height = 500 } = props;

  const leftStripes = [];
  const rightStripes = [];

  for (let i = 0; i < valuesCount; i++) {
    // chart bars width starts at 100% and gradually decreases without reaching 0%
    const stripeWidth = 100 - (95 / valuesCount) * i;
    // removing legend space (15%) to consider only chart height
    const chartHeight = height * 0.85;
    // dividing chart height by values count to get the height of each bar
    // in percentage
    const stripeHeight = ((chartHeight / valuesCount / 2) * 100) / chartHeight;
    // progressively distancing the bars from the top plus a small
    // initial offset (1%)
    const stripeTop = i * stripeHeight * 2 + 1;
    leftStripes.push(
      <SkeletonStripe
        isInline={true}
        key={uuid()}
        customStyle={{
          width: stripeWidth + '%',
          height: stripeHeight + '%',
          left: 0,
          top: stripeTop + '%'
        }}
      />
    );
    rightStripes.push(
      <SkeletonStripe
        isInline={true}
        key={uuid()}
        customStyle={{
          width: stripeWidth + '%',
          height: stripeHeight + '%',
          right: 0,
          top: stripeTop + '%'
        }}
      />
    );
  }

  return (
    <Split className="skeleton-double-barchart" style={{ width, height }}>
      <SplitItem isFilled={true}>
        <div className="skeleton-double-barchart__chart skeleton-double-barchart__chart--left">
          <div className="skeleton-double-barchart__chart__legend">
            <SkeletonStripe isInline={true} />
          </div>
          <div className="skeleton-double-barchart__chart__bars">
            {leftStripes}
          </div>
        </div>
      </SplitItem>
      <SplitItem isFilled={true}>
        <div className="skeleton-double-barchart__chart skeleton-double-barchart__chart--right">
          <div className="skeleton-double-barchart__chart__legend">
            <SkeletonStripe isInline={true} />
          </div>
          <div className="skeleton-double-barchart__chart__bars">
            {rightStripes}
          </div>
        </div>
      </SplitItem>
    </Split>
  );
};

export default SkeletonDoubleBarChart;
