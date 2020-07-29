import React from 'react';
import TaskConsoleContext, { DefaultContext } from './TaskConsoleContext';
import { GraphQL } from '@kogito-apps/common';
import UserTaskInstance = GraphQL.UserTaskInstance;

const TaskConsoleContextProvider: React.FC = props => {
  return (
    <TaskConsoleContext.Provider value={new DefaultContext<UserTaskInstance>()}>
      {props.children}
    </TaskConsoleContext.Provider>
  );
};

export default TaskConsoleContextProvider;
