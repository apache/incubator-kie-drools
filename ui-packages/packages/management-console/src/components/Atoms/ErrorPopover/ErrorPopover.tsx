import React from 'react';
import { Popover, Button } from '@patternfly/react-core';
import { ProcessInstanceIconCreator } from '../../../utils/Utils';
import { GraphQL } from '@kogito-apps/common';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import ProcessInstance = GraphQL.ProcessInstance;

interface IOwnProps {
  processInstanceData: ProcessInstance;
  onSkipClick: (processInstance: GraphQL.ProcessInstance) => Promise<void>;
  onRetryClick: (processInstance: GraphQL.ProcessInstance) => Promise<void>;
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
            onClick={() => onSkipClick(processInstanceData)}
            className="pf-u-mr-sm"
          >
            Skip
          </Button>,
          <Button
            key="confirm2"
            variant="secondary"
            id="retry-button"
            onClick={() => onRetryClick(processInstanceData)}
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
        {ProcessInstanceIconCreator(processInstanceData.state)}
      </Button>
    </Popover>
  );
};

export default ErrorPopover;
