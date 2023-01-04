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
  RequestPropertyNames,
  NotificationPropertyNames
} from '@kogito-tooling/envelope-bus/dist/api';
import {
  FormDisplayerChannelApi,
  FormDisplayerEnvelopeApi
} from '../../../api';
import { MessageBusServer } from '@kogito-tooling/envelope-bus/dist/api';
import { EnvelopeBusMessageManager } from '@kogito-tooling/envelope-bus/dist/common';
import { EnvelopeBusController } from '@kogito-tooling/envelope-bus/dist/envelope';
import { FormDisplayerEnvelopeViewApi } from '../../FormDisplayerEnvelopeView';

export const MockedApiRequests = jest.fn<
  Pick<FormDisplayerChannelApi, RequestPropertyNames<FormDisplayerChannelApi>>,
  []
>(() => ({}));

export const MockedApiNotifications = jest.fn<
  Pick<
    FormDisplayerChannelApi,
    NotificationPropertyNames<FormDisplayerChannelApi>
  >,
  []
>(() => ({
  notifyOnOpenForm: jest.fn()
}));

export const MockedMessageBusClientApi = jest.fn<
  MessageBusClientApi<FormDisplayerChannelApi>,
  []
>(() => ({
  requests: new MockedApiRequests(),
  notifications: new MockedApiNotifications(),
  subscribe: jest.fn(),
  unsubscribe: jest.fn()
}));

export const MockedMessageBusServer = jest.fn<
  MessageBusServer<FormDisplayerEnvelopeApi, FormDisplayerChannelApi>,
  []
>(() => ({
  receive: jest.fn()
}));

export const MockedEnvelopeBusMessageManager = jest.fn<
  Partial<
    EnvelopeBusMessageManager<FormDisplayerEnvelopeApi, FormDisplayerChannelApi>
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
    EnvelopeBusController<FormDisplayerEnvelopeApi, FormDisplayerChannelApi>
  >,
  []
>(() => ({
  bus: jest.fn(),
  manager: new MockedEnvelopeBusMessageManager() as EnvelopeBusMessageManager<
    FormDisplayerEnvelopeApi,
    FormDisplayerChannelApi
  >,
  associate: jest.fn(),
  channelApi: new MockedMessageBusClientApi(),
  startListening: jest.fn(),
  stopListening: jest.fn(),
  send: jest.fn(),
  receive: jest.fn()
}));

export const MockedEnvelopeBusController =
  new MockedEnvelopeBusControllerDefinition() as EnvelopeBusController<
    FormDisplayerEnvelopeApi,
    FormDisplayerChannelApi
  >;

export const MockedFormDisplayerEnvelopeViewApi = jest.fn<
  FormDisplayerEnvelopeViewApi,
  []
>(() => ({
  initForm: jest.fn(),
  startSubmit: jest.fn(),
  notifySubmitResponse: jest.fn()
}));
