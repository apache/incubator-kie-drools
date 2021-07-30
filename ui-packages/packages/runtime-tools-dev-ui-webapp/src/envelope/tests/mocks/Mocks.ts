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
  RuntimeToolsDevUIChannelApi,
  RuntimeToolsDevUIEnvelopeApi
} from '../../../api';
import { EnvelopeBusController } from '@kogito-tooling/envelope-bus/dist/envelope';
import { RuntimeToolsDevUIEnvelopeViewApi } from '../../RuntimeToolsDevUIEnvelopeViewApi';

export const MockedApiRequests = jest.fn<
  Pick<
    RuntimeToolsDevUIChannelApi,
    RequestPropertyNames<RuntimeToolsDevUIChannelApi>
  >,
  []
>(() => ({}));

export const MockedApiNotifications = jest.fn<
  Pick<
    RuntimeToolsDevUIChannelApi,
    NotificationPropertyNames<RuntimeToolsDevUIChannelApi>
  >,
  []
>(() => ({}));

export const MockedMessageBusClientApi = jest.fn<
  MessageBusClientApi<RuntimeToolsDevUIChannelApi>,
  []
>(() => ({
  requests: new MockedApiRequests(),
  notifications: new MockedApiNotifications(),
  subscribe: jest.fn(),
  unsubscribe: jest.fn()
}));

export const MockedEnvelopeBusController = jest.fn<
  EnvelopeBusController<
    RuntimeToolsDevUIEnvelopeApi,
    RuntimeToolsDevUIChannelApi
  >,
  []
>(() => ({
  bus: jest.fn(),
  // @ts-ignore
  manager: jest.fn(),
  associate: jest.fn(),
  channelApi: new MockedMessageBusClientApi(),
  startListening: jest.fn(),
  stopListening: jest.fn(),
  send: jest.fn(),
  receive: jest.fn()
}));

export const MockedRuntimeToolsDevUIEnvelopeViewApi = jest.fn<
  RuntimeToolsDevUIEnvelopeViewApi,
  []
>(() => ({
  setDataIndexUrl: jest.fn(),
  setUsers: jest.fn(),
  navigateTo: jest.fn()
}));
