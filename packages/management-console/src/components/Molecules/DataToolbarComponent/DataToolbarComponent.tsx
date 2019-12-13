import React, { useState, useEffect } from 'react';
import {
  DataToolbar,
  DataToolbarItem,
  DataToolbarContent,
  DataToolbarFilter,
  DataToolbarToggleGroup,
  DataToolbarGroup
} from '@patternfly/react-core/dist/esm/experimental';
import {
  Button,
  Select,
  SelectOption,
  SelectVariant
} from '@patternfly/react-core';
import { FilterIcon, SyncIcon } from '@patternfly/react-icons';
import _ from 'lodash';

interface IOwnProps {
  checkedArray: any;
  filterClick: any;
  setCheckedArray: any;
}
const DataToolbarWithFilter: React.FC<IOwnProps> = ({
  checkedArray,
  filterClick,
  setCheckedArray
}) => {
  const [isExpanded, setisExpanded] = useState(false);
  const [filters, setfilters] = useState(checkedArray);
  const [isFilterClicked, setIsFilterClicked] = useState(false);
  const [isClearAllClicked, setIsClearAllClicked] = useState(false);
  const allStates = ['ACTIVE', 'COMPLETED', 'ERROR', 'SUSPENDED', 'ABORTED'];
  const onFilterClick = () => {
    setfilters(checkedArray);
    filterClick();
    setIsFilterClicked(true);
  };

  const onSelect = (event, selection) => {
    setIsFilterClicked(false);
    setIsClearAllClicked(false);
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
    const index = checkedArray.indexOf(id);
    checkedArray.splice(index, 1);
    onFilterClick();
  };

  useEffect(() => {
    if (!checkedArray.length && isFilterClicked) {
      setfilters(checkedArray);
    }
  }, [checkedArray]);

  const clearAll = () => {
    setIsClearAllClicked(true);
    setCheckedArray(['ACTIVE']);
    setfilters(['ACTIVE']);
    filterClick(['ACTIVE']);
  };

  const onRefreshClick = () => {
    if (isFilterClicked) {
      filterClick(checkedArray);
    }
    if (isClearAllClicked) {
      filterClick(['ACTIVE']);
    }
  };
  const onStatusToggle = isExpandedItem => {
    setisExpanded(isExpandedItem);
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
      <DataToolbarGroup variant="filter-group">
        <DataToolbarFilter
          chips={filters}
          deleteChip={onDelete}
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

export default DataToolbarWithFilter;
