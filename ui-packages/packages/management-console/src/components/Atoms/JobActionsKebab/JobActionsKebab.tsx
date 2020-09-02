import React, { useState } from 'react';
import { DropdownItem, Dropdown, KebabToggle } from '@patternfly/react-core';
import JobsPanelDetailsModal from '../JobsPanelDetailsModal/JobsPanelDetailsModal';
import { OUIAProps, componentOuiaProps, GraphQL } from '@kogito-apps/common';
import { setTitle } from '../../../utils/Utils';

interface JobActionsProps {
  job: GraphQL.Job;
}

const JobActionsKebab: React.FC<JobActionsProps & OUIAProps> = ({
  job,
  ouiaId,
  ouiaSafe
}) => {
  const [isKebabOpen, setIsKebabOpen] = useState<boolean>(false);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

  const onSelect = () => {
    setIsKebabOpen(!isKebabOpen);
  };

  const onToggle = isOpen => {
    setIsKebabOpen(isOpen);
  };

  const onDetailsClick = () => {
    handleModalToggle();
  };
  const dropdownItems = [
    <DropdownItem key="details" component="button" onClick={onDetailsClick}>
      Details
    </DropdownItem>
  ];

  return (
    <>
      <JobsPanelDetailsModal
        modalTitle={setTitle('success', 'Job Details')}
        isModalOpen={isModalOpen}
        handleModalToggle={handleModalToggle}
        job={job}
      />
      <Dropdown
        onSelect={onSelect}
        toggle={<KebabToggle onToggle={onToggle} id="kebab-toggle" />}
        isOpen={isKebabOpen}
        isPlain
        aria-label="Job actions dropdown"
        aria-labelledby="Job actions dropdown"
        dropdownItems={dropdownItems}
        {...componentOuiaProps(ouiaId, 'job-actions-kebab', ouiaSafe)}
      />
    </>
  );
};

export default JobActionsKebab;
