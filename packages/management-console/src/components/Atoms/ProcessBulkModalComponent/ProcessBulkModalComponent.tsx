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
import ProcessBulkListComponent from '../ProcessBulkListComponent/ProcessBulkListComponent';

interface IOwnProps {
  isModalLarge: any;
  modalTitle: any;
  modalContent?: string;
  handleModalToggle: any;
  abortedMessageObj?: any;
  completedMessageObj?: any;
  isModalOpen: boolean;
  checkedArray: string[];
  handleSkip?: any;
  handleRetry?: any;
  isAddonPresent?: boolean;
  isAbortModalOpen?: boolean;
  handleSkipModalToggle?: any;
  handleRetryModalToggle?: any;
  isSingleAbort?: any;
}
const Modalbox: React.FC<IOwnProps> = ({
  modalContent,
  modalTitle,
  abortedMessageObj,
  completedMessageObj,
  isModalLarge,
  isModalOpen,
  checkedArray,
  handleModalToggle,
  handleSkip,
  handleRetry,
  isAddonPresent,
  isAbortModalOpen,
  handleSkipModalToggle,
  handleRetryModalToggle,
  isSingleAbort
}) => {
  return (
    <Modal
      isLarge={isModalLarge}
      isSmall={!isModalLarge}
      title=""
      header={
        <Title headingLevel={TitleLevel.h1} size={BaseSizes['2xl']}>
          {modalTitle}
        </Title>
      }
      isOpen={isModalOpen}
      onClose={handleModalToggle}
      actions={
        isModalLarge && isAddonPresent
          ? [
              <Button
                key="confirm1"
                variant="secondary"
                onClick={() => {
                  handleSkip();
                  handleSkipModalToggle();
                }}
              >
                Skip
              </Button>,
              <Button
                key="confirm2"
                variant="secondary"
                onClick={() => {
                  handleRetry();
                  handleSkipModalToggle();
                }}
              >
                Retry
              </Button>,
              <Button
                key="confirm3"
                variant="primary"
                onClick={handleModalToggle}
              >
                Close
              </Button>
            ]
          : [
              <Button
                key="confirm3"
                variant="primary"
                onClick={handleModalToggle}
              >
                OK
              </Button>
            ]
      }
      isFooterLeftAligned={false}
    >
      {abortedMessageObj !== undefined &&
        completedMessageObj !== undefined &&
        isAbortModalOpen && (
          <ProcessBulkListComponent
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

export default Modalbox;
