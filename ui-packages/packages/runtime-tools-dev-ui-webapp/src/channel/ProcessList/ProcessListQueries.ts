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

import { ApolloClient } from 'apollo-client';
import {
  BulkProcessInstanceActionResponse,
  ProcessInstance,
  ProcessListSortBy,
  ProcessInstanceFilter
} from '@kogito-apps/management-console-shared/dist/types';
import { OperationType } from '@kogito-apps/management-console-shared/dist/components/BulkList';
import {
  handleProcessAbort,
  handleProcessMultipleAction,
  handleProcessSkip,
  handleProcessRetry,
  getProcessInstances,
  getChildProcessInstances
} from '@kogito-apps/runtime-gateway-api';

export interface ProcessListQueries {
  getProcessInstances(
    start: number,
    end: number,
    filters: ProcessInstanceFilter,
    sortBy: ProcessListSortBy
  ): Promise<ProcessInstance[]>;
  getChildProcessInstances(
    rootProcessInstanceId: string
  ): Promise<ProcessInstance[]>;
  handleProcessSkip(processInstance: ProcessInstance): Promise<void>;
  handleProcessAbort(processInstance: ProcessInstance): Promise<void>;
  handleProcessRetry(processInstance: ProcessInstance): Promise<void>;

  handleProcessMultipleAction(
    processInstances: ProcessInstance[],
    operationType: OperationType
  ): Promise<BulkProcessInstanceActionResponse>;
}

export class GraphQLProcessListQueries implements ProcessListQueries {
  private readonly client: ApolloClient<any>;

  constructor(client: ApolloClient<any>) {
    this.client = client;
  }

  getProcessInstances(
    offset: number,
    limit: number,
    filters: ProcessInstanceFilter,
    sortBy: ProcessListSortBy
  ): Promise<ProcessInstance[]> {
    return getProcessInstances(offset, limit, filters, sortBy, this.client);
  }

  getChildProcessInstances(
    rootProcessInstanceId: string
  ): Promise<ProcessInstance[]> {
    return getChildProcessInstances(rootProcessInstanceId, this.client);
  }

  async handleProcessSkip(processInstance: ProcessInstance): Promise<void> {
    return handleProcessSkip(processInstance, this.client);
  }

  async handleProcessAbort(processInstance: ProcessInstance): Promise<void> {
    return handleProcessAbort(processInstance, this.client);
  }

  async handleProcessRetry(processInstance: ProcessInstance): Promise<void> {
    return handleProcessRetry(processInstance, this.client);
  }

  async handleProcessMultipleAction(
    processInstances: ProcessInstance[],
    operationType: OperationType
  ) {
    return handleProcessMultipleAction(
      processInstances,
      operationType,
      this.client
    );
  }
}
