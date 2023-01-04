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
import get from 'lodash/get';
import has from 'lodash/has';
import isEmpty from 'lodash/isEmpty';
import set from 'lodash/set';
import { Bullseye } from '@patternfly/react-core';
import { KogitoSpinner } from '@kogito-apps/components-common';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import { TaskFormDriver } from '../../../api';
import EmptyTaskForm from '../EmptyTaskForm/EmptyTaskForm';
import TaskFormRenderer from '../TaskFormRenderer/TaskFormRenderer';
import {
  parseTaskSchema,
  TaskDataAssignments
} from '../utils/TaskFormDataUtils';

export interface TaskFormProps {
  userTask: UserTaskInstance;
  schema: Record<string, any>;
  driver: TaskFormDriver;
}

enum State {
  READY,
  SUBMITTING,
  SUBMITTED
}

const TaskForm: React.FC<TaskFormProps & OUIAProps> = ({
  userTask,
  schema,
  driver,
  ouiaId,
  ouiaSafe
}) => {
  const [formData, setFormData] = useState<any>(null);
  const [formState, setFormState] = useState<State>(State.READY);
  const [taskFormSchema, setTaskFormSchema] = useState<Record<string, any>>();
  const [taskFormAssignments, setTaskFormAssignments] =
    useState<TaskDataAssignments>();

  useEffect(() => {
    const parsedSchema = parseTaskSchema(schema);
    setTaskFormSchema(parsedSchema.schema);
    setTaskFormAssignments(parsedSchema.assignments);
  }, []);

  if (formState === State.SUBMITTING) {
    return (
      <Bullseye
        {...componentOuiaProps(
          (ouiaId ? ouiaId : 'task-form') + '-submit-spinner',
          'task-form',
          true
        )}
      >
        <KogitoSpinner
          spinnerText={`Submitting for task ${
            userTask.referenceName
          } (${userTask.id.substring(0, 5)})`}
        />
      </Bullseye>
    );
  }

  if (formState === State.READY || formState === State.SUBMITTED) {
    const doSubmit = async (
      phase: string,
      data: any,
      onSuccess?: (response: any) => void,
      onFailure?: (response: any) => void
    ) => {
      try {
        setFormState(State.SUBMITTING);
        setFormData(data);

        const payload = {};

        taskFormAssignments.outputs.forEach((output) => {
          if (has(data, output)) {
            set(payload, output, get(data, output));
          }
        });

        const result = await driver.doSubmit(phase, payload);
        if (onSuccess) {
          onSuccess(result);
        }
      } catch (err) {
        if (onFailure) {
          onFailure(err);
        }
      } finally {
        setFormState(State.SUBMITTED);
      }
    };

    if (!taskFormSchema) {
      return (
        <Bullseye
          {...componentOuiaProps(
            (ouiaId ? ouiaId : 'task-form-') + '-loading-spinner',
            'task-form',
            true
          )}
        >
          <KogitoSpinner spinnerText={`Loading task form...`} />
        </Bullseye>
      );
    }

    if (isEmpty(taskFormSchema.properties)) {
      return (
        <EmptyTaskForm
          {...componentOuiaProps(
            (ouiaId ? ouiaId : 'task-form') + '-empty-form',
            'task-form',
            ouiaSafe
          )}
          userTask={userTask}
          enabled={formState == State.READY}
          formSchema={taskFormSchema}
          submit={(phase) => doSubmit(phase, {})}
        />
      );
    }

    return (
      <TaskFormRenderer
        {...componentOuiaProps(
          (ouiaId ? ouiaId : 'task-form') + '-form-renderer',
          'task-form',
          ouiaSafe
        )}
        userTask={userTask}
        formSchema={taskFormSchema}
        formData={formData}
        enabled={formState == State.READY}
        submit={doSubmit}
      />
    );
  }
};

export default TaskForm;
