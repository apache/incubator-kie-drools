import React from 'react';
import { Modal, Button, TextContent, Text } from '@patternfly/react-core';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/common';
interface IOwnProps {
  actionType: string;
  modalTitle: JSX.Element;
  modalContent: string;
  handleModalToggle: () => void;
  isModalOpen: boolean;
}
const JobsCancelModal: React.FC<IOwnProps & OUIAProps> = ({
  actionType,
  modalContent,
  modalTitle,
  isModalOpen,
  handleModalToggle,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <Modal
      variant="small"
      title=""
      header={modalTitle}
      isOpen={isModalOpen}
      onClose={handleModalToggle}
      aria-label={`${actionType} Modal`}
      aria-labelledby={`${actionType} Modal`}
      actions={[
        <Button
          key="confirm-selection"
          variant="primary"
          onClick={handleModalToggle}
        >
          OK
        </Button>
      ]}
      {...componentOuiaProps(ouiaId, 'jobs-cancel-modal', ouiaSafe)}
    >
      <TextContent>
        <Text>{modalContent}</Text>
      </TextContent>
    </Modal>
  );
};
export default JobsCancelModal;
