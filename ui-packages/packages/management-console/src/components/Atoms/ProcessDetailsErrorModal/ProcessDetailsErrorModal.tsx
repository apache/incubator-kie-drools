import React from 'react';
import {
  Modal,
  ModalVariant,
  ModalBoxBody,
  TextContent,
  Text
} from '@patternfly/react-core';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/common';

interface IOwnProps {
  errorString: string;
  errorModalOpen: boolean;
  errorModalAction: JSX.Element[];
  handleErrorModal: () => void;
  label: string;
  title: JSX.Element;
}
const ProcessDetailsErrorModal: React.FC<IOwnProps & OUIAProps> = ({
  errorString,
  errorModalOpen,
  errorModalAction,
  handleErrorModal,
  label,
  title,
  ouiaId,
  ouiaSafe
}) => {
  const errorModalContent = (): JSX.Element => {
    return (
      <ModalBoxBody>
        <TextContent>
          <Text>{errorString}</Text>
        </TextContent>
      </ModalBoxBody>
    );
  };

  return (
    <Modal
      variant={ModalVariant.small}
      aria-labelledby={label}
      aria-label={label}
      title=""
      header={title}
      isOpen={errorModalOpen}
      onClose={handleErrorModal}
      actions={errorModalAction}
      {...componentOuiaProps(ouiaId, 'process-details-error-modal', ouiaSafe)}
    >
      {errorModalContent()}
    </Modal>
  );
};

export default ProcessDetailsErrorModal;
