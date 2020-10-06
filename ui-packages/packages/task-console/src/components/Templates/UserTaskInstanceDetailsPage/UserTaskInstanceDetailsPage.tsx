import React, { useEffect, useState, useContext } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import {
  Breadcrumb,
  BreadcrumbItem,
  Card,
  CardBody,
  CardHeader,
  PageSection,
  Title,
  Text,
  TextVariants,
  Button,
  Flex,
  FlexItem,
  Drawer,
  DrawerContent,
  DrawerContentBody,
  DrawerPanelContent,
  DrawerHead,
  DrawerActions,
  DrawerCloseButton,
  DrawerPanelBody,
  Bullseye
} from '@patternfly/react-core';
import {
  componentOuiaProps,
  GraphQL,
  KogitoEmptyState,
  KogitoEmptyStateType,
  ouiaPageTypeAndObjectId,
  OUIAProps,
  KogitoSpinner,
  ServerErrors
} from '@kogito-apps/common';
import TaskConsoleContext, {
  IContext
} from '../../../context/TaskConsoleContext/TaskConsoleContext';
import PageTitle from '../../Molecules/PageTitle/PageTitle';
import TaskState from '../../Atoms/TaskState/TaskState';
import TaskForm from '../../Organisms/TaskForm/TaskForm';
import { TaskStateType } from '../../../util/Variants';
import TaskDetails from '../../Organisms/TaskDetails/TaskDetails';
import { StaticContext } from 'react-router';
import * as H from 'history';

interface MatchProps {
  taskId: string;
}

const UserTaskInstanceDetailsPage: React.FC<RouteComponentProps<
  MatchProps,
  StaticContext,
  H.LocationState
> &
  OUIAProps> = ({ ouiaId, ouiaSafe, ...props }) => {
  const id = props.match.params.taskId;
  const [userTask, setUserTask] = useState<GraphQL.UserTaskInstance>(null);
  const [isDetailsExpanded, setIsDetailsExpanded] = useState<boolean>(false);

  const context: IContext<GraphQL.UserTaskInstance> = useContext(
    TaskConsoleContext
  );

  const [
    getUserTask,
    { loading, data, error }
  ] = GraphQL.useGetUserTaskByIdLazyQuery();

  useEffect(() => {
    !loading &&
      data &&
      data.UserTaskInstances &&
      setUserTask(data.UserTaskInstances[0]);
  }, [data]);

  useEffect(() => {
    window.onpopstate = () => {
      props.history.push({ state: { ...props.location.state } });
    };
  });

  useEffect(() => {
    return ouiaPageTypeAndObjectId('user-tasks', id);
  });

  useEffect(() => {
    if (context.getActiveItem() && context.getActiveItem().id === id) {
      setUserTask(context.getActiveItem());
    } else {
      getUserTask({
        variables: {
          id
        }
      });
    }
  }, []);

  const onViewDetailsClick = () => {
    setIsDetailsExpanded(!isDetailsExpanded);
  };

  const onDetailsCloseClick = () => {
    setIsDetailsExpanded(false);
  };

  const panelContent = (
    <DrawerPanelContent>
      <DrawerHead>
        <span tabIndex={isDetailsExpanded ? 0 : -1}>
          <Title headingLevel="h3" size="xl">
            Details
          </Title>
        </span>
        <DrawerActions>
          <DrawerCloseButton onClick={onDetailsCloseClick} />
        </DrawerActions>
      </DrawerHead>
      <DrawerPanelBody>
        <TaskDetails userTaskInstance={userTask} />
      </DrawerPanelBody>
    </DrawerPanelContent>
  );

  if (loading) {
    return (
      <PageSection>
        <Card>
          <Bullseye>
            <KogitoSpinner spinnerText="Loading task details..." />
          </Bullseye>
        </Card>
      </PageSection>
    );
  }

  if (error) {
    return <ServerErrors error={error} variant="large" />;
  }

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
              <Link
                to={'/'}
                onClick={() => {
                  context.setActiveItem(null);
                }}
              >
                Task Inbox
              </Link>
            </BreadcrumbItem>
            <BreadcrumbItem>{userTask.referenceName}</BreadcrumbItem>
          </Breadcrumb>
          <Flex
            className="example-border"
            justifyContent={{ default: 'justifyContentSpaceBetween' }}
          >
            <FlexItem>
              <PageTitle
                title={userTask.referenceName}
                extra={
                  <TaskState task={userTask} variant={TaskStateType.LABEL} />
                }
              />
              <Text component={TextVariants.small}>ID: {userTask.id}</Text>
            </FlexItem>
            <FlexItem>
              <Button
                variant="secondary"
                id="view-details"
                onClick={onViewDetailsClick}
              >
                View details
              </Button>
            </FlexItem>
          </Flex>
        </PageSection>
        <PageSection>
          <Drawer isExpanded={isDetailsExpanded}>
            <DrawerContent panelContent={panelContent}>
              <DrawerContentBody>
                <Card className="pf-u-h-100">
                  <CardHeader>
                    <Title headingLevel="h3" size="xl">
                      Form
                    </Title>
                  </CardHeader>
                  <CardBody className="pf-u-h-100">
                    <TaskForm
                      userTaskInstance={userTask}
                      successCallback={() => {
                        context.setActiveItem(null);
                        props.history.push('/');
                      }}
                      errorCallback={() => {
                        context.setActiveItem(null);
                        props.history.push('/');
                      }}
                    />
                  </CardBody>
                </Card>
              </DrawerContentBody>
            </DrawerContent>
          </Drawer>
        </PageSection>
      </div>
    </React.Fragment>
  );
};

export default UserTaskInstanceDetailsPage;
