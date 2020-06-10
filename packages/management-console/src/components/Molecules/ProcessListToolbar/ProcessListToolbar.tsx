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
  Dropdown,
  DropdownToggle,
  DropdownToggleCheckbox,
  DropdownItem,
  DropdownPosition
} from '@patternfly/react-core';
import { FilterIcon, SyncIcon } from '@patternfly/react-icons';
import _ from 'lodash';
import './ProcessListToolbar.css';
import { GraphQL } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;

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
  getProcessInstances: (options: any) => void;
  setSearchWord: (searchWord: string) => void;
  searchWord: string;
  isAllChecked: boolean;
  setIsAllChecked: (isAllChecked: boolean) => void;
  setSelectedNumber: (selectedNumber: number) => void;
  selectedNumber: number;
}
const ProcessListToolbar: React.FC<IOwnProps> = ({
  checkedArray,
  filterClick,
  setCheckedArray,
  filters,
  setFilters,
  setIsStatusSelected,
  abortedObj,
  handleAbortAll,
  getProcessInstances,
  setSearchWord,
  searchWord,
  isAllChecked,
  initData,
  setInitData,
  setIsAllChecked,
  setAbortedObj,
  selectedNumber,
  setSelectedNumber
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(false);
  const [isFilterClicked, setIsFilterClicked] = useState<boolean>(false);
  const [shouldRefresh, setShouldRefresh] = useState<boolean>(true);
  const [isCheckboxDropdownOpen, setisCheckboxDropdownOpen] = useState(false);
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
    setSearchWord('');
    setCheckedArray(['ACTIVE']);
    setFilters({ ...filters, status: ['ACTIVE'], businessKey: [] });
    filters.businessKey = [];
    filterClick(['ACTIVE']);
    getProcessInstances({
      variables: {
        state: ProcessInstanceState.Active,
        offset: 0,
        limit: 10
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
  const checkboxDropdownToggle = () => {
    setisCheckboxDropdownOpen(!isCheckboxDropdownOpen);
  };

  const handleCheckboxSelectClick = (selection, isCheckboxClicked) => {
    if (selection === 'none') {
      setIsAllChecked(false);
      setSelectedNumber(0);
      const copyOfInitData = { ...initData };
      const copyOfAbortedObj = { ...abortedObj };
      copyOfInitData.ProcessInstances.map(instance => {
        delete copyOfAbortedObj[instance.id];
        instance.isChecked = false;
        if (instance.childDataList !== undefined && instance.isOpen) {
          instance.childDataList.map(child => {
            delete copyOfAbortedObj[child.id];
            child.isChecked = false;
          });
        }
      });
      setAbortedObj(copyOfAbortedObj);
      setInitData(copyOfInitData);
    } else if (selection === 'parent') {
      let parentSelectedNumber = 0;
      setIsAllChecked(true);
      const copyOfInitData = { ...initData };
      let copyOfAbortedObj = { ...abortedObj };
      copyOfInitData.ProcessInstances.map(instance => {
        const tempObj = {};
        if (
          instance.addons.includes('process-management') &&
          instance.serviceUrl !== null
        ) {
          instance.isChecked = true;
          tempObj[instance.id] = instance;
          parentSelectedNumber += 1;
        }
        if (instance.childDataList !== undefined && instance.isOpen) {
          instance.childDataList.map(child => {
            delete copyOfAbortedObj[child.id];
            child.isChecked = false;
          });
        }
        copyOfAbortedObj = { ...copyOfAbortedObj, ...tempObj };
      });
      setSelectedNumber(parentSelectedNumber);
      setAbortedObj(copyOfAbortedObj);
      setInitData(copyOfInitData);
    } else if (selection === 'parent&child') {
      let allSelected = 0;
      setIsAllChecked(true);
      const copyOfInitData = { ...initData };
      let copyOfAbortedObj = { ...abortedObj };
      copyOfInitData.ProcessInstances.map(instance => {
        const tempObj = {};
        if (
          instance.addons.includes('process-management') &&
          instance.serviceUrl !== null
        ) {
          instance.isChecked = true;
          tempObj[instance.id] = instance;
          allSelected += 1;
        }
        if (instance.childDataList !== undefined && instance.isOpen) {
          instance.childDataList.map(child => {
            if (
              child.addons.includes('process-management') &&
              instance.serviceUrl !== null
            ) {
              tempObj[child.id] = child;
              child.isChecked = true;
              allSelected += 1;
            }
          });
        }
        copyOfAbortedObj = { ...copyOfAbortedObj, ...tempObj };
      });
      setSelectedNumber(allSelected);
      setAbortedObj(copyOfAbortedObj);
      setInitData(copyOfInitData);
    }
    if (!isCheckboxClicked) {
      setisCheckboxDropdownOpen(!isCheckboxDropdownOpen);
    } else {
      if (isAllChecked) {
        setIsAllChecked(false);
        const copyOfInitData = { ...initData };
        const copyOfAbortedObj = { ...abortedObj };
        copyOfInitData.ProcessInstances.map(instance => {
          delete copyOfAbortedObj[instance.id];
          instance.isChecked = false;
          if (instance.childDataList !== undefined && instance.isOpen) {
            instance.childDataList.map(child => {
              delete copyOfAbortedObj[child.id];
              child.isChecked = false;
            });
          }
        });
        setSelectedNumber(0);
        setAbortedObj(copyOfAbortedObj);
        setInitData(copyOfInitData);
      } else {
        let allSelected = 0;
        setIsAllChecked(true);
        const copyOfInitData = { ...initData };
        let copyOfAbortedObj = { ...abortedObj };
        copyOfInitData.ProcessInstances.map(instance => {
          const tempObj = {};
          if (
            instance.addons.includes('process-management') &&
            instance.serviceUrl !== null
          ) {
            instance.isChecked = true;
            tempObj[instance.id] = instance;
            allSelected += 1;
          }
          if (instance.childDataList !== undefined && instance.isOpen) {
            instance.childDataList.map(child => {
              if (
                child.addons.includes('process-management') &&
                instance.serviceUrl !== null
              ) {
                tempObj[child.id] = child;
                child.isChecked = true;
                allSelected += 1;
              }
            });
          }
          copyOfAbortedObj = { ...copyOfAbortedObj, ...tempObj };
        });
        setSelectedNumber(allSelected);
        setAbortedObj(copyOfAbortedObj);
        setInitData(copyOfInitData);
      }
    }
  };

  const checkboxItems = [
    <DropdownItem
      key="none"
      onClick={() => handleCheckboxSelectClick('none', false)}
    >
      Select none
    </DropdownItem>,
    <DropdownItem
      key="all-parent"
      onClick={() => handleCheckboxSelectClick('parent', false)}
    >
      Select all parent processes
    </DropdownItem>,
    <DropdownItem
      key="all-parent-child"
      onClick={() => handleCheckboxSelectClick('parent&child', false)}
    >
      Select all processes
    </DropdownItem>
  ];

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
        <DataToolbarItem variant="bulk-select">
          <Dropdown
            position={DropdownPosition.left}
            toggle={
              <DropdownToggle
                onToggle={checkboxDropdownToggle}
                splitButtonItems={[
                  <DropdownToggleCheckbox
                    id="select-all-checkbox"
                    key="split-checkbox"
                    aria-label="Select all"
                    isChecked={isAllChecked}
                    onChange={() =>
                      handleCheckboxSelectClick('parent&child', true)
                    }
                  />
                ]}
              >
                {selectedNumber === 0 ? '' : selectedNumber + ' selected'}
              </DropdownToggle>
            }
            dropdownItems={checkboxItems}
            isOpen={isCheckboxDropdownOpen}
          />
        </DataToolbarItem>

        <DataToolbarFilter
          chips={filters.status}
          deleteChip={onDelete}
          className="kogito-management-console__state-dropdown-list pf-u-mr-sm"
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
          <Button
            variant="plain"
            onClick={onRefreshClick}
            aria-label="Refresh list"
          >
            <SyncIcon />
          </Button>
        </DataToolbarItem>
      </DataToolbarGroup>
      <DataToolbarItem variant="separator" />
      <DataToolbarGroup className="pf-u-ml-md">{buttonItems}</DataToolbarGroup>
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

export default ProcessListToolbar;
