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
import { OUIAProps, componentOuiaProps, GraphQL } from '@kogito-apps/common';
import { JobsIconCreator } from '../../../utils/Utils';
interface JobsPanelProps {
  processInstanceId: string;
}

const ProcessDetailsJobsPanel: React.FC<JobsPanelProps & OUIAProps> = ({
  processInstanceId,
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

  const { data, loading } = GraphQL.useGetJobsByProcessInstanceIdQuery({
    variables: {
      processInstanceId
    }
  });

  const createRows = (jobsArray: GraphQL.Job[]): IRow[] => {
    const jobRows = [];
    jobsArray.forEach(job => {
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
            title: <JobActionsKebab job={job} />
          }
        ]
      });
    });
    return jobRows;
  };

  useEffect(() => {
    if (!loading && data) {
      setRows(createRows(data.Jobs));
    }
  }, [data]);

  if (!loading && data && data.Jobs.length > 0) {
    return (
      <Card
        {...componentOuiaProps(
          ouiaId,
          'process-details-jobs-panel',
          ouiaSafe ? ouiaSafe : !loading
        )}
      >
        <CardHeader>
          <Title headingLevel="h3" size="xl">
            Jobs Panel
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
