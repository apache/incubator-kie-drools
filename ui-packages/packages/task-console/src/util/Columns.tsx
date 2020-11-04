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

import { DataTableColumn, GraphQL } from '@kogito-apps/common';
import TaskDescription from '../components/Atoms/TaskDescription/TaskDescription';
import React from 'react';
import TaskState from '../components/Atoms/TaskState/TaskState';
import Moment from 'react-moment';

export default class Columns {
  static getDefaultColumn = (
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

  static getDateColumn(
    columnPath: string,
    columnLabel: string,
    isSortable: boolean
  ): DataTableColumn {
    return {
      label: columnLabel,
      path: columnPath,
      bodyCellTransformer: value => (
        <Moment fromNow>{new Date(`${value}`)}</Moment>
      ),
      isSortable
    };
  }

  static getTaskDescriptionColumn(isSortable: boolean): DataTableColumn {
    return {
      label: 'Name',
      path: 'referenceName',
      bodyCellTransformer: (cellValue, rowTask: GraphQL.UserTaskInstance) => (
        <TaskDescription task={rowTask} />
      ),
      isSortable
    };
  }

  static getTaskStateColumn(isSortable: boolean): DataTableColumn {
    return {
      label: 'Status',
      path: 'state',
      bodyCellTransformer: (cellValue, rowTask: GraphQL.UserTaskInstance) => (
        <TaskState task={rowTask} />
      ),
      isSortable
    };
  }
}
