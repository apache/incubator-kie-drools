/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import React, {useCallback, useState} from 'react';
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
  Tooltip,
  OverflowMenuContent,
  OverflowMenuControl,
  Dropdown,
  DropdownItem,
  KebabToggle,
  OverflowMenu,
  OverflowMenuItem,
  DropdownPosition,
  DropdownToggle,
  DropdownToggleCheckbox
} from '@patternfly/react-core';
import { FilterIcon, SyncIcon } from '@patternfly/react-icons';
import _ from 'lodash';
import {
  BulkListType,
  IOperationResults,
  IOperations,
  OperationType,
  ProcessInstance,
  ProcessInstanceState,
  ProcessInfoModal,
  setTitle
} from '@kogito-apps/management-console-shared';
import { ProcessInstanceFilter, ProcessListDriver } from '../../../api';
import '../styles.css';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import { formatForBulkListProcessInstance } from '../utils/ProcessListUtils';

enum Category {
  STATUS = 'Status',
  BUSINESS_KEY = 'Business key'
}

enum BulkSelectionType {
  NONE = 'NONE',
  PARENT = 'PARENT',
  PARENT_CHILD = 'PARENT_CHILD'
}

interface ProcessListToolbarProps {
  filters: ProcessInstanceFilter;
  setFilters: React.Dispatch<React.SetStateAction<ProcessInstanceFilter>>;
  applyFilter: (filter: ProcessInstanceFilter) => void;
  refresh: () => void;
  processStates: ProcessInstanceState[];
  setProcessStates: React.Dispatch<
    React.SetStateAction<ProcessInstanceState[]>
  >;
  selectedInstances: ProcessInstance[];
  setSelectedInstances: React.Dispatch<React.SetStateAction<ProcessInstance[]>>;
  processInstances: ProcessInstance[];
  setProcessInstances: React.Dispatch<React.SetStateAction<ProcessInstance[]>>;
  isAllChecked: boolean;
  setIsAllChecked: React.Dispatch<React.SetStateAction<boolean>>;
  driver: ProcessListDriver;
  defaultStatusFilter: ProcessInstanceState[];
  singularProcessLabel: string;
  pluralProcessLabel: string;
  isWorkflow: boolean;
  isTriggerCloudEventEnabled?: boolean;
}

