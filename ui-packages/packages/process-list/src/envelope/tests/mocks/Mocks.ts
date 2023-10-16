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
import { ProcessListChannelApi, ProcessListEnvelopeApi } from '../../../api';
import {
  ProcessInstance,
  ProcessInstanceState
} from '@kogito-apps/management-console-shared/dist/types';
import { MessageBusServer } from '@kie-tools-core/envelope-bus/dist/api';
import { EnvelopeBusMessageManager } from '@kie-tools-core/envelope-bus/dist/common';
import { EnvelopeClient } from '@kie-tools-core/envelope-bus/dist/envelope';
import { ProcessListEnvelopeViewApi } from '../../ProcessListEnvelopeView';

export const processInstance: ProcessInstance = {
  id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
  processId: 'travels',
  businessKey: 'GEE21',
  parentProcessInstance: null,
  processName: 'Travels',
  rootProcessInstanceId: null,
  roles: [],
  state: ProcessInstanceState.Active,
  start: new Date('2019-10-22T03:40:44.089Z'),
  end: new Date('2019-10-22T03:40:44.089Z'),
  lastUpdate: new Date('2019-10-22T03:40:44.089Z'),
  serviceUrl: null,
  endpoint: 'http://localhost:4000',
  error: {
    nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-34578e904e6b',
    message: 'some thing went wrong'
  },
  addons: [],
  variables:
    '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
  nodes: [
    {
      nodeId: '1',
      name: 'End Event 1',
      definitionId: 'EndEvent_1',
      id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
      enter: new Date('2019-10-22T03:40:44.089Z'),
      exit: new Date('2019-10-22T03:40:44.089Z'),
      type: 'EndNode'
    }
  ],
  childProcessInstances: []
};

const mockNotificationConsumer = {
  subscribe: jest.fn(),
  unsubscribe: jest.fn(),
  send: jest.fn()
};

export const MockedApiRequests = jest.fn<
  Pick<ProcessListChannelApi, RequestPropertyNames<ProcessListChannelApi>>,
  []
>(() => ({
  processList__initialLoad: jest.fn(),
  processList__openProcess: jest.fn(),
  processList__applyFilter: jest.fn(),
  processList__applySorting: jest.fn(),
  processList__handleProcessSkip: jest.fn(),
  processList__handleProcessRetry: jest.fn(),
  processList__handleProcessAbort: jest.fn(),
  processList__handleProcessMultipleAction: jest.fn(),
  processList__query: jest.fn(),
  processList__getChildProcessesQuery: jest.fn()
}));

export const MockedApiNotifications = jest.fn<
  ApiNotificationConsumers<ProcessListChannelApi>,
  []
>(() => ({
  processList__openTriggerCloudEvent: mockNotificationConsumer
}));

export const MockedMessageBusClientApi = jest.fn<
  MessageBusClientApi<ProcessListChannelApi>,
  []
>(() => ({
  requests: new MockedApiRequests(),
  notifications: new MockedApiNotifications(),
  subscribe: jest.fn(),
  unsubscribe: jest.fn(),
  shared: jest.fn()
}));

export const MockedMessageBusServer = jest.fn<
  MessageBusServer<ProcessListEnvelopeApi, ProcessListChannelApi>,
  []
>(() => ({
  receive: jest.fn()
}));

export const MockedEnvelopeBusMessageManager = jest.fn<
  Partial<
    EnvelopeBusMessageManager<ProcessListEnvelopeApi, ProcessListChannelApi>
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
  Partial<EnvelopeClient<ProcessListEnvelopeApi, ProcessListChannelApi>>,
  []
>(() => ({
  bus: jest.fn(),
  manager: new MockedEnvelopeBusMessageManager() as EnvelopeBusMessageManager<
    ProcessListEnvelopeApi,
    ProcessListChannelApi
  >,
  associate: jest.fn(),
  channelApi: new MockedMessageBusClientApi(),
  startListening: jest.fn(),
  stopListening: jest.fn(),
  send: jest.fn(),
  receive: jest.fn()
}));

export const MockedEnvelopeClient =
  new MockedEnvelopeBusControllerDefinition() as EnvelopeClient<
    ProcessListEnvelopeApi,
    ProcessListChannelApi
  >;

export const MockedProcessListEnvelopeViewApi = jest.fn<
  ProcessListEnvelopeViewApi,
  []
>(() => ({
  initialize: jest.fn()
}));
