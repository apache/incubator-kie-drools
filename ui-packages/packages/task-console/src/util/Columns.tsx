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

import { DataTableColumn } from '@kogito-apps/common';
import TaskDescription from '../components/Atoms/TaskDescription/TaskDescription';
import React from 'react';
import TaskState from '../components/Atoms/TaskState/TaskState';
import Moment from 'react-moment';

export default class Columns {
  static getDefaultColumn = (
    columnPath: string,
    columnLabel: string
  ): DataTableColumn => {
    return {
      path: columnPath,
      label: columnLabel
    };
  };

  static getDateColumn(
    columnPath: string,
    columnLabel: string
  ): DataTableColumn {
    return {
      label: columnLabel,
      path: columnPath,
      bodyCellTransformer: value => (
        <Moment fromNow>{new Date(`${value}`)}</Moment>
      )
    };
  }

  static getTaskDescriptionColumn(): DataTableColumn {
    return {
      label: 'Name',
      path: 'referenceName',
      bodyCellTransformer: (cellValue, rowTask) => (
        <TaskDescription task={rowTask} />
      )
    };
  }

  static getTaskStateColumn(): DataTableColumn {
    return {
      label: 'State',
      path: 'state',
      bodyCellTransformer: (cellValue, rowTask) => <TaskState task={rowTask} />
    };
  }
}
