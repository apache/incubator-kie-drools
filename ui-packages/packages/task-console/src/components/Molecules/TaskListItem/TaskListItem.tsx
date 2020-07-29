import Moment from 'react-moment';
import React, { useContext } from 'react';
import {
  DataListAction,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow
} from '@patternfly/react-core';
import { Link } from 'react-router-dom';
import TaskConsoleContext, {
  IContext
} from '../../../context/TaskConsoleContext/TaskConsoleContext';
import { GraphQL } from '@kogito-apps/common';
import UserTaskInstance = GraphQL.UserTaskInstance;

export interface IOwnProps {
  id: number;
  userTaskInstanceData: UserTaskInstance;
}

const TaskListItem: React.FC<IOwnProps> = ({ userTaskInstanceData }) => {
  const context: IContext<UserTaskInstance> = useContext(TaskConsoleContext);

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
              onClick={() => {
                context.setActiveItem(userTaskInstanceData);
              }}
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
