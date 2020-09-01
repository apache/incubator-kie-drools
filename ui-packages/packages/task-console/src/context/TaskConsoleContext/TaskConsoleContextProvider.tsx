import React from 'react';
import TaskConsoleContext, { DefaultContext } from './TaskConsoleContext';
import { GraphQL, User } from '@kogito-apps/common';
import UserTaskInstance = GraphQL.UserTaskInstance;

interface IOwnProps {
  user: User;
  children;
}

const TaskConsoleContextProvider: React.FC<IOwnProps> = ({
  user,
  children
}) => {
  return (
    <TaskConsoleContext.Provider
      value={new DefaultContext<UserTaskInstance>(user)}
    >
      {children}
    </TaskConsoleContext.Provider>
  );
};

export default TaskConsoleContextProvider;
