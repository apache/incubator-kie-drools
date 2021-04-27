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
import { ProcessInstanceFilter, SortBy } from '@kogito-apps/process-list';
import { ProcessInstance } from '@kogito-apps/management-console-shared';
import { ProcessListQueries } from './ProcessListQueries';

export interface ProcessListGatewayApi {
  processListState: ProcessListState;
  initialLoad: (filter: ProcessInstanceFilter, sortBy: SortBy) => Promise<void>;
  applyFilter: (filter: ProcessInstanceFilter) => Promise<void>;
  applySorting: (SortBy: SortBy) => Promise<void>;
  query(offset: number, limit: number): Promise<ProcessInstance[]>;
  getChildProcessesQuery(
    rootProcessInstanceId: string
  ): Promise<ProcessInstance[]>;
}

export interface ProcessListState {
  filters: ProcessInstanceFilter;
  sortBy: SortBy;
}
export class ProcessListGatewayApiImpl implements ProcessListGatewayApi {
  private readonly queries: ProcessListQueries;
  private _ProcessListState: ProcessListState;

  constructor(queries: ProcessListQueries) {
    this.queries = queries;
    this._ProcessListState = {
      filters: {
        status: [],
        businessKey: []
      },
      sortBy: {}
    };
  }

  get processListState(): ProcessListState {
    return this._ProcessListState;
  }

  initialLoad = (
    filter: ProcessInstanceFilter,
    sortBy: SortBy
  ): Promise<void> => {
    this._ProcessListState.filters = filter;
    this._ProcessListState.sortBy = sortBy;
    return Promise.resolve();
  };

  applyFilter = (filter: ProcessInstanceFilter): Promise<void> => {
    this.processListState.filters = filter;
    return Promise.resolve();
  };

  applySorting = (sortBy: SortBy) => {
    this._ProcessListState.sortBy = sortBy;
    return Promise.resolve();
  };

  query(offset: number, limit: number): Promise<ProcessInstance[]> {
    return new Promise<ProcessInstance[]>((resolve, reject) => {
      this.queries
        .getProcessInstances(
          offset,
          limit,
          this._ProcessListState.filters,
          this._ProcessListState.sortBy
        )
        .then(value => {
          resolve(value);
        })
        .catch(reason => {
          reject(reason);
        });
    });
  }

  getChildProcessesQuery(
    rootProcessInstanceId: string
  ): Promise<ProcessInstance[]> {
    return new Promise<ProcessInstance[]>((resolve, reject) => {
      this.queries
        .getChildProcessInstances(rootProcessInstanceId)
        .then(value => {
          resolve(value);
        })
        .catch(reason => {
          reject(reason);
        });
    });
  }
}
