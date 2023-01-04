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
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import ProcessListModal from '../../Atoms/ProcessListModal/ProcessListModal';
import {
  formatForBulkListProcessInstance,
  performMultipleAction,
  setTitle
} from '../../../utils/Utils';
import ProcessInstanceState = GraphQL.ProcessInstanceState;
import {
  BulkListType,
  IOperationResults,
  IOperations,
  OperationType
} from '../../Atoms/BulkList/BulkList';

interface IOwnProps {
  filterClick: (statusArray: ProcessInstanceState[] | string[]) => void;
  filters: filterType;
  setFilters: (filters) => void;
  initData: any;
  setInitData: (initData) => void;
  selectedInstances: GraphQL.ProcessInstance[];
  setSelectedInstances: (selectedInstances: GraphQL.ProcessInstance[]) => void;
  setSearchWord: (searchWord: string) => void;
  searchWord: string;
  isAllChecked: boolean;
  setIsAllChecked: (isAllChecked: boolean) => void;
  statusArray: GraphQL.ProcessInstanceState[];
  setStatusArray: (stautsArray) => void;
}

export type filterType = {
  status: ProcessInstanceState[] | string[];
  businessKey: string[];
};

enum BulkSelectionType {
  NONE = 'NONE',
  PARENT = 'PARENT',
  PARENT_CHILD = 'PARENT_CHILD'
}

