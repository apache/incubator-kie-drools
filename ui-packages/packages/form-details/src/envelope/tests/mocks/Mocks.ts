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
  RequestPropertyNames
} from '@kogito-tooling/envelope-bus/dist/api';
import { FormDetailsChannelApi, FormDetailsEnvelopeApi } from '../../../api';
import { EnvelopeBusController } from '@kogito-tooling/envelope-bus/dist/envelope';
import { FormDetailsEnvelopeViewApi } from '../../FormDetailsEnvelopeView';

export const MockedApiRequests = jest.fn<
  Pick<FormDetailsChannelApi, RequestPropertyNames<FormDetailsChannelApi>>,
  []
>(() => ({
  formDetails__getFormContent: jest.fn(),
  formDetails__saveFormContent: jest.fn()
}));

export const MockedMessageBusClientApi = jest.fn<
  MessageBusClientApi<FormDetailsChannelApi>,
  []
>(() => ({
  requests: new MockedApiRequests(),
  notifications: jest.fn(),
  subscribe: jest.fn(),
  unsubscribe: jest.fn()
}));

export const MockedEnvelopeBusController = jest.fn<
  EnvelopeBusController<FormDetailsEnvelopeApi, FormDetailsChannelApi>,
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

export const MockedFormDetailsEnvelopeViewApi = jest.fn<
  FormDetailsEnvelopeViewApi,
  []
>(() => ({
  initialize: jest.fn()
}));
