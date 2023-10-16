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
import React, { useState, useEffect } from 'react';
import {
  Table,
  TableHeader,
  TableBody,
  sortable,
  IRow,
  ISortBy
} from '@patternfly/react-table/dist/js/components/Table';
import { Tooltip } from '@patternfly/react-core/dist/js/components/Tooltip';
import {
  KogitoEmptyState,
  KogitoEmptyStateType
} from '@kogito-apps/components-common/dist/components/KogitoEmptyState';
import { KogitoSpinner } from '@kogito-apps/components-common/dist/components/KogitoSpinner';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import {
  Job,
  JobsSortBy
} from '@kogito-apps/management-console-shared/dist/types';
import {
  setTitle,
  constructObject
} from '@kogito-apps/management-console-shared/dist/utils/Utils';
import Moment from 'react-moment';
import _ from 'lodash';
import { JobsIconCreator } from '../../../utils/utils';
import { JobsManagementDriver } from '../../../api';
import { HistoryIcon } from '@patternfly/react-icons/dist/js/icons/history-icon';
import '../styles.css';

interface ActionsMeta {
  title: string;
  onClick: (event, rowId, rowData, extra) => void;
}
interface RowTitle {
  title: JSX.Element;
}

interface RetrievedValueType {
  tempRows: RowTitle[];
  jobType: string;
}

interface JobsManagementTableProps {
  jobs: Job[];
  driver: JobsManagementDriver;
  doQueryJobs: (offset: number, limit: number) => Promise<void>;
  handleCancelModalToggle: () => void;
  handleDetailsToggle: () => void;
  handleRescheduleToggle: () => void;
  isActionPerformed: boolean;
  isLoading: boolean;
  setIsActionPerformed: (isActionPerformed: boolean) => void;
  selectedJobInstances: Job[];
  setModalTitle: (title: JSX.Element) => void;
  setModalContent: (content: string) => void;
  setSelectedJobInstances: (selectedJobInstances: Job[]) => void;
  setSelectedJob: (job: Job) => void;
  setSortBy: (sortObj: ISortBy) => void;
  setOrderBy: (orderBy: JobsSortBy) => void;
  sortBy: ISortBy;
}

