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

import React, { useEffect, useState } from 'react';
import {
  ToolbarFilter,
  ToolbarGroup,
  ToolbarItem,
  ToolbarToggleGroup,
  Toolbar,
  ToolbarContent
} from '@patternfly/react-core/dist/js/components/Toolbar';
import {
  Select,
  SelectOption,
  SelectVariant
} from '@patternfly/react-core/dist/js/components/Select';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { Tooltip } from '@patternfly/react-core/dist/js/components/Tooltip';
import { InputGroup } from '@patternfly/react-core/dist/js/components/InputGroup';
import { TextInput } from '@patternfly/react-core/dist/js/components/TextInput';
import { FilterIcon } from '@patternfly/react-icons/dist/js/icons/filter-icon';
import { SyncIcon } from '@patternfly/react-icons/dist/js/icons/sync-icon';
import _ from 'lodash';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { QueryFilter } from '../../../api';

interface TaskInboxToolbarProps {
  activeFilter: QueryFilter;
  allTaskStates: string[];
  activeTaskStates: string[];
  applyFilter: (filter: QueryFilter) => void;
  refresh: () => void;
}

enum Category {
  STATUS = 'Status',
  TASK_NAME = 'Task name'
}

const TaskInboxToolbar: React.FC<TaskInboxToolbarProps & OUIAProps> = ({
  activeFilter,
  allTaskStates,
  activeTaskStates,
  applyFilter,
  refresh,
  ouiaSafe,
  ouiaId
}) => {
  const [isStatusExpanded, setStatusExpanded] = useState(false);

  const [allStates, setAllStates] = useState<string[]>([]);
  const [activeStates, setActiveStates] = useState<string[]>([]);

  // filters currently applied
  const [filterTaskStates, setFilterTaskStates] = useState<string[]>([]);
  const [filterTaskNames, setFilterTaskNames] = useState<string[]>([]);

  // filters not applied yet
  const [selectedTaskStates, setSelectedTaskStates] = useState<string[]>([]);
  const [taskNameInput, setTaskNameInput] = useState<string>('');

  useEffect(() => {
    setAllStates(allTaskStates);
    setActiveStates(activeTaskStates);
    setSelectedTaskStates(activeFilter.taskStates);
    setFilterTaskStates(activeFilter.taskStates);
    setFilterTaskNames(activeFilter.taskNames);
  }, [activeFilter]);

  const createStatusMenuItems = () => {
    return allStates.map((state) => <SelectOption key={state} value={state} />);
  };

  const doResetFilter = () => {
    applyFilter({
      taskStates: activeStates,
      taskNames: []
    });
  };

  const onDeleteFilterGroup = (categoryName: Category, value: string): void => {
    const newFilterTaskStates = [...filterTaskStates];
    const newFilterTaskNames = [...filterTaskNames];

    switch (categoryName) {
      case Category.STATUS:
        _.remove(newFilterTaskStates, (status: string) => {
          return status === value;
        });
        setFilterTaskStates(newFilterTaskStates);
        setSelectedTaskStates(newFilterTaskStates);
        break;
      case Category.TASK_NAME:
        _.remove(newFilterTaskNames, (status: string) => {
          return status === value;
        });
        setFilterTaskNames(newFilterTaskNames);
        break;
    }
    applyFilter({
      taskNames: newFilterTaskNames,
      taskStates: newFilterTaskStates
    });
  };

  const onSelectTaskState = (
    event: React.MouseEvent,
    selection: string
  ): void => {
    const filter: string[] = [...selectedTaskStates];

    if (!filter.includes(selection)) {
      filter.push(selection);
    } else {
      _.remove(filter, (status: string) => {
        return status === selection;
      });
    }
    setSelectedTaskStates(filter);
  };

  const doApplyFilter = () => {
    const newTaskNames = [...filterTaskNames];
    if (taskNameInput && !newTaskNames.includes(taskNameInput)) {
      newTaskNames.push(taskNameInput);
      setFilterTaskNames(newTaskNames);
    }
    setFilterTaskStates([...selectedTaskStates]);
    setTaskNameInput('');
    applyFilter({
      taskStates: [...selectedTaskStates],
      taskNames: newTaskNames
    });
  };

  const toggleGroupItems = (
    <React.Fragment>
      <ToolbarGroup variant="filter-group">
        <ToolbarFilter
          chips={filterTaskStates}
          deleteChip={onDeleteFilterGroup}
          categoryName={Category.STATUS}
        >
          <Select
            variant={SelectVariant.checkbox}
            aria-label="Status"
            onToggle={setStatusExpanded}
            onSelect={onSelectTaskState}
            selections={selectedTaskStates}
            isOpen={isStatusExpanded}
            placeholderText="Status"
          >
            {createStatusMenuItems()}
          </Select>
        </ToolbarFilter>
        <ToolbarFilter
          chips={filterTaskNames}
          deleteChip={onDeleteFilterGroup}
          categoryName={Category.TASK_NAME}
        >
          <InputGroup>
            <TextInput
              name="taskName"
              id="taskName"
              type="search"
              aria-label="task name"
              onChange={setTaskNameInput}
              placeholder="Filter by Task name"
              value={taskNameInput}
            />
          </InputGroup>
        </ToolbarFilter>
        <ToolbarItem>
          <Button
            id="apply-filter"
            variant="primary"
            onClick={doApplyFilter}
            isDisabled={
              _.isEmpty(selectedTaskStates) && _.isEmpty(taskNameInput)
            }
          >
            Apply Filter
          </Button>
        </ToolbarItem>
      </ToolbarGroup>
    </React.Fragment>
  );

  const toolbarItems = (
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
    <Toolbar
      id="task-inbox-with-filter"
      className="pf-m-toggle-group-container"
      collapseListedFiltersBreakpoint="xl"
      clearAllFilters={doResetFilter}
      clearFiltersButtonText="Reset to default"
      {...componentOuiaProps(ouiaId, 'task-inbox-toolbar', ouiaSafe)}
    >
      <ToolbarContent>{toolbarItems}</ToolbarContent>
    </Toolbar>
  );
};

export default TaskInboxToolbar;
