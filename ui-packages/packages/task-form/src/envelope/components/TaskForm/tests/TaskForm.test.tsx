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
import React from 'react';
import { act } from 'react-dom/test-utils';
import _ from 'lodash';
import wait from 'waait';
import TaskForm, { TaskFormProps } from '../TaskForm';
import { TaskFormDriver } from '../../../../api';
import { mount } from 'enzyme';
import { ApplyForVisaForm } from '../../utils/tests/mocks/ApplyForVisa';
import TaskFormRenderer from '../../TaskFormRenderer/TaskFormRenderer';
import EmptyTaskForm from '../../EmptyTaskForm/EmptyTaskForm';
import {
  MockedTaskFormDriver,
  testUserTask
} from '../../../../embedded/tests/mocks/Mocks';
import { parseTaskSchema } from '../../utils/TaskFormDataUtils';

jest.mock('../../TaskFormRenderer/TaskFormRenderer');
jest.mock('../../EmptyTaskForm/EmptyTaskForm');

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@kogito-apps/components-common/dist/components/KogitoSpinner', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/components-common'), {
    KogitoSpinner: () => {
      return <MockedComponent />;
    }
  })
);

let props: TaskFormProps;
let driverDoSubmitSpy;

const getTaskFormDriver = (): TaskFormDriver => {
  const driver = new MockedTaskFormDriver();
  driverDoSubmitSpy = jest.spyOn(driver, 'doSubmit');
  return driver;
};

const getTaskFormWrapper = () => {
  return mount(<TaskForm {...props} />).find('TaskForm');
};

describe('TaskForm Test', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    props = {
      driver: getTaskFormDriver(),
      userTask: _.cloneDeep(testUserTask),
      schema: _.cloneDeep(ApplyForVisaForm)
    };
  });

  it('Empty Task Form rendering', async () => {
    _.unset(props.schema, 'properties');

    let wrapper;

    await act(async () => {
      wrapper = getTaskFormWrapper();
      wait();
    });

    wrapper = wrapper.update().find(TaskForm);

    expect(wrapper).toMatchSnapshot();

    expect(props.driver.getTaskFormSchema).not.toHaveBeenCalled();
    expect(props.driver.getCustomForm).not.toHaveBeenCalled();

    let emptyForm = wrapper.find(EmptyTaskForm);
    expect(emptyForm.exists()).toBeTruthy();

    expect(emptyForm.props().enabled).toBeTruthy();
    expect(emptyForm.props().formSchema).toStrictEqual(props.schema);
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
    let wrapper;

    await act(async () => {
      wrapper = getTaskFormWrapper();
      wait();
    });

    wrapper = wrapper.update().find(TaskForm);

    expect(wrapper).toMatchSnapshot();

    expect(props.driver.getTaskFormSchema).not.toHaveBeenCalled();
    expect(props.driver.getCustomForm).not.toHaveBeenCalled();

    let taskFormRenderer = wrapper.find(TaskFormRenderer);
    expect(taskFormRenderer.exists()).toBeTruthy();

    expect(taskFormRenderer.props().enabled).toBeTruthy();
    expect(taskFormRenderer.props().userTask).toStrictEqual(props.userTask);
    expect(taskFormRenderer.props().formSchema).toStrictEqual(
      parseTaskSchema(props.schema).schema
    );
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

    const formSchema = taskFormRenderer.props().formSchema;

    expect(_.get(formSchema, 'properties.trip.input')).toBeUndefined();
    expect(_.get(formSchema, 'properties.traveller.input')).toBeUndefined();
    expect(_.get(formSchema, 'properties.traveller.output')).toBeUndefined();
    expect(
      _.get(formSchema, 'properties.visaApplication.input')
    ).toBeUndefined();

    expect(_.get(formSchema, 'properties.trip.uniforms.disabled')).toBeTruthy();
    expect(_.get(formSchema, 'properties.traveller.uniforms')).toBeUndefined();
    expect(
      _.get(formSchema, 'properties.visaApplication.uniforms.disabled')
    ).toBeTruthy();
  });
});
