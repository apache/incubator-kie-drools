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

import React, { useState } from 'react';
import { Button, CardTitle, Divider } from '@patternfly/react-core';
import { ISortBy } from '@patternfly/react-table';
import {
  ServerErrors,
  KogitoEmptyState,
  KogitoEmptyStateType,
  LoadMore
} from '@kogito-apps/components-common';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import {
  JobsCancelModal,
  JobsDetailsModal,
  JobsRescheduleModal,
  setTitle,
  BulkListType,
  IOperationResults,
  IOperations,
  OperationType,
  formatForBulkListJob,
  Job,
  JobStatus,
  JobsSortBy,
  OrderBy
} from '@kogito-apps/management-console-shared';
import { JobsManagementDriver } from '../../../api';
import JobsManagementTable from '../JobsManagementTable/JobsManagementTable';
import JobsManagementToolbar from '../JobsManagementToolbar/JobsManagementToolbar';
import '../styles.css';

interface JobsManagementProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: JobsManagementDriver;
}

const JobsManagement: React.FC<JobsManagementProps & OUIAProps> = ({
  ouiaId,
  ouiaSafe,
  driver,
  isEnvelopeConnectedToChannel
}) => {
  const defaultPageSize: number = 10;
  const defaultStatus: JobStatus[] = [JobStatus.Scheduled];
  const defaultChip: JobStatus[] = [JobStatus.Scheduled];
  const defaultSortBy: ISortBy = { index: 6, direction: 'asc' };
  const defaultOrderBy: JobsSortBy = {
    lastUpdate: OrderBy.ASC
  };
  const [chips, setChips] = useState(defaultChip);
  const [selectedStatus, setSelectedStatus] =
    useState<JobStatus[]>(defaultStatus);
  const [selectedJobInstances, setSelectedJobInstances] = useState([]);
  const [jobs, setJobs] = useState<Job[]>([]);
  const [displayTable, setDisplayTable] = useState(true);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState('');
  const [isActionPerformed, setIsActionPerformed] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<JSX.Element>(null);
  const [modalContent, setModalContent] = useState<string>('');
  const [sortBy, setSortBy] = useState<ISortBy>(defaultSortBy);
  const [orderBy, setOrderBy] = useState<JobsSortBy>(defaultOrderBy);
  const [limit, setLimit] = useState<number>(defaultPageSize);
  const [offset, setOffset] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(defaultPageSize);
  const [isLoadingMore, setIsLoadingMore] = useState<boolean>(false);
  const [isCancelModalOpen, setIsCancelModalOpen] = useState<boolean>(false);
  const [isDetailsModalOpen, setIsDetailsModalOpen] = useState<boolean>(false);
  const [isRescheduleModalOpen, setIsRescheduleModalOpen] =
    useState<boolean>(false);
  const [rescheduleError, setRescheduleError] = useState<string>('');
  const [selectedJob, setSelectedJob] = useState<any>({});
  const [jobOperationResults, setJobOperationResults] =
    useState<IOperationResults>({
      CANCEL: {
        successItems: [],
        failedItems: [],
        ignoredItems: []
      }
    });

  const onRefresh = async (): Promise<void> => {
    setIsLoading(true);
    await driver.initialLoad(selectedStatus, orderBy);
    setSortBy(defaultSortBy);
    setOffset(0);
    doQueryJobs(0, 10);
  };

  const initLoad = async (): Promise<void> => {
    const defaultState: any = {
      filters: ['SCHEDULED'],
      sortBy: { lastUpdate: 'ASC' }
    };
    setIsLoading(true);
    await driver.initialLoad(defaultState.filters, defaultState.sortBy);
    doQueryJobs(0, 10);
  };

  const doQueryJobs = async (
    _offset: number,
    _limit: number
  ): Promise<void> => {
    try {
      const jobsResponse: Job[] = await driver.query(_offset, _limit);
      setIsLoading(false);
      setLimit(jobsResponse.length);
      if (_offset > 0 && jobs.length > 0) {
        setIsLoadingMore(false);
        const tempData: Job[] = jobs.concat(jobsResponse);
        setJobs(tempData);
      } else {
        setJobs(jobsResponse);
      }
    } catch (err) {
      setError(err);
    }
  };

  React.useEffect(() => {
    if (isEnvelopeConnectedToChannel) {
      initLoad();
    }
  }, [isEnvelopeConnectedToChannel]);

  const handleCancelModalToggle = (): void => {
    setIsCancelModalOpen(!isCancelModalOpen);
  };

  const handleCancelModalCloseToggle = (): void => {
    setIsCancelModalOpen(!isCancelModalOpen);
    doQueryJobs(0, 10);
  };

  const handleDetailsToggle = (): void => {
    setIsDetailsModalOpen(!isDetailsModalOpen);
  };

  const handleRescheduleToggle = (): void => {
    setIsRescheduleModalOpen(!isRescheduleModalOpen);
  };

  const onGetMoreInstances = async (
    initVal: number,
    _pageSize: number
  ): Promise<void> => {
    setIsLoadingMore(true);
    setOffset(initVal);
    setPageSize(_pageSize);
    await driver.initialLoad(selectedStatus, orderBy);
    doQueryJobs(initVal, _pageSize);
  };

  const handleBulkCancel = (cancelResults, ignoredJobs): void => {
    setIsActionPerformed(true);
    setModalTitle(setTitle('success', 'Job Cancel'));
    setModalContent('');
    setJobOperationResults({
      ...jobOperationResults,
      [OperationType.CANCEL]: {
        ...jobOperationResults[OperationType.CANCEL],
        successItems: formatForBulkListJob(cancelResults.successJobs),
        failedItems: formatForBulkListJob(cancelResults.failedJobs),
        ignoredItems: formatForBulkListJob(ignoredJobs)
      }
    });
    handleCancelModalToggle();
  };

  const jobOperations: IOperations = {
    CANCEL: {
      type: BulkListType.JOB,
      results: jobOperationResults[OperationType.CANCEL],
      messages: {
        successMessage: 'Canceled jobs: ',
        noItemsMessage: 'No jobs were canceled',
        warningMessage:
          'Note: The job status has been updated. The list may appear inconsistent until you refresh any applied filters.',
        ignoredMessage:
          'These jobs were ignored because they were already canceled or executed.'
      },
      functions: {
        perform: async () => {
          const ignoredJobs = [];
          const remainingInstances = selectedJobInstances.filter((job) => {
            if (
              job.status === JobStatus.Canceled ||
              job.status === JobStatus.Executed
            ) {
              ignoredJobs.push(job);
            } else {
              return true;
            }
          });
          const cancelResults = await driver.bulkCancel(remainingInstances);
          handleBulkCancel(cancelResults, ignoredJobs);
        }
      }
    }
  };

  const detailsAction: JSX.Element[] = [
    <Button
      key="confirm-selection"
      variant="primary"
      onClick={handleDetailsToggle}
    >
      OK
    </Button>
  ];

  const rescheduleActions: JSX.Element[] = [
    <Button
      key="cancel-reschedule"
      variant="secondary"
      onClick={handleRescheduleToggle}
    >
      Cancel
    </Button>
  ];

  const onResetToDefault = (): void => {
    setSelectedStatus(defaultStatus);
    setChips(defaultChip);
    setDisplayTable(true);
    initLoad();
  };

  const handleJobReschedule = async (
    job,
    repeatInterval,
    repeatLimit,
    scheduleDate
  ) => {
    const response = await driver.rescheduleJob(
      job,
      repeatInterval,
      repeatLimit,
      scheduleDate
    );
    if (response && response.modalTitle === 'success') {
      handleRescheduleToggle();
      setIsLoading(true);
      doQueryJobs(0, 10);
    } else if (response && response.modalTitle === 'failure') {
      handleRescheduleToggle();
      setRescheduleError(response.modalContent);
      setIsLoading(true);
      doQueryJobs(0, 10);
    }
  };
  return (
    <div
      {...componentOuiaProps(
        ouiaId,
        'JobsManagementPage',
        ouiaSafe ? ouiaSafe : !isLoading
      )}
    >
      {error.length === 0 ? (
        <>
          <CardTitle>
            <JobsManagementToolbar
              chips={chips}
              onResetToDefault={onResetToDefault}
              driver={driver}
              doQueryJobs={doQueryJobs}
              jobOperations={jobOperations}
              onRefresh={onRefresh}
              selectedStatus={selectedStatus}
              selectedJobInstances={selectedJobInstances}
              setChips={setChips}
              setDisplayTable={setDisplayTable}
              setIsLoading={setIsLoading}
              setSelectedJobInstances={setSelectedJobInstances}
              setSelectedStatus={setSelectedStatus}
            />
          </CardTitle>
          <Divider />
          {isEnvelopeConnectedToChannel && displayTable ? (
            <JobsManagementTable
              jobs={jobs}
              driver={driver}
              doQueryJobs={doQueryJobs}
              handleCancelModalToggle={handleCancelModalToggle}
              handleDetailsToggle={handleDetailsToggle}
              handleRescheduleToggle={handleRescheduleToggle}
              isActionPerformed={isActionPerformed}
              isLoading={isLoadingMore ? false : isLoading}
              setIsActionPerformed={setIsActionPerformed}
              selectedJobInstances={selectedJobInstances}
              setModalTitle={setModalTitle}
              setModalContent={setModalContent}
              setSelectedJobInstances={setSelectedJobInstances}
              setSelectedJob={setSelectedJob}
              setSortBy={setSortBy}
              sortBy={sortBy}
              setOrderBy={setOrderBy}
            />
          ) : (
            <>
              {selectedStatus.length === 0 && (
                <div className="kogito-jobs-management__emptyState">
                  <KogitoEmptyState
                    type={KogitoEmptyStateType.Reset}
                    title="No filter applied."
                    body="Try applying at least one filter to see results"
                    onClick={() => onResetToDefault()}
                  />
                </div>
              )}
            </>
          )}
          {isEnvelopeConnectedToChannel &&
            (!isLoading || isLoadingMore) &&
            (limit === pageSize || isLoadingMore) && (
              <LoadMore
                offset={offset}
                setOffset={setOffset}
                getMoreItems={onGetMoreInstances}
                pageSize={pageSize}
                isLoadingMore={isLoadingMore}
              />
            )}
        </>
      ) : (
        <ServerErrors error={error} variant="large" />
      )}
      {selectedJob && Object.keys(selectedJob).length > 0 && (
        <JobsDetailsModal
          actionType="Job Details"
          modalTitle={setTitle('success', 'Job Details')}
          isModalOpen={isDetailsModalOpen}
          handleModalToggle={handleDetailsToggle}
          modalAction={detailsAction}
          job={selectedJob}
        />
      )}
      {selectedJob && Object.keys(selectedJob).length > 0 && (
        <JobsRescheduleModal
          actionType="Job Reschedule"
          isModalOpen={isRescheduleModalOpen}
          handleModalToggle={handleRescheduleToggle}
          modalAction={rescheduleActions}
          job={selectedJob}
          rescheduleError={rescheduleError}
          setRescheduleError={setRescheduleError}
          handleJobReschedule={handleJobReschedule}
        />
      )}
      <JobsCancelModal
        actionType="Job Cancel"
        isModalOpen={isCancelModalOpen}
        handleModalToggle={handleCancelModalCloseToggle}
        modalTitle={modalTitle}
        modalContent={modalContent}
        jobOperations={jobOperations[OperationType.CANCEL]}
      />
    </div>
  );
};

export default JobsManagement;
