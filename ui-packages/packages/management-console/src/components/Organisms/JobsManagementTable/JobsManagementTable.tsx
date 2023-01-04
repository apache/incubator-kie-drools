import React, { useState, useEffect } from 'react';
import {
  Table,
  TableBody,
  TableHeader,
  IRow,
  sortable,
  ISortBy
} from '@patternfly/react-table';
import {
  GraphQL,
  constructObject,
  KogitoSpinner,
  KogitoEmptyState,
  KogitoEmptyStateType
} from '@kogito-apps/common';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import { Tooltip } from '@patternfly/react-core';
import { JobsIconCreator, jobCancel } from '../../../utils/Utils';
import Moment from 'react-moment';
import { HistoryIcon } from '@patternfly/react-icons';
import { refetchContext } from '../../contexts';
import _ from 'lodash';
import './JobsManagementTable.css';
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
interface IOwnProps {
  data: GraphQL.GetJobsWithFiltersQuery;
  handleDetailsToggle: () => void;
  handleRescheduleToggle: () => void;
  handleCancelModalToggle: () => void;
  setModalTitle: (modalTitle: JSX.Element) => void;
  setModalContent: (modalContent: string) => void;
  setOffset: (offSet: number) => void;
  setOrderBy: (order: GraphQL.JobOrderBy) => void;
  setSelectedJob: (job: GraphQL.Job) => void;
  selectedJobInstances: GraphQL.Job[];
  setSelectedJobInstances: (job: GraphQL.Job[]) => void;
  sortBy: ISortBy;
  setSortBy: (sortObj: ISortBy) => void;
  setIsActionPerformed: (isActionPerformed: boolean) => void;
  isActionPerformed: boolean;
  loading: boolean;
}

const JobsManagementTable: React.FC<IOwnProps & OUIAProps> = ({
  data,
  handleDetailsToggle,
  handleRescheduleToggle,
  handleCancelModalToggle,
  setModalTitle,
  setModalContent,
  setOffset,
  setOrderBy,
  setSelectedJob,
  setSortBy,
  selectedJobInstances,
  setSelectedJobInstances,
  sortBy,
  setIsActionPerformed,
  isActionPerformed,
  loading,
  ouiaId,
  ouiaSafe
}) => {
  const [rows, setRows] = useState<IRow[]>([]);
  useEffect(() => {
    if (isActionPerformed) {
      const updatedRows = rows.filter((row) => {
        row.selected = false;
        return row;
      });
      setSelectedJobInstances([]);
      setRows(updatedRows);
    }
  }, [isActionPerformed]);
  const columns = [
    { title: 'Id' },
    { title: 'Status' },
    { title: 'Expiration time' },
    { title: 'Retries' },
    { title: 'Execution counter' },
    { title: 'Last update' }
  ];
  const editableJobStatus: string[] = ['SCHEDULED', 'ERROR'];
  const jobRow: IRow[] = [];

  const checkNotEmpty = () => {
    if (data && data.Jobs && data.Jobs.length > 0 && !loading) {
      return true;
    } else {
      return false;
    }
  };
  columns.forEach((column) => {
    column['props'] = { className: 'pf-u-text-align-center' };
    if (checkNotEmpty() && column.title !== 'Id') {
      column['transforms'] = [sortable];
    }
  });

  const handleJobDetails = (id): void => {
    const job = data.Jobs.find((job) => job.id === id);
    setSelectedJob(job);
    handleDetailsToggle();
  };

  const handleJobReschedule = (id): void => {
    const job = data.Jobs.find((job) => job.id === id);
    setSelectedJob(job);
    handleRescheduleToggle();
  };

  const refetch = React.useContext(refetchContext);

  const handleCancelAction = async (id): Promise<void> => {
    const job = data.Jobs.find((job) => job.id === id);
    await jobCancel(job, setModalTitle, setModalContent, refetch);
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

  const tableContent = (): void => {
    !loading &&
      !_.isEmpty(data) &&
      data.Jobs.map((job) => {
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
    if (loading) {
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

  const onSelect = (event, isSelected, rowId, rowData): void => {
    setIsActionPerformed(false);
    const copyOfRows = [...rows];
    if (rowId === -1) {
      copyOfRows.forEach((row) => {
        row.selected = isSelected;
        return row;
      });
      if (selectedJobInstances.length === data.Jobs.length) {
        setSelectedJobInstances([]);
      } else if (selectedJobInstances.length < data.Jobs.length) {
        /* istanbul ignore else*/
        setSelectedJobInstances(_.cloneDeep(data.Jobs));
      }
    } else {
      if (copyOfRows[rowId]) {
        copyOfRows[rowId].selected = isSelected;
        const row = [...data.Jobs].filter(
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

  useEffect(() => {
    setRows([]);
    tableContent();
  }, [loading, data]);

  const onSort = (event, index: number, direction: 'asc' | 'desc'): void => {
    setSortBy({ index, direction });
    setOffset(0);
    let sortingColumn: string = event.target.innerText;
    sortingColumn = _.camelCase(sortingColumn);
    const obj: GraphQL.JobOrderBy = {};
    constructObject(obj, sortingColumn, direction.toUpperCase());
    setOrderBy(obj);
  };

  return (
    <Table
      onSelect={checkNotEmpty() ? onSelect : null}
      cells={columns}
      rows={rows}
      sortBy={sortBy}
      onSort={onSort}
      actionResolver={checkNotEmpty() ? actionResolver : null}
      aria-label="Jobs management Table"
      className="kogito-management-console--jobsManagement__table"
      {...componentOuiaProps(ouiaId, 'jobs-management-table', ouiaSafe)}
    >
      <TableHeader />
      <TableBody rowKey="rowKey" />
    </Table>
  );
};

export default JobsManagementTable;
