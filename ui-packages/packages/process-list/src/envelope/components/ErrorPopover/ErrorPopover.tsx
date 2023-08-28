/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import React from 'react';
import { Popover } from '@patternfly/react-core/dist/js/components/Popover';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { ProcessInstanceIconCreator } from '../utils/ProcessListUtils';
import { ProcessInstance } from '@kogito-apps/management-console-shared/dist/types';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';

interface ErrorPopoverProps {
  processInstanceData: ProcessInstance;
  onSkipClick: (processInstance: ProcessInstance) => Promise<void>;
  onRetryClick: (processInstance: ProcessInstance) => Promise<void>;
}
const ErrorPopover: React.FC<ErrorPopoverProps & OUIAProps> = ({
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
            data-testid="skip-button"
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
            data-testid="retry-button"
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
      <Button variant="link" isInline data-testid="error-state">
        {ProcessInstanceIconCreator(processInstanceData.state)}
      </Button>
    </Popover>
  );
};

export default ErrorPopover;
