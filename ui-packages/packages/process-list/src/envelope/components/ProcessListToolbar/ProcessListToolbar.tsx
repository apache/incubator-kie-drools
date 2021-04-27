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
  Toolbar,
  ToolbarItem,
  ToolbarContent,
  ToolbarFilter,
  ToolbarToggleGroup,
  ToolbarGroup,
  Button,
  Select,
  SelectOption,
  SelectVariant,
  InputGroup,
  TextInput,
  Tooltip
} from '@patternfly/react-core';
import { FilterIcon, SyncIcon } from '@patternfly/react-icons';
import _ from 'lodash';
import { ProcessInstanceState } from '@kogito-apps/management-console-shared';
import { ProcessInstanceFilter } from '../../../api';
import '../styles.css';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/components-common';

enum Category {
  STATUS = 'Status',
  BUSINESS_KEY = 'Business key'
}
interface ProcessListToolbarProps {
  filters: ProcessInstanceFilter;
  setFilters: React.Dispatch<React.SetStateAction<ProcessInstanceFilter>>;
  applyFilter: (filter: ProcessInstanceFilter) => void;
  refresh: () => void;
  processStates: ProcessInstanceState[];
  setProcessStates: React.Dispatch<
    React.SetStateAction<ProcessInstanceState[]>
  >;
}

const ProcessListToolbar: React.FC<ProcessListToolbarProps & OUIAProps> = ({
  filters,
  setFilters,
  applyFilter,
  refresh,
  processStates,
  setProcessStates,
  ouiaId,
  ouiaSafe
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(false);
  const [businessKeyInput, setBusinessKeyInput] = useState<string>('');

  const onStatusToggle = (isExpandedItem: boolean): void => {
    setIsExpanded(isExpandedItem);
  };

  const onSelect = (event, selection): void => {
    if (processStates.includes(selection)) {
      const newProcessStates = [...processStates].filter(
        state => state !== selection
      );
      setProcessStates(newProcessStates);
    } else {
      setProcessStates([...processStates, selection]);
    }
  };

  const onDeleteChip = (categoryName: Category, value: string): void => {
    const clonedProcessStates = [...processStates];
    const clonedBusinessKeyArray = [...filters.businessKey];
    switch (categoryName) {
      case Category.STATUS:
        _.remove(clonedProcessStates, (status: string) => {
          return status === value;
        });
        setProcessStates(clonedProcessStates);
        setFilters({ ...filters, status: clonedProcessStates });
        break;
      case Category.BUSINESS_KEY:
        _.remove(clonedBusinessKeyArray, (businessKey: string) => {
          return businessKey === value;
        });
        setFilters({ ...filters, businessKey: clonedBusinessKeyArray });
        break;
    }
    applyFilter({
      status: clonedProcessStates,
      businessKey: clonedBusinessKeyArray
    });
  };

  const onApplyFilter = (): void => {
    setBusinessKeyInput('');
    const clonedBusinessKeyArray = [...filters.businessKey];
    if (
      businessKeyInput &&
      !clonedBusinessKeyArray.includes(businessKeyInput)
    ) {
      clonedBusinessKeyArray.push(businessKeyInput);
    }
    setFilters({
      ...filters,
      status: processStates,
      businessKey: clonedBusinessKeyArray
    });
    applyFilter({
      status: processStates,
      businessKey: clonedBusinessKeyArray
    });
  };

  const onEnterClicked = (event: React.KeyboardEvent<EventTarget>): void => {
    if (event.key === 'Enter') {
      businessKeyInput.length > 0 && onApplyFilter();
    }
  };

  const resetAllFilters = (): void => {
    const defaultFilters = {
      status: [ProcessInstanceState.Active],
      businessKey: []
    };
    setProcessStates(defaultFilters.status);
    setFilters(defaultFilters);
    applyFilter(defaultFilters);
  };

  const statusMenuItems: JSX.Element[] = [
    <SelectOption key="ACTIVE" value="ACTIVE" />,
    <SelectOption key="COMPLETED" value="COMPLETED" />,
    <SelectOption key="ERROR" value="ERROR" />,
    <SelectOption key="ABORTED" value="ABORTED" />,
    <SelectOption key="SUSPENDED" value="SUSPENDED" />
  ];

  const toggleGroupItems: JSX.Element = (
    <React.Fragment>
      <ToolbarGroup variant="filter-group">
        <ToolbarFilter
          chips={filters.status}
          deleteChip={onDeleteChip}
          className="kogito-management-console__state-dropdown-list pf-u-mr-sm"
          categoryName="Status"
          id="datatoolbar-filter-status"
        >
          <Select
            variant={SelectVariant.checkbox}
            aria-label="Status"
            onToggle={onStatusToggle}
            onSelect={onSelect}
            selections={processStates}
            isOpen={isExpanded}
            placeholderText="Status"
            id="status-select"
          >
            {statusMenuItems}
          </Select>
        </ToolbarFilter>
        <ToolbarFilter
          chips={filters.businessKey}
          deleteChip={onDeleteChip}
          categoryName={Category.BUSINESS_KEY}
        >
          <InputGroup>
            <TextInput
              name="businessKey"
              id="businessKey"
              type="search"
              aria-label="business key"
              onChange={setBusinessKeyInput}
              onKeyPress={onEnterClicked}
              placeholder="Filter by business key"
              value={businessKeyInput}
            />
          </InputGroup>
        </ToolbarFilter>
        <ToolbarItem>
          <Button
            variant="primary"
            onClick={onApplyFilter}
            id="apply-filter-button"
          >
            Apply filter
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
            <Button variant="plain" onClick={refresh} id="refresh">
              <SyncIcon />
            </Button>
          </Tooltip>
        </ToolbarItem>
      </ToolbarGroup>
    </React.Fragment>
  );

  return (
    <>
      <Toolbar
        id="data-toolbar-with-filter"
        className="pf-m-toggle-group-container kogito-management-console__state-dropdown-list"
        collapseListedFiltersBreakpoint="xl"
        clearAllFilters={resetAllFilters}
        clearFiltersButtonText="Reset to default"
        {...componentOuiaProps(ouiaId, 'process-list-toolbar', ouiaSafe)}
      >
        <ToolbarContent>{toolbarItems}</ToolbarContent>
      </Toolbar>
    </>
  );
};

export default ProcessListToolbar;
