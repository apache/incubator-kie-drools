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
import { RouteComponentProps } from 'react-router-dom';
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
import { Grid, GridItem } from '@patternfly/react-core/dist/js/layouts/Grid';
import { Flex, FlexItem } from '@patternfly/react-core/dist/js/layouts/Flex';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { Title } from '@patternfly/react-core/dist/js/components/Title';
import { PageSection } from '@patternfly/react-core/dist/js/components/Page';
import { KogitoSpinner } from '@kogito-apps/components-common/dist/components/KogitoSpinner';
import {
  FormNotification,
  Notification
} from '@kogito-apps/components-common/dist/components/FormNotification';
import {
  KogitoEmptyState,
  KogitoEmptyStateType
} from '@kogito-apps/components-common/dist/components/KogitoEmptyState';
import { ServerErrors } from '@kogito-apps/components-common/dist/components/ServerErrors';
import {
  OUIAProps,
  componentOuiaProps,
  ouiaPageTypeAndObjectId
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { PageTitle } from '@kogito-apps/consoles-common/dist/components/layout/PageTitle';
import { UserTaskInstance, TaskState } from '@kogito-apps/task-console-shared';
import { TaskInboxGatewayApi } from '../../../channel/TaskInbox';
import { useTaskInboxGatewayApi } from '../../../channel/TaskInbox/TaskInboxContext';
import TaskFormContainer from '../../containers/TaskFormContainer/TaskFormContainer';
import '../../styles.css';
import { EmbeddedTaskDetails } from '@kogito-apps/task-details';
import { useDevUIAppContext } from '../../contexts/DevUIAppContext';

interface Props {
  taskId?: string;
}

const TaskDetailsPage: React.FC<RouteComponentProps<Props> & OUIAProps> = ({
  ouiaId,
  ouiaSafe,
  ...props
}) => {
  const taskInboxGatewayApi: TaskInboxGatewayApi = useTaskInboxGatewayApi();
  const appContext = useDevUIAppContext();

  const [taskId] = useState<string>(props.match.params.taskId);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [userTask, setUserTask] = useState<UserTaskInstance>();
  const [notification, setNotification] = useState<Notification>();
  const [error, setError] = useState();
  const [isDetailsExpanded, setIsDetailsExpanded] = useState<boolean>(false);

  useEffect(() => {
    return ouiaPageTypeAndObjectId('task-details-page', taskId);
  });

  const loadTask = async () => {
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
    loadTask();
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
      customActions: [
        {
          label: 'Go to Task Inbox',
          onClick: () => {
            setNotification(null);
            goToInbox();
          }
        }
      ],
      close: () => {
        setNotification(null);
      }
    });
  };

  const goToInbox = () => {
    taskInboxGatewayApi.clearOpenTask();
    props.history.push('/TaskInbox');
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
        <Card className="Dev-ui__card-size">
          <Bullseye>
            <KogitoSpinner
              spinnerText={`Loading details for task: ${taskId}`}
            />
          </Bullseye>
        </Card>
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
        <Grid hasGutter md={1} className={'Dev-ui__card-size'}>
          <GridItem span={12} className={'Dev-ui__card-size'}>
            <Card className={'Dev-ui__card-size'}>
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
        <Grid hasGutter md={1} className={'Dev-ui__card-size'}>
          <GridItem span={12} className={'Dev-ui__card-size'}>
            <Card className={'Dev-ui__card-size'}>
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
    <DrawerPanelContent className={'Dev-ui__card-size'}>
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
          targetOrigin={appContext.getDevUIUrl()}
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
              <Grid hasGutter md={1} className={'Dev-ui__card-size'}>
                <GridItem span={12} className={'Dev-ui__card-size'}>
                  <Card className={'Dev-ui__card-size'}>
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
