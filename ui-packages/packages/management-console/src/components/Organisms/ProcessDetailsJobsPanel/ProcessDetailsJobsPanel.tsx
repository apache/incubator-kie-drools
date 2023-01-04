import React, { useState, useEffect } from 'react';
import {
  Card,
  CardHeader,
  Title,
  CardBody,
  Tooltip
} from '@patternfly/react-core';
import {
  Table,
  TableVariant,
  TableHeader,
  TableBody,
  IRow,
  ICell
} from '@patternfly/react-table';
import Moment from 'react-moment';
import JobActionsKebab from '../../Atoms/JobActionsKebab/JobActionsKebab';
import { GraphQL } from '@kogito-apps/common';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import { JobsIconCreator } from '../../../utils/Utils';
import { refetchContext } from '../../contexts';

interface ResponseDataType {
  Jobs?: GraphQL.Job[];
}
interface JobResponseMeta {
  data: ResponseDataType;
  loading: boolean;
  refetch: () => void;
}
interface JobsPanelProps {
  jobsResponse: JobResponseMeta;
}

const ProcessDetailsJobsPanel: React.FC<JobsPanelProps & OUIAProps> = ({
  jobsResponse,
  ouiaId,
  ouiaSafe
}) => {
  const [rows, setRows] = useState<IRow[]>([]);

  const columns: ICell[] = [
    {
      title: 'Job id'
    },
    {
      title: 'Status'
    },
    {
      title: 'Expiration time'
    },
    {
      title: 'Actions'
    }
  ];

  const createRows = (jobsArray: GraphQL.Job[]): IRow[] => {
    const jobRows = [];
    jobsArray.forEach((job) => {
      jobRows.push({
        cells: [
          {
            title: (
              <Tooltip content={job.id}>
                <span>{job.id.substring(0, 7)}</span>
              </Tooltip>
            )
          },
          {
            title: JobsIconCreator(job.status)
          },
          {
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
          },
          {
            title: (
              <refetchContext.Provider value={jobsResponse.refetch}>
                <JobActionsKebab job={job} />
              </refetchContext.Provider>
            )
          }
        ]
      });
    });
    return jobRows;
  };

  useEffect(() => {
    if (!jobsResponse.loading && jobsResponse.data) {
      setRows(createRows(jobsResponse.data.Jobs));
    }
  }, [jobsResponse.data]);

  if (
    !jobsResponse.loading &&
    jobsResponse.data &&
    jobsResponse.data.Jobs.length > 0
  ) {
    return (
      <Card
        {...componentOuiaProps(
          ouiaId,
          'process-details-jobs-panel',
          ouiaSafe ? ouiaSafe : !jobsResponse.loading
        )}
      >
        <CardHeader>
          <Title headingLevel="h3" size="xl">
            Jobs
          </Title>
        </CardHeader>
        <CardBody>
          <Table
            aria-label="Process details jobs panel"
            aria-labelledby="Process details jobs panel"
            variant={TableVariant.compact}
            rows={rows}
            cells={columns}
          >
            <TableHeader />
            <TableBody />
          </Table>
        </CardBody>
      </Card>
    );
  } else {
    return null;
  }
};

export default ProcessDetailsJobsPanel;
