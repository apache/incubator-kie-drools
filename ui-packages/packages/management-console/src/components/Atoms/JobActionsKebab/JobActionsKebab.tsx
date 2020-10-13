import React, { useState } from 'react';
import {
  DropdownItem,
  Dropdown,
  KebabToggle,
  Button
} from '@patternfly/react-core';
import JobsPanelDetailsModal from '../JobsPanelDetailsModal/JobsPanelDetailsModal';
import JobsRescheduleModal from '../JobsRescheduleModal/JobsRescheduleModal';
import { OUIAProps, componentOuiaProps, GraphQL } from '@kogito-apps/common';
import { setTitle } from '../../../utils/Utils';
interface IOwnProps {
  job: GraphQL.Job;
}

const JobActionsKebab: React.FC<IOwnProps & OUIAProps> = ({
  job,
  ouiaId,
  ouiaSafe
}) => {
  const [isKebabOpen, setIsKebabOpen] = useState<boolean>(false);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [rescheduleClicked, setRescheduleClicked] = useState<boolean>(false);

  const RescheduleJobs: string[] = ['SCHEDULED', 'ERROR'];
  const handleModalToggle = (): void => {
    setIsModalOpen(!isModalOpen);
  };

  const onSelect = (): void => {
    setIsKebabOpen(!isKebabOpen);
  };

  const onToggle = (isOpen): void => {
    setIsKebabOpen(isOpen);
  };

  const onDetailsClick = (): void => {
    handleModalToggle();
  };

  const handleRescheduleAction = (): void => {
    setRescheduleClicked(!rescheduleClicked);
  };

  const rescheduleActions: JSX.Element[] = [
    <Button
      key="cancel-reschedule"
      variant="secondary"
      onClick={handleRescheduleAction}
    >
      Cancel
    </Button>
  ];

  const detailsAction: JSX.Element[] = [
    <Button
      key="confirm-selection"
      variant="primary"
      onClick={handleModalToggle}
    >
      OK
    </Button>
  ];

  const dropdownItems = (): JSX.Element[] => {
    if (job.endpoint !== null && RescheduleJobs.includes(job.status)) {
      return [
        <DropdownItem key="details" component="button" onClick={onDetailsClick}>
          Details
        </DropdownItem>,
        <DropdownItem
          key="reschedule"
          component="button"
          id="reschedule-option"
          onClick={handleRescheduleAction}
        >
          Reschedule
        </DropdownItem>
      ];
    } else {
      return [
        <DropdownItem key="details" component="button" onClick={onDetailsClick}>
          Details
        </DropdownItem>
      ];
    }
  };
  return (
    <>
      <JobsPanelDetailsModal
        actionType="Job Details"
        modalTitle={setTitle('success', 'Job Details')}
        isModalOpen={isModalOpen}
        handleModalToggle={handleModalToggle}
        modalAction={detailsAction}
        job={job}
      />
      <JobsRescheduleModal
        actionType="Job Reschedule"
        modalTitle={setTitle('success', 'Job Reschedule')}
        isModalOpen={rescheduleClicked}
        handleModalToggle={handleRescheduleAction}
        modalAction={rescheduleActions}
        job={job}
        setRescheduleClicked={setRescheduleClicked}
        rescheduleClicked={rescheduleClicked}
      />
      <Dropdown
        onSelect={onSelect}
        toggle={<KebabToggle onToggle={onToggle} id="kebab-toggle" />}
        isOpen={isKebabOpen}
        isPlain
        position="right"
        aria-label="Job actions dropdown"
        aria-labelledby="Job actions dropdown"
        dropdownItems={dropdownItems()}
        {...componentOuiaProps(ouiaId, 'job-actions-kebab', ouiaSafe)}
      />
    </>
  );
};

export default JobActionsKebab;
