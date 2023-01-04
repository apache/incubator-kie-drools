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
import Moment from 'react-moment';
import TaskDescription from '../TaskDescription/TaskDescription';
import { DataTableColumn } from '@kogito-apps/components-common';
import { UserTaskInstance, TaskState } from '@kogito-apps/task-console-shared';

export const getDefaultColumn = (
  columnPath: string,
  columnLabel: string,
  isSortable: boolean
): DataTableColumn => {
  return {
    path: columnPath,
    label: columnLabel,
    isSortable
  };
};

export const getDateColumn = (
  columnPath: string,
  columnLabel: string
): DataTableColumn => {
  return {
    label: columnLabel,
    path: columnPath,
    bodyCellTransformer: (value) => (
      <Moment fromNow>{new Date(`${value}`)}</Moment>
    ),
    isSortable: true
  };
};

export const getTaskDescriptionColumn = (
  selectTask: (task: UserTaskInstance) => void
): DataTableColumn => {
  return {
    label: 'Name',
    path: 'referenceName',
    bodyCellTransformer: (cellValue, rowTask: UserTaskInstance) => {
      return (
        <TaskDescription task={rowTask} onClick={() => selectTask(rowTask)} />
      );
    },
    isSortable: true
  };
};

export const getTaskStateColumn = (): DataTableColumn => {
  return {
    label: 'Status',
    path: 'state',
    bodyCellTransformer: (cellValue, rowTask: UserTaskInstance) => (
      <TaskState task={rowTask} />
    ),
    isSortable: true
  };
};

export const getDefaultTaskStates = (): string[] => {
  return ['Ready', 'Reserved', 'Completed', 'Aborted', 'Skipped'];
};

export const getDefaultActiveTaskStates = (): string[] => {
  return ['Ready', 'Reserved'];
};
