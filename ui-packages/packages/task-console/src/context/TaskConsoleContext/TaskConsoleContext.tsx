import React from 'react';
import { ISortBy } from '@patternfly/react-table';
import { User } from '@kogito-apps/common';

export interface IContext<T> {
  getUser(): User;
  setActiveItem(item: T);
  getActiveItem(): T;
  getActiveQueryInfo(): IQueryInfo;
}

export interface IQueryInfo {
  sortBy?: ISortBy;
  offset?: number;
  maxElements?: number;
}

export class DefaultContext<T> implements IContext<T> {
  private user: User;
  private item: T;
  private readonly queryInfo: IQueryInfo = {
    maxElements: 0
  };

  constructor(user: User) {
    this.user = user;
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
}

const TaskConsoleContext = React.createContext<IContext<any>>(null);

export default TaskConsoleContext;