const JobsManagementTable: React.FC<JobsManagementTableProps & OUIAProps> = ({
  jobs,
  driver,
  doQueryJobs,
  handleCancelModalToggle,
  handleDetailsToggle,
  handleRescheduleToggle,
  isActionPerformed,
  isLoading,
  setIsActionPerformed,
  selectedJobInstances,
  setModalTitle,
  setModalContent,
  setSelectedJobInstances,
  setSelectedJob,
  setSortBy,
  sortBy,
  setOrderBy,
  ouiaId,
  ouiaSafe
}) => {
  const [rows, setRows] = useState<IRow[]>([]);
  const jobRow: IRow[] = [];
  const editableJobStatus: string[] = ['SCHEDULED', 'ERROR'];
  const columns = [
    { title: 'Id' },
    { title: 'Status' },
    { title: 'Expiration time' },
    { title: 'Retries' },
    { title: 'Execution counter' },
    { title: 'Last update' }
  ];

  const checkNotEmpty = (): boolean => {
    if (jobs && jobs.length > 0 && !isLoading) {
      return true;
    } else {
      return false;
    }
  };

  columns.map((column) => {
    column['props'] = { className: 'pf-u-text-align-center' };
    checkNotEmpty() && column.title !== 'Id'
      ? (column['transforms'] = [sortable])
      : '';
    return column;
  });

  const getValues = (job): RetrievedValueType => {
    const tempRows: RowTitle[] = [];
    let jobType: string = '';
    for (const item in job) {
      if (item === 'id') {
        const ele = {
          title: (
            <Tooltip content={job.id}>
              <span>{job.id.substring(0, 7)}</span>
            </Tooltip>
          )
        };
        tempRows.push(ele);
      } else if (item === 'status') {
        const ele = {
          title: JobsIconCreator(job.status)
        };
        if (editableJobStatus.includes(job[item])) {
          jobType = 'Editable';
        } else {
          jobType = 'Non-editable';
        }
        tempRows.push(ele);
      } else if (item === 'expirationTime') {
        const ele = {
          title: (
            <React.Fragment>
              {job.expirationTime ? (
                <>
                  {' '}
                  expires in{' '}
                  <Moment fromNow ago>
                    {job.expirationTime}
                  </Moment>
                </>
              ) : (
                'N/A'
              )}
            </React.Fragment>
          )
        };
        tempRows.push(ele);
      } else if (item === 'lastUpdate') {
        const ele = {
          title: (
            <>
              <HistoryIcon className="pf-u-mr-sm" /> Updated{' '}
              <Moment fromNow>{job.lastUpdate}</Moment>
            </>
          )
        };
        tempRows.push(ele);
      } else {
        const ele = {
          title: <span>{job[item]}</span>
        };
        tempRows.push(ele);
      }
    }
    return { tempRows, jobType };
  };

  const onSelect = (event, isSelected, rowId, rowData): void => {
    setIsActionPerformed(false);
    const copyOfRows = [...rows];
    if (rowId === -1) {
      copyOfRows.forEach((row) => {
        row.selected = isSelected;
        return row;
      });
      if (selectedJobInstances.length === jobs.length) {
        setSelectedJobInstances([]);
      } else if (selectedJobInstances.length < jobs.length) {
        /* istanbul ignore else*/
        setSelectedJobInstances(_.cloneDeep(jobs));
      }
    } else {
      if (copyOfRows[rowId]) {
        copyOfRows[rowId].selected = isSelected;
        const row = [...jobs].filter(
          (job) => job.id === copyOfRows[rowId].rowKey
        );
        const rowData = _.find(selectedJobInstances, [
          'id',
          copyOfRows[rowId].rowKey
        ]);
        if (rowData === undefined) {
          setSelectedJobInstances([...selectedJobInstances, row[0]]);
        } else {
          const copyOfSelectedJobInstances = [...selectedJobInstances];
          _.remove(
            copyOfSelectedJobInstances,
            (job) => job.id === copyOfRows[rowId].rowKey
          );
          setSelectedJobInstances(copyOfSelectedJobInstances);
        }
      }
    }
    setRows(copyOfRows);
  };

  const tableContent = (jobs): void => {
    !isLoading &&
      !_.isEmpty(jobs) &&
      jobs.map((job) => {
        const retrievedValue = getValues(
          _.pick(job, [
            'id',
            'status',
            'expirationTime',
            'retries',
            'executionCounter',
            'lastUpdate'
          ])
        );
        jobRow.push({
          cells: retrievedValue.tempRows,
          type: retrievedValue.jobType,
          rowKey: job.id,
          selected: false
        });
      });
    if (isLoading) {
      const tempRows = [
        {
          rowKey: '1',
          cells: [
            {
              props: { colSpan: 8 },
              title: <KogitoSpinner spinnerText={'Loading jobs list...'} />
            }
          ]
        }
      ];
      setRows(tempRows);
    } else {
      if (jobRow.length === 0) {
        const tempRows = [
          {
            rowKey: '1',
            cells: [
              {
                props: { colSpan: 8 },
                title: (
                  <KogitoEmptyState
                    type={KogitoEmptyStateType.Search}
                    title="No results found"
                    body="Try using different filters"
                  />
                )
              }
            ]
          }
        ];
        setRows(tempRows);
      } else {
        setRows((prev) => [...prev, ...jobRow]);
      }
    }
  };

  const handleJobDetails = (id): void => {
    const job = jobs.find((job) => job.id === id);
    setSelectedJob(job);
    handleDetailsToggle();
  };

  const handleJobReschedule = (id): void => {
    const job = jobs.find((job) => job.id === id);
    setSelectedJob(job);
    handleRescheduleToggle();
  };

  const handleCancelAction = async (id): Promise<void> => {
    const job: any = jobs.find((job) => job.id === id);
    const cancelResponse = await driver.cancelJob(job);
    const title: JSX.Element = setTitle(
      cancelResponse.modalTitle,
      'Job cancel'
    );
    setModalTitle(title);
    setModalContent(cancelResponse.modalContent);
    handleCancelModalToggle();
  };

  const dynamicActions = (rowData) => {
    if (rowData.type === 'Editable') {
      return [
        {
          title: 'Reschedule',
          onClick: (event, rowId, rowData, extra) =>
            handleJobReschedule(rowData.rowKey)
        },
        {
          title: 'Cancel',
          onClick: (event, rowId, rowData, extra) =>
            handleCancelAction(rowData.rowKey)
        }
      ];
    } else {
      return [];
    }
  };

  const actionResolver = (rowData): ActionsMeta[] => {
    const editActions = dynamicActions(rowData);
    return [
      {
        title: 'Details',
        onClick: (event, rowId, rowData, extra) =>
          handleJobDetails(rowData.rowKey)
      },
      ...editActions
    ];
  };

  const onSort = async (
    event,
    index: number,
    direction: 'asc' | 'desc'
  ): Promise<void> => {
    setSortBy({ index, direction });
    let sortingColumn: string = event.target.innerText;
    sortingColumn = _.camelCase(sortingColumn);
    const obj: JobsSortBy = {};
    constructObject(obj, sortingColumn, direction.toUpperCase());
    setOrderBy(obj);
    await driver.sortBy(obj);
    doQueryJobs(0, 10);
  };

  useEffect(() => {
    /* istanbul ignore else*/
    if (isActionPerformed) {
      const updatedRows = rows.filter((row) => {
        row.selected = false;
        return row;
      });
      setSelectedJobInstances([]);
      setRows(updatedRows);
    }
  }, [isActionPerformed]);

  useEffect(() => {
    setRows([]);
    tableContent(jobs);
  }, [isLoading, jobs]);

  return (
    <Table
      cells={columns}
      rows={rows}
      onSelect={checkNotEmpty() ? onSelect : null}
      actionResolver={checkNotEmpty() ? actionResolver : null}
      sortBy={sortBy}
      onSort={onSort}
      aria-label="Jobs management Table"
      className="kogito-jobs-management__table"
      {...componentOuiaProps(ouiaId, 'jobs-management-table', ouiaSafe)}
    >
      <TableHeader />
      <TableBody />
    </Table>
  );
};

export default JobsManagementTable;
