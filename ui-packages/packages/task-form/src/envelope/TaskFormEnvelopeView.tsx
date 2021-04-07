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

import React, { useImperativeHandle, useState } from 'react';
import { MessageBusClientApi } from '@kogito-tooling/envelope-bus/dist/api';
import { TaskFormChannelApi } from '../api';
import '@patternfly/patternfly/patternfly.css';
import { UserTaskInstance } from '@kogito-apps/task-inbox/dist/types';
import TaskForm from './components/TaskForm/TaskForm';
import { TaskFormEnvelopeViewDriver } from './TaskFormEnvelopeViewDriver';

export interface TaskFormEnvelopeViewApi {
  initialize: (userTask: UserTaskInstance) => void;
}

interface Props {
  channelApi: MessageBusClientApi<TaskFormChannelApi>;
}

export const TaskFormEnvelopeView = React.forwardRef<
  TaskFormEnvelopeViewApi,
  Props
>((props, forwardedRef) => {
  const [
    isEnvelopeConnectedToChannel,
    setEnvelopeConnectedToChannel
  ] = useState<boolean>(false);
  const [userTask, setUserTask] = useState<UserTaskInstance>();
  useImperativeHandle(
    forwardedRef,
    () => ({
      initialize: (userTask: UserTaskInstance) => {
        setEnvelopeConnectedToChannel(true);
        setUserTask(userTask);
      }
    }),
    []
  );

  return (
    <TaskForm
      isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
      userTask={userTask}
      driver={new TaskFormEnvelopeViewDriver(props.channelApi)}
    />
  );
});

export default TaskFormEnvelopeView;
