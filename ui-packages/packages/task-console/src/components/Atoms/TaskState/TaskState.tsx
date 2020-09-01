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
  BanIcon,
  CheckCircleIcon,
  OnRunningIcon
} from '@patternfly/react-icons';

import { GraphQL } from '@kogito-apps/common';
import UserTaskInstance = GraphQL.UserTaskInstance;

interface IOwnProps {
  task: UserTaskInstance;
}

const TaskState: React.FC<IOwnProps> = ({ task }) => {
  let icon;

  if (task.state === 'Aborted') {
    icon = <BanIcon className="pf-u-mr-sm" />;
  } else if (task.completed) {
    icon = (
      <CheckCircleIcon
        className="pf-u-mr-sm"
        color="var(--pf-global--success-color--100)"
      />
    );
  } else {
    icon = <OnRunningIcon className="pf-u-mr-sm" />;
  }

  return (
    <React.Fragment>
      {icon} {task.state}
    </React.Fragment>
  );
};

export default TaskState;
