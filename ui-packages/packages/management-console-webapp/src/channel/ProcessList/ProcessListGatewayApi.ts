/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import {
  BulkProcessInstanceActionResponse,
  ProcessInstance,
  ProcessInstanceFilter,
  ProcessListSortBy
} from '@kogito-apps/management-console-shared/dist/types';
import { OperationType } from '@kogito-apps/management-console-shared/dist/components/BulkList';
import { ProcessListQueries } from './ProcessListQueries';

export interface ProcessListGatewayApi {
  processListState: ProcessListState;
  initialLoad: (
    filter: ProcessInstanceFilter,
    sortBy: ProcessListSortBy
  ) => Promise<void>;
  openProcess: (process: ProcessInstance) => Promise<void>;
  applyFilter: (filter: ProcessInstanceFilter) => Promise<void>;
  applySorting: (SortBy: ProcessListSortBy) => Promise<void>;
  handleProcessSkip: (processInstance: ProcessInstance) => Promise<void>;
  handleProcessRetry: (processInstance: ProcessInstance) => Promise<void>;
  handleProcessAbort: (processInstance: ProcessInstance) => Promise<void>;
  handleProcessMultipleAction: (
    processInstances: ProcessInstance[],
    operationType: OperationType
  ) => Promise<BulkProcessInstanceActionResponse>;
  query(offset: number, limit: number): Promise<ProcessInstance[]>;
  getChildProcessesQuery(
    rootProcessInstanceId: string
  ): Promise<ProcessInstance[]>;
  onOpenProcessListen: (listener: OnOpenProcessListener) => UnSubscribeHandler;
  openTriggerCloudEvent(processInstance?: ProcessInstance): void;
}

export interface ProcessListState {
  filters: ProcessInstanceFilter;
  sortBy: ProcessListSortBy;
}

export interface OnOpenProcessListener {
  onOpen: (process: ProcessInstance) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}
export class ProcessListGatewayApiImpl implements ProcessListGatewayApi {
  private readonly listeners: OnOpenProcessListener[] = [];
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

  openProcess = (process: ProcessInstance): Promise<void> => {
    this.listeners.forEach((listener) => listener.onOpen(process));
    return Promise.resolve();
  };

  initialLoad = (
    filter: ProcessInstanceFilter,
    sortBy: ProcessListSortBy
  ): Promise<void> => {
    this._ProcessListState.filters = filter;
    this._ProcessListState.sortBy = sortBy;
    return Promise.resolve();
  };

  applyFilter = (filter: ProcessInstanceFilter): Promise<void> => {
    this.processListState.filters = filter;
    return Promise.resolve();
  };

  applySorting = (sortBy: ProcessListSortBy) => {
    this._ProcessListState.sortBy = sortBy;
    return Promise.resolve();
  };

  handleProcessSkip = async (
    processInstance: ProcessInstance
  ): Promise<void> => {
    return this.queries.handleProcessSkip(processInstance);
  };

  handleProcessRetry = async (
    processInstance: ProcessInstance
  ): Promise<void> => {
    return this.queries.handleProcessRetry(processInstance);
  };

  handleProcessAbort = async (
    processInstance: ProcessInstance
  ): Promise<void> => {
    return this.queries.handleProcessAbort(processInstance);
  };

  handleProcessMultipleAction = async (
    processInstances: ProcessInstance[],
    operationType: OperationType
  ): Promise<BulkProcessInstanceActionResponse> => {
    return this.queries.handleProcessMultipleAction(
      processInstances,
      operationType
    );
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
        .then((value) => {
          resolve(value);
        })
        .catch((reason) => {
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
        .then((value) => {
          resolve(value);
        })
        .catch((reason) => {
          reject(reason);
        });
    });
  }

  onOpenProcessListen(listener: OnOpenProcessListener): UnSubscribeHandler {
    this.listeners.push(listener);

    const unSubscribe = () => {
      const index = this.listeners.indexOf(listener);
      if (index > -1) {
        this.listeners.splice(index, 1);
      }
    };

    return {
      unSubscribe
    };
  }

  openTriggerCloudEvent(processInstance?: ProcessInstance): void {
    console.info('not supported');
  }
}
