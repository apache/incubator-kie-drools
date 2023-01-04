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

import { MessageBusClientApi } from '@kogito-tooling/envelope-bus/dist/api';
import {
  ProcessDefinition,
  ProcessDefinitionListChannelApi,
  ProcessDefinitionListDriver
} from '../api';

/**
 * Implementation of ProcessDefinitionListDriver that delegates calls to the channel Api
 */
export default class ProcessDefinitionListEnvelopeViewDriver
  implements ProcessDefinitionListDriver
{
  constructor(
    private readonly channelApi: MessageBusClientApi<ProcessDefinitionListChannelApi>
  ) {}
  setProcessDefinitionFilter(filter: string[]): Promise<void> {
    return this.channelApi.requests.processDefinitionList__setProcessDefinitionFilter(
      filter
    );
  }
  getProcessDefinitionFilter(): Promise<string[]> {
    return this.channelApi.requests.processDefinitionList__getProcessDefinitionFilter();
  }
  openProcessForm(processDefinition: ProcessDefinition): Promise<void> {
    return this.channelApi.requests.processDefinitionList__openProcessForm(
      processDefinition
    );
  }

  getProcessDefinitionsQuery(): Promise<ProcessDefinition[]> {
    return this.channelApi.requests.processDefinitionList__getProcessDefinitionsQuery();
  }
}
