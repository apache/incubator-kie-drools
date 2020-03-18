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
} from '@patternfly/react-core';
import { FilterIcon, SyncIcon } from '@patternfly/react-icons';
import _ from 'lodash';
import './DatatoolbarComponent.css';

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
}
const DataToolbarComponent: React.FC<IOwnProps> = ({
  checkedArray,
  filterClick,
  setCheckedArray,
  filters,
  setFilters,
  setIsStatusSelected,
  abortedObj,
  handleAbortAll
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(false);
  const [isFilterClicked, setIsFilterClicked] = useState<boolean>(false);
  const [isClearAllClicked, setIsClearAllClicked] = useState<boolean>(false);
  const [shouldRefresh, setShouldRefresh] = useState<boolean>(true);

  const onFilterClick = () => {
    if (checkedArray.length === 0) {
      setFilters(checkedArray);
      setIsFilterClicked(true);
      setIsStatusSelected(false);
    } else {
      setFilters(checkedArray);
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
    if (checkedArray.length === 1 && filters.length === 1) {
      const index = checkedArray.indexOf(id);
      checkedArray.splice(index, 1);
      setCheckedArray([]);
      setFilters([]);
      setIsStatusSelected(false);
    } else if (!isFilterClicked) {
      if (filters.length === 1) {
        setCheckedArray([]);
        setFilters([]);
        setIsStatusSelected(false);
        setIsFilterClicked(false);
      } else {
        const index = filters.indexOf(id);
        filters.splice(index, 1);
        checkedArray = [...filters];
        setCheckedArray(checkedArray);
        filterClick(filters);
        setIsFilterClicked(true);
      }
    } else {
      const index = checkedArray.indexOf(id);
      checkedArray.splice(index, 1);
      filterClick();
    }
    setShouldRefresh(true);
  };

  useEffect(() => {
    if (!checkedArray.length && isFilterClicked) {
      setFilters(checkedArray);
    }
  }, [checkedArray]);

  const clearAll = () => {
    setIsClearAllClicked(true);
    setCheckedArray(['ACTIVE']);
    setFilters(['ACTIVE']);
    filterClick(['ACTIVE']);
    setShouldRefresh(true);
  };

  const onRefreshClick = () => {
    if (shouldRefresh) {
      filterClick(checkedArray);
    }
  };
  const onStatusToggle = isExpandedItem => {
    setIsExpanded(isExpandedItem);
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
          chips={filters}
          deleteChip={onDelete}
          className="kogito-management-console__state-dropdown-list"
          categoryName="Status"
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
        <DataToolbarItem>
          <Button variant="primary" onClick={onFilterClick}>
            Apply Filter
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
      <DataToolbarGroup
        className="pf-u-ml-auto"
      >
        {buttonItems}
      </DataToolbarGroup>
    </React.Fragment>
  );

  return (
    <DataToolbar
      id="data-toolbar-with-filter"
      className="pf-m-toggle-group-container"
      collapseListedFiltersBreakpoint="xl"
      clearAllFilters={() => clearAll()}
      clearFiltersButtonText="Reset to default"
    >
      <DataToolbarContent>{toolbarItems}</DataToolbarContent>
    </DataToolbar>
  );
};

export default DataToolbarComponent;
