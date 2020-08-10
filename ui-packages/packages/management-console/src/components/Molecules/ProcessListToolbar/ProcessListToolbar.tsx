import React, { useState } from 'react';
import {
  Toolbar,
  ToolbarItem,
  ToolbarContent,
  ToolbarFilter,
  ToolbarToggleGroup,
  ToolbarGroup,
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
  DropdownPosition,
  OverflowMenuControl,
  OverflowMenuContent,
  KebabToggle,
  OverflowMenu,
  OverflowMenuItem
} from '@patternfly/react-core';
import { FilterIcon, SyncIcon } from '@patternfly/react-icons';
import _ from 'lodash';
import './ProcessListToolbar.css';
import { GraphQL } from '@kogito-apps/common';
import ProcessListModal from '../../Atoms/ProcessListModal/ProcessListModal';
import { performMultipleAction, setTitle } from '../../../utils/Utils';
import ProcessInstanceState = GraphQL.ProcessInstanceState;
/* tslint:disable:no-string-literal */
interface IOwnProps {
  filterClick: (statusArray: ProcessInstanceState[] | string[]) => void;
  filters: filterType;
  setFilters: (filters) => void;
  initData: any;
  setInitData: (initData) => void;
  selectedInstances: ProcessInstanceBulkList;
  setSelectedInstances: (selectedInstances: ProcessInstanceBulkList) => void;
  setSearchWord: (searchWord: string) => void;
  searchWord: string;
  isAllChecked: boolean;
  setIsAllChecked: (isAllChecked: boolean) => void;
  setSelectedNumber: (selectedNumber: number) => void;
  selectedNumber: number;
  statusArray: GraphQL.ProcessInstanceState[];
  setStatusArray: (stautsArray) => void;
}

type filterType = {
  status: ProcessInstanceState[] | string[];
  businessKey: string[];
};

export enum OperationType {
  ABORT = 'ABORT',
  SKIP = 'SKIP',
  RETRY = 'RETRY'
}

export interface ProcessInstanceBulkList {
  [key: string]: GraphQL.ProcessInstance;
}

interface IOperationResult {
  successInstances: ProcessInstanceBulkList;
  failedInstances: ProcessInstanceBulkList;
  ignoredInstances: ProcessInstanceBulkList;
}
interface IOperationMessages {
  successMessage: string;
  warningMessage?: string;
  ignoredMessage: string;
  noProcessMessage: string;
}

interface IOperationFunctions {
  perform: () => void;
}

interface IOperationResults {
  [key: string]: IOperationResult;
}

export interface IOperation {
  results: IOperationResult;
  messages: IOperationMessages;
  functions: IOperationFunctions;
}

interface IOperations {
  [key: string]: IOperation;
}

