import React, { useState, useEffect } from 'react';
import { Link, Redirect } from 'react-router-dom';
import {
  componentOuiaProps,
  GraphQL,
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner,
  ouiaPageTypeAndObjectId,
  OUIAProps,
  ServerErrors
} from '@kogito-apps/common';
import PageTitle from '../../Molecules/PageTitle/PageTitle';
import {
  Breadcrumb,
  BreadcrumbItem,
  Bullseye,
  Button,
  Card,
  CardBody,
  PageSection,
  Toolbar,
  ToolbarContent,
  ToolbarGroup,
  ToolbarItem,
  DropdownItem,
  OverflowMenu,
  OverflowMenuContent,
  OverflowMenuItem,
  OverflowMenuControl,
  Dropdown,
  KebabToggle
} from '@patternfly/react-core';
import { ISortBy } from '@patternfly/react-table';
import JobsManagementTable from '../../Organisms/JobsManagementTable/JobsManagementTable';
import JobsManagementFiters from '../../Organisms/JobsManagementFilters/JobsManagementFilters';
import JobsPanelDetailsModal from '../../Atoms/JobsPanelDetailsModal/JobsPanelDetailsModal';
import JobsRescheduleModal from '../../Atoms/JobsRescheduleModal/JobsRescheduleModal';
import { refetchContext } from '../../contexts';
import { setTitle, performMultipleCancel } from '../../../utils/Utils';
import JobsCancelModal from '../../Atoms/JobsCancelModal/JobsCancelModal';
import { SyncIcon } from '@patternfly/react-icons';

enum JobOperationType {
  CANCEL = 'CANCEL'
}

export interface JobsBulkList {
  [key: string]: GraphQL.Job;
}

interface IJobOperationResult {
  successJobs: JobsBulkList;
  failedJobs: JobsBulkList;
  ignoredJobs: JobsBulkList;
}
interface IJobOperationMessages {
  successMessage: string;
  warningMessage?: string;
  ignoredMessage: string;
  noJobsMessage: string;
}

interface IJobOperationFunctions {
  perform: () => void;
}

interface IJobOperationResults {
  [key: string]: IJobOperationResult;
}

export interface IJobOperation {
  results: IJobOperationResult;
  messages: IJobOperationMessages;
  functions: IJobOperationFunctions;
}

interface IOperations {
  [key: string]: IJobOperation;
}

