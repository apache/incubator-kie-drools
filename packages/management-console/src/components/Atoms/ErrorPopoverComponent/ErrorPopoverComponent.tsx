import React from 'react';
import { Popover, Button } from '@patternfly/react-core';
import {
  handleSkip,
  handleRetry,
  stateIconCreator
} from '../../../utils/Utils';
import { GraphQL } from '@kogito-apps/common';
import ProcessInstance = GraphQL.ProcessInstance;
interface IOwnProps {
  processInstanceData: ProcessInstance;
  setModalTitle: (modalTitle: string) => void;
  setTitleType: (titleType: string) => void;
  setModalContent: (modalContent: string) => void;
  handleRetryModalToggle: () => void;
  handleSkipModalToggle: () => void;
}
const ErrorPopover: React.FC<IOwnProps> = ({
  processInstanceData,
  setModalTitle,
  setTitleType,
  setModalContent,
  handleRetryModalToggle,
  handleSkipModalToggle
}) => {
  return (
    <Popover
      zIndex={300}
      headerContent={<div>Process error</div>}
      bodyContent={
        <div>
          {processInstanceData.error
            ? processInstanceData.error.message
            : 'No error message found'}
        </div>
      }
      footerContent={
        processInstanceData.addons.includes('process-management') &&
        processInstanceData.serviceUrl && [
          <Button
            key="confirm1"
            variant="secondary"
            onClick={() =>
              handleSkip(
                processInstanceData,
                setModalTitle,
                setTitleType,
                setModalContent,
                handleSkipModalToggle
              )
            }
            className="pf-u-mr-sm"
          >
            Skip
          </Button>,
          <Button
            key="confirm2"
            variant="secondary"
            onClick={() =>
              handleRetry(
                processInstanceData,
                setModalTitle,
                setTitleType,
                setModalContent,
                handleRetryModalToggle
              )
            }
            className="pf-u-mr-sm"
          >
            Retry
          </Button>
        ]
      }
      position="auto"
    >
      <Button variant="link" isInline>
        {stateIconCreator(processInstanceData.state)}
      </Button>
    </Popover>
  );
};

export default ErrorPopover;
