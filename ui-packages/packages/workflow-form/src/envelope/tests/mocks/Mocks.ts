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
  ApiNotificationConsumers,
  MessageBusClientApi,
  NotificationPropertyNames,
  RequestPropertyNames
} from '@kie-tools-core/envelope-bus/dist/api';
import { EnvelopeClient } from '@kie-tools-core/envelope-bus/dist/envelope';
import { WorkflowFormChannelApi, WorkflowFormEnvelopeApi } from '../../../api';
import { WorkflowFormEnvelopeViewApi } from '../../WorkflowFormEnvelopeView';

export const workflowForm__resetBusinessKey = jest.fn();
export const workflowForm__getCustomWorkflowSchema = jest.fn();
export const workflowForm__startWorkflow = jest.fn();

export const workflowSchema = {
  title: 'Expression',
  description: 'Schema for expression test',
  type: 'object',
  properties: {
    numbers: {
      description: 'The array of numbers to be operated with',
      type: 'array',
      items: {
        type: 'object',
        properties: {
          x: { type: 'number' },
          y: { type: 'number' }
        }
      }
    }
  },
  required: ['numbers']
};
export const MockedApiRequests = jest.fn<
  Pick<WorkflowFormChannelApi, RequestPropertyNames<WorkflowFormChannelApi>>,
  []
>(() => ({
  workflowForm__resetBusinessKey: workflowForm__resetBusinessKey,
  workflowForm__getCustomWorkflowSchema: workflowForm__getCustomWorkflowSchema,
  workflowForm__startWorkflow: workflowForm__startWorkflow
}));

export const MockedApiNotifications = jest.fn<
  ApiNotificationConsumers<WorkflowFormChannelApi>,
  []
>(() => ({}));

export const MockedMessageBusClientApi = jest.fn<
  MessageBusClientApi<WorkflowFormChannelApi>,
  []
>(() => ({
  requests: new MockedApiRequests(),
  notifications: new MockedApiNotifications(),
  subscribe: jest.fn(),
  unsubscribe: jest.fn(),
  shared: jest.fn()
}));

export const MockedEnvelopeClient = jest.fn<
  EnvelopeClient<WorkflowFormEnvelopeApi, WorkflowFormChannelApi>,
  []
>(() => ({
  bus: jest.fn(),
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  manager: jest.fn(),
  associate: jest.fn(),
  channelApi: new MockedMessageBusClientApi(),
  startListening: jest.fn(),
  stopListening: jest.fn(),
  send: jest.fn(),
  receive: jest.fn()
}));

export const MockedWorkflowFormEnvelopeViewApi = jest.fn<
  WorkflowFormEnvelopeViewApi,
  []
>(() => ({
  initialize: jest.fn()
}));
