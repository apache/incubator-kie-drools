import Moment from 'react-moment';
import React, { useState, useEffect, useContext } from 'react';
import {
  DataListAction,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow
} from '@patternfly/react-core';
import {
  useGetProcessInstanceByIdLazyQuery,
  UserTaskInstance
} from '../../../graphql/types';
import { Link } from 'react-router-dom';
import TaskConsoleContext, {
  IContext
} from '../../../context/TaskConsoleContext/TaskConsoleContext';
import { TaskInfoImpl, TaskInfo } from '../../../model/TaskInfo';

export interface IOwnProps {
  id: number;
  userTaskInstanceData: UserTaskInstance;
}

const TaskListItem: React.FC<IOwnProps> = ({ userTaskInstanceData }) => {
  const [isProcessInstanceLoaded, setProcessInstanceLoaded] = useState(false);

  const [
    getProcessInstance,
    { loading, data }
  ] = useGetProcessInstanceByIdLazyQuery({
    fetchPolicy: 'network-only'
  });

  const context: IContext<TaskInfo> = useContext(TaskConsoleContext);

  if (!isProcessInstanceLoaded && userTaskInstanceData.state === 'Ready') {
    getProcessInstance({
      variables: {
        id: userTaskInstanceData.processInstanceId
      }
    });
    setProcessInstanceLoaded(true);
  }

  useEffect(() => {
    if (!loading && data !== undefined) {
      setProcessInstanceLoaded(true);
    }
  }, [data]);

  return (
    <React.Fragment>
      <DataListItem aria-labelledby="kie-datalist-item">
        <DataListItemRow>
          <DataListItemCells
            dataListCells={[
              <DataListCell key={1}>{userTaskInstanceData.name}</DataListCell>,
              <DataListCell key={2}>
                {userTaskInstanceData.started ? (
                  <Moment fromNow>
                    {new Date(`${userTaskInstanceData.started}`)}
                  </Moment>
                ) : (
                  ''
                )}
              </DataListCell>,
              <DataListCell key={3}>
                {userTaskInstanceData.processId}
              </DataListCell>,
              <DataListCell key={4}>
                {userTaskInstanceData.processInstanceId}
              </DataListCell>,
              <DataListCell key={5}>{userTaskInstanceData.state}</DataListCell>
            ]}
          />

          <DataListAction
            aria-labelledby="kie-datalist-item kie-datalist-action"
            id="kie-datalist-action"
            aria-label="Actions"
          >
            <Link
              to={'/Task/' + userTaskInstanceData.id}
              onClick={() =>
                context.setActiveItem(
                  new TaskInfoImpl(
                    userTaskInstanceData,
                    data.ProcessInstances[0].endpoint
                  )
                )
              }
            >
              Open Task
            </Link>
          </DataListAction>
        </DataListItemRow>
      </DataListItem>
    </React.Fragment>
  );
};

export default TaskListItem;
