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
import React, { useMemo, useState } from 'react';
import {
  Dropdown,
  DropdownItem,
  KebabToggle
} from '@patternfly/react-core/dist/js/components/Dropdown';
import {
  ProcessInstance,
  ProcessInstanceState
} from '@kogito-apps/management-console-shared';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import { checkProcessInstanceState } from '../utils/ProcessListUtils';

interface ProcessListActionsKebabProps {
  processInstance: ProcessInstance;
  onSkipClick: (processInstance: ProcessInstance) => Promise<void>;
  onRetryClick: (processInstance: ProcessInstance) => Promise<void>;
  onAbortClick: (processInstance: ProcessInstance) => Promise<void>;
  onOpenTriggerCloudEvent?: (processInstance: ProcessInstance) => void;
}

const ProcessListActionsKebab: React.FC<
  ProcessListActionsKebabProps & OUIAProps
> = ({
  processInstance,
  onSkipClick,
  onRetryClick,
  onAbortClick,
  onOpenTriggerCloudEvent,
  ouiaId,
  ouiaSafe
}) => {
  const [isKebabOpen, setIsKebabOpen] = useState<boolean>(false);

  const onSelect = (): void => {
    setIsKebabOpen(!isKebabOpen);
  };

  const onToggle = (isOpen: boolean): void => {
    setIsKebabOpen(isOpen);
  };

  const dropDownList = useMemo(() => {
    const result: JSX.Element[] = [];

    if (processInstance.state === ProcessInstanceState.Error) {
      result.push(
        <DropdownItem
          key={'Retry'}
          onClick={() => onRetryClick(processInstance)}
        >
          Retry
        </DropdownItem>
      );
      result.push(
        <DropdownItem key={'Skip'} onClick={() => onSkipClick(processInstance)}>
          Skip
        </DropdownItem>
      );
    }

    if (onOpenTriggerCloudEvent) {
      result.push(
        <DropdownItem
          key={'CloudEvent'}
          onClick={() => onOpenTriggerCloudEvent(processInstance)}
        >
          Send Cloud Event
        </DropdownItem>
      );
    }
    result.push(
      <DropdownItem key={'Abort'} onClick={() => onAbortClick(processInstance)}>
        Abort
      </DropdownItem>
    );

    return result;
  }, [
    processInstance,
    onSkipClick,
    onRetryClick,
    onAbortClick,
    onOpenTriggerCloudEvent
  ]);

  return (
    <Dropdown
      onSelect={onSelect}
      toggle={
        <KebabToggle
          isDisabled={checkProcessInstanceState(processInstance)}
          onToggle={onToggle}
          id="kebab-toggle"
        />
      }
      isOpen={isKebabOpen}
      isPlain
      position="right"
      aria-label="process instance actions dropdown"
      aria-labelledby="process instance actions dropdown"
      dropdownItems={dropDownList}
      {...componentOuiaProps(ouiaId, 'process-list-actions-kebab', ouiaSafe)}
    />
  );
};

export default ProcessListActionsKebab;
