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
  MessageBusClientApi,
  NotificationPropertyNames,
  RequestPropertyNames
} from '@kogito-tooling/envelope-bus/dist/api';
import {
  ProcessDefinitionListChannelApi,
  ProcessDefinitionListEnvelopeApi
} from '../../../api';
import { EnvelopeBusController } from '@kogito-tooling/envelope-bus/dist/envelope';
import { ProcessDefinitionListEnvelopeViewApi } from '../../ProcessDefinitionListEnvelopeView';

export const MockedApiRequests = jest.fn<
  Pick<
    ProcessDefinitionListChannelApi,
    RequestPropertyNames<ProcessDefinitionListChannelApi>
  >,
  []
>(() => ({
  processDefinitionList__getProcessDefinitionsQuery: jest.fn(),
  processDefinitionList__openProcessForm: jest.fn(),
  processDefinitionList__setProcessDefinitionFilter: jest.fn(),
  processDefinitionList__getProcessDefinitionFilter: jest.fn()
}));

export const MockedApiNotifications = jest.fn<
  Pick<
    ProcessDefinitionListChannelApi,
    NotificationPropertyNames<ProcessDefinitionListChannelApi>
  >,
  []
>(() => ({
  processDefinitionsList__openTriggerCloudEvent: jest.fn()
}));

export const MockedMessageBusClientApi = jest.fn<
  MessageBusClientApi<ProcessDefinitionListChannelApi>,
  []
>(() => ({
  requests: new MockedApiRequests(),
  notifications: new MockedApiNotifications(),
  subscribe: jest.fn(),
  unsubscribe: jest.fn()
}));

export const MockedEnvelopeBusController = jest.fn<
  EnvelopeBusController<
    ProcessDefinitionListEnvelopeApi,
    ProcessDefinitionListChannelApi
  >,
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

export const MockedProcessDefinitionListEnvelopeViewApi = jest.fn<
  ProcessDefinitionListEnvelopeViewApi,
  []
>(() => ({
  initialize: jest.fn()
}));
