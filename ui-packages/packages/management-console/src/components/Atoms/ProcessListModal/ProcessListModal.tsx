import React from 'react';
import { Modal, Button, TextContent, Text } from '@patternfly/react-core';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import BulkList, { IOperation } from '../BulkList/BulkList';
interface IOwnProps {
  modalTitle: JSX.Element;
  modalContent?: string;
  handleModalToggle: () => void;
  isModalOpen: boolean;
  resetSelected?: () => void;
  operationResult?: IOperation;
  processName?: string;
}
const ProcessListModal: React.FC<IOwnProps & OUIAProps> = ({
  modalContent,
  modalTitle,
  isModalOpen,
  handleModalToggle,
  resetSelected,
  operationResult,
  processName,
  ouiaId,
  ouiaSafe
}) => {
  const onOkClick = () => {
    handleModalToggle();
    operationResult && resetSelected();
  };

  const createBoldText = (text: string, shouldBeBold: string): JSX.Element => {
    if (shouldBeBold && shouldBeBold.length > 0) {
      const textArray = text.split(shouldBeBold);
      return (
        <span>
          {textArray.map((item, index) => (
            <React.Fragment key={index}>
              {item}
              {index !== textArray.length - 1 && <b>{shouldBeBold}</b>}
            </React.Fragment>
          ))}
        </span>
      );
    } else {
      return <span>{text}</span>;
    }
  };

  return (
    <Modal
      variant="small"
      title=""
      header={modalTitle}
      isOpen={isModalOpen}
      onClose={onOkClick}
      aria-label="process list modal"
      aria-labelledby="process list modal"
      actions={[
        <Button key="confirm-selection" variant="primary" onClick={onOkClick}>
          OK
        </Button>
      ]}
      {...componentOuiaProps(ouiaId, 'process-list-modal', ouiaSafe)}
    >
      {operationResult !== undefined && (
        <BulkList operationResult={operationResult} />
      )}
      <TextContent>
        <Text>{createBoldText(modalContent, processName)}</Text>
      </TextContent>
    </Modal>
  );
};

export default ProcessListModal;
