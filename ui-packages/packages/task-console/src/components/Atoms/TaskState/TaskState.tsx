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

import { GraphQL } from '@kogito-apps/common';
import UserTaskInstance = GraphQL.UserTaskInstance;
import { Label } from '@patternfly/react-core';
import {
  BanIcon,
  CheckCircleIcon,
  OnRunningIcon
} from '@patternfly/react-icons';
import { TaskStateType } from '../../../util/Variants';

interface IOwnProps {
  task: UserTaskInstance;
  variant?: TaskStateType;
}

const TaskState: React.FC<IOwnProps> = ({ task, variant }) => {
  const icon: JSX.Element = resolveTaskStateIcon(task);

  if (variant === TaskStateType.LABEL) {
    const color = resolveTaskStateLabelColor(task);
    return (
      <Label color={color} icon={icon}>
        {task.state}
      </Label>
    );
  }

  return (
    <React.Fragment>
      {icon} {task.state}
    </React.Fragment>
  );
};

function resolveTaskStateIcon(task: UserTaskInstance): JSX.Element {
  if (task.state === 'Aborted') {
    return <BanIcon className="pf-u-mr-sm" />;
  } else if (task.completed) {
    return (
      <CheckCircleIcon
        className="pf-u-mr-sm"
        color="var(--pf-global--success-color--100)"
      />
    );
  } else {
    return <OnRunningIcon className="pf-u-mr-sm" />;
  }
}

function resolveTaskStateLabelColor(task: UserTaskInstance) {
  if (task.state === 'Aborted') {
    return 'red';
  } else if (task.completed) {
    return 'green';
  } else {
    return 'blue';
  }
}

export default TaskState;
