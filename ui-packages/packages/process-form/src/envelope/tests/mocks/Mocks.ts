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
  RequestPropertyNames
} from '@kie-tools-core/envelope-bus/dist/api';
import { EnvelopeClient } from '@kie-tools-core/envelope-bus/dist/envelope';
import { ProcessFormChannelApi, ProcessFormEnvelopeApi } from '../../../api';
import { ProcessFormEnvelopeViewApi } from '../../ProcessFormEnvelopeView';

export const MockedApiRequests = jest.fn<
  Pick<ProcessFormChannelApi, RequestPropertyNames<ProcessFormChannelApi>>,
  []
>(() => ({
  processForm__getProcessFormSchema: jest.fn(),
  processForm__startProcess: jest.fn()
}));

export const MockedApiNotifications = jest.fn<
  ApiNotificationConsumers<ProcessFormChannelApi>,
  []
>(() => ({}));

export const MockedMessageBusClientApi = jest.fn<
  MessageBusClientApi<ProcessFormChannelApi>,
  []
>(() => ({
  requests: new MockedApiRequests(),
  notifications: new MockedApiNotifications(),
  subscribe: jest.fn(),
  unsubscribe: jest.fn(),
  shared: jest.fn()
}));

export const MockedEnvelopeClient = jest.fn<
  EnvelopeClient<ProcessFormEnvelopeApi, ProcessFormChannelApi>,
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

export const MockedProcessFormEnvelopeViewApi = jest.fn<
  ProcessFormEnvelopeViewApi,
  []
>(() => ({
  initialize: jest.fn()
}));
