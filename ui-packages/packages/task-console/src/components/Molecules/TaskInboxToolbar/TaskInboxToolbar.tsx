import React, { useState, useContext, useReducer, useEffect } from 'react';
import {
  Button,
  Select,
  SelectOption,
  SelectVariant,
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
import _ from 'lodash';
import TaskConsoleContext, {
  IContext
} from '../../../context/TaskConsoleContext/TaskConsoleContext';
import { GraphQL, componentOuiaProps, OUIAProps } from '@kogito-apps/common';
import { getAllTaskStates } from '../../../util/Utils';

interface ITaskInboxFilterProps {
  applyFilter: () => void;
  resetFilter: () => void;
}
enum Category {
  STATUS = 'Status',
  TASK_NAME = 'Task name'
}

const TaskInboxToolbar: React.FC<ITaskInboxFilterProps & OUIAProps> = ({
  applyFilter,
  resetFilter,
  ouiaSafe,
  ouiaId
}) => {
  const context: IContext<GraphQL.UserTaskInstance> = useContext(
    TaskConsoleContext
  );
  const [, forceUpdate] = useReducer(x => x + 1, 0);
  const [isExpanded, setIsExpanded] = useState(false);
  const [searchText, setSearchText] = useState<string>('');

  useEffect(() => {
    context.getActiveFilters().selectedStatus = [
      ...context.getActiveFilters().filters.status
    ];
    forceUpdate();
  }, []);

  const onFilterClick = () => {
    context.getActiveFilters().filters.status = [
      ...context.getActiveFilters().selectedStatus
    ];
    if (
      !context.getActiveFilters().filters.taskNames.includes(searchText) &&
      searchText.length > 0
    ) {
      context.getActiveFilters().filters.taskNames = [
        ...context.getActiveFilters().filters.taskNames,
        searchText
      ];
    }
    setSearchText('');
    applyFilter();
  };

  const onSelect = (event: React.MouseEvent, selection: string): void => {
    const selectedStatus = context.getActiveFilters().selectedStatus;
    if (!selectedStatus.includes(selection)) {
      selectedStatus.push(selection);
    } else {
      _.remove(selectedStatus, (status: string) => {
        return status === selection;
      });
    }

    forceUpdate();
  };

  const onDelete = (categoryName: Category, value: string): void => {
    switch (categoryName) {
      case Category.STATUS: {
        const statusArray = [...context.getActiveFilters().filters.status];
        _.remove(statusArray, (status: string) => {
          return status === value;
        });
        context.getActiveFilters().filters.status = [...statusArray];
        context.getActiveFilters().selectedStatus = [...statusArray];
        break;
      }
      case Category.TASK_NAME: {
        const taskNames = [...context.getActiveFilters().filters.taskNames];
        _.remove(taskNames, (taskName: string) => {
          return taskName === value;
        });
        context.getActiveFilters().filters.taskNames = [...taskNames];
        break;
      }
    }
    applyFilter();
  };

  const onRefreshClick = (): void => {
    applyFilter();
  };

  const handleTextBoxChange = (text: string): void => {
    setSearchText(text);
    if (text === '') {
      setSearchText('');
      return;
    }
  };

  const handleEnterClick = (event: React.KeyboardEvent): void => {
    if (event.key === 'Enter') {
      onFilterClick();
    }
  };

  const onStatusToggle = (isExpandedItem: boolean): void => {
    setIsExpanded(isExpandedItem);
  };

  const createStatusMenuItems = () => {
    return getAllTaskStates().map(state => (
      <SelectOption key={state} value={state} />
    ));
  };

  const toggleGroupItems = (
    <React.Fragment>
      <ToolbarGroup variant="filter-group">
        <ToolbarFilter
          chips={context.getActiveFilters().filters.status}
          deleteChip={onDelete}
          categoryName={Category.STATUS}
        >
          <Select
            variant={SelectVariant.checkbox}
            aria-label="Status"
            onToggle={onStatusToggle}
            onSelect={onSelect}
            selections={context.getActiveFilters().selectedStatus}
            isOpen={isExpanded}
            placeholderText="Status"
          >
            {createStatusMenuItems()}
          </Select>
        </ToolbarFilter>
        <ToolbarFilter
          chips={context.getActiveFilters().filters.taskNames}
          deleteChip={onDelete}
          categoryName={Category.TASK_NAME}
        >
          <InputGroup>
            <TextInput
              name="taskName"
              id="taskName"
              type="search"
              aria-label="task name"
              onChange={handleTextBoxChange}
              onKeyPress={handleEnterClick}
              placeholder="Filter by name"
              value={searchText}
            />
          </InputGroup>
        </ToolbarFilter>
        <ToolbarItem>
          <Button
            id="apply-filter"
            variant="primary"
            onClick={onFilterClick}
            isDisabled={
              context.getActiveFilters().selectedStatus.length === 0 &&
              searchText.length === 0
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
            <Button variant="plain" onClick={onRefreshClick} id="refresh">
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
      clearAllFilters={resetFilter}
      clearFiltersButtonText="Reset to default"
      {...componentOuiaProps(ouiaId, 'task-inbox-toolbar', ouiaSafe)}
    >
      <ToolbarContent>{toolbarItems}</ToolbarContent>
    </Toolbar>
  );
};

export default TaskInboxToolbar;
