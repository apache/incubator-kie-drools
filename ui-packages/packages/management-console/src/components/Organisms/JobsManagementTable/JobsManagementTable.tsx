import React, { useState, useEffect } from 'react';
import { Table, TableBody, TableHeader, IRow } from '@patternfly/react-table';
import { OUIAProps, componentOuiaProps, GraphQL } from '@kogito-apps/common';
import { Tooltip } from '@patternfly/react-core';
import { JobsIconCreator, jobCancel, setTitle } from '../../../utils/Utils';
import Moment from 'react-moment';
import { HistoryIcon } from '@patternfly/react-icons';
import { refetchContext } from '../../contexts';

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
  data: GraphQL.GetAllJobsQuery;
  handleDetailsToggle: () => void;
  handleRescheduleToggle: () => void;
  handleCancelModalToggle: () => void;
  setModalTitle: (modalTitle: JSX.Element) => void;
  setModalContent: (modalContent: string) => void;
  setSelectedJob: (job: GraphQL.Job) => void;
}

const JobsManagementTable: React.FC<IOwnProps & OUIAProps> = ({
  data,
  handleDetailsToggle,
  handleRescheduleToggle,
  handleCancelModalToggle,
  setModalTitle,
  setModalContent,
  setSelectedJob,
  ouiaId,
  ouiaSafe
}) => {
  const [rows, setRows] = useState<IRow[]>([]);
  const editableJobStatus: string[] = ['SCHEDULED', 'ERROR'];
  const columns: string[] = ['Id', 'Status', 'Expiration time', 'Last update'];
  const jobRow: IRow[] = [];

  const handleJobDetails = (id): void => {
    const job = data.Jobs.find(job => job.id === id);
    setSelectedJob(job);
    handleDetailsToggle();
  };

  const handleJobReschedule = (id): void => {
    const job = data.Jobs.find(job => job.id === id);
    setSelectedJob(job);
    handleRescheduleToggle();
  };

  const refetch = React.useContext(refetchContext);

  const handleCancelAction = (id): void => {
    const job = data.Jobs.find(job => job.id === id);
    jobCancel(
      job,
      () => {
        setModalTitle(setTitle('success', 'Job cancel'));
        setModalContent(`The job: ${job.id} is canceled successfully`);
      },
      errorMessage => {
        setModalTitle(setTitle('failure', 'Job cancel'));
        setModalContent(
          `The job: ${job.id} failed to cancel. Error message: ${errorMessage}`
        );
      },
      refetch
    );
    handleCancelModalToggle();
  };

  const dynamicActions = rowData => {
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
              <span>{job.id}</span>
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
      }
    }
    return { tempRows, jobType };
  };

  const tableContent = (): void => {
    data.Jobs.map(job => {
      const retrievedValue = getValues(job);
      jobRow.push({
        cells: retrievedValue.tempRows,
        type: retrievedValue.jobType,
        rowKey: job.id
      });
    });
    /* istanbul ignore else */
    if (jobRow) {
      setRows(prev => [...prev, ...jobRow]);
    }
  };

  const onSelect = (): void => {
    return null;
  };

  useEffect(() => {
    tableContent();
  }, [data]);

  return (
    <>
      {rows.length > 0 && (
        <Table
          onSelect={onSelect}
          cells={columns}
          rows={rows}
          actionResolver={actionResolver}
          aria-label="Jobs management Table"
          className="kogito-common--domain-explorer__table"
          {...componentOuiaProps(ouiaId, 'jobs-management-table', ouiaSafe)}
        >
          <TableHeader />
          <TableBody rowKey="rowKey" />
        </Table>
      )}
    </>
  );
};

export default JobsManagementTable;
