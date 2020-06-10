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

interface IOwnProps {
  modalTitle: JSX.Element;
  modalContent?: string;
  handleModalToggle: () => void;
  abortedMessageObj?: any;
  completedMessageObj?: any;
  isModalOpen: boolean;
  checkedArray: string[];
  isAbortModalOpen?: boolean;
  isSingleAbort?: any;
}
const ProcessListModal: React.FC<IOwnProps> = ({
  modalContent,
  modalTitle,
  abortedMessageObj,
  completedMessageObj,
  isModalOpen,
  checkedArray,
  handleModalToggle,
  isAbortModalOpen,
  isSingleAbort
}) => {
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
      onClose={handleModalToggle}
      actions={[
        <Button
          key="confirm-selection"
          variant="primary"
          onClick={handleModalToggle}
        >
          OK
        </Button>
      ]}
      isFooterLeftAligned={false}
    >
      {abortedMessageObj !== undefined &&
        completedMessageObj !== undefined &&
        isAbortModalOpen && (
          <ProcessListBulkInstances
            abortedMessageObj={abortedMessageObj}
            completedMessageObj={completedMessageObj}
            isSingleAbort={isSingleAbort}
            checkedArray={checkedArray}
            isAbortModalOpen={isAbortModalOpen}
          />
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
