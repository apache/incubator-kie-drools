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

import React, { useContext, useEffect, useState } from 'react';
import axios from 'axios';
import { Stack, StackItem } from '@patternfly/react-core';
import {
  GraphQL,
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner
} from '@kogito-apps/common';
import TaskConsoleContext, {
  IContext
} from '../../../context/TaskConsoleContext/TaskConsoleContext';
import FormRenderer from '../../Molecules/FormRenderer/FormRenderer';
import { TaskFormSubmitHandler } from '../../../util/uniforms/TaskFormSubmitHandler/TaskFormSubmitHandler';
import { FormSchema } from '../../../util/uniforms/FormSchema';
import { getTaskSchemaEndPoint } from '../../../util/Utils';
import FormNotification, {
  Notification
} from '../../Atoms/FormNotification/FormNotification';
import UserTaskInstance = GraphQL.UserTaskInstance;
import { NotificationType } from '../../../util/Variants';

interface IOwnProps {
  userTaskInstance?: UserTaskInstance;
  successCallback?: () => void;
  errorCallback?: () => void;
}

const TaskForm: React.FC<IOwnProps> = ({
  userTaskInstance,
  successCallback,
  errorCallback
}) => {
  // tslint:disable: no-floating-promises
  const context: IContext<UserTaskInstance> = useContext(TaskConsoleContext);
  const [notification, setNotification] = useState<Notification>();
  const [loading, setLoading] = useState<boolean>(true);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [submitted, setSubmitted] = useState<boolean>(false);
  const [stateUserTask, setStateUserTask] = useState<UserTaskInstance>();
  const [taskFormSchema, setTaskFormSchema] = useState<FormSchema>(null);

  if (!stateUserTask) {
    if (userTaskInstance) {
      setStateUserTask(userTaskInstance);
    } else {
      if (context.getActiveItem()) {
        setStateUserTask(context.getActiveItem());
      }
    }
  }

  useEffect(() => {
    loadForm();
  }, [stateUserTask]);

  const loadForm = () => {
    if (stateUserTask) {
      const endpoint = getTaskSchemaEndPoint(stateUserTask, context.getUser());

      axios
        .get(endpoint, {
          headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json',
            crossorigin: 'true',
            'Access-Control-Allow-Origin': '*'
          }
        })
        .then(res => {
          if (res.status === 200) {
            setTaskFormSchema(res.data);
          }
          setLoading(false);
        })
        .catch(er => {
          setLoading(false);
        });
    }
  };

  const showNotification = (
    notificationType: NotificationType,
    submitMessage: string,
    submitCallback?: () => void,
    notificationDetails?: string
  ) => {
    setNotification({
      type: notificationType,
      message: submitMessage,
      details: notificationDetails,
      customAction: submitCallback
        ? {
            label: 'Select another Task',
            onClick: () => {
              setNotification(null);
              if (submitCallback) {
                submitCallback();
              }
            }
          }
        : undefined,
      close: () => {
        setNotification(null);
      }
    });
  };

  if (stateUserTask) {
    if (loading) {
      return (
        <KogitoSpinner
          spinnerText={'Loading form for task: ' + stateUserTask.name}
        />
      );
    }

    if (isSubmitting) {
      return <KogitoSpinner spinnerText={'Submitting form...'} />;
    }

    if (taskFormSchema) {
      const notifySuccess = (phase: string) => {
        const message = `Task '${userTaskInstance.referenceName}' successfully transitioned to phase '${phase}'.`;

        showNotification(NotificationType.SUCCESS, message, successCallback);
        setIsSubmitting(false);
        setSubmitted(true);
      };

      const notifyError = (phase: string, error?: string) => {
        const message = `Task '${userTaskInstance.referenceName}' couldn't transition to phase '${phase}'.`;

        showNotification(NotificationType.ERROR, message, errorCallback, error);
        setIsSubmitting(false);
      };

      const formSubmitHandler = new TaskFormSubmitHandler(
        stateUserTask,
        taskFormSchema,
        context.getUser(),
        () => setIsSubmitting(true),
        phase => notifySuccess(phase),
        (phase, errorMessage) => notifyError(phase, errorMessage)
      );

      const formData = JSON.parse(stateUserTask.inputs);

      return (
        <Stack hasGutter>
          {notification && (
            <StackItem>
              <FormNotification notification={notification} />
            </StackItem>
          )}
          <StackItem>
            <FormRenderer
              formSchema={taskFormSchema}
              model={formData}
              readOnly={submitted}
              formSubmitHandler={formSubmitHandler}
            />
          </StackItem>
        </Stack>
      );
    }
  }

  return (
    <KogitoEmptyState
      type={KogitoEmptyStateType.Info}
      title="No form to show"
      body="Cannot find form"
    />
  );
};

export default TaskForm;
