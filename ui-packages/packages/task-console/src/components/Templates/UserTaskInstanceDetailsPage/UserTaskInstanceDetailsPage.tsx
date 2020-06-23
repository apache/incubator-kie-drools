import React, { useContext, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import {
  Breadcrumb,
  BreadcrumbItem,
  Card,
  CardBody,
  Grid,
  GridItem,
  InjectedOuiaProps,
  PageSection,
  withOuiaContext
} from '@patternfly/react-core';
import { ouiaPageTypeAndObjectId } from '@kogito-apps/common';
import PageTitle from '../../Molecules/PageTitle/PageTitle';
import TaskForm from '../../Organisms/TaskForm/TaskForm';
import TaskConsoleContext, {
  IContext
} from '../../../context/TaskConsoleContext/TaskConsoleContext';
import { TaskInfo } from '../../../model/TaskInfo';

interface MatchProps {
  taskID: string;
}

const UserTaskInstanceDetailsPage: React.FC<
  RouteComponentProps<MatchProps, {}, {}> & InjectedOuiaProps
> = ({ ouiaContext, ...props }) => {
  const id = props.match.params.taskID;

  const context: IContext<TaskInfo> = useContext(TaskConsoleContext);

  useEffect(() => {
    window.onpopstate = () => {
      props.history.push({ state: { ...props.location.state } });
    };
  });

  useEffect(() => {
    return ouiaPageTypeAndObjectId(ouiaContext, 'user-tasks', id);
  });

  const taskInfo = context.getActiveItem();

  return (
    <React.Fragment>
      <PageSection variant="light">
        <PageTitle title="Task Details" />
        <Breadcrumb>
          <BreadcrumbItem>
            <Link to={'/'}>Home</Link>
          </BreadcrumbItem>
        </Breadcrumb>
      </PageSection>
      <PageSection>
        <Grid gutter="md" className="pf-u-h-100">
          <GridItem span={12} className="pf-u-h-100">
            <Card className="pf-u-h-100">
              <CardBody className="pf-u-h-100">
                <TaskForm
                  taskInfo={taskInfo}
                  successCallback={() => props.history.goBack()}
                  errorCallback={() => props.history.goBack()}
                />
              </CardBody>
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    </React.Fragment>
  );
};

export default withOuiaContext(UserTaskInstanceDetailsPage);
