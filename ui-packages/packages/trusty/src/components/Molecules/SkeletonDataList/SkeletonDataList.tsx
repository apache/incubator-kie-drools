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
import {
  DataList,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow
} from '@patternfly/react-core';
import SkeletonStripe from '../../Atoms/SkeletonStripe/SkeletonStripe';
import './SkeletonDataList.scss';

type SkeletonDataListProps = {
  rowsCount: number;
  colsCount: number;
  hasHeader?: boolean;
};

const SkeletonDataList = (props: SkeletonDataListProps) => {
  const { rowsCount, colsCount, hasHeader } = props;

  const rows = [];
  for (let i = 0; i < rowsCount; i++) {
    const row = [];
    for (let j = 0; j < colsCount; j++) {
      const size = (i + j) % 2 ? 'lg' : 'md';
      row.push(
        <DataListCell key={`content-${j}`}>
          <SkeletonStripe size={size} />
        </DataListCell>
      );
    }
    const skeletonRow = {
      cells: row,
      key: 'skeleton-row-' + i
    };
    rows.push(skeletonRow);
  }

  return (
    <DataList aria-label="Loading content">
      {rows.map((item, index) => {
        let headerClass;
        if (hasHeader && index === 0) {
          headerClass = 'skeleton-datalist__header';
        }
        return (
          <DataListItem aria-labelledby={`Loading row ${index}`} key={item.key}>
            <DataListItemRow className={headerClass}>
              <DataListItemCells dataListCells={item.cells} />
            </DataListItemRow>
          </DataListItem>
        );
      })}
    </DataList>
  );
};

export default SkeletonDataList;
