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
import { ProcessListDriver } from '../../../api';
import {
  ProcessInstance,
  ProcessInstanceState,
  OrderBy,
  ProcessInstanceFilter,
  ProcessListSortBy,
  ProcessListState
} from '@kogito-apps/management-console-shared/dist/types';
import ProcessListTable from '../ProcessListTable/ProcessListTable';
import ProcessListToolbar from '../ProcessListToolbar/ProcessListToolbar';
import { LoadMore } from '@kogito-apps/components-common/dist/components/LoadMore';
import { ServerErrors } from '@kogito-apps/components-common/dist/components/ServerErrors';
import {
  KogitoEmptyState,
  KogitoEmptyStateType
} from '@kogito-apps/components-common/dist/components/KogitoEmptyState';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { ISortBy } from '@patternfly/react-table/dist/js/components/Table';
import _ from 'lodash';
import {
  alterOrderByObj,
  processListDefaultStatusFilter,
  workflowListDefaultStatusFilter
} from '../utils/ProcessListUtils';

import '../styles.css';

interface ProcessListProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: ProcessListDriver;
  initialState: ProcessListState;
  singularProcessLabel: string;
  pluralProcessLabel: string;
  isWorkflow: boolean;
  isTriggerCloudEventEnabled?: boolean;
}
const ProcessList: React.FC<ProcessListProps & OUIAProps> = ({
  driver,
  isEnvelopeConnectedToChannel,
  initialState,
  singularProcessLabel,
  pluralProcessLabel,
  isTriggerCloudEventEnabled = false,
  isWorkflow,
  ouiaId,
  ouiaSafe
}) => {
  const defaultStatusFilter =
    singularProcessLabel == 'Process'
      ? processListDefaultStatusFilter
      : workflowListDefaultStatusFilter;

  const defaultFilters: ProcessInstanceFilter =
    initialState && initialState.filters
      ? { ...initialState.filters }
      : {
          status: defaultStatusFilter,
          businessKey: []
        };
  const defaultOrderBy: any =
    initialState && initialState.sortBy
      ? initialState.sortBy
      : {
          lastUpdate: OrderBy.DESC
        };
  const [defaultPageSize] = useState<number>(10);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isLoadingMore, setIsLoadingMore] = useState<boolean>(false);
  const [offset, setOffset] = useState<number>(0);
  const [limit, setLimit] = useState<number>(defaultPageSize);
  const [pageSize, setPageSize] = useState<number>(defaultPageSize);
  const [processInstances, setProcessInstances] = useState<ProcessInstance[]>(
    []
  );
  const [error, setError] = useState<string>(undefined);
  const [filters, setFilters] = useState<ProcessInstanceFilter>(defaultFilters);
  const [processStates, setProcessStates] =
    useState<ProcessInstanceState[]>(defaultStatusFilter);
  const [expanded, setExpanded] = React.useState<{ [key: number]: boolean }>(
    {}
  );
  const [sortBy, setSortBy] = useState<ProcessListSortBy | ISortBy>(
    defaultOrderBy
  );
  const [selectedInstances, setSelectedInstances] = useState<ProcessInstance[]>(
    []
  );
  const [selectableInstances, setSelectableInstances] = useState<number>(0);
  const [isAllChecked, setIsAllChecked] = useState<boolean>(false);

  useEffect(() => {
    if (isEnvelopeConnectedToChannel) {
      initLoad();
    }
  }, [isEnvelopeConnectedToChannel]);

  useEffect(() => {
    setIsLoading(true);
    if (initialState && initialState.filters) {
      setFilters(initialState.filters);
      setProcessStates(initialState.filters.status);
      setSortBy(initialState.sortBy);
    }
  }, [initialState]);

  const initLoad = async () => {
    setIsLoading(true);
    setFilters(defaultFilters);
    await driver.initialLoad(defaultFilters, defaultOrderBy);
    doQuery(0, 10, true);
  };

  const countExpandableRows = (instances: ProcessInstance[]): void => {
    instances.forEach((processInstance, index) => {
      expanded[index] = false;
      processInstance.isSelected = false;
      processInstance.isOpen = false;
      processInstance.childProcessInstances = [];
      if (
        processInstance.serviceUrl &&
        processInstance.addons.includes('process-management')
      ) {
        setSelectableInstances((prev) => prev + 1);
      }
    });
  };

  const doQuery = async (
    _offset: number,
    _limit: number,
    _resetProcesses: boolean,
    _resetPagination: boolean = false,
    _loadMore: boolean = false
  ): Promise<void> => {
    setIsLoadingMore(_loadMore);
    setSelectableInstances(0);
    setSelectedInstances([]);
    try {
      const response: ProcessInstance[] = await driver.query(_offset, _limit);
      setLimit(response.length);
      if (_resetProcesses) {
        countExpandableRows(response);
        setProcessInstances(response);
      } else {
        const newData = processInstances.concat(response);
        countExpandableRows(newData);
        setProcessInstances(newData);
      }
      if (_resetPagination) {
        setOffset(_offset);
      }
    } catch (err) {
      setError(err.errorMessage);
    } finally {
      setIsLoading(false);
      setIsLoadingMore(false);
    }
  };

  useEffect(() => {
    if (
      selectedInstances.length === selectableInstances &&
      selectableInstances !== 0
    ) {
      setIsAllChecked(true);
    } else {
      setIsAllChecked(false);
    }
  }, [processInstances]);

  const applyFilter = async (filter: ProcessInstanceFilter): Promise<void> => {
    setIsLoading(true);
    setProcessInstances([]);
    await driver.applyFilter(filter);
    doQuery(0, defaultPageSize, true, true);
  };

  const applySorting = async (
    event,
    index: number,
    direction: 'asc' | 'desc'
  ) => {
    setIsLoading(true);
    setProcessInstances([]);
    setSortBy({ index, direction });
    let sortingColumn: string = event.target.innerText;
    sortingColumn = _.camelCase(sortingColumn);
    let sortByObj = _.set({}, sortingColumn, direction.toUpperCase());
    sortByObj = alterOrderByObj(sortByObj);
    await driver.applySorting(sortByObj);
    doQuery(0, defaultPageSize, true, true);
  };

  const doRefresh = async (): Promise<void> => {
    setIsLoading(true);
    setProcessInstances([]);
    doQuery(0, defaultPageSize, true, true);
  };

  const doResetFilters = (): void => {
    const resetFilter = {
      status: defaultStatusFilter,
      businessKey: []
    };
    setIsLoading(true);
    setProcessStates(defaultStatusFilter);
    setFilters(resetFilter);
    applyFilter(resetFilter);
  };

  const mustShowLoadMore =
    (!isLoading || isLoadingMore) &&
    processInstances &&
    limit === pageSize &&
    filters.status.length > 0;

  if (error) {
    return <ServerErrors error={error} variant={'large'} />;
  }

  return (
    <div
      {...componentOuiaProps(
        ouiaId,
        'process-list',
        ouiaSafe ? ouiaSafe : !isLoading
      )}
    >
      <ProcessListToolbar
        applyFilter={applyFilter}
        refresh={doRefresh}
        filters={filters}
        setFilters={setFilters}
        processStates={processStates}
        setProcessStates={setProcessStates}
        selectedInstances={selectedInstances}
        setSelectedInstances={setSelectedInstances}
        processInstances={processInstances}
        setProcessInstances={setProcessInstances}
        isAllChecked={isAllChecked}
        setIsAllChecked={setIsAllChecked}
        driver={driver}
        defaultStatusFilter={defaultStatusFilter}
        singularProcessLabel={singularProcessLabel}
        pluralProcessLabel={pluralProcessLabel}
        isWorkflow={isWorkflow}
        isTriggerCloudEventEnabled={isTriggerCloudEventEnabled}
      />
      {filters.status.length > 0 ? (
        <>
          <ProcessListTable
            processInstances={processInstances}
            isLoading={isLoading}
            expanded={expanded}
            setExpanded={setExpanded}
            driver={driver}
            onSort={applySorting}
            sortBy={sortBy}
            setProcessInstances={setProcessInstances}
            selectedInstances={selectedInstances}
            setSelectedInstances={setSelectedInstances}
            selectableInstances={selectableInstances}
            setSelectableInstances={setSelectableInstances}
            setIsAllChecked={setIsAllChecked}
            singularProcessLabel={singularProcessLabel}
            pluralProcessLabel={pluralProcessLabel}
            isTriggerCloudEventEnabled={isTriggerCloudEventEnabled}
          />
          {mustShowLoadMore && (
            <LoadMore
              offset={offset}
              setOffset={setOffset}
              getMoreItems={(_offset, _limit) => {
                setPageSize(_limit);
                doQuery(_offset, _limit, false, true, true);
              }}
              pageSize={pageSize}
              isLoadingMore={isLoadingMore}
            />
          )}
        </>
      ) : (
        <div className="kogito-process-list__emptyState-card">
          <KogitoEmptyState
            type={KogitoEmptyStateType.Reset}
            title="No filters applied."
            body="Try applying at least one filter to see results"
            onClick={doResetFilters}
          />
        </div>
      )}
    </div>
  );
};

export default ProcessList;
