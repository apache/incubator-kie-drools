import React from 'react';
import { User } from '@kogito-apps/common';

export interface SortBy {
  index: number;
  direction: string;
}
export interface IContext<T> {
  getUser(): User;
  setActiveItem(item: T);
  getActiveItem(): T;
  getActiveQueryInfo(): IQueryInfo;
}

export interface IQueryInfo {
  sortBy?: SortBy;
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
