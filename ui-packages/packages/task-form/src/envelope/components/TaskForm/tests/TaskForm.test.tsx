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

import React from 'react';
import { act } from 'react-dom/test-utils';
import _ from 'lodash';
import wait from 'waait';
import TaskForm, { TaskFormProps } from '../TaskForm';
import { TaskFormSchema } from '../../../../types';
import { TaskFormDriver } from '../../../../api';
import {
  getWrapper,
  KogitoEmptyState,
  KogitoSpinner
} from '@kogito-apps/components-common';
import { ApplyForVisaForm } from '../../utils/tests/mocks/ApplyForVisa';
import TaskFormRenderer from '../../TaskFormRenderer/TaskFormRenderer';
import EmptyTaskForm from '../../EmptyTaskForm/EmptyTaskForm';
import {
  MockedTaskFormDriver,
  testUserTask
} from '../../../../embedded/tests/mocks/Mocks';

jest.mock('../../TaskFormRenderer/TaskFormRenderer');
jest.mock('../../EmptyTaskForm/EmptyTaskForm');

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@kogito-apps/components-common', () => ({
  ...jest.requireActual('@kogito-apps/components-common'),
  KogitoEmptyState: () => {
    return <MockedComponent />;
  },
  KogitoSpinner: () => {
    return <MockedComponent />;
  }
}));

let props: TaskFormProps;
let driverGetTaskFormSchemaSpy;
let driverDoSubmitSpy;

const getTaskFormDriver = (schema?: TaskFormSchema): TaskFormDriver => {
  const driver = new MockedTaskFormDriver();
  driverGetTaskFormSchemaSpy = jest.spyOn(driver, 'getTaskFormSchema');
  driverGetTaskFormSchemaSpy.mockReturnValue(
    new Promise<TaskFormSchema>((resolve, reject) => {
      if (schema) {
        resolve(schema);
      } else {
        reject('cannot load form');
      }
    })
  );
  driverDoSubmitSpy = jest.spyOn(driver, 'doSubmit');
  props.driver = driver;
  return driver;
};

const getTaskFormWrapper = () => {
  return getWrapper(<TaskForm {...props} />, 'TaskForm');
};

describe('TaskForm Test', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    props = {
      isEnvelopeConnectedToChannel: true,
      driver: null,
      userTask: _.cloneDeep(testUserTask)
    };
  });

  it('Envelope not connected', () => {
    const driver = getTaskFormDriver(_.cloneDeep(ApplyForVisaForm));

    props.isEnvelopeConnectedToChannel = false;

    const wrapper = getTaskFormWrapper();

    expect(wrapper).toMatchSnapshot();

    expect(driver.getTaskFormSchema).not.toHaveBeenCalled();

    const spinner = wrapper.find(KogitoSpinner);
    expect(spinner.exists()).toBeTruthy();

    const renderer = wrapper.find(TaskFormRenderer);
    expect(renderer.exists()).toBeFalsy();

    const emptyForm = wrapper.find(EmptyTaskForm);
    expect(emptyForm.exists()).toBeFalsy();

    const emptyState = wrapper.find(KogitoEmptyState);
    expect(emptyState.exists()).toBeFalsy();
  });

  it('Empty Task Form rendering', async () => {
    const schema = _.cloneDeep(ApplyForVisaForm);

    _.unset(schema, 'properties');

    const driver = getTaskFormDriver(schema);

    let wrapper;

    await act(async () => {
      wrapper = getTaskFormWrapper();
      wait();
    });

    wrapper = wrapper.update().find(TaskForm);

    expect(wrapper).toMatchSnapshot();

    expect(driver.getTaskFormSchema).toHaveBeenCalled();

    let emptyForm = wrapper.find(EmptyTaskForm);
    expect(emptyForm.exists()).toBeTruthy();

    expect(emptyForm.props().enabled).toBeTruthy();
    expect(emptyForm.props().formSchema).toStrictEqual(schema);
    expect(emptyForm.props().userTask).toStrictEqual(props.userTask);

    await act(async () => {
      emptyForm.props().submit('complete');
      wait();
    });

    expect(driverDoSubmitSpy).toHaveBeenCalledWith('complete', {});

    wrapper = wrapper.update().find(TaskForm);
    emptyForm = wrapper.find(EmptyTaskForm);
    expect(emptyForm.exists()).toBeTruthy();
    expect(emptyForm.props().enabled).toBeFalsy();
  });

  it('Task Form rendering', async () => {
    const schema = _.cloneDeep(ApplyForVisaForm);

    const driver = getTaskFormDriver(schema);

    let wrapper;

    await act(async () => {
      wrapper = getTaskFormWrapper();
      wait();
    });

    wrapper = wrapper.update().find(TaskForm);

    expect(wrapper).toMatchSnapshot();

    expect(driver.getTaskFormSchema).toHaveBeenCalled();

    let taskFormRenderer = wrapper.find(TaskFormRenderer);
    expect(taskFormRenderer.exists()).toBeTruthy();

    expect(taskFormRenderer.props().enabled).toBeTruthy();
    expect(taskFormRenderer.props().userTask).toStrictEqual(props.userTask);
    expect(taskFormRenderer.props().formSchema).toStrictEqual(schema);
    expect(taskFormRenderer.props().formData).toBeNull();

    const formData = JSON.parse(props.userTask.inputs);

    const payload = {
      traveller: formData.traveller
    };

    await act(async () => {
      taskFormRenderer.props().submit('complete', formData);
      wait();
    });

    expect(driverDoSubmitSpy).toHaveBeenCalledWith('complete', payload);

    wrapper = wrapper.update().find(TaskForm);
    taskFormRenderer = wrapper.find(TaskFormRenderer);
    expect(taskFormRenderer.exists()).toBeTruthy();
    expect(taskFormRenderer.props().enabled).toBeFalsy();
    expect(taskFormRenderer.props().formData).toStrictEqual(formData);
  });

  it('Empty state', async () => {
    const driver = getTaskFormDriver();

    let wrapper;

    await act(async () => {
      wrapper = getTaskFormWrapper();
      wait();
    });

    wrapper = wrapper.update().find(TaskForm);

    expect(wrapper).toMatchSnapshot();

    expect(driver.getTaskFormSchema).toHaveBeenCalled();

    const emptyState = wrapper.find(KogitoEmptyState);
    expect(emptyState.exists()).toBeTruthy();
  });
});