const ProcessListToolbar: React.FC<ProcessListToolbarProps & OUIAProps> = ({
  filters,
  setFilters,
  applyFilter,
  refresh,
  processStates,
  setProcessStates,
  selectedInstances,
  setSelectedInstances,
  processInstances,
  setProcessInstances,
  isAllChecked,
  setIsAllChecked,
  driver,
  defaultStatusFilter,
  singularProcessLabel,
  pluralProcessLabel,
  isWorkflow,
  isTriggerCloudEventEnabled= false,
  ouiaId,
  ouiaSafe,
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(false);
  const [businessKeyInput, setBusinessKeyInput] = useState<string>('');
  const [isKebabOpen, setIsKebabOpen] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<string>('');
  const [titleType, setTitleType] = useState<string>('');
  const [operationType, setOperationType] = useState<OperationType>(null);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [isCheckboxDropdownOpen, setisCheckboxDropdownOpen] =
    useState<boolean>(false);
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

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

  const operations: IOperations = {
    ABORT: {
      type: isWorkflow ? BulkListType.WORKFLOW : BulkListType.PROCESS_INSTANCE,
      results: operationResults[OperationType.ABORT],
      messages: {
        successMessage: `Aborted ${pluralProcessLabel?.toLowerCase()}: `,
        noItemsMessage: `No ${pluralProcessLabel?.toLowerCase()} were aborted`,
        warningMessage: !processStates.includes(ProcessInstanceState.Aborted)
          ? `Note: The ${singularProcessLabel?.toLowerCase()} status has been updated. The list may appear inconsistent until you refresh any applied filters.`
          : '',
        ignoredMessage: `These ${pluralProcessLabel?.toLowerCase()} were ignored because they were already completed or aborted.`
      },
      functions: {
        perform: async () => {
          const ignoredItems = [];
          const remainingInstances = selectedInstances.filter(
            (instance: ProcessInstance) => {
              if (
                instance.state === ProcessInstanceState.Aborted ||
                instance.state === ProcessInstanceState.Completed
              ) {
                ignoredItems.push(instance);
              } else {
                return true;
              }
            }
          );
          await driver
            .handleProcessMultipleAction(
              remainingInstances,
              OperationType.ABORT
            )
            .then((result) => {
              onShowMessage(
                'Abort operation',
                result.successProcessInstances,
                result.failedProcessInstances,
                ignoredItems,
                OperationType.ABORT
              );
              processInstances.forEach((instance) => {
                result.successProcessInstances.forEach((successInstances) => {
                  if (successInstances.id === instance.id) {
                    instance.state = ProcessInstanceState.Aborted;
                  }
                });
              });
              setProcessInstances([...processInstances]);
            });
        }
      }
    },
    SKIP: {
      type: isWorkflow ? BulkListType.WORKFLOW : BulkListType.PROCESS_INSTANCE,
      results: operationResults[OperationType.SKIP],
      messages: {
        successMessage: `Skipped ${pluralProcessLabel?.toLowerCase()}: `,
        noItemsMessage: `No ${pluralProcessLabel?.toLowerCase()} were skipped`,
        ignoredMessage: `These ${pluralProcessLabel?.toLowerCase()} were ignored because they were not in error state.`
      },
      functions: {
        perform: async () => {
          const ignoredItems = [];
          const remainingInstances = selectedInstances.filter(
            (instance: ProcessInstance) => {
              if (instance.state !== ProcessInstanceState.Error) {
                ignoredItems.push(instance);
              } else {
                return true;
              }
            }
          );
          await driver
            .handleProcessMultipleAction(remainingInstances, OperationType.SKIP)
            .then((result) => {
              onShowMessage(
                'Skip operation',
                result.successProcessInstances,
                result.failedProcessInstances,
                ignoredItems,
                OperationType.SKIP
              );
            });
        }
      }
    },
    RETRY: {
      type: isWorkflow ? BulkListType.WORKFLOW : BulkListType.PROCESS_INSTANCE,
      results: operationResults[OperationType.RETRY],
      messages: {
        successMessage: `Retriggered ${pluralProcessLabel?.toLowerCase()}: `,
        noItemsMessage: `No ${pluralProcessLabel?.toLowerCase()} were retriggered`,
        ignoredMessage: `These ${pluralProcessLabel?.toLowerCase()} were ignored because they were not in error state.`
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
          await driver
            .handleProcessMultipleAction(
              remainingInstances,
              OperationType.RETRY
            )
            .then((result) => {
              onShowMessage(
                'Retry operation',
                result.successProcessInstances,
                result.failedProcessInstances,
                ignoredItems,
                OperationType.RETRY
              );
            });
        }
      }
    }
  };

  const onShowMessage = (
    title: string,
    successItems: ProcessInstance[],
    failedItems: ProcessInstance[],
    ignoredItems: ProcessInstance[],
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

  const checkboxDropdownToggle = (): void => {
    setisCheckboxDropdownOpen(!isCheckboxDropdownOpen);
  };

  const onStatusToggle = (isExpandedItem: boolean): void => {
    setIsExpanded(isExpandedItem);
  };

  const onProcessManagementButtonSelect = (): void => {
    setIsKebabOpen(!isKebabOpen);
  };

  const onProcessManagementKebabToggle = (isOpen: boolean) => {
    setIsKebabOpen(isOpen);
  };

  const onSelect = (event, selection): void => {
    if (processStates.includes(selection)) {
      const newProcessStates = [...processStates].filter(
        (state) => state !== selection
      );
      setProcessStates(newProcessStates);
    } else {
      setProcessStates([...processStates, selection]);
    }
  };

  const onDeleteChip = (categoryName: Category, value: string): void => {
    const clonedProcessStates = [...processStates];
    const clonedBusinessKeyArray = [...filters.businessKey];
    switch (categoryName) {
      case Category.STATUS:
        _.remove(clonedProcessStates, (status: string) => {
          return status === value;
        });
        setProcessStates(clonedProcessStates);
        setFilters({ ...filters, status: clonedProcessStates });
        break;
      case Category.BUSINESS_KEY:
        _.remove(clonedBusinessKeyArray, (businessKey: string) => {
          return businessKey === value;
        });
        setFilters({ ...filters, businessKey: clonedBusinessKeyArray });
        break;
    }
    applyFilter({
      status: clonedProcessStates,
      businessKey: clonedBusinessKeyArray
    });
  };

  const onApplyFilter = (): void => {
    setBusinessKeyInput('');
    const clonedBusinessKeyArray = [...filters.businessKey];
    if (
      businessKeyInput &&
      !clonedBusinessKeyArray.includes(businessKeyInput)
    ) {
      clonedBusinessKeyArray.push(businessKeyInput);
    }
    setFilters({
      ...filters,
      status: processStates,
      businessKey: clonedBusinessKeyArray
    });
    applyFilter({
      status: processStates,
      businessKey: clonedBusinessKeyArray
    });
  };

  const onEnterClicked = (event: React.KeyboardEvent<EventTarget>): void => {
    /* istanbul ignore else */
    if (event.key === 'Enter') {
      businessKeyInput.length > 0 && onApplyFilter();
    }
  };

  const resetAllFilters = (): void => {
    const defaultFilters = {
      status: defaultStatusFilter,
      businessKey: []
    };
    setProcessStates(defaultFilters.status);
    setFilters(defaultFilters);
    applyFilter(defaultFilters);
  };

  const resetSelected = (): void => {
    const clonedProcessInstances = _.cloneDeep(processInstances);
    clonedProcessInstances.forEach((processInstance: ProcessInstance) => {
      processInstance.isSelected = false;
      /* istanbul ignore else */
      if (!_.isEmpty(processInstance.childProcessInstances)) {
        processInstance.childProcessInstances.forEach(
          (childInstance: ProcessInstance) => {
            childInstance.isSelected = false;
          }
        );
      }
    });
    setProcessInstances(clonedProcessInstances);
    setSelectedInstances([]);
    setIsAllChecked(false);
  };

  const handleCheckboxSelectClick = (
    selection: string,
    isCheckBoxClicked: boolean
  ): void => {
    const clonedProcessInstances = [...processInstances];
    if (selection === BulkSelectionType.NONE) {
      clonedProcessInstances.forEach((instance: ProcessInstance) => {
        instance.isSelected = false;
        instance.childProcessInstances.length > 0 &&
          instance.childProcessInstances.forEach(
            (childInstance: ProcessInstance) => {
              childInstance.isSelected = false;
            }
          );
      });
      setSelectedInstances([]);
    }
    if (selection === BulkSelectionType.PARENT) {
      const tempSelectedInstances = [];
      clonedProcessInstances.forEach((instance: ProcessInstance) => {
        /* istanbul ignore else */
        if (
          instance.serviceUrl &&
          instance.addons.includes('process-management')
        ) {
          instance.isSelected = true;
          tempSelectedInstances.push(instance);
        }
        instance.childProcessInstances.length > 0 &&
          instance.childProcessInstances.forEach(
            (childInstance: ProcessInstance) => {
              childInstance.isSelected = false;
            }
          );
      });
      setSelectedInstances(tempSelectedInstances);
    }
    if (selection === BulkSelectionType.PARENT_CHILD) {
      const tempSelectedInstances = [];
      if (isAllChecked && isCheckBoxClicked) {
        tempSelectedInstances.length = 0;
        clonedProcessInstances.forEach((instance: ProcessInstance) => {
          if (
            instance.serviceUrl &&
            instance.addons.includes('process-management')
          ) {
            instance.isSelected = false;
          }
          instance.childProcessInstances.length > 0 &&
            instance.childProcessInstances.forEach(
              (childInstance: ProcessInstance) => {
                if (
                  childInstance.serviceUrl &&
                  childInstance.addons.includes('process-management')
                ) {
                  if (instance.isOpen) {
                    childInstance.isSelected = false;
                  }
                }
              }
            );
        });
      } else {
        clonedProcessInstances.forEach((instance: ProcessInstance) => {
          /* istanbul ignore else */
          if (
            instance.serviceUrl &&
            instance.addons.includes('process-management')
          ) {
            instance.isSelected = true;
            tempSelectedInstances.push(instance);
          }

          instance.childProcessInstances.length > 0 &&
            instance.childProcessInstances.forEach(
              (childInstance: ProcessInstance) => {
                if (
                  childInstance.serviceUrl &&
                  childInstance.addons.includes('process-management')
                ) {
                  if (instance.isOpen) {
                    childInstance.isSelected = true;
                    tempSelectedInstances.push(childInstance);
                  }
                }
              }
            );
        });
      }
      setSelectedInstances(tempSelectedInstances);
    }
    setProcessInstances(clonedProcessInstances);
  };

  const onOpenCloudEventClick = useCallback(() => {
    if(isTriggerCloudEventEnabled) {
      driver.openTriggerCloudEvent();
    }
  }, [driver])

  const statusMenuItems: JSX.Element[] = [
    <SelectOption key="ACTIVE" value="ACTIVE" />,
    <SelectOption key="COMPLETED" value="COMPLETED" />,
    <SelectOption key="ERROR" value="ERROR" />,
    <SelectOption key="ABORTED" value="ABORTED" />,
    <SelectOption key="SUSPENDED" value="SUSPENDED" />
  ];

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

  const toggleGroupItems: JSX.Element = (
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
          deleteChip={onDeleteChip}
          className="kogito-management-console__state-dropdown-list pf-u-mr-sm"
          categoryName="Status"
          id="datatoolbar-filter-status"
        >
          <Select
            variant={SelectVariant.checkbox}
            aria-label="Status"
            onToggle={onStatusToggle}
            onSelect={onSelect}
            selections={processStates}
            isOpen={isExpanded}
            placeholderText="Status"
            id="status-select"
          >
            {statusMenuItems}
          </Select>
        </ToolbarFilter>
        <ToolbarFilter
          chips={filters.businessKey}
          deleteChip={onDeleteChip}
          categoryName={Category.BUSINESS_KEY}
        >
          <InputGroup>
            <TextInput
              name="businessKey"
              id="businessKey"
              type="search"
              aria-label="business key"
              onChange={setBusinessKeyInput}
              onKeyPress={onEnterClicked}
              placeholder="Filter by business key"
              value={businessKeyInput}
            />
          </InputGroup>
        </ToolbarFilter>
        <ToolbarItem>
          <Button
            variant="primary"
            onClick={onApplyFilter}
            id="apply-filter-button"
          >
            Apply filter
          </Button>
        </ToolbarItem>
      </ToolbarGroup>
      <ToolbarGroup>
        <ToolbarItem variant="separator" />
        <ToolbarGroup className="pf-u-ml-md" id="process-management-buttons">
          {buttonItems}
        </ToolbarGroup>
      </ToolbarGroup>
    </React.Fragment>
  );

  const toolbarItems: JSX.Element = (
    <React.Fragment>
      <ToolbarToggleGroup toggleIcon={<FilterIcon />} breakpoint="xl">
        {toggleGroupItems}
      </ToolbarToggleGroup>
      <ToolbarGroup variant="icon-button-group">
        <ToolbarItem>
          <Tooltip content={'Refresh'}>
            <Button variant="plain" onClick={refresh} id="refresh">
              <SyncIcon />
            </Button>
          </Tooltip>
        </ToolbarItem>
      </ToolbarGroup>
      {
          isTriggerCloudEventEnabled && <ToolbarGroup>
            <ToolbarItem variant="separator"/>
            <ToolbarItem>
              <Button
                  variant="primary"
                  onClick={() => onOpenCloudEventClick()}
              >
                Trigger Cloud Event
              </Button>
            </ToolbarItem>
          </ToolbarGroup>
      }
    </React.Fragment>
  );

  return (
    <>
      <ProcessInfoModal
        modalTitle={setTitle(titleType, modalTitle)}
        isModalOpen={isModalOpen}
        operationResult={operations[operationType]}
        handleModalToggle={handleModalToggle}
        resetSelected={resetSelected}
        ouiaId="operation-result"
      />
      <Toolbar
        id="data-toolbar-with-filter"
        className="pf-m-toggle-group-container kogito-management-console__state-dropdown-list"
        collapseListedFiltersBreakpoint="xl"
        clearAllFilters={resetAllFilters}
        clearFiltersButtonText="Reset to default"
        {...componentOuiaProps(ouiaId, 'process-list-toolbar', ouiaSafe)}
      >
        <ToolbarContent>{toolbarItems}</ToolbarContent>
      </Toolbar>
    </>
  );
};

export default ProcessListToolbar;
