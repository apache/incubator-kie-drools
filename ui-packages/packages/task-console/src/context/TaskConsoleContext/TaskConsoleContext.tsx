import React from 'react';
import { User } from '@kogito-apps/common';
import { getActiveTaskStates } from '../../util/Utils';
import { ISortBy } from '@patternfly/react-table';

export interface IContext<T> {
  getUser(): User;
  setActiveItem(item: T);
  getActiveItem(): T;
  getActiveQueryInfo(): IQueryInfo;
  getActiveFilters(): IActiveFilters;
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

export class DefaultContext<T> implements IContext<T> {
  private user: User;
  private item: T;
  private readonly queryInfo: IQueryInfo = {
    sortBy: {
      index: 5,
      direction: 'desc'
    },
    maxElements: 0
  };
  private activeFilters: IActiveFilters;

  constructor(user: User) {
    this.user = user;
    this.activeFilters = {
      selectedStatus: getActiveTaskStates(),
      filters: {
        status: getActiveTaskStates(),
        taskNames: []
      }
    };
  }

  getUser(): User {
    return this.user;
  }

  getActiveItem(): T {
    return this.item;
  }

  setActiveItem(item: T) {
    this.item = item;
  }

  getActiveQueryInfo(): IQueryInfo {
    return this.queryInfo;
  }

  getActiveFilters(): IActiveFilters {
    return this.activeFilters;
  }
}

const TaskConsoleContext = React.createContext<IContext<any>>(null);

export default TaskConsoleContext;
