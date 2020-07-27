import React from 'react';
import {
  Modal,
  Title,
  TitleLevel,
  BaseSizes,
  Button,
  TextContent,
  Text
} from '@patternfly/react-core';
import ProcessListBulkInstances from '../ProcessListBulkInstances/ProcessListBulkInstances';
import { IOperation } from '../../Molecules/ProcessListToolbar/ProcessListToolbar';
interface IOwnProps {
  modalTitle: JSX.Element;
  modalContent?: string;
  handleModalToggle: () => void;
  isModalOpen: boolean;
  resetSelected?: () => void;
  operationResult?: IOperation;
}
const ProcessListModal: React.FC<IOwnProps> = ({
  modalContent,
  modalTitle,
  isModalOpen,
  handleModalToggle,
  resetSelected,
  operationResult
}) => {
  const onOkClick = () => {
    handleModalToggle();
    operationResult && resetSelected();
  };

  return (
    <Modal
      isSmall={true}
      title=""
      header={
        <Title headingLevel={TitleLevel.h1} size={BaseSizes['2xl']}>
          {modalTitle}
        </Title>
      }
      isOpen={isModalOpen}
      onClose={onOkClick}
      actions={[
        <Button key="confirm-selection" variant="primary" onClick={onOkClick}>
          OK
        </Button>
      ]}
      isFooterLeftAligned={false}
    >
      {operationResult !== undefined && (
        <ProcessListBulkInstances operationResult={operationResult} />
      )}
      <TextContent>
        <Text>
          <strong>{modalContent}</strong>
        </Text>
      </TextContent>
    </Modal>
  );
};

export default ProcessListModal;
