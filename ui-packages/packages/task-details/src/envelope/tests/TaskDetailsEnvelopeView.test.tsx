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
import { mount } from 'enzyme';
import { act } from 'react-dom/test-utils';
import {
  TaskDetailsEnvelopeView,
  TaskDetailsEnvelopeViewApi
} from '../TaskDetailsEnvelopeView';
import { MockedMessageBusClientApi, userTask } from './utils/Mocks';
import TaskDetails from '../component/TaskDetails';

jest.mock('../component/TaskDetails');

describe('TaskDetailsEnvelopeApiImpl tests', () => {
  it('Snapshot test', () => {
    const channelApi = new MockedMessageBusClientApi();

    const forwardRef = React.createRef<TaskDetailsEnvelopeViewApi>();

    let wrapper = mount(
      <TaskDetailsEnvelopeView channelApi={channelApi} ref={forwardRef} />
    )
      .update()
      .find(TaskDetailsEnvelopeView);

    expect(wrapper).toMatchSnapshot();

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.setTask(userTask);
      }
    });

    wrapper = wrapper.update().find(TaskDetailsEnvelopeView);

    expect(wrapper).toMatchSnapshot();

    const details = wrapper.find(TaskDetails);

    expect(details.exists()).toBeTruthy();
    expect(details.props().userTask).toBe(userTask);
  });
});
