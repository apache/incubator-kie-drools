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

import React, { useEffect, useState } from 'react';
import axios from 'axios';
import _ from 'lodash';
import {
  AppContext,
  GraphQL,
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner,
  useKogitoAppContext
} from '@kogito-apps/common';
import {
  ITaskConsoleContext,
  useTaskConsoleContext
} from '../../../context/TaskConsoleContext/TaskConsoleContext';
import FormRenderer from '../../Molecules/FormRenderer/FormRenderer';
import { TaskFormSubmitHandler } from '../../../util/uniforms/TaskFormSubmitHandler/TaskFormSubmitHandler';
import { FormSchema } from '../../../util/uniforms/FormSchema';
import { getTaskSchemaEndPoint } from '../../../util/Utils';
import UserTaskInstance = GraphQL.UserTaskInstance;
import { OUIAProps } from '@kogito-apps/common';

interface IOwnProps {
  userTaskInstance?: UserTaskInstance;
  onSubmitSuccess: (message: string) => void;
  onSubmitError: (message: string, details?: string) => void;
}

const TaskForm: React.FC<IOwnProps & OUIAProps> = ({
  userTaskInstance,
  onSubmitSuccess,
  onSubmitError,
  ouiaId,
  ouiaSafe
}) => {
  // tslint:disable: no-floating-promises
  const context: ITaskConsoleContext<UserTaskInstance> = useTaskConsoleContext();
  const appContext: AppContext = useKogitoAppContext();

  const [loading, setLoading] = useState<boolean>(true);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [submitted, setSubmitted] = useState<boolean>(false);
  const [stateUserTask, setStateUserTask] = useState<UserTaskInstance>();
  const [taskFormSchema, setTaskFormSchema] = useState<FormSchema>(null);
  const [formOutput, setFormOutput] = useState<any>(null);

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
      const endpoint = getTaskSchemaEndPoint(
        stateUserTask,
        appContext.getCurrentUser()
      );

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

  if (stateUserTask) {
    if (loading) {
      return (
        <KogitoSpinner
          spinnerText={'Loading form for task: ' + stateUserTask.name}
          ouiaId={(ouiaId ? ouiaId : 'task-form') + '-spinner-loading'}
          ouiaSafe={ouiaSafe}
        />
      );
    }

    if (isSubmitting) {
      return (
        <KogitoSpinner
          spinnerText={'Submitting form...'}
          ouiaId={(ouiaId ? ouiaId : 'task-form') + '-spinner-submitting'}
          ouiaSafe={ouiaSafe}
        />
      );
    }

    if (taskFormSchema) {
      const notifySuccess = (phase: string) => {
        onSubmitSuccess(phase);
        setIsSubmitting(false);
        setSubmitted(true);
      };

      const notifyError = (phase: string, error?: string) => {
        onSubmitError(phase, error);
        setIsSubmitting(false);
      };

      const formSubmitHandler = new TaskFormSubmitHandler(
        stateUserTask,
        taskFormSchema,
        appContext.getCurrentUser(),
        output => {
          setFormOutput(output);
          setIsSubmitting(true);
        },
        phase => notifySuccess(phase),
        (phase, errorMessage) => notifyError(phase, errorMessage)
      );

      const toJSON = (value: string) => {
        if (value) {
          try {
            return JSON.parse(value);
          } catch (e) {
            // do nothing
          }
        }
        return {};
      };

      const generateFormData = () => {
        const taskInputs = toJSON(stateUserTask.inputs);
        if (!stateUserTask.outputs) {
          return taskInputs;
        }

        const taskOutputs = formOutput || toJSON(stateUserTask.outputs);

        return _.merge(taskInputs, taskOutputs);
      };

      const formData = generateFormData();

      return (
        <FormRenderer
          formSchema={taskFormSchema}
          model={formData}
          readOnly={submitted}
          formSubmitHandler={formSubmitHandler}
          ouiaId={(ouiaId ? ouiaId : 'task-form') + '-form-renderer'}
          ouiaSafe={ouiaSafe}
        />
      );
    }
  }

  return (
    <KogitoEmptyState
      type={KogitoEmptyStateType.Info}
      title="No form to show"
      body="Cannot find form"
      ouiaId={(ouiaId ? ouiaId : 'task-form') + '-no-form'}
      ouiaSafe={ouiaSafe}
    />
  );
};

export default TaskForm;
