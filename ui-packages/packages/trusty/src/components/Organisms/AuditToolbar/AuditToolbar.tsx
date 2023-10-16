/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useRef } from 'react';
import {
  Button,
  ButtonVariant,
  Toolbar,
  ToolbarContent,
  ToolbarItem,
  ToolbarItemVariant,
  InputGroup,
  TextInput,
  Divider,
  PaginationVariant,
  ToolbarToggleGroup
} from '@patternfly/react-core';
import {
  OutlinedCalendarIcon,
  SearchIcon,
  SyncIcon
} from '@patternfly/react-icons';
import PaginationContainer from '../PaginationContainer/PaginationContainer';
import DatePicker from '../../Molecules/DatePicker/DatePicker';

type toolbarBaseProps = {
  page: number;
  pageSize: number;
  setPage: (page: number) => void;
  setPageSize: (pageSize: number) => void;
  total: number;
};

type topToolbarProps = {
  setSearchString: (searchString: string) => void;
  fromDate: string;
  setFromDate: (fromDate: string) => void;
  toDate: string;
  setToDate: (toDate: string) => void;
  onRefresh: () => void;
};

export const AuditToolbarTop = (props: toolbarBaseProps & topToolbarProps) => {
  const {
    setSearchString,
    fromDate,
    setFromDate,
    toDate,
    setToDate,
    total,
    pageSize,
    page,
    setPage,
    setPageSize,
    onRefresh
  } = props;

  const searchField = useRef<HTMLInputElement>();
  const onSearchSubmit = (): void => {
    if (searchField && searchField.current) {
      setSearchString(searchField.current.value);
    }
  };
  const onSearchEnter = (event: React.KeyboardEvent): void => {
    if (searchField && searchField.current && event.key === 'Enter') {
      setSearchString(searchField.current.value);
    }
  };

  return (
    <>
      <Toolbar
        id="audit-overview-top-toolbar"
        inset={{
          default: 'insetMd',
          sm: 'insetMd',
          md: 'insetMd',
          xl: 'insetXl'
        }}
      >
        <ToolbarContent>
          <ToolbarItem
            spacer={{ default: 'spacerLg', md: 'spacerMd', lg: 'spacerLg' }}
          >
            <InputGroup>
              <TextInput
                name="audit-search-input"
                ref={searchField}
                id="audit-search-input"
                type="search"
                aria-label="search executions"
                onKeyDown={onSearchEnter}
                placeholder="Search by ID"
                ouiaId="search-input"
              />
              <Button
                id="audit-search"
                variant={ButtonVariant.control}
                aria-label="search button for search input"
                onClick={onSearchSubmit}
                ouiaId="search-button"
              >
                <SearchIcon />
              </Button>
            </InputGroup>
          </ToolbarItem>
          <ToolbarToggleGroup
            toggleIcon={<OutlinedCalendarIcon />}
            breakpoint={'lg'}
          >
            <ToolbarItem variant="label">From</ToolbarItem>
            <ToolbarItem>
              <DatePicker
                fromDate={fromDate}
                maxDate={toDate}
                value={fromDate}
                label="Starting date"
                id="audit-from-date"
                onDateUpdate={setFromDate}
              />
            </ToolbarItem>
            <ToolbarItem variant="label">To</ToolbarItem>
            <ToolbarItem>
              <DatePicker
                value={toDate}
                minDate={fromDate}
                onDateUpdate={setToDate}
                id="audit-to-date"
                label="Ending date"
              />
            </ToolbarItem>
          </ToolbarToggleGroup>

          <ToolbarItem>
            <ToolbarItem>
              <Button
                id="executions-refresh"
                variant="plain"
                title="Refresh"
                aria-label="Refresh"
                onClick={() => onRefresh()}
                ouiaId="refresh-button"
              >
                <SyncIcon />
              </Button>
            </ToolbarItem>
          </ToolbarItem>
        </ToolbarContent>
      </Toolbar>
      <Divider />
      <Toolbar>
        <ToolbarContent>
          <ToolbarItem
            variant={ToolbarItemVariant.pagination}
            data-ouia-component-id="top-pagination"
          >
            <PaginationContainer
              total={total}
              page={page}
              pageSize={pageSize}
              onSetPage={setPage}
              onSetPageSize={setPageSize}
              paginationId="audit-overview-top-pagination"
              position={PaginationVariant.top}
            />
          </ToolbarItem>
        </ToolbarContent>
      </Toolbar>
    </>
  );
};

export const AuditToolbarBottom = (props: toolbarBaseProps) => {
  const { total, pageSize, page, setPage, setPageSize } = props;
  return (
    <PaginationContainer
      total={total}
      page={page}
      pageSize={pageSize}
      onSetPage={setPage}
      onSetPageSize={setPageSize}
      paginationId="audit-overview-bottom-pagination"
      position={PaginationVariant.bottom}
    />
  );
};
