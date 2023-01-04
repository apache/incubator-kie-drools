import React, { useEffect, useState } from 'react';
import {
  TableComposable,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  ExpandableRowContent,
  ISortBy
} from '@patternfly/react-table';
import {
  EndpointLink,
  GraphQL,
  ItemDescriptor,
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner
} from '@kogito-apps/common';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import {
  getProcessInstanceDescription,
  handleAbort,
  handleRetry,
  handleSkip,
  ProcessInstanceIconCreator,
  setTitle
} from '../../../utils/Utils';
import { Link } from 'react-router-dom';
import Moment from 'react-moment';
import { HistoryIcon } from '@patternfly/react-icons';
import ProcessListChildTable from '../../Molecules/ProcessListChildTable/ProcessListChildTable';
import { Checkbox } from '@patternfly/react-core';
import _ from 'lodash';
import DisablePopup from '../../Molecules/DisablePopup/DisablePopup';
import ProcessListActionsKebab from '../../Atoms/ProcessListActionsKebab/ProcessListActionsKebab';
import ErrorPopover from '../../Atoms/ErrorPopover/ErrorPopover';
import { filterType } from '../../Molecules/ProcessListToolbar/ProcessListToolbar';
import ProcessListModal from '../../Atoms/ProcessListModal/ProcessListModal';

export enum TitleType {
  SUCCESS = 'success',
  FAILURE = 'failure'
}
interface IOwnProps {
  initData: GraphQL.GetProcessInstancesQuery;
  setInitData: React.Dispatch<
    React.SetStateAction<GraphQL.GetProcessInstancesQuery>
  >;
  loading: boolean;
  filters: filterType;
  expanded: { [key: number]: boolean };
  setExpanded: React.Dispatch<React.SetStateAction<{ [key: number]: boolean }>>;
  setSelectedInstances: React.Dispatch<
    React.SetStateAction<GraphQL.ProcessInstance[]>
  >;
  selectedInstances: GraphQL.ProcessInstance[];
  setSelectableInstances: React.Dispatch<React.SetStateAction<number>>;
  setIsAllChecked: (isAllChecked: boolean) => void;
  selectableInstances: number;
  onSort: (event: any, index: number, direction: 'desc' | 'asc') => void;
  sortBy: ISortBy;
}

interface RowPairType {
  id: string;
  parent: JSX.Element[];
  child: string[];
  noPadding?: boolean;
}

