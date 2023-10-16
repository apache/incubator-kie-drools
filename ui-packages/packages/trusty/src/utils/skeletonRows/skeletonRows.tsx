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
import { IRow } from '@patternfly/react-table';
import SkeletonStripe from '../../components/Atoms/SkeletonStripe/SkeletonStripe';

/*
 * Based on a number of rows and columns, this function creates an array specifically intended
 * for the Patternfly Table component. Feeding the skeletons array to the table rows prop,
 * it will produce animated stripes to be displayed while loading real data
 * */

const skeletonRows = (
  colsCount: number,
  rowsCount: number,
  rowKey?: string
) => {
  const skeletons = [];
  rowKey = rowKey || 'key';
  for (let j = 0; j < rowsCount; j++) {
    const cells = [];
    for (let i = 0; i < colsCount; i++) {
      const size = (i + j) % 2 ? 'lg' : 'md';
      cells.push({
        title: <SkeletonStripe size={size} />
      });
    }
    const skeletonRow: IRow = {
      cells
    };
    skeletonRow[rowKey] = `skeleton-${j}`;
    skeletons.push(skeletonRow);
  }
  return skeletons;
};

export default skeletonRows;
