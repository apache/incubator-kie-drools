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
import * as React from 'react';
import { useImperativeHandle, useState } from 'react';

import { TaskDetailsChannelApi } from '../api';
import { MessageBusClientApi } from '@kie-tools-core/envelope-bus/dist/api';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import TaskDetails from './component/TaskDetails';

import '@patternfly/patternfly/patternfly.css';

export interface TaskDetailsEnvelopeViewApi {
  setTask(task: UserTaskInstance): void;
}

interface Props {
  channelApi: MessageBusClientApi<TaskDetailsChannelApi>;
}

export const TaskDetailsEnvelopeView = React.forwardRef<
  TaskDetailsEnvelopeViewApi,
  Props
>((props, forwardedRef) => {
  const [task, setTask] = useState<UserTaskInstance>();

  useImperativeHandle(
    forwardedRef,
    () => {
      return {
        setTask: (userTask: UserTaskInstance) => {
          setTask(userTask);
        }
      };
    },
    []
  );

  return <TaskDetails userTask={task} />;
});
