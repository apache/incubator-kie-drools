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
import { MessageBusClientApi } from '@kie-tools-core/envelope-bus/dist/api';
import { TaskFormChannelApi, TaskFormDriver } from '../api';
import { CustomForm } from '../types';

/**
 * Implementation of TaskFormDriver to be used on TaskFormEnvelopeView
 */
export class TaskFormEnvelopeViewDriver implements TaskFormDriver {
  constructor(
    private readonly channelApi: MessageBusClientApi<TaskFormChannelApi>
  ) {}

  getTaskFormSchema(): Promise<Record<string, any>> {
    return this.channelApi.requests.taskForm__getTaskFormSchema();
  }

  getCustomForm(): Promise<CustomForm> {
    return this.channelApi.requests.taskForm__getCustomForm();
  }

  doSubmit(phase?: string, payload?: any): Promise<any> {
    return this.channelApi.requests.taskForm__doSubmit(phase, payload);
  }
}
