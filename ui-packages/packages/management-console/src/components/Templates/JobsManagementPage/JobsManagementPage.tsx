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
  ToolbarItem
} from '@patternfly/react-core';
import JobsManagementTable from '../../Organisms/JobsManagementTable/JobsManagementTable';
import JobsManagementFiters from '../../Organisms/JobsManagementFilters/JobsManagementFilters';
import JobsPanelDetailsModal from '../../Atoms/JobsPanelDetailsModal/JobsPanelDetailsModal';
import JobsRescheduleModal from '../../Atoms/JobsRescheduleModal/JobsRescheduleModal';
import { refetchContext } from '../../contexts';
import { setTitle } from '../../../utils/Utils';
import JobsCancelModal from '../../Atoms/JobsCancelModal/JobsCancelModal';
import { SyncIcon } from '@patternfly/react-icons';

const JobsManagementPage: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
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

  const { loading, data, error, refetch } = GraphQL.useGetAllJobsQuery({
    fetchPolicy: 'network-only',
    notifyOnNetworkStatusChange: true,
    variables: { values }
  });

  useEffect(() => {
    return ouiaPageTypeAndObjectId('jobs-management');
  });

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
        </ToolbarContent>
      </Toolbar>
    );
  };

  if (data) {
    if (!loading && selectedStatus.length > 0 && data.Jobs.length === 0) {
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
                      setSelectedJob={setSelectedJob}
                    />
                  </refetchContext.Provider>
                  {selectedStatus.length === 0 && (
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
