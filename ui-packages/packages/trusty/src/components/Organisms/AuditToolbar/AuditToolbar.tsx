import React, { useRef } from 'react';
import {
  Button,
  ButtonVariant,
  Toolbar,
  ToolbarContent,
  ToolbarItem,
  ToolbarItemVariant,
  InputGroup,
  TextInput
} from '@patternfly/react-core';
import { SearchIcon, SyncIcon } from '@patternfly/react-icons';
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
    <Toolbar id="audit-overview-top-toolbar">
      <ToolbarContent>
        <ToolbarItem variant="label">Search</ToolbarItem>
        <ToolbarItem>
          <InputGroup>
            <TextInput
              name="audit-search-input"
              ref={searchField}
              id="audit-search-input"
              type="search"
              aria-label="search executions"
              onKeyDown={onSearchEnter}
              placeholder="Search by ID"
            />
            <Button
              id="audit-search"
              variant={ButtonVariant.control}
              aria-label="search button for search input"
              onClick={onSearchSubmit}
            >
              <SearchIcon />
            </Button>
          </InputGroup>
        </ToolbarItem>
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
        <ToolbarItem>
          <ToolbarItem>
            <Button
              id="executions-refresh"
              variant="plain"
              title="Refresh"
              aria-label="Refresh"
              onClick={() => onRefresh()}
            >
              <SyncIcon />
            </Button>
          </ToolbarItem>
        </ToolbarItem>
        <ToolbarItem variant={ToolbarItemVariant.pagination}>
          <PaginationContainer
            total={total}
            page={page}
            pageSize={pageSize}
            onSetPage={setPage}
            onSetPageSize={setPageSize}
            paginationId="audit-overview-top-pagination"
          />
        </ToolbarItem>
      </ToolbarContent>
    </Toolbar>
  );
};

export const AuditToolbarBottom = (props: toolbarBaseProps) => {
  const { total, pageSize, page, setPage, setPageSize } = props;
  return (
    <Toolbar id="audit-overview-bottom-toolbar">
      <ToolbarContent>
        <ToolbarItem variant="pagination">
          <PaginationContainer
            total={total}
            page={page}
            pageSize={pageSize}
            onSetPage={setPage}
            onSetPageSize={setPageSize}
            paginationId="audit-overview-bottom-pagination"
          />
        </ToolbarItem>
      </ToolbarContent>
    </Toolbar>
  );
};
