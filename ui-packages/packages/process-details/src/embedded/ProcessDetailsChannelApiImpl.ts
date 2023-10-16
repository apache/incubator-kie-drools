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
import { ProcessDetailsChannelApi, ProcessDetailsDriver } from '../api';
import {
  ProcessInstance,
  Job,
  JobCancel,
  SvgSuccessResponse,
  SvgErrorResponse,
  TriggerableNode,
  NodeInstance
} from '@kogito-apps/management-console-shared/dist/types';

export class ProcessDetailsChannelApiImpl implements ProcessDetailsChannelApi {
  constructor(private readonly driver: ProcessDetailsDriver) {}

  processDetails__getProcessDiagram(
    data: ProcessInstance
  ): Promise<SvgSuccessResponse | SvgErrorResponse> {
    return this.driver.getProcessDiagram(data);
  }

  processDetails__handleProcessAbort(
    processInstance: ProcessInstance
  ): Promise<void> {
    return this.driver.handleProcessAbort(processInstance);
  }

  processDetails__cancelJob(
    job: Pick<Job, 'id' | 'endpoint'>
  ): Promise<JobCancel> {
    return this.driver.cancelJob(job);
  }

  processDetails__rescheduleJob(
    job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> {
    return this.driver.rescheduleJob(
      job,
      repeatInterval,
      repeatLimit,
      scheduleDate
    );
  }

  processDetails__getTriggerableNodes(
    processInstance: ProcessInstance
  ): Promise<TriggerableNode[]> {
    return this.driver.getTriggerableNodes(processInstance);
  }

  processDetails__handleNodeTrigger(
    processInstance: ProcessInstance,
    node: TriggerableNode
  ): Promise<void> {
    return this.driver.handleNodeTrigger(processInstance, node);
  }

  processDetails__handleProcessVariableUpdate(
    processInstance: ProcessInstance,
    updatedJson: Record<string, unknown>
  ) {
    return this.driver.handleProcessVariableUpdate(
      processInstance,
      updatedJson
    );
  }

  processDetails__processDetailsQuery(id: string): Promise<ProcessInstance> {
    return this.driver.processDetailsQuery(id);
  }

  processDetails__jobsQuery(id: string): Promise<Job[]> {
    return this.driver.jobsQuery(id);
  }
  processDetails__openProcessDetails(id: string): void {
    this.driver.openProcessInstanceDetails(id);
  }

  processDetails__handleProcessRetry(
    processInstance: ProcessInstance
  ): Promise<void> {
    return this.driver.handleProcessRetry(processInstance);
  }

  processDetails__handleNodeInstanceCancel(
    processInstance: ProcessInstance,
    node: NodeInstance
  ): Promise<void> {
    return this.driver.handleNodeInstanceCancel(processInstance, node);
  }

  processDetails__handleProcessSkip(
    processInstance: ProcessInstance
  ): Promise<void> {
    return this.driver.handleProcessSkip(processInstance);
  }

  processDetails__handleNodeInstanceRetrigger(
    processInstance: ProcessInstance,
    node: Pick<NodeInstance, 'id'>
  ): Promise<void> {
    return this.driver.handleNodeInstanceRetrigger(processInstance, node);
  }
}
