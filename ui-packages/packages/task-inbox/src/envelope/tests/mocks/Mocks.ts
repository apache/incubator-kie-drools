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
import { TaskInboxChannelApi, TaskInboxEnvelopeApi } from '../../../api';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import { EnvelopeBusController } from '@kogito-tooling/envelope-bus/dist/envelope';
import { TaskInboxEnvelopeViewApi } from '../../TaskInboxEnvelopeView';

export const userTask: UserTaskInstance = {
  id: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
  description: null,
  name: 'VisaApplication',
  referenceName: 'Apply for visa',
  priority: '1',
  processInstanceId: '9ae7ce3b-d49c-4f35-b843-8ac3d22fa427',
  processId: 'travels',
  rootProcessInstanceId: null,
  rootProcessId: null,
  state: 'Ready',
  actualOwner: null,
  adminGroups: [],
  adminUsers: [],
  completed: null,
  started: new Date('2020-02-19T11:11:56.282Z'),
  excludedUsers: [],
  potentialGroups: [],
  potentialUsers: [],
  inputs:
    '{"Skippable":"true","trip":{"city":"Boston","country":"US","begin":"2020-02-19T23:00:00.000+01:00","end":"2020-02-26T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","traveller":{"firstName":"Rachel","lastName":"White","email":"rwhite@gorle.com","nationality":"Polish","address":{"street":"Cabalone","city":"Zerf","zipCode":"765756","country":"Poland"}},"Priority":"1"}',
  outputs: '{}',
  lastUpdate: new Date('2020-02-19T11:11:56.282Z'),
  endpoint:
    'http://localhost:8080/travels/9ae7ce3b-d49c-4f35-b843-8ac3d22fa427/VisaApplication/45a73767-5da3-49bf-9c40-d533c3e77ef3'
};

export const MockedApiRequests = jest.fn<
  Pick<TaskInboxChannelApi, RequestPropertyNames<TaskInboxChannelApi>>,
  []
>(() => ({
  taskInbox__applySorting: jest.fn(),
  taskInbox__refresh: jest.fn(),
  taskInbox__setInitialState: jest.fn(),
  taskInbox__query: jest.fn(),
  taskInbox__applyFilter: jest.fn()
}));

export const MockedApiNotifications = jest.fn<
  Pick<TaskInboxChannelApi, NotificationPropertyNames<TaskInboxChannelApi>>,
  []
>(() => ({
  taskInbox__openTask: jest.fn()
}));

export const MockedMessageBusClientApi = jest.fn<
  MessageBusClientApi<TaskInboxChannelApi>,
  []
>(() => ({
  requests: new MockedApiRequests(),
  notifications: new MockedApiNotifications(),
  subscribe: jest.fn(),
  unsubscribe: jest.fn()
}));

export const MockedEnvelopeBusController = jest.fn<
  EnvelopeBusController<TaskInboxEnvelopeApi, TaskInboxChannelApi>,
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

export const MockedTaskInboxEnvelopeViewApi = jest.fn<
  TaskInboxEnvelopeViewApi,
  []
>(() => ({
  initialize: jest.fn(),
  notify: jest.fn()
}));
