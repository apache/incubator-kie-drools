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
import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import { QueryFilter, SortBy, TaskInboxState } from './TaskInboxEnvelopeApi';

/**
 * Channel Api for Task Inbox
 */
export interface TaskInboxChannelApi {
  /**
   * Initializes the channel with an initial state. This will only be called if the channel doesn't provide a default
   * state to TaskInbox.
   * @param initState
   */
  taskInbox__setInitialState(initState: TaskInboxState): Promise<void>;

  /**
   * Sets a filter to be applied to the queries.
   * @param filter
   */
  taskInbox__applyFilter(filter: QueryFilter): Promise<void>;

  /**
   * Sets a sorting to be applied to the queries.
   * @param sortBy
   */
  taskInbox__applySorting(sortBy: SortBy): Promise<void>;

  /**
   * Requests the channel to query a range of user tasks. The query must apply the filters and sorting configured.
   * @param offset - the starting index of the query.
   * @param limit - the maximum number of results expected.
   *
   * @return a Promise<UserTaskInstance[]> that will be resolved with the query result
   */
  taskInbox__query(offset: number, limit: number): Promise<UserTaskInstance[]>;

  /**
   * Notifies the channel that a UserTaskInstance has been opened in TaskInbox
   * @param task
   */
  taskInbox__openTask(task: UserTaskInstance): void;
}
