import React from 'react';
import axios from 'axios';
import { getWrapperAsync } from '@kogito-apps/common';
import TaskForm from '../TaskForm';
import { TaskInfo, TaskInfoImpl } from '../../../../model/TaskInfo';
import { UserTaskInstance } from '../../../../graphql/types';
import ApplyForVisaForm from '../../../../util/tests/mocks/ApplyForVisa';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('axios');
jest.mock('@kogito-apps/common', () => ({
  ...jest.requireActual('@kogito-apps/common'),
  KogitoEmptyState: () => {
    return <MockedComponent />;
  }
}));
const mockedAxios = axios as jest.Mocked<typeof axios>;

const userTaskInstance: UserTaskInstance = {
  id: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
  description: null,
  name: 'Apply for visa',
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
  started: '2020-02-19T11:11:56.282Z',
  excludedUsers: [],
  potentialGroups: [],
  potentialUsers: [],
  inputs:
    '{"Skippable":"true","trip":{"city":"Boston","country":"US","begin":"2020-02-19T23:00:00.000+01:00","end":"2020-02-26T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","traveller":{"firstName":"Rachel","lastName":"White","email":"rwhite@gorle.com","nationality":"Polish","address":{"street":"Cabalone","city":"Zerf","zipCode":"765756","country":"Poland"}},"Priority":"1"}',
  outputs: '{}',
  referenceName: 'VisaApplication',
  lastUpdate: '2020-02-19T11:11:56.282Z'
};

const taskInfo: TaskInfo = new TaskInfoImpl(
  userTaskInstance,
  'http://localhost:8080/travels'
);

describe('TaskForm Test', () => {
  it('Test rendering form', async () => {
    mockedAxios.get.mockResolvedValue({
      status: 200,
      data: ApplyForVisaForm
    });
    const wrapper = await getWrapperAsync(
      <TaskForm taskInfo={taskInfo} />,
      'TaskForm'
    );

    wrapper.update();

    expect(wrapper).toMatchSnapshot();
  });

  it('Test rendering form with error', async () => {
    mockedAxios.get.mockResolvedValue({
      status: 500
    });
    const wrapper = await getWrapperAsync(
      <TaskForm taskInfo={taskInfo} />,
      'TaskForm'
    );

    wrapper.update();

    expect(wrapper).toMatchSnapshot();
  });
});
