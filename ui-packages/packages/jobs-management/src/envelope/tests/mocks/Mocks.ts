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
} from '@kie-tools-core/envelope-bus/dist/api';
import {
  JobsManagementChannelApi,
  JobsManagementEnvelopeApi
} from '../../../api';
import {
  Job,
  JobStatus
} from '@kogito-apps/management-console-shared/dist/types';
import { MessageBusServer } from '@kie-tools-core/envelope-bus/dist/api';
import { EnvelopeBusMessageManager } from '@kie-tools-core/envelope-bus/dist/common';
import { EnvelopeClient } from '@kie-tools-core/envelope-bus/dist/envelope';
import { JobsManagementEnvelopeViewApi } from '../../JobsManagementEnvelopeView';

export const Jobs: Job = {
  callbackEndpoint:
    'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
  endpoint: 'http://localhost:4000/jobs',
  executionCounter: 0,
  expirationTime: new Date('2020-08-29T04:35:54.631Z'),
  id: 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0',
  lastUpdate: new Date('2020-06-29T03:35:54.635Z'),
  priority: 0,
  processId: 'travels',
  processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
  repeatInterval: null,
  repeatLimit: null,
  retries: 2,
  rootProcessId: '',
  scheduledId: null,
  status: JobStatus.Scheduled
};

export const MockedApiRequests = jest.fn<
  Pick<
    JobsManagementChannelApi,
    RequestPropertyNames<JobsManagementChannelApi>
  >,
  []
>(() => ({
  jobList__initialLoad: jest.fn(),
  jobList__applyFilter: jest.fn(),
  jobList__bulkCancel: jest.fn(),
  jobList_cancelJob: jest.fn(),
  jobList_rescheduleJob: jest.fn(),
  jobList_sortBy: jest.fn(),
  jobList__query: jest.fn()
}));

export const MockedApiNotifications = jest.fn<
  Pick<
    JobsManagementChannelApi,
    NotificationPropertyNames<JobsManagementChannelApi>
  >,
  []
>(() => ({
  jobList__sortBy: jest.fn()
}));

export const MockedMessageBusClientApi = jest.fn<
  MessageBusClientApi<JobsManagementChannelApi>,
  []
>(() => ({
  requests: new MockedApiRequests(),
  notifications: new MockedApiNotifications(),
  subscribe: jest.fn(),
  unsubscribe: jest.fn(),
  shared: jest.fn()
}));

export const MockedMessageBusServer = jest.fn<
  MessageBusServer<JobsManagementEnvelopeApi, JobsManagementChannelApi>,
  []
>(() => ({
  receive: jest.fn()
}));

export const MockedEnvelopeBusMessageManager = jest.fn<
  Partial<
    EnvelopeBusMessageManager<
      JobsManagementEnvelopeApi,
      JobsManagementChannelApi
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

export const MockedEnvelopeClientDefinition = jest.fn<
  Partial<EnvelopeClient<JobsManagementEnvelopeApi, JobsManagementChannelApi>>,
  []
>(() => ({
  bus: jest.fn(),
  manager: new MockedEnvelopeBusMessageManager() as EnvelopeBusMessageManager<
    JobsManagementEnvelopeApi,
    JobsManagementChannelApi
  >,
  associate: jest.fn(),
  channelApi: new MockedMessageBusClientApi(),
  startListening: jest.fn(),
  stopListening: jest.fn(),
  send: jest.fn(),
  receive: jest.fn()
}));

export const MockedEnvelopeClient =
  new MockedEnvelopeClientDefinition() as EnvelopeClient<
    JobsManagementEnvelopeApi,
    JobsManagementChannelApi
  >;

export const MockedJobsManagementEnvelopeViewApi = jest.fn<
  JobsManagementEnvelopeViewApi,
  []
>(() => ({
  initialize: jest.fn()
}));
