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

import axios from 'axios';
import _ from 'lodash';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import { DefaultUser, User } from '@kogito-apps/consoles-common';
import {
  TaskFormGatewayApi,
  TaskFormGatewayApiImpl
} from '../TaskFormGatewayApi';
import { ApplyForVisaForm } from './mocks/Mocks';

const task: UserTaskInstance = {
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

const user: User = new DefaultUser('jon snow', ['hero']);

jest.mock('axios');

const mockedAxios = axios as jest.Mocked<typeof axios>;

let gatewayApi: TaskFormGatewayApi;

describe('TaskFormGatewayApi tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    gatewayApi = new TaskFormGatewayApiImpl(() => user);
  });

  it('getTaskFormSchema', async () => {
    const testSchema = _.cloneDeep(ApplyForVisaForm);
    mockedAxios.get.mockResolvedValue({
      status: 200,
      data: testSchema
    });

    const resultSchema = await gatewayApi.getTaskFormSchema(task);

    expect(mockedAxios.get).toHaveBeenCalled();

    const url = mockedAxios.get.mock.calls[0][0];

    expect(url).toEqual(
      `${task.endpoint}/schema?user=${user.id}&group=${user.groups[0]}`
    );

    expect(resultSchema).toStrictEqual(testSchema);
  });

  it('getTaskFormSchema - Completed task', async () => {
    mockedAxios.get.mockResolvedValue({
      status: 200,
      data: _.cloneDeep(ApplyForVisaForm)
    });

    const completedTask = _.cloneDeep(task);
    completedTask.state = 'Complete';
    completedTask.completed = completedTask.lastUpdate;

    await gatewayApi.getTaskFormSchema(completedTask);

    expect(mockedAxios.get).toHaveBeenCalled();

    const url = mockedAxios.get.mock.calls[0][0];

    expect(url).toEqual(`http://localhost:8080/travels/VisaApplication/schema`);
  });

  it('getTaskFormSchema - HTTP Error', async () => {
    const errorMessage = 'This is an error loading the form schema';

    mockedAxios.get.mockResolvedValue({
      status: 400,
      data: errorMessage
    });

    expect(gatewayApi.getTaskFormSchema(task)).rejects.toEqual({
      status: 400,
      data: errorMessage
    });
  });

  it('getTaskFormSchema - Promise reject', async () => {
    const errorMessage = 'This is an error loading the form schema';

    mockedAxios.get.mockImplementationOnce(() =>
      Promise.reject(new Error(errorMessage))
    );

    expect(gatewayApi.getTaskFormSchema(task)).rejects.toThrow(errorMessage);
  });

  it('doSubmit', async () => {
    const data = {
      name: 'Jon',
      lastName: 'Snow'
    };

    mockedAxios.post.mockResolvedValue({
      status: 200,
      data: data
    });

    const result = await gatewayApi.doSubmit(task, 'complete', {});

    expect(mockedAxios.post).toHaveBeenCalled();

    const url = mockedAxios.post.mock.calls[0][0];

    expect(url).toEqual(
      `${task.endpoint}?phase=complete&user=${user.id}&group=${user.groups[0]}`
    );

    expect(result).toStrictEqual(data);
  });

  it('doSubmit - HTTP Error', async () => {
    const errorMessage = 'This is an error loading the form schema';

    mockedAxios.post.mockResolvedValue({
      status: 400,
      data: errorMessage
    });

    expect(gatewayApi.doSubmit(task, 'complete', {})).rejects.toEqual({
      status: 400,
      data: errorMessage
    });
  });

  it('doSubmit - Promise reject', async () => {
    const errorMessage = 'This is an error loading the form schema';

    mockedAxios.post.mockImplementationOnce(() =>
      Promise.reject(new Error(errorMessage))
    );

    expect(gatewayApi.doSubmit(task, 'complete', {})).rejects.toThrow(
      errorMessage
    );
  });
});