const JobsManagementPage: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const defaultOrderBy: GraphQL.JobOrderBy = {
    lastUpdate: GraphQL.OrderBy.Asc
  };
  const defaultSortBy: ISortBy = { index: 6, direction: 'asc' };
  const defaultStatus: GraphQL.JobStatus[] = [GraphQL.JobStatus.Scheduled];
  const [isDetailsModalOpen, setIsDetailsModalOpen] = useState<boolean>(false);
  const [isRescheduleModalOpen, setIsRescheduleModalOpen] = useState<boolean>(
    false
  );
  const [isCancelModalOpen, setIsCancelModalOpen] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<JSX.Element>(null);
  const [modalContent, setModalContent] = useState<string>('');
  const [selectedJob, setSelectedJob] = useState<any>({});
  const [selectedStatus, setSelectedStatus] = useState<GraphQL.JobStatus[]>(
    defaultStatus
  );
  const [chips, setChips] = useState<GraphQL.JobStatus[]>(defaultStatus);
  const [values, setValues] = useState<GraphQL.JobStatus[]>(defaultStatus);
  const [orderBy, setOrderBy] = useState<GraphQL.JobOrderBy>(defaultOrderBy);
  const [sortBy, setSortBy] = useState<ISortBy>(defaultSortBy);
  const [isKebabOpen, setIsKebabOpen] = useState<boolean>(false);
  const [selectedJobInstances, setSelectedJobInstances] = useState<
    GraphQL.Job[]
  >([]);
  const [jobOperationResults, setJobOperationResults] = useState<
    IJobOperationResults
  >({
    CANCEL: {
      successJobs: {},
      failedJobs: {},
      ignoredJobs: {}
    }
  });

  const { loading, data, error, refetch } = GraphQL.useGetJobsWithFiltersQuery({
    fetchPolicy: 'network-only',
    notifyOnNetworkStatusChange: true,
    variables: { values, orderBy }
  });

  const jobOperations: IOperations = {
    CANCEL: {
      results: jobOperationResults[JobOperationType.CANCEL],
      messages: {
        successMessage: 'Canceled jobs: ',
        noJobsMessage: 'No jobs were canceled',
        warningMessage:
          'Note: The job status has been updated. The list may appear inconsistent until you refresh any applied filters.',
        ignoredMessage:
          'These jobs were ignored because they were already canceled or executed.'
      },
      functions: {
        perform: async () => {
          const ignoredJobs = {};
          const remainingJobs = selectedJobInstances.filter(job => {
            if (
              job.status === GraphQL.JobStatus.Canceled ||
              job.status === GraphQL.JobStatus.Executed
            ) {
              ignoredJobs[job.id] = job;
            } else {
              return true;
            }
          });
          await performMultipleCancel(
            remainingJobs,
            (successJobs, failedJobs) => {
              setModalTitle(setTitle('success', 'Job Cancel'));
              setModalContent('');
              setJobOperationResults({
                ...jobOperationResults,
                [JobOperationType.CANCEL]: {
                  ...jobOperationResults[JobOperationType.CANCEL],
                  successJobs,
                  failedJobs,
                  ignoredJobs
                }
              });
              handleCancelModalToggle();
            }
          );
        }
      }
    }
  };

  useEffect(() => {
    return ouiaPageTypeAndObjectId('jobs-management');
  });

  const jobManagementButtonSelect = () => {
    setIsKebabOpen(!isKebabOpen);
  };

  const jobManagementKebabToggle = isOpen => {
    setIsKebabOpen(isOpen);
  };

  const handleDetailsToggle = (): void => {
    setIsDetailsModalOpen(!isDetailsModalOpen);
  };

  const handleRescheduleToggle = (): void => {
    setIsRescheduleModalOpen(!isRescheduleModalOpen);
  };

  const handleCancelModalToggle = (): void => {
    setIsCancelModalOpen(!isCancelModalOpen);
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

  const onRefresh = (): void => {
    window.location.reload();
  };

  const onReset = (): void => {
    setSelectedStatus(defaultStatus);
    setChips(defaultStatus);
    setValues(defaultStatus);
  };

  const dropdownItemsJobsManagementButtons = (): JSX.Element[] => {
    return [
      <DropdownItem
        key="cancel"
        onClick={jobOperations[JobOperationType.CANCEL].functions.perform}
        isDisabled={selectedJobInstances.length === 0}
      >
        Cancel selected
      </DropdownItem>
    ];
  };

  const jobManagementButtons: JSX.Element = (
    <OverflowMenu breakpoint="xl">
      <OverflowMenuContent>
        <OverflowMenuItem>
          <Button
            variant="secondary"
            onClick={jobOperations[JobOperationType.CANCEL].functions.perform}
            isDisabled={selectedJobInstances.length === 0}
          >
            Cancel selected
          </Button>
        </OverflowMenuItem>
      </OverflowMenuContent>
      <OverflowMenuControl>
        <Dropdown
          onSelect={jobManagementButtonSelect}
          toggle={<KebabToggle onToggle={jobManagementKebabToggle} />}
          isOpen={isKebabOpen}
          isPlain
          dropdownItems={dropdownItemsJobsManagementButtons()}
        />
      </OverflowMenuControl>
    </OverflowMenu>
  );

  const renderToolbar = (): JSX.Element => {
    return (
      <Toolbar
        id="data-toolbar-with-chip-groups"
        className="pf-m-toggle-group-container"
        collapseListedFiltersBreakpoint="md"
        clearAllFilters={onReset}
        clearFiltersButtonText="Reset to default"
      >
        <ToolbarContent>
          <JobsManagementFiters
            selectedStatus={selectedStatus}
            setSelectedStatus={setSelectedStatus}
            setValues={setValues}
            chips={chips}
            setChips={setChips}
          />
          <ToolbarGroup>
            <ToolbarItem>
              <Button
                variant="plain"
                onClick={() => {
                  onRefresh();
                }}
                id="refresh-button"
                ouiaId="refresh-button"
                aria-label={'Refresh list'}
              >
                <SyncIcon />
              </Button>
            </ToolbarItem>
          </ToolbarGroup>
          <ToolbarItem variant="separator" />
          <ToolbarGroup className="pf-u-ml-md" id="jobs-management-buttons">
            {jobManagementButtons}
          </ToolbarGroup>
        </ToolbarContent>
      </Toolbar>
    );
  };

  if (data) {
    if (!loading && values.length > 0 && data.Jobs.length === 0) {
      return (
        <Redirect
          to={{
            pathname: '/NoData',
            state: {
              prev: '/ProcessInstances',
              title: 'Jobs not found',
              description: `There are no jobs associated with any process instance.`,
              buttonText: 'Go to process instance'
            }
          }}
        />
      );
    }
  }
  return (
    <div
      {...componentOuiaProps(
        ouiaId,
        'JobsManagementPage',
        ouiaSafe ? ouiaSafe : !loading
      )}
    >
      {!error ? (
        <>
          <PageSection variant="light">
            <PageTitle title="Jobs Management" />
            <Breadcrumb>
              <BreadcrumbItem>
                <Link to={'/'}>Home</Link>
              </BreadcrumbItem>
              <BreadcrumbItem isActive>Jobs</BreadcrumbItem>
            </Breadcrumb>
          </PageSection>
          <PageSection>
            {renderToolbar()}
            {!loading ? (
              <Card>
                <CardBody>
                  <refetchContext.Provider value={refetch}>
                    <JobsManagementTable
                      data={data}
                      handleDetailsToggle={handleDetailsToggle}
                      handleRescheduleToggle={handleRescheduleToggle}
                      handleCancelModalToggle={handleCancelModalToggle}
                      setModalTitle={setModalTitle}
                      setModalContent={setModalContent}
                      setOrderBy={setOrderBy}
                      setSelectedJob={setSelectedJob}
                      setSortBy={setSortBy}
                      selectedJobInstances={selectedJobInstances}
                      setSelectedJobInstances={setSelectedJobInstances}
                      sortBy={sortBy}
                    />
                  </refetchContext.Provider>
                  {chips.length === 0 && (
                    <KogitoEmptyState
                      type={KogitoEmptyStateType.Reset}
                      title="No filter applied."
                      body="Try applying at least one filter to see results"
                      onClick={() => onReset()}
                    />
                  )}
                </CardBody>
              </Card>
            ) : (
              <Card>
                <Bullseye>
                  <KogitoSpinner spinnerText="Loading jobs list..." />
                </Bullseye>
              </Card>
            )}
            {selectedJob && Object.keys(selectedJob).length > 0 && (
              <JobsPanelDetailsModal
                actionType="Job Details"
                modalTitle={setTitle('success', 'Job Details')}
                isModalOpen={isDetailsModalOpen}
                handleModalToggle={handleDetailsToggle}
                modalAction={detailsAction}
                job={selectedJob}
              />
            )}
            {selectedJob && Object.keys(selectedJob).length > 0 && (
              <refetchContext.Provider value={refetch}>
                <JobsRescheduleModal
                  actionType="Job Reschedule"
                  modalTitle={setTitle('success', 'Job Reschedule')}
                  isModalOpen={isRescheduleModalOpen}
                  handleModalToggle={handleRescheduleToggle}
                  modalAction={rescheduleActions}
                  job={selectedJob}
                  setRescheduleClicked={setIsRescheduleModalOpen}
                  rescheduleClicked={isRescheduleModalOpen}
                />
              </refetchContext.Provider>
            )}
            <JobsCancelModal
              actionType="Job Cancel"
              isModalOpen={isCancelModalOpen}
              handleModalToggle={handleCancelModalToggle}
              modalTitle={modalTitle}
              modalContent={modalContent}
              jobOperations={jobOperations[JobOperationType.CANCEL]}
            />
          </PageSection>
        </>
      ) : (
        <ServerErrors error={error} variant="large" />
      )}
    </div>
  );
};

export default JobsManagementPage;
