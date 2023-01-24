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
import React, { useEffect, useState } from 'react';
import {
  TableComposable,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  ExpandableRowContent
} from '@patternfly/react-table';
import _ from 'lodash';
import {
  ProcessInstance,
  setTitle,
  ProcessInfoModal,
  TitleType,
  ProcessInstanceState
} from '@kogito-apps/management-console-shared';
import ProcessListChildTable from '../ProcessListChildTable/ProcessListChildTable';
import {
  ItemDescriptor,
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner,
  EndpointLink
} from '@kogito-apps/components-common';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import { HistoryIcon } from '@patternfly/react-icons';
import Moment from 'react-moment';
import {
  getProcessInstanceDescription,
  ProcessInstanceIconCreator
} from '../utils/ProcessListUtils';
import { ProcessListDriver } from '../../../api';
import ProcessListActionsKebab from '../ProcessListActionsKebab/ProcessListActionsKebab';
import { Checkbox } from '@patternfly/react-core';
import DisablePopup from '../DisablePopup/DisablePopup';
import '../styles.css';
import ErrorPopover from '../ErrorPopover/ErrorPopover';
interface ProcessListTableProps {
  processInstances: ProcessInstance[];
  isLoading: boolean;
  expanded: {
    [key: number]: boolean;
  };
  setExpanded: React.Dispatch<
    React.SetStateAction<{
      [key: number]: boolean;
    }>
  >;
  driver: ProcessListDriver;
  onSort: (
    event: React.SyntheticEvent<EventTarget>,
    index: number,
    direction: 'desc' | 'asc'
  ) => void;
  sortBy: any;
  setProcessInstances: React.Dispatch<React.SetStateAction<ProcessInstance[]>>;
  selectedInstances: ProcessInstance[];
  setSelectedInstances: React.Dispatch<React.SetStateAction<ProcessInstance[]>>;
  selectableInstances: number;
  setSelectableInstances: React.Dispatch<React.SetStateAction<number>>;
  setIsAllChecked: React.Dispatch<React.SetStateAction<boolean>>;
  singularProcessLabel: string;
  pluralProcessLabel: string;
}

