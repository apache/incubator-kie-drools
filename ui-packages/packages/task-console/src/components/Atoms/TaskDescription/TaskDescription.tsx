/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import {
  ItemDescriptor,
  GraphQL,
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/common';
import { Link } from 'react-router-dom';
import {
  ITaskConsoleContext,
  useTaskConsoleContext
} from '../../../context/TaskConsoleContext/TaskConsoleContext';
import UserTaskInstance = GraphQL.UserTaskInstance;

interface IOwnProps {
  task: UserTaskInstance;
}

const TaskDescription: React.FC<IOwnProps & OUIAProps> = ({
  task,
  ouiaId,
  ouiaSafe
}) => {
  const context: ITaskConsoleContext<UserTaskInstance> = useTaskConsoleContext();

  return (
    <Link
      to={'/TaskDetails/' + task.id}
      onClick={() => context.setActiveItem(task)}
      {...componentOuiaProps(ouiaId, 'task-description', ouiaSafe)}
    >
      <div>
        <strong>
          <ItemDescriptor
            itemDescription={{
              id: task.id,
              name: task.referenceName
            }}
          />
        </strong>
      </div>
    </Link>
  );
};

export default TaskDescription;
