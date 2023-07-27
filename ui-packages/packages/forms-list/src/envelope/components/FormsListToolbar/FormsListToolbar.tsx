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
import _ from 'lodash';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { FormFilter } from '../../../api';

interface FormsListToolbarProps {
  filterFormNames: string[];
  setFilterFormNames: React.Dispatch<React.SetStateAction<string[]>>;
  applyFilter: (filter: FormFilter) => void;
}

enum Category {
  FORM_NAME = 'Form name'
}

const FormsListToolbar: React.FC<FormsListToolbarProps & OUIAProps> = ({
  applyFilter,
  filterFormNames,
  setFilterFormNames,
  ouiaSafe,
  ouiaId
}) => {
  const [formNameInput, setFormNameInput] = useState<string>('');

  const doResetFilter = (): void => {
    applyFilter({
      formNames: []
    });
    setFilterFormNames([]);
  };

  const doRefresh = (): void => {
    applyFilter({
      formNames: [...filterFormNames]
    });
  };

  const onEnterClicked = (event: React.KeyboardEvent<EventTarget>): void => {
    /* istanbul ignore else */
    if (event.key === 'Enter') {
      formNameInput.length > 0 && doApplyFilter();
    }
  };

  const onDeleteFilterGroup = (categoryName: Category, value: string): void => {
    const newFilterFormNames = [...filterFormNames];
    if (categoryName === Category.FORM_NAME) {
      _.remove(newFilterFormNames, (status: string) => {
        return status === value;
      });
      setFilterFormNames(newFilterFormNames);
      applyFilter({
        formNames: newFilterFormNames
      });
    }
  };

  const doApplyFilter = (): void => {
    const newFormNames = [...filterFormNames];
    if (formNameInput && !newFormNames.includes(formNameInput)) {
      newFormNames.push(formNameInput);
      setFilterFormNames(newFormNames);
    }
    setFormNameInput('');
    applyFilter({
      formNames: newFormNames
    });
  };

  const toggleGroupItems: JSX.Element = (
    <React.Fragment>
      <ToolbarGroup variant="filter-group">
        <ToolbarFilter
          key="input-form-name"
          chips={filterFormNames}
          deleteChip={onDeleteFilterGroup}
          categoryName={Category.FORM_NAME}
        >
          <InputGroup>
            <TextInput
              name="formName"
              id="formName"
              type="search"
              aria-label="form name"
              onChange={setFormNameInput}
              onKeyPress={onEnterClicked}
              placeholder="Filter by Form name"
              value={formNameInput}
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
      id="forms-list-with-filter"
      className="pf-m-toggle-group-container"
      collapseListedFiltersBreakpoint="xl"
      clearAllFilters={doResetFilter}
      clearFiltersButtonText="Reset to default"
      {...componentOuiaProps(ouiaId, 'forms-list-toolbar', ouiaSafe)}
    >
      <ToolbarContent>{toolbarItems}</ToolbarContent>
    </Toolbar>
  );
};

export default FormsListToolbar;
