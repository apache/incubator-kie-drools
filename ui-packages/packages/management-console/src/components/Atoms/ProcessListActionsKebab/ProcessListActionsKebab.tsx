import React, { useState } from 'react';
import { DropdownItem, Dropdown, KebabToggle } from '@patternfly/react-core';
import { GraphQL } from '@kogito-apps/common';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import { checkProcessInstanceState } from '../../../utils/Utils';
interface IOwnProps {
  processInstance: GraphQL.ProcessInstance;
  onSkipClick: (processInstance: GraphQL.ProcessInstance) => Promise<void>;
  onRetryClick: (processInstance: GraphQL.ProcessInstance) => Promise<void>;
  onAbortClick: (processInstance: GraphQL.ProcessInstance) => Promise<void>;
}
export enum TitleType {
  SUCCESS = 'success',
  FAILURE = 'failure'
}
const ProcessListActionsKebab: React.FC<IOwnProps & OUIAProps> = ({
  processInstance,
  onSkipClick,
  onRetryClick,
  onAbortClick,
  ouiaId,
  ouiaSafe
}) => {
  const [isKebabOpen, setIsKebabOpen] = useState<boolean>(false);

  const onSelect = (): void => {
    setIsKebabOpen(!isKebabOpen);
  };

  const onToggle = (isOpen): void => {
    setIsKebabOpen(isOpen);
  };

  const dropDownList = (): JSX.Element[] => {
    if (processInstance.state === 'ERROR') {
      return [
        <DropdownItem key={1} onClick={() => onRetryClick(processInstance)}>
          Retry
        </DropdownItem>,
        <DropdownItem key={2} onClick={() => onSkipClick(processInstance)}>
          Skip
        </DropdownItem>,
        <DropdownItem key={4} onClick={() => onAbortClick(processInstance)}>
          Abort
        </DropdownItem>
      ];
    } else {
      return [
        <DropdownItem key={4} onClick={() => onAbortClick(processInstance)}>
          Abort
        </DropdownItem>
      ];
    }
  };

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
      dropdownItems={dropDownList()}
      {...componentOuiaProps(ouiaId, 'process-list-actions-kebab', ouiaSafe)}
    />
  );
};

export default ProcessListActionsKebab;
