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
  MessageBusClientApi,
  RequestPropertyNames,
  NotificationPropertyNames,
  ApiNotificationConsumers
} from '@kie-tools-core/envelope-bus/dist/api';
import { MockedMessageBusClientApi, userTask } from './mocks/Mocks';
import TaskInboxEnvelopeViewDriver from '../TaskInboxEnvelopeViewDriver';
import {
  QueryFilter,
  SortBy,
  TaskInboxChannelApi,
  TaskInboxState
} from '../../api';

let channelApi: MessageBusClientApi<TaskInboxChannelApi>;
let requests: Pick<
  TaskInboxChannelApi,
  RequestPropertyNames<TaskInboxChannelApi>
>;
let notifications: ApiNotificationConsumers<TaskInboxChannelApi>;
let driver: TaskInboxEnvelopeViewDriver;

describe('TaskInboxEnvelopeViewDriver tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    channelApi = new MockedMessageBusClientApi();
    requests = channelApi.requests;
    notifications = channelApi.notifications;
    driver = new TaskInboxEnvelopeViewDriver(channelApi);
  });

  describe('Requests', () => {
    it('setInitialState', () => {
      const initialState: TaskInboxState = {
        filters: {
          taskNames: [],
          taskStates: []
        },
        sortBy: {
          property: 'lastUpdate',
          direction: 'asc'
        },
        currentPage: {
          offset: 0,
          limit: 10
        }
      };

      driver.setInitialState(initialState);

      expect(requests.taskInbox__setInitialState).toHaveBeenCalledWith(
        initialState
      );
    });

    it('applyFilter', () => {
      const filter: QueryFilter = {
        taskStates: [],
        taskNames: []
      };
      driver.applyFilter(filter);

      expect(requests.taskInbox__applyFilter).toHaveBeenCalledWith(filter);
    });

    it('applySorting', () => {
      const sortBy: SortBy = {
        property: 'lastUpdate',
        direction: 'asc'
      };
      driver.applySorting(sortBy);

      expect(requests.taskInbox__applySorting).toHaveBeenCalledWith(sortBy);
    });

    it('query', () => {
      driver.query(0, 10);

      expect(requests.taskInbox__query).toHaveBeenCalledWith(0, 10);
    });
  });

  describe('Notifications', () => {
    it('openTask', () => {
      driver.openTask(userTask);

      expect(notifications.taskInbox__openTask.send).toHaveBeenCalledWith(
        userTask
      );
    });
  });
});
