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
  QueryFilter,
  SortBy,
  TaskInboxState,
  UserTaskInstance
} from '@kogito-apps/task-inbox';
import { TaskInboxQueries } from './TaskInboxQueries';
import { User } from '@kogito-apps/consoles-common';

export interface TaskInboxGatewayApi {
  taskInboxState: TaskInboxState;
  setInitialState: (initialState: TaskInboxState) => Promise<void>;
  applyFilter(filter: QueryFilter): Promise<void>;
  applySorting(sortBy: SortBy): Promise<void>;
  query(offset: number, limit: number): Promise<UserTaskInstance[]>;
  getTaskById(uuid: string): Promise<UserTaskInstance>;
  openTask: (userTask: UserTaskInstance) => void;
  clearOpenTask: () => Promise<void>;
}

export class TaskInboxGatewayApiImpl implements TaskInboxGatewayApi {
  private readonly user: User;
  private readonly queries: TaskInboxQueries;
  private readonly onOpenTask: (task: UserTaskInstance) => void;
  private _taskInboxState: TaskInboxState;
  private activeTask: UserTaskInstance;

  constructor(
    user: User,
    queries: TaskInboxQueries,
    onOpenTask: (task: UserTaskInstance) => void
  ) {
    this.user = user;
    this.queries = queries;
    this.onOpenTask = onOpenTask;
  }

  get taskInboxState(): TaskInboxState {
    return this._taskInboxState;
  }

  setInitialState(taskInboxState: TaskInboxState): Promise<void> {
    this._taskInboxState = taskInboxState;
    return Promise.resolve();
  }

  public clearOpenTask(): Promise<void> {
    this.activeTask = null;
    return Promise.resolve();
  }

  openTask(task: UserTaskInstance): Promise<void> {
    this.activeTask = task;
    this.onOpenTask(task);
    return Promise.resolve();
  }

  applyFilter(filter: QueryFilter): Promise<void> {
    this._taskInboxState.filters = filter;
    return Promise.resolve();
  }

  applySorting(sortBy: SortBy): Promise<void> {
    this._taskInboxState.sortBy = sortBy;
    return Promise.resolve();
  }

  getTaskById(uuid: string): Promise<UserTaskInstance> {
    if (this.activeTask && this.activeTask.id === uuid) {
      return Promise.resolve(this.activeTask);
    }
    return Promise.resolve(undefined);
  }

  query(offset: number, limit: number): Promise<UserTaskInstance[]> {
    return new Promise<UserTaskInstance[]>((resolve, reject) => {
      this.queries
        .getUserTasks(
          this.user,
          offset,
          limit,
          this._taskInboxState.filters,
          this._taskInboxState.sortBy
        )
        .then(value => {
          this._taskInboxState.currentPage = { offset, limit };
          resolve(value);
        })
        .catch(reason => {
          reject(reason);
        });
    });
  }
}
