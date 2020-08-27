import React from 'react';
import {
  Modal,
  Title,
  Button,
  TextContent,
  Text,
  TitleSizes
} from '@patternfly/react-core';
import ProcessListBulkInstances from '../ProcessListBulkInstances/ProcessListBulkInstances';
import { IOperation } from '../../Molecules/ProcessListToolbar/ProcessListToolbar';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/common';
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
  };

  return (
    <Modal
      variant="small"
      title=""
      header={
        <Title headingLevel="h1" size={TitleSizes['2xl']}>
          {modalTitle}
        </Title>
      }
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
        <ProcessListBulkInstances operationResult={operationResult} />
      )}
      <TextContent>
        <Text>
          {modalContent &&
            processName &&
            createBoldText(modalContent, processName)}
        </Text>
      </TextContent>
    </Modal>
  );
};

export default ProcessListModal;
