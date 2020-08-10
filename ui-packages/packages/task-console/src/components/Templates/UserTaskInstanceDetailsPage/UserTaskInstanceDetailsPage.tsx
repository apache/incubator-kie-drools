import React, { useContext, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import {
  Breadcrumb,
  BreadcrumbItem,
  Card,
  CardBody,
  Grid,
  GridItem,
  PageSection
} from '@patternfly/react-core';
import {
  ouiaPageTypeAndObjectId,
  GraphQL,
  componentOuiaProps, OUIAProps
} from '@kogito-apps/common';
import PageTitle from '../../Molecules/PageTitle/PageTitle';
import TaskForm from '../../Organisms/TaskForm/TaskForm';
import TaskConsoleContext, {
  IContext
} from '../../../context/TaskConsoleContext/TaskConsoleContext';
import UserTaskInstance = GraphQL.UserTaskInstance;

interface MatchProps {
  taskID: string;
}

const UserTaskInstanceDetailsPage: React.FC<RouteComponentProps<
  MatchProps,
  {},
  {}
> &
  OUIAProps> = ({
    ouiaId,
    ouiaSafe,
    ...props
  }) => {
  const id = props.match.params.taskID;

  const context: IContext<UserTaskInstance> = useContext(TaskConsoleContext);

  useEffect(() => {
    window.onpopstate = () => {
      props.history.push({ state: { ...props.location.state } });
    };
  });

  useEffect(() => {
    return ouiaPageTypeAndObjectId( 'user-tasks', id);
  });

  const activeUserTask = context.getActiveItem();

  return (
    <React.Fragment>
      <div
        {...componentOuiaProps(ouiaId, 'UserTaskInstanceDetails', ouiaSafe)}
      >
      <PageSection variant="light">
        <PageTitle title="Task Details" />
        <Breadcrumb>
          <BreadcrumbItem>
            <Link to={'/'}>Home</Link>
          </BreadcrumbItem>
        </Breadcrumb>
      </PageSection>
      <PageSection>
        <Grid hasGutter md={1} className="pf-u-h-100">
          <GridItem span={12} className="pf-u-h-100">
            <Card className="pf-u-h-100">
              <CardBody className="pf-u-h-100">
                <TaskForm
                  userTaskInstance={activeUserTask}
                  successCallback={() => props.history.goBack()}
                  errorCallback={() => props.history.goBack()}
                />
              </CardBody>
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
      </div>
    </React.Fragment>
  );
};

export default UserTaskInstanceDetailsPage;
