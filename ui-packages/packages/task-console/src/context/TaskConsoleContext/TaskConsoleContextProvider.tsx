import React from 'react';
import { GraphQL, useKogitoAppContext, User } from '@kogito-apps/common';
import TaskConsoleContext, { DefaultContext } from './TaskConsoleContext';
import UserTaskInstance = GraphQL.UserTaskInstance;

interface IOwnProps {
  user?: User;
  children;
}

const TaskConsoleContextProvider: React.FC<IOwnProps> = ({
  user,
  children
}) => {
  const context = useKogitoAppContext();

  const contextUser = user || context.getCurrentUser();

  return (
    <TaskConsoleContext.Provider
      value={new DefaultContext<UserTaskInstance>(contextUser)}
    >
      {children}
    </TaskConsoleContext.Provider>
  );
};

export default TaskConsoleContextProvider;
