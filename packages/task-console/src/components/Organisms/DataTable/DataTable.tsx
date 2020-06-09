import React, { useEffect, useState } from 'react';
import { Bullseye } from '@patternfly/react-core';
import { Table, TableHeader, TableBody } from '@patternfly/react-table';
import { KogitoSpinner, KogitoEmptyState } from '@kogito-apps/common';
import '@patternfly/patternfly/patternfly-addons.css';
import _ from 'lodash';
import uuidv4 from 'uuid';

interface IOwnProps {
  data: any[];
  isLoading: boolean;
  columns?: any[];
  networkStatus: any;
  error: any;
  refetch: () => void;
  LoadingComponent?: React.ReactNode;
  ErrorComponent?: React.ReactNode;
}

const getColumns = (data, columns) => {
  let columnList = [];
  if (data) {
    columnList = columns
      ? columns
      : _.filter(_.keys(_.sample(data)), key => key !== '__typename');
  }
  return columnList;
};

const getRows = (data, columns) => {
  let rowList = [];
  if (data) {
    rowList = data.map(rowData => {
      return {
        cells: _.reduce(
          columns,
          (result, column) => {
            _.forEach(rowData, (value, key) => {
              if (key.toLowerCase() === column.toLowerCase()) {
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
  refetch,
  LoadingComponent,
  ErrorComponent
}) => {
  const [rows, setRows] = useState<any>([]);
  const [columnList, setColumnList] = useState<any>([]);

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
          iconType="warningTriangleIcon"
          title="Oops... error while loading"
          body="Try using the refresh action to reload user tasks"
          refetch={refetch}
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
          iconType="searchIcon"
          title="No results found"
          body="Try using different filters"
        />
      )}
    </React.Fragment>
  );
};

export default DataTable;
