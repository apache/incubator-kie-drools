/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import _ from 'lodash';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import { CustomDashboardFilter } from '../../../api';

interface CustomDashboardListToolbarProps {
  filterDashboardNames: string[];
  setFilterDashboardNames: React.Dispatch<React.SetStateAction<string[]>>;
  applyFilter: (filter: CustomDashboardFilter) => void;
}

enum Category {
  CUSTOM_DASHBOARD_NAME = 'Custom Dashboard name'
}

const CustomDashboardListToolbar: React.FC<
  CustomDashboardListToolbarProps & OUIAProps
> = ({
  applyFilter,
  filterDashboardNames,
  setFilterDashboardNames,
  ouiaSafe,
  ouiaId
}) => {
  const [dashboardNameInput, setDashboardNameInput] = useState<string>('');

  const doResetFilter = (): void => {
    applyFilter({
      customDashboardNames: []
    });
    setFilterDashboardNames([]);
  };

  const doRefresh = (): void => {
    applyFilter({
      customDashboardNames: [...filterDashboardNames]
    });
  };

  const onEnterClicked = (event: React.KeyboardEvent<EventTarget>): void => {
    /* istanbul ignore else */
    if (event.key === 'Enter') {
      dashboardNameInput.length > 0 && doApplyFilter();
    }
  };

  const onDeleteFilterGroup = (categoryName: Category, value: string): void => {
    const newFilterDashboardNames = [...filterDashboardNames];
    if (categoryName === Category.CUSTOM_DASHBOARD_NAME) {
      _.remove(newFilterDashboardNames, (status: string) => {
        return status === value;
      });
      setFilterDashboardNames(newFilterDashboardNames);
      applyFilter({
        customDashboardNames: newFilterDashboardNames
      });
    }
  };

  const doApplyFilter = (): void => {
    const newDashboardNames = [...filterDashboardNames];
    if (dashboardNameInput && !newDashboardNames.includes(dashboardNameInput)) {
      newDashboardNames.push(dashboardNameInput);
      setFilterDashboardNames(newDashboardNames);
    }
    setDashboardNameInput('');
    applyFilter({
      customDashboardNames: newDashboardNames
    });
  };

  const toggleGroupItems: JSX.Element = (
    <React.Fragment>
      <ToolbarGroup variant="filter-group">
        <ToolbarFilter
          key="input-customDashboard-name"
          chips={filterDashboardNames}
          deleteChip={onDeleteFilterGroup}
          categoryName={Category.CUSTOM_DASHBOARD_NAME}
        >
          <InputGroup>
            <TextInput
              name="customDashboardName"
              id="customDashboardName"
              type="search"
              aria-label="Dashboard name"
              onChange={setDashboardNameInput}
              onKeyPress={onEnterClicked}
              placeholder="Filter by dashboard name"
              value={dashboardNameInput}
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
    </React.Fragment>
  );

  return (
    <Toolbar
      id="custom-dashboard-list-with-filter"
      className="pf-m-toggle-group-container"
      collapseListedFiltersBreakpoint="xl"
      clearAllFilters={doResetFilter}
      clearFiltersButtonText="Reset to default"
      {...componentOuiaProps(ouiaId, 'custom-dashboard-list-toolbar', ouiaSafe)}
    >
      <ToolbarContent>{toolbarItems}</ToolbarContent>
    </Toolbar>
  );
};

export default CustomDashboardListToolbar;
