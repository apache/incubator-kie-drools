import React, { useState } from 'react';
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
import { handleAbortAll } from '../../../utils/Utils';

type filterType = {
  status: ProcessInstanceState[] | string[];
  businessKey: string[];
};
interface IOwnProps {
  filterClick: (statusArray: ProcessInstanceState[] | string[]) => void;
  filters: filterType;
  setFilters: (filters) => void;
  initData: any;
  setInitData: (initData) => void;
  abortedObj: any;
  setAbortedObj: any;
  setCompletedMessageObj: any;
  setAbortedMessageObj: any;
  getProcessInstances: (options: any) => void;
  setSearchWord: (searchWord: string) => void;
  searchWord: string;
  isAllChecked: boolean;
  setIsAllChecked: (isAllChecked: boolean) => void;
  setSelectedNumber: (selectedNumber: number) => void;
  selectedNumber: number;
  statusArray: string[];
  setStatusArray: (stautsArray) => void;
  setModalTitle: (modalTitle: string) => void;
  setTitleType: (titleType: string) => void;
  handleAbortModalToggle: () => void;
}
const ProcessListToolbar: React.FC<IOwnProps> = ({
  filterClick,
  filters,
  setFilters,
  abortedObj,
  getProcessInstances,
  setSearchWord,
  searchWord,
  isAllChecked,
  initData,
  setInitData,
  setIsAllChecked,
  setAbortedObj,
  selectedNumber,
  setSelectedNumber,
  statusArray,
  setStatusArray,
  setModalTitle,
  setTitleType,
  setAbortedMessageObj,
  setCompletedMessageObj,
  handleAbortModalToggle
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(false);
  const [isCheckboxDropdownOpen, setisCheckboxDropdownOpen] = useState<boolean>(
    false
  );
  const [shouldRefresh, setShouldRefresh] = useState<boolean>(true);

  const onFilterClick = (): void => {
    setShouldRefresh(true);
    filters.status = statusArray;
    searchWord.length > 0 &&
      !filters.businessKey.includes(searchWord) &&
      setFilters({
        ...filters,
        businessKey: [...filters.businessKey, searchWord]
      });
    filterClick(statusArray);
  };

  const onSelect = (event): void => {
    const selection = event.target.id;
    setShouldRefresh(false);
    if (selection) {
      const index = statusArray.indexOf(selection);
      if (index === -1) {
        setStatusArray([...statusArray, selection]);
      } else {
        const copyOfStatusArray = statusArray.slice();
        _.remove(copyOfStatusArray, (status: string) => {
          return status === selection;
        });
        setStatusArray(copyOfStatusArray);
      }
    }
  };

  const onDelete = (type: string = '', id: string = ''): void => {
    setShouldRefresh(true);
    if (type === 'Status') {
      const copyOfStatusArray = statusArray.slice();
      _.remove(copyOfStatusArray, (status: string) => {
        return status === id;
      });
      setFilters({ ...filters, status: copyOfStatusArray });
      setStatusArray(copyOfStatusArray);
      copyOfStatusArray.length > 0 && filterClick(copyOfStatusArray);
    }
    if (type === 'Business key') {
      filters.businessKey.splice(filters.businessKey.indexOf(id), 1);
      filterClick(statusArray);
    }
  };

  const clearAll = (): void => {
    setShouldRefresh(true);
    setSearchWord('');
    setFilters({
      ...filters,
      status: [ProcessInstanceState.Active],
      businessKey: []
    });
    setStatusArray([ProcessInstanceState.Active]);
    filters.businessKey = [];
    filterClick([ProcessInstanceState.Active]);
    getProcessInstances({
      variables: {
        state: ProcessInstanceState.Active,
        offset: 0,
        limit: 10
      }
    });
  };

  const onRefreshClick = (): void => {
    shouldRefresh && filterClick(statusArray);
  };
  const onStatusToggle = isExpandedItem => {
    setIsExpanded(isExpandedItem);
  };

  const handleTextBoxChange = (event): void => {
    const word = event;
    setSearchWord(word);
    if (word === '') {
      setSearchWord('');
      return;
    }
  };
  const handleEnterClick = (e): void => {
    if (e.key === 'Enter') {
      setShouldRefresh(true);
      searchWord.length > 0 && onFilterClick();
    }
  };
  const checkboxDropdownToggle = (): void => {
    setisCheckboxDropdownOpen(!isCheckboxDropdownOpen);
  };

  const handleCheckboxSelectClick = (
    selection: string,
    isCheckboxClicked: boolean
  ): void => {
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
      id="none"
    >
      Select none
    </DropdownItem>,
    <DropdownItem
      key="all-parent"
      onClick={() => handleCheckboxSelectClick('parent', false)}
      id="all-parent"
    >
      Select all parent processes
    </DropdownItem>,
    <DropdownItem
      key="all-parent-child"
      onClick={() => handleCheckboxSelectClick('parent&child', false)}
      id="all-parent-child"
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
        <DataToolbarItem variant="bulk-select" id="bulk-select">
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
          id="datatoolbar-filter-status"
        >
          <Select
            variant={SelectVariant.checkbox}
            aria-label="Status"
            onToggle={onStatusToggle}
            onSelect={onSelect}
            selections={statusArray}
            isExpanded={isExpanded}
            placeholderText="Status"
            id="status-select"
          >
            {statusMenuItems}
          </Select>
        </DataToolbarFilter>
        <DataToolbarFilter
          chips={filters.businessKey}
          deleteChip={onDelete}
          categoryName="Business key"
          id="datatoolbar-filter-businesskey"
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
              isDisabled={statusArray.length === 0}
            />
          </InputGroup>
        </DataToolbarFilter>
        <DataToolbarItem>
          <Button
            variant="primary"
            onClick={onFilterClick}
            id="apply-filter-button"
            isDisabled={statusArray.length === 0}
          >
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
          <Button
            variant="secondary"
            onClick={() =>
              handleAbortAll(
                abortedObj,
                initData,
                setModalTitle,
                setTitleType,
                setAbortedMessageObj,
                setCompletedMessageObj,
                handleAbortModalToggle
              )
            }
          >
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
            id="refresh-button"
            isDisabled={statusArray.length === 0}
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
