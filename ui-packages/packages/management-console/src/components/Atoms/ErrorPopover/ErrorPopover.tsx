import React from 'react';
import { Popover, Button } from '@patternfly/react-core';
import { stateIconCreator } from '../../../utils/Utils';
import { GraphQL, OUIAProps, componentOuiaProps } from '@kogito-apps/common';
import ProcessInstance = GraphQL.ProcessInstance;

interface IOwnProps {
  processInstanceData: ProcessInstance;
  onSkipClick: () => void;
  onRetryClick: () => void;
}
const ErrorPopover: React.FC<IOwnProps & OUIAProps> = ({
  processInstanceData,
  onSkipClick,
  onRetryClick,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <Popover
      zIndex={300}
      id={processInstanceData.id}
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
            id="skip-button"
            variant="secondary"
            onClick={onSkipClick}
            className="pf-u-mr-sm"
          >
            Skip
          </Button>,
          <Button
            key="confirm2"
            variant="secondary"
            id="retry-button"
            onClick={onRetryClick}
            className="pf-u-mr-sm"
          >
            Retry
          </Button>
        ]
      }
      position="auto"
      {...componentOuiaProps(ouiaId, 'error-popover', ouiaSafe)}
    >
      <Button variant="link" isInline>
        {stateIconCreator(processInstanceData.state)}
      </Button>
    </Popover>
  );
};

export default ErrorPopover;
