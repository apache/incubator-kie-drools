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
import { ProcessDetailsDriver } from '../../../api';
import {
  Job,
  JobCancel,
  NodeInstance,
  ProcessInstance,
  SvgErrorResponse,
  SvgSuccessResponse,
  TriggerableNode
} from '@kogito-apps/management-console-shared/dist/types';
import { ProcessDetails } from './Mocks';

export default class TestProcessDetailsDriver implements ProcessDetailsDriver {
  constructor(id: string) {
    this.doSetState(id);
  }

  initialLoad(): Promise<void> {
    return Promise.resolve();
  }

  private doSetState(id: string) {
    // do nothing
  }

  processDetailsQuery(id: string): Promise<ProcessInstance> {
    this.doSetState(id);
    return null;
  }

  openProcessInstanceDetails(id: string): void {
    this.doSetState(id);
    // do nothing
  }

  handleProcessAbort(data: ProcessInstance): Promise<void> {
    return Promise.resolve(undefined);
  }

  cancelJob(job: Pick<Job, 'id' | 'endpoint'>): Promise<JobCancel> {
    return Promise.resolve(undefined);
  }

  getProcessDiagram(
    data: ProcessInstance
  ): Promise<SvgSuccessResponse | SvgErrorResponse> {
    return Promise.resolve(undefined);
  }

  handleProcessVariableUpdate(
    processInstance: ProcessInstance,
    updatedJson: Record<string, unknown>
  ) {
    return Promise.resolve(ProcessDetails.variables);
  }

  rescheduleJob(
    job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> {
    return Promise.resolve({ modalContent: '', modalTitle: '' });
  }

  getTriggerableNodes(
    processInstance: ProcessInstance
  ): Promise<TriggerableNode[]> {
    return new Promise((resolve, reject) => {
      resolve([
        {
          nodeDefinitionId: '_BDA56801-1155-4AF2-94D4-7DAADED2E3C0',
          name: 'Send visa application',
          id: 1,
          type: 'ActionNode',
          uniqueId: '1'
        },
        {
          nodeDefinitionId: '_175DC79D-C2F1-4B28-BE2D-B583DFABF70D',
          name: 'Book',
          id: 2,
          type: 'Split',
          uniqueId: '2'
        },
        {
          nodeDefinitionId: '_E611283E-30B0-46B9-8305-768A002C7518',
          name: 'visasrejected',
          id: 3,
          type: 'EventNode',
          uniqueId: '3'
        }
      ]);
    });
  }

  handleNodeTrigger(
    processInstance: ProcessInstance,
    node: TriggerableNode
  ): Promise<void> {
    return new Promise((resolve, reject) => {
      resolve();
    });
  }

  jobsQuery(id: string): Promise<Job[]> {
    return Promise.resolve([]);
  }

  handleProcessRetry(processInstance: ProcessInstance): Promise<void> {
    return new Promise((resolve, reject) => {
      resolve();
    });
  }

  handleNodeInstanceCancel(
    processInstance: ProcessInstance,
    node: NodeInstance
  ): Promise<void> {
    return new Promise((resolve, reject) => {
      resolve();
    });
  }

  handleProcessSkip(processInstance: ProcessInstance): Promise<void> {
    return new Promise((resolve, reject) => {
      resolve();
    });
  }

  handleNodeInstanceRetrigger(
    processInstance: ProcessInstance,
    node: Pick<NodeInstance, 'id'>
  ): Promise<void> {
    return Promise.resolve(undefined);
  }
}
