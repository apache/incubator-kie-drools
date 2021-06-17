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
import { mount } from 'enzyme';
import { MockedMessageBusClientApi, testUserTask } from './mocks/Mocks';
import TaskFormEnvelopeView, {
  TaskFormEnvelopeViewApi
} from '../TaskFormEnvelopeView';
import TaskForm from '../components/TaskForm/TaskForm';

jest.mock('../components/TaskForm/TaskForm');

describe('TaskFormEnvelopeView tests', () => {
  it('Snapshot', () => {
    const channelApi = MockedMessageBusClientApi();
    const forwardRef = React.createRef<TaskFormEnvelopeViewApi>();

    let wrapper = mount(
      <TaskFormEnvelopeView channelApi={channelApi} ref={forwardRef} />
    ).find('TaskFormEnvelopeView');

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize(testUserTask);
      }
    });

    wrapper = wrapper.update();

    expect(wrapper.find(TaskFormEnvelopeView)).toMatchSnapshot();

    const taskForm = wrapper.find(TaskForm);

    expect(taskForm.exists()).toBeTruthy();
    expect(taskForm.props().isEnvelopeConnectedToChannel).toBeTruthy();
    expect(taskForm.props().driver).not.toBeNull();
  });
});
