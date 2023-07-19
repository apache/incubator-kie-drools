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

import React, { useState, useEffect } from 'react';
import {
  DropdownItem,
  Dropdown,
  KebabToggle
} from '@patternfly/react-core/dist/js/components/Dropdown';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import {
  Select,
  SelectOption,
  SelectVariant
} from '@patternfly/react-core/dist/js/components/Select';
import {
  Toolbar,
  ToolbarContent,
  ToolbarFilter,
  ToolbarGroup,
  ToolbarItem
} from '@patternfly/react-core/dist/js/components/Toolbar';
import {
  OverflowMenu,
  OverflowMenuContent,
  OverflowMenuItem,
  OverflowMenuControl
} from '@patternfly/react-core/dist/js/components/OverflowMenu';
import { SyncIcon } from '@patternfly/react-icons/dist/js/icons/sync-icon';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import {
  IOperations,
  OperationType,
  JobStatus,
  Job
} from '@kogito-apps/management-console-shared';
import { JobsManagementDriver } from '../../../api';
import '../styles.css';

interface JobsManagementToolbarProps {
  chips: JobStatus[];
  driver: JobsManagementDriver;
  doQueryJobs: (offset: number, limit: number) => Promise<void>;
  jobOperations: IOperations;
  onResetToDefault: () => void;
  onRefresh: () => void;
  selectedStatus: JobStatus[];
  selectedJobInstances: Job[];
  setSelectedJobInstances: (selectedJobInstances: Job[]) => void;
  setSelectedStatus: (
    selectedStatus: ((selectedStatus: JobStatus[]) => JobStatus[]) | JobStatus[]
  ) => void;
  setChips: (chips: ((chip: JobStatus[]) => JobStatus[]) | JobStatus[]) => void;
  setDisplayTable: (displayTable: boolean) => void;
  setIsLoading: (isLoading: boolean) => void;
}
const JobsManagementToolbar: React.FC<
  JobsManagementToolbarProps & OUIAProps
