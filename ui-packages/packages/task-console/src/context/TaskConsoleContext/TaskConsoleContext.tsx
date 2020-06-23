import React from 'react';

export interface IContext<T> {
  setActiveItem(item: any);
  getActiveItem(): any;
}

export class DefaultContext<T> implements IContext<T> {
  private item: T;

  getActiveItem(): T {
    return this.item;
  }

  setActiveItem(item: T) {
    this.item = item;
  }
}

const TaskConsoleContext = React.createContext<IContext<any>>(
  new DefaultContext()
);

export default TaskConsoleContext;
