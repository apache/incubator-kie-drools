import React from 'react';
import { User } from '@kogito-apps/common';

export interface IContext<T> {
  getUser(): User;
  setActiveItem(item: any);
  getActiveItem(): any;
}

export class DefaultContext<T> implements IContext<T> {
  private user: User;
  private item: T;

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
}

const TaskConsoleContext = React.createContext<IContext<any>>(null);

export default TaskConsoleContext;
