import React, { useEffect, useState } from 'react';
import { Bullseye } from '@patternfly/react-core';
import {
  Table,
  TableHeader,
  TableBody,
  ICell,
  IRow
} from '@patternfly/react-table';
import KogitoSpinner from '../../Atoms/KogitoSpinner/KogitoSpinner';
import {
  KogitoEmptyState,
  KogitoEmptyStateType
} from '../../Atoms/KogitoEmptyState/KogitoEmptyState';
import '@patternfly/patternfly/patternfly-addons.css';
import _ from 'lodash';
import uuidv4 from 'uuid';

interface IOwnProps {
  data: any[];
  isLoading: boolean;
  columns?: ICell[];
  networkStatus: any;
  error: any;
  refetch: () => void;
  LoadingComponent?: React.ReactNode;
  ErrorComponent?: React.ReactNode;
}

const getColumns = (data: any[], columns: ICell[]) => {
  let columnList: ICell[] = [];
  if (data) {
    columnList = columns
      ? _.filter(columns, column => !_.isEmpty(column.data))
      : _.filter(_.keys(_.sample(data)), key => key !== '__typename').map(
          key => ({ title: key, data: key } as ICell)
        );
  }
  return columnList;
};

const getRows = (data: any[], columns: ICell[]) => {
  let rowList: IRow[] = [];
  if (data) {
    rowList = data.map(rowData => {
      return {
        cells: _.reduce(
          columns,
          (result, column: ICell) => {
            _.forEach(rowData, (value, key) => {
              if (
                column.data &&
                key.toLowerCase() === column.data.toLowerCase()
              ) {
                if (_.isEmpty(value) || value === '{}') {
                  result.push('N/A');
                } else {
                  result.push(value);
                }
              }
            });
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

const DataTable: React.FC<IOwnProps> = ({
  data,
  isLoading,
  columns,
  networkStatus,
  error,
  LoadingComponent,
  ErrorComponent,
  refetch
}) => {
  const [rows, setRows] = useState<IRow[]>([]);
  const [columnList, setColumnList] = useState<ICell[]>([]);

  useEffect(() => {
    if (!_.isEmpty(data)) {
      setColumnList(getColumns(data, columns));
    }
  }, [data]);

  useEffect(() => {
    if (!_.isEmpty(data) && !_.isEmpty(columnList)) {
      setRows(getRows(data, columnList));
    }
  }, [columnList]);

  if (isLoading) {
    return LoadingComponent ? (
      <React.Fragment>{LoadingComponent}</React.Fragment>
    ) : (
      <Bullseye>
        <KogitoSpinner spinnerText="Loading..." />
      </Bullseye>
    );
  }

  if (networkStatus === 4) {
    return LoadingComponent ? (
      <React.Fragment>{LoadingComponent}</React.Fragment>
    ) : (
      <Bullseye>
        <KogitoSpinner spinnerText="Loading..." />
      </Bullseye>
    );
  }

  if (error) {
    return ErrorComponent ? (
      <React.Fragment>{ErrorComponent}</React.Fragment>
    ) : (
      <div className=".pf-u-my-xl">
        <KogitoEmptyState
          type={KogitoEmptyStateType.Refresh}
          title="Oops... error while loading"
          body="Try using the refresh action to reload user tasks"
          onClick={refetch}
        />
      </div>
    );
  }

  return (
    <React.Fragment>
      {data !== undefined &&
        !isLoading &&
        rows.length > 0 &&
        columnList.length > 0 && (
          <Table aria-label="Data Table" cells={columnList} rows={rows}>
            <TableHeader />
            <TableBody rowKey="rowKey" />
          </Table>
        )}
      {data !== undefined && !isLoading && rows.length === 0 && (
        <KogitoEmptyState
          type={KogitoEmptyStateType.Search}
          title="No results found"
          body="Try using different filters"
        />
      )}
    </React.Fragment>
  );
};

export default DataTable;