const ProcessListToolbar: React.FC<IOwnProps> = ({
  filterClick,
  filters,
  setFilters,
  selectedInstances,
  setSearchWord,
  searchWord,
  isAllChecked,
  initData,
  setInitData,
  setIsAllChecked,
  setSelectedInstances,
  selectedNumber,
  setSelectedNumber,
  statusArray,
  setStatusArray
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(false);
  const [isCheckboxDropdownOpen, setisCheckboxDropdownOpen] = useState<boolean>(
    false
  );
  const [shouldRefresh, setShouldRefresh] = useState<boolean>(true);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<string>('');
  const [titleType, setTitleType] = useState<string>('');
  const [isKebabOpen, setIsKebabOpen] = useState<boolean>(false);
  const [operationType, setOperationType] = useState<OperationType>();
  const [operationResults, setOperationResults] = useState<IOperationResults>({
    ABORT: {
      successInstances: {},
      failedInstances: {},
      ignoredInstances: {}
    },
    SKIP: {
      successInstances: {},
      failedInstances: {},
      ignoredInstances: {}
    },
    RETRY: {
      successInstances: {},
      failedInstances: {},
      ignoredInstances: {}
    }
  });

  const operations: IOperations = {
    ABORT: {
      results: operationResults[OperationType.ABORT],
      messages: {
        successMessage: 'Aborted process: ',
        noProcessMessage: 'No processes were aborted',
        warningMessage: !statusArray.includes(
          GraphQL.ProcessInstanceState.Aborted
        )
          ? 'Note: The process status has been updated. The list may appear inconsistent until you refresh any applied filters.'
          : '',
        ignoredMessage:
          'These processes were ignored because they were already completed or aborted.'
      },
      functions: {
        perform: async () => {
          const instancesToBeIgnored = {};
          for (const [id, processInstance] of Object.entries(
            selectedInstances
          )) {
            if (
              processInstance['state'] === ProcessInstanceState.Aborted ||
              processInstance['state'] === ProcessInstanceState.Completed
            ) {
              instancesToBeIgnored[id] = processInstance;
              delete selectedInstances[id];
            }
          }
          await performMultipleAction(
            selectedInstances,
            (successInstances, failedInstances) => {
              onShowMessage(
                'Abort operation',
                successInstances,
                failedInstances,
                instancesToBeIgnored,
                OperationType.ABORT
              );
            },
            OperationType.ABORT
          );
        }
      }
    },
    SKIP: {
      results: operationResults[OperationType.SKIP],
      messages: {
        successMessage: 'Skipped process: ',
        noProcessMessage: 'No processes were skipped',
        ignoredMessage:
          'These processes were ignored because they were not in error state.'
      },
      functions: {
        perform: async () => {
          const instancesToBeIgnored = {};
          for (const [id, processInstance] of Object.entries(
            selectedInstances
          )) {
            if (processInstance['state'] !== ProcessInstanceState.Error) {
              instancesToBeIgnored[id] = processInstance;
              delete selectedInstances[id];
            }
          }
          await performMultipleAction(
            selectedInstances,
            (successInstances, failedInstances) => {
              onShowMessage(
                'Skip operation',
                successInstances,
                failedInstances,
                instancesToBeIgnored,
                OperationType.SKIP
              );
            },
            OperationType.SKIP
          );
        }
      }
    },
    RETRY: {
      results: operationResults[OperationType.RETRY],
      messages: {
        successMessage: 'Retried process: ',
        noProcessMessage: 'No processes were retried',
        ignoredMessage:
          'These processes were ignored because they were not in error state.'
      },
      functions: {
        perform: async () => {
          const instancesToBeIgnored = {};
          for (const [id, processInstance] of Object.entries(
            selectedInstances
          )) {
            if (processInstance['state'] !== ProcessInstanceState.Error) {
              instancesToBeIgnored[id] = processInstance;
              delete selectedInstances[id];
            }
          }
          await performMultipleAction(
            selectedInstances,
            (successInstances, failedInstances) => {
              onShowMessage(
                'Retry operation',
                successInstances,
                failedInstances,
                instancesToBeIgnored,
                OperationType.RETRY
              );
            },
            OperationType.RETRY
          );
        }
      }
    }
  };

  const onProcessManagementButtonSelect = () => {
    setIsKebabOpen(!isKebabOpen);
  };

  const onProcessManagementKebabToggle = isOpen => {
    setIsKebabOpen(isOpen);
  };

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

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

  const onSelect = (event,selection): void => {
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
      const copyOfSelectedInstances = { ...selectedInstances };
      copyOfInitData.ProcessInstances.map(instance => {
        delete copyOfSelectedInstances[instance.id];
        instance.isChecked = false;
        if (instance.childDataList !== undefined && instance.isOpen) {
          instance.childDataList.map(child => {
            delete copyOfSelectedInstances[child.id];
            child.isChecked = false;
          });
        }
      });
      setSelectedInstances(copyOfSelectedInstances);
      setInitData(copyOfInitData);
    } else if (selection === 'parent') {
      let parentSelectedNumber = 0;
      setIsAllChecked(true);
      const copyOfInitData = { ...initData };
      let copyOfSelectedInstances = { ...selectedInstances };
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
            delete copyOfSelectedInstances[child.id];
            child.isChecked = false;
          });
        }
        copyOfSelectedInstances = { ...copyOfSelectedInstances, ...tempObj };
      });
      setSelectedNumber(parentSelectedNumber);
      setSelectedInstances(copyOfSelectedInstances);
      setInitData(copyOfInitData);
    } else if (selection === 'parent&child') {
      let allSelected = 0;
      setIsAllChecked(true);
      const copyOfInitData = { ...initData };
      let copyOfSelectedInstances = { ...selectedInstances };
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
        copyOfSelectedInstances = { ...copyOfSelectedInstances, ...tempObj };
      });
      setSelectedNumber(allSelected);
      setSelectedInstances(copyOfSelectedInstances);
      setInitData(copyOfInitData);
    }
    if (!isCheckboxClicked) {
      setisCheckboxDropdownOpen(!isCheckboxDropdownOpen);
    } else {
      if (isAllChecked) {
        setIsAllChecked(false);
        const copyOfInitData = { ...initData };
        const copyOfSelectedInstances = { ...selectedInstances };
        copyOfInitData.ProcessInstances.map(instance => {
          delete copyOfSelectedInstances[instance.id];
          instance.isChecked = false;
          if (instance.childDataList !== undefined && instance.isOpen) {
            instance.childDataList.map(child => {
              delete copyOfSelectedInstances[child.id];
              child.isChecked = false;
            });
          }
        });
        setSelectedNumber(0);
        setSelectedInstances(copyOfSelectedInstances);
        setInitData(copyOfInitData);
      } else {
        let allSelected = 0;
        setIsAllChecked(true);
        const copyOfInitData = { ...initData };
        let copyOfSelectedInstances = { ...selectedInstances };
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
          copyOfSelectedInstances = { ...copyOfSelectedInstances, ...tempObj };
        });
        setSelectedNumber(allSelected);
        setSelectedInstances(copyOfSelectedInstances);
        setInitData(copyOfInitData);
      }
    }
  };

  const onShowMessage = (
    title: string,
    successInstances: ProcessInstanceBulkList,
    failedInstances: ProcessInstanceBulkList,
    ignoredInstances: ProcessInstanceBulkList,
    operation: OperationType
  ) => {
    setModalTitle(title);
    setTitleType('success');
    setOperationType(operation);
    setOperationResults({
      ...operationResults,
      [operation]: {
        ...operationResults[operation],
        successInstances,
        failedInstances,
        ignoredInstances
      }
    });
    handleModalToggle();
  };

  const resetSelected = () => {
    initData.ProcessInstances.map(processInstance => {
      processInstance['isChecked'] = false;
      processInstance['childDataList'] &&
        processInstance['childDataList'].length !== 0 &&
        processInstance['childDataList'].map(
          child => (child['isChecked'] = false)
        );
    });
    setSelectedInstances({});
    setSelectedNumber(0);
    setIsAllChecked(false);
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

  const dropdownItemsProcesManagementButtons = () => {
    return [
      <DropdownItem
        key="abort"
        onClick={operations[OperationType.ABORT].functions.perform}
        isDisabled={Object.keys(selectedInstances).length === 0}
      >
        Abort selected
      </DropdownItem>,
      <DropdownItem
        key="skip"
        onClick={operations[OperationType.SKIP].functions.perform}
        isDisabled={Object.keys(selectedInstances).length === 0}
      >
        Skip selected
      </DropdownItem>,
      <DropdownItem
        key="retry"
        onClick={operations[OperationType.RETRY].functions.perform}
        isDisabled={Object.keys(selectedInstances).length === 0}
      >
        Retry selected
      </DropdownItem>
    ];
  };

  const buttonItems = (
    <OverflowMenu breakpoint="xl">
      <OverflowMenuContent>
        <OverflowMenuItem>
          <Button
            variant="secondary"
            onClick={operations[OperationType.ABORT].functions.perform}
            isDisabled={Object.keys(selectedInstances).length === 0}
          >
            Abort selected
          </Button>
        </OverflowMenuItem>
        <OverflowMenuItem>
          <Button
            variant="secondary"
            onClick={operations[OperationType.SKIP].functions.perform}
            isDisabled={Object.keys(selectedInstances).length === 0}
          >
            Skip selected
          </Button>
        </OverflowMenuItem>
        <OverflowMenuItem>
          <Button
            variant="secondary"
            onClick={operations[OperationType.RETRY].functions.perform}
            isDisabled={Object.keys(selectedInstances).length === 0}
          >
            Retry selected
          </Button>
        </OverflowMenuItem>
      </OverflowMenuContent>
      <OverflowMenuControl>
        <Dropdown
          onSelect={onProcessManagementButtonSelect}
          toggle={<KebabToggle onToggle={onProcessManagementKebabToggle} />}
          isOpen={isKebabOpen}
          isPlain
          dropdownItems={dropdownItemsProcesManagementButtons()}
        />
      </OverflowMenuControl>
    </OverflowMenu>
  );

  const toggleGroupItems = (
    <React.Fragment>
      <ToolbarGroup variant="filter-group">
        <ToolbarItem variant="bulk-select" id="bulk-select">
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
        </ToolbarItem>

        <ToolbarFilter
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
            isOpen={isExpanded}
            placeholderText="Status"
            id="status-select"
          >
            {statusMenuItems}
          </Select>
        </ToolbarFilter>
        <ToolbarFilter
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
        </ToolbarFilter>
        <ToolbarItem>
          <Button
            variant="primary"
            onClick={onFilterClick}
            id="apply-filter-button"
            isDisabled={statusArray.length === 0}
          >
            Apply filter
          </Button>
        </ToolbarItem>
      </ToolbarGroup>
    </React.Fragment>
  );

  const toolbarItems = (
    <React.Fragment>
      <ToolbarToggleGroup toggleIcon={<FilterIcon />} breakpoint="xl">
        {toggleGroupItems}
      </ToolbarToggleGroup>
      <ToolbarGroup variant="icon-button-group">
        <ToolbarItem>
          <Button
            variant="plain"
            onClick={onRefreshClick}
            aria-label="Refresh list"
            id="refresh-button"
            isDisabled={statusArray.length === 0}
          >
            <SyncIcon />
          </Button>
        </ToolbarItem>
      </ToolbarGroup>
      <ToolbarItem variant="separator" />
      <ToolbarGroup className="pf-u-ml-md" id="process-management-buttons">
        {buttonItems}
      </ToolbarGroup>
    </React.Fragment>
  );

  return (
    <>
      <ProcessListModal
        modalTitle={setTitle(titleType, modalTitle)}
        isModalOpen={isModalOpen}
        operationResult={operations[operationType] && operations[operationType]}
        handleModalToggle={handleModalToggle}
        resetSelected={resetSelected}
      />
      <Toolbar
        id="data-toolbar-with-filter"
        className="pf-m-toggle-group-container kogito-management-console__state-dropdown-list"
        collapseListedFiltersBreakpoint="xl"
        clearAllFilters={() => clearAll()}
        clearFiltersButtonText="Reset to default"
      >
        <ToolbarContent>{toolbarItems}</ToolbarContent>
      </Toolbar>
    </>
  );
};

export default ProcessListToolbar;
