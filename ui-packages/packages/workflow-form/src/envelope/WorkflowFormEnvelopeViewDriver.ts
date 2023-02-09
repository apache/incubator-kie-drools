/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { MessageBusClientApi } from '@kogito-tooling/envelope-bus/dist/api';
import { WorkflowFormChannelApi, WorkflowFormDriver } from '../api';

/**
 * Implementation of WorkflowFormDriver to be used on WorkflowFormEnvelopeView
 */
export class WorkflowFormEnvelopeViewDriver implements WorkflowFormDriver {
  constructor(
    private readonly channelApi: MessageBusClientApi<WorkflowFormChannelApi>
  ) {}

  resetBusinessKey(): Promise<void> {
    return this.channelApi.requests.workflowForm__resetBusinessKey();
  }

  getCustomWorkflowSchema(): Promise<Record<string, any>> {
    return this.channelApi.requests.workflowForm__getCustomWorkflowSchema();
  }

  startWorkflow(endpoint: string, data: Record<string, any>): Promise<void> {
    return this.channelApi.requests.workflowForm__startWorkflow(endpoint, data);
  }
}