const ProcessListTable: React.FC<IOwnProps & OUIAProps> = ({
  initData,
  loading,
  filters,
  setExpanded,
  expanded,
  setInitData,
  setSelectedInstances,
  selectedInstances,
  setSelectableInstances,
  setIsAllChecked,
  selectableInstances,
  onSort,
  sortBy,
  ouiaId,
  ouiaSafe
}) => {
  const [rowPairs, setRowPairs] = useState<RowPairType[]>([]);
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
    useState<GraphQL.ProcessInstance>(null);
  const currentPage = { prev: location.pathname };
  window.localStorage.setItem('state', JSON.stringify(currentPage));

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

  const onShowMessage = (
    title: string,
    content: string,
    type: TitleType,
    processInstance
  ): void => {
    setSelectedProcessInstance(processInstance);
    setTitleType(type);
    setModalTitle(title);
    setModalContent(content);
    handleModalToggle();
  };

  const onSkipClick = async (
    processInstance: GraphQL.ProcessInstance
  ): Promise<void> => {
    await handleSkip(
      processInstance,
      () =>
        onShowMessage(
          'Skip operation',
          `The process ${processInstance.processName} was successfully skipped.`,
          TitleType.SUCCESS,
          processInstance
        ),
      (errorMessage: string) =>
        onShowMessage(
          'Skip operation',
          `The process ${processInstance.processName} failed to skip. Message: ${errorMessage}`,
          TitleType.FAILURE,
          processInstance
        )
    );
  };

  const onRetryClick = async (
    processInstance: GraphQL.ProcessInstance
  ): Promise<void> => {
    await handleRetry(
      processInstance,
      () =>
        onShowMessage(
          'Retry operation',
          `The process ${processInstance.processName} was successfully re-executed.`,
          TitleType.SUCCESS,
          processInstance
        ),
      (errorMessage: string) =>
        onShowMessage(
          'Retry operation',
          `The process ${processInstance.processName} failed to re-execute. Message: ${errorMessage}`,
          TitleType.FAILURE,
          processInstance
        )
    );
  };

  const onAbortClick = async (
    processInstance: GraphQL.ProcessInstance
  ): Promise<void> => {
    await handleAbort(
      processInstance,
      () =>
        onShowMessage(
          'Abort operation',
          `The process ${processInstance.processName} was successfully aborted.`,
          TitleType.SUCCESS,
          processInstance
        ),
      (errorMessage: string) =>
        onShowMessage(
          'Abort operation',
          `Failed to abort process ${processInstance.processName}. Message: ${errorMessage}`,
          TitleType.FAILURE,
          processInstance
        )
    );
  };

  useEffect(() => {
    const tempRowPairs = [];
    if (!loading && Object.keys(initData).length !== 0) {
      initData.ProcessInstances.forEach(
        (
          processInstance: GraphQL.ProcessInstance & { isSelected?: boolean }
        ) => {
          tempRowPairs.push({
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
                <Link
                  to={{
                    pathname: '/Process/' + processInstance.id,
                    state: { filters }
                  }}
                >
                  <div>
                    <strong>
                      <ItemDescriptor
                        itemDescription={getProcessInstanceDescription(
                          processInstance
                        )}
                      />
                    </strong>
                  </div>
                </Link>
                <EndpointLink
                  serviceUrl={processInstance.serviceUrl}
                  isLinkShown={false}
                />
              </>,
              <>
                {processInstance.state ===
                GraphQL.ProcessInstanceState.Error ? (
                  <ErrorPopover
                    processInstanceData={processInstance}
                    onSkipClick={onSkipClick}
                    onRetryClick={onRetryClick}
                  />
                ) : (
                  ProcessInstanceIconCreator(processInstance.state)
                )}
              </>,
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
        }
      );
      setRowPairs(tempRowPairs);
    }
  }, [initData]);

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
          filters={filters}
          initData={initData}
          setInitData={setInitData}
          setSelectedInstances={setSelectedInstances}
          selectedInstances={selectedInstances}
          setSelectableInstances={setSelectableInstances}
          onSkipClick={onSkipClick}
          onRetryClick={onRetryClick}
          onAbortClick={onAbortClick}
          ouiaId={parentId}
        />
      );
    }
  };

  const checkBoxSelect = (
    processInstance: GraphQL.ProcessInstance & { isSelected?: boolean }
  ): void => {
    const clonedInitData = { ...initData };
    clonedInitData.ProcessInstances.forEach(
      (instance: GraphQL.ProcessInstance & { isSelected?: boolean }) => {
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
      }
    );
    setInitData(clonedInitData);
  };

  const onToggle = (pairIndex: number, pair: RowPairType): void => {
    setExpanded({
      ...expanded,
      [pairIndex]: !expanded[pairIndex]
    });

    if (expanded[pairIndex]) {
      const processInstance =
        !loading &&
        !_.isEmpty(initData) &&
        initData.ProcessInstances.find((instance) => instance.id === pair.id);
      !_.isEmpty(processInstance['childProcessInstances']) &&
        processInstance['childProcessInstances'].forEach(
          (
            childInstance: GraphQL.ProcessInstance & {
              isOpen?: boolean;
              isSelected?: boolean;
            }
          ) => {
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
      !loading &&
        !_.isEmpty(initData) &&
        initData.ProcessInstances.forEach(
          (instance: GraphQL.ProcessInstance & { isOpen?: boolean }) => {
            if (processInstance.id === instance.id) {
              instance.isOpen = false;
              instance.childProcessInstances.forEach((child) => {
                if (
                  child.serviceUrl &&
                  child.addons.includes('process-management')
                ) {
                  setSelectableInstances((prev) => prev - 1);
                }
              });
            }
          }
        );
    } else {
      const processInstance =
        !loading &&
        !_.isEmpty(initData) &&
        initData.ProcessInstances.find((instance) => instance.id === pair.id);
      !loading &&
        !_.isEmpty(initData) &&
        initData.ProcessInstances.forEach(
          (instance: GraphQL.ProcessInstance & { isOpen?: boolean }) => {
            if (processInstance.id === instance.id) {
              instance.isOpen = true;
            }
          }
        );
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
      <ProcessListModal
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
          ouiaSafe ? ouiaSafe : !loading
        )}
      >
        <Thead>
          <Tr ouiaId="process-list-table-header">
            {columns.map((column, columnIndex) => {
              let sortParams = {};
              if (!loading && rowPairs.length > 0) {
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
        {!loading && initData && rowPairs.length > 0 ? (
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
                    onToggle: (event) => onToggle(pairIndex, pair)
                  }}
                  {...componentOuiaProps(
                    columns[0].toLocaleLowerCase(),
                    'process-list-cell',
                    true
                  )}
                />
                {pair.parent.map((cell, cellIndex) => (
                  <Td
                    key={`${pairIndex}-parent-${cellIndex}`}
                    dataLabel={columns[cellIndex + 1]}
                    {...componentOuiaProps(
                      columns[cellIndex + 1].toLowerCase(),
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
              <Tbody key={pairIndex} isExpanded={expanded[pairIndex] === true}>
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
                  {loading && (
                    <KogitoSpinner
                      spinnerText={'Loading process instances...'}
                    />
                  )}
                  {!loading && rowPairs.length === 0 && (
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
