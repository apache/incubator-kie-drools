/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useEffect, useState, useContext } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import {
  Breadcrumb,
  BreadcrumbItem,
  Card,
  CardBody,
  PageSection,
  Title,
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
  Bullseye,
  Tooltip
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
  ITaskConsoleContext
} from '../../../context/TaskConsoleContext/TaskConsoleContext';
import PageTitle from '../../Molecules/PageTitle/PageTitle';
import TaskState from '../../Atoms/TaskState/TaskState';
import TaskForm from '../../Organisms/TaskForm/TaskForm';
import { NotificationType, TaskStateType } from '../../../util/Variants';
import TaskDetails from '../../Organisms/TaskDetails/TaskDetails';
import FormNotification, {
  Notification
} from '../../Atoms/FormNotification/FormNotification';
import './UserTaskInstanceDetailsPage.css';

interface MatchProps {
  taskId: string;
}

const UserTaskInstanceDetailsPage: React.FC<RouteComponentProps<MatchProps> &
  OUIAProps> = ({ ouiaId, ouiaSafe, ...props }) => {
  const id = props.match.params.taskId;
  const [userTask, setUserTask] = useState<GraphQL.UserTaskInstance>(null);
  const [isDetailsExpanded, setIsDetailsExpanded] = useState<boolean>(false);
  const [notification, setNotification] = useState<Notification>();

  const context: ITaskConsoleContext<GraphQL.UserTaskInstance> = useContext(
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

  const showNotification = (
    notificationType: NotificationType,
    submitMessage: string,
    notificationDetails?: string
  ) => {
    setNotification({
      type: notificationType,
      message: submitMessage,
      details: notificationDetails,
      customAction: {
        label: 'Go to Task Inbox',
        onClick: () => {
          setNotification(null);
          goToInbox();
        }
      },
      close: () => {
        setNotification(null);
      }
    });
  };

  const onSubmitSuccess = (phase: string) => {
    const message = `Task '${userTask.referenceName}' successfully transitioned to phase '${phase}'.`;

    showNotification(NotificationType.SUCCESS, message);
  };

  const onSubmitError = (phase, details?: string) => {
    const message = `Task '${userTask.referenceName}' couldn't transition to phase '${phase}'.`;

    showNotification(NotificationType.ERROR, message, details);
  };

  const goToInbox = () => {
    context.setActiveItem(null);
    props.history.push('/');
  };

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
              <Tooltip content={userTask.id}>
                <PageTitle
                  title={userTask.referenceName}
                  extra={
                    <TaskState task={userTask} variant={TaskStateType.LABEL} />
                  }
                />
              </Tooltip>
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
          {notification && (
            <div className="kogito-task-console--task-details-page">
              <FormNotification notification={notification} />
            </div>
          )}
        </PageSection>
        <PageSection>
          <Drawer isExpanded={isDetailsExpanded}>
            <DrawerContent panelContent={panelContent}>
              <DrawerContentBody>
                <Card className="pf-u-h-100">
                  <CardBody className="pf-u-h-100">
                    <TaskForm
                      userTaskInstance={userTask}
                      onSubmitSuccess={onSubmitSuccess}
                      onSubmitError={onSubmitError}
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
