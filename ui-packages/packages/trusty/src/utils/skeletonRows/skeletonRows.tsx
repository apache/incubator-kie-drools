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