const ProcessListToolbar: React.FC<IOwnProps & OUIAProps> = ({
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
  statusArray,
  setStatusArray,
  ouiaId,
  ouiaSafe
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(false);
  const [isCheckboxDropdownOpen, setisCheckboxDropdownOpen] =
    useState<boolean>(false);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<string>('');
  const [titleType, setTitleType] = useState<string>('');
  const [isKebabOpen, setIsKebabOpen] = useState<boolean>(false);
  const [operationType, setOperationType] = useState<OperationType>();
  const [operationResults, setOperationResults] = useState<IOperationResults>({
    ABORT: {
      successItems: [],
      failedItems: [],
      ignoredItems: []
    },
    SKIP: {
      successItems: [],
      failedItems: [],
      ignoredItems: []
    },
    RETRY: {
      successItems: [],
      failedItems: [],
      ignoredItems: []
    }
  });

  const operations: IOperations = {
    ABORT: {
      type: BulkListType.PROCESS_INSTANCE,
      results: operationResults[OperationType.ABORT],
      messages: {
        successMessage: 'Aborted process: ',
        noItemsMessage: 'No processes were aborted',
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
          const ignoredItems = [];
          const remainingInstances = selectedInstances.filter((instance) => {
            if (
              instance.state === ProcessInstanceState.Aborted ||
              instance['state'] === ProcessInstanceState.Completed
            ) {
              ignoredItems.push(instance);
            } else {
              return true;
            }
          });
          await performMultipleAction(
            remainingInstances,
            (successItems, failedItems) => {
              onShowMessage(
                'Abort operation',
                successItems,
                failedItems,
                ignoredItems,
                OperationType.ABORT
              );
              const copyOfInitData = _.cloneDeep(initData);
              copyOfInitData.ProcessInstances.forEach((processInstance) => {
                successItems.forEach((successItem) => {
                  if (successItem.id === processInstance.id) {
                    processInstance.state =
                      GraphQL.ProcessInstanceState.Aborted;
                  }
                });
              });
              setInitData(copyOfInitData);
            },
            OperationType.ABORT
          );
        }
      }
    },
    SKIP: {
      type: BulkListType.PROCESS_INSTANCE,
      results: operationResults[OperationType.SKIP],
      messages: {
        successMessage: 'Skipped process: ',
        noItemsMessage: 'No processes were skipped',
        ignoredMessage:
          'These processes were ignored because they were not in error state.'
      },
      functions: {
        perform: async () => {
          const ignoredItems = [];
          const remainingInstances = selectedInstances.filter((instance) => {
            if (instance['state'] !== ProcessInstanceState.Error) {
              ignoredItems.push(instance);
            } else {
              return true;
            }
          });
          await performMultipleAction(
            remainingInstances,
            (successItems, failedItems) => {
              onShowMessage(
                'Skip operation',
                successItems,
                failedItems,
                ignoredItems,
                OperationType.SKIP
              );
            },
            OperationType.SKIP
          );
        }
      }
    },
    RETRY: {
      type: BulkListType.PROCESS_INSTANCE,
      results: operationResults[OperationType.RETRY],
      messages: {
        successMessage: 'Retried process: ',
        noItemsMessage: 'No processes were retried',
        ignoredMessage:
          'These processes were ignored because they were not in error state.'
      },
      functions: {
        perform: async () => {
          const ignoredItems = [];
          const remainingInstances = selectedInstances.filter((instance) => {
            if (instance['state'] !== ProcessInstanceState.Error) {
              ignoredItems.push(instance);
            } else {
              return true;
            }
          });
          await performMultipleAction(
            remainingInstances,
            (successItems, failedItems) => {
              onShowMessage(
                'Retry operation',
                successItems,
                failedItems,
                ignoredItems,
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

  const onProcessManagementKebabToggle = (isOpen) => {
    setIsKebabOpen(isOpen);
  };

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

  const onFilterClick = (): void => {
    filters.status = statusArray;
    searchWord.length > 0 &&
      !filters.businessKey.includes(searchWord) &&
      setFilters({
        ...filters,
        businessKey: [...filters.businessKey, searchWord]
      });
    filterClick(statusArray);
  };

  const onSelect = (event, selection): void => {
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

  const onDelete = (type = '', id = ''): void => {
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
    filterClick(statusArray);
  };
  const onStatusToggle = (isExpandedItem) => {
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
      searchWord.length > 0 && onFilterClick();
    }
  };
  const checkboxDropdownToggle = (): void => {
    setisCheckboxDropdownOpen(!isCheckboxDropdownOpen);
  };

  const handleCheckboxSelectClick = (
    selection: string,
    isCheckBoxClicked: boolean
  ): void => {
    const clonedData = _.cloneDeep(initData);
    if (selection === BulkSelectionType.NONE) {
      clonedData.ProcessInstances.forEach((instance) => {
        instance.isSelected = false;
        instance.childProcessInstances.length > 0 &&
          instance.childProcessInstances.forEach((childInstance) => {
            childInstance.isSelected = false;
          });
      });
      setSelectedInstances([]);
    }
    if (selection === BulkSelectionType.PARENT) {
      const tempSelectedInstances = [];
      clonedData.ProcessInstances.forEach((instance) => {
        if (
          instance.serviceUrl &&
          instance.addons.includes('process-management')
        ) {
          instance.isSelected = true;
          tempSelectedInstances.push(instance);
        }
        instance.childProcessInstances.length > 0 &&
          instance.childProcessInstances.forEach((childInstance) => {
            childInstance.isSelected = false;
          });
      });
      setSelectedInstances(tempSelectedInstances);
    }
    if (selection === BulkSelectionType.PARENT_CHILD) {
      const tempSelectedInstances = [];
      if (isAllChecked && isCheckBoxClicked) {
        tempSelectedInstances.length = 0;
        clonedData.ProcessInstances.forEach((instance) => {
          if (
            instance.serviceUrl &&
            instance.addons.includes('process-management')
          ) {
            instance.isSelected = false;
          }
          instance.childProcessInstances.length > 0 &&
            instance.childProcessInstances.forEach((childInstance) => {
              if (
                childInstance.serviceUrl &&
                childInstance.addons.includes('process-management')
              ) {
                if (instance.isOpen) {
                  childInstance.isSelected = false;
                }
              }
            });
        });
      } else {
        clonedData.ProcessInstances.forEach((instance) => {
          if (
            instance.serviceUrl &&
            instance.addons.includes('process-management')
          ) {
            instance.isSelected = true;
            tempSelectedInstances.push(instance);
          }
          instance.childProcessInstances.length > 0 &&
            instance.childProcessInstances.forEach((childInstance) => {
              if (
                childInstance.serviceUrl &&
                childInstance.addons.includes('process-management')
              ) {
                if (instance.isOpen) {
                  childInstance.isSelected = true;
                  tempSelectedInstances.push(childInstance);
                }
              }
            });
        });
      }

      setSelectedInstances(tempSelectedInstances);
    }
    setInitData(clonedData);
  };

  const onShowMessage = (
    title: string,
    successItems: any,
    failedItems: any,
    ignoredItems: any,
    operation: OperationType
  ) => {
    setModalTitle(title);
    setTitleType('success');
    setOperationType(operation);
    setOperationResults({
      ...operationResults,
      [operation]: {
        ...operationResults[operation],
        successItems: formatForBulkListProcessInstance(successItems),
        failedItems: formatForBulkListProcessInstance(failedItems),
        ignoredItems: formatForBulkListProcessInstance(ignoredItems)
      }
    });
    handleModalToggle();
  };

  const resetSelected = (): void => {
    const clonedInitData = _.cloneDeep(initData);
    clonedInitData.ProcessInstances.forEach((processInstance) => {
      processInstance.isSelected = false;
      if (!_.isEmpty(processInstance.childProcessInstances)) {
        processInstance.childProcessInstances.forEach(
          (
            childInstance: GraphQL.ProcessInstance & { isSelected: boolean }
          ) => {
            childInstance.isSelected = false;
          }
        );
      }
    });
    setIsAllChecked(false);
    setSelectedInstances([]);
    setInitData(clonedInitData);
  };

  const checkboxItems = [
    <DropdownItem
      key="none"
      onClick={() => handleCheckboxSelectClick(BulkSelectionType.NONE, false)}
      id="none"
    >
      Select none
    </DropdownItem>,
    <DropdownItem
      key="all-parent"
      onClick={() => handleCheckboxSelectClick(BulkSelectionType.PARENT, false)}
      id="all-parent"
    >
      Select all parent processes
    </DropdownItem>,
    <DropdownItem
      key="all-parent-child"
      onClick={() =>
        handleCheckboxSelectClick(BulkSelectionType.PARENT_CHILD, false)
      }
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
        isDisabled={selectedInstances.length === 0}
      >
        Abort selected
      </DropdownItem>,
      <DropdownItem
        key="skip"
        onClick={operations[OperationType.SKIP].functions.perform}
        isDisabled={selectedInstances.length === 0}
      >
        Skip selected
      </DropdownItem>,
      <DropdownItem
        key="retry"
        onClick={operations[OperationType.RETRY].functions.perform}
        isDisabled={selectedInstances.length === 0}
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
            isDisabled={selectedInstances.length === 0}
          >
            Abort selected
          </Button>
        </OverflowMenuItem>
        <OverflowMenuItem>
          <Button
            variant="secondary"
            onClick={operations[OperationType.SKIP].functions.perform}
            isDisabled={selectedInstances.length === 0}
          >
            Skip selected
          </Button>
        </OverflowMenuItem>
        <OverflowMenuItem>
          <Button
            variant="secondary"
            onClick={operations[OperationType.RETRY].functions.perform}
            isDisabled={selectedInstances.length === 0}
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
                isDisabled={filters.status.length === 0}
                onToggle={checkboxDropdownToggle}
                splitButtonItems={[
                  <DropdownToggleCheckbox
                    id="select-all-checkbox"
                    key="split-checkbox"
                    aria-label="Select all"
                    isChecked={isAllChecked}
                    onChange={() =>
                      handleCheckboxSelectClick(
                        BulkSelectionType.PARENT_CHILD,
                        true
                      )
                    }
                    isDisabled={filters.status.length === 0}
                  />
                ]}
              >
                {selectedInstances.length === 0
                  ? ''
                  : selectedInstances.length + ' selected'}
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
        ouiaId="operation-result"
      />
      <Toolbar
        id="data-toolbar-with-filter"
        className="pf-m-toggle-group-container kogito-management-console__state-dropdown-list"
        collapseListedFiltersBreakpoint="xl"
        clearAllFilters={() => clearAll()}
        clearFiltersButtonText="Reset to default"
        {...componentOuiaProps(ouiaId, 'process-list-toolbar', ouiaSafe)}
      >
        <ToolbarContent>{toolbarItems}</ToolbarContent>
      </Toolbar>
    </>
  );
};

export default ProcessListToolbar;
