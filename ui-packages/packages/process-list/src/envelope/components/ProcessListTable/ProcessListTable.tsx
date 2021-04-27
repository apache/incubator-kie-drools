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
import {
  TableComposable,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  ExpandableRowContent,
  ISortBy
} from '@patternfly/react-table';
import _ from 'lodash';
import { ProcessInstance } from '@kogito-apps/management-console-shared';
import ProcessListChildTable from '../ProcessListChildTable/ProcessListChildTable';
import {
  ItemDescriptor,
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner,
  EndpointLink,
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/components-common';
import { HistoryIcon } from '@patternfly/react-icons';
import Moment from 'react-moment';
import {
  getProcessInstanceDescription,
  ProcessInstanceIconCreator
} from '../utils/ProcessListUtils';
import { ProcessListDriver } from '../../../api';

interface ProcessListTableProps {
  processInstances: ProcessInstance[];
  isLoading: boolean;
  expanded: {
    [key: number]: boolean;
  };
  setExpanded: React.Dispatch<
    React.SetStateAction<{
      [key: number]: boolean;
    }>
  >;
  driver: ProcessListDriver;
  onSort: (
    event: React.SyntheticEvent<EventTarget>,
    index: number,
    direction: 'desc' | 'asc'
  ) => void;
  sortBy: ISortBy;
}

const ProcessListTable: React.FC<ProcessListTableProps & OUIAProps> = ({
  processInstances,
  isLoading,
  expanded,
  setExpanded,
  driver,
  sortBy,
  onSort,
  ouiaId,
  ouiaSafe
}) => {
  const [rowPairs, setRowPairs] = useState<any>([]);
  const columns: string[] = ['Id', 'Status', 'Created', 'Last update'];

  useEffect(() => {
    if (!_.isEmpty(processInstances)) {
      const tempRows = [];
      processInstances.forEach((processInstance: ProcessInstance) => {
        tempRows.push({
          id: processInstance.id,
          parent: [
            <>
              <div>
                <strong>
                  <ItemDescriptor
                    itemDescription={getProcessInstanceDescription(
                      processInstance
                    )}
                  />
                </strong>
              </div>
              <EndpointLink
                serviceUrl={processInstance.serviceUrl}
                isLinkShown={false}
              />
            </>,
            ProcessInstanceIconCreator(processInstance.state),
            processInstance.start ? (
              <Moment fromNow>{new Date(`${processInstance.start}`)}</Moment>
            ) : (
              ''
            ),
            processInstance.lastUpdate ? (
              <span>
                <HistoryIcon className="pf-u-mr-sm" /> {'Updated '}
                <Moment fromNow>
                  {new Date(`${processInstance.lastUpdate}`)}
                </Moment>
              </span>
            ) : (
              ''
            )
          ],
          child: [processInstance.id]
        });
      });
      setRowPairs(tempRows);
    }
  }, [processInstances]);

  const loadChild = (
    parentId: string,
    parentIndex: number
  ): JSX.Element | null => {
    if (!expanded[parentIndex]) {
      return null;
    } else {
      return (
        <ProcessListChildTable parentProcessId={parentId} driver={driver} />
      );
    }
  };

  const onToggle = (pairIndex: number): void => {
    setExpanded({
      ...expanded,
      [pairIndex]: !expanded[pairIndex]
    });
  };

  return (
    <React.Fragment>
      <TableComposable
        aria-label="Process List Table"
        {...componentOuiaProps(
          ouiaId,
          'process-list-table',
          ouiaSafe ? ouiaSafe : !isLoading
        )}
      >
        <Thead>
          <Tr>
            <Th
              style={{
                width: '72px'
              }}
            />
            {columns.map((column, columnIndex) => {
              const sortParams = {
                sort: {
                  sortBy,
                  onSort,
                  columnIndex
                }
              };
              if (!isLoading && rowPairs.length > 0) {
                return (
                  <Th key={columnIndex} {...sortParams}>
                    {column}
                  </Th>
                );
              } else {
                return <Th key={columnIndex}>{column}</Th>;
              }
            })}
          </Tr>
        </Thead>
        {!isLoading && !_.isEmpty(rowPairs) ? (
          rowPairs.map((pair, pairIndex) => {
            const parentRow = (
              <Tr key={`${pairIndex}-parent`}>
                <Td
                  key={`${pairIndex}-parent-0`}
                  expand={{
                    rowIndex: pairIndex,
                    isExpanded: expanded[pairIndex],
                    onToggle: () => onToggle(pairIndex)
                  }}
                />
                {pair.parent.map((cell, cellIndex) => (
                  <Td
                    key={`${pairIndex}-parent-${++cellIndex}`}
                    dataLabel={columns[cellIndex]}
                  >
                    {cell}
                  </Td>
                ))}
              </Tr>
            );
            const childRow = (
              <Tr
                key={`${pairIndex}-child`}
                isExpanded={expanded[pairIndex] === true}
              >
                <Td key={`${pairIndex}-child-0`} />
                {rowPairs[pairIndex].child.map((cell, cellIndex) => (
                  <Td
                    key={`${pairIndex}-child-${++cellIndex}`}
                    dataLabel={columns[cellIndex]}
                    noPadding={rowPairs[pairIndex].noPadding}
                    colSpan={6}
                  >
                    <ExpandableRowContent>
                      {loadChild(cell, pairIndex)}
                    </ExpandableRowContent>
                  </Td>
                ))}
              </Tr>
            );
            return (
              <Tbody key={pairIndex}>
                {parentRow}
                {childRow}
              </Tbody>
            );
          })
        ) : (
          <tbody>
            <Tr>
              <Td colSpan={7}>
                <>
                  {isLoading && (
                    <KogitoSpinner
                      spinnerText={'Loading process instances...'}
                    />
                  )}
                  {!isLoading && rowPairs.length === 0 && (
                    <KogitoEmptyState
                      type={KogitoEmptyStateType.Search}
                      title="No results found"
                      body="Try using different filters"
                    />
                  )}
                </>
              </Td>
            </Tr>
          </tbody>
        )}
      </TableComposable>
    </React.Fragment>
  );
};

export default ProcessListTable;
