import {
  EndpointLink,
  GraphQL,
  ItemDescriptor,
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner,
  ServerErrors
} from '@kogito-apps/common';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import { HistoryIcon } from '@patternfly/react-icons';
import { IRow, Table, TableBody, TableHeader } from '@patternfly/react-table';
import {
  getProcessInstanceDescription,
  ProcessInstanceIconCreator
} from '../../../utils/Utils';
import React, { useEffect, useState } from 'react';
import Moment from 'react-moment';
import { Link } from 'react-router-dom';
import _ from 'lodash';
import DisablePopup from '../DisablePopup/DisablePopup';
import { Checkbox } from '@patternfly/react-core';
import ProcessListActionsKebab from '../../Atoms/ProcessListActionsKebab/ProcessListActionsKebab';
import ErrorPopover from '../../Atoms/ErrorPopover/ErrorPopover';
import { filterType } from '../ProcessListToolbar/ProcessListToolbar';
import './ProcessListChildTable.css';
interface IOwnProps {
  parentProcessId: string;
  filters: filterType;
  initData: GraphQL.GetProcessInstancesQuery;
  setInitData: React.Dispatch<
    React.SetStateAction<GraphQL.GetProcessInstancesQuery>
  >;
  setSelectedInstances: React.Dispatch<
    React.SetStateAction<GraphQL.ProcessInstance[]>
  >;
  selectedInstances: GraphQL.ProcessInstance[];
  setSelectableInstances: React.Dispatch<React.SetStateAction<number>>;
  onSkipClick?: (processInstance: GraphQL.ProcessInstance) => Promise<void>;
  onRetryClick?: (processInstance: GraphQL.ProcessInstance) => Promise<void>;
  onAbortClick?: (processInstance: GraphQL.ProcessInstance) => Promise<void>;
}
const ProcessListChildTable: React.FC<IOwnProps & OUIAProps> = ({
  parentProcessId,
  filters,
  initData,
  setInitData,
  setSelectedInstances,
  setSelectableInstances,
  selectedInstances,
  onSkipClick,
  onRetryClick,
  onAbortClick,
  ouiaId,
  ouiaSafe
}) => {
  const [rows, setRows] = useState<(IRow | string[])[]>([]);
  const { loading, error, data } = GraphQL.useGetChildInstancesQuery({
    variables: {
      rootProcessInstanceId: parentProcessId
    }
  });
  const currentPage = { prev: location.pathname };
  window.localStorage.setItem('state', JSON.stringify(currentPage));
  const columns = [
    {
      title: ''
    },
    {
      title: 'Id'
    },
    {
      title: 'Status'
    },
    {
      title: 'Created'
    },
    {
      title: 'Last update'
    },
    {
      title: ''
    }
  ];

  useEffect(() => {
    if (!loading && data) {
      const clonedInitData = JSON.parse(JSON.stringify(initData));
      !_.isEmpty(clonedInitData) &&
        clonedInitData.ProcessInstances.forEach((processInstanceData) => {
          if (processInstanceData.id === parentProcessId) {
            data.ProcessInstances.forEach(
              (
                processInstance: GraphQL.ProcessInstance & {
                  isSelected: boolean;
                }
              ) => {
                processInstance.isSelected = false;
              }
            );
            processInstanceData.childProcessInstances = data.ProcessInstances;
          }
        });

      data.ProcessInstances.forEach(
        (
          instance: GraphQL.ProcessInstance & {
            isSelected: boolean;
          }
        ) => {
          instance.isSelected = false;
          if (
            instance.serviceUrl &&
            instance.addons.includes('process-management')
          ) {
            setSelectableInstances((prev) => prev + 1);
          }
        }
      );
      setInitData(clonedInitData);
    }
  }, [data]);

  useEffect(() => {
    const tempRows = [];
    const processInstance =
      !_.isEmpty(initData) &&
      initData.ProcessInstances.find(
        (processInstanceData) => processInstanceData.id === parentProcessId
      );
    if (
      !_.isEmpty(processInstance) &&
      processInstance['childProcessInstances'] &&
      processInstance['childProcessInstances'].length > 0
    ) {
      processInstance['childProcessInstances'].forEach(
        (child: GraphQL.ProcessInstance & { isSelected: boolean }) => {
          tempRows.push({
            cells: [
              {
                title: (
                  <>
                    {child.addons.includes('process-management') &&
                    child.serviceUrl !== null ? (
                      <Checkbox
                        isChecked={child.isSelected}
                        onChange={() => checkBoxSelect(child)}
                        aria-label="process-list-checkbox"
                        id={`checkbox-${child.id}`}
                        name={`checkbox-${child.id}`}
                      />
                    ) : (
                      <DisablePopup
                        processInstanceData={child}
                        component={
                          <Checkbox
                            aria-label="process-list-checkbox-disabled"
                            id={`checkbox-${child.id}`}
                            isDisabled={true}
                          />
                        }
                      />
                    )}
                  </>
                )
              },
              {
                title: (
                  <>
                    <Link
                      to={{
                        pathname: '/Process/' + child.id,
                        state: { filters }
                      }}
                    >
                      <div>
                        <strong>
                          <ItemDescriptor
                            itemDescription={getProcessInstanceDescription(
                              child
                            )}
                          />
                        </strong>
                      </div>
                    </Link>
                    <EndpointLink
                      serviceUrl={child.serviceUrl}
                      isLinkShown={false}
                    />
                  </>
                )
              },
              {
                title: (
                  <>
                    {child.state === GraphQL.ProcessInstanceState.Error ? (
                      <ErrorPopover
                        processInstanceData={child}
                        onRetryClick={onRetryClick}
                        onSkipClick={onSkipClick}
                      />
                    ) : (
                      ProcessInstanceIconCreator(child.state)
                    )}
                  </>
                )
              },
              {
                title: child.start ? (
                  <Moment fromNow>{new Date(`${child.start}`)}</Moment>
                ) : (
                  ''
                )
              },
              {
                title: child.lastUpdate ? (
                  <span>
                    {' '}
                    <HistoryIcon className="pf-u-mr-sm" /> Updated{' '}
                    <Moment fromNow>{new Date(`${child.lastUpdate}`)}</Moment>
                  </span>
                ) : (
                  ''
                )
              },
              {
                title: (
                  <ProcessListActionsKebab
                    processInstance={child}
                    onSkipClick={onSkipClick}
                    onRetryClick={onRetryClick}
                    onAbortClick={onAbortClick}
                  />
                )
              }
            ]
          });
        }
      );
      setRows(tempRows);
    }
  }, [initData]);

  const checkBoxSelect = (processInstance: GraphQL.ProcessInstance): void => {
    const clonedInitData = { ...initData };
    clonedInitData.ProcessInstances.forEach((instance) => {
      if (instance.id === parentProcessId) {
        instance['childProcessInstances'].forEach((childInstance) => {
          if (childInstance.id === processInstance.id) {
            if (childInstance.isSelected) {
              childInstance.isSelected = false;
              setSelectedInstances(
                selectedInstances.filter(
                  (selectedInstance) => selectedInstance.id !== childInstance.id
                )
              );
            } else {
              childInstance.isSelected = true;
              setSelectedInstances([...selectedInstances, childInstance]);
            }
          }
        });
      }
    });
    setInitData(clonedInitData);
  };

  if (loading) {
    return <KogitoSpinner spinnerText={'Loading child instances...'} />;
  }
  if (error) {
    return <ServerErrors error={error} variant="large" />;
  }
  if (!loading && data && data.ProcessInstances.length === 0) {
    return (
      <KogitoEmptyState
        type={KogitoEmptyStateType.Info}
        title="No child process instances"
        body="This process has no related sub processes"
      />
    );
  }
  return (
    <Table
      aria-label="Process List Child Table"
      cells={columns}
      rows={rows}
      variant={'compact'}
      className="kogito-management-console__compact-table"
      {...componentOuiaProps(
        ouiaId,
        'process-list-child-table',
        ouiaSafe ? ouiaSafe : !loading
      )}
    >
      <TableHeader />
      <TableBody />
    </Table>
  );
};

export default ProcessListChildTable;
