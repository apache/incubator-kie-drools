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

import {
  MessageBusClientApi,
  RequestPropertyNames
} from '@kogito-tooling/envelope-bus/dist/api';
import {
  CustomDashboardListChannelApi,
  CustomDashboardListEnvelopeApi
} from '../../../api';
import { MessageBusServer } from '@kogito-tooling/envelope-bus/dist/api';
import { EnvelopeBusMessageManager } from '@kogito-tooling/envelope-bus/dist/common';
import { EnvelopeBusController } from '@kogito-tooling/envelope-bus/dist/envelope';
import { CustomDashboardListEnvelopeViewApi } from '../../CustomDashboardListEnvelopeView';

export const MockedApiRequests = jest.fn<
  Pick<
    CustomDashboardListChannelApi,
    RequestPropertyNames<CustomDashboardListChannelApi>
  >,
  []
>(() => ({
  customDashboardList__getFilter: jest.fn(),
  customDashboardList__applyFilter: jest.fn(),
  customDashboardList__getCustomDashboardQuery: jest.fn(),
  customDashboardList__openDashboard: jest.fn()
}));

export const MockedMessageBusClientApi = jest.fn<
  MessageBusClientApi<CustomDashboardListChannelApi>,
  []
>(() => ({
  requests: new MockedApiRequests(),
  notifications: jest.fn(),
  subscribe: jest.fn(),
  unsubscribe: jest.fn()
}));

export const MockedMessageBusServer = jest.fn<
  MessageBusServer<
    CustomDashboardListEnvelopeApi,
    CustomDashboardListChannelApi
  >,
  []
>(() => ({
  receive: jest.fn()
}));

export const MockedEnvelopeBusMessageManager = jest.fn<
  Partial<
    EnvelopeBusMessageManager<
      CustomDashboardListEnvelopeApi,
      CustomDashboardListChannelApi
    >
  >,
  []
>(() => ({
  callbacks: jest.fn(),
  remoteSubscriptions: jest.fn(),
  localSubscriptions: jest.fn(),
  send: jest.fn(),
  name: jest.fn(),
  requestIdCounter: jest.fn(),
  clientApi: new MockedMessageBusClientApi(),
  server: new MockedMessageBusServer(),
  requests: jest.fn(),
  notifications: jest.fn(),
  subscribe: jest.fn(),
  unsubscribe: jest.fn(),
  request: jest.fn(),
  notify: jest.fn(),
  respond: jest.fn(),
  callback: jest.fn(),
  receive: jest.fn(),
  getNextRequestId: jest.fn()
}));

export const MockedEnvelopeBusControllerDefinition = jest.fn<
  Partial<
    EnvelopeBusController<
      CustomDashboardListEnvelopeApi,
      CustomDashboardListChannelApi
    >
  >,
  []
>(() => ({
  bus: jest.fn(),
  manager: new MockedEnvelopeBusMessageManager() as EnvelopeBusMessageManager<
    CustomDashboardListEnvelopeApi,
    CustomDashboardListChannelApi
  >,
  associate: jest.fn(),
  channelApi: new MockedMessageBusClientApi(),
  startListening: jest.fn(),
  stopListening: jest.fn(),
  send: jest.fn(),
  receive: jest.fn()
}));

export const MockedEnvelopeBusController = new MockedEnvelopeBusControllerDefinition() as EnvelopeBusController<
  CustomDashboardListEnvelopeApi,
  CustomDashboardListChannelApi
>;

export const MockedCustomDashboardListEnvelopeViewApi = jest.fn<
  CustomDashboardListEnvelopeViewApi,
  []
>(() => ({
  initialize: jest.fn()
}));
