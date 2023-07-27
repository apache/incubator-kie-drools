/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useEffect, useState } from 'react';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';
import {
  Table,
  TableHeader,
  TableBody,
  IRow,
  ITransform,
  ICell,
  sortable,
  ISortBy
} from '@patternfly/react-table/dist/js/components/Table';
import isEmpty from 'lodash/isEmpty';
import filter from 'lodash/filter';
import sample from 'lodash/sample';
import keys from 'lodash/keys';
import reduce from 'lodash/reduce';
import isFunction from 'lodash/isFunction';
import uuidv4 from 'uuid';
import jp from 'jsonpath';
import {
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { KogitoSpinner } from '../KogitoSpinner/KogitoSpinner';
import {
  KogitoEmptyState,
  KogitoEmptyStateType
} from '../KogitoEmptyState/KogitoEmptyState';

export interface DataTableColumn {
  path: string;
  label: string;
  bodyCellTransformer?: (value: any, rowDataObj: Record<string, any>) => any;
  isSortable?: boolean;
}

interface IOwnProps {
  data: any[];
  isLoading: boolean;
  columns: DataTableColumn[];
  error: any;
  LoadingComponent?: React.ReactNode;
  ErrorComponent?: React.ReactNode;
  sortBy?: ISortBy;
  onSorting?: (index: number, direction: string) => void;
}

const getCellData = (dataObj: Record<string, unknown>, path: string) => {
  if (dataObj && path) {
    return !isEmpty(jp.value(dataObj, path)) ? jp.value(dataObj, path) : 'N/A';
  } else {
    return 'N/A';
  }
};

const getColumns = (data: any[], columns: DataTableColumn[]) => {
  if (data) {
    return columns
      ? filter(columns, (column) => !isEmpty(column.path)).map((column) => {
          return {
            title: column.label,
            data: column.path,
            cellTransforms: column.bodyCellTransformer
              ? [
                  ((value, extra) => {
                    const rowDataObj = data[extra.rowIndex];
                    return {
                      children: column.bodyCellTransformer(
                        value.title,
                        rowDataObj
                      )
                    };
                  }) as ITransform
                ]
              : undefined,
            transforms: column.isSortable ? [sortable] : undefined
          } as ICell;
        })
      : filter(keys(sample(data)), (key) => key !== '__typename').map(
          (key) => ({ title: key, data: `$.${key}` } as ICell)
        );
  } else if (columns) {
    return filter(columns, (column) => !isEmpty(column.path)).map((column) => {
      return {
        title: column.label,
        data: column.path
      } as ICell;
    });
  }
  return [];
};

const getRows = (data: any[], columns: ICell[]) => {
  let rowList: IRow[] = [];
  if (data) {
    rowList = data.map((rowData) => {
      return {
        cells: reduce(
          columns,
          (result, column: ICell) => {
            if (column.data) {
              result.push(getCellData(rowData, column.data));
            }
            return result;
          },
          []
        ),
        rowKey: uuidv4() // This is a walkaround to bypass the "id" cannot be included in "columns" issue
      };
    });
  }
  return rowList;
};

export const DataTable: React.FC<IOwnProps & OUIAProps> = ({
  data,
  isLoading,
  columns,
  error,
  LoadingComponent,
  ErrorComponent,
  sortBy,
  onSorting,
  ouiaId,
  ouiaSafe
}) => {
  const [rows, setRows] = useState<IRow[]>([]);
  const [columnList, setColumnList] = useState<(ICell | string)[]>([]);

  useEffect(() => {
    if (isLoading) {
      const cols = getColumns(null, columns);
      const row = [
        {
          cells: [
            {
              props: { colSpan: cols.length },
              title: LoadingComponent ? (
                <>{LoadingComponent}</>
              ) : (
                <Bullseye>
                  <KogitoSpinner spinnerText="Loading ..." />
                </Bullseye>
              )
            }
          ],
          rowKey: '0'
        }
      ];
      setColumnList(cols);
      setRows(row);
    } else if (isEmpty(data)) {
      const cols = getColumns(null, columns);
      const row = [
        {
          cells: [
            {
              props: { colSpan: cols.length },
              title: (
                <KogitoEmptyState
                  type={KogitoEmptyStateType.Search}
                  title="No results found"
                  body="Try using different filters"
                />
              )
            }
          ],
          rowKey: '0'
        }
      ];
      setColumnList(cols);
      setRows(row);
    } else {
      const cols = getColumns(data, columns);
      setColumnList(cols);
      setRows(getRows(data, cols));
    }
  }, [data, isLoading]);

  const onSort = (event, index, direction) => {
    if (isFunction(onSorting)) {
      onSorting(index, direction);
    }
  };

  if (error) {
    return ErrorComponent ? (
      <React.Fragment>{ErrorComponent}</React.Fragment>
    ) : (
      <div className=".pf-u-my-xl">
        <KogitoEmptyState
          type={KogitoEmptyStateType.Refresh}
          title="Oops... error while loading"
          body="Try using the refresh action to reload"
        />
      </div>
    );
  }

  return (
    <React.Fragment>
      <Table
        aria-label="Data Table"
        cells={columnList}
        rows={rows}
        sortBy={sortBy}
        onSort={onSort}
        {...componentOuiaProps(
          ouiaId,
          'data-table',
          ouiaSafe ? ouiaSafe : !isLoading
        )}
      >
        <TableHeader />
        <TableBody rowKey="rowKey" />
      </Table>
    </React.Fragment>
  );
};