> = ({
  chips,
  driver,
  doQueryJobs,
  onResetToDefault,
  jobOperations,
  onRefresh,
  selectedStatus,
  selectedJobInstances,
  setChips,
  setDisplayTable,
  setIsLoading,
  setSelectedStatus,
  setSelectedJobInstances,
  ouiaId,
  ouiaSafe
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(false);
  const [chipRemoved, setChipRemoved] = useState<boolean>(false);
  const [isKebabOpen, setIsKebabOpen] = useState<boolean>(false);

  const statusMenuItems: JSX.Element[] = [
    <SelectOption key="CANCELED" value="CANCELED" />,
    <SelectOption key="ERROR" value="ERROR" />,
    <SelectOption key="EXECUTED" value="EXECUTED" />,
    <SelectOption key="RETRY" value="RETRY" />,
    <SelectOption key="SCHEDULED" value="SCHEDULED" />
  ];

  const onStatusToggle = (): void => {
    setIsExpanded(!isExpanded);
  };

  const filterData = async (): Promise<void> => {
    await driver.applyFilter(selectedStatus);
    doQueryJobs(0, 10);
    setChipRemoved(false);
  };

  const onApplyFilter = async (): Promise<void> => {
    setChips(selectedStatus);
    setIsLoading(true);
    filterData();
  };

  const onDelete = (type: string = '', id: string = ''): void => {
    const chipsCopy = [...chips];
    const tempChips = chipsCopy.filter((item) => item !== id);
    setSelectedJobInstances([]);
    let selectedStatusCopy = [...selectedStatus];
    setChips(tempChips);
    selectedStatusCopy = selectedStatusCopy.filter((item) => item !== id);
    setSelectedStatus(selectedStatusCopy);
    if (tempChips.length > 0) {
      setIsLoading(true);
      setChipRemoved(true);
    } else {
      setDisplayTable(false);
    }
  };

  const onSelect = (event, selection: JobStatus): void => {
    let selectionText = event.target.id;
    selectionText = selectionText.split('pf-random-id-')[1].split('-')[1];
    let selectedStatusCopy = [...selectedStatus];
    if (selectedStatus.includes(selectionText)) {
      selectedStatusCopy = selectedStatusCopy.filter(
        (item) => item !== selectionText
      );
      setSelectedStatus(selectedStatusCopy);
    } else {
      selectedStatusCopy = [...selectedStatusCopy, selectionText];
      setSelectedStatus(selectedStatusCopy);
    }
  };

  const cancelJobsOptionSelect = (): void => {
    setIsKebabOpen(!isKebabOpen);
  };

  const cancelJobsKebabToggle = (isOpen): void => {
    setIsKebabOpen(isOpen);
  };

  const dropdownItemsCancelJobsButtons = (): JSX.Element[] => {
    return [
      <DropdownItem
        key="cancel"
        onClick={jobOperations[OperationType.CANCEL].functions.perform}
        isDisabled={selectedJobInstances.length === 0}
      >
        Cancel selected
      </DropdownItem>
    ];
  };

  const cancelJobsOption: JSX.Element = (
    <OverflowMenu breakpoint="xl">
      <OverflowMenuContent>
        <OverflowMenuItem>
          <Button
            variant="secondary"
            onClick={jobOperations[OperationType.CANCEL].functions.perform}
            isDisabled={selectedJobInstances.length === 0}
          >
            Cancel selected
          </Button>
        </OverflowMenuItem>
      </OverflowMenuContent>
      <OverflowMenuControl>
        <Dropdown
          onSelect={cancelJobsOptionSelect}
          toggle={<KebabToggle onToggle={cancelJobsKebabToggle} />}
          isOpen={isKebabOpen}
          isPlain
          dropdownItems={dropdownItemsCancelJobsButtons()}
        />
      </OverflowMenuControl>
    </OverflowMenu>
  );

  useEffect(() => {
    if (chipRemoved) {
      filterData();
    }
  }, [chipRemoved]);

  return (
    <Toolbar
      id="data-toolbar-with-chip-groups"
      className="pf-m-toggle-group-container"
      collapseListedFiltersBreakpoint="md"
      clearAllFilters={() => {
        onResetToDefault();
      }}
      clearFiltersButtonText="Reset to default"
    >
      <ToolbarContent>
        <ToolbarGroup
          variant="filter-group"
          {...componentOuiaProps(ouiaId, 'job-filters', ouiaSafe)}
        >
          <ToolbarFilter
            chips={chips}
            deleteChip={onDelete}
            categoryName="Status"
            className="kogito-jobs-management__state-dropdown-list"
          >
            <Select
              variant={SelectVariant.checkbox}
              aria-label="Status"
              onToggle={onStatusToggle}
              onSelect={onSelect}
              selections={selectedStatus}
              isOpen={isExpanded}
              placeholderText="Status"
              id="status-select"
            >
              {statusMenuItems}
            </Select>
          </ToolbarFilter>
        </ToolbarGroup>
        <ToolbarGroup
          {...componentOuiaProps(ouiaId, 'job-filters/button', ouiaSafe)}
        >
          <ToolbarItem>
            <Button
              variant="primary"
              onClick={onApplyFilter}
              id="apply-filter"
              isDisabled={!(selectedStatus.length > 0)}
            >
              Apply Filter
            </Button>
          </ToolbarItem>
        </ToolbarGroup>
        <ToolbarGroup>
          <ToolbarItem>
            <Button
              variant="plain"
              onClick={() => {
                onRefresh();
                setSelectedJobInstances([]);
              }}
              id="refresh-button"
              ouiaId="refresh-button"
              aria-label={'Refresh list'}
            >
              <SyncIcon />
            </Button>
          </ToolbarItem>
        </ToolbarGroup>
        <ToolbarItem variant="separator" />
        <ToolbarGroup className="pf-u-ml-md" id="jobs-management-buttons">
          {cancelJobsOption}
        </ToolbarGroup>
      </ToolbarContent>
    </Toolbar>
  );
};

export default JobsManagementToolbar;
