import React from 'react';
import TaskConsoleContext, { DefaultContext } from './TaskConsoleContext';
import { TaskInfo } from '../../model/TaskInfo';

const TaskConsoleContextProvider: React.FC = props => {
  return (
    <TaskConsoleContext.Provider value={new DefaultContext<TaskInfo>()}>
      {props.children}
    </TaskConsoleContext.Provider>
  );
};

export default TaskConsoleContextProvider;
