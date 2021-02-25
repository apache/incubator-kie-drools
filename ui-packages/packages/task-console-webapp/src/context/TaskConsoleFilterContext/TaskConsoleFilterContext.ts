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

import React, { useContext } from 'react';
import { getActiveTaskStates } from '../../utils/Utils';

export interface ISortBy {
  index?: number;
  direction?: 'asc' | 'desc';
}

export interface IQueryInfo {
  sortBy?: ISortBy;
  offset?: number;
  maxElements?: number;
}

export interface IActiveFilters {
  filters?: {
    status: string[];
    taskNames: string[];
  };
  selectedStatus?: string[];
}

export interface ITaskConsoleFilterContext {
  getActiveQueryInfo(): IQueryInfo;
  getActiveFilters(): IActiveFilters;
}

export class TaskConsoleFilterContextImpl implements ITaskConsoleFilterContext {
  private readonly queryInfo: IQueryInfo;
  private activeFilters: IActiveFilters;

  constructor() {
    this.activeFilters = {
      selectedStatus: getActiveTaskStates(),
      filters: {
        status: getActiveTaskStates(),
        taskNames: []
      }
    };

    this.queryInfo = {
      sortBy: {
        index: 5,
        direction: 'desc'
      },
      maxElements: 0
    };
  }

  getActiveQueryInfo(): IQueryInfo {
    return this.queryInfo;
  }

  getActiveFilters(): IActiveFilters {
    return this.activeFilters;
  }
}

const TaskConsoleFilterContext = React.createContext<ITaskConsoleFilterContext>(
  new TaskConsoleFilterContextImpl()
);

export const useTaskConsoleFilterContext = () =>
  useContext<ITaskConsoleFilterContext>(TaskConsoleFilterContext);

export default TaskConsoleFilterContext;
