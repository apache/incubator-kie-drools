/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import React, { useEffect, useState } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import {
  Breadcrumb,
  BreadcrumbItem
} from '@patternfly/react-core/dist/js/components/Breadcrumb';
import {
  Drawer,
  DrawerActions,
  DrawerCloseButton,
  DrawerContent,
  DrawerContentBody,
  DrawerHead,
  DrawerPanelBody,
  DrawerPanelContent
} from '@patternfly/react-core/dist/js/components/Drawer';
import { Card, CardBody } from '@patternfly/react-core/dist/js/components/Card';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { Title } from '@patternfly/react-core/dist/js/components/Title';
import { PageSection } from '@patternfly/react-core/dist/js/components/Page';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';
import { Grid, GridItem } from '@patternfly/react-core/dist/js/layouts/Grid';
import { Flex, FlexItem } from '@patternfly/react-core/dist/js/layouts/Flex';
import {
  KogitoSpinner,
  ServerErrors,
  KogitoEmptyState,
  KogitoEmptyStateType
} from '@kogito-apps/components-common';
import {
  OUIAProps,
  componentOuiaProps,
  ouiaPageTypeAndObjectId
} from '@kogito-apps/ouia-tools';
import { PageTitle } from '@kogito-apps/consoles-common';
import { UserTaskInstance, TaskState } from '@kogito-apps/task-console-shared';
import { TaskInboxGatewayApi } from '../../../channel/inbox';
import { useTaskInboxGatewayApi } from '../../../channel/inbox/TaskInboxContext';
import TaskFormContainer from './components/TaskFormContainer/TaskFormContainer';
import FormNotification, {
  Notification
} from './components/FormNotification/FormNotification';
import '../../styles.css';
import { EmbeddedTaskDetails } from '@kogito-apps/task-details';

interface Props {
  taskId?: string;
}

const TaskDetailsPage: React.FC<RouteComponentProps<Props> & OUIAProps> = ({
  ouiaId,
  ouiaSafe,
  ...props
}) => {
  const taskInboxGatewayApi: TaskInboxGatewayApi = useTaskInboxGatewayApi();

  const [taskId] = useState<string>(props.match.params.taskId);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [userTask, setUserTask] = useState<UserTaskInstance>();
  const [notification, setNotification] = useState<Notification>();
  const [error, setError] = useState();
  const [isDetailsExpanded, setIsDetailsExpanded] = useState<boolean>(false);

  useEffect(() => {
    return ouiaPageTypeAndObjectId('task-details-page', taskId);
  });

  const loatTask = async () => {
    try {
      const task = await taskInboxGatewayApi.getTaskById(taskId);
      setUserTask(task);
    } catch (err) {
      setError(err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loatTask();
  }, []);

  const showNotification = (
    notificationType: 'error' | 'success',
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

  const goToInbox = () => {
    taskInboxGatewayApi.clearOpenTask();
    props.history.push('/');
  };

  const onSubmitSuccess = (phase: string) => {
    const message = `Task '${userTask.referenceName}' successfully transitioned to phase '${phase}'.`;

    showNotification('success', message);
  };

  const onSubmitError = (phase, details?: string) => {
    const message = `Task '${userTask.referenceName}' couldn't transition to phase '${phase}'.`;

    showNotification('error', message, details);
  };

  if (isLoading) {
    return (
      <PageSection
        {...componentOuiaProps(
          'spinner' + (ouiaId ? '-' + ouiaId : ''),
          'task-details-page-section',
          ouiaSafe
        )}
      >
        <Grid hasGutter md={1} className={'kogito-task-console__full-size'}>
          <GridItem span={12} className={'kogito-task-console__full-size'}>
            <Card className={'kogito-task-console__full-size'}>
              <Bullseye>
                <KogitoSpinner
                  spinnerText={`Loading details for task: ${taskId}`}
                />
              </Bullseye>
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    );
  }

  if (error) {
    return (
      <PageSection
        {...componentOuiaProps(
          'error' + (ouiaId ? '-' + ouiaId : ''),
          'task-details-page-section',
          ouiaSafe
        )}
      >
        <Grid hasGutter md={1} className={'kogito-task-console__full-size'}>
          <GridItem span={12} className={'kogito-task-console__full-size'}>
            <Card className={'kogito-task-console__full-size'}>
              <ServerErrors error={error} variant="large">
                <Button variant="primary" onClick={() => goToInbox()}>
                  Go to Inbox
                </Button>
              </ServerErrors>
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    );
  }

  if (!userTask) {
    return (
      <PageSection
        {...componentOuiaProps(
          'empty' + (ouiaId ? '-' + ouiaId : ''),
          'task-details-page-section',
          ouiaSafe
        )}
      >
        <Grid hasGutter md={1} className={'kogito-task-console__full-size'}>
          <GridItem span={12} className={'kogito-task-console__full-size'}>
            <Card className={'kogito-task-console__full-size'}>
              <KogitoEmptyState
                type={KogitoEmptyStateType.Info}
                title={'Cannot find task'}
                body={`Cannot find task with id '${taskId}'`}
              />
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    );
  }

  const onViewDetailsClick = () => {
    setIsDetailsExpanded(!isDetailsExpanded);
  };

  const onDetailsCloseClick = () => {
    setIsDetailsExpanded(false);
  };

  const panelContent = (
    <DrawerPanelContent className={'kogito-task-console__full-size'}>
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
        <EmbeddedTaskDetails
          targetOrigin={window.location.origin}
          userTask={userTask}
        />
      </DrawerPanelBody>
    </DrawerPanelContent>
  );

  return (
    <React.Fragment>
      <PageSection
        variant="light"
        {...componentOuiaProps(
          'header' + (ouiaId ? '-' + ouiaId : ''),
          'task-details-page-section',
          ouiaSafe
        )}
      >
        <Breadcrumb>
          <BreadcrumbItem>
            <Link
              to={'/'}
              onClick={() => {
                taskInboxGatewayApi.clearOpenTask();
              }}
            >
              Task Inbox
            </Link>
          </BreadcrumbItem>
          <BreadcrumbItem>{userTask.referenceName}</BreadcrumbItem>
        </Breadcrumb>
        <Flex justifyContent={{ default: 'justifyContentSpaceBetween' }}>
          <FlexItem>
            <PageTitle
              title={userTask.referenceName}
              extra={<TaskState task={userTask} variant={'label'} />}
            />
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
          <div className="kogito-task-console__task-details-page">
            <FormNotification notification={notification} />
          </div>
        )}
      </PageSection>
      <PageSection
        {...componentOuiaProps(
          'content' + (ouiaId ? '-' + ouiaId : ''),
          'task-details-page-section',
          ouiaSafe
        )}
      >
        <Drawer isExpanded={isDetailsExpanded}>
          <DrawerContent panelContent={panelContent}>
            <DrawerContentBody>
              <Grid
                hasGutter
                md={1}
                className={'kogito-task-console__full-size'}
              >
                <GridItem
                  span={12}
                  className={'kogito-task-console__full-size'}
                >
                  <Card className={'kogito-task-console__full-size'}>
                    <CardBody className="pf-u-h-100">
                      <TaskFormContainer
                        userTask={userTask}
                        onSubmitSuccess={onSubmitSuccess}
                        onSubmitError={onSubmitError}
                      />
                    </CardBody>
                  </Card>
                </GridItem>
              </Grid>
            </DrawerContentBody>
          </DrawerContent>
        </Drawer>
      </PageSection>
    </React.Fragment>
  );
};

export default TaskDetailsPage;
