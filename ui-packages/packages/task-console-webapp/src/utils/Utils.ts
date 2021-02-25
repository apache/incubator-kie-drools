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

import { GraphQL, User } from '@kogito-apps/consoles-common';

import UserTaskInstance = GraphQL.UserTaskInstance;

export const getTaskSchemaEndPoint = (
  task: UserTaskInstance,
  user: User
): string => {
  let params = '';
  let endpoint = task.endpoint;

  if (task.completed) {
    // if task is completed we load the schema for the task definition
    endpoint = endpoint.slice(0, -(task.id.length + 1));
    endpoint = endpoint.replace(task.processInstanceId + '/', '');
  } else {
    params = `?${getTaskEndpointSecurityParams(user)}`;
  }

  return `${endpoint}/schema${params}`;
};

export const getTaskEndpointSecurityParams = (user: User): string => {
  let groups = '';

  if (user.groups && user.groups.length > 0) {
    groups = `&group=${user.groups.join('&group=')}`;
  }
  return `user=${user.id}${groups}`;
};

export const resolveTaskPriority = (priority?: string): string => {
  switch (priority) {
    case '0':
      return '0 - High';
    case '5':
      return '5 - Medium';
    case '10':
      return '10 - Low';
  }

  return priority || '-';
};

export const trimTaskEndpoint = (userTask: UserTaskInstance): string => {
  if (userTask.endpoint) {
    const endpoint = userTask.endpoint;
    const pid = userTask.processInstanceId;
    return `${endpoint.substring(0, endpoint.indexOf(pid))}...`;
  }

  return '-';
};

export const getAllTaskStates = (): string[] => {
  // @ts-ignore
  if (window.KOGITO_TASK_STATES_LIST) {
    // @ts-ignore
    return window.KOGITO_TASK_STATES_LIST.split(',').map(state => state.trim());
  }
  // @ts-ignore
  if (process.env.KOGITO_TASK_STATES_LIST) {
    // @ts-ignore
    return process.env.KOGITO_TASK_STATES_LIST.split(',').map(state =>
      state.trim()
    );
  }
  return ['Ready', 'Reserved', 'Completed', 'Aborted', 'Skipped'];
};

export const getActiveTaskStates = (): string[] => {
  // @ts-ignore
  if (window.KOGITO_TASK_ACTIVE_STATES_LIST) {
    // @ts-ignore
    return window.KOGITO_TASK_ACTIVE_STATES_LIST.split(',').map(state =>
      state.trim()
    );
  }
  // @ts-ignore
  if (process.env.KOGITO_TASK_ACTIVE_STATES_LIST) {
    // @ts-ignore
    return process.env.KOGITO_TASK_ACTIVE_STATES_LIST.split(',').map(state =>
      state.trim()
    );
  }
  return ['Ready', 'Reserved'];
};
