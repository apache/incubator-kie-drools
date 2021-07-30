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

import { TaskInboxQueries } from '../TaskInboxQueries';
import { QueryFilter, SortBy, TaskInboxState } from '@kogito-apps/task-inbox';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import {
  OnOpenTaskListener,
  TaskInboxGatewayApi,
  TaskInboxGatewayApiImpl
} from '../TaskInboxGatewayApi';
import { DefaultUser, User } from '@kogito-apps/consoles-common';

export const task: UserTaskInstance = {
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

const getUserTaskByIdMock = jest.fn();
const getUserTasksMock = jest.fn();

const MockTaskInboxQueries = jest.fn<TaskInboxQueries, []>(() => ({
  getUserTaskById: getUserTaskByIdMock,
  getUserTasks: getUserTasksMock
}));

const user: User = new DefaultUser('jon snow', ['hero']);

let initialState: TaskInboxState;
let queries: TaskInboxQueries;
let gatewayApi: TaskInboxGatewayApi;

describe('TaskInboxChannelApiImpl tests', () => {
  const getCurrentUser = jest.fn();
  beforeEach(() => {
    jest.clearAllMocks();
    initialState = {
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
    queries = new MockTaskInboxQueries();
    gatewayApi = new TaskInboxGatewayApiImpl(queries, getCurrentUser);
    getUserTasksMock.mockReturnValue(Promise.resolve([]));
  });

  it('setInitialState', () => {
    gatewayApi.setInitialState(initialState);
    expect(gatewayApi.taskInboxState).toBe(initialState);
  });

  it('applyFilter', () => {
    const filter: QueryFilter = {
      taskStates: ['Ready', 'Completed'],
      taskNames: ['Task']
    };

    gatewayApi.setInitialState(initialState);

    gatewayApi.applyFilter(filter);

    expect(gatewayApi.taskInboxState.filters).toBe(filter);
  });

  it('applySorting', () => {
    const sortBy: SortBy = {
      property: 'lastUpdate',
      direction: 'asc'
    };
    gatewayApi.setInitialState(initialState);
    gatewayApi.applySorting(sortBy);

    expect(gatewayApi.taskInboxState.sortBy).toBe(sortBy);
  });

  it('query', () => {
    gatewayApi.setInitialState(initialState);
    gatewayApi.query(0, 10);

    expect(queries.getUserTasks).toHaveBeenCalledWith(
      getCurrentUser(),
      0,
      10,
      initialState.filters,
      initialState.sortBy
    );
  });

  it('openTask', () => {
    const listener: OnOpenTaskListener = {
      onOpen: jest.fn()
    };

    const unsubscribe = gatewayApi.onOpenTaskListen(listener);

    gatewayApi.openTask(task);

    expect(listener.onOpen).toHaveBeenLastCalledWith(task);

    unsubscribe.unSubscribe();
  });

  it('getTaskById', async () => {
    let userTask = await gatewayApi.getTaskById(task.id);
    expect(userTask).toBe(undefined);

    await gatewayApi.openTask(task);

    userTask = await gatewayApi.getTaskById(task.id);
    expect(userTask).toBe(task);
  });
});
