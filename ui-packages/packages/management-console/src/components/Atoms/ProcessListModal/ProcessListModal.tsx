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
interface IOwnProps {
  modalTitle: JSX.Element;
  modalContent?: string;
  handleModalToggle: () => void;
  isModalOpen: boolean;
  resetSelected?: () => void;
  operationResult?: IOperation;
  processName?: string;
}
const ProcessListModal: React.FC<IOwnProps> = ({
  modalContent,
  modalTitle,
  isModalOpen,
  handleModalToggle,
  resetSelected,
  operationResult,
  processName
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
      actions={[
        <Button key="confirm-selection" variant="primary" onClick={onOkClick}>
          OK
        </Button>
      ]}
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
