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
import { getWrapper } from '@kogito-apps/components-common';
import { MockedMessageBusClientApi } from './mocks/Mocks';
import { TaskInboxState } from '../../api';
import TaskInboxEnvelopeView, {
  TaskInboxEnvelopeViewApi
} from '../TaskInboxEnvelopeView';
import TaskInbox from '../components/TaskInbox/TaskInbox';

jest.mock('../components/TaskInbox/TaskInbox');

const initialState: TaskInboxState = {
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
const activeTaskStates = ['Ready'];
const allTaskStates = ['Ready', 'Finished'];

describe('TaskInboxEnvelopeView tests', () => {
  it('Snapshot', () => {
    const channelApi = new MockedMessageBusClientApi();

    const forwardRef = React.createRef<TaskInboxEnvelopeViewApi>();

    let wrapper = getWrapper(
      <TaskInboxEnvelopeView channelApi={channelApi} ref={forwardRef} />,
      'TaskInboxEnvelopeView'
    );

    expect(wrapper).toMatchSnapshot();

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize(
          initialState,
          allTaskStates,
          activeTaskStates
        );
      }
    });

    wrapper = wrapper.update().find(TaskInboxEnvelopeView);

    expect(wrapper).toMatchSnapshot();

    const inbox = wrapper.find(TaskInbox);

    expect(inbox.exists()).toBeTruthy();
    expect(inbox.props().isEnvelopeConnectedToChannel).toBeTruthy();
    expect(inbox.props().driver).not.toBeNull();
    expect(inbox.props().allTaskStates).toBe(allTaskStates);
    expect(inbox.props().activeTaskStates).toBe(activeTaskStates);
  });
});
