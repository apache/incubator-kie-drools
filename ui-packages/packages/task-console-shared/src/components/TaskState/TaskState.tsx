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

import { Label } from '@patternfly/react-core/dist/js/components/Label';
import { BanIcon } from '@patternfly/react-icons/dist/js/icons/ban-icon';
import { CheckCircleIcon } from '@patternfly/react-icons/dist/js/icons/check-circle-icon';
import { OnRunningIcon } from '@patternfly/react-icons/dist/js/icons/on-running-icon';
import {
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { UserTaskInstance } from '../../types';

interface Props {
  task: UserTaskInstance;
  variant?: 'default' | 'label';
}

const TaskState: React.FC<Props & OUIAProps> = ({
  task,
  variant,
  ouiaId,
  ouiaSafe
}) => {
  const icon: JSX.Element = resolveTaskStateIcon(task);

  if (variant === 'label') {
    const color = resolveTaskStateLabelColor(task);
    return (
      <Label
        color={color}
        icon={icon}
        {...componentOuiaProps(ouiaId, 'task-state', ouiaSafe)}
      >
        {task.state}
      </Label>
    );
  }

  return (
    <React.Fragment>
      {icon}{' '}
      <span {...componentOuiaProps(ouiaId, 'task-state', ouiaSafe)}>
        {task.state}
      </span>
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
