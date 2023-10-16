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
import { ProcessListChannelApi, ProcessListDriver } from '../api';
import {
  BulkProcessInstanceActionResponse,
  ProcessInstance,
  ProcessInstanceFilter,
  ProcessListSortBy
} from '@kogito-apps/management-console-shared/dist/types';
import { OperationType } from '@kogito-apps/management-console-shared/dist/components/BulkList';

export class ProcessListChannelApiImpl implements ProcessListChannelApi {
  constructor(private readonly driver: ProcessListDriver) {}

  processList__initialLoad(
    filter: ProcessInstanceFilter,
    sortBy: ProcessListSortBy
  ): Promise<void> {
    return this.driver.initialLoad(filter, sortBy);
  }

  processList__openProcess(process: ProcessInstance): Promise<void> {
    return this.driver.openProcess(process);
  }

  processList__applyFilter(filter: ProcessInstanceFilter): Promise<void> {
    return this.driver.applyFilter(filter);
  }

  processList__applySorting(sortBy: ProcessListSortBy): Promise<void> {
    return this.driver.applySorting(sortBy);
  }

  processList__handleProcessSkip(
    processInstance: ProcessInstance
  ): Promise<void> {
    return this.driver.handleProcessSkip(processInstance);
  }
  processList__handleProcessRetry(
    processInstance: ProcessInstance
  ): Promise<void> {
    return this.driver.handleProcessRetry(processInstance);
  }
  processList__handleProcessAbort(
    processInstance: ProcessInstance
  ): Promise<void> {
    return this.driver.handleProcessAbort(processInstance);
  }
  processList__handleProcessMultipleAction(
    processInstances: ProcessInstance[],
    operationType: OperationType
  ): Promise<BulkProcessInstanceActionResponse> {
    return this.driver.handleProcessMultipleAction(
      processInstances,
      operationType
    );
  }
  processList__query(
    offset: number,
    limit: number
  ): Promise<ProcessInstance[]> {
    return this.driver.query(offset, limit);
  }

  processList__getChildProcessesQuery(
    rootProcessInstanceId: string
  ): Promise<ProcessInstance[]> {
    return this.driver.getChildProcessesQuery(rootProcessInstanceId);
  }

  processList__openTriggerCloudEvent(processInstance?: ProcessInstance): void {
    this.driver.openTriggerCloudEvent(processInstance);
  }
}
