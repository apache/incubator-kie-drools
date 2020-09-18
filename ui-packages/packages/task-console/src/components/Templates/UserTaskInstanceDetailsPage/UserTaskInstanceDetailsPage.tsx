import React, { useContext, useEffect, useState } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import {
  Breadcrumb,
  BreadcrumbItem,
  Card,
  CardBody,
  CardHeader,
  Grid,
  GridItem,
  PageSection,
  Title,
  Text,
  TextVariants
} from '@patternfly/react-core';
import {
  componentOuiaProps,
  GraphQL,
  KogitoEmptyState,
  KogitoEmptyStateType,
  ouiaPageTypeAndObjectId,
  OUIAProps
} from '@kogito-apps/common';
import TaskConsoleContext, {
  IContext
} from '../../../context/TaskConsoleContext/TaskConsoleContext';
import PageTitle from '../../Molecules/PageTitle/PageTitle';
import TaskState from '../../Atoms/TaskState/TaskState';
import TaskForm from '../../Organisms/TaskForm/TaskForm';
import { TaskStateType } from '../../../util/Variants';
import UserTaskInstance = GraphQL.UserTaskInstance;
import TaskDetails from '../../Organisms/TaskDetails/TaskDetails';

interface MatchProps {
  taskId: string;
}

const UserTaskInstanceDetailsPage: React.FC<RouteComponentProps<
  MatchProps,
  {},
  {}
> &
  OUIAProps> = ({ ouiaId, ouiaSafe, ...props }) => {
  const id = props.match.params.taskId;

  const [userTask, setUserTask] = useState<UserTaskInstance>();

  const context: IContext<UserTaskInstance> = useContext(TaskConsoleContext);

  useEffect(() => {
    window.onpopstate = () => {
      props.history.push({ state: { ...props.location.state } });
    };
  });

  useEffect(() => {
    return ouiaPageTypeAndObjectId('user-tasks', id);
  });

  useEffect(() => {
    if (context.getActiveItem()) {
      setUserTask(context.getActiveItem());
    }
  }, []);

  if (!userTask) {
    return (
      <KogitoEmptyState
        type={KogitoEmptyStateType.Info}
        title={'Cannot find task'}
        body={`Cannot find task with id '${id}'`}
      />
    );
  }

  return (
    <React.Fragment>
      <div {...componentOuiaProps(ouiaId, 'UserTaskInstanceDetails', ouiaSafe)}>
        <PageSection variant="light">
          <Breadcrumb>
            <BreadcrumbItem>
              <Link to={'/'}>Task Inbox</Link>
            </BreadcrumbItem>
            <BreadcrumbItem>{userTask.referenceName}</BreadcrumbItem>
          </Breadcrumb>

          <PageTitle
            title={userTask.referenceName}
            extra={<TaskState task={userTask} variant={TaskStateType.LABEL} />}
          />

          <Text component={TextVariants.small}>ID: {userTask.id}</Text>
        </PageSection>
        <PageSection>
          <Grid hasGutter md={1} className="pf-u-h-100">
            <GridItem span={8} className="pf-u-h-100">
              <Card className="pf-u-h-100">
                <CardHeader>
                  <Title headingLevel="h3" size="xl">
                    Form
                  </Title>
                </CardHeader>
                <CardBody className="pf-u-h-100">
                  <TaskForm
                    userTaskInstance={userTask}
                    successCallback={() => props.history.push('/')}
                    errorCallback={() => props.history.push('/')}
                  />
                </CardBody>
              </Card>
            </GridItem>
            <GridItem span={4} className="pf-u-h-100">
              <Card className="pf-u-h-100">
                <CardHeader>
                  <Title headingLevel="h3" size="xl">
                    Details
                  </Title>
                </CardHeader>
                <CardBody className="pf-u-h-100">
                  <TaskDetails userTaskInstance={userTask} />
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