const ProcessListTable: React.FC<ProcessListTableProps & OUIAProps> = ({
  isLoading,
  expanded,
  setExpanded,
  sortBy,
  onSort,
  processInstances,
  setProcessInstances,
  selectedInstances,
  setSelectedInstances,
  selectableInstances,
  setSelectableInstances,
  setIsAllChecked,
  singularProcessLabel,
  pluralProcessLabel,
  driver,
  ouiaId,
  ouiaSafe
}) => {
  const [rowPairs, setRowPairs] = useState<any>([]);
  const columns: string[] = [
    '__Toggle',
    '__Select',
    'Id',
    'Status',
    'Created',
    'Last update',
    '__Actions'
  ];
  const [modalTitle, setModalTitle] = useState<string>('');
  const [modalContent, setModalContent] = useState<string>('');
  const [titleType, setTitleType] = useState<string>('');
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [selectedProcessInstance, setSelectedProcessInstance] =
    useState<ProcessInstance>(null);

  const handleModalToggle = (): void => {
    setIsModalOpen(!isModalOpen);
  };
  const onShowMessage = (
    title: string,
    content: string,
    type: TitleType,
    processInstance: ProcessInstance
  ): void => {
    setSelectedProcessInstance(processInstance);
    setTitleType(type);
    setModalTitle(title);
    setModalContent(content);
    handleModalToggle();
  };

  const onSkipClick = async (
    processInstance: ProcessInstance
  ): Promise<void> => {
    try {
      await driver.handleProcessSkip(processInstance);
      onShowMessage(
        'Skip operation',
        `The ${singularProcessLabel?.toLowerCase()} ${
          processInstance.processName
        } was successfully skipped.`,
        TitleType.SUCCESS,
        processInstance
      );
    } catch (error) {
      onShowMessage(
        'Skip operation',
        `The ${singularProcessLabel?.toLowerCase()} ${
          processInstance.processName
        } failed to skip. Message: ${error.message}`,
        TitleType.FAILURE,
        processInstance
      );
    } finally {
      handleModalToggle();
    }
  };

  const onRetryClick = async (
    processInstance: ProcessInstance
  ): Promise<void> => {
    try {
      await driver.handleProcessRetry(processInstance);
      onShowMessage(
        'Retry operation',
        `The ${singularProcessLabel?.toLowerCase()} ${
          processInstance.processName
        } was successfully re-executed.`,
        TitleType.SUCCESS,
        processInstance
      );
    } catch (error) {
      onShowMessage(
        'Retry operation',
        `The ${singularProcessLabel?.toLowerCase()} ${
          processInstance.processName
        } failed to re-execute. Message: ${error.message}`,
        TitleType.FAILURE,
        processInstance
      );
    } finally {
      handleModalToggle();
    }
  };

  const onAbortClick = async (
    processInstance: ProcessInstance
  ): Promise<void> => {
    try {
      await driver.handleProcessAbort(processInstance);
      onShowMessage(
        'Abort operation',
        `The ${singularProcessLabel?.toLowerCase()} ${
          processInstance.processName
        } was successfully aborted.`,
        TitleType.SUCCESS,
        processInstance
      );
      processInstances.forEach((instance) => {
        if (instance.id === processInstance.id) {
          instance.state = ProcessInstanceState.Aborted;
        }
      });
      setProcessInstances([...processInstances]);
    } catch (error) {
      onShowMessage(
        'Abort operation',
        `Failed to abort ${singularProcessLabel?.toLowerCase()} ${
          processInstance.processName
        }. Message: ${error.message}`,
        TitleType.FAILURE,
        processInstance
      );
    } finally {
      handleModalToggle();
    }
  };

  const handleClick = (processInstance: ProcessInstance): void => {
    driver.openProcess(processInstance);
  };

  useEffect(() => {
    if (!_.isEmpty(processInstances)) {
      const tempRows = [];
      processInstances.forEach((processInstance: ProcessInstance) => {
        tempRows.push({
          id: processInstance.id,
          parent: [
            <>
              {processInstance.addons.includes('process-management') &&
              processInstance.serviceUrl !== null ? (
                <Checkbox
                  isChecked={processInstance.isSelected}
                  onChange={() => checkBoxSelect(processInstance)}
                  aria-label="process-list-checkbox"
                  id={`checkbox-${processInstance.id}`}
                  name={`checkbox-${processInstance.id}`}
                />
              ) : (
                <DisablePopup
                  processInstanceData={processInstance}
                  component={
                    <Checkbox
                      aria-label="process-list-checkbox-disabled"
                      id={`checkbox-${processInstance.id}`}
                      isDisabled={true}
                    />
                  }
                />
              )}
            </>,
            <>
              <a
                className="kogito-process-list__link"
                onClick={() => handleClick(processInstance)}
                {...componentOuiaProps(ouiaId, 'process-description', ouiaSafe)}
              >
                <strong>
                  <ItemDescriptor
                    itemDescription={getProcessInstanceDescription(
                      processInstance
                    )}
                  />
                </strong>
              </a>
              <EndpointLink
                serviceUrl={processInstance.serviceUrl}
                isLinkShown={false}
              />
            </>,
            processInstance.state === ProcessInstanceState.Error ? (
              <ErrorPopover
                processInstanceData={processInstance}
                onSkipClick={onSkipClick}
                onRetryClick={onRetryClick}
              />
            ) : (
              ProcessInstanceIconCreator(processInstance.state)
            ),
            processInstance.start ? (
              <Moment fromNow>{new Date(`${processInstance.start}`)}</Moment>
            ) : (
              ''
            ),
            processInstance.lastUpdate ? (
              <span>
                <HistoryIcon className="pf-u-mr-sm" /> {'Updated '}
                <Moment fromNow>
                  {new Date(`${processInstance.lastUpdate}`)}
                </Moment>
              </span>
            ) : (
              ''
            ),
            <ProcessListActionsKebab
              processInstance={processInstance}
              onSkipClick={onSkipClick}
              onRetryClick={onRetryClick}
              onAbortClick={onAbortClick}
              key={processInstance.id}
            />
          ],
          child: [processInstance.id]
        });
      });
      setRowPairs(tempRows);
    } else {
      setRowPairs([]);
    }
  }, [processInstances]);

  const loadChild = (
    parentId: string,
    parentIndex: number
  ): JSX.Element | null => {
    if (!expanded[parentIndex]) {
      return null;
    } else {
      return (
        <ProcessListChildTable
          parentProcessId={parentId}
          processInstances={processInstances}
          setProcessInstances={setProcessInstances}
          selectedInstances={selectedInstances}
          setSelectedInstances={setSelectedInstances}
          setSelectableInstances={setSelectableInstances}
          singularProcessLabel={singularProcessLabel}
          pluralProcessLabel={pluralProcessLabel}
          driver={driver}
          onSkipClick={onSkipClick}
          onRetryClick={onRetryClick}
          onAbortClick={onAbortClick}
          ouiaId={parentId}
        />
      );
    }
  };

  const checkBoxSelect = (processInstance: ProcessInstance): void => {
    const clonedProcessInstances = [...processInstances];
    clonedProcessInstances.forEach((instance: ProcessInstance) => {
      if (processInstance.id === instance.id) {
        if (instance.isSelected) {
          instance.isSelected = false;
          setSelectedInstances(
            selectedInstances.filter(
              (selectedInstance) => selectedInstance.id !== instance.id
            )
          );
        } else {
          instance.isSelected = true;
          setSelectedInstances([...selectedInstances, instance]);
        }
      }
    });
    setProcessInstances(clonedProcessInstances);
  };

  const onToggle = (pairIndex: number, pair: any): void => {
    setExpanded({
      ...expanded,
      [pairIndex]: !expanded[pairIndex]
    });

    if (expanded[pairIndex]) {
      const processInstance: ProcessInstance = processInstances.find(
        (instance) => instance.id === pair.id
      );
      processInstance.childProcessInstances.forEach(
        (childInstance: ProcessInstance) => {
          if (childInstance.isSelected) {
            const index = selectedInstances.findIndex(
              (selectedInstance) => selectedInstance.id === childInstance.id
            );
            if (index !== -1) {
              selectedInstances.splice(index, 1);
            }
          }
        }
      );
      processInstances.forEach((instance: ProcessInstance) => {
        if (processInstance.id === instance.id) {
          instance.isOpen = false;
          instance.childProcessInstances.forEach((child: ProcessInstance) => {
            if (
              child.serviceUrl &&
              child.addons.includes('process-management')
            ) {
              setSelectableInstances((prev) => prev - 1);
            }
          });
        }
      });
    } else {
      const processInstance =
        !_.isEmpty(processInstances) &&
        processInstances.find((instance) => instance.id === pair.id);
      !_.isEmpty(processInstances) &&
        processInstances.forEach((instance: ProcessInstance) => {
          if (processInstance.id === instance.id) {
            instance.isOpen = true;
          }
        });
    }
    if (
      selectedInstances.length === selectableInstances &&
      selectableInstances !== 0
    ) {
      setIsAllChecked(true);
    } else {
      setIsAllChecked(false);
    }
  };

  return (
    <React.Fragment>
      <ProcessInfoModal
        isModalOpen={isModalOpen}
        handleModalToggle={handleModalToggle}
        modalTitle={setTitle(titleType, modalTitle)}
        modalContent={modalContent}
        processName={
          selectedProcessInstance && selectedProcessInstance.processName
        }
        ouiaId={
          selectedProcessInstance && 'process-' + selectedProcessInstance.id
        }
      />
      <TableComposable
        aria-label="Process List Table"
        {...componentOuiaProps(
          ouiaId,
          'process-list-table',
          ouiaSafe ? ouiaSafe : !isLoading
        )}
      >
        <Thead>
          <Tr ouiaId="process-list-table-header">
            {columns.map((column, columnIndex) => {
              let sortParams = {};
              if (!isLoading && rowPairs.length > 0) {
                sortParams = {
                  sort: {
                    sortBy,
                    onSort,
                    columnIndex
                  }
                };
              }
              let styleParams = undefined;
              switch (columnIndex) {
                case 0:
                  styleParams = { width: '72px' };
                  sortParams = {};
                  break;
                case 1:
                  styleParams = { width: '86px' };
                  sortParams = {};
                  break;
                case columns.length - 1:
                  styleParams = { width: '188px' };
                  sortParams = {};
                  break;
              }
              return (
                <Th style={styleParams} key={columnIndex} {...sortParams}>
                  {column.startsWith('__') ? '' : column}
                </Th>
              );
            })}
          </Tr>
        </Thead>
        {!isLoading && !_.isEmpty(rowPairs) ? (
          rowPairs.map((pair, pairIndex) => {
            const parentRow = (
              <Tr
                key={`${pairIndex}-parent`}
                {...componentOuiaProps(pair.id, 'process-list-row', true)}
              >
                <Td
                  key={`${pairIndex}-parent-0`}
                  expand={{
                    rowIndex: pairIndex,
                    isExpanded: expanded[pairIndex],
                    onToggle: () => onToggle(pairIndex, pair)
                  }}
                  {...componentOuiaProps(
                    columns[0].toLowerCase(),
                    'process-list-cell',
                    true
                  )}
                />
                {pair.parent.map((cell, cellIndex) => (
                  <Td
                    key={`${pairIndex}-parent-${++cellIndex}`}
                    dataLabel={columns[cellIndex]}
                    {...componentOuiaProps(
                      columns[cellIndex].toLowerCase(),
                      'process-list-cell',
                      true
                    )}
                  >
                    {cell}
                  </Td>
                ))}
              </Tr>
            );
            const childRow = (
              <Tr
                key={`${pairIndex}-child`}
                isExpanded={expanded[pairIndex] === true}
                {...componentOuiaProps(
                  pair.id,
                  'process-list-row-expanded',
                  true
                )}
              >
                <Td key={`${pairIndex}-child-0`} />
                {rowPairs[pairIndex].child.map((cell, cellIndex) => (
                  <Td
                    key={`${pairIndex}-child-${++cellIndex}`}
                    dataLabel={columns[cellIndex]}
                    noPadding={rowPairs[pairIndex].noPadding}
                    colSpan={6}
                  >
                    <ExpandableRowContent>
                      {loadChild(cell, pairIndex)}
                    </ExpandableRowContent>
                  </Td>
                ))}
              </Tr>
            );
            return (
              <Tbody key={pairIndex}>
                {parentRow}
                {childRow}
              </Tbody>
            );
          })
        ) : (
          <tbody>
            <Tr>
              <Td colSpan={7}>
                <>
                  {isLoading && rowPairs.length === 0 && (
                    <KogitoSpinner
                      spinnerText={'Loading process instances...'}
                    />
                  )}
                  {!isLoading && rowPairs.length === 0 && (
                    <KogitoEmptyState
                      type={KogitoEmptyStateType.Search}
                      title="No results found"
                      body="Try using different filters"
                    />
                  )}
                </>
              </Td>
            </Tr>
          </tbody>
        )}
      </TableComposable>
    </React.Fragment>
  );
};

export default ProcessListTable;
