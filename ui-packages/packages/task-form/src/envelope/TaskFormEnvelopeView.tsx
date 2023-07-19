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

import React, {
  useCallback,
  useEffect,
  useImperativeHandle,
  useState
} from 'react';
import { MessageBusClientApi } from '@kie-tools-core/envelope-bus/dist/api';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import { TaskFormChannelApi, TaskFormInitArgs, User } from '../api';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner
} from '@kogito-apps/components-common';
import { CustomForm } from '../types';
import { TaskFormEnvelopeViewDriver } from './TaskFormEnvelopeViewDriver';
import CustomTaskFormDisplayer from './components/CustomTaskFormDisplayer/CustomTaskFormDisplayer';
import TaskForm from './components/TaskForm/TaskForm';

import '@patternfly/patternfly/patternfly.css';

export interface TaskFormEnvelopeViewApi {
  initialize: (initArgs: TaskFormInitArgs) => void;
}

interface Props {
  channelApi: MessageBusClientApi<TaskFormChannelApi>;
}

export const TaskFormEnvelopeView = React.forwardRef<
  TaskFormEnvelopeViewApi,
  Props & OUIAProps
>(({ channelApi, ouiaId, ouiaSafe }, forwardedRef) => {
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] =
    useState<boolean>(false);
  const [userTask, setUserTask] = useState<UserTaskInstance>();
  const [user, setUser] = useState<User>();
  const [taskFormSchema, setTaskFormSchema] =
    useState<Record<string, any>>(null);
  const [customForm, setCustomForm] = useState<CustomForm>(null);

  const [driver] = useState<TaskFormEnvelopeViewDriver>(
    new TaskFormEnvelopeViewDriver(channelApi)
  );

  useImperativeHandle(
    forwardedRef,
    () => ({
      initialize: (initArgs: TaskFormInitArgs) => {
        setEnvelopeConnectedToChannel(true);
        setUserTask(initArgs.userTask);
        setUser(initArgs.user);
      }
    }),
    []
  );

  useEffect(() => {
    if (isEnvelopeConnectedToChannel) {
      loadForm();
    }
  }, [isEnvelopeConnectedToChannel]);

  const loadForm = useCallback(async () => {
    if (!isEnvelopeConnectedToChannel) {
      setIsLoading(true);
    }

    const customFormPromise: Promise<void> = new Promise<void>((resolve) => {
      driver
        .getCustomForm()
        .then((customForm) => {
          setCustomForm(customForm);
          resolve();
        })
        .catch((error) => resolve());
    });

    const schemaPromise: Promise<void> = new Promise<void>((resolve) => {
      driver
        .getTaskFormSchema()
        .then((schema) => {
          setTaskFormSchema(schema);
          resolve();
        })
        .catch((error) => resolve());
    });

    Promise.all([customFormPromise, schemaPromise]).then((values) => {
      setIsLoading(false);
    });
  }, [isEnvelopeConnectedToChannel]);

  if (isLoading) {
    return (
      <Bullseye
        {...componentOuiaProps(
          (ouiaId ? ouiaId : 'task-form-envelope-view') + '-loading-spinner',
          'task-form',
          true
        )}
      >
        <KogitoSpinner spinnerText={`Loading task form...`} />
      </Bullseye>
    );
  }

  if (taskFormSchema) {
    if (customForm) {
      return (
        <CustomTaskFormDisplayer
          {...componentOuiaProps(
            (ouiaId ? ouiaId : 'task-form-envelope-view') + '-custom-task-form',
            'custom-task-form',
            ouiaSafe
          )}
          userTask={userTask}
          schema={taskFormSchema}
          customForm={customForm}
          user={user}
          driver={driver}
        />
      );
    }
    return (
      <TaskForm
        {...componentOuiaProps(
          (ouiaId ? ouiaId : 'task-form-envelope-view') + '-task-form',
          'task-form',
          ouiaSafe
        )}
        userTask={userTask}
        schema={taskFormSchema}
        driver={driver}
      />
    );
  }

  return (
    <KogitoEmptyState
      type={KogitoEmptyStateType.Info}
      title="No form to show"
      body={`Cannot find form for task  ${
        userTask.referenceName
      } (${userTask.id.substring(0, 5)})`}
      {...componentOuiaProps(
        (ouiaId ? ouiaId : 'task-form-envelope-view') + '-no-form',
        'empty-task-form',
        ouiaSafe
      )}
    />
  );
});

export default TaskFormEnvelopeView;
