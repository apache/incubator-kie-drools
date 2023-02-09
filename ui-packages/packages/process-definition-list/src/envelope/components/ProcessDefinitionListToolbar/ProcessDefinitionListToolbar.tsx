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

import React, { useState } from 'react';
import {
  Button,
  ToolbarFilter,
  ToolbarGroup,
  ToolbarItem,
  ToolbarToggleGroup,
  Toolbar,
  ToolbarContent,
  TextInput,
  InputGroup,
  Tooltip
} from '@patternfly/react-core';
import { FilterIcon, SyncIcon } from '@patternfly/react-icons';
import remove from 'lodash/remove';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
interface ProcessDefinitionListToolbarProps {
  filterProcessNames: string[];
  setFilterProcessNames: React.Dispatch<React.SetStateAction<string[]>>;
  applyFilter: () => void;
  singularProcessLabel: string;
  onOpenTriggerCloudEvent?: () => void;
}

enum Category {
  PROCESS_NAME = 'Process name'
}

const ProcessDefinitionListToolbar: React.FC<
  ProcessDefinitionListToolbarProps & OUIAProps
> = ({
  applyFilter,
  filterProcessNames,
  setFilterProcessNames,
  singularProcessLabel,
  onOpenTriggerCloudEvent,
  ouiaSafe,
  ouiaId
}) => {
  const [processNameInput, setProcessNameInput] = useState<string>('');

  const doResetFilter = (): void => {
    applyFilter();
    setFilterProcessNames([]);
  };

  const doRefresh = (): void => {
    setFilterProcessNames([...filterProcessNames]);
    applyFilter();
  };

  const onEnterClicked = (event: React.KeyboardEvent<EventTarget>): void => {
    /* istanbul ignore else */
    if (event.key === 'Enter') {
      processNameInput.length > 0 && doApplyFilter();
    }
  };

  const onDeleteFilterGroup = (categoryName: Category, value: string): void => {
    const newfilterProcessNames = [...filterProcessNames];
    if (categoryName === Category.PROCESS_NAME) {
      remove(newfilterProcessNames, (status: string) => {
        return status === value;
      });
      setFilterProcessNames(newfilterProcessNames);
      applyFilter();
    }
  };

  const doApplyFilter = (): void => {
    const newProcessNames = [...filterProcessNames];
    if (processNameInput && !newProcessNames.includes(processNameInput)) {
      newProcessNames.push(processNameInput);
      setFilterProcessNames(newProcessNames);
    }
    setProcessNameInput('');
    applyFilter();
  };

  const toggleGroupItems: JSX.Element = (
    <React.Fragment>
      <ToolbarGroup variant="filter-group">
        <ToolbarFilter
          key="input-process-name"
          chips={filterProcessNames}
          deleteChip={onDeleteFilterGroup}
          categoryName={Category.PROCESS_NAME}
        >
          <InputGroup>
            <TextInput
              name="processName"
              id="processName"
              type="search"
              aria-label="process name"
              onChange={setProcessNameInput}
              onKeyPress={onEnterClicked}
              placeholder={`Filter by ${singularProcessLabel.toLowerCase()} name`}
              value={processNameInput}
            />
          </InputGroup>
        </ToolbarFilter>
        <ToolbarItem>
          <Button id="apply-filter" variant="primary" onClick={doApplyFilter}>
            Apply Filter
          </Button>
        </ToolbarItem>
      </ToolbarGroup>
    </React.Fragment>
  );

  const toolbarItems: JSX.Element = (
    <React.Fragment>
      <ToolbarToggleGroup toggleIcon={<FilterIcon />} breakpoint="xl">
        {toggleGroupItems}
      </ToolbarToggleGroup>
      <ToolbarGroup variant="icon-button-group">
        <ToolbarItem>
          <Tooltip content={'Refresh'}>
            <Button variant="plain" onClick={doRefresh} id="refresh">
              <SyncIcon />
            </Button>
          </Tooltip>
        </ToolbarItem>
      </ToolbarGroup>
      {onOpenTriggerCloudEvent && (
        <ToolbarGroup>
          <ToolbarItem variant="separator" />
          <ToolbarItem>
            <Button
              variant="primary"
              key={'triggerCloudEventButton'}
              onClick={() => onOpenTriggerCloudEvent()}
            >
              Trigger Cloud Event
            </Button>
          </ToolbarItem>
        </ToolbarGroup>
      )}
    </React.Fragment>
  );

  return (
    <Toolbar
      id="process-definition-list-with-filter"
      className="pf-m-toggle-group-container"
      collapseListedFiltersBreakpoint="xl"
      clearAllFilters={doResetFilter}
      clearFiltersButtonText="Reset to default"
      {...componentOuiaProps(
        ouiaId,
        'process-definition-list-toolbar',
        ouiaSafe
      )}
    >
      <ToolbarContent>{toolbarItems}</ToolbarContent>
    </Toolbar>
  );
};

export default ProcessDefinitionListToolbar;
