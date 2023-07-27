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

import {
  BulkProcessInstanceActionResponse,
  ProcessInstance,
  ProcessInstanceFilter,
  ProcessListSortBy
} from '@kogito-apps/management-console-shared/dist/types';
import { OperationType } from '@kogito-apps/management-console-shared/dist/components/BulkList';
export interface ProcessListChannelApi {
  processList__initialLoad(
    filter: ProcessInstanceFilter,
    sortBy: ProcessListSortBy
  ): Promise<void>;
  processList__openProcess(process: ProcessInstance): Promise<void>;
  processList__applyFilter(filter: ProcessInstanceFilter): Promise<void>;
  processList__applySorting(sortBy: ProcessListSortBy): Promise<void>;
  processList__handleProcessSkip(
    processInstance: ProcessInstance
  ): Promise<void>;
  processList__handleProcessRetry(
    processInstance: ProcessInstance
  ): Promise<void>;
  processList__handleProcessAbort(
    processInstance: ProcessInstance
  ): Promise<void>;
  processList__handleProcessMultipleAction(
    processInstances: ProcessInstance[],
    operationType: OperationType
  ): Promise<BulkProcessInstanceActionResponse>;
  processList__query(offset: number, limit: number): Promise<ProcessInstance[]>;
  processList__getChildProcessesQuery(
    rootProcessInstanceId: string
  ): Promise<ProcessInstance[]>;
  processList__openTriggerCloudEvent(processInstance?: ProcessInstance): void;
}
