import React, { useState, useEffect } from 'react';
import {
  DataToolbar,
  DataToolbarItem,
  DataToolbarContent,
  DataToolbarFilter,
  DataToolbarToggleGroup,
  DataToolbarGroup,
  Button,
  Select,
  SelectOption,
  SelectVariant,
  InputGroup,
  TextInput,
  ButtonVariant
} from '@patternfly/react-core';
import { FilterIcon, SyncIcon, SearchIcon } from '@patternfly/react-icons';
import _ from 'lodash';
import './DatatoolbarComponent.css';
import { ProcessInstanceState } from '../../../graphql/types';

interface IOwnProps {
  checkedArray: any;
  filterClick: any;
  setCheckedArray: any;
  setIsStatusSelected: any;
  filters: any;
  setFilters: any;
  initData: any;
  setInitData: any;
  abortedObj: any;
  setAbortedObj: any;
  handleAbortAll: any;
  setOffset: (offset: number) => void;
  getProcessInstances: (options: any) => void;
  setLimit: (limit: number) => void;
  pageSize: number;
  setSearchWord: (searchWord: string) => void;
  searchWord: string;
}
const DataToolbarComponent: React.FC<IOwnProps> = ({
  checkedArray,
  filterClick,
  setCheckedArray,
  filters,
  setFilters,
  setIsStatusSelected,
  abortedObj,
  handleAbortAll,
  setOffset,
  getProcessInstances,
  setLimit,
  pageSize,
  setSearchWord,
  searchWord
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(false);
  const [isFilterClicked, setIsFilterClicked] = useState<boolean>(false);
  const [isClearAllClicked, setIsClearAllClicked] = useState<boolean>(false);
  const [shouldRefresh, setShouldRefresh] = useState<boolean>(true);

  const onFilterClick = () => {
    if (checkedArray.length === 0) {
      setFilters({ ...filters, status: checkedArray });
      setIsFilterClicked(true);
      setIsStatusSelected(false);
    } else {
      setFilters({ ...filters, status: checkedArray });
      filterClick();
      setIsFilterClicked(true);
      setIsStatusSelected(true);
    }
    setShouldRefresh(true);
  };

  const onSelect = (event, selection) => {
    setIsFilterClicked(false);
    setIsClearAllClicked(false);
    setShouldRefresh(false);
    if (selection) {
      const index = checkedArray.indexOf(selection);
      if (index === -1) {
        setCheckedArray([...checkedArray, selection]);
      } else {
        const tempArr = checkedArray.slice();
        _.remove(tempArr, _temp => {
          return _temp === selection;
        });
        setCheckedArray(tempArr);
      }
    }
  };

  const onDelete = (type = '', id = '') => {
    if (type === 'Status') {
      if (checkedArray.length === 1 && filters.status.length === 1) {
        const index = checkedArray.indexOf(id);
        checkedArray.splice(index, 1);
        setCheckedArray([]);
        setFilters({ ...filters, status: [], businessKey: [] });
        setIsStatusSelected(false);
        setShouldRefresh(false);
      } else if (!isFilterClicked) {
        if (filters.status.length === 1) {
          setCheckedArray([]);
          setFilters({ ...filters, status: [], businessKey: [] });
          setIsStatusSelected(false);
          setIsFilterClicked(false);
        } else {
          const index = filters.status.indexOf(id);
          checkedArray.splice(index, 1);
          checkedArray = [...filters.status];
          setCheckedArray(checkedArray);
          filterClick(checkedArray);
          setIsFilterClicked(true);
          setShouldRefresh(true);
        }
      } else {
        const index = checkedArray.indexOf(id);
        checkedArray.splice(index, 1);
        filterClick();
        setShouldRefresh(true);
      }
    }
    if (type === 'Business key') {
      filters.businessKey.splice(filters.businessKey.indexOf(id), 1);
      filterClick();
    }
  };

  useEffect(() => {
    if (!checkedArray.length && isFilterClicked) {
      setSearchWord('');
      setCheckedArray(checkedArray);
      setFilters({
        ...filters,
        status: checkedArray,
        businessKey: [...filters.businessKey]
      });
    }
  }, [checkedArray]);

  const clearAll = () => {
    setOffset(0);
    setLimit(pageSize);
    setIsClearAllClicked(true);
    setSearchWord('');
    setCheckedArray(['ACTIVE']);
    setFilters({ ...filters, status: ['ACTIVE'], businessKey: [] });
    filters.businessKey = [];
    filterClick(['ACTIVE']);
    getProcessInstances({
      variables: {
        state: ProcessInstanceState.Active,
        offset: 0,
        limit: pageSize
      }
    });
    setShouldRefresh(true);
  };

  const onRefreshClick = () => {
    if (shouldRefresh && checkedArray.length !== 0) {
      filterClick(checkedArray);
    }
  };
  const onStatusToggle = isExpandedItem => {
    setIsExpanded(isExpandedItem);
  };

  const handleTextBoxChange = event => {
    const word = event;
    setSearchWord(word);
    if (word === '') {
      setSearchWord('');
      return;
    }
  };
  const handleEnterClick = e => {
    if (e.key === 'Enter') {
      setShouldRefresh(true);
      filterClick(checkedArray);
    }
  };
  const statusMenuItems = [
    <SelectOption key="ACTIVE" value="ACTIVE" />,
    <SelectOption key="COMPLETED" value="COMPLETED" />,
    <SelectOption key="ERROR" value="ERROR" />,
    <SelectOption key="ABORTED" value="ABORTED" />,
    <SelectOption key="SUSPENDED" value="SUSPENDED" />
  ];

  const toggleGroupItems = (
    <React.Fragment>
      <DataToolbarGroup>
        <DataToolbarFilter
          chips={filters.status}
          deleteChip={onDelete}
          categoryName="Status"
          className=""
        >
          <Select
            variant={SelectVariant.checkbox}
            aria-label="Status"
            onToggle={onStatusToggle}
            onSelect={onSelect}
            selections={checkedArray}
            isExpanded={isExpanded}
            placeholderText="Status"
          >
            {statusMenuItems}
          </Select>
        </DataToolbarFilter>
        <DataToolbarFilter
          chips={filters.businessKey}
          deleteChip={onDelete}
          categoryName="Business key"
        >
          <InputGroup>
            <TextInput
              name="businessKey"
              id="businessKey"
              type="search"
              aria-label="business key"
              onChange={handleTextBoxChange}
              onKeyPress={handleEnterClick}
              placeholder="Filter by business key"
              value={searchWord}
              isDisabled={checkedArray.length === 0}
            />
          </InputGroup>
        </DataToolbarFilter>
        <DataToolbarItem>
          <Button variant="primary" onClick={onFilterClick}>
            Apply filter
          </Button>
        </DataToolbarItem>
      </DataToolbarGroup>
    </React.Fragment>
  );

  const buttonItems = (
    <React.Fragment>
      <DataToolbarItem>
        {Object.keys(abortedObj).length !== 0 ? (
          <Button variant="secondary" onClick={handleAbortAll}>
            Abort selected
          </Button>
        ) : (
          <Button variant="secondary" isDisabled>
            Abort selected
          </Button>
        )}
      </DataToolbarItem>
    </React.Fragment>
  );

  const toolbarItems = (
    <React.Fragment>
      <DataToolbarToggleGroup toggleIcon={<FilterIcon />} breakpoint="xl">
        {toggleGroupItems}
      </DataToolbarToggleGroup>
      <DataToolbarGroup variant="icon-button-group">
        <DataToolbarItem>
          <Button variant="plain" onClick={onRefreshClick}>
            <SyncIcon />
          </Button>
        </DataToolbarItem>
      </DataToolbarGroup>
      <DataToolbarGroup className="pf-u-ml-auto">
        {buttonItems}
      </DataToolbarGroup>
    </React.Fragment>
  );

  return (
    <DataToolbar
      id="data-toolbar-with-filter"
      className="pf-m-toggle-group-container kogito-management-console__state-dropdown-list"
      collapseListedFiltersBreakpoint="xl"
      clearAllFilters={() => clearAll()}
      clearFiltersButtonText="Reset to default"
    >
      <DataToolbarContent>{toolbarItems}</DataToolbarContent>
    </DataToolbar>
  );
};

export default DataToolbarComponent;
