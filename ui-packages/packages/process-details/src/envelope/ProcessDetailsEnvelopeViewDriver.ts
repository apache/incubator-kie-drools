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

import { MessageBusClientApi } from '@kie-tools-core/envelope-bus/dist/api';
import { ProcessDetailsChannelApi, ProcessDetailsDriver } from '../api';
import {
  ProcessInstance,
  Job,
  JobCancel,
  SvgSuccessResponse,
  SvgErrorResponse,
  TriggerableNode,
  NodeInstance
} from '@kogito-apps/management-console-shared';

export default class ProcessDetailsEnvelopeViewDriver
  implements ProcessDetailsDriver
{
  constructor(
    private readonly channelApi: MessageBusClientApi<ProcessDetailsChannelApi>
  ) {}

  getProcessDiagram(
    data: ProcessInstance
  ): Promise<SvgSuccessResponse | SvgErrorResponse> {
    return this.channelApi.requests.processDetails__getProcessDiagram(data);
  }

  handleProcessAbort(processInstance: ProcessInstance): Promise<void> {
    return this.channelApi.requests.processDetails__handleProcessAbort(
      processInstance
    );
  }

  cancelJob(job: Pick<Job, 'id' | 'endpoint'>): Promise<JobCancel> {
    return this.channelApi.requests.processDetails__cancelJob(job);
  }

  rescheduleJob(
    job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> {
    return this.channelApi.requests.processDetails__rescheduleJob(
      job,
      repeatInterval,
      repeatLimit,
      scheduleDate
    );
  }

  getTriggerableNodes(
    processInstance: ProcessInstance
  ): Promise<TriggerableNode[]> {
    return this.channelApi.requests.processDetails__getTriggerableNodes(
      processInstance
    );
  }

  handleNodeTrigger(
    processInstance: ProcessInstance,
    node: TriggerableNode
  ): Promise<void> {
    return this.channelApi.requests.processDetails__handleNodeTrigger(
      processInstance,
      node
    );
  }

  handleProcessVariableUpdate(
    processInstance: ProcessInstance,
    updatedJson: Record<string, unknown>
  ) {
    return this.channelApi.requests.processDetails__handleProcessVariableUpdate(
      processInstance,
      updatedJson
    );
  }

  processDetailsQuery(id: string): Promise<ProcessInstance> {
    return this.channelApi.requests.processDetails__processDetailsQuery(id);
  }

  jobsQuery(id: string): Promise<Job[]> {
    return this.channelApi.requests.processDetails__jobsQuery(id);
  }
  openProcessInstanceDetails(id: string): void {
    return this.channelApi.notifications.processDetails__openProcessDetails.send(
      id
    );
  }

  handleProcessRetry(processInstance: ProcessInstance): Promise<void> {
    return this.channelApi.requests.processDetails__handleProcessRetry(
      processInstance
    );
  }

  handleNodeInstanceCancel(
    processInstance: ProcessInstance,
    node: NodeInstance
  ): Promise<void> {
    return this.channelApi.requests.processDetails__handleNodeInstanceCancel(
      processInstance,
      node
    );
  }

  handleProcessSkip(processInstance: ProcessInstance): Promise<void> {
    return this.channelApi.requests.processDetails__handleProcessSkip(
      processInstance
    );
  }

  handleNodeInstanceRetrigger(
    processInstance: ProcessInstance,
    node: Pick<NodeInstance, 'id'>
  ): Promise<void> {
    return this.channelApi.requests.processDetails__handleNodeInstanceRetrigger(
      processInstance,
      node
    );
  }
}
