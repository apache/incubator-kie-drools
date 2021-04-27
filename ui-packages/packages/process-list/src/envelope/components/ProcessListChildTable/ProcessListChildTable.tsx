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

import { ProcessListDriver } from '../../../api';
import { IRow, Table, TableBody, TableHeader } from '@patternfly/react-table';
import React, { useEffect, useState } from 'react';
import { ProcessInstance } from '@kogito-apps/management-console-shared';
import _ from 'lodash';
import {
  ServerErrors,
  KogitoSpinner,
  ItemDescriptor,
  EndpointLink,
  KogitoEmptyState,
  KogitoEmptyStateType,
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/components-common';
import {
  getProcessInstanceDescription,
  ProcessInstanceIconCreator
} from '../utils/ProcessListUtils';
import { HistoryIcon } from '@patternfly/react-icons';
import Moment from 'react-moment';
export interface ProcessListChildTableProps {
  parentProcessId: string;
  driver: ProcessListDriver;
}
const ProcessListChildTable: React.FC<ProcessListChildTableProps &
  OUIAProps> = ({ parentProcessId, driver, ouiaId, ouiaSafe }) => {
  const [rows, setRows] = useState<(IRow | string[])[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [showNoDataEmptyState, setShowNoDataEmptyState] = useState<boolean>(
    false
  );
  const [error, setError] = useState<string>(undefined);
  const columns = [
    {
      title: 'Id'
    },
    {
      title: 'Status'
    },
    {
      title: 'Created'
    },
    {
      title: 'Last update'
    }
  ];

  const createRows = (processInstances: ProcessInstance[]): void => {
    if (!_.isEmpty(processInstances)) {
      const tempRows = [];
      processInstances.forEach((child: ProcessInstance) => {
        tempRows.push({
          cells: [
            {
              title: (
                <>
                  <div>
                    <strong>
                      <ItemDescriptor
                        itemDescription={getProcessInstanceDescription(child)}
                      />
                    </strong>
                  </div>
                  <EndpointLink
                    serviceUrl={child.serviceUrl}
                    isLinkShown={false}
                  />
                </>
              )
            },
            {
              title: ProcessInstanceIconCreator(child.state)
            },
            {
              title: child.start ? (
                <Moment fromNow>{new Date(`${child.start}`)}</Moment>
              ) : (
                ''
              )
            },
            {
              title: child.lastUpdate ? (
                <span>
                  {' '}
                  <HistoryIcon className="pf-u-mr-sm" /> Updated{' '}
                  <Moment fromNow>{new Date(`${child.lastUpdate}`)}</Moment>
                </span>
              ) : (
                ''
              )
            }
          ]
        });
      });
      setRows(tempRows);
      setShowNoDataEmptyState(false);
    } else {
      setShowNoDataEmptyState(true);
    }
  };

  const getChildProcessInstances = async (): Promise<void> => {
    try {
      setIsLoading(true);
      const response = await driver.getChildProcessesQuery(parentProcessId);
      createRows(response);
    } catch (error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    getChildProcessInstances();
  }, []);

  if (isLoading) {
    return <KogitoSpinner spinnerText={'Loading child instances...'} />;
  }

  if (error) {
    return <ServerErrors error={error} variant="large" />;
  }

  if (!isLoading && showNoDataEmptyState) {
    return (
      <KogitoEmptyState
        type={KogitoEmptyStateType.Info}
        title="No child process instances"
        body="This process has no related sub processes"
      />
    );
  }

  return (
    <Table
      aria-label="Process List Child Table"
      cells={columns}
      rows={rows}
      variant={'compact'}
      className="kogito-management-console__compact-table"
      {...componentOuiaProps(
        ouiaId,
        'process-list-child-table',
        ouiaSafe ? ouiaSafe : !isLoading
      )}
    >
      <TableHeader />
      <TableBody />
    </Table>
  );
};

export default ProcessListChildTable;
