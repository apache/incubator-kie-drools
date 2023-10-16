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
import { Grid, GridItem, gridSpans } from '@patternfly/react-core';
import SkeletonStripe from '../../Atoms/SkeletonStripe/SkeletonStripe';

type SkeletonGridProps = {
  rowsCount: number;
  colsDefinition: number | number[];
};

/*
 * SkeletonGrid can be called passing as colsDefinition a number of columns or an explicit list of columns sizes that
 * will be fed to the css grid component. This is intended for cases when an uneven number of columns
 * is needed or when specific columns size are wanted instead of columns with equally divided size
 * */
const SkeletonGrid = (props: SkeletonGridProps) => {
  const { rowsCount, colsDefinition } = props;
  let colsCount = 0;
  let colList: number[] = [];

  if (typeof colsDefinition === 'number') {
    colsCount = colsDefinition;
    colList.length = colsDefinition;
    colList.fill(Math.floor(12 / colsDefinition));
  }
  if (Array.isArray(colsDefinition)) {
    colsCount = colsDefinition.length;
    colList = colsDefinition;
  }
  const gridRows = [];
  for (let i = 0; i < rowsCount; i++) {
    for (let j = 0; j < colsCount; j++) {
      const size = (i + j) % 2 ? 'lg' : 'md';
      gridRows.push(
        <GridItem
          span={colList[j] as gridSpans}
          key={`skeleton-grid-${j}-${i}`}
        >
          <SkeletonStripe size={size} />
        </GridItem>
      );
    }
  }
  return <Grid hasGutter>{gridRows}</Grid>;
};

export default SkeletonGrid;
