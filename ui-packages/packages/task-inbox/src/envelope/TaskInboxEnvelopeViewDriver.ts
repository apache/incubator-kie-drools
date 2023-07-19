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

import { MessageBusClientApi } from '@kie-tools-core/envelope-bus/dist/api';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import {
  QueryFilter,
  SortBy,
  TaskInboxChannelApi,
  TaskInboxDriver,
  TaskInboxState
} from '../api';

/**
 * Implementation of TaskInboxDriver that delegates calls to the channel Api
 */
export default class TaskInboxEnvelopeViewDriver implements TaskInboxDriver {
  constructor(
    private readonly channelApi: MessageBusClientApi<TaskInboxChannelApi>
  ) {}

  setInitialState(taskInboxState: TaskInboxState): Promise<void> {
    return this.channelApi.requests.taskInbox__setInitialState(taskInboxState);
  }

  applyFilter(filter: QueryFilter): Promise<void> {
    return this.channelApi.requests.taskInbox__applyFilter(filter);
  }

  applySorting(sortBy: SortBy): Promise<void> {
    return this.channelApi.requests.taskInbox__applySorting(sortBy);
  }

  query(offset: number, limit: number): Promise<UserTaskInstance[]> {
    return this.channelApi.requests.taskInbox__query(offset, limit);
  }

  openTask(task: UserTaskInstance): void {
    return this.channelApi.notifications.taskInbox__openTask.send(task);
  }
}
