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
  setIsStatusSelected: any;
  filters: any;
  setFilters: any;
}
const DataToolbarWithFilter: React.FC<IOwnProps> = ({
  checkedArray,
  filterClick,
  setCheckedArray,
  filters,
  setFilters,
  setIsStatusSelected
}) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [isFilterClicked, setIsFilterClicked] = useState(false);
  const [isClearAllClicked, setIsClearAllClicked] = useState(false);

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
    if (checkedArray.length === 1) {
      const index = checkedArray.indexOf(id);
      checkedArray.splice(index, 1);
      setCheckedArray([]);
      setFilters([]);
      setIsStatusSelected(false);
    } else {
      const index = checkedArray.indexOf(id);
      checkedArray.splice(index, 1);
      filterClick();
    }
  };

  useEffect(() => {
    if (!checkedArray.length && isFilterClicked) {
      setFilters(checkedArray);
    }
  }, [checkedArray]);

  const clearAll = () => {
    setIsClearAllClicked(true);
    setCheckedArray(['Ready']);
    setFilters(['Ready']);
    filterClick(['Ready']);
  };

  const onRefreshClick = () => {
    if (checkedArray.length === 0) {
      checkedArray.length = 0;
    } else if (isFilterClicked) {
      filterClick(checkedArray);
    } else if (isClearAllClicked) {
      filterClick(['Ready']);
    }
  };
  const onStatusToggle = isExpandedItem => {
    setIsExpanded(isExpandedItem);
  };
  const statusMenuItems = [
    <SelectOption key="Ready" value="Ready" />,
    <SelectOption key="Completed" value="Completed" />,
    <SelectOption key="Aborted" value="Aborted" />,
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
